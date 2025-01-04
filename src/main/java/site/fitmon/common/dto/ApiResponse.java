package site.fitmon.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse {

    private String message;

    public static ApiResponse of(String message) {
        return new ApiResponse(message);
    }
}