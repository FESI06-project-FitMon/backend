package site.fitmon.gathering.repository;

import java.time.LocalDateTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import site.fitmon.gathering.domain.Gathering;

public interface GatheringRepository extends JpaRepository<Gathering, Long>, GatheringRepositoryCustom {

    @Modifying
    @Query("UPDATE Gathering g SET g.status = '진행중' " +
        "WHERE g.startDate <= :now AND g.endDate >= :now " +
        "AND g.status = '시작전'")
    void updateStatusToInProgress(@Param("now") LocalDateTime now);

    @Modifying
    @Query("UPDATE Gathering g SET g.status = '종료됨' " +
        "WHERE g.endDate < :now " +
        "AND g.status = '진행중'")
    void updateStatusToCompleted(@Param("now") LocalDateTime now);
}
