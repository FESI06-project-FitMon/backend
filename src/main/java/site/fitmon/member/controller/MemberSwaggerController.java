package site.fitmon.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import site.fitmon.auth.dto.request.SignupRequest;

@Tag(name = "회원 API", description = "회원 API")
public interface MemberSwaggerController {

    @Operation(summary = "회원가입")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "회원가입에 성공했습니다.",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(
                    type = "object",
                    example = "{\"message\": \"회원가입이 완료되었습니다.\"}"
                )
            )
        )})
    ResponseEntity<Void> signUp(@RequestBody SignupRequest signupRequest);
}
