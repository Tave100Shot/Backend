package com.api.TaveShot.global.security.oauth2;

import static com.api.TaveShot.global.constant.OauthConstant.ADMIN_REDIRECT_URL;
import static com.api.TaveShot.global.constant.OauthConstant.REDIRECT_URL;
import static com.api.TaveShot.global.exception.ErrorType._SERVER_USER_NOT_FOUND;

import com.api.TaveShot.domain.Member.domain.Member;
import com.api.TaveShot.domain.Member.domain.Role;
import com.api.TaveShot.domain.Member.dto.response.AuthResponse;
import com.api.TaveShot.domain.Member.repository.MemberRepository;
import com.api.TaveShot.global.exception.ApiException;
import com.api.TaveShot.global.security.jwt.JwtProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomOAuthSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final MemberRepository memberRepository;
    private final JwtProvider jwtProvider;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        CustomOauth2User oauth2User = (CustomOauth2User) authentication.getPrincipal();
        GithubUserInfo githubUserInfo = createGitHubUserInfo(oauth2User);

        if(response.isCommitted()) {
            log.debug("------------------ Response 전송 완료");
        }

        String loginId = githubUserInfo.getLoginId();

        log.info("------------------ 소셜 로그인 성공: " + loginId);

        Integer id = githubUserInfo.getId();
        log.info("------------------ id = " + id);

        String mail = githubUserInfo.getMail();
        String profileImageUrl = githubUserInfo.getProfileImageUrl();

        Member loginMember = memberRepository.findByGitId(Long.valueOf(id)).orElseThrow(
                () -> new ApiException(_SERVER_USER_NOT_FOUND));
        String loginMemberId = String.valueOf(loginMember.getGitId());
        String token = generateToken(loginMemberId);
        String gitLoginId = loginMember.getGitLoginId();

        Role role = loginMember.getRole();
        boolean isAdmin = isAdmin(role);

        // 2차 인증 여부 확인
        boolean secondAuth = getSecondAuth(loginMember.getBojName());

        AuthResponse authResponse = AuthResponse.builder()
                .memberId(loginMember.getId())
                .mail(mail)
                .gitLoginId(gitLoginId)
                .isAdmin(isAdmin)
                .gitProfileImageUrl(profileImageUrl)
                .secondAuth(secondAuth)
                .build();

        // ToDo 아래는 임시 데이터, front와 협의 후 수정
        registerResponse(response, authResponse, token);
    }

    private boolean isAdmin(Role role) {
        if (role.equals(Role.MANAGER)) {
            return true;
        }
        return false;
    }

    private boolean getSecondAuth(String bojName) {
        if (bojName == null) {
            return false;
        }
        return true;
    }

    private GithubUserInfo createGitHubUserInfo(CustomOauth2User oauth2User) {
        Map<String, Object> userInfo = oauth2User.getAttributes();
        return GithubUserInfo.builder()
                .userInfo(userInfo)
                .build();
    }

    private String generateToken(String loginMemberId) {
        String ourToken = jwtProvider.generateJwtToken(loginMemberId);
        log.info("ourToken = " + ourToken);
        return ourToken;
    }

    private void registerResponse(HttpServletResponse response,
                                  AuthResponse authResponse, String token) throws IOException {
        String encodedMemberId = URLEncoder.encode(String.valueOf(authResponse.memberId()), StandardCharsets.UTF_8);
        String encodedLoginId = URLEncoder.encode(authResponse.gitLoginId(), StandardCharsets.UTF_8);
        String encodedGitProfileImageUrl = URLEncoder.encode(authResponse.gitProfileImageUrl(), StandardCharsets.UTF_8);

        boolean isAdmin = authResponse.isAdmin();
            
        // 프론트엔드 페이지로 토큰과 함께 리다이렉트
        String frontendRedirectUrl = getRedirectUrl(isAdmin, authResponse, token, encodedMemberId, encodedLoginId,
                encodedGitProfileImageUrl);
        log.info("Front! redirect url: " + REDIRECT_URL);
        response.sendRedirect(frontendRedirectUrl);
    }

    private String getRedirectUrl(boolean isAdmin, AuthResponse authResponse, String token, String encodedMemberId,
                                    String encodedLoginId, String encodedGitProfileImageUrl) {
        if (isAdmin) {
            log.info("isAdmin! " + ADMIN_REDIRECT_URL);
            return String.format(
                    "%s?token=%s&memberId=%s&gitLoginId=%s&secondAuth=%s&profileImgUrl=%s",
                    ADMIN_REDIRECT_URL, token, encodedMemberId, encodedLoginId, authResponse.secondAuth(),
                    encodedGitProfileImageUrl
            );
        }
        return String.format(
                "%s?token=%s&memberId=%s&gitLoginId=%s&secondAuth=%s&profileImgUrl=%s",
                REDIRECT_URL, token, encodedMemberId, encodedLoginId, authResponse.secondAuth(),
                encodedGitProfileImageUrl
        );
    }

}
