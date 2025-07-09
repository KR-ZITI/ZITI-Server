package server.server.domain.gpt.domain;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import server.server.domain.user.domain.User;
import server.server.global.entity.BaseEntity;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GptAnswer extends BaseEntity {

    @Column(name = "gpt_answer", length = 3000, nullable = false)
    private String answer;

    @Column(name = "gpt_question", length = 1500, nullable = false)
    private String question;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Builder
    public GptAnswer(String answer, String question, User user) {
        this.answer = answer;
        this.question = question;
        this.user = user;
    }
}
