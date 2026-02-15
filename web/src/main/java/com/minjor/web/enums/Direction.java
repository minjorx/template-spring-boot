package com.minjor.web.enums;

import com.minjor.common.enums.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Direction implements BaseEnum<String, String> {
    ASC("ASC", "asc"),
    DESC("DESC", "desc"),
    ;
    private final String label;
    private final String value;
}