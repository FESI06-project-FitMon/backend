package site.fitmon.gathering.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;
import site.fitmon.gathering.dto.request.GatheringSearchCondition;
import site.fitmon.gathering.dto.response.GatheringResponse;

@Repository
public interface GatheringRepositoryCustom {

    Slice<GatheringResponse> searchGatherings(GatheringSearchCondition condition, Pageable pageable);
}
