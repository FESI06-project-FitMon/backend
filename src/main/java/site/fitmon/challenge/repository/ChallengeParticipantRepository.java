package site.fitmon.challenge.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import site.fitmon.challenge.domain.Challenge;
import site.fitmon.challenge.domain.ChallengeParticipant;
import site.fitmon.gathering.domain.Gathering;
import site.fitmon.member.domain.Member;

@Repository
public interface ChallengeParticipantRepository extends JpaRepository<ChallengeParticipant, Long> {

    boolean existsByChallengeAndMember(Challenge challenge, Member member);

    @Modifying
    @Query("DELETE FROM ChallengeParticipant cp WHERE cp.challenge = :challenge")
    void deleteByChallenge(@Param("challenge") Challenge challenge);

    List<ChallengeParticipant> findByMemberAndChallenge_Gathering(Member member, Gathering gathering);
}
