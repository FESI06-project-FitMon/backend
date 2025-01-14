package site.fitmon.member.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberUpdateRequest {

    @Schema(description = "수정할 닉네임", example = "핏몬핏몬")
    @Size(min = 2, max = 10, message = "닉네임은 2 ~ 10자 사이로 입력해주세요.")
    private String nickName;

    @Schema(description = "수정할 프로필 이미지 URL", example = "https://fitmon-bucket.s3.amazonaws.com/members/998b8514-0fe2-4c5e-8efa-3a804c6abb86_profile.png")
    private String profileImageUrl;
}
