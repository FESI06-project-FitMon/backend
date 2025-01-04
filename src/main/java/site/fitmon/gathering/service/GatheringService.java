package site.fitmon.gathering.service;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.fitmon.common.exception.ApiException;
import site.fitmon.common.exception.ErrorCode;
import site.fitmon.gathering.domain.Gathering;
import site.fitmon.gathering.dto.request.GatheringCreateRequest;
import site.fitmon.gathering.repository.GatheringRepository;
import site.fitmon.member.domain.Member;
import site.fitmon.member.repository.MemberRepository;

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

        Gathering gathering = Gathering.builder()
            .title(request.getTitle())
            .description(request.getDescription())
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
