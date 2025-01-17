package server.server.domain.gpt.presentation.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GptAnswerList {
    private String answer;
    private String question;
}
