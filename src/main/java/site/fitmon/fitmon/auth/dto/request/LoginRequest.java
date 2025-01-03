package site.fitmon.fitmon.auth.dto.request;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LoginRequest {

    @Schema(description = "이메일", example = "kimfitmon@fitmon.site")
    @NotBlank
    @Email
    private String email;

    @Schema(description = "비밀번호", example = "password1234")
    @NotBlank
    private String password;
}
