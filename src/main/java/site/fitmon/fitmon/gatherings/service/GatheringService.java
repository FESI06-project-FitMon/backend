package site.fitmon.fitmon.gatherings.service;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.fitmon.fitmon.common.exception.ApiException;
import site.fitmon.fitmon.common.exception.ErrorCode;
import site.fitmon.fitmon.gatherings.domain.Gatherings;
import site.fitmon.fitmon.gatherings.dto.request.GatheringCreateRequest;
import site.fitmon.fitmon.gatherings.repository.GatheringRepository;
import site.fitmon.fitmon.member.domain.Member;
import site.fitmon.fitmon.member.repository.MemberRepository;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GatheringService {

    private final GatheringRepository gatheringRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public void createGathering(GatheringCreateRequest request, String username) {
        Member member = memberRepository.findByEmail(username)
            .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        validateDateRange(request.getStartDate(), request.getEndDate());

        Gatherings gathering = Gatherings.builder()
            .title(request.getTitle())
            .captain(member)
            .mainType(request.getMainType())
            .subType(request.getSubType())
            .imageUrl(request.getImageUrl())
            .startDate(request.getStartDate())
            .endDate(request.getEndDate())
            .mainLocation(request.getMainLocation())
            .subLocation(request.getSubLocation())
            .totalCount(request.getTotalCount())
            .minCount(request.getMinCount())
            .participantCount(1)
            .tags(request.getTags())
            .build();

        gatheringRepository.save(gathering);
    }

    private void validateDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        LocalDateTime now = LocalDateTime.now();

        if (startDate.isBefore(now)) {
            throw new ApiException(ErrorCode.INVALID_START_DATE);
        }

        if (endDate.isBefore(startDate)) {
            throw new ApiException(ErrorCode.INVALID_DATE_RANGE);
        }
    }
}
