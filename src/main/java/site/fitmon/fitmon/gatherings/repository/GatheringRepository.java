package site.fitmon.fitmon.gatherings.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import site.fitmon.fitmon.gatherings.domain.Gatherings;

public interface GatheringRepository extends JpaRepository<Gatherings, Long> {

}
