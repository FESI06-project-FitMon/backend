package site.fitmon.common.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.WebUtils;
import site.fitmon.auth.domain.CustomUserDetails;
import site.fitmon.common.domain.RefreshToken;
import site.fitmon.common.domain.RefreshTokenRepository;
import site.fitmon.common.exception.ApiException;
import site.fitmon.common.exception.ErrorCode;
import site.fitmon.common.exception.ErrorResponse;
import site.fitmon.member.domain.Member;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {

        try {
            handleTokenValidation(request, response);
        } catch (ApiException ex) {
            sendErrorResponse(response, ex.getErrorCode());
            return;
        } catch (Exception ex) {
            log.error("Authentication error occurred", ex);
            sendErrorResponse(response, ErrorCode.SERVER_ERROR);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private void handleTokenValidation(HttpServletRequest request, HttpServletResponse response) {
        String token = resolveToken(request);
        if (token == null) {
            return;
        }

        try {
            validateAccessTokenAndSetAuthentication(token);
        } catch (ExpiredJwtException e) {
            handleExpiredAccessToken(request, response);
        } catch (JwtException e) {
            throw new ApiException(ErrorCode.INVALID_TOKEN);
        }
    }

    private void validateAccessTokenAndSetAuthentication(String token) {
        if (jwtTokenProvider.validateToken(token)) {
            setAuthentication(token);
        }
    }

    private void handleExpiredAccessToken(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = getRefreshTokenFromCookie(request);
        if (refreshToken == null) {
            throw new ApiException(ErrorCode.INVALID_TOKEN);
        }

        try {
            refreshAccessToken(refreshToken, response);
        } catch (ExpiredJwtException e) {
            handleExpiredRefreshToken(response, refreshToken);
        }
    }

    private String getRefreshTokenFromCookie(HttpServletRequest request) {
        Cookie refreshTokenCookie = WebUtils.getCookie(request, "refresh_token");
        return refreshTokenCookie != null ? refreshTokenCookie.getValue() : null;
    }

    private void refreshAccessToken(String refreshToken, HttpServletResponse response) {
        try {
            if (!jwtTokenProvider.validateToken(refreshToken)) {
                handleExpiredRefreshToken(response, refreshToken);
                return;
            }

            RefreshToken refreshTokenEntity = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new ApiException(ErrorCode.INVALID_TOKEN));

            Member member = refreshTokenEntity.getMember();
            String newAccessToken = jwtTokenProvider.createAccessToken(member.getId());

            Cookie accessTokenCookie = new Cookie("access_token", newAccessToken);
            accessTokenCookie.setHttpOnly(true);
            accessTokenCookie.setSecure(true);
            accessTokenCookie.setPath("/");
            accessTokenCookie.setMaxAge(60 * 60);
            response.addCookie(accessTokenCookie);

            setAuthentication(newAccessToken);

        } catch (ApiException e) {
            throw e;
        }
    }

    private void handleExpiredRefreshToken(HttpServletResponse response, String refreshToken) {
        try {
            refreshTokenRepository.findByToken(refreshToken)
                .ifPresent(refreshTokenRepository::delete);

            Cookie refreshTokenCookie = new Cookie("refresh_token", null);
            refreshTokenCookie.setMaxAge(0);
            refreshTokenCookie.setPath("/");
            refreshTokenCookie.setHttpOnly(true);
            refreshTokenCookie.setSecure(true);
            response.addCookie(refreshTokenCookie);

            Cookie accessTokenCookie = new Cookie("access_token", null);
            accessTokenCookie.setMaxAge(0);
            accessTokenCookie.setPath("/");
            accessTokenCookie.setHttpOnly(true);
            accessTokenCookie.setSecure(true);
            response.addCookie(accessTokenCookie);

            SecurityContextHolder.clearContext();

            sendErrorResponse(response, ErrorCode.EXPIRED_TOKEN);

        } catch (IOException e) {
            log.error("Failed to send error response for expired refresh token", e);
            throw new ApiException(ErrorCode.SERVER_ERROR);
        }
    }

    private String resolveToken(HttpServletRequest request) {
        Cookie accessTokenCookie = WebUtils.getCookie(request, "access_token");
        return accessTokenCookie != null ? accessTokenCookie.getValue() : null;
    }

    private void setAuthentication(String token) {
        Long memberId = jwtTokenProvider.getId(token);
        Member member = new Member(memberId);

        CustomUserDetails customUserDetails = new CustomUserDetails(member);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
            customUserDetails, null, customUserDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private void sendErrorResponse(HttpServletResponse response, ErrorCode errorCode) throws IOException {
        response.setStatus(errorCode.getHttpStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        ErrorResponse errorResponse = new ErrorResponse(errorCode);
        String jsonResponse = objectMapper.writeValueAsString(errorResponse);

        response.getWriter().write(jsonResponse);
    }
}