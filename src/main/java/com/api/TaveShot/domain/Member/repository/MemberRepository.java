package com.api.TaveShot.domain.Member.repository;

import com.api.TaveShot.domain.Member.domain.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {
    Optional<Member> findByGitId(Long gitId);
    Optional<Member> findByGitEmail(String gitEmail);
}
