package com.minjor.web.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ServerException extends RuntimeException{
    public static final int ERROR = 500;
    public static final String MESSAGE = "服务内部错误，请联系管理员";
    public ServerException(String message) {
        super(message);
    }
}
