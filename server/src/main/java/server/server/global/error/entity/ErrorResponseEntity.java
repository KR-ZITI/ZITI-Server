package server.server.global.error.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import server.server.global.error.ErrorCode;


@Data
@Builder
public class ErrorResponseEntity {
    private int status;
    private String code;
    private String message;

    public static ResponseEntity<ErrorResponseEntity> responseEntity(ErrorCode e){
        return ResponseEntity
                .status(e.getHttpStatus())
                .body(ErrorResponseEntity.builder()
                        .status(e.getHttpStatus())
                        .code(e.name())
                        .message(e.getMessage())
                        .build()
                );
    }
}
