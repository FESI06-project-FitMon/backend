package site.fitmon.image.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import site.fitmon.common.exception.ApiException;
import site.fitmon.common.exception.ErrorCode;
import site.fitmon.image.entity.ImageType;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageService {

    private final S3Service s3Service;

    public String uploadImage(ImageType type, MultipartFile file) {
        validateImageFile(file);
        return s3Service.uploadImage(file, type);
    }

    public void deleteImage(String imageUrl) {
        validateImageUrl(imageUrl);
        s3Service.deleteImage(imageUrl);
    }

    private void validateImageFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new ApiException(ErrorCode.IMAGE_REQUIRED);
        }

        String contentType = file.getContentType();
        if (contentType == null || !isValidImageType(contentType)) {
            throw new ApiException(ErrorCode.INVALID_IMAGE_TYPE);
        }

        if (file.getSize() > 5 * 1024 * 1024) {
            throw new ApiException(ErrorCode.IMAGE_SIZE_EXCEEDED);
        }
    }

    private void validateImageUrl(String imageUrl) {
        if (imageUrl == null || imageUrl.isBlank()) {
            throw new ApiException(ErrorCode.IMAGE_REQUIRED);
        }

        if (!imageUrl.startsWith("https://") || !imageUrl.contains("s3.amazonaws.com")) {
            throw new ApiException(ErrorCode.INVALID_IMAGE_FILENAME);
        }
    }

    private boolean isValidImageType(String contentType) {
        return contentType.equals(MediaType.IMAGE_JPEG_VALUE) ||
            contentType.equals("image/jpg") ||
            contentType.equals(MediaType.IMAGE_PNG_VALUE);
    }
}
