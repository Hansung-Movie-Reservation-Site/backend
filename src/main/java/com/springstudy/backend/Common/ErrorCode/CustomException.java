package com.springstudy.backend.Common.ErrorCode;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException{
    private final CodeInterface codeInterface;
    public CustomException(CodeInterface codeInterface){
        super(codeInterface.getMessage());
        this.codeInterface = codeInterface;
    }
    public CustomException(String message, CodeInterface codeInterface){
        super(codeInterface.getMessage() + message);
        this.codeInterface = codeInterface;
    }
}