package site.fitmon.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TokenResponse {
    private String accessToken;
    private String refreshToken;
    private Long memberId;
    private String nickName;
    private String email;
    private String profileImageUrl;
}
