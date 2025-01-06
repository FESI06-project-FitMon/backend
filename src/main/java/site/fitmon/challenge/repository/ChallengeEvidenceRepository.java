package site.fitmon.challenge.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import site.fitmon.challenge.domain.ChallengeEvidence;

@Repository
public interface ChallengeEvidenceRepository extends JpaRepository<ChallengeEvidence, Long> {

}
