package com.minjor.datam.controller;

import com.minjor.datam.enums.DatasetType;
import com.minjor.datam.resp.DatasetNamesVo;
import com.minjor.datam.service.DatasetService;
import com.minjor.web.controller.BaseController;
import com.minjor.web.model.resp.ResultJson;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 查询管理
 */
@Tag(name = "DatasetsController", description = "数据集管理")
@RestController
@RequestMapping("/datam/datasets")
public class DatasetsController extends BaseController {
    @Autowired
    private DatasetService datasetService;

    @GetMapping("/names")
    public ResultJson<List<DatasetNamesVo>> names(
            @RequestParam(required = false) String dsId,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) DatasetType type
            ) {
        return success(datasetService.activedNameList(dsId, name, type));
    }
}
