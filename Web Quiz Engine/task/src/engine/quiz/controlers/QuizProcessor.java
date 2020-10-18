package engine.quiz.controlers;

import engine.quiz.dto.CompletedQuizDto;
import engine.quiz.dto.QuizDto;
import engine.quiz.entity.Answers;
import engine.quiz.entity.CompletedQuiz;
import engine.quiz.entity.Quiz;
import engine.quiz.entity.User;
import engine.quiz.repository.CompletedQuizzesRepository;
import engine.quiz.repository.QuizRepository;
import engine.quiz.repository.UserRepository;
import engine.quiz.services.ResponseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.Valid;

@RestController
public class QuizProcessor {

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CompletedQuizzesRepository completedQuizzesRepository;

    @PostMapping(value = "/api/quizzes", consumes = "application/json")
    public ResponseEntity<QuizDto> addQuiz(@Valid @RequestBody Quiz quiz) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserDetails userDetails = (UserDetails) principal;
        String email = userDetails.getUsername();
        User user = userRepository.findUserByEmail(email).get();
        quiz.setCreateUser(user);
        quizRepository.save(quiz);
        QuizDto quizDto = convertQuizDto(quiz);
        return new ResponseEntity<>(quizDto, HttpStatus.OK);
    }

    @GetMapping(path = "/api/quizzes/{id}")
    public ResponseEntity<QuizDto> getQuiz(@PathVariable Long id) {
        Quiz quiz = quizRepository.findById(id).orElse(null);
        if (quiz != null) {
            QuizDto quizDto = convertQuizDto(quiz);
            return new ResponseEntity<>(quizDto, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

    }

    @GetMapping(path = "/api/quizzes")
    public Page<QuizDto> getQuizzes(
            @RequestParam(required = false, defaultValue = "0") Integer page
    ) {

        Pageable paging = PageRequest.of(page, 10);

        return quizRepository.findAll(paging)
                .map(this::convertQuizDto);
    }

    @GetMapping(path = "/api/quizzes/completed")
    public Page<CompletedQuizDto> getCompletedQuizzes(
            @RequestParam(required = false, defaultValue = "0") Integer page
    ) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserDetails userDetails = (UserDetails) principal;

        Pageable paging = PageRequest.of(page, 10);
        return completedQuizzesRepository
                .findAllByUserOrderByCompletedAtDesc(userDetails.getUsername(), paging)
                .map(this::convertCompletedQuizDto);
    }

    @PostMapping(path = "/api/quizzes/{id}/solve")
    public ResponseEntity<ResponseService> solveQuiz(@PathVariable Long id, @RequestBody Answers answer) {
        Quiz quiz = quizRepository.findById(id).orElse(null);
        if (quiz != null) {
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            UserDetails userDetails = (UserDetails) principal;
            String email = userDetails.getUsername();
            User user = userRepository.findUserByEmail(email).get();
            ResponseService responseService = new ResponseService(quiz, answer);
            if (responseService.isSuccess()) {
                CompletedQuiz completedQuiz = new CompletedQuiz();
                completedQuiz.setUser(user);
                completedQuiz.setQuiz(quiz);
                completedQuizzesRepository.save(completedQuiz);
            }
            return new ResponseEntity<>(responseService, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Transactional
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
        completedQuizzesRepository.deleteAllByQuiz(quiz);
        quizRepository.delete(quiz);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    private QuizDto convertQuizDto(Quiz quiz) {
        QuizDto quizDto = new QuizDto();
        quizDto.setId(quiz.getId());
        quizDto.setTitle(quiz.getTitle());
        quizDto.setText(quiz.getText());
        quizDto.setOptions(quiz.getOptions());
        return quizDto;
    }

    private CompletedQuizDto convertCompletedQuizDto(CompletedQuiz completedQuiz) {
        CompletedQuizDto completedQuizDto = new CompletedQuizDto();
        completedQuizDto.setId(completedQuiz.getQuiz().getId());
        completedQuizDto.setCompletedAt(completedQuiz.getCompletedAt());
        return completedQuizDto;
    }
}
