package site.fitmon.member.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import site.fitmon.gathering.domain.GatheringStatus;
import site.fitmon.gathering.domain.MainType;
import site.fitmon.gathering.domain.SubType;
import site.fitmon.gathering.dto.response.ParticipantsResponse;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MemberCaptainGatheringResponse {

    private Long gatheringId;
    private boolean captainStatus;
    private boolean participantStatus;
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
}
