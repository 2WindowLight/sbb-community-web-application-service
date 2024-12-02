package com.mysite.sbb.oauth2;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class KakaoLogoutSuccessHandler implements LogoutSuccessHandler{
    @Value("${kakao.logout-url}")
    private String kakaoLogoutUrl;
    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String clientId;
    @Value("${kakao.logout-redirect-uri}")
    private String logoutRedirectUri;

    @Override
    public void onLogoutSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof KakaoOAuth2User) {
                String kakaoLogoutRedirect = UriComponentsBuilder.fromHttpUrl(kakaoLogoutUrl)
                        .queryParam("client_id", clientId)
                        .queryParam("logout_redirect_uri", logoutRedirectUri)
                        .build().toUriString();
                response.sendRedirect(kakaoLogoutRedirect);
                return;
            }
        }
        response.sendRedirect("/");
    }


}
