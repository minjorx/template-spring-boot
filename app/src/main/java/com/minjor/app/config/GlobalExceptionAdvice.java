package com.minjor.app.config;

import com.minjor.web.model.resp.ResultJson;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.sql.SQLException;

/**
 * 全局异常处理
 * @author minjor
 * @date 2026/01/17 17:05
 * @description
 * 1. 统一异常处理
 * 2. 统一返回结果处理
 */
@Slf4j
@RestControllerAdvice(basePackages = "com.minjor")
public class GlobalExceptionAdvice {
    @Value("${spring.servlet.multipart.max-file-size:2MB}")
    private String maxFileSize;

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResultJson<Void> handleMaxSizeException(MaxUploadSizeExceededException exc,
                                                   HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:5173");
        response.setHeader("Access-Control-Allow-Methods", "POST, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        log.error("上传文件过大: {}", exc.getMessage());
        return new ResultJson<>(
                HttpStatus.CONTENT_TOO_LARGE.value(),
                "上传文件过大, 不能超过" + maxFileSize,
                null);
    }

    @ExceptionHandler(SQLException.class)
    public ResultJson<Void> handleSQLException(SQLException exc) {
        return new ResultJson<>(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "数据层异常: " + exc.getMessage(),
                null);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResultJson<Void> handleRuntimeException(RuntimeException exc) {
        log.error("运行时异常: {}", exc.getMessage(), exc);
        return new ResultJson<>(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                exc.getMessage(),
                null);
    }
}