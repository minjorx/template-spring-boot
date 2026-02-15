package com.minjor.data.utils;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.minjor.web.model.req.PageReq;
import lombok.experimental.UtilityClass;

@UtilityClass
public class PageHelper {

    public static <T> Page<T> buildPage(Integer pageNum, Integer pageSize) {
        return new Page<>(pageNum, pageSize);
    }

    public static <T> Page<T> buildPage(PageReq<?> req) {
        return new Page<>(req.getPageNum(), req.getPageSize());
    }
}
