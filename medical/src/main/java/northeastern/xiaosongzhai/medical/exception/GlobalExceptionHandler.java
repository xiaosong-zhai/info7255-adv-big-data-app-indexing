package northeastern.xiaosongzhai.medical.exception;

import northeastern.xiaosongzhai.medical.response.Response;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * @Author: Xiaosong Zhai
 * @Date: 2024/2/6 23:01
 * @Description: global exception
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        // if the request body is not valid
        return ResponseEntity.badRequest().body("Data does not match the schema specified");
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<Object> handleDuplicateKeyException(DuplicateKeyException e) {
        // if the key is duplicated
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException e) {
        // if the argument is illegal
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("The requested resource does not exist");
    }

}
