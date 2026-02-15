package com.minjor.datam.enums;

import com.baomidou.mybatisplus.annotation.IEnum;
import com.minjor.common.enums.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ExecuteSource implements BaseEnum<String, String>, IEnum<String> {
    MANUAL("手动输入", "manual"),
    SAVED_QUERY("保存查询", "saved_query"),
    METRIC("指标", "metric"),
    ;
    private final String label;
    private final String value;
}
