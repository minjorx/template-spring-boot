package com.minjor.datam.controller;

import com.minjor.common.model.LabelValue;
import com.minjor.datam.dto.ColumnInfo;
import com.minjor.datam.service.MetaDataService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/datam/meta")
public class MetaDataController {
    @Autowired
    private MetaDataService metaDataService;

    @GetMapping("/labelValues")
    public List<LabelValue<String, String, String>> labelValues(
            @Valid @NotBlank(message = "数据源不能为空") @RequestParam String dsId,
            @RequestParam (required = false) String name) {
        return metaDataService.labelValues(dsId, name);
    }

    @GetMapping("/columns")
    public List<ColumnInfo> detail(
            @Valid @NotBlank(message = "数据源不能为空") @RequestParam String dsId,
            @Valid @NotBlank(message = "表名不能为空") @RequestParam String tableName) {
        return metaDataService.columns(dsId, tableName);
    }
}
