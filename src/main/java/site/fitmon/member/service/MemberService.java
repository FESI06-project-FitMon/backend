package site.fitmon.member.service;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.fitmon.common.dto.PageResponse;
import site.fitmon.common.exception.ApiException;
import site.fitmon.common.exception.ErrorCode;
import site.fitmon.gathering.domain.Gathering;
import site.fitmon.gathering.domain.GatheringParticipant;
import site.fitmon.gathering.repository.GatheringParticipantRepository;
import site.fitmon.member.domain.Member;
import site.fitmon.member.dto.request.MemberUpdateRequest;
import site.fitmon.member.dto.response.MemberCalendarResponse;
import site.fitmon.member.dto.response.MemberResponse;
import site.fitmon.member.repository.MemberRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final GatheringParticipantRepository gatheringParticipantRepository;

    public MemberResponse getMemberInfo(String memberId) {
        Member member = memberRepository.findById(Long.valueOf(memberId))
            .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        return MemberResponse.builder()
            .memberId(member.getId())
            .nickName(member.getNickName())
            .email(member.getEmail())
            .profileImageUrl(member.getProfileImageUrl())
            .build();
    }

    @Transactional
    public void updateMember(@Valid MemberUpdateRequest request, String memberId) {
        Member member = memberRepository.findById(Long.valueOf(memberId))
            .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));
        member.updateMemberProfile(request.getNickName(), request.getProfileImageUrl());
    }

    public PageResponse<MemberCalendarResponse> getCalendarGatherings(String memberId, PageRequest pageable) {
        Member member = memberRepository.findById(Long.valueOf(memberId))
            .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        Page<GatheringParticipant> participantGatherings = gatheringParticipantRepository.findByMemberId(member.getId(),
            pageable);

        List<MemberCalendarResponse> response = participantGatherings.getContent()
            .stream()
            .map(participant -> {
                Gathering gathering = participant.getGathering();
                return new MemberCalendarResponse(
                    gathering.getId(),
                    participant.isCaptainStatus(),
                    true,
                    gathering.getTitle(),
                    gathering.getDescription(),
                    gathering.getMainType(),
                    gathering.getSubType(),
                    gathering.getImageUrl(),
                    gathering.getStartDate(),
                    gathering.getEndDate(),
                    gathering.getStatus()
                );
            })
            .toList();

        return new PageResponse<>(
            response,
            participantGatherings.getNumber(),
            participantGatherings.getTotalElements(),
            participantGatherings.getTotalPages()
        );
    }
}
