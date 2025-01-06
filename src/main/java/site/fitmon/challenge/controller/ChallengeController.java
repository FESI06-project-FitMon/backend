package site.fitmon.challenge.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import site.fitmon.challenge.dto.ChallengeCreateRequest;
import site.fitmon.challenge.service.ChallengeService;
import site.fitmon.common.dto.ApiResponse;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class ChallengeController implements ChallengeSwaggerController {

    private final ChallengeService challengeService;

    @PostMapping("/gatherings/{gatherId}/challenges")
    public ResponseEntity<ApiResponse> createChallenge(
        @Valid @RequestBody ChallengeCreateRequest request,
        @PathVariable Long gatherId,
        @AuthenticationPrincipal UserDetails userDetails) {

        challengeService.createChallenge(request, gatherId, userDetails.getUsername());

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.of("챌린지 생성 성공"));
    }

    @PostMapping("/challenges/{challengeId}/verification")
    public ResponseEntity<ApiResponse> verficateChallenge(
        @PathVariable Long challengeId,
        @AuthenticationPrincipal UserDetails userDetails) {

        challengeService.verifyChallenge(challengeId, userDetails.getUsername());

        return ResponseEntity.ok(ApiResponse.of("챌린지 인증 성공"));
    }
}
