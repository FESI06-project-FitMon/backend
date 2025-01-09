package site.fitmon.review.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import site.fitmon.auth.domain.CustomUserDetails;
import site.fitmon.common.dto.ApiResponse;
import site.fitmon.common.dto.SliceResponse;
import site.fitmon.review.dto.request.ReviewCreateRequest;
import site.fitmon.review.dto.response.GatheringReviewsResponse;
import site.fitmon.review.service.ReviewService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class ReviewController implements ReviewSwaggerController {

    private final ReviewService reviewService;

    @PostMapping("/gatherings/{gatheringId}/guestbooks")
    public ResponseEntity<ApiResponse> createReview(
        @Valid @RequestBody ReviewCreateRequest request,
        @AuthenticationPrincipal CustomUserDetails userDetails,
        @PathVariable Long gatheringId) {

        reviewService.createReview(request, userDetails.getUsername(), gatheringId);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.of("리뷰 생성 성공"));
    }

    @GetMapping("/gatherings/{gatheringId}/guestbooks")
    public ResponseEntity<SliceResponse<GatheringReviewsResponse>> getGatheringReviews(
        @PathVariable Long gatheringId,
        @AuthenticationPrincipal CustomUserDetails userDetails,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "4") int pageSize
    ) {
        PageRequest pageable = PageRequest.of(page, pageSize, Sort.by(Direction.DESC, "createdAt"));
        String email = userDetails != null ? userDetails.getUsername() : null;
        return ResponseEntity.ok(reviewService.getGatheringReviews(gatheringId, email, pageable));
    }
}
