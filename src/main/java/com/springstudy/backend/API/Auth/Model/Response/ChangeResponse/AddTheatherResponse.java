package com.springstudy.backend.API.Auth.Model.Response.ChangeResponse;

import com.springstudy.backend.API.Auth.Model.Response.Response;
import com.springstudy.backend.API.Repository.Entity.MyTheather;
import com.springstudy.backend.Common.ErrorCode.ErrorCode;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.util.List;

public record AddTheatherResponse(
        @NotNull
        ErrorCode code,

        List<MyTheather> myTheatherList

) implements Response {
}
