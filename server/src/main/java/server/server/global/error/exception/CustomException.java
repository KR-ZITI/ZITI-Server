package server.server.global.error.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import server.server.global.error.ErrorCode;

@Getter
@AllArgsConstructor
public class CustomException extends RuntimeException{
    private final ErrorCode errorCode;
}
