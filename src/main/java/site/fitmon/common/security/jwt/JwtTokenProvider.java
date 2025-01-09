package site.fitmon.common.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Optional;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import site.fitmon.common.exception.ApiException;
import site.fitmon.common.exception.ErrorCode;

@Slf4j
@Component
public class JwtTokenProvider {

    private SecretKey secretKey;

    @Value("${jwt.access.expiration}")
    private long accessTokenValidity;

    @Value("${jwt.refresh.expiration}")
    private long refreshTokenValidity;

    public JwtTokenProvider(@Value("${jwt.secret.key}") String secret) {
        this.secretKey =
            new SecretKeySpec(
                secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    public Long getId(String token) {
        Claims claims = getClaims(token);
        return Optional.ofNullable(claims.get("id", Long.class))
            .orElseThrow(() -> new ApiException(ErrorCode.INVALID_TOKEN));
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.error("Invalid JWT signature");
        } catch (ExpiredJwtException e) {
            log.error("Expired JWT token");
        } catch (UnsupportedJwtException e) {
            log.error("Unsupported JWT token");
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty");
        }
        return false;
    }

    public String createAccessToken(Long id) {
        return createToken(id, accessTokenValidity);
    }

    public String createRefreshToken(Long id) {
        return createToken(id, refreshTokenValidity);
    }

    private String createToken(Long id, long validityInSeconds) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInSeconds * 1000);

        return Jwts.builder()
            .claim("id", id)
            .issuedAt(now)
            .expiration(validity)
            .signWith(secretKey)
            .compact();
    }
}
