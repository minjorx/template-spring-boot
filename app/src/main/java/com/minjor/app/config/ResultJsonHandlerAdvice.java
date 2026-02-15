package com.minjor.app.config;

import com.minjor.common.utils.JacksonUtil;
import com.minjor.web.anno.ResultJsonIgnore;
import com.minjor.web.enums.ResultStatus;
import com.minjor.web.model.resp.ResultJson;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.lang.reflect.Method;

@Slf4j
@RestControllerAdvice(basePackages = "com.minjor")
public class ResultJsonHandlerAdvice implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        Method method = returnType.getMethod();

        // 快速空检查
        if (method == null) {  // 直接使用 == 而非 Objects.isNull()
            return false;
        }

        // 执行检查逻辑

        // 按性能开销排序：类型比较 -> 注解检查
        Class<?> returnClass = method.getReturnType();

        // 1. 检查void返回（最常见，最快）
        if (returnClass == Void.TYPE) {  // 使用 == 而非 equals()
            return false;
        }

        // 2. 检查ResultJson类型返回
        if (returnClass == ResultJson.class) {  // 类对象是单例，可用 ==
            return false;
        }

        if (returnClass == ResponseEntity.class) {
            return false;
        }

        // 3. 注解检查（反射开销最大）
        // 先检查方法注解（通常比类注解更常用）
        if (method.isAnnotationPresent(ResultJsonIgnore.class)) {
            return false;
        }

        // 最后检查类注解（可能触发类加载）
        return !method.getDeclaringClass().isAnnotationPresent(ResultJsonIgnore.class);
    }

    @Override
    public @Nullable Object beforeBodyWrite(@Nullable Object body, @NonNull MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        if (selectedConverterType.getSimpleName().contains("StringHttpMessageConverter")) {
            return JacksonUtil.toJson(new ResultJson<>(ResultStatus.SUCCESS_CODE, ResultStatus.SUCCESS, body));
        }
        return new ResultJson<>(ResultStatus.SUCCESS_CODE, ResultStatus.SUCCESS, body);
    }
}