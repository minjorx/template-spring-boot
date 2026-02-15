package com.minjor.datam.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.minjor.data.enums.DataStatus;
import com.minjor.data.utils.PageHelper;
import com.minjor.datam.entity.Datasource;
import com.minjor.datam.mapper.DatasourceMapper;
import com.minjor.datam.req.DatasourceSaveReq;
import com.minjor.web.model.req.PageReq;
import com.minjor.web.model.resp.ResultPage;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.Objects;
import java.util.Optional;

@Service
public class DatasourceService {

    @Autowired
    private DatasourceMapper datasourceMapper;

    @Autowired
    private DataSourceConnectionService dataSourceConnectionService;

    @EventListener
    public void onApplicationReady(ApplicationReadyEvent event) {
        datasourceMapper.selectList(null).forEach(datasource -> {
            dataSourceConnectionService.connectDataSource(datasource);
        });
    }

    public ResultPage<Datasource> listPage(PageReq<Void> req) {
        Page<Datasource> pageResult = datasourceMapper.selectPage(PageHelper.buildPage(req), new QueryWrapper<>());
        return ResultPage.of(pageResult.getRecords(), pageResult.getTotal(), req.getPageNum(), req.getPageSize());
    }

    public Datasource coverSaveTo(DatasourceSaveReq req) {
        Datasource datasource = new Datasource();
        datasource.setName(req.getName());
        datasource.setStatus(DataStatus.ACTIVE);
        datasource.setDbName(req.getDbName());
        datasource.setHost(req.getHost());
        datasource.setUserName(req.getUserName());
        datasource.setUserPassword(req.getUserPassword());
        datasource.setPort(req.getDbPort());
        datasource.setDbType(req.getDbType());
        return datasource;
    }

    @Transactional(rollbackFor = Exception.class)
    public Datasource save(DatasourceSaveReq req) {
        Datasource datasource = coverSaveTo(req);
        Assert.isTrue(!datasourceMapper.exists(new QueryWrapper<Datasource>().eq("name", req.getName())),
                "数据源名称重复");

        if (!dataSourceConnectionService.testConnection(datasource)) {
            throw new RuntimeException("连接测试失败");
        }
        Assert.isTrue(datasourceMapper.insert(datasource) > 0, "保存失败");
        dataSourceConnectionService.connectDataSource(datasource);
        return datasource;
    }

    @Transactional(rollbackFor = Exception.class)
    public void remove(String id) {
        Datasource datasource = Optional.ofNullable(datasourceMapper.selectById(id))
                .orElseThrow(() -> new RuntimeException("数据源不存在"));
        Assert.isTrue(datasourceMapper.deleteById(id) > 0, "删除失败");
        dataSourceConnectionService.closeDataSource(id);
    }

    public boolean testConnection(@Valid DatasourceSaveReq req) {
        Datasource datasource = coverSaveTo(req);
        return dataSourceConnectionService.testConnection(datasource);
    }

    public Datasource get(String id) {
        return Optional.ofNullable(datasourceMapper.selectById(id))
                .orElseThrow(() -> new RuntimeException("数据源不存在"));
    }

    @Transactional
    public void updateStatus(String id, @NotBlank(message = "状态不能为空") DataStatus dataStatus) {
        Datasource datasource = Optional.ofNullable(datasourceMapper.selectById(id))
                .orElseThrow(() -> new RuntimeException("数据源不存在"));
        if (Objects.equals(datasource.getStatus(), dataStatus)) {
            return;
        }
        datasource.setStatus(dataStatus);
        Assert.isTrue(datasourceMapper.updateById(datasource) > 0, "更新失败");
        if (dataStatus == DataStatus.ACTIVE) {
            dataSourceConnectionService.connectDataSource(datasource);
        } else {
            dataSourceConnectionService.closeDataSource(id);
        }
    }
}
