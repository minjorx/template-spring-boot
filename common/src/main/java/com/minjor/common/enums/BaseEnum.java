package com.minjor.common.enums;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public interface BaseEnum<L, V>{
    L getLabel();
    V getValue();
}
