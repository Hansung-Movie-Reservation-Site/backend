package com.springstudy.backend.Common.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springstudy.backend.API.Auth.Model.Response.AccountResponse.LoginResponse;
import com.springstudy.backend.API.Auth.Model.UserDetailDTO;
import com.springstudy.backend.Common.ErrorCode.CustomException;
import com.springstudy.backend.Common.ErrorCode.ErrorCode;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class JsonResponseUtil {
    private final ObjectMapper objectMapper;

    public String success(UserDetailDTO userDetailDTO ) {
        try {
            return objectMapper.writeValueAsString(
                    Map.of(
                            "code", 200,
                            "data", userDetailDTO
                    )
            );
        } catch (JsonProcessingException e) {
            throw new CustomException(ErrorCode.JSON_PROCESSOR_ERROR);
        }
    }

    public String error(int code, ErrorCode errorCode) {
        try {
            return objectMapper.writeValueAsString(
                    Map.of(
                            "code", code,
                            "data", errorCode
                    )
            );
        } catch (JsonProcessingException e) {
            throw new CustomException(ErrorCode.JSON_PROCESSOR_ERROR);
        }
    }
    public String error(int code, String errorCode) {
        try {
            return objectMapper.writeValueAsString(
                    Map.of(
                            "code", code,
                            "data", errorCode
                    )
            );
        } catch (JsonProcessingException e) {
            throw new CustomException(ErrorCode.JSON_PROCESSOR_ERROR);
        }
    }
}