package site.fitmon.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import site.fitmon.auth.domain.CustomUserDetails;
import site.fitmon.member.dto.response.MemberResponse;

@Tag(name = "회원 API", description = "회원 API")
public interface MemberSwaggerController {

    @Operation(summary = "로그인 한 회원정보", description = "현재 로그인한 사용자의 아이디, 닉네임을 받아 옵니다.")
    ResponseEntity<MemberResponse> getMyInfo(
        @AuthenticationPrincipal CustomUserDetails userDetails);

}
