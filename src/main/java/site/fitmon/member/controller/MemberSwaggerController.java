package site.fitmon.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import site.fitmon.auth.domain.CustomUserDetails;
import site.fitmon.common.dto.ApiResponse;
import site.fitmon.common.dto.PageResponse;
import site.fitmon.member.dto.request.MemberUpdateRequest;
import site.fitmon.member.dto.response.MemberCalendarResponse;
import site.fitmon.member.dto.response.MemberResponse;

@Tag(name = "회원 API", description = "회원 API")
public interface MemberSwaggerController {

    @Operation(summary = "로그인 한 회원정보", description = "현재 로그인한 사용자의 아이디, 닉네임을 받아 옵니다.")
    ResponseEntity<MemberResponse> getMyInfo(
        @AuthenticationPrincipal CustomUserDetails userDetails);

    @Operation(summary = "회원정보 수정", description = "현재 로그인한 사용자의 닉네임, 프로필을 수정합니다.")
    ResponseEntity<ApiResponse> updateMemberInfo(
        @Valid @RequestBody MemberUpdateRequest request,
        @AuthenticationPrincipal CustomUserDetails userDetails);

    @Operation(summary = "캘린더 조회", description = "내가 참여한 / 모임장 인 모임을 모두 확인합니다.")
    ResponseEntity<PageResponse<MemberCalendarResponse>> getCalendarGatherings(
        @AuthenticationPrincipal CustomUserDetails userDetails,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int pageSize
    );
}
