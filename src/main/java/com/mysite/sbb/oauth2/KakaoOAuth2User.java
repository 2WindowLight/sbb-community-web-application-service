package com.mysite.sbb.oauth2;

import java.util.Collection;
import java.util.Map;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;
@Getter
public class KakaoOAuth2User implements OAuth2User  {
    private final OAuth2User oauth2User;
    private final String username;
    private final String profileImage;
    private final String accessToken;
    private final String provider;
    public KakaoOAuth2User(OAuth2User oauth2User, String username, String profileImage, String accessToken, String provider) {
        this.oauth2User = oauth2User;
        this.username = username;
        this.profileImage = profileImage;
        this.accessToken = accessToken;
        this.provider = provider;
    }
    @Override
    public Map<String, Object> getAttributes() {
        return oauth2User.getAttributes();
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return oauth2User.getAuthorities();
    }
    @Override
    public String getName() {
        return username;
    }

}
