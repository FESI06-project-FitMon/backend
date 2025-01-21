package site.fitmon.review.repository;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import site.fitmon.gathering.domain.Gathering;
import site.fitmon.gathering.domain.MainType;
import site.fitmon.gathering.domain.SubType;
import site.fitmon.member.domain.Member;
import site.fitmon.review.domain.Review;
import site.fitmon.review.dto.response.GuestbookResponse;
import site.fitmon.review.dto.response.ReviewStatisticsProjection;

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

    Page<Review> findAllByMemberId(Long memberId, Pageable pageable);

    @Query("SELECT r.rating AS rating, COUNT(r.id) AS count " +
        "FROM Review r " +
        "JOIN r.gathering g " +
        "WHERE (:mainType IS NULL OR g.mainType = :mainType) " +
        "AND (:subType IS NULL OR g.subType = :subType) " +
        "AND (:mainLocation IS NULL OR g.mainLocation = :mainLocation) " +
        "AND (:subLocation IS NULL OR g.subLocation = :subLocation) " +
        "AND (:searchDate IS NULL OR DATE(r.createdAt) = :searchDate) " +
        "GROUP BY r.rating")
    List<ReviewStatisticsProjection> findReviewStatistics(
        @Param("mainType") MainType mainType,
        @Param("subType") SubType subType,
        @Param("mainLocation") String mainLocation,
        @Param("subLocation") String subLocation,
        @Param("searchDate") LocalDate searchDate
    );

    @Query("SELECT new site.fitmon.review.dto.response.GuestbookResponse(" +
        "r.rating, " +
        "r.content, " +
        "g.mainType, " +
        "g.subType, " +
        "g.title, " +
        "g.mainLocation, " +
        "g.subLocation, " +
        "m.nickName, " +
        "r.createdAt, " +
        "g.startDate, " +
        "g.endDate, " +
        "g.status" +
        ") " +
        "FROM Review r " +
        "JOIN r.gathering g " +
        "JOIN r.member m " +
        "WHERE (:mainType IS NULL OR g.mainType = :mainType) " +
        "AND (:subType IS NULL OR g.subType = :subType) " +
        "AND (:mainLocation IS NULL OR g.mainLocation = :mainLocation) " +
        "AND (:subLocation IS NULL OR g.subLocation = :subLocation) " +
        "AND (:searchDate IS NULL OR DATE(r.createdAt) = :searchDate)")
    Page<GuestbookResponse> findGuestbookEntries(
        @Param("mainType") MainType mainType,
        @Param("subType") SubType subType,
        @Param("mainLocation") String mainLocation,
        @Param("subLocation") String subLocation,
        @Param("searchDate") LocalDate searchDate,
        Pageable pageable
    );
}
