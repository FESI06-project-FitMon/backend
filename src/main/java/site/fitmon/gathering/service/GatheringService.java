package site.fitmon.gathering.service;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.fitmon.challenge.domain.Challenge;
import site.fitmon.challenge.domain.ChallengeEvidence;
import site.fitmon.challenge.domain.ChallengeParticipant;
import site.fitmon.challenge.dto.request.ChallengeCreateRequest;
import site.fitmon.challenge.repository.ChallengeEvidenceRepository;
import site.fitmon.challenge.repository.ChallengeParticipantRepository;
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
    private final ChallengeParticipantRepository challengeParticipantRepository;
    private final ChallengeEvidenceRepository challengeEvidenceRepository;
    private final GatheringStatusService gatheringStatusService;

    @Transactional
    public void createGathering(GatheringCreateRequest request, String memberId) {
        Member member = validateMember(memberId);

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
    public void joinGathering(Long gathering, String memberId) {
        Member member = validateMember(memberId);

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
        LocalDate gatheringStartDate = gathering.getStartDate().toLocalDate();
        LocalDate gatheringEndDate = gathering.getEndDate().toLocalDate();

        for (ChallengeCreateRequest challenge : challenges) {
            LocalDate challengeStartDate = challenge.getStartDate().toLocalDate();
            LocalDate challengeEndDate = challenge.getEndDate().toLocalDate();

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
        LocalDate today = LocalDate.now();

        if (startDate.toLocalDate().isBefore(today)) {
            throw new ApiException(ErrorCode.INVALID_START_DATE);
        }

        if (endDate.toLocalDate().isBefore(startDate.toLocalDate())) {
            throw new ApiException(ErrorCode.INVALID_DATE_RANGE);
        }
    }

    public GatheringDetailResponse getGatheringDetail(Long gatheringId, String memberId) {
        Gathering gathering = gatheringRepository.findById(gatheringId)
            .orElseThrow(() -> new ApiException(ErrorCode.GATHERING_NOT_FOUND));
        Long memberIdLong = (memberId != null) ? Long.valueOf(memberId) : null;
        return gatheringRepository.findGatheringDetail(gathering, memberIdLong);
    }

    public GatheringDetailStatusResponse getGatheringDetailStatus(Long gatheringId) {
        Gathering gathering = gatheringRepository.findById(gatheringId)
            .orElseThrow(() -> new ApiException(ErrorCode.GATHERING_NOT_FOUND));
        return gatheringRepository.findGatheringDetailStatus(gathering);
    }

    @Transactional
    public void modifyGathering(@Valid GatheringModifyRequest request, Long gatheringId, String memberId) {
        Member member = validateMember(memberId);

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

    @Transactional
    public void deleteGathering(Long gatheringId, String memberId) {
        Member member = validateMember(memberId);

        Gathering gathering = gatheringRepository.findById(gatheringId)
            .orElseThrow(() -> new ApiException(ErrorCode.GATHERING_NOT_FOUND));

        GatheringParticipant participant = gatheringParticipantRepository.findByGatheringAndMember(gathering,
                member)
            .orElseThrow(() -> new ApiException(ErrorCode.GATHERING_PARTICIPANT_NOT_FOUND));

        if (!participant.isCaptainStatus()) {
            throw new ApiException(ErrorCode.GATHERING_NOT_CAPTAIN);
        }

        gathering.cancel();
    }

    @Transactional(readOnly = true)
    public SliceResponse<GatheringResponse> getLikedGatherings(List<Long> gatheringIds,
        GatheringSearchCondition condition, Pageable pageable) {
        Slice<GatheringResponse> slice = gatheringRepository.findLikedGatherings(gatheringIds, condition, pageable);

        return new SliceResponse<>(
            slice.getContent(),
            slice.hasNext()
        );
    }

    private Member validateMember(String id) {
        return Optional.ofNullable(id)
            .map(Long::valueOf)
            .flatMap(memberRepository::findById)
            .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));
    }

    @Transactional
    public void cancelGathering(String memberId, Long gatheringId) {
        Member member = validateMember(memberId);
        Gathering gathering = gatheringRepository.findById(gatheringId)
            .orElseThrow(() -> new ApiException(ErrorCode.GATHERING_NOT_FOUND));
        GatheringParticipant participant = gatheringParticipantRepository.findByGatheringAndMember(gathering,
                member)
            .orElseThrow(() -> new ApiException(ErrorCode.GATHERING_PARTICIPANT_NOT_FOUND));
        if (participant.isCaptainStatus()) {
            throw new ApiException(ErrorCode.GATHERING_CAPTAIN);
        }
        gatheringParticipantRepository.delete(participant);

        List<ChallengeParticipant> challengeParticipants = challengeParticipantRepository.findByMemberAndChallenge_Gathering(
            member, gathering);
        if (!challengeParticipants.isEmpty()) {
            challengeParticipantRepository.deleteAll(challengeParticipants);
        }

        List<ChallengeEvidence> challengeEvidences = challengeEvidenceRepository.findByMemberAndChallenge_Gathering(
            member, gathering);
        if (!challengeEvidences.isEmpty()) {
            challengeEvidenceRepository.deleteAll(challengeEvidences);
        }
    }
}
