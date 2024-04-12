package northeastern.xiaosongzhai.medical.exception;

import java.io.Serial;

/**
 * @Author: Xiaosong Zhai
 * @Date: 2024/4/11 3:50
 * @Description: custom exception
 */
public class CustomException extends RuntimeException{

    @Serial
    private static final long serialVersionUID = -8514355987198608731L;

    private final String errorCode;
    private final String errorMessage;

    public CustomException(String errorCode, String errorMessage) {
        super(errorMessage);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public CustomException(String errorCode, String errorMessage, Throwable cause) {
        super(errorMessage, cause);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

}
