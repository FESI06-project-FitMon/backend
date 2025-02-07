package site.fitmon.gathering.controller;

import jakarta.validation.Valid;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import site.fitmon.auth.domain.CustomUserDetails;
import site.fitmon.common.dto.ApiResponse;
import site.fitmon.common.dto.SliceResponse;
import site.fitmon.gathering.domain.MainType;
import site.fitmon.gathering.domain.SubType;
import site.fitmon.gathering.dto.request.GatheringCreateRequest;
import site.fitmon.gathering.dto.request.GatheringLikesRequest;
import site.fitmon.gathering.dto.request.GatheringModifyRequest;
import site.fitmon.gathering.dto.request.GatheringSearchCondition;
import site.fitmon.gathering.dto.response.GatheringDetailResponse;
import site.fitmon.gathering.dto.response.GatheringDetailStatusResponse;
import site.fitmon.gathering.dto.response.GatheringResponse;
import site.fitmon.gathering.service.GatheringService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/gatherings")
public class GatheringController implements GatheringsSwaggerController {

    private final GatheringService gatheringService;

    @PostMapping
    public ResponseEntity<ApiResponse> createGathering(
        @Valid @RequestBody GatheringCreateRequest request,
        @AuthenticationPrincipal CustomUserDetails userDetails) {

        gatheringService.createGathering(request, userDetails.getUsername());

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.of("모임 생성 성공"));
    }

    @PostMapping("/{gatheringId}/participants")
    public ResponseEntity<ApiResponse> joinGathering(
        @PathVariable Long gatheringId,
        @AuthenticationPrincipal CustomUserDetails userDetails) {

        gatheringService.joinGathering(gatheringId, userDetails.getUsername());

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.of("모임 참가 성공"));
    }

    @GetMapping
    public ResponseEntity<SliceResponse<GatheringResponse>> searchGatherings(
        @RequestParam(required = false) MainType mainType,
        @RequestParam(required = false) SubType subType,
        @RequestParam(required = false) String mainLocation,
        @RequestParam(required = false) String subLocation,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate searchDate,
        @RequestParam(defaultValue = "deadline") String sortBy,
        @RequestParam(defaultValue = "ASC") String sortDirection,
        @RequestParam(value = "page", defaultValue = "0") int page,
        @RequestParam(defaultValue = "8") int pageSize
    ) {

        GatheringSearchCondition condition = GatheringSearchCondition.builder()
            .mainType(mainType)
            .subType(subType)
            .mainLocation(mainLocation)
            .subLocation(subLocation)
            .searchDate(searchDate)
            .sortBy(sortBy)
            .sortDirection(sortDirection)
            .build();

        PageRequest pageable = PageRequest.of(page, pageSize);

        return ResponseEntity.ok(gatheringService.searchGatherings(condition, pageable));
    }

    @GetMapping("/{gatheringId}")
    public ResponseEntity<GatheringDetailResponse> getGatheringDetail(
        @PathVariable Long gatheringId,
        @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        String email = userDetails != null ? userDetails.getUsername() : null;
        return ResponseEntity.ok(gatheringService.getGatheringDetail(gatheringId, email));
    }

    @GetMapping("/{gatheringId}/status")
    public ResponseEntity<GatheringDetailStatusResponse> getGatheringDetailStatus(@PathVariable Long gatheringId) {
        return ResponseEntity.ok(gatheringService.getGatheringDetailStatus(gatheringId));
    }

    @PatchMapping("/{gatheringId}")
    public ResponseEntity<ApiResponse> modifyGathering(
        @Valid @RequestBody GatheringModifyRequest request,
        @PathVariable Long gatheringId,
        @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        gatheringService.modifyGathering(request, gatheringId, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.of("모임 수정 완료"));
    }

    @DeleteMapping("/{gatheringId}")
    public ResponseEntity<ApiResponse> deleteGathering(
        @PathVariable Long gatheringId,
        @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        gatheringService.deleteGathering(gatheringId, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.of("모임 취소 완료"));
    }

    @PostMapping("/likes")
    public ResponseEntity<SliceResponse<GatheringResponse>> getLikedGatherings(
        @RequestBody GatheringLikesRequest request,
        @RequestParam(required = false) MainType mainType,
        @RequestParam(required = false) SubType subType,
        @RequestParam(required = false) String mainLocation,
        @RequestParam(required = false) String subLocation,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate searchDate,
        @RequestParam(defaultValue = "deadline") String sortBy,
        @RequestParam(defaultValue = "ASC") String sortDirection,
        @RequestParam(value = "page", defaultValue = "0") int page,
        @RequestParam(defaultValue = "8") int pageSize
    ) {
        GatheringSearchCondition condition = GatheringSearchCondition.builder()
            .mainType(mainType)
            .subType(subType)
            .mainLocation(mainLocation)
            .subLocation(subLocation)
            .searchDate(searchDate)
            .sortBy(sortBy)
            .sortDirection(sortDirection)
            .build();

        PageRequest pageable = PageRequest.of(page, pageSize);
        return ResponseEntity.ok(gatheringService.getLikedGatherings(request.getGatheringIds(), condition, pageable));
    }

    @DeleteMapping("/{gatheringId}/cancel")
    public ResponseEntity<ApiResponse> cancelParticipantGathering(
        @AuthenticationPrincipal CustomUserDetails userDetails,
        @PathVariable Long gatheringId
    ) {
        gatheringService.cancelGathering(userDetails.getUsername(), gatheringId);
        return ResponseEntity.ok(ApiResponse.of("모임 참여 취소"));
    }
}
