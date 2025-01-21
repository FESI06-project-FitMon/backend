package site.fitmon.review.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import site.fitmon.auth.domain.CustomUserDetails;
import site.fitmon.common.dto.ApiResponse;
import site.fitmon.common.dto.PageResponse;
import site.fitmon.common.dto.SliceResponse;
import site.fitmon.gathering.domain.MainType;
import site.fitmon.gathering.domain.SubType;
import site.fitmon.review.dto.request.ReviewCreateRequest;
import site.fitmon.review.dto.request.ReviewUpdateRequest;
import site.fitmon.review.dto.response.GatheringReviewsResponse;
import site.fitmon.review.dto.response.GuestbookResponse;
import site.fitmon.review.dto.response.MyReviewResponse;
import site.fitmon.review.dto.response.ReviewStatisticsDto;

@Tag(name = "방명록 API", description = "방명록 API")
public interface ReviewSwaggerController {

    @Operation(summary = "방명록 작성")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "201",
            description = "방명록 생성 성공",
            content = {@Content()}
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "잘못된 입력 값",
            content = {@Content()}
        )})
    ResponseEntity<ApiResponse> createReview(
        @Valid @RequestBody ReviewCreateRequest request,
        @AuthenticationPrincipal CustomUserDetails userDetails,
        @PathVariable Long gatheringId);

    @Operation(summary = "특정 모임의 방명록 리스트를 조회", description = "특정 모임의 방명록 리스트를 조회 합니다. 무한스크롤로 10개씩 받아 옵니다.")
    ResponseEntity<SliceResponse<GatheringReviewsResponse>> getGatheringReviews(
        @PathVariable Long gatheringId,
        @AuthenticationPrincipal CustomUserDetails userDetails,
        @RequestParam(defaultValue = "0") int page,
        @Parameter(description = "한번에 조회해 올 사이즈")
        @RequestParam int pageSize
    );

    @Operation(summary = "방명록 수정")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "방명록 생성 성공",
            content = {@Content()}
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "잘못된 입력 값",
            content = {@Content()}
        )})
    ResponseEntity<ApiResponse> updateReview(
        @Valid @RequestBody ReviewUpdateRequest request,
        @AuthenticationPrincipal CustomUserDetails userDetails,
        @PathVariable Long gatheringId,
        @PathVariable Long guestbookId
    );

    @Operation(summary = "방명록 삭제")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "방명록 삭제 성공",
            content = {@Content()}
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "잘못된 입력 값",
            content = {@Content()}
        )})
    ResponseEntity<ApiResponse> deleteReview(
        @AuthenticationPrincipal CustomUserDetails userDetails,
        @PathVariable Long gatheringId,
        @PathVariable Long guestbookId
    );

    @Operation(summary = "내가 작성한 방명록 목록 조회", description = "내가 작성한 방명록 목록을 조회합니다.")
    ResponseEntity<PageResponse<MyReviewResponse>> findMyReviews(
        @AuthenticationPrincipal CustomUserDetails userDetails,
        @RequestParam(defaultValue = "0") int page,
        @Parameter(description = "한번에 조회해 올 사이즈")
        @RequestParam(defaultValue = "10") int pageSize
    );

    @Operation(summary = "방명록 평점 정보 조회")
    ResponseEntity<ReviewStatisticsDto> getReviewStatistics(
        @RequestParam(required = false) MainType mainType,
        @RequestParam(required = false) SubType subType,
        @Parameter(description = "서울시")
        @RequestParam(required = false) String mainLocation,
        @Parameter(description = "강남구")
        @RequestParam(required = false) String subLocation,
        @Parameter(description = "검색 날짜 (YYYY-MM-DD)")
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate searchDate
    );

    @Operation(summary = "방명록 목록 조회(무한스크롤)")
    ResponseEntity<SliceResponse<GuestbookResponse>> getGuestbookEntries(
        @RequestParam(required = false) MainType mainType,
        @RequestParam(required = false) SubType subType,
        @Parameter(description = "서울시")
        @RequestParam(required = false) String mainLocation,
        @Parameter(description = "강남구")
        @RequestParam(required = false) String subLocation,
        @Parameter(description = "검색 날짜 (YYYY-MM-DD)")
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate searchDate,
        @Parameter(description = "정렬 기준 (latest, highestRating, mostParticipants)")
        @RequestParam(defaultValue = "latest") String sortBy,
        @Parameter(description = "정렬 순서 (ASC, DESC)")
        @RequestParam(defaultValue = "DESC") String sortDirection,
        @Parameter(description = "조회 시작 위치 (최소 0)")
        @RequestParam(value = "page", defaultValue = "0") int page,
        @Parameter(description = "한번에 조회해 올 사이즈")
        @RequestParam(value = "size", defaultValue = "20") int pageSize
    );
}
