package site.fitmon.member.dto.response;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberResponse {

    private Long memberId;
    private String nickName;
    private String email;
    private String profileImageUrl;

    @Builder
    public MemberResponse(Long memberId, String nickName, String email, String profileImageUrl) {
        this.memberId = memberId;
        this.nickName = nickName;
        this.email = email;
        this.profileImageUrl = profileImageUrl;
    }
}
