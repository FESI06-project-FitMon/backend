package site.fitmon.review.dto.response;

import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import site.fitmon.review.domain.Review;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MyReviewResponse {

    private Long reviewId;
    private Long gatheringId;
    private String content;
    private Integer rating;
    private LocalDateTime createdAt;

    public static MyReviewResponse from(Review review) {
        return new MyReviewResponse(
            review.getId(),
            review.getGathering().getId(),
            review.getContent(),
            review.getRating(),
            review.getCreatedAt()
        );
    }

    @Builder
    private MyReviewResponse(Long reviewId, Long gatheringId,
        String content, Integer rating, LocalDateTime createdAt) {
        this.reviewId = reviewId;
        this.gatheringId = gatheringId;
        this.content = content;
        this.rating = rating;
        this.createdAt = createdAt;
    }
}
