package com.minjor.data.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.annotation.IEnum;
import com.minjor.common.enums.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum DataStatus implements BaseEnum<String, Integer>, IEnum<Integer> {
    ACTIVE("启用", 1),
    INACTIVE("禁用", 0),
    ;
    private final String label;
    private final Integer value;
}
