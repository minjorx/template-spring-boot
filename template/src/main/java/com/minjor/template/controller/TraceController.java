package com.minjor.template.controller;

import com.minjor.template.service.TraceService;
import com.minjor.web.controller.BaseController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Tag(name = "TemplateTraceController", description = "链路追踪")
@Slf4j
@RestController
@RequestMapping("/template/trace")
public class TraceController extends BaseController {
    @Autowired
    private TraceService traceService;

    @Operation(description = "测试")
    @GetMapping("/test")
    public String test() {
        return traceService.test();
    }

    @Operation(description = "获取 MDC")
    @GetMapping("/mdc")
    public Map<String, String> testMdc() {
        // 获取 MDC 中的所有键值对
        Map<String, String> mdcMap = MDC.getCopyOfContextMap();
        log.info("MDC: {}", mdcMap);
        return mdcMap;
    }
}
