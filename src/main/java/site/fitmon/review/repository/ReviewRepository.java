package site.fitmon.review.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import site.fitmon.gathering.domain.Gathering;
import site.fitmon.member.domain.Member;
import site.fitmon.review.domain.Review;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    boolean existsByGatheringAndMember(Gathering gathering, Member member);

    @Query("SELECT r FROM Review r " +
        "JOIN FETCH r.member " +
        "WHERE r.gathering.id = :gatheringId")
    Slice<Review> findAllWithMemberByGatheringId(
        @Param("gatheringId") Long gatheringId,
        Pageable pageable
    );
}
