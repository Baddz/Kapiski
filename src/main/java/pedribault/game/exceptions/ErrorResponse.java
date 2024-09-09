package pedribault.game.exceptions;

import org.springframework.http.HttpStatus;

public class ErrorResponse {

    private HttpStatus status; // Le statut HTTP
    private String message;    // Le message personnalisé
    private String errorDetail; // Le message d'erreur de l'exception

    public ErrorResponse(HttpStatus status, String message, String errorDetail) {
        this.status = status;
        this.message = message;
        this.errorDetail = errorDetail;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getErrorDetail() {
        return errorDetail;
    }

    public void setErrorDetail(String errorDetail) {
        this.errorDetail = errorDetail;
    }

}
