package site.fitmon.fitmon.image.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import site.fitmon.fitmon.image.dto.response.ImageUploadResponse;
import site.fitmon.fitmon.image.entity.ImageType;

@Tag(name = "이미지 API", description = "이미지 업로드 API")
public interface ImageSwaggerController {

    @Operation(summary = "이미지 업로드", description = "멤버/모임/챌린지에 사용될 이미지를 업로드합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "이미지 업로드 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청", content = {@Content()}),
        @ApiResponse(responseCode = "500", description = "서버 오류", content = {@Content()})
    })
    ResponseEntity<ImageUploadResponse> uploadImage(
        @Parameter(description = "이미지 타입(MEMBER/GATHERING/CHALLENGE)", required = true)
        @RequestParam("type") ImageType type,
        @Parameter(description = "이미지 파일(JPEG/JPG/PNG)", required = true)
        @RequestParam("file") MultipartFile file);


}
