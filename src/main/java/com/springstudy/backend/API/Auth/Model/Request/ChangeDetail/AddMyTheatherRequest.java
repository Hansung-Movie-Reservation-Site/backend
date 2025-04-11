package com.springstudy.backend.API.Auth.Model.Request.ChangeDetail;

import com.springstudy.backend.API.Repository.Entity.Spot;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record AddMyTheatherRequest(
        @NotNull
        Long user_id,

        List<Long> mySpotList
) {
}
