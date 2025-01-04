package site.fitmon.common.exception;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
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
    INVALID_CREDENTIALS(UNAUTHORIZED, "아이디 또는 비밀번호가 올바르지 않습니다."),

    //Gatherings
    EXCEEDS_MAX_TAGS(BAD_REQUEST, "태그는 최대 3개까지만 설정할 수 있습니다."),
    INVALID_START_DATE(BAD_REQUEST, "시작일은 현재시간 이후여야 합니다."),
    INVALID_DATE_RANGE(BAD_REQUEST, "종료일은 현재시간 이후여야 합니다."),

    //Image
    IMAGE_UPLOAD_FAILED(INTERNAL_SERVER_ERROR, "이미지 업로드 중 오류가 발생했습니다."),
    IMAGE_REQUIRED(BAD_REQUEST, "이미지 파일은 필수입니다."),
    IMAGE_SIZE_EXCEEDED(BAD_REQUEST, "이미지 크기는 5MB를 초과할 수 없습니다."),
    INVALID_IMAGE_TYPE(BAD_REQUEST, "JPG, JPEG, PNG 형식의 이미지만 업로드 가능합니다."),
    INVALID_IMAGE_FILENAME(BAD_REQUEST, "잘못된 파일명입니다."),

    //Challenge
    CHALLENGE_START_DATE_BEFORE_GATHERING(BAD_REQUEST, "챌린지 시작일은 모임 시작일 이후여야 합니다."),
    CHALLENGE_END_DATE_AFTER_GATHERING(BAD_REQUEST, "챌린지 종료일은 모임 종료일 이전이어야 합니다."),
    INVALID_CHALLENGE_DATE_RANGE(BAD_REQUEST, "챌린지 시작일은 챌린지 종료일 이전이어야 합니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
