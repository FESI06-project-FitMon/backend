package site.fitmon.review.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.fitmon.challenge.repository.ChallengeEvidenceRepository;
import site.fitmon.common.exception.ApiException;
import site.fitmon.common.exception.ErrorCode;
import site.fitmon.gathering.domain.Gathering;
import site.fitmon.gathering.domain.GatheringStatus;
import site.fitmon.gathering.repository.GatheringParticipantRepository;
import site.fitmon.gathering.repository.GatheringRepository;
import site.fitmon.member.domain.Member;
import site.fitmon.member.repository.MemberRepository;
import site.fitmon.review.domain.Review;
import site.fitmon.review.dto.request.ReviewCreateRequest;
import site.fitmon.review.repository.ReviewRepository;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final MemberRepository memberRepository;
    private final GatheringRepository gatheringRepository;
    private final GatheringParticipantRepository gatheringParticipantRepository;
    private final ChallengeEvidenceRepository challengeEvidenceRepository;

    @Transactional
    public void createReview(@Valid ReviewCreateRequest request, String email, Long gatheringId) {
        Member member = memberRepository.findByEmail(email)
            .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        Gathering gathering = gatheringRepository.findById(gatheringId)
            .orElseThrow(() -> new ApiException(ErrorCode.GATHERING_NOT_FOUND));

        validateReviewCreation(gathering, member);

        Review review = Review.builder()
            .member(member)
            .gathering(gathering)
            .rating(request.getRating())
            .content(request.getContent())
            .build();

        reviewRepository.save(review);
    }

    private void validateReviewCreation(Gathering gathering, Member member) {
        if (!gatheringParticipantRepository.existsByGatheringAndMember(gathering, member)) {
            throw new ApiException(ErrorCode.NOT_GATHERING_PARTICIPANT);
        }

        if (reviewRepository.existsByGatheringAndMember(gathering, member)) {
            throw new ApiException(ErrorCode.ALREADY_WROTE_REVIEW);
        }

        boolean canWriteReview = gathering.getStatus() == GatheringStatus.종료됨 ||
            hasAnyChallengeEvidence(gathering, member);

        if (!canWriteReview) {
            throw new ApiException(ErrorCode.INVALID_REVIEW_CONDITION);
        }
    }

    private boolean hasAnyChallengeEvidence(Gathering gathering, Member member) {
        return challengeEvidenceRepository.hasEvidenceInGathering(member, gathering);
    }

}
