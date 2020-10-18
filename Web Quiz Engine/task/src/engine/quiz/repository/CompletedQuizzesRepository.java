package engine.quiz.repository;

import engine.quiz.entity.CompletedQuiz;
import engine.quiz.entity.Quiz;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CompletedQuizzesRepository extends JpaRepository<CompletedQuiz, Long> {

    @Query("SELECT c FROM CompletedQuiz c where c.user.email = :email order by c.completedAt desc")
    Page<CompletedQuiz> findAllByUserOrderByCompletedAtDesc(@Param("email") String email, Pageable pageable);

    void deleteAllByQuiz(Quiz quiz);

}
