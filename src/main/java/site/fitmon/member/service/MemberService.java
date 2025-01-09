package site.fitmon.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.fitmon.common.exception.ApiException;
import site.fitmon.common.exception.ErrorCode;
import site.fitmon.member.domain.Member;
import site.fitmon.member.dto.response.MemberResponse;
import site.fitmon.member.repository.MemberRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;

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

}
