package site.fitmon.challenge;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.fitmon.challenge.dto.ChallengeCreateRequest;
import site.fitmon.challenge.repository.ChallengeRepository;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ChallengeService {

    private final ChallengeRepository challengeRepository;

    @Transactional
    public void createChallenge(ChallengeCreateRequest request, Long gatheringId, String email) {
        // create challenge
    }
}
