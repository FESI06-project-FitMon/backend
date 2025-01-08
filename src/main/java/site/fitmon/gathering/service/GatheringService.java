package site.fitmon.gathering.service;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.fitmon.challenge.domain.Challenge;
import site.fitmon.challenge.dto.request.ChallengeCreateRequest;
import site.fitmon.challenge.repository.ChallengeRepository;
import site.fitmon.common.dto.SliceResponse;
import site.fitmon.common.exception.ApiException;
import site.fitmon.common.exception.ErrorCode;
import site.fitmon.gathering.domain.Gathering;
import site.fitmon.gathering.domain.GatheringParticipant;
import site.fitmon.gathering.dto.request.GatheringCreateRequest;
import site.fitmon.gathering.dto.request.GatheringModifyRequest;
import site.fitmon.gathering.dto.request.GatheringSearchCondition;
import site.fitmon.gathering.dto.response.GatheringDetailResponse;
import site.fitmon.gathering.dto.response.GatheringDetailStatusResponse;
import site.fitmon.gathering.dto.response.GatheringResponse;
import site.fitmon.gathering.repository.GatheringParticipantRepository;
import site.fitmon.gathering.repository.GatheringRepository;
import site.fitmon.member.domain.Member;
import site.fitmon.member.repository.MemberRepository;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GatheringService {

    private final MemberRepository memberRepository;
    private final GatheringRepository gatheringRepository;
    private final GatheringParticipantRepository gatheringParticipantRepository;
    private final ChallengeRepository challengeRepository;
    private final GatheringStatusService gatheringStatusService;

    @Transactional
    public void createGathering(GatheringCreateRequest request, String username) {
        Member member = memberRepository.findByEmail(username)
            .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        validateDateRange(request.getStartDate(), request.getEndDate());

        Gathering gathering = Gathering.builder()
            .title(request.getTitle())
            .description(request.getDescription())
            .mainType(request.getMainType())
            .subType(request.getSubType())
            .imageUrl(request.getImageUrl())
            .startDate(request.getStartDate())
            .endDate(request.getEndDate())
            .mainLocation(request.getMainLocation())
            .subLocation(request.getSubLocation())
            .totalCount(request.getTotalCount())
            .minCount(request.getMinCount())
            .tags(request.getTags())
            .build();

        GatheringParticipant gatheringParticipant = GatheringParticipant.builder()
            .member(member)
            .gathering(gathering)
            .captainStatus(true)
            .build();

        Gathering savedGathering = gatheringRepository.save(gathering);
        gatheringParticipantRepository.save(gatheringParticipant);

        if (request.getChallenges() != null && !request.getChallenges().isEmpty()) {
            validateChallengesDateRange(request.getChallenges(), savedGathering);
            createChallenges(request.getChallenges(), savedGathering);
        }
    }

    @Transactional
    public void joinGathering(Long gathering, String email) {
        Member member = memberRepository.findByEmail(email)
            .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        Gathering foundGathering = gatheringRepository.findById(gathering)
            .orElseThrow(() -> new ApiException(ErrorCode.GATHERING_NOT_FOUND));

        if (gatheringParticipantRepository.existsByGatheringAndMember(foundGathering, member)) {
            throw new ApiException(ErrorCode.ALREADY_JOINED_GATHERING);
        }

        GatheringParticipant gatheringParticipant = GatheringParticipant.builder()
            .member(member)
            .gathering(foundGathering)
            .captainStatus(false)
            .build();

        gatheringParticipantRepository.save(gatheringParticipant);
    }

    @Transactional
    public SliceResponse<GatheringResponse> searchGatherings(GatheringSearchCondition condition, Pageable pageable) {
        gatheringStatusService.updateGatheringStatus();
        Slice<GatheringResponse> slice = gatheringRepository.searchGatherings(condition, pageable);

        return new SliceResponse<>(
            slice.getContent(),
            slice.hasNext()
        );
    }

    private void createChallenges(List<ChallengeCreateRequest> challengeRequests, Gathering gathering) {
        List<Challenge> challenges = challengeRequests.stream()
            .map(request -> Challenge.builder()
                .gathering(gathering)
                .title(request.getTitle())
                .description(request.getDescription())
                .imageUrl(request.getImageUrl())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .build())
            .toList();

        challengeRepository.saveAll(challenges);
    }

    private void validateChallengesDateRange(List<ChallengeCreateRequest> challenges, Gathering gathering) {
        LocalDateTime gatheringStartDate = gathering.getStartDate();
        LocalDateTime gatheringEndDate = gathering.getEndDate();

        for (ChallengeCreateRequest challenge : challenges) {
            LocalDateTime challengeStartDate = challenge.getStartDate();
            LocalDateTime challengeEndDate = challenge.getEndDate();

            if (challengeStartDate.isBefore(gatheringStartDate)) {
                throw new ApiException(ErrorCode.CHALLENGE_START_DATE_BEFORE_GATHERING);
            }

            if (challengeEndDate.isAfter(gatheringEndDate)) {
                throw new ApiException(ErrorCode.CHALLENGE_END_DATE_AFTER_GATHERING);
            }

            if (challengeEndDate.isBefore(challengeStartDate)) {
                throw new ApiException(ErrorCode.INVALID_CHALLENGE_DATE_RANGE);
            }
        }
    }

    private void validateDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        LocalDateTime now = LocalDateTime.now();

        if (startDate.isBefore(now)) {
            throw new ApiException(ErrorCode.INVALID_START_DATE);
        }

        if (endDate.isBefore(startDate)) {
            throw new ApiException(ErrorCode.INVALID_DATE_RANGE);
        }
    }

    public GatheringDetailResponse getGatheringDetail(Long gatheringId, String email) {
        Gathering gathering = gatheringRepository.findById(gatheringId)
            .orElseThrow(() -> new ApiException(ErrorCode.GATHERING_NOT_FOUND));
        return gatheringRepository.findGatheringDetail(gathering, email);
    }

    public GatheringDetailStatusResponse getGatheringDetailStatus(Long gatheringId) {
        Gathering gathering = gatheringRepository.findById(gatheringId)
            .orElseThrow(() -> new ApiException(ErrorCode.GATHERING_NOT_FOUND));
        return gatheringRepository.findGatheringDetailStatus(gathering);
    }

    @Transactional
    public void modifyGathering(@Valid GatheringModifyRequest request, Long gatheringId, String email) {
        Member member = memberRepository.findByEmail(email)
            .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        Gathering gathering = gatheringRepository.findById(gatheringId)
            .orElseThrow(() -> new ApiException(ErrorCode.GATHERING_NOT_FOUND));

        GatheringParticipant participant = gatheringParticipantRepository.findByGatheringAndMember(gathering,
                member)
            .orElseThrow(() -> new ApiException(ErrorCode.GATHERING_PARTICIPANT_NOT_FOUND));

        if (!participant.isCaptainStatus()) {
            throw new ApiException(ErrorCode.GATHERING_NOT_CAPTAIN);
        }

        gathering.update(request);
    }
}
