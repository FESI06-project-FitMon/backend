package site.fitmon.gathering.repository;

import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import site.fitmon.gathering.dto.request.GatheringSearchCondition;
import site.fitmon.gathering.dto.response.GatheringDetailResponse;
import site.fitmon.gathering.dto.response.GatheringResponse;

@Repository
public interface GatheringRepositoryCustom {

    Slice<GatheringResponse> searchGatherings(GatheringSearchCondition condition, Pageable pageable);

    Optional<GatheringDetailResponse> findGatheringDetail(@Param("gatheringId") Long gatheringId);
}
