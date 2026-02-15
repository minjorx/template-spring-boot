package com.minjor.app.config;

import com.minjor.web.exception.BusinessException;
import com.minjor.web.exception.ServerException;
import com.minjor.web.model.resp.ResultJson;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

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
        log.error("数据层异常: {}", exc.getMessage());
        return new ResultJson<>(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "数据层异常: " + exc.getMessage(),
                null);
    }

    @ExceptionHandler(BadSqlGrammarException.class)
    public ResultJson<Void> handleBadSqlGrammarException(BadSqlGrammarException exc) {
        log.error("数据层异常: {}", exc.getMessage());
        return new ResultJson<>(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "数据层异常",
                null);
    }


    /**
     * 捕获 @RequestBody 对象校验失败的异常 (MethodArgumentNotValidException)
     * 这通常发生在 POST/PUT 请求，校验请求体时。
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResultJson<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException exc) {
        log.error("参数校验失败: {}", exc.getMessage());
        List<FieldError> fieldErrors = exc.getBindingResult().getFieldErrors();
        List<String> errorMessages = fieldErrors.stream()
                .map(FieldError::getDefaultMessage)
                .toList();
        return new ResultJson<>(
                HttpStatus.BAD_REQUEST.value(),
                "参数校验失败: " + String.join("; ", errorMessages),
                null
        );
    }

    @ExceptionHandler(BindException.class)
    public ResultJson<Object> handleBindException(BindException exc) {
        log.error("参数校验失败: {}", exc.getMessage());
        List<FieldError> fieldErrors = exc.getBindingResult().getFieldErrors();
        List<String> errorMessages = fieldErrors.stream()
                .map(FieldError::getDefaultMessage)
                .toList();
        return new ResultJson<>(
                HttpStatus.BAD_REQUEST.value(),
                "参数校验失败: " + String.join("; ", errorMessages),
                null
        );
    }

    @ExceptionHandler(BusinessException.class)
    public ResultJson<Void> handleBusinessException(BusinessException exc) {
        log.error("业务异常: {}", exc.getMessage(), exc);
        return new ResultJson<>(
                exc.getCode(),
                exc.getMessage(),
                null);
    }

    @ExceptionHandler(ServerException.class)
    public ResultJson<Void> handleServerException(ServerException exc) {
        log.error("服务器内部错误: {}", exc.getMessage(), exc);
        return new ResultJson<>(
                ServerException.ERROR,
                ServerException.MESSAGE,
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

    @ExceptionHandler(Exception.class)
    public ResultJson<Void> handleException(Exception exc, HttpServletRequest request) {
        log.error("全局异常: {}", exc.getMessage(), exc);
        return new ResultJson<>(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "服务器内部错误: " + exc.getMessage(),
                null);
    }
}