package site.fitmon.review.dto.response;

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
public class GuestbookResponse {

    private int guestbookScore;
    private String guestBookContent;
    private MainType mainType;
    private SubType subType;
    private String gatheringTitle;
    private String mainLocation;
    private String subLocation;
    private String nickName;
    private LocalDateTime guestbookCreatedDate;
    private LocalDateTime gatheringStartDate;
    private LocalDateTime gatheringEndDate;
    private GatheringStatus gatheringStatus;
}
