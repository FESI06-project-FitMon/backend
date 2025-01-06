package site.fitmon.challenge.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import site.fitmon.challenge.dto.request.ChallengeCreateRequest;
import site.fitmon.challenge.dto.request.ChallengeEvidenceRequest;
import site.fitmon.challenge.dto.response.PopularChallengeResponse;
import site.fitmon.common.dto.ApiResponse;

@Tag(name = "챌린지 API", description = "챌린지 API")
public interface ChallengeSwaggerController {

    @Operation(summary = "챌린지 생성")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "201",
            description = "챌린지 생성 성공",
            content = {@Content()}
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "잘못된 입력 값",
            content = {@Content()}
        )})
    ResponseEntity<ApiResponse> createChallenge(
        @Valid @RequestBody ChallengeCreateRequest request,
        @PathVariable Long gatheringId,
        @AuthenticationPrincipal UserDetails userDetails);


    @Operation(summary = "챌린지 인증")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "201",
            description = "챌린지 인증 성공",
            content = {@Content()}
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "잘못된 입력 값",
            content = {@Content()}
        )})
    ResponseEntity<ApiResponse> verifyChallenge(
        @Valid @RequestBody ChallengeEvidenceRequest request,
        @PathVariable Long challengeId,
        @AuthenticationPrincipal UserDetails userDetails);

    @Operation(summary = "챌린지 참가")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "201",
            description = "챌린지 참가 성공",
            content = {@Content()}
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "잘못된 입력 값",
            content = {@Content()}
        )})
    ResponseEntity<ApiResponse> joinChallenge(
        @PathVariable Long challengeId,
        @AuthenticationPrincipal UserDetails userDetails);

    @Operation(summary = "챌린지 조회", description = "진행 중인 챌린지 목록을 참여자가 많은 순으로 8개까지 불러 옵니다.")
    ResponseEntity<List<PopularChallengeResponse>> getPopularChallenges();
}