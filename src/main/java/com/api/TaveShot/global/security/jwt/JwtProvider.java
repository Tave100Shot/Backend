package com.api.TaveShot.global.security.jwt;

import static com.api.TaveShot.global.constant.OauthConstant.ACCESS_TOKEN_VALID_TIME;
import static com.api.TaveShot.global.exception.ErrorType._JWT_EXPIRED;
import static com.api.TaveShot.global.exception.ErrorType._JWT_PARSING_ERROR;
import static com.api.TaveShot.global.exception.ErrorType._USER_NOT_FOUND_BY_TOKEN;

import com.api.TaveShot.domain.Member.domain.Member;
import com.api.TaveShot.domain.Member.domain.Role;
import com.api.TaveShot.domain.Member.repository.MemberRepository;
import com.api.TaveShot.global.exception.ApiException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtProvider {

    private final MemberRepository memberRepository;

    @Value("${jwt.secret.key}")
    private String SECRET_KEY;

    @Value("${jwt.expiration.time.minutes}")
    private int emailTokenExpirationMinutes;

    public String generateJwtToken(final String id) {
        Claims claims = createClaims(id);
        Date now = new Date();
        long expiredDate = calculateExpirationDate(now);
        SecretKey secretKey = generateKey();

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(expiredDate))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateJwtTokenForEmail(final String id, int minutesUntilExpiration) {
        Claims claims = createClaims(id);
        Date now = new Date();
        long expiredDate = now.getTime() + minutesUntilExpiration * 60 * 1000;
        SecretKey secretKey = generateKey();

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(expiredDate))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .signWith(generateKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // JWT claims 생성
    private Claims createClaims(final String id) {
        return Jwts.claims().setSubject(id);
    }

    // JWT 만료 시간 계산
    private long calculateExpirationDate(final Date now) {
        return now.getTime() + ACCESS_TOKEN_VALID_TIME;
    }

    // Key 생성
    private SecretKey generateKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
    }

    // 토큰의 유효성 검사
    public void isValidToken(final String jwtToken) {

        log.info("JwtProvider.isValidToken");
        log.info("jwtToken = " + jwtToken);

        try {
            SecretKey key = generateKey();
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(jwtToken);

        } catch (ExpiredJwtException e) { // 어세스 토큰 만료
            throw new ApiException(_JWT_EXPIRED);
        } catch (Exception e) {
            throw new ApiException(_JWT_PARSING_ERROR);
        }
    }

    // jwtToken 으로 Authentication 에 사용자 등록
    public void getAuthenticationFromToken(final String jwtToken) {

        log.info("--------------------------------------------");
        log.info("JwtProvider.getAuthenticationFromToken");
        log.info("jwtToken = " + jwtToken);


        log.info("-------------- getAuthenticationFromToken jwt token: " + jwtToken);
        Member loginMember = getGitLoginId(jwtToken);
        // setContextHolder 메서드 내에서 로그 추가
        log.debug("Setting SecurityContext with Member: {}", loginMember);

        setContextHolder(jwtToken, loginMember);
    }

    // token 으로부터 유저 정보 확인
    private Member getGitLoginId(final String jwtToken) {
        Long userId = Long.valueOf(getUserIdFromToken(jwtToken));
        return memberRepository.findByGitId(userId)
                .orElseThrow(() -> new ApiException(_USER_NOT_FOUND_BY_TOKEN));
    }

    private void setContextHolder(String jwtToken, Member loginMember) {

        Role role = loginMember.getRole();

        List<GrantedAuthority> authorities = new ArrayList<>();
        // Role을 GrantedAuthority로 변환하여 목록에 추가
        authorities.add(new SimpleGrantedAuthority("ROLE_" + role.name()));

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginMember, jwtToken, authorities);

        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }

    // 토큰에서 유저 아이디 얻기
    public String getUserIdFromToken(final String jwtToken) {
        SecretKey key = generateKey();

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(jwtToken)
                .getBody();

        log.info("-------------- JwtProvider.getUserIdFromAccessToken: " + claims.getSubject());
        return claims.getSubject();
    }
}
