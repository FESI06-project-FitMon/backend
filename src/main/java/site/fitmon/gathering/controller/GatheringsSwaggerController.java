package site.fitmon.gathering.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import site.fitmon.common.dto.ApiResponse;
import site.fitmon.common.dto.SliceResponse;
import site.fitmon.gathering.domain.MainType;
import site.fitmon.gathering.domain.SubType;
import site.fitmon.gathering.dto.request.GatheringCreateRequest;
import site.fitmon.gathering.dto.request.GatheringModifyRequest;
import site.fitmon.gathering.dto.response.GatheringDetailResponse;
import site.fitmon.gathering.dto.response.GatheringDetailStatusResponse;
import site.fitmon.gathering.dto.response.GatheringResponse;

@Tag(name = "모임 API", description = "모임 API")
public interface GatheringsSwaggerController {

    @Operation(summary = "모임 생성", security = {@SecurityRequirement(name = "cookieAuth")})
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "201",
            description = "모임 생성 성공",
            content = {@Content()}
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "잘못된 입력 값",
            content = {@Content()}
        )})
    ResponseEntity<ApiResponse> createGathering(
        @Valid @RequestBody GatheringCreateRequest request,
        @AuthenticationPrincipal UserDetails userDetails);


    @Operation(summary = "모임 참가")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "201",
            description = "모임 참가 성공",
            content = {@Content()}
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "잘못된 입력 값",
            content = {@Content()}
        )})
    ResponseEntity<ApiResponse> joinGathering(
        @PathVariable Long gathering,
        @AuthenticationPrincipal UserDetails userDetails);

    @Operation(summary = "모임 목록 조회", description = "모임 목록을 8개씩 무한 스크롤로 조회합니다. 타입, 장소, 시간 필터링이 가능합니다.")
    ResponseEntity<SliceResponse<GatheringResponse>> searchGatherings(
        @RequestParam(required = false) MainType mainType,
        @RequestParam(required = false) SubType subType,
        @Parameter(description = "서울시")
        @RequestParam(required = false) String mainLocation,
        @Parameter(description = "강남구")
        @RequestParam(required = false) String subLocation,
        @Parameter(description = "검색 날짜 (YYYY-MM-DD)")
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate searchDate,
        @Parameter(description = "정렬 기준 (deadline, participants) / 마감 임박 -> deadline, ASC")
        @RequestParam(defaultValue = "deadline") String sortBy,
        @Parameter(description = "정렬 순서 (ASC, DESC)")
        @RequestParam(defaultValue = "ASC") String sortDirection,
        @Parameter(description = "조회 시작 위치 (최소 0)")
        @RequestParam(value = "page", defaultValue = "0") int page,
        @Parameter(description = "한번에 조회해 올 사이즈")
        @RequestParam int pageSize
    );

    @Operation(summary = "모임 상세 조회", description = "특정 모임 상세 조회 / 평점 및 참여자 리스트 최대 5명을 불러 옵니다.")
    ResponseEntity<GatheringDetailResponse> getGatheringDetail(
        @PathVariable Long gatheringId,
        @AuthenticationPrincipal UserDetails userDetails);

    @Operation(summary = "특정 모임 상태 조회", description = "특정 모임 상태 조회")
    @GetMapping("/{gatheringId}/status")
    ResponseEntity<GatheringDetailStatusResponse> getGatheringDetailStatus(@PathVariable Long gatheringId);

    @Operation(summary = "모임 수정", security = {@SecurityRequirement(name = "cookieAuth")})
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "201",
            description = "모임 수정 성공",
            content = {@Content()}
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "잘못된 입력 값",
            content = {@Content()}
        )})
    ResponseEntity<ApiResponse> modifyGathering(
        @Valid @RequestBody GatheringModifyRequest request,
        @PathVariable Long gatheringId,
        @AuthenticationPrincipal UserDetails userDetails
    );

    @Operation(summary = "모임 취소")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "모임 취소 성공",
            content = {@Content()}
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "잘못된 입력 값",
            content = {@Content()}
        )})
    ResponseEntity<ApiResponse> deleteGathering(
        @PathVariable Long gatheringId,
        @AuthenticationPrincipal UserDetails userDetails
    );
}
