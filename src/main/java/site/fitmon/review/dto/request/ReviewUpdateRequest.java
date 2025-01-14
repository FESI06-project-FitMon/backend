package site.fitmon.review.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ReviewUpdateRequest {

    @Min(value = 1, message = "평점은 1점이상 가능합니다.")
    @Max(value = 5, message = "평점은 최대 5점이하로 가능합니다.")
    @Schema(description = "평점", example = "3")
    private Integer rating;

    @NotBlank(message = "내용은 필수 입니다.")
    @Schema(description = "내용", example = "지루해서 졸았습니다..")
    private String content;
}
