package com.api.TaveShot.domain.Member.repository;

import static com.api.TaveShot.domain.Member.domain.QMember.member;

import com.api.TaveShot.domain.Member.domain.Member;
import com.api.TaveShot.domain.newsletter.client.domain.QSubscription;
import com.api.TaveShot.global.exception.ApiException;
import com.api.TaveShot.global.exception.ErrorType;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

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

    private BooleanExpression containsEmail(final String memberEmail) {
        if (memberEmail == null || memberEmail.isEmpty()) {
            return Expressions.TRUE; // 검색 조건이 없으면 항상 true
        }
        return member.gitEmail.contains(memberEmail);
    }

    private BooleanExpression containsBojName(final String memberBojName) {
        if (memberBojName == null || memberBojName.isEmpty()) {
            return Expressions.TRUE; // 검색 조건이 없으면 항상 true
        }
        return member.bojName.contains(memberBojName);
    }

    @Override
    public Page<Member> getMemberPaging(final String searchBojName, final String searchEmail, final Pageable pageable) {
        List<Member> members = getMemberContent(searchBojName, searchEmail);
        Long count = getMemberCount(searchBojName, searchEmail);

        return new PageImpl<>(members, pageable, count);
    }

    private List<Member> getMemberContent(final String memberBojName, final String memberEmail) {
        return queryFactory
                .selectFrom(member)
                .leftJoin(member.subscription, QSubscription.subscription).fetchJoin()
                .where(
                        containsBojName(memberBojName),
                        containsEmail(memberEmail)
                )
                .fetch();
    }

    private Long getMemberCount(final String memberBojName, final String memberEmail) {
        return queryFactory
                .select(Wildcard.count)
                .from(member)
                .leftJoin(member.subscription, QSubscription.subscription)
                .where(
                        containsBojName(memberBojName),
                        containsEmail(memberEmail)
                )
                .fetchOne();
    }


}
