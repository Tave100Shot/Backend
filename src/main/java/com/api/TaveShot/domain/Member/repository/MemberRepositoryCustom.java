package com.api.TaveShot.domain.Member.repository;

import com.api.TaveShot.domain.Member.domain.Member;

public interface MemberRepositoryCustom {

    Member findByIdActivated(Long id);
}
