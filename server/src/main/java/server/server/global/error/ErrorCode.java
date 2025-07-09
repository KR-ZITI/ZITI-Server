package server.server.global.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    BAD_REQUEST(400, "Bad Request"),

    JUMUN_NOT_FOUND(404, "Jumun Not Found"),
    USER_NOT_FOUND(404, "User Not Found"),
    USER_ALREADY_EXISTS(409, "User Already Exists"),
    USER_MISS_MATCHED(409, "User Miss Match"),
    ROLE_NOT_FOUND(404,"Role Not Found"),
    ORDER_TYPE_NOT_FOUND(404,"Order Type Not Found"),
    BOARD_NOT_FOUND(404, "Board Not Found"),
    COMMENT_NOT_FOUND(404, "Comment Not Found"),
    REPORT_USER_NOT_FOUND(404, "Report User Not Found"),
    REPORT_COMMENT_NOT_FOUND(404, "Report Comment Not Found"),
    REPORT_BOARD_NOT_FOUND(404, "Report Board Not Found"),
    EMAIL_NOT_FOUND(404, "Email Not Found"),
    EMAIL_MISS_MATCHED(409, "Email Miss Match"),
    CODE_NOT_FOUND(404, "Code Not Found"),
    CODE_EXPIRED(401, "Code Expired"),
    ACCOUNT_ID_NOT_CHECK(401, "AccountId Not Check"),
    PASSWORD_MISS_MATCHED(409, "Password Miss Match"),
    ORIGIN_PASSWORD_MISS_MATCHED(409, "Origin Password Miss Match"),
    CHANGE_PASSWORD_MISS_MATCHED(409, "Change Password Miss Match"),
    SELF_COMMENT_NOT_ALLOWED(409, "Self Comment Not Allowed"),
    SELF_PAYMENT_NOT_ALLOWED(409, "Self Payment Not Allowed"),
    ALREADY_REPLIED(409, "Already Replied"),
    ONLY_COUNSELOR_CAN_REPLY(403, "오직 상담사만 답장 할 수 있습니다."),
    NOT_LOCAL_ACCOUNT(403, "로컬 회원가입한 계정이 아닙니다."),
    INSUFFICIENT_BALANCE(400, "보유 금액보다 결제 금액이 큽니다."),

    CODE_MISS_MATCHED(401,"Code Miss Match"),

    EXIST_EMAIL(409, "You already have an email created."),
    EXIST_USER(409, "Exist User"),

    IMAGE_NOT_FOUND(404, "Image Not Found"),
    INVALID_IMAGE_EXTENSION(409, "Invalid Image Extension"),

    NOTIFICATION_NOT_FOUND(404, "Notification Not Found"),
    INTERNAL_SERVER_ERROR(500, "Internal Server Error"),

    YOUTUBE_ACCOUNT_NOT_FOUND(404, "Youtube Account Not Found"),

    INVALID_FILE_TYPE(400, "허용되지 않은 파일 형식입니다."),
    FILE_UPLOAD_FAIL(500, "파일 업로드에 실패했습니다."),


    JWT_INVALID(401, "Jwt Invalid"),
    JWT_EXPIRED(401, "Jwt Expired"),

    EMAIL_SEND_FAILED(502, "메일 발송에 실패했습니다.");

    private final Integer httpStatus;

    private final String message;
}
