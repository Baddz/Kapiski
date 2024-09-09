package pedribault.game.exceptions;

import org.springframework.http.HttpStatus;

public class TheGameException extends RuntimeException {

    private HttpStatus status;
    private String message;
    private String detailedMessage;

    public TheGameException(HttpStatus status, String message, String detailedMessage) {
        super(message);
        this.message = message;
        this.status = status;
        this.detailedMessage = detailedMessage;
    }

    public TheGameException(HttpStatus status, String message) {
        super(message);
        this.message = message;
        this.status = status;
        this.detailedMessage = null;
    }

    public String getMessage() {
        return message;
    }

    public String getDetailedMessage() {
        return detailedMessage;
    }

    public HttpStatus getStatus() {
        return status;
    }

}
