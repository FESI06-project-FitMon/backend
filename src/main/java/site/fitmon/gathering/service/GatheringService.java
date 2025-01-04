package site.fitmon.gathering.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.fitmon.challenge.domain.Challenge;
import site.fitmon.challenge.dto.ChallengeCreateRequest;
import site.fitmon.challenge.repository.ChallengeRepository;
import site.fitmon.common.exception.ApiException;
import site.fitmon.common.exception.ErrorCode;
import site.fitmon.gathering.domain.Gathering;
import site.fitmon.gathering.domain.GatheringParticipant;
import site.fitmon.gathering.dto.request.GatheringCreateRequest;
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
            .isCaptain(true)
            .build();

        Gathering savedGathering = gatheringRepository.save(gathering);
        gatheringParticipantRepository.save(gatheringParticipant);

        if (request.getChallenges() != null && !request.getChallenges().isEmpty()) {
            validateChallengesDateRange(request.getChallenges(), savedGathering);
            createChallenges(request.getChallenges(), savedGathering);
        }
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
}
