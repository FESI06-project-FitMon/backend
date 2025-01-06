package site.fitmon.challenge.dto.response;

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
    private long totalParticipants;
    private long successParticipants;
}
