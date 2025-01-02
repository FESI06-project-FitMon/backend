package site.fitmon.fitmon.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
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
            description = "사용자 생성 성공",
            content = {@Content()}
        ),
        @ApiResponse(
            responseCode = "400",
            description = "이미 사용중인 아이디입니다.",
            content = {@Content()}
        )})
    ResponseEntity<Void> signUp(@Valid @RequestBody SignupRequest request);

    @Operation(summary = "로그인")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "로그인 성공(Header: Authorization, Cookie: refresh_token)",
            content = {@Content()}
        ),
        @ApiResponse(
            responseCode = "401",
            description = "아이디 또는 비밀번호가 일치하지 않습니다.",
            content = {@Content()}
        )})
    ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request, HttpServletResponse response);

    @Operation(summary = "로그아웃")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "로그아웃 성공",
            content = {@Content()}
        )})
    ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response);
}
