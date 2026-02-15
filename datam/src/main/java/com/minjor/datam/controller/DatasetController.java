package com.minjor.datam.controller;

import com.minjor.datam.req.DatasetSaveReq;
import com.minjor.datam.resp.DatasetNamesVo;
import com.minjor.datam.service.DatasetService;
import com.minjor.web.controller.BaseController;
import com.minjor.web.model.resp.ResultJson;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 查询管理
 */
@Tag(name = "DatasetController", description = "数据集查询和操作")
@RestController
@RequestMapping("/datam/dataset")
public class DatasetController extends BaseController {
    @Autowired
    private DatasetService datasetService;

    @GetMapping("detail/{id}")
    public ResultJson<Object> detail(
            @Valid @NotBlank(message = "ID不能为空") @PathVariable("id") String id) {
        return success(datasetService.detail(id));
    }

    @PostMapping("/save")
    public ResultJson<Void> save(@Valid @RequestBody DatasetSaveReq req) {
        datasetService.save(req);
        return success();
    }

}
