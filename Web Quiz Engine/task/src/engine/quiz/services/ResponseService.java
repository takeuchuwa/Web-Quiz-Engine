package engine.quiz.services;

import engine.quiz.entity.Answers;
import engine.quiz.entity.Quiz;
import org.springframework.stereotype.Service;

@Service
public class ResponseService {

    private boolean isSuccess;

    private String feedback;

    public ResponseService(){}

    public ResponseService(Quiz quiz, Answers answer) {
        if (quiz.getAnswer().equals(answer.getAnswer())) {
            isSuccess = true;
            feedback = "Congratulations, you're right!";
        } else {
            isSuccess = false;
            feedback = "Wrong answer! Please, try again.";
        }
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }
}
