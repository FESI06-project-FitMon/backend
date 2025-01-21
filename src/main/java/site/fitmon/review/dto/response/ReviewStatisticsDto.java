package site.fitmon.review.dto.response;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ReviewStatisticsDto {

    private double averageRating;
    private Map<String, Long> ratingCounts;
}
