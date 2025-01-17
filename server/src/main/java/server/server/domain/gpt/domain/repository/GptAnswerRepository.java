package server.server.domain.gpt.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import server.server.domain.gpt.domain.GptAnswer;

import java.util.List;

@Repository
public interface GptAnswerRepository extends JpaRepository<GptAnswer, Integer> {
    @Query("select a from GptAnswer a where a.answer LIKE %:answer%")
    List<GptAnswer> findAllGptAnswerByAnswerSearch(@Param("answer") String answer);
}

