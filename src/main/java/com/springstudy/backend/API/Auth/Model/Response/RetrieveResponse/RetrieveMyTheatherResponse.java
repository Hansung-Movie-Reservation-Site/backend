package com.springstudy.backend.API.Auth.Model.Response.RetrieveResponse;

import com.springstudy.backend.API.Auth.Model.RetrieveResponse;
import com.springstudy.backend.API.Repository.Entity.MyTheather;
import com.springstudy.backend.Common.ErrorCode.ErrorCode;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record RetrieveMyTheatherResponse(
        @NotBlank
        ErrorCode errorCode,
        List<MyTheather> myTheatherList
) implements RetrieveResponse {
}
