package site.fitmon.gathering.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import site.fitmon.gathering.domain.GatheringParticipant;

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

    public ParticipantsResponse(GatheringParticipant participant) {
        this.memberId = participant.getMember().getId();
        this.nickName = participant.getMember().getNickName();
        this.profileImageUrl = participant.getMember().getProfileImageUrl();
    }
}
