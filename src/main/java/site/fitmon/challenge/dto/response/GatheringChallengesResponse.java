package site.fitmon.challenge.dto.response;

import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GatheringChallengesResponse {

    private Long gatheringId;
    private Long challengeId;
    private String title;
    private String description;
    private String imageUrl;
    private long participantCount;
    private long successParticipantCount;
    private boolean participantStatus;
    private boolean verificationStatus;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
