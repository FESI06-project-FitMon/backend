package site.fitmon.gathering.repository;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import site.fitmon.gathering.domain.Gathering;
import site.fitmon.gathering.domain.GatheringParticipant;
import site.fitmon.member.domain.Member;

@Repository
public interface GatheringParticipantRepository extends JpaRepository<GatheringParticipant, Long> {

    Optional<GatheringParticipant> findByGatheringAndMember(Gathering gathering, Member member);

    boolean existsByGatheringAndMember(Gathering foundGathering, Member member);

    Optional<GatheringParticipant> findByGatheringIdAndCaptainStatus(Long gatheringId, boolean captainStatus);

    @Query("SELECT gp FROM GatheringParticipant gp WHERE gp.member.id = :memberId")
    Page<GatheringParticipant> findByMemberId(@Param("memberId") Long memberId, Pageable pageable);
}
