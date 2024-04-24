//package com.api.TaveShot.domain.news_letter.service;
//
//import com.api.TaveShot.domain.news_letter.domain.NewsLetter;
//import com.api.TaveShot.domain.news_letter.dto.NewsLetterRequestDto;
//import com.api.TaveShot.domain.news_letter.repository.NewsLetterRepository;
//import com.api.TaveShot.global.exception.ApiException;
//import com.api.TaveShot.global.exception.ErrorType;
//import org.springframework.stereotype.Service;
//
//import java.util.Optional;
//
//@Service
//public class NewsLetterService {
//    private final NewsLetterRepository newsLetterRepository;
//
//    public NewsLetterService(NewsLetterRepository newsLetterRepository) {
//        this.newsLetterRepository = newsLetterRepository;
//    }
//
//
//    public Long subscribe(NewsLetterRequestDto requestDto) {
//        Optional<NewsLetter> existingSubscription = newsLetterRepository
//                .findByEmailAndSubscriptionType(requestDto.getEmail(), requestDto.getSubscriptionType());
//
//        if (existingSubscription.isPresent()) {
//            throw new ApiException(ErrorType._SUBSCRIPTION_ALREADY_EXIST);
//        }
//
//        NewsLetter newsLetter = newsLetterRepository.save(NewsLetter.builder()
//                .nickname(requestDto.getNickname())
//                .email(requestDto.getEmail())
//                .subscriptionType(requestDto.getSubscriptionType())
//                .build());
//        return newsLetter.getId();
//    }
//
////    public void sendMonthlyNewsLetters(){
////        Iterable<NewsLetter> newsLetters = newsLetterRepository.findAll();
////        for (NewsLetter newsLetter : newsLetters) {
////            sendEmail(newsLetter.getEmail(), "......."); //내용 발송은 추후에 결정.
////        }
////    }
////
////    private void sendEmail(String nickname, String email) {
////    }
//}
