package site.fitmon.challenge.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ChallengeCreateRequest {

    @Schema(description = "챌린지 제목", example = "3km 챌린지")
    @NotNull(message = "제목은 필수 입력 값입니다.")
    private String title;

    @Schema(description = "모임 상세", example = "이번주 내로 3km 도전!")
    @Size(max = 50, message = "50자 이하로 입력해주세요.")
    private String description;

    @Schema(description = "챌린지 이미지 URL", example = "https://fitmon-bucket.s3.amazonaws.com/challenges/df171e45-29b7-4b37-9ad4-fbbe7c94e417_running.jpg")
    private String imageUrl;

    @Schema(description = "챌린지 시작 일시", example = "2025-03-10T14:00:00")
    @Future(message = "시작일은 현재 시간 이후여야 합니다.")
    private LocalDateTime startDate;

    @Schema(description = "챌린지 종료 일시", example = "2025-03-20T16:00:00")
    @Future(message = "종료일은 현재 시간 이후여야 합니다.")
    private LocalDateTime endDate;
}
