package site.fitmon.fitmon.gatherings.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import site.fitmon.fitmon.gatherings.domain.MainType;
import site.fitmon.fitmon.gatherings.domain.SubType;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GatheringCreateRequest {

    @Schema(description = "모임 제목", example = "같이 운동해요!")
    @NotNull
    private String title;

    @Schema(description = "모임 메인 카테고리", example = "유산소형")
    @NotNull
    private MainType mainType;

    @Schema(description = "모임 서브 카테고리", example = "런닝")
    @NotNull
    private SubType subType;

    @Schema(description = "모임 대표 이미지 URL", example = "https://fitmon-bucket.s3.amazonaws.com/gatherings/af61233a-ed83-432c-b685-1d29a6c75de1_whale.jpg")
    private String imageUrl;

    @Schema(description = "모임 시작 일시", example = "2025-03-10T14:00:00")
    @NotNull
    @Future
    private LocalDateTime startDate;

    @Schema(description = "모임 종료 일시", example = "2025-03-20T16:00:00")
    @NotNull
    @Future
    private LocalDateTime endDate;

    @Schema(description = "모임 메인 장소", example = "서울시")
    @NotNull
    private String mainLocation;

    @Schema(description = "모임 상세 장소", example = "강남구")
    @NotNull
    private String subLocation;

    @Schema(description = "모임 최대 인원", example = "10")
    @NotNull
    @Positive
    private Integer totalCount;

    @Schema(description = "모임 최소 인원", example = "5")
    @NotNull
    @Positive
    private Integer minCount;

    @Schema(description = "모임 태그 목록 (최대 3개)", example = "[\"초보환영\", \"저녁운동\", \"함께달리기\"]")
    private List<String> tags;

    @AssertTrue(message = "종료일은 시작일 이후여야 합니다.")
    private boolean isValidDateRange() {
        return startDate != null && endDate != null && !endDate.isBefore(startDate);
    }

    @AssertTrue(message = "최소 인원은 총 인원을 초과할 수 없습니다.")
    private boolean isValidCountRange() {
        return totalCount != null && minCount != null && totalCount >= minCount;
    }
}

