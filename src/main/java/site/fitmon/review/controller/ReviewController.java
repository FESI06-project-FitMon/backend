package site.fitmon.review.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import site.fitmon.common.dto.ApiResponse;
import site.fitmon.review.dto.request.ReviewCreateRequest;
import site.fitmon.review.service.ReviewService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class ReviewController implements ReviewSwaggerController {

    private final ReviewService reviewService;

    @PostMapping("/gatherings/{gatheringId}/guestbooks")
    public ResponseEntity<ApiResponse> createReview(
        @Valid @RequestBody ReviewCreateRequest request,
        @AuthenticationPrincipal UserDetails userDetails,
        @PathVariable Long gatheringId) {

        reviewService.createReview(request, userDetails.getUsername(), gatheringId);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.of("리뷰 생성 성공"));
    }
}
