package com.minjor.datam.req;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Map;

@Data
public class TryExecuteSqlReq {
    @NotBlank(message = "数据源不能为空")
    private String dsId;
    @NotBlank(message = "SQL不能为空")
    private String sql;
    private Map<String, Object> params;
}
