package com.minjor.datam.req;

import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record DatasetSaveReq(
        String id,
        @NotBlank(message = "数据源不能为空") String dsId,
        @NotBlank(message = "数据集编码不能为空") String code,
        @NotBlank(message = "数据集来源表不能为空") String table,
        @NotBlank(message = "数据集名称不能为空") String name,
        String description,
        List<String> dimensions,
        List<String> measures
) {
}
