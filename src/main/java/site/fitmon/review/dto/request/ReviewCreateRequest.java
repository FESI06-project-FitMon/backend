package site.fitmon.review.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ReviewCreateRequest {

    @Min(value = 1, message = "평점은 1점이상 가능합니다.")
    @Max(value = 5, message = "평점은 최대 5점이하로 가능합니다.")
    @Schema(description = "평점", example = "5")
    private Integer rating;

    @Size(max = 600, message = "600자 이하로 입력해주세요.")
    @Schema(description = "내용", example = "모임에 참석하고 3대 500 달성했어요!!")
    private String content;
}
