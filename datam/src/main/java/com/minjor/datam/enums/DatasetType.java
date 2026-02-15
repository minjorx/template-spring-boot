package com.minjor.datam.enums;

import com.baomidou.mybatisplus.annotation.IEnum;
import com.minjor.common.enums.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DatasetType implements BaseEnum<String, String>, IEnum<String> {
    TABLE("表","table"),
    VIEW("视图","view"),
    MATERIALIZED_VIEW("物化视图", "materialized_view"),
    ;
    private final String label;
    private final String value;
}
