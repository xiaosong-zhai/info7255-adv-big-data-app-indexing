package northeastern.xiaosongzhai.medical.response;

import lombok.Getter;

/**
 * @Author: Xiaosong Zhai
 * @Date: 2024/2/6 22:34
 * @Description: response code for all requests
 */
@Getter
public enum ResponseCode {

    SUCCESS(200),
    CREATED(201),
    NO_CONTENT(204),
    NOT_MODIFIED(304),
    BAD_REQUEST(400),
    UNAUTHORIZED(401),
    NOT_FOUND(404),
    METHOD_NOT_ALLOWED(405),
    Forbidden(403),
    SERVICE_UNAVAILABLE(503);


    private final int code;

    ResponseCode(int code) {
        this.code = code;
    }
}
