package site.fitmon.member.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import site.fitmon.auth.domain.CustomUserDetails;
import site.fitmon.common.dto.ApiResponse;
import site.fitmon.member.dto.request.MemberUpdateRequest;
import site.fitmon.member.dto.response.MemberResponse;
import site.fitmon.member.service.MemberService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class MemberController implements MemberSwaggerController {

    private final MemberService memberService;

    @GetMapping("members/me")
    public ResponseEntity<MemberResponse> getMyInfo(
        @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(memberService.getMemberInfo(userDetails.getUsername()));
    }

    @PatchMapping("/my-page/profile")
    public ResponseEntity<ApiResponse> updateMemberInfo(
        @Valid @RequestBody MemberUpdateRequest request,
        @AuthenticationPrincipal CustomUserDetails userDetails
        ) {
        memberService.updateMember(request, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.of("프로필 변경 성공"));
    }
}
