package com.api.TaveShot.domain.Member.dto.response;

import com.api.TaveShot.domain.Member.domain.Member;
import com.api.TaveShot.domain.newsletter.client.domain.Subscription;
import com.api.TaveShot.domain.newsletter.domain.LetterType;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "개별 멤버의 상세 정보")
public record MemberSingleResponse(
        @Schema(description = "백준 이름", example = "bojUser123")
        String bojName,

        @Schema(description = "GitHub 이름, 회원가입할 때의 유일한 id를 뜻함. 변경할 수 없는 ID", example = "toychip")
        String gitLoginId,

        @Schema(description = "구독 상태", example = "true")
        boolean subStatus,

        @Schema(description = "구독종류", example = "DEV_LETTER")
        LetterType letterType,

        @Schema(description = "2차 인증 여부", example = "true")
        boolean secondCertified,

        @Schema(description = "이메일 주소", example = "user@example.com")
        String email
) {

    public static MemberSingleResponse from(Member member) {
        String bojName = getBojName(member.getBojName());
        String gitLoginId = getGitLoginId(member.getGitLoginId());
        LetterType letterType = getLetterType(member.getSubscription());
        String email = getEmail(member.getGitEmail());
        boolean subStatus = getStatus(letterType);
        boolean secondCertified = getSecondCertified(member.getBojName());
        return new MemberSingleResponse(bojName, gitLoginId, subStatus, letterType, secondCertified, email);
    }

    private static String getGitLoginId(final String gitLoginId) {
        if (gitLoginId == null || gitLoginId.isBlank()) {
            return "x";
        }
        return gitLoginId;
    }

    private static String getBojName(String bojName) {
        if (bojName == null || bojName.isBlank()) {
            return "x";
        }
        return bojName;
    }

    /*
    구독여부 "일반회원" or "구독회원",
    구독여부 "√" or "X"
    위 2개 중 구독여부 1번만 보냄
     */
    private static boolean getStatus(LetterType letterType) {
        if (letterType.equals(LetterType.NONE)) {
            return false;
        }
        return true;
    }

    private static LetterType getLetterType(Subscription subscription) {
        if (subscription == null) {
            return LetterType.NONE;
        }
        return subscription.getLetterType();
    }

    private static boolean getSecondCertified(String bojName) {
        if (bojName == null || bojName.isBlank()) {
            return false;
        }
        return true;
    }

    private static String getEmail(String email) {
        if (email == null || email.isBlank()) {
            return "x";
        }
        return email;
    }
}
