package site.fitmon.gathering.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import site.fitmon.gathering.domain.GatheringParticipant;

@Repository
public interface GatheringParticipantRepository extends JpaRepository<GatheringParticipant, Long> {

}
