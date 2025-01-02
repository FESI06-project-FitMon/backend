package site.fitmon.fitmon.common.domain;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import site.fitmon.fitmon.member.domain.Member;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByMember(Member member);

    Optional<RefreshToken> findByToken(String token);

    void deleteByMember(Member member);
}
