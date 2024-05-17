package com.api.TaveShot.domain.newsletter.repository;

import static com.api.TaveShot.domain.newsletter.domain.QNewsletter.newsletter;

import com.api.TaveShot.domain.newsletter.domain.LetterType;
import com.api.TaveShot.domain.newsletter.domain.Newsletter;
import com.api.TaveShot.global.exception.ApiException;
import com.api.TaveShot.global.exception.ErrorType;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@RequiredArgsConstructor
public class NewsletterRepositoryImpl implements NewsletterRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Newsletter findByIdActivated(final Long newsletterId) {
        Newsletter findNewsletter = jpaQueryFactory
                .selectFrom(newsletter)
                .where(
                        newsletter.id.eq(newsletterId),
                        getActivated()
                )
                .fetchOne();
        return Optional.ofNullable(findNewsletter)
                .orElseThrow(() -> new ApiException(ErrorType.NEWSLETTER_NOT_FOUND));
    }

    private BooleanExpression getActivated() {
        return newsletter.deleted.isFalse();
    }

    @Override
    public Page<Newsletter> getPaging(final List<LetterType> letterTypes, final String containWord,
                                      final Pageable pageable) {
        List<Newsletter> pageContent = getPageContent(letterTypes, containWord, pageable);
        Long count = countQuery(letterTypes, containWord);
        return new PageImpl<>(pageContent, pageable, count);
    }

    @Override
    public List<Newsletter> findRecent6(final Long limit) {
        return jpaQueryFactory
                .selectFrom(newsletter)
                .where(
                        getActivated()
                )
                .limit(limit)
                .orderBy(newsletter.id.desc())
                .fetch();
    }

    @Override
    public List<Newsletter> findByYearAndMonth(int year, int month) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.plusMonths(1);  // 다음 달 1일

        return jpaQueryFactory.selectFrom(newsletter)
                .where(
                        newsletter.createdDate.goe(startDate.atStartOfDay()),
                        newsletter.createdDate.lt(endDate.atStartOfDay()))
                .fetch();
    }

    private List<Newsletter> getPageContent(final List<LetterType> letterTypes, final String containWord,
                                            final Pageable pageable) {
        return jpaQueryFactory
                .selectFrom(newsletter)
                .where(
                        newsletter.content.contains(containWord),
                        newsletter.title.contains(containWord),
                        newsletter.letterType.in(letterTypes),
                        getActivated()
                )
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .orderBy(newsletter.id.desc())
                .fetch();
    }

    private Long countQuery(final List<LetterType> letterTypes, final String containWord) {
        return jpaQueryFactory
                .select(Wildcard.count)
                .from(newsletter)
                .where(
                        newsletter.content.contains(containWord),
                        newsletter.title.contains(containWord),
                        newsletter.letterType.in(letterTypes),
                        getActivated()
                )
                .fetchOne();
    }
}
