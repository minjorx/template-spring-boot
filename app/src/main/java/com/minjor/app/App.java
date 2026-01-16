package com.minjor.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.server.servlet.context.ServletComponentScan;
import org.springframework.context.ApplicationContext;

@SpringBootApplication(scanBasePackages = {"com.minjor"})
@ServletComponentScan
public class App {

    static void main(String[] args) {
        ApplicationContext ctx = SpringApplication.run(App.class, args);
    }
}
