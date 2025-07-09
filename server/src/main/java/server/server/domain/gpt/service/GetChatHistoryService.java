package server.server.domain.gpt.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import server.server.domain.gpt.domain.GptAnswer;
import server.server.domain.gpt.presentation.dto.response.ChatHistoryList;
import server.server.domain.gpt.presentation.dto.response.ChatHistoryResponse;
import server.server.domain.user.domain.User;
import server.server.domain.user.domain.facade.UserFacade;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetChatHistoryService {
    private final UserFacade userFacade;

    @Transactional(readOnly = true)
    public ChatHistoryResponse execute() {
        User user = userFacade.getCurrentUser();
        List<GptAnswer> answers = user.getAnswers();

        List<ChatHistoryList> list = answers.stream()
                .map(a -> ChatHistoryList.builder()
                        .question(a.getQuestion())
                        .answer(a.getAnswer())
                        .createdAt(a.getCreatedDate())
                        .build()).toList();

        return new ChatHistoryResponse(list);
    }
}
