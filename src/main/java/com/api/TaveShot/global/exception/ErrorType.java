package com.api.TaveShot.global.exception;

import static org.springframework.http.HttpStatus.*;

import lombok.Getter;
import lombok.ToString;
import org.springframework.http.HttpStatus;

@Getter
@ToString
public enum ErrorType {

    /**
     * Error Message Convention
     *
     * name : _(head) + Error Name status : HttpStatus
     *
     * errorCode : 400번 오류인 상황이 여러개 올텐데, 4001, 4002, 4003.. 이런식으로 설정 (해당 오류들은 MEMBER 와 관련된 400 오류들)
     * ex) Member Error, Http Status Code: 400 -> MEMBER_4000
     *
     * message : 사람이 알아볼 수 있도록 작성
     * ex) "인증이 필요합니다."
     */

    // ------------------------------------------ SERVER ------------------------------------------
    _CANT_TRANCE_INSTANCE(INTERNAL_SERVER_ERROR, "SERVER_5000", "상수는 인스턴스화 할 수 없습니다."),
    _SERVER_USER_NOT_FOUND(INTERNAL_SERVER_ERROR, "SERVER_5001", "로그인이 성공한 소셜 로그인 유저가 DB에 존재하지 않을 수 없습니다."),



    // ---------------------------------------- JWT TOKEN ----------------------------------------
    _JWT_PARSING_ERROR(BAD_REQUEST, "JWT_4001", "JWT Token이 올바르지 않습니다."),
    _JWT_EXPIRED(UNAUTHORIZED, "JWT_4010", "Jwt Token의 유효 기간이 만료되었습니다."),


    // ------------------------------------------ PAGING ------------------------------------------
    _PAGING_INVALID_PAGE_SIZE(BAD_REQUEST, "PAGING_4001", "게시글 사이즈가 너무 큽니다. 최대 100개까지 조회 가능합니다."),
    _PAGING_INVALID_PAGE_NUMBER(BAD_REQUEST, "PAGING_4002", "게시글 사이즈가 너무 큽니다. 최대 1000 페이지까지 조회 가능합니다."),
    _PAGING_INVALID_DATA_SIZE(BAD_REQUEST, "PAGING_4003", "요청된 페이지 번호가 데이터 범위를 초과합니다."),



    // ------------------------------------------ POST ------------------------------------------
    _POST_INVALID_TIER(BAD_REQUEST, "POST_4000", "게시글의 등급이 잘못되었습니다."),
    _POST_NOT_FOUND(NOT_FOUND, "POST_4040", "해당 게시글이 존재하지 않습니다."),
    _POST_USER_FORBIDDEN(FORBIDDEN, "POST_4030", "해당 게시글에 접근 권한이 없습니다."),


    // ------------------------------------------ COMMENT ------------------------------------------
    _COMMENT_NOT_FOUND(NOT_FOUND, "POST_4040", "해당 게시글이 존재하지 않습니다."),

    // ------------------------------------------ S3 ------------------------------------------
    EXCEEDING_FILE_COUNT(BAD_REQUEST, "S4001", "사진 개수가 너무 많습니다."),
    S3_UPLOAD(INTERNAL_SERVER_ERROR, "S5001", "서버오류, S3 사진 업로드 에러입니다."),
    S3_CONNECT(INTERNAL_SERVER_ERROR, "S5002", "서버오류, S3 연결 에러입니다."),
    S3_CONVERT(INTERNAL_SERVER_ERROR, "S5003", "서버오류, S3 변환 에러입니다."),

    // ------------------------------------------ USER ------------------------------------------
    _USER_NOT_FOUND_BY_TOKEN(NOT_FOUND, "USER_4040", "제공된 토큰으로 사용자를 찾을 수 없습니다."),
    _UNAUTHORIZED(UNAUTHORIZED, "USER_4010", "로그인되지 않은 상태입니다."),
    _USER_NOT_FOUND_DB(NOT_FOUND, "USER_4041", "존재하지 않는 회원입니다."),

    // ------------------------------------------ Web Client ------------------------------------------
    _WEB_CLIENT_ERROR(INTERNAL_SERVER_ERROR, "WEBCLIENT_5001", "WebClient 요청 중 오류가 발생했습니다. 이는 서버 측의 문제일 수도 있고, 네트워크 문제 또는 요청의 잘못된 형식 등 다른 이유일 수 있습니다."),


    // ------------------------------------------ Solved API ------------------------------------------
    _SOLVED_INTERNAL_SERVER_ERROR(INTERNAL_SERVER_ERROR, "SOLVED_5001", "Solved API 서버에서 내부 오류가 발생했습니다. 이는 서버 측의 문제로, 일시적인 오류일 수 있으므로 잠시 후 다시 시도해 보시기 바랍니다."),
    _SOLVED_INVALID_REQUEST(BAD_REQUEST, "SOLVED_4001", "잘못된 요청입니다. 전송된 요청의 형식, 값, 파라미터 등이 Solved API의 요구 사항을 충족하지 못했습니다."),
    _SOLVED_NOT_FOUND(NOT_FOUND, "SOLVED_4041", "Solved API에서 해당 사용자를 찾을 수 없습니다. 사용자 식별자가 잘못되었거나 존재하지 않는 사용자를 참조한 경우 발생합니다."),


