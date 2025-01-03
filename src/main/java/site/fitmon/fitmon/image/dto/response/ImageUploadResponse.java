package site.fitmon.fitmon.image.dto.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ImageUploadResponse {

    private String imageUrl;

    private ImageUploadResponse(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public static ImageUploadResponse from(String imageUrl) {
        return new ImageUploadResponse(imageUrl);
    }
}
