package server.server.domain.user.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {
    ROLE_USER("User", "고객"),
    ROLE_ADMIN("ADMIN", "어드민");

    private final String key;
    private final String title;
}
