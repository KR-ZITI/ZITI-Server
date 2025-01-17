package server.server;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import server.server.domain.gpt.domain.GptAnswer;
import server.server.domain.gpt.domain.repository.GptAnswerRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class GptAnswerRepositoryTest {

    @Autowired
    private GptAnswerRepository gptAnswerRepository;

    @Test
    public void testFindAllGptAnswerByKeyword() {
        // 사전 설정된 데이터베이스에 존재하는 데이터에 대한 테스트
        gptAnswerRepository.save(GptAnswer.builder().answer("손흥민은 멋진 인물이야.").question("손흥민이 누구야?").build());
        gptAnswerRepository.save(GptAnswer.builder().answer("손흥민는 선수야").question("손흥민이 누구지?").build());
        gptAnswerRepository.save(GptAnswer.builder().answer("손흥만은 좋은 인물 이징").question("손흥만이 누구야?").build());

        List<GptAnswer> results = gptAnswerRepository.findAllGptAnswerByAnswerSearch("손흥민이 누구야?");

        assertNotNull(results);
        assertFalse(results.isEmpty());

        // 결과 출력 (Optional)
        results.forEach(gptAnswer -> System.out.println("결과 : " + gptAnswer.getAnswer()));
    }
}
