package site.fitmon.review.dto.response;

import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import site.fitmon.member.domain.Member;
import site.fitmon.review.domain.Review;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GatheringReviewsResponse {

    private Long reviewId;
    private Integer rating;
    private String content;
    private LocalDateTime createDate;
    private Writer writer;
    private boolean reviewOwnerStatus;

    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    class Writer {

        private Long memberId;
        private String nickName;
        private String profileImageUrl;

        public Writer(Member member) {
            this.memberId = member.getId();
            this.nickName = member.getNickName();
            this.profileImageUrl = member.getProfileImageUrl();
        }
    }

    public GatheringReviewsResponse(Review review, Long currentMemberId) {
        this.reviewId = review.getId();
        this.rating = review.getRating();
        this.content = review.getContent();
        this.createDate = review.getCreatedAt();
        this.writer = new Writer(review.getMember());
        this.reviewOwnerStatus = review.getMember().getId().equals(currentMemberId);
    }

}
