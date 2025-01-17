package site.fitmon.gathering.dto.request;

import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GatheringLikesRequest {

    private List<Long> gatheringIds;
}
