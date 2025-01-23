package site.fitmon.member.service;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.fitmon.common.dto.PageResponse;
import site.fitmon.common.exception.ApiException;
import site.fitmon.common.exception.ErrorCode;
import site.fitmon.gathering.domain.Gathering;
import site.fitmon.gathering.domain.GatheringParticipant;
import site.fitmon.gathering.dto.response.ParticipantsResponse;
import site.fitmon.gathering.repository.GatheringParticipantRepository;
import site.fitmon.gathering.repository.GatheringRepository;
import site.fitmon.member.domain.Member;
import site.fitmon.member.dto.request.MemberUpdateRequest;
import site.fitmon.member.dto.response.MemberCalendarResponse;
import site.fitmon.member.dto.response.MemberCaptainGatheringResponse;
import site.fitmon.member.dto.response.MemberParticipantsResponse;
import site.fitmon.member.dto.response.MemberResponse;
import site.fitmon.member.repository.MemberRepository;
import site.fitmon.review.repository.ReviewRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final GatheringParticipantRepository gatheringParticipantRepository;
    private final ReviewRepository reviewRepository;
    private final GatheringRepository gatheringRepository;

    public MemberResponse getMemberInfo(String memberId) {
        Member member = memberRepository.findById(Long.valueOf(memberId))
            .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        return MemberResponse.builder()
            .memberId(member.getId())
            .nickName(member.getNickName())
            .email(member.getEmail())
            .profileImageUrl(member.getProfileImageUrl())
            .build();
    }

    @Transactional
    public void updateMember(@Valid MemberUpdateRequest request, String memberId) {
        Member member = memberRepository.findById(Long.valueOf(memberId))
            .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));
        member.updateMemberProfile(request.getNickName(), request.getProfileImageUrl());
    }

    public PageResponse<MemberCalendarResponse> getCalendarGatherings(String memberId, PageRequest pageable) {
        Member member = memberRepository.findById(Long.valueOf(memberId))
            .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        Page<GatheringParticipant> participantGatherings = gatheringParticipantRepository.findByMemberId(member.getId(),
            pageable);

        List<MemberCalendarResponse> response = participantGatherings.getContent()
            .stream()
            .map(participant -> {
                Gathering gathering = participant.getGathering();
                return new MemberCalendarResponse(
                    gathering.getId(),
                    participant.isCaptainStatus(),
                    true,
                    gathering.getTitle(),
                    gathering.getDescription(),
                    gathering.getMainType(),
                    gathering.getSubType(),
                    gathering.getImageUrl(),
                    gathering.getStartDate(),
                    gathering.getEndDate(),
                    gathering.getStatus()
                );
            })
            .toList();

        return new PageResponse<>(
            response,
            participantGatherings.getNumber(),
            participantGatherings.getTotalElements(),
            participantGatherings.getTotalPages()
        );
    }

    public PageResponse<MemberParticipantsResponse> getParticipantsGatherings(String memberId, PageRequest pageable) {
        Member member = memberRepository.findById(Long.valueOf(memberId))
            .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        Page<GatheringParticipant> participantGatherings = gatheringParticipantRepository
            .findByMemberIdAndCaptainStatusFalse(member.getId(), pageable);

        List<MemberParticipantsResponse> responses = participantGatherings.getContent().stream()
            .map(participant -> {
                Gathering gathering = participant.getGathering();

                Long participantCount = getParticipantCount(gathering);
                List<ParticipantsResponse> participants = getRecentParticipants(gathering);
                Double averageRating = getAvgRating(gathering);
                Long guestBookCount = getReviewCount(gathering);

                return new MemberParticipantsResponse(
                    gathering.getId(),
                    participant.isCaptainStatus(),
                    true,
                    gathering.getTitle(),
                    gathering.getDescription(),
                    gathering.getMainType(),
                    gathering.getSubType(),
                    gathering.getImageUrl(),
                    gathering.getStartDate(),
                    gathering.getEndDate(),
                    gathering.getMainLocation(),
                    gathering.getSubLocation(),
                    gathering.getMinCount(),
                    gathering.getTotalCount(),
                    participantCount,
                    gathering.getStatus(),
                    gathering.getTagList(),
                    participants,
                    averageRating != null ? BigDecimal.valueOf(averageRating).setScale(1, RoundingMode.HALF_UP)
                        .doubleValue() : 0.0,
                    guestBookCount
                );
            })
            .toList();

        return new PageResponse<>(
            responses,
            participantGatherings.getNumber(),
            participantGatherings.getTotalElements(),
            participantGatherings.getTotalPages()
        );
    }

    public PageResponse<MemberCaptainGatheringResponse> getCaptainGatherings(String memberId, PageRequest pageable) {
        Member member = memberRepository.findById(Long.valueOf(memberId))
            .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        Page<GatheringParticipant> captainGatherings = gatheringParticipantRepository.findByMemberIdAndCaptainStatusTrue(
            member.getId(), pageable);

        List<MemberCaptainGatheringResponse> responses = captainGatherings.getContent().stream()
            .map(participant -> {
                Gathering gathering = participant.getGathering();

                Double averageRating = getAvgRating(gathering);
                Long reviewCount = getReviewCount(gathering);

                return new MemberCaptainGatheringResponse(
                    gathering.getId(),
                    participant.isCaptainStatus(),
                    true,
                    gathering.getTitle(),
                    gathering.getDescription(),
                    gathering.getMainType(),
                    gathering.getSubType(),
                    gathering.getImageUrl(),
                    gathering.getStartDate(),
                    gathering.getEndDate(),
                    gathering.getMainLocation(),
                    gathering.getSubLocation(),
                    gathering.getMinCount(),
                    gathering.getTotalCount(),
                    gatheringParticipantRepository.countByGatheringId(gathering.getId()),
                    gathering.getStatus(),
                    gathering.getTagList(),
                    getRecentParticipants(gathering),
                    averageRating,
                    reviewCount
                );
            })
            .toList();

        return new PageResponse<>(
            responses,
            captainGatherings.getNumber(),
            captainGatherings.getTotalElements(),
            captainGatherings.getTotalPages()
        );
    }

    private Long getParticipantCount(Gathering gathering) {
        return gatheringParticipantRepository.countByGatheringId(gathering.getId());
    }

    private List<ParticipantsResponse> getRecentParticipants(Gathering gathering) {
        return gatheringParticipantRepository.findByGatheringId(gathering.getId()).stream()
            .map(ParticipantsResponse::new)
            .limit(10)
            .toList();
    }

    private Double getAvgRating(Gathering gathering) {
        return reviewRepository.findAverageRatingByGatheringId(gathering.getId());
    }

    private Long getReviewCount(Gathering gathering) {
        return reviewRepository.countReviewsByGatheringId(gathering.getId());
    }
}
