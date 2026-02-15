package com.minjor.web.model.req;

import com.minjor.web.enums.Direction;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class PageReq<C> {
    private C condition;
    @Schema(description = "排序规则",
            defaultValue = "[{\"field\":\"\",\"order\":\"DESC\"}]",
            example = "[{\"field\":\"\",\"order\":\"DESC\"}]")
    private List<Sorted> sorted;

    private int pageNum;
    private int pageSize;

    public void setPageNum(int pageNum) {
        this.pageNum = Math.max(pageNum, 1);
    }

    public void setPageSize(int pageSize) {
        if (pageSize < 1) {
            this.pageSize = 10;
        } else {
            this.pageSize = pageSize;
        }
    }

    public record Sorted(String field, Direction order) {
    }
}
