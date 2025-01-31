package site.fitmon.review.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import site.fitmon.common.domain.BaseEntity;
import site.fitmon.gathering.domain.Gathering;
import site.fitmon.member.domain.Member;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Review extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gathering_id", nullable = false)
    private Gathering gathering;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false, length = 600)
    private String content;

    @Column(nullable = false)
    private Integer rating;

    @Builder
    public Review(Gathering gathering, Member member, String content, Integer rating) {
        validateRating(rating);
        this.gathering = gathering;
        this.member = member;
        this.content = content;
        this.rating = rating;
    }

    private void validateRating(Integer rating) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("별점은 1에서 5 사이여야 합니다.");
        }
    }

    public void update(Integer rating, String content) {
        this.rating = rating;
        this.content = content;
    }

    public boolean isWriter(Member member) {
        return this.member.getId().equals(member.getId());
    }

    public boolean isGathering(Gathering gathering) {
        return this.gathering.getId().equals(gathering.getId());
    }
}
