package site.fitmon.challenge.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import site.fitmon.challenge.domain.ChallengeEvidence;
import site.fitmon.gathering.domain.Gathering;
import site.fitmon.member.domain.Member;

@Repository
public interface ChallengeEvidenceRepository extends JpaRepository<ChallengeEvidence, Long> {

    @Query("SELECT CASE WHEN COUNT(ce) > 0 THEN true ELSE false END FROM ChallengeEvidence ce " +
        "WHERE ce.member = :member AND ce.challenge.gathering = :gathering")
    boolean hasEvidenceInGathering(@Param("member") Member member, @Param("gathering") Gathering gathering);
}
