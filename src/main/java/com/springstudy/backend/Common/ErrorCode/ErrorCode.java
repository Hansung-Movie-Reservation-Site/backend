package com.springstudy.backend.Common.ErrorCode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ErrorCode implements CodeInterface{
    SUCCESS(0,"SUCCESS"),
    USER_ALREADY_EXISTS(-1, "USER_ALREADY_EXISTS"),
    USER_CREATE_FAILED(-2,"USER_CREATE_FAILED"),
    NOT_EXIST_USER(-3, "NOT_EXIST_USER"),
    MISMATCH_PASSWORD(-4, "MISMATCH_PASSWORD"),
    FAILURE(-5,"FAILURE"),
    NOT_LOGIN(-6,"NOT_LOGIN"),
    NUMBER_FORMAT_ERROR(-7,"NUMBER_FORMAT_ERROR"),
    NOT_EXIST_CHALLENGE(-8,"NOT_EXIST_CHALLENGE"),
    ERROR_DATA_ACCESS(-9,"ERROR_DATA_ACCESS"),
    NOT_DELETE_CHALLENGE(-10,"NOT_DELETE_CHALLENGE"),
    ERROR_REDIS_ACCESS(-11,"ERROR_REDIS_ACCESS"),

    JWT_CREATE_ERROR(-11, "JWT_CREATE_ERROR"),
    AUTH_SAVE_ERROR(-12, "AUTH_SAVE_ERROR"),
    SIGNATURE_EXCEPTION(-13, "SIGNATURE_EXCEPTION"),
    JWT_EXPIRATE_PASSED(-14, "JWT_EXPIRATE_PASSED"),
    JWT_ACCESS_DENIED(-15, "JWT_ACCESS_DENIED"),

    VERIFY_FAILED(-16, "VERIFY_FAILED"),
    MISMATCH_USERNAME(-17, "MISMATCH_USERNAME"),

    //API 에러.
    API_RESPONSE_MISMATCH(-18, "API_RESPONSE_MISMATCH"),
    API_RESPONSE_NULL(-19, "API_RESPONSE_NULL"),
    API_REQUEST_ERROR(-20, "REQUEST_ERROR_CLIENT"),
    API_RESPONSE_ERROR(-21, "API_RESPONSE_ERROR"),
    RESTAPI_ERROR(-22, "RESTAPI_ERROR"),
    EMAIL_FORM_ERROR(-23, "EMAIL_FORM_ERROR"),
    NOT_EXIST_MOVIE(-24, "NOT_EXIST_MOVIE"),
    INVALID_RATING(-25, "INVALID_RATING");

    private final Integer code;
    private final String message;
}
