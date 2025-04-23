package com.springstudy.backend.API.OAuth;

import com.springstudy.backend.API.Repository.Entity.User;
import com.springstudy.backend.API.Repository.Entity.UserCredentional;
import com.springstudy.backend.API.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

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
        String provider = oAuth2UserInfo.getProvider();
        String providerId = oAuth2UserInfo.getProviderId();
        String email = oAuth2UserInfo.getProviderEmail();
        String loginId = provider + "_" + providerId;
        String username = oAuth2UserInfo.getProviderName();
        String password = passwordEncoder.encode("겟인데어");

        //Member member = memberRepository.findByLoginId(loginId).orElse(null);

        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            User user = new User().builder()
                    .username(username)
                    .email(email)
                    .build();
            UserCredentional userCredentional = new UserCredentional().builder()
                    .user(user)
                    .password(password)
                    .build();
            user.setUserCredentional(userCredentional);
            userRepository.save(user);

//            member = Member.builder()
//                    .loginId(loginId)
//                    .password(password)
//                    .email(email)
//                    .username(username)
//                    .role(role)
//                    .provider(provider)
//                    .providerId(providerId)
//                    .build();
//            memberRepository.save(member);
        } else {
            System.out.println("이미 로그인을 한적이 있습니다.");
        }

//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        AuthUser authUser = (AuthUser) authentication.getPrincipal();
//        System.out.println(authUser.getEmail());

        return new PrincipalDetails(userOptional.get(), oAuth2User.getAttributes());
    }
}
