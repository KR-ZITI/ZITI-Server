package server.server.domain.gpt.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import server.server.domain.gpt.domain.GptAnswer;
import server.server.domain.gpt.domain.facade.GptAnswerFacade;
import server.server.domain.gpt.domain.repository.GptAnswerRepository;
import server.server.domain.gpt.presentation.dto.request.GPTCompletionChatRequest;

@Slf4j
@Component
@RequiredArgsConstructor
public class StreamCompletionHandler extends TextWebSocketHandler {
    private final HashMap<String, WebSocketSession> sessionHashMap = new HashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final OpenAiService openAiService;
    private final GptAnswerRepository gptAnswerRepository;
    private final GptAnswerFacade gptAnswerFacade;

    private final StringBuilder responseBuffer = new StringBuilder();

    List<ChatMessage> conversation = new ArrayList<>();

    /* Client가 접속 시 호출되는 메서드 */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) {

        sessionHashMap.put(session.getId(), session);
        log.info("현재 접근한 유저 : {}", session.getId());
    }

    /* Client가 접속 해제 시 호출되는 메서드드 */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {

        sessionHashMap.remove(session.getId());
        log.info("연결해제 한 유저 : {}", session.getId());
    }

    /* Client로부터 텍스트 메시지를 수신했을 때 호출되는 메서드 */
    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {

        // 메시지 payload를 GPTCompletionChatRequest 객체로 변환
        GPTCompletionChatRequest gptCompletionChatRequest = objectMapper.readValue(message.getPayload(),
                GPTCompletionChatRequest.class);

        // StringBuilder responseBuffer 초기화
        responseBuffer.setLength(0);

        // 메시지 전송을 위한 채팅 메시지 리스트 초기화
        conversation.clear();

        // streamCompletion 메서드 호출
        sessionHashMap.keySet().forEach(key -> {
            streamCompletion(key, gptCompletionChatRequest);
        });
    }

    /* GPT 스트림 응답을 처리하는 메서드 */
    private void streamCompletion(String key, GPTCompletionChatRequest gptCompletionChatRequest) {

        ChatMessage systemMsg = gptCompletionChatRequest.promptStart();
        conversation.add(systemMsg);

        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        List<GptAnswer> gptAnswers = gptAnswerFacade.getGptAnswerAllById(sort);

        gptAnswers.forEach(gptAnswer -> {
            conversation.add(new ChatMessage("user", gptAnswer.getQuestion()));
            conversation.add(new ChatMessage("assistant", gptAnswer.getAnswer()));
        });

        // 확인용 출력
        conversation.forEach(chatMessage -> log.info("{} : {}", chatMessage.getRole(), chatMessage.getContent()));

        ChatMessage userMsg = gptCompletionChatRequest.convertChatMessage();
        conversation.add(userMsg);

        openAiService.streamChatCompletion(ChatCompletionRequest.builder()
                        .model("gpt-3.5-turbo")
                        .messages(conversation)
                        .maxTokens(1000)
                        .stream(true)
                        .build())
                .blockingForEach(response -> {
                    String responseSegment = response.getChoices().get(0).getMessage().getContent();
                    if (responseSegment != null) {
                        responseBuffer.append(responseSegment); // 응답 부분을 StringBuilder에 추가
                        sessionHashMap.get(key).sendMessage(new TextMessage(responseSegment)); // 세션에 응답 부분 전송
                    }
                });
        String fullResponse = responseBuffer.toString(); // 전체 응답을 문자열로 변환

        // GPT 답변을 저장
        gptAnswerRepository.save(GptAnswer.builder()
                .question(gptCompletionChatRequest.getMessage())
                .answer(fullResponse).build());
    }
}