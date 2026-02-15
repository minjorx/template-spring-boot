package com.minjor.web.enums;

import com.minjor.common.enums.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Group implements BaseEnum<String, String> {
    SYSTEM("系统", "system"),
    USER("用户", "user"),
    ;
    private final String label;
    private final String value;
}
