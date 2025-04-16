package com.springstudy.backend.API.Auth.Model;

import com.springstudy.backend.API.Repository.Entity.MyTheather;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class UserDetailDTO {
    private Long user_id;

    private String username;

    private String email;

    private List<MyTheather> myTheatherList;

    public UserDetailDTO(Long user_id, String username, String email, List<MyTheather> myTheatherList) {
        this.user_id = user_id;
        this.username = username;
        this.email = email;
        this.myTheatherList = myTheatherList;
    }
}
