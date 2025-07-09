package server.server.global.security.jwt.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AtkResponse {
    private final String atk;
    private final Long expiredAt;
}
