package site.fitmon.review.service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.fitmon.challenge.repository.ChallengeEvidenceRepository;
import site.fitmon.common.dto.PageResponse;
import site.fitmon.common.dto.SliceResponse;
import site.fitmon.common.exception.ApiException;
import site.fitmon.common.exception.ErrorCode;
import site.fitmon.gathering.domain.Gathering;
import site.fitmon.gathering.domain.GatheringStatus;
import site.fitmon.gathering.domain.MainType;
import site.fitmon.gathering.domain.SubType;
import site.fitmon.gathering.repository.GatheringParticipantRepository;
import site.fitmon.gathering.repository.GatheringRepository;
import site.fitmon.member.domain.Member;
import site.fitmon.member.repository.MemberRepository;
import site.fitmon.review.domain.Review;
import site.fitmon.review.dto.request.ReviewCreateRequest;
import site.fitmon.review.dto.request.ReviewUpdateRequest;
import site.fitmon.review.dto.response.GatheringReviewsResponse;
import site.fitmon.review.dto.response.GuestbookResponse;
import site.fitmon.review.dto.response.MyReviewResponse;
import site.fitmon.review.dto.response.ReviewStatisticsDto;
import site.fitmon.review.dto.response.ReviewStatisticsProjection;
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
    public void createReview(ReviewCreateRequest request, String memberId, Long gatheringId) {
        Member member = memberRepository.findById(Long.valueOf(memberId))
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

    @Transactional
    public void updateReview(ReviewUpdateRequest request, String memberId, Long gatheringId, Long guestbookId) {
        Member member = memberRepository.findById(Long.valueOf(memberId))
            .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        Gathering gathering = gatheringRepository.findById(gatheringId)
            .orElseThrow(() -> new ApiException(ErrorCode.GATHERING_NOT_FOUND));

        Review review = reviewRepository.findById(guestbookId)
            .orElseThrow(() -> new ApiException(ErrorCode.REVIEW_NOT_FOUND));

        if (!review.isGathering(gathering)) {
            throw new ApiException(ErrorCode.REVIEW_WRITER_NOT_FOUND);
        }

        if (!review.isWriter(member)) {
            throw new ApiException(ErrorCode.INVALID_REVIEW_GATHERING);
        }

        review.update(request.getRating(), request.getContent());
    }

    @Transactional
    public void deleteReview(String memberId, Long gatheringId, Long guestbookId) {
        Member member = memberRepository.findById(Long.valueOf(memberId))
            .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        Gathering gathering = gatheringRepository.findById(gatheringId)
            .orElseThrow(() -> new ApiException(ErrorCode.GATHERING_NOT_FOUND));

        Review review = reviewRepository.findById(guestbookId)
            .orElseThrow(() -> new ApiException(ErrorCode.REVIEW_NOT_FOUND));

        if (!review.isGathering(gathering)) {
            throw new ApiException(ErrorCode.REVIEW_WRITER_NOT_FOUND);
        }

        if (!review.isWriter(member)) {
            throw new ApiException(ErrorCode.INVALID_REVIEW_GATHERING);
        }

        reviewRepository.delete(review);
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

    public SliceResponse<GatheringReviewsResponse> getGatheringReviews(Long gatheringId, String memberId,
        PageRequest pageable) {
        final Long currentMemberId = memberId != null ?
            memberRepository.findById(Long.valueOf(memberId))
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND))
                .getId()
            : null;
        Gathering findedGathering = gatheringRepository.findById(gatheringId)
            .orElseThrow(() -> new ApiException(ErrorCode.GATHERING_NOT_FOUND));

        Slice<Review> slice = reviewRepository.findAllWithMemberByGatheringId(
            findedGathering.getId(), pageable);

        List<GatheringReviewsResponse> response = slice.getContent().stream()
            .map(review -> new GatheringReviewsResponse(review, currentMemberId))
            .toList();

        return new SliceResponse<>(
            response,
            slice.hasNext()
        );
    }

    public PageResponse<MyReviewResponse> findMyReviews(String memberId, Pageable pageable) {
        Member member = memberRepository.findById(Long.valueOf(memberId))
            .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));
        Page<Review> reviews = reviewRepository.findAllByMemberId(
            member.getId(),
            PageRequest.of(
                pageable.getPageNumber(),
                10,
                Sort.by(Sort.Direction.DESC, "createdAt")
            )
        );

        List<MyReviewResponse> response = reviews.getContent().stream()
            .map(MyReviewResponse::from)
            .toList();

        return new PageResponse<>(
            response,
            reviews.getNumber(),
            reviews.getTotalElements(),
            reviews.getTotalPages()
        );
    }

    public ReviewStatisticsDto getReviewStatistics(
        MainType mainType,
        SubType subType,
        String mainLocation,
        String subLocation,
        LocalDate searchDate
    ) {
        List<ReviewStatisticsProjection> statistics = reviewRepository.findReviewStatistics(
            mainType, subType, mainLocation, subLocation, searchDate
        );

        Map<String, Long> ratingCounts = new HashMap<>();
        for (int i = 1; i <= 5; i++) {
            ratingCounts.put(i + "점", 0L);
        }

        statistics.forEach(stat -> ratingCounts.put(stat.getRating() + "점", stat.getCount()));

        double totalRatings = statistics.stream()
            .mapToDouble(stat -> stat.getRating() * stat.getCount())
            .sum();
        long totalReviews = statistics.stream()
            .mapToLong(ReviewStatisticsProjection::getCount)
            .sum();
        double averageRating = totalReviews > 0 ? totalRatings / totalReviews : 0.0;

        return new ReviewStatisticsDto(averageRating, ratingCounts);
    }

    public SliceResponse<GuestbookResponse> getGuestbookEntries(
        MainType mainType,
        SubType subType,
        String mainLocation,
        String subLocation,
        LocalDate searchDate,
        String sortBy,
        String sortDirection,
        int page,
        int pageSize
    ) {
        Sort.Direction direction = sortDirection.equalsIgnoreCase("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, resolveSortField(sortBy));
        Pageable pageable = PageRequest.of(page, pageSize, sort);

        Page<GuestbookResponse> guestbookEntries = reviewRepository.findGuestbookEntries(
            mainType, subType, mainLocation, subLocation, searchDate, pageable
        );
        return new SliceResponse<>(
            guestbookEntries.getContent(),
            guestbookEntries.hasNext()
        );
    }

    private String resolveSortField(String sortBy) {
        switch (sortBy) {
            case "highestRating":
                return "reviewScore";
            case "mostParticipants":
                return "participantCount";
            default:
                return "createdAt";
        }
    }
}
