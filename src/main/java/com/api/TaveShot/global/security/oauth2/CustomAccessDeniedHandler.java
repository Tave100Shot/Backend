package com.api.TaveShot.global.security.oauth2;

import com.api.TaveShot.global.exception.ErrorType;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {
        // 권한 부족으로 인한 예외 처리
        ErrorType errorType = ErrorType._ACCESS_DENIED; // 권한 부족에 해당하는 ErrorType
        setResponse(response, errorType);
    }

    private void setResponse(HttpServletResponse response, ErrorType errorType) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        int status = errorType.getStatus().value();
        response.setStatus(status);

        response.getWriter().println(
                "{\"status\" : \"" + status + "\"," +
                        "\"errorCode\" : \"" + errorType.getErrorCode() + "\"," +
                        "\"message\" : \"" + errorType.getMessage() + "\"}");
    }
}
