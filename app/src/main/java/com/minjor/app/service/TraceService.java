package com.minjor.app.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TraceService {

    public String test() {
        log.info("trace test");
        return "trace test";
    }
}
