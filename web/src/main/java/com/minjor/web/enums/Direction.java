package com.minjor.web.enums;

import com.minjor.common.enums.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Direction implements BaseEnum<String> {
    ASC("ASC"),
    DESC("DESC"),
    ;
    private final String label;
}