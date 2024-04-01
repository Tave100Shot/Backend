package com.api.TaveShot.domain.news_letter.main.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NewsLetterResponseDto {
    private Long id;
    private String nickname;
    private String email;
    private String subscriptionType;

    @Builder
    public NewsLetterResponseDto(Long id, String nickname, String email,String subscriptionType){
        this.id = id;
        this.nickname = nickname;
        this.email = email;
        this.subscriptionType = subscriptionType;
    }
}
