package com.minjor.web.enums;

import com.minjor.common.enums.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Group implements BaseEnum<String> {
    SYSTEM("系统"),
    USER("用户"),
    ;
    private final String label;
}
