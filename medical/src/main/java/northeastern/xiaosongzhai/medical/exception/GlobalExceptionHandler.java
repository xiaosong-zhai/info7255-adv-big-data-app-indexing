package northeastern.xiaosongzhai.medical.exception;

import northeastern.xiaosongzhai.medical.response.Response;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

/**
 * @Author: Xiaosong Zhai
 * @Date: 2024/2/6 23:01
 * @Description: global exception
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({MethodArgumentNotValidException.class, HttpMessageNotReadableException.class})
    public ResponseEntity<Object> handleValidationExceptions(Exception ex, WebRequest request) {
        // 检查异常类型并相应处理
        if (ex instanceof MethodArgumentNotValidException) {
            return ResponseEntity.badRequest().body("Data does not match the schema specified for MethodArgumentNotValidException");
        } else if (ex instanceof HttpMessageNotReadableException) {
            return ResponseEntity.badRequest().body("Data does not match the schema specified for HttpMessageNotReadableException");
        } else {
            return ResponseEntity.badRequest().body("Data does not match the schema specified for other exceptions");
        }
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
