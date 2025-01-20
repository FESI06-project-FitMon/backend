package site.fitmon.challenge.service;

import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.fitmon.challenge.domain.Challenge;
import site.fitmon.challenge.domain.ChallengeEvidence;
import site.fitmon.challenge.domain.ChallengeParticipant;
import site.fitmon.challenge.dto.request.ChallengeCreateRequest;
import site.fitmon.challenge.dto.request.ChallengeEvidenceRequest;
import site.fitmon.challenge.dto.request.ChallengeSearchCondition;
import site.fitmon.challenge.dto.response.GatheringChallengesResponse;
import site.fitmon.challenge.dto.response.PopularChallengeResponse;
import site.fitmon.challenge.repository.ChallengeEvidenceRepository;
import site.fitmon.challenge.repository.ChallengeParticipantRepository;
import site.fitmon.challenge.repository.ChallengeRepository;
import site.fitmon.common.dto.SliceResponse;
import site.fitmon.common.exception.ApiException;
import site.fitmon.common.exception.ErrorCode;
import site.fitmon.gathering.domain.Gathering;
import site.fitmon.gathering.domain.GatheringParticipant;
import site.fitmon.gathering.repository.GatheringParticipantRepository;
import site.fitmon.gathering.repository.GatheringRepository;
import site.fitmon.member.domain.Member;
import site.fitmon.member.repository.MemberRepository;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ChallengeService {

    private final MemberRepository memberRepository;
    private final GatheringRepository gatheringRepository;
    private final GatheringParticipantRepository gatheringParticipantRepository;
    private final ChallengeRepository challengeRepository;
    private final ChallengeEvidenceRepository challengeEvidenceRepository;
    private final ChallengeParticipantRepository challengeParticipantRepository;

    @Transactional
    public void createChallenge(ChallengeCreateRequest request, Long gatheringId, String memberId) {
        Member member = memberRepository.findById(Long.valueOf(memberId))
            .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        Gathering gathering = gatheringRepository.findById(gatheringId)
            .orElseThrow(() -> new ApiException(ErrorCode.GATHERING_NOT_FOUND));

        GatheringParticipant participant = gatheringParticipantRepository.findByGatheringAndMember(gathering,
                member)
            .orElseThrow(() -> new ApiException(ErrorCode.GATHERING_PARTICIPANT_NOT_FOUND));

        if (!participant.isCaptainStatus()) {
            throw new ApiException(ErrorCode.GATHERING_NOT_CAPTAIN);
        }

        Challenge challenge = Challenge.builder()
            .title(request.getTitle())
            .description(request.getDescription())
            .imageUrl(request.getImageUrl())
            .gathering(gathering)
            .totalCount(request.getTotalCount())
            .startDate(request.getStartDate())
            .endDate(request.getEndDate())
            .build();

        challengeRepository.save(challenge);
    }

    @Transactional
    public void verifyChallenge(ChallengeEvidenceRequest request, Long challengeId, String memberId) {
        Member member = memberRepository.findById(Long.valueOf(memberId))
            .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        Challenge challenge = challengeRepository.findById(challengeId)
            .orElseThrow(() -> new ApiException(ErrorCode.CHALLENGE_NOT_FOUND));

        if (!challengeParticipantRepository.existsByChallengeAndMember(challenge, member)) {
            throw new ApiException(ErrorCode.CHALLENGE_PARTICIPANT_NOT_FOUND);
        }

        ChallengeEvidence evidence = ChallengeEvidence.builder()
            .challenge(challenge)
            .member(member)
            .imageUrl(request.getImageUrl())
            .build();

        challengeEvidenceRepository.save(evidence);
    }

    @Transactional
    public void joinChallenge(Long challengeId, String memberId) {
        Member member = memberRepository.findById(Long.valueOf(memberId))
            .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        Challenge challenge = challengeRepository.findById(challengeId)
            .orElseThrow(() -> new ApiException(ErrorCode.CHALLENGE_NOT_FOUND));

        Gathering gathering = challenge.getGathering();

        gatheringParticipantRepository
            .findByGatheringAndMember(gathering, member)
            .orElseThrow(() -> new ApiException(ErrorCode.GATHERING_PARTICIPANT_NOT_FOUND));

        if (challengeParticipantRepository.existsByChallengeAndMember(challenge, member)) {
            throw new ApiException(ErrorCode.ALREADY_JOINED_CHALLENGE);
        }

        ChallengeParticipant challengeParticipant = ChallengeParticipant.builder()
            .challenge(challenge)
            .member(member)
            .build();

        challengeParticipantRepository.save(challengeParticipant);
    }

    public List<PopularChallengeResponse> getPopularChallenges() {
        return challengeRepository.findPopularChallenges(LocalDateTime.now());
    }

    public SliceResponse<GatheringChallengesResponse> getGatheringChallenges(
        ChallengeSearchCondition condition,
        Long gatheringId,
        String memberId,
        Pageable pageable
    ) {
        Slice<GatheringChallengesResponse> slice = challengeRepository.getGatheringChallenges(gatheringId,
            Long.valueOf(memberId), condition, pageable);
        return new SliceResponse<>(
            slice.getContent(),
            slice.hasNext()
        );
    }
}
