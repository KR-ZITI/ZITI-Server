package server.server.domain.gpt.presentation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class ChatHistoryList {
    private String question;
    private String answer;
    private LocalDateTime createdAt;
}
