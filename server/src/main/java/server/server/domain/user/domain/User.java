package server.server.domain.user.domain;

import io.swagger.v3.oas.annotations.info.Contact;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import server.server.domain.gpt.domain.GptAnswer;
import server.server.domain.user.enums.Role;
import server.server.global.entity.BaseEntity;

import java.util.List;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {
    // 이메일
    @Length(min = 6, max = 30)
    @Column(unique = true)
    private String email;

    // 패스워드
    @Length(max = 68)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private List<GptAnswer> answers;

    @Builder
    public User(String email, String password, Role role) {
        this.email = email;
        this.password = password;
        this.role = role;
    }
}
