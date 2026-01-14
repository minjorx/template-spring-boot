package com.minjor.app.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
@WebFilter(filterName = "loggingFilter", urlPatterns = "/*")
public class LoggingFilter implements Filter {    // 设置最大缓存 100KB，避免大文件导致 OOM
    private static final int CONTENT_CACHE_LIMIT = 1000 * 1024; // 100 KB

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        // 只处理 HTTP 请求
        if (!(request instanceof HttpServletRequest httpRequest) || !(response instanceof HttpServletResponse httpResponse)) {
            chain.doFilter(request, response);
            return;
        }

        // 包装请求和响应，使其支持多次读取 body
        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(httpRequest, CONTENT_CACHE_LIMIT);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(httpResponse);

        long startTime = System.currentTimeMillis();

        try {
            chain.doFilter(wrappedRequest, wrappedResponse);
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            logDetails(wrappedRequest, wrappedResponse, duration);
            // 必须调用 copyBodyToResponse，否则客户端收不到响应体
            wrappedResponse.copyBodyToResponse();
        }
    }

    private void logDetails(ContentCachingRequestWrapper request, ContentCachingResponseWrapper response, long duration) {
        String method = request.getMethod();
        String uri = request.getRequestURI();
        String queryString = request.getQueryString();
        String fullUrl = queryString == null ? uri : uri + "?" + queryString;

        // 获取请求 Body（只处理 application/json）
        String requestBody = getString(request.getContentType(), request.getContentLength(), request.getContentAsByteArray());

        // 获取响应 Body（只处理 application/json）
        String responseBody = getString(response.getContentType(), response.getContentSize(), response.getContentAsByteArray());

        // 获取响应状态码
        int status = response.getStatus();
        log.info("Request: {} {} | Request Body: {} | Response Status: {} | Response Body: {} | Duration: {}ms",
                method, fullUrl, requestBody, status, responseBody, duration);
    }

    private static @NonNull String getString(String request, int request1, byte[] buffer) {
        String requestBody = "";
        if (request != null && request.toLowerCase().contains("application/json")) {
            if (request1 > 0) {
                if (buffer.length > 0) {
                    requestBody = new String(buffer, StandardCharsets.UTF_8);
                }
            }
        } else {
            requestBody = "..";
        }
        return requestBody;
    }
}