package site.fitmon.common.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

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

    public String getEmail(String token) {
        return getClaims(token).getSubject();
    }

    public Long getId(String token) {
        return getClaims(token).get("id", Long.class);
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

    public String createAccessToken(Long id, String email) {
        return createToken(id, email, accessTokenValidity);
    }

    public String createRefreshToken(Long id, String email) {
        return createToken(id, email, refreshTokenValidity);
    }

    private String createToken(Long id, String email, long validityInSeconds) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInSeconds * 1000);

        return Jwts.builder()
            .subject(email)
            .claim("id", id)
            .issuedAt(now)
            .expiration(validity)
            .signWith(secretKey)
            .compact();
    }
}
