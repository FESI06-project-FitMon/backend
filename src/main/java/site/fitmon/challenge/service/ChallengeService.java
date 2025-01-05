package site.fitmon.challenge.service;

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

    @Transactional
    public void createChallenge(ChallengeCreateRequest request, Long gatheringId, String email) {
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

        Challenge challenge = Challenge.builder()
            .title(request.getTitle())
            .description(request.getDescription())
            .imageUrl(request.getImageUrl())
            .gathering(gathering)
            .startDate(request.getStartDate())
            .endDate(request.getEndDate())
            .build();

        challengeRepository.save(challenge);
    }
}
