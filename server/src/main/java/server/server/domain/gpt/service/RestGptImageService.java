package server.server.domain.gpt.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;
import server.server.domain.gpt.domain.GptAnswer;
import server.server.domain.gpt.domain.repository.GptAnswerRepository;
import server.server.domain.user.domain.User;
import server.server.domain.user.domain.facade.UserFacade;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RestGptImageService {

    @Value("${openai.api.key}")
    private String apiKey;

    private static final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";

    private RestClient restClient;
    private final UserFacade userFacade;
    private final GptAnswerRepository gptAnswerRepository;

    @PostConstruct
    private void initRestClient() {
        this.restClient = RestClient.builder()
                .baseUrl(OPENAI_API_URL)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    @Transactional
    public String completionChatWithImage(String message, MultipartFile image) throws IOException {
        // 1. 이미지 저장 및 base64 변환
        File savedImage = saveImage(image);
        String mimeType = Files.probeContentType(savedImage.toPath());
        String base64Image = encodeToBase64(savedImage);
        savedImage.delete();

        // 2. 텍스트 + 이미지 포함 메시지 생성
        Map<String, Object> userMessage = Map.of(
                "role", "user",
                "content", List.of(
                        Map.of("type", "text", "text", message),
                        Map.of("type", "image_url", "image_url", Map.of(
                                "url", "data:" + mimeType + ";base64," + base64Image
                        ))
                )
        );

        // 3. 요청 바디 구성
        Map<String, Object> requestBody = Map.of(
                "model", "gpt-4.1",
                "messages", List.of(
                        Map.of("role", "system", "content", "너는 이미지와 텍스트를 함께 분석해서 정확히 설명하는 도우미야."),
                        userMessage
                ),
                "max_tokens", 1000
        );

        // 4. 요청 전송 및 응답 파싱
        Map<?, ?> response = restClient.post()
                .body(requestBody)
                .retrieve()
                .body(Map.class);

        String gptAnswer = Optional.ofNullable(response)
                .map(res -> (List<Map<String, Object>>) res.get("choices"))
                .map(choices -> choices.get(0))
                .map(choice -> (Map<String, Object>) choice.get("message"))
                .map(msg -> (String) msg.get("content"))
                .orElse("GPT 응답 없음");

        // 5. 응답 저장
        saveAnswer(List.of(gptAnswer), message);

        return gptAnswer;
    }

    private void saveAnswer(List<String> response, String requestQuestion) {

        User user = userFacade.getCurrentUser();

        String answer = response.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.joining());

        gptAnswerRepository.save(GptAnswer.builder()
                .question(requestQuestion)
                .answer(answer)
                .user(user).build());
    }

    private File saveImage(MultipartFile multipartFile) throws IOException {
        File tempFile = File.createTempFile("upload-", Objects.requireNonNull(multipartFile.getOriginalFilename()));
        multipartFile.transferTo(tempFile);
        return tempFile;
    }

    private String encodeToBase64(File imageFile) throws IOException {
        byte[] fileContent = Files.readAllBytes(imageFile.toPath());
        return Base64.getEncoder().encodeToString(fileContent);
    }
}
