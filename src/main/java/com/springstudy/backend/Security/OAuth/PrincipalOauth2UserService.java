package com.springstudy.backend.Security.OAuth;

import com.springstudy.backend.API.Repository.Entity.User;
import com.springstudy.backend.API.Repository.Entity.UserCredentional;
import com.springstudy.backend.API.Repository.UserRepository;
import com.springstudy.backend.Common.ErrorCode.CustomException;
import com.springstudy.backend.Common.ErrorCode.ErrorCode;
import com.springstudy.backend.Common.util.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    // 후처리
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // 강제회원 가입
        OAuth2UserInfo oAuth2UserInfo = null;
        if (userRequest.getClientRegistration().getRegistrationId().equals("google")) {
            oAuth2UserInfo = new GoogleUserInfo(oAuth2User.getAttributes());
        } else {
            System.out.println("지원하지않음.");
        }

        String email = oAuth2UserInfo.getProviderEmail();
        String username = oAuth2UserInfo.getProviderName();
        String password = passwordEncoder.encode("겟인데어");

        Optional<User> userOptional = userRepository.findByEmail(email);

        User user = null;
        if (userOptional.isEmpty()) {
            user = generatedUser(username, password, email);
            if (user == null) {
                throw new CustomException(ErrorCode.AUTH_SAVE_ERROR);
            }
        } else user = userOptional.get();

        return new PrincipalDetails(user, oAuth2User.getAttributes());
    }

    public User generatedUser(String username, String password, String email) {

        User user = new User().builder()
                .username(username)
                .email(email)
                .myTheatherList(new ArrayList<>())
                .build();
        UserCredentional userCredentional = new UserCredentional().builder()
                .user(user)
                .password(password)
                .build();

        user.setUserCredentional(userCredentional);
        return userRepository.save(user);
    }
}