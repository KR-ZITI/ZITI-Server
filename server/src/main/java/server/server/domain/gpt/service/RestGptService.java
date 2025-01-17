package server.server.domain.gpt.service;

import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatCompletionResult;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import server.server.domain.gpt.domain.GptAnswer;
import server.server.domain.gpt.domain.facade.GptAnswerFacade;
import server.server.domain.gpt.domain.repository.GptAnswerRepository;
import server.server.domain.gpt.presentation.dto.request.GPTCompletionChatRequest;
import server.server.domain.gpt.presentation.dto.response.CompletionChatResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class RestGptService {
    private final OpenAiService openAiService;
    private final GptAnswerRepository gptAnswerRepository;
    private final GptAnswerFacade gptAnswerFacade;

    @Transactional
    public CompletionChatResponse completionChat(GPTCompletionChatRequest gptCompletionChatRequest) {

        List<ChatMessage> conversation = new ArrayList<>();
        ChatMessage systemMsg = gptCompletionChatRequest.promptStart();
        conversation.add(systemMsg);

        /*List<GptAnswer> gptAnswers = gptAnswerFacade.getGptAnswerByAnswer(gptCompletionChatRequest.getMessage());
        if (gptAnswers.isEmpty()) {
            System.out.println("gptAnswers 리스트가 비어있습니다.");
        } else {
            gptAnswers.forEach(gptAnswer -> System.out.println("결과 : " + gptAnswer.getAnswer()));
        }*/

        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        List<GptAnswer> gptAnswers = gptAnswerFacade.getGptAnswerAllById(sort);

        gptAnswers.forEach(gptAnswer -> {
            conversation.add(new ChatMessage("user", gptAnswer.getQuestion()));
            conversation.add(new ChatMessage("assistant", gptAnswer.getAnswer()));
        });

        ChatMessage userMsg = gptCompletionChatRequest.convertChatMessage();
        conversation.add(userMsg);

        // 확인용 출력
        conversation.forEach(chatMessage -> log.info("{} : {}", chatMessage.getRole(), chatMessage.getContent()));

        ChatCompletionResult chatCompletion = openAiService.createChatCompletion(ChatCompletionRequest.builder()
                .model("gpt-3.5-turbo")
                .messages(conversation)
                .maxTokens(1000)
                .build());

        CompletionChatResponse response = CompletionChatResponse.of(chatCompletion);
        log.info("Chat completed: {}", response);

        List<String> messages = response.getMessages().stream()
                .map(CompletionChatResponse.Message::getMessage)
                .collect(Collectors.toList());

       saveAnswer(messages, gptCompletionChatRequest.getMessage());

        return response;
    }

    private GptAnswer saveAnswer(List<String> response, String requestQuestion) {

        String answer = response.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.joining());

        return gptAnswerRepository.save(GptAnswer.builder()
                .question(requestQuestion)
                .answer(answer).build());
    }

}
