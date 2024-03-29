package com.api.TaveShot.domain.search.service;

import static com.api.TaveShot.global.util.NumberValidator.extractNumberFromBojString;

import com.api.TaveShot.domain.recommend.repository.ProblemElementRepository;
import com.api.TaveShot.domain.search.dto.GoogleItemDto;
import com.api.TaveShot.domain.search.dto.GoogleItemDtos;
import com.api.TaveShot.domain.search.dto.GoogleListResponseDto;
import com.api.TaveShot.domain.search.dto.GoogleResponseDto;
import com.api.TaveShot.global.exception.ApiException;
import com.api.TaveShot.global.exception.ErrorType;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;


@Transactional
@RequiredArgsConstructor
@Slf4j
@Service
public class SearchService {

    private final ProblemElementRepository problemElementRepository;
    @Value("${google.secret.key}")
    private String KEY;
    @Value("${google.secret.cx}")
    private String CX;

    public GoogleItemDtos findBlog(String query, int index) {

        Long questionNumber = extractNumberFromBojString(query);

        problemElementRepository.findByProblemId(questionNumber)
                .orElseThrow(() -> new ApiException(ErrorType._PROBLEM_NOT_FOUND));

        WebClient webClient = WebClient.builder()
                .baseUrl("https://www.googleapis.com/customsearch/v1")
                .build();

        List<GoogleItemDto> googleResponseDtos = new ArrayList<>();
        CountDownLatch latch = new CountDownLatch(1);

        for(int i=1;i<=2;i++) {
            GoogleResponseDto one = new GoogleResponseDto();

            int position = i;
            GoogleResponseDto dto = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .queryParam("key", KEY)
                            .queryParam("cx", CX)
                            .queryParam("q", query)
                            .queryParam("start", position)
                            .build())
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(GoogleResponseDto.class)
                    .block();

            if(dto == null)
                throw new ApiException(ErrorType._PROBLEM_NO_SOLUTION);

            for (GoogleItemDto googleItemDto : dto.getItems()) {
                int check = 0;
                googleItemDto.modifyBlog(googleItemDto.getLink());
                System.out.println(googleItemDto.getLink());
                if(isRelated(googleItemDto.getTitle(), String.valueOf(questionNumber)))
                    for(GoogleItemDto googleItemDto2 : googleResponseDtos){
                        if((googleItemDto.getLink()).equals(googleItemDto2.getLink())) {
                            check = 1;
                            break;
                        }
                    }
                if(check==0)
                    googleResponseDtos.add(googleItemDto);
            }

            if(i==1){
                String str = googleResponseDtos.get(0).getTitle();
                if(!isRelated(str, String.valueOf(questionNumber)))
                    throw new ApiException(ErrorType._PROBLEM_NO_SOLUTION);
            }
        }


        return GoogleItemDtos.builder().dtos(googleResponseDtos).build();

    }

    public static boolean isRelated(String str, String pattern){
        if (str.contains(pattern)) {
            return true;
        } else {
            return false;
        }
    }


}
