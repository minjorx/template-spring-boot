package com.minjor.template.controller;

import com.minjor.template.service.TraceService;
import com.minjor.web.anno.ResultJsonIgnore;
import com.minjor.web.controller.BaseController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Tag(name = "TemplateAppController", description = "系统配置相关")
@Slf4j
@RestController
@RequestMapping("/template/app")
public class AppController extends BaseController {
    @Autowired
    private TraceService traceService;

    @Operation(description = "测试ResultJsonIgnore")
    @ResultJsonIgnore
    @GetMapping("/withoutResultJson")
    public String withoutResultJson() {
        return traceService.test();
    }
}
