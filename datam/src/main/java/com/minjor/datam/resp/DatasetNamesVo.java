package com.minjor.datam.resp;

import com.minjor.datam.enums.DatasetType;

public record DatasetNamesVo(String id,
                             String code,
                             String name,
                             DatasetType type){
}
