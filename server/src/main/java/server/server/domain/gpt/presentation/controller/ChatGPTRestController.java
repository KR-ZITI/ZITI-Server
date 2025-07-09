package server.server.domain.gpt.presentation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import server.server.domain.gpt.presentation.dto.request.GPTCompletionChatRequest;
import server.server.domain.gpt.presentation.dto.response.ChatHistoryResponse;
import server.server.domain.gpt.presentation.dto.response.CompletionChatResponse;
import server.server.domain.gpt.service.GetChatHistoryService;
import server.server.domain.gpt.service.RestGptImageService;
import server.server.domain.gpt.service.RestGptService;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/chatgpt/rest")
@RequiredArgsConstructor
@Tag(name = "AI API", description = "AI에 대한 API입니다.")
public class ChatGPTRestController {

    private final RestGptService gptChatRestService;
    private final RestGptImageService gptImageService;

    private final GetChatHistoryService getChatHistoryService;

    @Operation(summary = "질문 생성", description = "ChatGpt 4.0-o Version에게 질문을 하여 답을 받습니다.")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/completion/chat")
    public CompletionChatResponse completionChat(@RequestBody GPTCompletionChatRequest gptCompletionChatRequest) {
        return gptChatRestService.completionChat(gptCompletionChatRequest);
    }

    @Operation(summary = "이미지 + 질문 생성", description = "이미지를 포함한 질문을 GPT에게 전달합니다.")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> chatWithImage(
            @RequestPart("image") MultipartFile image,
            @RequestPart("message") String message
    ) {
        try {
            String result = gptImageService.completionChatWithImage(message, image);
            return ResponseEntity.ok(Map.of(
                    "messages", List.of(Map.of("message", result))
            ));
        } catch (IOException e) {
            log.error("이미지 처리 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "error", "이미지 처리 중 오류 발생"
            ));
        }
    }

    @Operation(summary = "이전 질문 히스토리", description = "예전에 질문했던 내용을 보관하여 보여줍니다.")
    @GetMapping("/history")
    public ResponseEntity<ChatHistoryResponse> chatHistory() {
        ChatHistoryResponse  response = getChatHistoryService.execute();
        return ResponseEntity.ok(response);
    }
}
