package site.fitmon.fitmon.image.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import site.fitmon.fitmon.image.dto.response.ImageUploadResponse;
import site.fitmon.fitmon.image.entity.ImageType;
import site.fitmon.fitmon.image.service.S3Service;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/images")
public class ImageController implements ImageSwaggerController {

    private final S3Service s3Service;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ImageUploadResponse> uploadImage(
        @RequestParam("type") ImageType type,
        @RequestParam("file") MultipartFile file) {
        String imageUrl = s3Service.uploadImage(file, type);
        return ResponseEntity.ok(ImageUploadResponse.from(imageUrl));
    }
}
