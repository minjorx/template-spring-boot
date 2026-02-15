package com.minjor.app;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.boot.web.server.servlet.context.ServletComponentScan;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.context.SecurityContextHolder;

@SpringBootApplication(scanBasePackages = {"com.minjor"})
@ServletComponentScan
@EntityScan(basePackages = "com.minjor.**.entity")
@MapperScan("com.minjor.**.mapper")
public class App {

    static void main(String[] args) {
        SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL);
        ApplicationContext ctx = SpringApplication.run(App.class, args);
    }
}
