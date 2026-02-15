package com.minjor.datam.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.minjor.data.entity.BaseEntity;
import com.minjor.data.enums.DataStatus;
import com.minjor.datam.entity.DataSet;
import com.minjor.datam.enums.DatasetType;
import com.minjor.datam.mapper.DataSetMapper;
import com.minjor.datam.req.DatasetSaveReq;
import com.minjor.datam.resp.DatasetNamesVo;
import jakarta.validation.Valid;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class DatasetService {
    @Autowired
    private DataSetMapper dataSetMapper;

    public List<DatasetNamesVo> activedNameList(String dsId, String name, DatasetType type) {
        QueryWrapper<DataSet> queryWrapper = new QueryWrapper<DataSet>();
        if (Strings.isNotBlank(dsId)) {
            queryWrapper.eq(DataSet.Fields.dsId, dsId);
        }
        if (Strings.isNotBlank(name)) {
            queryWrapper.like(DataSet.Fields.name, name);
        }
        if (Objects.nonNull(type)) {
            queryWrapper.eq(DataSet.Fields.type, type.getValue());
        }
        queryWrapper.eq(DataSet.Fields.status, DataStatus.ACTIVE.getValue());
        return dataSetMapper.selectList(queryWrapper.select(
                BaseEntity.Fields.id,
                DataSet.Fields.code,
                DataSet.Fields.name,
                DataSet.Fields.type
        )).stream().map(dataSet -> new DatasetNamesVo(
                dataSet.getId(),
                dataSet.getCode(),
                dataSet.getName(),
                dataSet.getType()
        )).toList();
    }

    public Object detail(String id) {
        return dataSetMapper.selectById(id);
    }

    public void save(@Valid DatasetSaveReq req) {
    }
}
