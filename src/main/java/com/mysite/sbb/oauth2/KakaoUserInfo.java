package com.mysite.sbb.oauth2;
import java.util.Map;
import lombok.Getter;

    @Getter
    public class KakaoUserInfo {
        private Long id;
        private String nickname;
        private String profileImage;
        public KakaoUserInfo(Map<String, Object> attributes) {
            this.id = (Long) attributes.get("id");
            Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
            if (kakaoAccount != null) {
                Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
                if (profile != null) {
                    this.nickname = (String) profile.get("nickname");
                    this.profileImage = (String)
                            profile.get("profile_image_url");
                }
            }
        }
    }

