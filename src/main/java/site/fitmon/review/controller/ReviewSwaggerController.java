package site.fitmon.review.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import site.fitmon.common.dto.ApiResponse;
import site.fitmon.common.dto.SliceResponse;
import site.fitmon.review.dto.request.ReviewCreateRequest;
import site.fitmon.review.dto.response.GatheringReviewsResponse;

@Tag(name = "방명록 API", description = "방명록 API")
public interface ReviewSwaggerController {

    @Operation(summary = "방명록 작성")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "201",
            description = "리뷰 생성 성공",
            content = {@Content()}
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "잘못된 입력 값",
            content = {@Content()}
        )})
    ResponseEntity<ApiResponse> createReview(
        @Valid @RequestBody ReviewCreateRequest request,
        @AuthenticationPrincipal UserDetails userDetails,
        @PathVariable Long gatheringId);

    @Operation(summary = "특정 모임의 방명록 리스트를 조회", description = "특정 모임의 방명록 리스트를 조회 합니다. 무한스크롤로 10개씩 받아 옵니다.")
    ResponseEntity<SliceResponse<GatheringReviewsResponse>> getGatheringReviews(
        @PathVariable Long gatheringId,
        @AuthenticationPrincipal UserDetails userDetails,
        @RequestParam(defaultValue = "0") int page
    );
}
