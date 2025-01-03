package site.fitmon.fitmon.image.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ImageType {
    MEMBER("members"),
    GATHERING("gatherings"),
    CHALLENGE("challenges");

    private final String directory;
}
