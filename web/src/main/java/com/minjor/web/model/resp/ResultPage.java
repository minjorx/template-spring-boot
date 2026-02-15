package com.minjor.web.model.resp;


import java.util.Collection;
import java.util.Collections;
import java.util.List;

public record ResultPage<T>(Collection<T> data, long total, int pageNum, int pageSize) {
    public static <T> ResultPage<T> of(Collection<T> data, long total, int pageNum, int pageSize) {
        return new ResultPage<>(data, total, pageNum, pageSize);
    }

}