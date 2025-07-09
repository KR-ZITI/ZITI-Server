package server.server.domain.gpt.presentation.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GPTChatWithImageRequest {
    private String message;         // 사용자 질문
    private String imageBase64;     // base64 이미지
}

