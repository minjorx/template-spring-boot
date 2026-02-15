package com.minjor.datam.controller;

import com.minjor.datam.entity.Datasource;
import com.minjor.datam.service.DatasourceService;
import com.minjor.web.controller.BaseController;
import com.minjor.data.req.DataStatusReq;
import com.minjor.web.model.resp.ResultJson;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 数据源管理
 */
@RestController
@RequestMapping("/datam/datasource")
public class DatasourceController extends BaseController {
    @Autowired
    private DatasourceService datasourceService;

    @DeleteMapping("/{id}")
    public ResultJson<Void> remove(@PathVariable String id) {
        datasourceService.remove(id);
        return success();
    }

    @GetMapping("/{id}")
    public ResultJson<Datasource> get(@PathVariable String id) {
        return success(datasourceService.get(id));
    }

    @PatchMapping("/{id}/status")
    public ResultJson<Void> updateStatus(@PathVariable String id, @RequestBody @Valid DataStatusReq req) {
        datasourceService.updateStatus(id, req.dataStatus());
        return success();
    }

}
