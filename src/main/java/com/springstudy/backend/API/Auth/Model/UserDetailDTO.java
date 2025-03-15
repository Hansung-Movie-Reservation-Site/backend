package com.springstudy.backend.API.Auth.Model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserDetailDTO {
    private Long user_id;

    private String username;

    private String email;
    public UserDetailDTO(Long user_id, String username, String email) {
        this.user_id = user_id;
        this.username = username;
        this.email = email;
    }
}
