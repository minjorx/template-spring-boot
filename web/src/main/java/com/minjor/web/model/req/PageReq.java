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
    private List<Sort> sort;

    private long pageNum;
    private long pageSize;

    public void setPageNum(long pageNum) {
        this.pageNum = pageNum;
        if (this.pageNum < 1) {
            this.pageNum = 1;
        }
    }

    public void setPageSize(long pageSize) {
        this.pageSize = pageSize;
        if (this.pageSize < 1) {
            this.pageSize = 10;
        }
    }

    public long getPageNum() {
        if (this.pageNum < 1) {
            return 1;
        }
        return this.pageNum;
    }

    public long getPageSize() {
        if (this.pageSize < 1) {
            return 10;
        }
        return this.pageSize;
    }

    public record Sort(String field, Direction order) {
    }
}