    // ------------------------------------------ GITHUB API ------------------------------------------
    _GITHUB_NAME_NOT_MATCH(BAD_REQUEST, "GITHUB_4000", "solvedApi 자기소개에 저장된 Github 이름과 본인 GitHubId와 다릅니다."),
    _GITHUB_REPO_INVALID(BAD_REQUEST, "GITHUB_4001", "Github 인증 Repository description이 잘못되었습니다."),
    _GITHUB_REPO_NOT_FOUND(NOT_FOUND, "GITHUB_4040", "요청한 GitHub 리포지토리를 찾을 수 없습니다."),
    _GITHUB_DESCRIPTION_NOT_FOUND(NOT_FOUND, "GITHUB_4041", "GitHub 리포지토리의 설명을 찾을 수 없습니다."),
    _GITHUB_SERVER_ERROR(INTERNAL_SERVER_ERROR, "GITHUB_5000", "GitHub API 서버에서 오류가 발생했습니다."),
    _GITHUB_API_ERROR(INTERNAL_SERVER_ERROR, "GITHUB_5001", "GitHub API 요청 중 일반 오류가 발생했습니다."),


    // ---------------------------------------- problem ---------------------------------------------------

    _PROBLEM_NOT_FOUND(BAD_REQUEST, "PROBLEM_4001", "존재하지 않는 문제입니다."),
    _PROBLEM_NO_SOLUTION(BAD_REQUEST, "PROBLEM_4002", "솔루션이 존재하지 않습니다."),

    // ------------------------------------------ Compile ------------------------------------------
    _COMPILE_PROBLEM_NOT_FOUND(NOT_FOUND, "PROBLEM_4040", "요청한 문제 번호를 찾을 수 없습니다."),
    _SUBMIT_PAGE_NOT_FOUND(NOT_FOUND, "COMPILE_4040", "아직 정답이 준비 되지 않아 코드를 제출할 수 없습니다."),
    _PROBLEM_CONVERSION_ERROR(INTERNAL_SERVER_ERROR, "PROBLEM_5002", "문제 정보 변환 중 오류가 발생했습니다."),
    _ACCESS_DENIED(FORBIDDEN, "ACCESS_4031", "권한이 없습니다."),

    // ------------------------------------------ NewsLetter ------------------------------------------
    _SUBSCRIPTION_ALREADY_EXIST(NOT_FOUND, "LETTER_4040", "이미 구독 중인 이메일과 구독 유형입니다."),
    LETTER_TYPE_NOT_FOUND(NOT_FOUND, "LETTER_4041", "뉴스레터 타입이 잘못됐습니다."),
    NEWSLETTER_NOT_FOUND(NOT_FOUND, "LETTER_4042", "뉴스레터를 찾을 수 없습니다."),
    _EMAIL_OR_NICKNAME_NOT_FOUND(NOT_FOUND,"LETTER_4043", "이메일 혹은 닉네임이 등록되어 있지 않습니다."),
    _INVALID_INPUT(BAD_REQUEST,"LETTER_4044", "gitEmail과 bojName은 null일 수 없습니다."),

    // ---------------------------------------------- Email ---------------------------------------------
    _INVALID_VERIFICATION_LINK(BAD_REQUEST,"EAMIL4040","유효하지 않은 인증 링크입니다."),
    _EMAIL_NOT_VERIFIED(BAD_REQUEST,"EAMIL4041","인증되지 않은 이메일입니다."),
    _INVALID_EMAIL_TOKEN(BAD_REQUEST,"EAMIL4042","유효하지 않은 이메일 토큰입니다."),
    _EMAIL_ALREADY_VERIFIED(BAD_REQUEST,"EAMIL4043","이메일 인증이 이미 완료됐습니다."),
    _EXPIRED_EMAIL_TOKEN(UNAUTHORIZED,"EAMIL4010","메일 토큰 유효기간이 만료됐습니다." ),
    _EMAIL_SEND_FAILED(INTERNAL_SERVER_ERROR,"EAMIL5000","메일 전송에 실패했습니다." ),
    _TEMPLATE_READ_FAILED(INTERNAL_SERVER_ERROR,"EAMIL5001","템플릿 변환에 실패했습니다." ),

    // ---------------------------------------------- static ---------------------------------------------
    _STATIC_ERROR_RUNTIME_EXCEPTION(INTERNAL_SERVER_ERROR, "SERVER_500", "알 수 없는 서버 에러입니다."),

    ;
    private final HttpStatus status;
    private final String errorCode;
    private final String message;

    ErrorType(HttpStatus status, String errorCode, String message) {
        this.status = status;
        this.errorCode = errorCode;
        this.message = message;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getMessage() {
        return message;
    }
}
