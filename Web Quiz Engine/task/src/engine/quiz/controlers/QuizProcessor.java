package engine.quiz.controlers;

import engine.quiz.Answers;
import engine.quiz.Response;
import engine.quiz.entity.Quiz;
import engine.quiz.entity.User;
import engine.quiz.repository.QuizRepository;
import engine.quiz.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
public class QuizProcessor {

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private UserRepository userRepository;

    @PostMapping(value = "/api/quizzes", consumes = "application/json")
    public ResponseEntity<Quiz> addQuiz(@Valid @RequestBody Quiz quiz) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserDetails userDetails = (UserDetails) principal;
        String email = userDetails.getUsername();
        User user = userRepository.findUserByEmail(email).get();
        quiz.setCreateUser(user);
        quizRepository.save(quiz);
        return new ResponseEntity<>(quiz, HttpStatus.OK);
    }

    @GetMapping(path = "/api/quizzes/{id}")
    public ResponseEntity<Quiz> getQuiz(@PathVariable Long id) {
        Quiz quiz = quizRepository.findById(id).orElse(null);
        return quiz == null
                ? new ResponseEntity<>(HttpStatus.NOT_FOUND)
                : new ResponseEntity<>(quiz, HttpStatus.OK);
    }

    @GetMapping(path = "/api/quizzes")
    public ResponseEntity<List<Quiz>> getQuizzes() {
        return new ResponseEntity<>(new ArrayList<>(quizRepository.findAll()), HttpStatus.OK);
    }

    @PostMapping(path = "/api/quizzes/{id}/solve")
    public ResponseEntity<Response> solveQuiz(@PathVariable Long id, @RequestBody Answers answer) {
        Quiz quiz = quizRepository.findById(id).orElse(null);
        return quiz == null
                ? new ResponseEntity<>(HttpStatus.NOT_FOUND)
                : new ResponseEntity<>(new Response(quiz, answer), HttpStatus.OK);
    }

    @DeleteMapping(path = "/api/quizzes/{id}")
    public ResponseEntity<Quiz> deleteQuiz(@PathVariable Long id) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserDetails userDetails = (UserDetails) principal;
        String email = userDetails.getUsername();
        User user = userRepository.findUserByEmail(email).get();
        Quiz quiz = quizRepository.findById(id).orElse(null);
        if (quiz == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        if (quiz.getCreateUser().getId() != user.getId()) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        quizRepository.delete(quiz);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
