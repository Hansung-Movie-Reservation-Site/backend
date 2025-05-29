package com.springstudy.backend.API.Auth.Model;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.ArrayList;
import java.util.List;


@Getter
public class AuthUser extends User {
    private String email;

    public AuthUser(String username, String password,
                      List<GrantedAuthority> authorities,
                      String email){
        super(username, password, authorities);
        this.email = email;
    }
    public AuthUser(String username){
        super(username, "", new ArrayList<>());
    }

}
//CustomUser 클래스가 User 클래스를 상속받고 있기 때문에,
//Lombok이 자동으로 빌더를 생성하는데 있어 super 생성자를 호출하는 부분에서 문제가 발생할 수 있습니다.
//
//        @Builder 어노테이션을 사용하려면, 부모 클래스인 User 클래스에 있는
//생성자도 빌더를 통해 접근할 수 있어야 합니다. 이를 해결하기 위해,
//CustomUser의 빌더를 직접 정의하고, 부모 클래스의 생성자도 반영해야 합니다.