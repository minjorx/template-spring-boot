package com.minjor.data.enums;

import com.baomidou.mybatisplus.annotation.IEnum;
import com.minjor.common.enums.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum TaskStatus implements BaseEnum<String, Integer>, IEnum<Integer> {
    ACTIVE("成功", 1),
    INACTIVE("失败", 0),
    ;
    private final String label;
    private final Integer value;
}
