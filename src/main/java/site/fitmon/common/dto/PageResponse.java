package site.fitmon.common.dto;

import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PageResponse<T> {

    private List<T> content;
    private int currentPage;
    private long totalElements;
    private int totalPages;

    public PageResponse(List<T> content, int currentPage, long totalElements, int totalPages) {
        this.content = content;
        this.currentPage = currentPage;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
    }
}
