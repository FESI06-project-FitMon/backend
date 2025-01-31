package site.fitmon.challenge.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;
import site.fitmon.challenge.dto.request.ChallengeSearchCondition;
import site.fitmon.challenge.dto.response.GatheringChallengesResponse;
import site.fitmon.member.dto.response.MemberChallengeResponse;
import site.fitmon.member.dto.response.OwnedGatheringChallengeResponse;

@Repository
public interface ChallengeRepositoryCustom {

    Slice<GatheringChallengesResponse> getGatheringChallenges(Long gatheringId,
        Long memberId,
        ChallengeSearchCondition condition,
        Pageable pageable);

    Page<MemberChallengeResponse> getMemberChallenges(Long memberId, Pageable pageable);

    Page<OwnedGatheringChallengeResponse> getOwnedGatheringChallenges(Long memberId, Pageable pageable);
}
