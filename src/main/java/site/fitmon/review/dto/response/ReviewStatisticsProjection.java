package site.fitmon.review.dto.response;

public interface ReviewStatisticsProjection {
    Integer getRating(); // 별점
    Long getCount(); // 해당 별점에 대한 사용자 수
}
