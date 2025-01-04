package site.fitmon.gathering.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import site.fitmon.common.dto.ApiResponse;
import site.fitmon.gathering.dto.request.GatheringCreateRequest;
import site.fitmon.gathering.service.GatheringService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/gatherings")
public class GatheringController implements GatheringsSwaggerController{

    private final GatheringService gatheringService;

    @PostMapping
    public ResponseEntity<ApiResponse> createGathering(
        @Valid @RequestBody GatheringCreateRequest request,
        @AuthenticationPrincipal UserDetails userDetails) {
        System.out.println("request1 = " + request.getImageUrl());
        System.out.println("request2 = " + request.getChallenges().get(0).getImageUrl());
        gatheringService.createGathering(request, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.of("모임 생성 성공"));
    }
}
