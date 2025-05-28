package com.springstudy.backend.API.Auth.Model.Response.RetrieveResponse;

import com.springstudy.backend.API.Auth.Model.RetrieveResponse;
import com.springstudy.backend.API.Repository.Entity.MyTheater;
import com.springstudy.backend.Common.ErrorCode.ErrorCode;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record RetrieveMyTheaterResponse(
        @NotBlank
        ErrorCode errorCode,
        List<MyTheater> myTheaterList
) implements RetrieveResponse {
}
