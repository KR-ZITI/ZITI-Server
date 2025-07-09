package server.server.domain.gpt.presentation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ChatHistoryResponse {
    private List<ChatHistoryList> list;
}
