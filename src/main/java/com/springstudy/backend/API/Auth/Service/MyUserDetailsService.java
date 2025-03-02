package com.springstudy.backend.API.Auth.Service;

import com.springstudy.backend.API.Auth.Model.AuthUser;
import com.springstudy.backend.API.Repository.Entity.User;
import com.springstudy.backend.API.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MyUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByUsername(username);
        if(user.isEmpty()){
            //todo error
        }
        User member = user.get();
        List<GrantedAuthority> authorityList = new ArrayList<>();
        authorityList.add(new SimpleGrantedAuthority("일반유저"));
        // User 객체는 권한 정보가 있어야 된다.
        return new AuthUser(member.getUsername(), member.getUser_credentional().getPassword(),
                authorityList, member.getEmail());
        // Authentication 객체와 비교할 대상.
    }
}
//UserDetailsService 인터페이스는 기본적으로 loadUserByUsername 메소드 하나만 정의되어 있습니다.
//이 메소드는 사용자의 이름(주로 이메일이나 사용자 이름)을
//기반으로 UserDetails 객체를 반환하는 역할을 합니다.
//
//UserDetailsService에서 추가적인 메소드는 정의되어 있지 않지만,
//이 인터페이스를 구현한 클래스 내에서는 다른 메소드를 추가하여 사용할 수 있습니다.
//예를 들어, 사용자 인증 외에 추가적인 작업(예: 사용자 권한 조회, 비밀번호 변경 등)을
//처리하기 위한 메소드를 추가할 수 있습니다.