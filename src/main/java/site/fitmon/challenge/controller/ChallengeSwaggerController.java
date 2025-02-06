package site.fitmon.challenge.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import site.fitmon.auth.domain.CustomUserDetails;
import site.fitmon.challenge.dto.request.ChallengeCreateRequest;
import site.fitmon.challenge.dto.request.ChallengeEvidenceRequest;
import site.fitmon.challenge.dto.request.ChallengeSearchCondition.ChallengeStatus;
import site.fitmon.challenge.dto.response.ChallengeCreateResponse;
import site.fitmon.challenge.dto.response.GatheringChallengesResponse;
import site.fitmon.challenge.dto.response.PopularChallengeResponse;
import site.fitmon.common.dto.ApiResponse;
import site.fitmon.common.dto.SliceResponse;

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
    ResponseEntity<ChallengeCreateResponse> createChallenge(
        @Valid @RequestBody ChallengeCreateRequest request,
        @PathVariable Long gatheringId,
        @AuthenticationPrincipal CustomUserDetails userDetails);


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
        @AuthenticationPrincipal CustomUserDetails userDetails);

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
        @AuthenticationPrincipal CustomUserDetails userDetails);

    @Operation(summary = "챌린지 조회", description = "진행 중인 챌린지 목록을 참여자가 많은 순으로 8개까지 불러 옵니다.")
    ResponseEntity<List<PopularChallengeResponse>> getPopularChallenges();

    @Operation(summary = "특정 모임의 챌린지 조회", description = "특정 모임의 챌린지 목록을 진행 상태에 따라 조회할 수 있습니다.")
    ResponseEntity<SliceResponse<GatheringChallengesResponse>> getGatheringChallenges(
        @Parameter(description = "정렬 기준 (IN_PROGRESS, CLOSED)")
        @RequestParam(required = false) ChallengeStatus status,
        @PathVariable Long gatheringId,
        @AuthenticationPrincipal CustomUserDetails userDetails,
        @Parameter(description = "조회 시작 위치 (최소 0)")
        @RequestParam(defaultValue = "0") int page,
        @Parameter(description = "한번에 조회해 올 사이즈")
        @RequestParam int pageSize
    );

    @Operation(summary = "챌린지 삭제")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "챌린지 삭제 성공",
            content = {@Content()}
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "잘못된 입력 값",
            content = {@Content()}
        )})
    ResponseEntity<ApiResponse> deleteChallenge(
        @PathVariable Long challengeId,
        @AuthenticationPrincipal CustomUserDetails userDetails
    );

    @Operation(summary = "특정 모임의 챌린지 전체 조회", description = "특정 모임의 챌린지 목록을 전체 조회 합니다.")
    ResponseEntity<List<GatheringChallengesResponse>> getAllGatheringChallenges(
        @PathVariable Long gatheringId,
        @AuthenticationPrincipal CustomUserDetails userDetails
    );
}