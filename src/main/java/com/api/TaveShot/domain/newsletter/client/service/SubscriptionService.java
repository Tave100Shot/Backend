package com.api.TaveShot.domain.newsletter.client.service;

import com.api.TaveShot.domain.Member.domain.Member;
import com.api.TaveShot.domain.Member.repository.MemberRepository;
import com.api.TaveShot.domain.newsletter.client.domain.Subscription;
import com.api.TaveShot.domain.newsletter.client.dto.SubscriptionRequest;
import com.api.TaveShot.domain.newsletter.client.dto.SubscriptionResponse;
import com.api.TaveShot.domain.newsletter.client.repository.SubscriptionRepository;
import com.api.TaveShot.domain.newsletter.domain.LetterType;
import com.api.TaveShot.global.exception.ApiException;
import com.api.TaveShot.global.exception.ErrorType;
import com.api.TaveShot.global.util.SecurityUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Transactional
public class SubscriptionService {

    private final MemberRepository memberRepository;
    private final SubscriptionRepository subscriptionRepository;

    public SubscriptionResponse subscribe(SubscriptionRequest request) throws ApiException {
        Member member = SecurityUtil.getCurrentMember();
        validateSubscription(member, request.getLetterType());

        Subscription subscription = Subscription.builder()
                .member(member)
                .letterType(request.getLetterType())
                .build();
        subscriptionRepository.save(subscription);

        member.Subscribed();
        memberRepository.save(member);

        return new SubscriptionResponse(
                request.getLetterType().getTitle(),
                member.getGitEmail(),
                member.getBojName()
        );
    }

    private void validateSubscription(Member member, LetterType letterType) throws ApiException {
        if (member.getGitEmail() == null || member.getBojName() == null) {
            throw new ApiException(ErrorType._EMAIL_OR_NICKNAME_NOT_FOUND);
        }

        if (!member.isEmailVerified()) {
            throw new ApiException(ErrorType._EMAIL_NOT_VERIFIED);
        }

        subscriptionRepository.findByMemberIdAndLetterType(member.getId(), letterType)
                .ifPresent(s -> {
                    throw new ApiException(ErrorType._SUBSCRIPTION_ALREADY_EXIST);
                });
    }
}

