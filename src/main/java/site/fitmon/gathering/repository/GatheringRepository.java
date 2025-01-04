package site.fitmon.gathering.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import site.fitmon.gathering.domain.Gathering;

public interface GatheringRepository extends JpaRepository<Gathering, Long> {

}
