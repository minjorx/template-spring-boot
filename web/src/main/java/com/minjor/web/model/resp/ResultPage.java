package com.minjor.web.model.resp;

import com.minjor.web.enums.ResultStatus;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public record ResultPage<T>(Collection<T> data, long total, long pageNum, long pageSize) {
    public static <T> ResultPage<T> of(Collection<T> data, long total, long pageNum, long pageSize) {
        return new ResultPage<>(data, total, pageNum, pageSize);
    }
}