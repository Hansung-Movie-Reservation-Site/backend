package com.springstudy.backend.API.Auth.Model.Response.ChangeResponse;

import com.springstudy.backend.API.Auth.Model.Response.Response;
import com.springstudy.backend.API.Repository.Entity.MyTheater;
import com.springstudy.backend.Common.ErrorCode.ErrorCode;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record AddTheaterResponse(
        @NotNull
        ErrorCode code,

        List<MyTheater> myTheaterList

) implements Response {
}
