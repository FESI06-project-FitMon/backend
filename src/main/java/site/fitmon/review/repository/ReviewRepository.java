package site.fitmon.review.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import site.fitmon.gathering.domain.Gathering;
import site.fitmon.member.domain.Member;
import site.fitmon.review.domain.Review;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    boolean existsByGatheringAndMember(Gathering gathering, Member member);
}
