package site.fitmon.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResponse {

    private Long memberId;
    private String nickName;
    private String email;
    private String profileImageUrl;

    public static LoginResponse of(Long memberId, String nickName, String email, String profileImageUrl) {
        return new LoginResponse(memberId, nickName, email, profileImageUrl);
    }
}
