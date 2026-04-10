package com.quiz.config.auth;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.net.URLEncoder;

@Component
public class CustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private final String loginUrl;

    public CustomAuthenticationFailureHandler() {
        this.loginUrl = "/login";
    }

    public CustomAuthenticationFailureHandler(String loginUrl) {
        this.loginUrl = loginUrl;
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception
    ) throws IOException, ServletException {

        String errorMessage = "Email və ya şifrə yanlışdır!";

        if (exception.getCause() instanceof DisabledException || exception instanceof DisabledException) {
            errorMessage = "Hesabınız təsdiqlənməyib. Zəhmət olmasa emailinizi yoxlayın.";
        }

        setDefaultFailureUrl(loginUrl + "?error=true&message=" + URLEncoder.encode(errorMessage, "UTF-8"));
        super.onAuthenticationFailure(request, response, exception);
    }
}