package site.fitmon.gathering.dto.response;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import site.fitmon.gathering.domain.GatheringStatus;
import site.fitmon.gathering.domain.MainType;
import site.fitmon.gathering.domain.SubType;

@Getter
public class GatheringResponse {
    private Long gatheringId;
    private String title;
    private String description;
    private MainType mainType;
    private SubType subType;
    private String imageUrl;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String mainLocation;
    private String subLocation;
    private Integer totalCount;
    private Long currentParticipants;
    private GatheringStatus status;
    private List<String> tags;

    @Builder
    public GatheringResponse(Long gatheringId, String title, String description, MainType mainType,
            SubType subType, String imageUrl, LocalDateTime startDate, LocalDateTime endDate,
            String mainLocation, String subLocation, Integer totalCount, Long currentParticipants,
            GatheringStatus status, String tags) {
        this.gatheringId = gatheringId;
        this.title = title;
        this.description = description;
        this.mainType = mainType;
        this.subType = subType;
        this.imageUrl = imageUrl;
        this.startDate = startDate;
        this.endDate = endDate;
        this.mainLocation = mainLocation;
        this.subLocation = subLocation;
        this.totalCount = totalCount;
        this.currentParticipants = currentParticipants;
        this.status = status;
        this.tags = (tags != null ? Arrays.asList(tags.split(",")) : new ArrayList<>());
    }
}
