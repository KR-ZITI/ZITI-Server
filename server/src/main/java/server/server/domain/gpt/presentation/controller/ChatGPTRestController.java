package server.server.domain.gpt.presentation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import server.server.domain.gpt.presentation.dto.request.GPTCompletionChatRequest;
import server.server.domain.gpt.presentation.dto.response.CompletionChatResponse;
import server.server.domain.gpt.service.RestGptService;

@RestController
@RequestMapping("/api/chatgpt/rest")
@RequiredArgsConstructor
@Tag(name = "AI API", description = "AI에 대한 API입니다.")
public class ChatGPTRestController {

    private final RestGptService gptChatRestService;

    @Operation(summary = "질문 생성", description = "ChatGpt 3.5-turbo Version에게 질문을 하여 답을 받습니다.")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/completion/chat")
    public CompletionChatResponse completionChat(final @RequestBody GPTCompletionChatRequest gptCompletionChatRequest) {
        return gptChatRestService.completionChat(gptCompletionChatRequest);
    }

}
