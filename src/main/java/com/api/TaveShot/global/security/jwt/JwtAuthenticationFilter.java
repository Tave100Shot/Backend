package com.api.TaveShot.global.security.jwt;

import com.api.TaveShot.global.exception.ApiException;
import com.api.TaveShot.global.security.oauth2.CustomAuthenticationEntryPoint;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;

    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response,
                                    final FilterChain filterChain) throws ServletException, IOException {

        String requestURI = request.getRequestURI();
        if (isPublicUri(requestURI)) {
            // Public uri 일 경우 검증 안함
            filterChain.doFilter(request, response);
            return;
        }

        log.info("-----   JWT filter do FilterInternal !!!!");
        String authorizationHeader = request.getHeader("Authorization");
        log.info("------- authorizationHeader = " + authorizationHeader);

        if (authorizationHeader != null && isBearer(authorizationHeader)) {
            try {
                // "Bearer " 이후의 문자열을 추출
                String jwtToken = authorizationHeader.substring(7);

                // token 단순 유효성 검증
                jwtProvider.isValidToken(jwtToken);

                // token을 활용하여 유저 정보 검증
                jwtProvider.getAuthenticationFromToken(jwtToken);
             } catch (ApiException e) {
                authenticationEntryPoint.commence(request, response, new AuthenticationException(e.getMessage(), e) {});
                return;
            }
        }

        filterChain.doFilter(request, response);

    }

    private boolean isBearer(final String authorizationHeader) {
        return authorizationHeader.startsWith("Bearer ");
    }

    private boolean isPublicUri(final String requestURI) {
        return
                requestURI.startsWith("/swagger-ui/**") ||
                requestURI.startsWith("/api/health") ||
                requestURI.startsWith("/favicon.ico") ||
                requestURI.startsWith("/api/v1/search/**") ||
                requestURI.startsWith("/api/newsletter/**") ||
                requestURI.startsWith("/api/compile/**") ||
                requestURI.startsWith("/v3/api-docs/**") ||
                requestURI.startsWith("/error") ||
                requestURI.startsWith("/api/email/verify") ||
                requestURI.startsWith("/login/**");
    }
}
