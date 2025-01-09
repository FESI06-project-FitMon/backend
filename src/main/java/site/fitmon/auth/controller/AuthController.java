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
import site.fitmon.auth.dto.response.LoginResponse;
import site.fitmon.auth.dto.response.TokenResponse;
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

        Cookie accessTokenCookie = new Cookie("access_token", tokenResponse.getAccessToken());
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setSecure(true);
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(60 * 60);
        response.addCookie(accessTokenCookie);

        Cookie refreshTokenCookie = new Cookie("refresh_token", tokenResponse.getRefreshToken());
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(7 * 24 * 60 * 60);
        response.addCookie(refreshTokenCookie);

        return ResponseEntity.ok(LoginResponse.of(tokenResponse.getMemberId(), tokenResponse.getNickName(), tokenResponse.getEmail(), tokenResponse.getProfileImageUrl()));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        try {
            String accessToken = null;
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("access_token".equals(cookie.getName())) {
                        accessToken = cookie.getValue();
                        break;
                    }
                }

                if (accessToken != null) {
                    try {
                        if (jwtTokenProvider.validateToken(accessToken)) {
                            Long memberId = jwtTokenProvider.getId(accessToken);
                            authService.logout(memberId);
                        }
                    } catch (Exception e) {
                        log.warn("Invalid token during logout", e);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error during logout", e);
        } finally {
            deleteCookie(response, "access_token");
            deleteCookie(response, "refresh_token");
        }

        return ResponseEntity.ok().build();
    }

    private void deleteCookie(HttpServletResponse response, String cookieName) {
        Cookie cookie = new Cookie(cookieName, null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        cookie.setHttpOnly(false);
        cookie.setSecure(true);
        response.addCookie(cookie);
    }
}
