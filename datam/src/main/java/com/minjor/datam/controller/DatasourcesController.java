package com.minjor.datam.controller;

import com.minjor.datam.entity.Datasource;
import com.minjor.datam.req.DatasourceSaveReq;
import com.minjor.datam.service.DatasourceService;
import com.minjor.web.controller.BaseController;
import com.minjor.web.model.req.PageReq;
import com.minjor.web.model.resp.ResultJson;
import com.minjor.web.model.resp.ResultPage;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 数据源管理
 */
@RestController
@RequestMapping("/datam/datasources")
public class DatasourcesController extends BaseController {
    @Autowired
    private DatasourceService datasourceService;

    @PostMapping("/page")
    public ResultPage<Datasource> list(
            @RequestBody PageReq<Void> req) {
        return datasourceService.listPage(req);
    }

    @PostMapping("/save")
    public ResultJson<Datasource> save(@Valid @RequestBody DatasourceSaveReq req) {
        return success(datasourceService.save(req));
    }

    @PostMapping("/test")
    public ResultJson<Void> test(@Valid @RequestBody DatasourceSaveReq req) {
        if (datasourceService.testConnection(req)) {
            return success("连接测试成功", null);
        } else {
            return fail("连接测试失败");
        }
    }
}
