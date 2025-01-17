package server.server.domain.gpt.domain.facade;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import server.server.domain.gpt.domain.GptAnswer;
import server.server.domain.gpt.domain.repository.GptAnswerRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GptAnswerFacade {
    private final GptAnswerRepository gptAnswerRepository;

    public List<GptAnswer> getGptAnswerByAnswer(String answer){
        return gptAnswerRepository.findAllGptAnswerByAnswerSearch(answer);
    }

    public List<GptAnswer> getGptAnswerAllById(Sort sort){
        return gptAnswerRepository.findAll(sort);
    }
}
