package site.fitmon.gathering.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ParticipantsResponse {

    private Long memberId;
    private String nickName;
    private String profileImageUrl;


    @Builder
    public ParticipantsResponse(Long memberId, String nickName, String profileImageUrl) {
        this.memberId = memberId;
        this.nickName = nickName;
        this.profileImageUrl = profileImageUrl;
    }
}
