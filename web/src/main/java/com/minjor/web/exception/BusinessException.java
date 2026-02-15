package com.minjor.web.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class BusinessException extends RuntimeException {
    private int code;

    public BusinessException(int code, String message) {
        this.code = code;
        super(message);
    }
}
