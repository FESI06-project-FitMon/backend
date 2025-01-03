package site.fitmon.fitmon.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.fitmon.fitmon.auth.dto.request.LoginRequest;
import site.fitmon.fitmon.auth.dto.request.SignupRequest;
import site.fitmon.fitmon.auth.dto.response.TokenResponse;
import site.fitmon.fitmon.common.domain.RefreshToken;
import site.fitmon.fitmon.common.domain.RefreshTokenRepository;
import site.fitmon.fitmon.common.exception.ApiException;
import site.fitmon.fitmon.common.exception.ErrorCode;
import site.fitmon.fitmon.common.security.jwt.JwtTokenProvider;
import site.fitmon.fitmon.member.domain.Member;
import site.fitmon.fitmon.member.repository.MemberRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public void signUp(SignupRequest request) {
        if (memberRepository.existsByEmail(request.getEmail())) {
            throw new ApiException(ErrorCode.DUPLICATE_MEMBER_EMAIL);
        }

        Member member = request.toEntity(passwordEncoder.encode(request.getPassword()));
        memberRepository.save(member);
    }

    @Transactional
    public TokenResponse login(LoginRequest request) {
        Member member = memberRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new ApiException(ErrorCode.INVALID_CREDENTIALS));

        if (!passwordEncoder.matches(request.getPassword(), member.getPassword())) {
            throw new ApiException(ErrorCode.INVALID_CREDENTIALS);
        }

        String accessToken = jwtTokenProvider.createAccessToken(member.getId(), member.getEmail());
        String refreshToken = jwtTokenProvider.createRefreshToken(member.getId(), member.getEmail());

        refreshTokenRepository.findByMember(member)
            .ifPresentOrElse(
                token -> token.updateToken(refreshToken),
                () -> refreshTokenRepository.save(new RefreshToken(refreshToken, member))
            );

        return new TokenResponse(accessToken, refreshToken);
    }

    @Transactional
    public void logout(String email) {
        Member member = memberRepository.findByEmail(email)
            .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));
        refreshTokenRepository.deleteByMember(member);
        SecurityContextHolder.clearContext();
    }
}
