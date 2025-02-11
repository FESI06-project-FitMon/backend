package site.fitmon.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import site.fitmon.member.domain.Member;

@Getter
@NoArgsConstructor
public class SignupRequest {

    @Schema(description = "이메일", example = "kimfitmon@fitmon.site")
    @Email(message = "유효하지 않은 이메일 형식입니다.")
    private String email;

    @Schema(description = "닉네임", example = "핏몬스터")
    @Size(min = 2, max = 10, message = "닉네임은 2 ~ 10자 사이로 입력해주세요.")
    private String nickName;

    @Schema(description = "비밀번호", example = "Password@123")
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
        message = "비밀번호는 대문자, 소문자, 숫자, 특수문자를 포함하여 8자 이상이어야 합니다."
    )
    private String password;

    public Member toEntity(String encodedPassword) {
        return Member.builder()
            .email(email)
            .nickName(nickName)
            .password(encodedPassword)
            .build();
    }
}
