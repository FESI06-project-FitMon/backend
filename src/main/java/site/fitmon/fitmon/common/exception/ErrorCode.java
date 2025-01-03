package site.fitmon.fitmon.common.exception;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    // Common
    INVALID_INPUT_VALUE(BAD_REQUEST, "유효하지 않은 입력값입니다."),
    SERVER_ERROR(INTERNAL_SERVER_ERROR, "서버 오류"),
    USER_NOT_FOUND(UNAUTHORIZED, "인증 정보가 잘못 되었습니다."),

    // Member
    DUPLICATE_MEMBER_EMAIL(BAD_REQUEST, "이미 존재하는 이메일입니다."),
    INVALID_TOKEN(UNAUTHORIZED, "토큰이 올바르지 않습니다."),
    EXPIRED_TOKEN(UNAUTHORIZED, "토큰이 만료 되었습니다."),
    INVALID_CREDENTIALS(UNAUTHORIZED, "아이디 또는 비밀번호가 올바르지 않습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
