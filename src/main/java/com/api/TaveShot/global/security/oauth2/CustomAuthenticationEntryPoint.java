package com.api.TaveShot.global.security.oauth2;

import com.api.TaveShot.global.exception.ApiException;
import com.api.TaveShot.global.exception.ErrorType;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    // 인증되지 않은 사용자가 보호된 리소스에 접근하려 할 때 동작

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        if (authException.getCause() instanceof ApiException) {
            ApiException apiException = (ApiException) authException.getCause();
            ErrorType errorType = apiException.getErrorType();
            setResponse(response, errorType);
            return;
        }

        ApiException apiException = new ApiException(ErrorType._JWT_PARSING_ERROR);
        ErrorType errorType = apiException.getErrorType();
        setResponse(response, errorType);
    }

    private void setResponse(HttpServletResponse response, ErrorType errorType) throws IOException {
        response.setContentType("application/json;charset=UTF-8");

        // Spring 5 이상에서는 HttpStatus enum을 사용하여 상태 코드를 가져옴
        int status = errorType.getStatus().value();
        response.setStatus(status);

        response.getWriter().println(
                "{\"status\" : \"" + status + "\"," +
                        "\"errorCode\" : \"" + errorType.getErrorCode() + "\"," +
                        "\"message\" : \"" + errorType.getMessage() + "\"}");
    }
}
