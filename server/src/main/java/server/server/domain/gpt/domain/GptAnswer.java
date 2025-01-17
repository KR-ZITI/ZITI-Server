package server.server.domain.gpt.domain;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import server.server.global.entity.BaseEntity;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GptAnswer extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "gpt_answer", length = 1500, nullable = false)
    private String answer;

    @Column(name = "gpt_question", length = 1500, nullable = false)
    private String question;

    @Builder
    public GptAnswer(String answer, String question) {
        this.answer = answer;
        this.question = question;
    }
}
