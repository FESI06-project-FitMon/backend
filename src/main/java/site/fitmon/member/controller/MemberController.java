package site.fitmon.member.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import site.fitmon.auth.domain.CustomUserDetails;
import site.fitmon.common.dto.ApiResponse;
import site.fitmon.common.dto.PageResponse;
import site.fitmon.member.dto.request.MemberUpdateRequest;
import site.fitmon.member.dto.response.MemberCalendarResponse;
import site.fitmon.member.dto.response.MemberCaptainGatheringResponse;
import site.fitmon.member.dto.response.MemberChallengeResponse;
import site.fitmon.member.dto.response.MemberParticipantsResponse;
import site.fitmon.member.dto.response.MemberResponse;
import site.fitmon.member.dto.response.OwnedGatheringChallengeResponse;
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

    @GetMapping("/my-page/calendar")
    public ResponseEntity<PageResponse<MemberCalendarResponse>> getCalendarGatherings(
        @AuthenticationPrincipal CustomUserDetails userDetails,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int pageSize
    ) {
        PageRequest pageable = PageRequest.of(page, pageSize, Sort.by(Direction.DESC, "createdAt"));
        return ResponseEntity.ok(memberService.getCalendarGatherings(userDetails.getUsername(), pageable));
    }

    @GetMapping("/my-page/gatherings/participants")
    public ResponseEntity<PageResponse<MemberParticipantsResponse>> getParticipantsGathergins(
        @AuthenticationPrincipal CustomUserDetails userDetails,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int pageSize
    ) {
        PageRequest pageable = PageRequest.of(page, pageSize, Sort.by(Direction.DESC, "createdAt"));
        return ResponseEntity.ok(memberService.getParticipantsGatherings(userDetails.getUsername(), pageable));
    }

    @GetMapping("/my-page/gatherings/captain")
    public ResponseEntity<PageResponse<MemberCaptainGatheringResponse>> getCaptainGathergins(
        @AuthenticationPrincipal CustomUserDetails userDetails,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int pageSize
    ) {
        PageRequest pageable = PageRequest.of(page, pageSize, Sort.by(Direction.DESC, "createdAt"));
        return ResponseEntity.ok(memberService.getCaptainGatherings(userDetails.getUsername(), pageable));
    }

    @GetMapping("/my-page/challenges")
    public ResponseEntity<PageResponse<MemberChallengeResponse>> getMyChallenges(
        @AuthenticationPrincipal CustomUserDetails userDetails,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int pageSize
    ) {
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by(Sort.Direction.DESC, "startDate"));

        return ResponseEntity.ok(
            memberService.getMemberChallenges(userDetails.getUsername(), pageable)
        );
    }

    @GetMapping("/my-page/owned-gatherings/challenges")
    public ResponseEntity<PageResponse<OwnedGatheringChallengeResponse>> getOwnedGatheringChallenges(
        @AuthenticationPrincipal CustomUserDetails userDetails,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int pageSize
    ) {
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by(Sort.Direction.DESC, "startDate"));
        PageResponse<OwnedGatheringChallengeResponse> response = memberService.getOwnedGatheringChallenges(
            userDetails.getUsername(),
            pageable
        );
        return ResponseEntity.ok(response);
    }
}
