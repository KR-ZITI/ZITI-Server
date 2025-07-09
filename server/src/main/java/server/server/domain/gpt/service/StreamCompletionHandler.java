package server.server.domain.gpt.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import server.server.domain.gpt.domain.GptAnswer;
import server.server.domain.gpt.domain.facade.GptAnswerFacade;
import server.server.domain.gpt.domain.repository.GptAnswerRepository;
import server.server.domain.gpt.presentation.dto.request.GPTChatWithImageRequest;

import jakarta.annotation.PostConstruct;
import server.server.domain.user.domain.User;
import server.server.domain.user.domain.facade.UserFacade;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class StreamCompletionHandler extends TextWebSocketHandler {

    private final HashMap<String, WebSocketSession> sessionHashMap = new HashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final GptAnswerRepository gptAnswerRepository;
    private final UserFacade userFacade;

    @Value("${openai.api.key}")
    private String apiKey;

    private static final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";
    private RestClient restClient;

    @PostConstruct
    private void initRestClient() {
        this.restClient = RestClient.builder()
                .baseUrl(OPENAI_API_URL)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessionHashMap.put(session.getId(), session);
        log.info("접속된 세션: {}", session.getId());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessionHashMap.remove(session.getId());
        log.info("해제된 세션: {}", session.getId());
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // 👇 여기서 email 추출
        String email = (String) session.getAttributes().get("email");
        if (email == null) {
            log.warn("세션에 email 없음. 인증되지 않은 요청.");
            return;
        }

        User user = userFacade.getUserByEmail(email); // 🔐 인증된 사용자 획득

        GPTChatWithImageRequest request = objectMapper.readValue(message.getPayload(), GPTChatWithImageRequest.class);
        streamCompletionWithImage(session.getId(), request, user); // 👇 user 넘겨줌
    }


    private void streamCompletionWithImage(String key, GPTChatWithImageRequest request, User user) {
        try {
            Map<String, Object> userMessage;

            if (request.getImageBase64() != null && !request.getImageBase64().isBlank()) {
                File imageFile = saveImageFromBase64(request.getImageBase64(), UUID.randomUUID() + ".png");
                String base64Image = Base64.getEncoder().encodeToString(java.nio.file.Files.readAllBytes(imageFile.toPath()));
                String mimeType = java.nio.file.Files.probeContentType(imageFile.toPath());
                imageFile.delete();

                userMessage = Map.of(
                        "role", "user",
                        "content", List.of(
                                Map.of("type", "text", "text", request.getMessage()),
                                Map.of("type", "image_url", "image_url", Map.of(
                                        "url", "data:" + mimeType + ";base64," + base64Image
                                ))
                        )
                );
            } else {
                userMessage = Map.of(
                        "role", "user",
                        "content", List.of(
                                Map.of("type", "text", "text", request.getMessage())
                        )
                );
            }

            Map<String, Object> body = Map.of(
                    "model", "gpt-4.1",
                    "messages", List.of(
                            Map.of("role", "system", "content", "너의 언어는 한국어이고 친구처럼 말해줘."),
                            userMessage
                    ),
                    "max_tokens", 1000
            );

            Map<?, ?> response = restClient.post()
                    .body(body)
                    .retrieve()
                    .body(Map.class);

            String reply = Optional.ofNullable(response)
                    .map(res -> (List<Map<String, Object>>) res.get("choices"))
                    .map(choices -> choices.get(0))
                    .map(choice -> (Map<String, Object>) choice.get("message"))
                    .map(msg -> (String) msg.get("content"))
                    .orElse("GPT 응답 없음");

            if (sessionHashMap.get(key) != null && sessionHashMap.get(key).isOpen()) {
                sessionHashMap.get(key).sendMessage(new TextMessage(reply));
            }

            gptAnswerRepository.save(GptAnswer.builder()
                    .question(request.getMessage())
                    .answer(reply)
                    .user(user)
                    .build());

        } catch (Exception e) {
            log.error("이미지 기반 GPT 처리 오류", e);
        }
    }

    private File saveImageFromBase64(String base64, String filename) throws IOException {
        String base64Data = base64.contains(",") ? base64.split(",")[1] : base64;
        byte[] decoded = Base64.getDecoder().decode(base64Data);
        File file = new File("uploads/" + filename);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(decoded);
        }
        return file;
    }
}
