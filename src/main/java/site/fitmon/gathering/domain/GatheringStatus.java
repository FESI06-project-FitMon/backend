package site.fitmon.gathering.domain;

import java.util.Arrays;
import lombok.Getter;

@Getter
public enum GatheringStatus {
    시작전("PENDING", "시작 전"),
    진행중("IN_PROGRESS", "진행 중"),
    종료됨("COMPLETED", "종료됨"),
    취소됨("CANCELLED", "취소됨");

    private final String code;
    private final String description;

    GatheringStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public static GatheringStatus findByCode(String code) {
        return Arrays.stream(GatheringStatus.values())
            .filter(status -> status.getCode().equals(code))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Invalid status code: " + code));
    }
}
