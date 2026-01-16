package com.minjor.app.controller;

import com.minjor.app.service.TraceService;
import com.minjor.web.controller.BaseController;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Objects;

@Slf4j
@RestController
@RequestMapping("/trace")
public class TraceController extends BaseController {
    @Autowired
    private TraceService traceService;

    @GetMapping("/test")
    public String test() {
        return traceService.test();
    }

    @GetMapping("/test-mdc")
    public String testMdc() {
        // 获取 MDC 中的所有键值对
        Map<String, String> mdcMap = MDC.getCopyOfContextMap();
        log.info("MDC: {}", mdcMap);
        if (Objects.isNull(mdcMap)) {
            return "MDC is null";
        }
        StringBuilder sb = new StringBuilder();
        for (String key : mdcMap.keySet()) {
            sb.append(key).append(": ").append(mdcMap.get(key)).append("\n");
        }
        return sb.toString();
    }
}
