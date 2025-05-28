package com.springstudy.backend.API.Auth.Model;

import com.springstudy.backend.API.Repository.Entity.MyTheater;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class UserDetailDTO {
    private Long user_id;

    private String username;

    private String email;

    private List<MyTheater> myTheaterList;

    public UserDetailDTO(Long user_id, String username, String email, List<MyTheater> myTheaterList) {
        this.user_id = user_id;
        this.username = username;
        this.email = email;
        this.myTheaterList = myTheaterList;
    }
}
