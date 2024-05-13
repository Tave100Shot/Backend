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
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final EmailSenderService emailSenderService;
    private final MemberRepository memberRepository;

    public List<SubscriptionResponse> subscribe(SubscriptionRequest request) throws ApiException {
        Member member = SecurityUtil.getCurrentMember();

        List<LetterType> requestedTypes = request.getLetterTypes();
        List<Subscription> existingSubscriptions = subscriptionRepository.findByMemberId(member.getId());

        Set<LetterType> currentSubscriptions = existingSubscriptions.stream()
                .map(Subscription::getLetterType)
                .collect(Collectors.toSet());

        // 동일한 유형으로 구독 시도
        for (LetterType requestedType : requestedTypes) {
            if (currentSubscriptions.contains(requestedType)) {
                throw new ApiException(ErrorType._SUBSCRIPTION_ALREADY_EXIST);
            }
        }

        // 이미 ALL을 구독 중인 경우
        if (currentSubscriptions.contains(LetterType.ALL)) {
            throw new ApiException(ErrorType._SUBSCRIPTION_ALREADY_EXIST);
        }

        // 개별 유형을 구독 중이고, ALL 또는 다른 개별 유형을 구독하려 할 때 ALL로 업데이트
        if (!Collections.disjoint(currentSubscriptions, EnumSet.of(LetterType.DEV_LETTER, LetterType.EMPLOYEE_LETTER)) &&
                !Collections.disjoint(requestedTypes, EnumSet.of(LetterType.ALL, LetterType.DEV_LETTER, LetterType.EMPLOYEE_LETTER))) {
            requestedTypes = List.of(LetterType.ALL);
            existingSubscriptions.forEach(subscriptionRepository::delete);
        }

        return processSubscriptions(member, requestedTypes);
    }


    private List<SubscriptionResponse> processSubscriptions(Member member, List<LetterType> requestedTypes) throws ApiException {
        List<SubscriptionResponse> responses = new ArrayList<>();
        for (LetterType type : requestedTypes) {
            validateSubscription(member, type);

            Subscription subscription = Subscription.builder()
                    .member(member)
                    .letterType(type)
                    .build();
            subscriptionRepository.save(subscription);

            member.subscribed();
            memberRepository.save(member);

            try {
                emailSenderService.sendEmail(member.getGitEmail(), "Subscribe successfully!~!", "You are now subscribed to " + type);
            } catch (MessagingException e) {
                throw new ApiException(ErrorType._EMAIL_SEND_FAILED);
            }

            responses.add(new SubscriptionResponse(type.getTitle(), member.getGitEmail(), member.getBojName()));
        }
        return responses;
    }

    private void validateSubscription(Member member, LetterType letterType) throws ApiException {
        if (member.getGitEmail() == null || member.getBojName() == null) {
            throw new ApiException(ErrorType._EMAIL_OR_NICKNAME_NOT_FOUND);
        }

        if (!member.isEmailVerified()) {
            throw new ApiException(ErrorType._EMAIL_NOT_VERIFIED);
        }
    }
}
