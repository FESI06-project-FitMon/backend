package site.fitmon.common.dto;

import java.util.List;
import lombok.Getter;

@Getter
public class SliceResponse<T> {

    private List<T> content;
    private boolean hasNext;

    public SliceResponse(List<T> content, boolean hasNext) {
        this.content = content;
        this.hasNext = hasNext;
    }
}
