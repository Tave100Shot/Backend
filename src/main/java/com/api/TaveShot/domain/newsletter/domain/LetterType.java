package com.api.TaveShot.domain.newsletter.domain;

import com.api.TaveShot.global.exception.ApiException;
import com.api.TaveShot.global.exception.ErrorType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LetterType {


    DEV_LETTER("개발 뉴스레터", 0),
    EMPLOYEE_LETTER("구인구직 뉴스레터", 1),
    ALL("위 레터 모두", 2),
    NONE("구독하지 않음", 3)
    ;

    private final String title;
    private final int code;

    public static LetterType findLetterTypeByValue(final String value) {
        try {
            return LetterType.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ApiException(ErrorType.LETTER_TYPE_NOT_FOUND);
        }
    }
}
