package site.fitmon.image.service;

import java.io.IOException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import site.fitmon.common.exception.ApiException;
import site.fitmon.common.exception.ErrorCode;
import site.fitmon.image.entity.ImageType;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3Service {

    private final S3Client s3Client;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucket;

    public String uploadImage(MultipartFile file, ImageType imageType) {
        try {
            String fileName = generateUniqueFileName(file.getOriginalFilename());
            String filePath = imageType.getDirectory() + "/" + fileName;

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(filePath)
                .contentType(file.getContentType())
                .build();

            s3Client.putObject(putObjectRequest,
                RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

            return generateImageUrl(filePath);
        } catch (IOException e) {
            log.error("Failed to upload image to S3", e);
            throw new ApiException(ErrorCode.IMAGE_UPLOAD_FAILED);
        } catch (S3Exception e) {
            log.error("S3 operation failed", e);
            throw new ApiException(ErrorCode.IMAGE_UPLOAD_FAILED);
        }
    }

    public void deleteImage(String imageUrl) {
        try {
            String key = extractKeyFromUrl(imageUrl);
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

            s3Client.deleteObject(deleteObjectRequest);
        } catch (S3Exception e) {
            log.error("Failed to delete image from S3", e);
            throw new ApiException(ErrorCode.IMAGE_UPLOAD_FAILED);
        }
    }

    private String generateUniqueFileName(String originalFilename) {
        return UUID.randomUUID().toString() + "_" + originalFilename;
    }

    private String generateImageUrl(String filePath) {
        return String.format("https://%s.s3.amazonaws.com/%s", bucket, filePath);
    }

    private String extractKeyFromUrl(String imageUrl) {
        String prefix = String.format("https://%s.s3.amazonaws.com/", bucket);
        return imageUrl.substring(prefix.length());
    }
}
