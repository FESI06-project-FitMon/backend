package site.fitmon.fitmon.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import site.fitmon.fitmon.auth.dto.request.LoginRequest;
import site.fitmon.fitmon.auth.dto.request.SignupRequest;
import site.fitmon.fitmon.auth.dto.response.TokenResponse;

@Tag(name = "인증 API", description = "인증 API")
public interface AuthSwaggerController {

    @Operation(summary = "회원가입")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "회원가입 성공"
        )})
    ResponseEntity<Void> signUp(@Valid @RequestBody SignupRequest request);

    @Operation(summary = "로그인")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "로그인 성공(Header: Authorization, Cookie: refresh_token)"
        )})
    ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request, HttpServletResponse response);
}
