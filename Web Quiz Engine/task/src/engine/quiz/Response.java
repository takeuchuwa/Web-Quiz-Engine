package engine.quiz;

public class Response {

    private boolean isSuccess;

    private String feedback;

    public Response(){}

    public Response(Quiz quiz, Answers answer) {
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
        this.isSuccess = success;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }
}
