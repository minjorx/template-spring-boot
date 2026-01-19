package com.minjor.template.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/thread")
public class ThreadController {
    @RequestMapping("/delay")
    public String delay() {
        try {
            Thread.sleep(500);
            System.out.println(Thread.currentThread());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "delay";
    }
}
