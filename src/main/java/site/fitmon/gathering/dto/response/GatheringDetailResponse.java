package site.fitmon.gathering.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import site.fitmon.gathering.domain.GatheringStatus;
import site.fitmon.gathering.domain.MainType;
import site.fitmon.gathering.domain.SubType;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GatheringDetailResponse {

    private Long gatheringId;
    private boolean captainStatus;
    private String title;
    private String description;
    private MainType mainType;
    private SubType subType;
    private String imageUrl;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String mainLocation;
    private String subLocation;
    private Integer minCount;
    private Integer totalCount;
    private Long participantCount;
    private GatheringStatus status;
    private List<String> tags;
    private List<ParticipantsResponse> participants;
    private Double averageRating;
    private Long guestBookCount;

    @Builder
    public GatheringDetailResponse(Long gatheringId, boolean captainStatus, String title, String description,
        MainType mainType,
        SubType subType,
        String imageUrl, LocalDateTime startDate, LocalDateTime endDate, String mainLocation, String subLocation,
        Integer minCount, Integer totalCount, Long participantCount, GatheringStatus status, List<String> tags,
        List<ParticipantsResponse> participants, Double averageRating, Long guestBookCount) {
        this.gatheringId = gatheringId;
        this.captainStatus = captainStatus;
        this.title = title;
        this.description = description;
        this.mainType = mainType;
        this.subType = subType;
        this.imageUrl = imageUrl;
        this.startDate = startDate;
        this.endDate = endDate;
        this.mainLocation = mainLocation;
        this.subLocation = subLocation;
        this.minCount = minCount;
        this.totalCount = totalCount;
        this.participantCount = participantCount;
        this.status = status;
        this.tags = tags;
        this.participants = participants;
        this.averageRating = averageRating;
        this.guestBookCount = guestBookCount;
    }
}
