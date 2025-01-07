package site.fitmon.challenge.dto.response;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PopularChallengeResponse {

    private Long gatheringId;
    private Long challengeId;
    private String title;
    private String description;
    private String imageUrl;
    private long participantCount;
    private long successParticipantCount;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
