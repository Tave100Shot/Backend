package com.api.TaveShot.domain.Member.repository;

import com.api.TaveShot.domain.Member.domain.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MemberRepositoryCustom {

    Member findByIdActivated(Long id);

    Page<Member> getMemberPaging(String memberName, String memberEmail, Pageable pageable);
}
