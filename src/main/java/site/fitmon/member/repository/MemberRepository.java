package site.fitmon.member.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import site.fitmon.member.domain.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEmail(String email);

    boolean existsByEmail(String email);
}
