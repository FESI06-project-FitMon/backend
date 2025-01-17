package site.fitmon.gathering.repository;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;
import site.fitmon.gathering.domain.Gathering;
import site.fitmon.gathering.dto.request.GatheringSearchCondition;
import site.fitmon.gathering.dto.response.GatheringDetailResponse;
import site.fitmon.gathering.dto.response.GatheringDetailStatusResponse;
import site.fitmon.gathering.dto.response.GatheringResponse;

@Repository
public interface GatheringRepositoryCustom {

    Slice<GatheringResponse> searchGatherings(GatheringSearchCondition condition, Pageable pageable);

    GatheringDetailResponse findGatheringDetail(Gathering gathering, Long email);

    GatheringDetailStatusResponse findGatheringDetailStatus(Gathering gathering);

    Slice<GatheringResponse> findLikedGatherings(List<Long> gatheringIds, GatheringSearchCondition condition,
        Pageable pageable);
}
