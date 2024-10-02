package pedribault.game.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.http.ResponseEntity;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(TheGameException.class)
    @ResponseBody
    public ResponseEntity<ErrorResponse> handleTheGameException(TheGameException ex) {
        log.error("[TheGameException]=[[STATUS]=[{}],[MESSAGE]=[{}],[DETAILED_MESSAGE]=[{}]]",
                ex.getStatus(), ex.getMessage(), ex.getDetailedMessage());
        ErrorResponse errorResponse = new ErrorResponse(ex.getStatus(), ex.getMessage(), ex.getDetailedMessage());
        return new ResponseEntity<>(errorResponse, ex.getStatus());
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex) {
        log.error("[ERROR]=[[CLASS]=[{}],[MESSAGE]=[{}],[LOCALIZED_MESSAGE]=[{}]]",
                ex.getClass(), ex.getMessage(), ex.getLocalizedMessage());
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred.", ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ResponseBody
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        if (e instanceof TheGameException) {
            return handleTheGameException((TheGameException) e);
        } else {
            return handleGeneralException(e);
        }
    }

}
