package com.minjor.data.req;


import com.minjor.data.enums.DataStatus;
import jakarta.validation.constraints.NotNull;

public record DataStatusReq( @NotNull(message = "状态不能为空") DataStatus dataStatus) {
}
