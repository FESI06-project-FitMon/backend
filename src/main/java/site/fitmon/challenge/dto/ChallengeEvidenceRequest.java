package site.fitmon.challenge.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ChallengeEvidenceRequest {

    @Schema(description = "챌린지 인증 이미지 URL", example = "https://fitmon-bucket.s3.amazonaws.com/challenges/df171e45-29b7-4b37-9ad4-fbbe7c94e417_running.jpg")
    @NotNull(message = "인증 이미지는 필수 입력 값입니다.")
    private String imageUrl;
}
