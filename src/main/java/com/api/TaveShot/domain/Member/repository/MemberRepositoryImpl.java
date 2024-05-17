package com.api.TaveShot.domain.Member.repository;

import static com.api.TaveShot.domain.Member.domain.QMember.*;

import com.api.TaveShot.domain.Member.domain.Member;
import com.api.TaveShot.domain.Member.domain.QMember;
import com.api.TaveShot.global.exception.ApiException;
import com.api.TaveShot.global.exception.ErrorType;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Optional;
import javax.swing.text.html.Option;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Member findByIdActivated(final Long id) {
        Member findMember = queryFactory
                .selectFrom(member)
                .where(
                        member.id.eq(id),
                        member.deleted.isFalse()
                )
                .fetchOne();

        return Optional.ofNullable(findMember)
                .orElseThrow(() -> new ApiException(ErrorType._USER_NOT_FOUND_DB));
    }
}
