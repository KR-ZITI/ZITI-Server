package server.server.domain.gpt.presentation.dto.request;

import com.theokanning.openai.completion.chat.ChatMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GPTCompletionChatRequest {

    private String model;

    private String role;

    private String message;

    private Integer maxTokens;


    public ChatMessage convertChatMessage() {
        return new ChatMessage("user", message);
    }

    public ChatMessage promptStart() {
        return new ChatMessage("system", "너의 언어는 한국어이고, 존댓말 대신 친구처럼 말을 해야해. 그리고 설정상 너는 나를 잘 도와주는 친구야. 모든 것을 알려주고 싶어하지만, 모르는게 있으면 너도 모른다고 대답을 해");
    }
}
