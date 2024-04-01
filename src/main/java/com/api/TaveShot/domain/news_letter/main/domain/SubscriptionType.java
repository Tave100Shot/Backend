package com.api.TaveShot.domain.news_letter.main.domain;

import com.api.TaveShot.global.exception.ApiException;
import com.api.TaveShot.global.exception.ErrorType;
import lombok.Getter;

import java.util.List;



public enum SubscriptionType {
    TECHNOLOGY_KNOWLEDGE("Technology", List.of(10)), // 매월 10일 발송
    JOB_INFORMATION("Job", List.of(15)); // 매월 15일 발송

    @Getter
    private final String typeName;
    private final List<Integer> associatedDays;

    SubscriptionType(String typeName, List<Integer> associatedDays) {
        this.typeName = typeName;
        this.associatedDays = associatedDays;
    }

    // 문자열을 enum으로 변환하는 메서드
    public static SubscriptionType findType(String input) {
        for (SubscriptionType type : SubscriptionType.values()) {
            if (type.typeName.equalsIgnoreCase(input)) {
                return type;
            }
        }
        throw new ApiException(ErrorType._SUBSCRIPTION_INVALID_TYPE);
    }

    public boolean containsDay(int day) {
        return associatedDays.contains(day);
    }
}
