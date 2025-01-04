package site.fitmon.auth.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import site.fitmon.auth.dto.request.LoginRequest;
import site.fitmon.auth.dto.request.SignupRequest;
import site.fitmon.auth.dto.response.TokenResponse;
import site.fitmon.auth.dto.response.LoginResponse;
import site.fitmon.auth.service.AuthService;
import site.fitmon.common.dto.ApiResponse;
import site.fitmon.common.security.jwt.JwtTokenProvider;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class AuthController implements AuthSwaggerController {

    private final JwtTokenProvider jwtTokenProvider;
    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse> signUp(@Valid @RequestBody SignupRequest request) {
        authService.signUp(request);
        log.info(request.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.of("사용자 생성 성공"));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request,
        HttpServletResponse response) {
        TokenResponse tokenResponse = authService.login(request);
        response.addHeader("Authorization", "Bearer " + tokenResponse.getAccessToken());
        Cookie refreshTokenCookie = new Cookie("refresh_token", tokenResponse.getRefreshToken());
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(7 * 24 * 60 * 60);
        response.addCookie(refreshTokenCookie);

        return ResponseEntity.ok(LoginResponse.of(tokenResponse.getMemberId(), tokenResponse.getEmail()));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            authService.logout(jwtTokenProvider.getEmail(token.substring(7)));
        }

        Cookie refreshTokenCookie = new Cookie("refresh_token", null);
        refreshTokenCookie.setMaxAge(0);
        refreshTokenCookie.setPath("/");
        response.addCookie(refreshTokenCookie);

        return ResponseEntity.ok().build();
    }
}
