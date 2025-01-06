package site.fitmon.challenge.repository;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import site.fitmon.challenge.domain.Challenge;
import site.fitmon.challenge.dto.response.PopularChallengeResponse;

@Repository
public interface ChallengeRepository extends JpaRepository<Challenge, Long> {

    @Query(value = "SELECT new site.fitmon.challenge.dto.response.PopularChallengeResponse(" +
        "c.gathering.id, c.id, c.title, c.description, c.imageUrl, " +
        "COUNT(DISTINCT cp.id), COUNT(DISTINCT ce.id)) " +
        "FROM Challenge c " +
        "LEFT JOIN ChallengeParticipant cp ON cp.challenge = c " +
        "LEFT JOIN ChallengeEvidence ce ON ce.challenge = c " +
        "WHERE c.deleted = false " +
        "AND c.startDate <= :now " +
        "AND c.endDate >= :now " +
        "GROUP BY c.id, c.gathering.id, c.title, c.description, c.imageUrl " +
        "ORDER BY COUNT(DISTINCT cp.id) DESC " +
        "LIMIT 8")
    List<PopularChallengeResponse> findPopularChallenges(LocalDateTime now);
}
