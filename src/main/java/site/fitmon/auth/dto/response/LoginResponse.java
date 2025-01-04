package site.fitmon.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResponse {

    private Long memberId;
    private String email;

    public static LoginResponse of(Long memberId, String email) {
        return new LoginResponse(memberId, email);
    }
}
