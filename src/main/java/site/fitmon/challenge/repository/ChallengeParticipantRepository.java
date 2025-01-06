package site.fitmon.challenge.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import site.fitmon.challenge.domain.Challenge;
import site.fitmon.challenge.domain.ChallengeParticipant;
import site.fitmon.member.domain.Member;

@Repository
public interface ChallengeParticipantRepository extends JpaRepository<ChallengeParticipant, Long> {

    boolean existsByChallengeAndMember(Challenge challenge, Member member);
}
