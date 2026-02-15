package com.minjor.datam.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.minjor.datam.entity.DataSet;
import com.minjor.datam.entity.ExecuteHistory;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DataSetMapper extends BaseMapper<DataSet> {
}
