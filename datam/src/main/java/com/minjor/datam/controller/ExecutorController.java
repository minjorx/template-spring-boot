package com.minjor.datam.controller;

import com.minjor.datam.req.TryExecuteSqlReq;
import com.minjor.datam.service.ExecutorService;
import com.minjor.web.controller.BaseController;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 数据源管理
 */
@RestController
@RequestMapping("/datam/executor")
public class ExecutorController extends BaseController {
    @Autowired
    private ExecutorService executorService;

    @GetMapping("/tryExecuteSql")
    public List<Map<String, Object>> tryExecuteSql(
            @Valid @NotBlank(message = "数据源ID不能为空") @RequestParam String dsId,
            @Valid @NotBlank(message = "SQL不能为空") @RequestParam String sql
    ) {
        return executorService.tryExecuteSql(dsId, sql, null);
    }

    @PostMapping("/tryExecuteSql")
    public List<Map<String, Object>> tryExecuteSql(
            @Valid @RequestBody TryExecuteSqlReq req
            ) {
        return executorService.tryExecuteSql(req.getDsId(), req.getSql(), req.getParams());
    }
}
