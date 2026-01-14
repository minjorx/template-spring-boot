package com.minjor.web.model.resp;

import com.minjor.web.enums.ResultStatus;

import java.util.List;

public record ResultPage<T>(Integer code, String message, List<T> data, long total, long pageNum, long pageSize) {
    public static <T> ResultPage<T> of(List<T> data, long total, long pageNum, long pageSize) {
        return new ResultPage<>(ResultStatus.SUCCESS_CODE, ResultStatus.SUCCESS, data, total, pageNum, pageSize);
    }
}