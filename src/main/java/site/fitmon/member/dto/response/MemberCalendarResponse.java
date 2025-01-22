package site.fitmon.member.dto.response;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import site.fitmon.gathering.domain.GatheringStatus;
import site.fitmon.gathering.domain.MainType;
import site.fitmon.gathering.domain.SubType;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MemberCalendarResponse {

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
    private GatheringStatus status;
}
