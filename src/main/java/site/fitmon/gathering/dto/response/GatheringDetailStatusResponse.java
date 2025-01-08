package site.fitmon.gathering.dto.response;

import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import site.fitmon.gathering.domain.GatheringStatus;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GatheringDetailStatusResponse {

    private List<ParticipantsResponse> participants;
    private Integer minCount;
    private Integer totalCount;
    private Long participantCount;
    private GatheringStatus status;
    private Double averageRating;
    private Long guestBookCount;

    @Builder
    public GatheringDetailStatusResponse(List<ParticipantsResponse> participants, Integer minCount, Integer totalCount,
        Long participantCount, GatheringStatus status, Double averageRating, Long guestBookCount) {
        this.participants = participants;
        this.minCount = minCount;
        this.totalCount = totalCount;
        this.participantCount = participantCount;
        this.status = status;
        this.averageRating = averageRating;
        this.guestBookCount = guestBookCount;
    }
}
