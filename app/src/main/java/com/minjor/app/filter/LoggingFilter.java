package com.minjor.app.filter;

import com.minjor.common.utils.JacksonUtil;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.ByteArrayInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

@Slf4j
@WebFilter(filterName = "loggingFilter", urlPatterns = "/*")
public class LoggingFilter implements Filter {

    // 设置最大缓存 100KB，避免大文件导致 OOM
    private static final int CONTENT_CACHE_LIMIT = 100 * 1024; // 100 KB
    private static final int RESPONSE_LOG_LIMIT = 100 * 1024; // 100 KB
    private static final int LOG_LIMIT = 100; // 100 KB

    // 需要处理的Content-Type前缀
    private static final String[] HANDLED_CONTENT_TYPES = {
            "application/json",
            "application/xml",
            "text/plain",
            "text/xml"
    };

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

        long startTime = System.currentTimeMillis();

        // 判断是否需要包装请求（基于Content-Type）
        boolean shouldWrapRequest = shouldWrapRequest(httpRequest);

        // 包装请求和响应
        HttpServletRequest wrappedRequest = shouldWrapRequest
                ? new CachedBodyHttpServletRequest(httpRequest, CONTENT_CACHE_LIMIT)
                : httpRequest;

        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(httpResponse);

        // 记录请求日志（如果需要包装）
        if (wrappedRequest instanceof CachedBodyHttpServletRequest cachedRequest) {
            logRequest(cachedRequest);
        } else {
            // 对于非JSON请求，只记录基本信息
            logSimpleRequest(httpRequest);
        }

        try {
            chain.doFilter(wrappedRequest, wrappedResponse);
        } finally {
            long duration = System.currentTimeMillis() - startTime;

            // 记录响应日志
            if (shouldLogResponse(wrappedResponse)) {
                logResponse(wrappedResponse, duration);
            } else {
                logSimpleResponse(wrappedResponse, duration);
            }

            wrappedResponse.copyBodyToResponse();
        }
    }

    /**
     * 判断是否需要包装请求并缓存内容
     */
    private boolean shouldWrapRequest(HttpServletRequest request) {
        // 获取Content-Type
        String contentType = request.getContentType();
        if (contentType == null) {
            return false;
        }

        // 转小写以便比较
        contentType = contentType.toLowerCase();

        // 只处理特定的Content-Type
        for (String handledType : HANDLED_CONTENT_TYPES) {
            if (contentType.startsWith(handledType)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 判断是否需要记录响应体内容
     */
    private boolean shouldLogResponse(ContentCachingResponseWrapper response) {
        String contentType = response.getContentType();
        if (contentType == null) {
            return false;
        }

        contentType = contentType.toLowerCase();
        return contentType.startsWith("application/json") || contentType.startsWith("application/xml");
    }

    /**
     * 记录非JSON请求的基本信息
     */
    private void logSimpleRequest(HttpServletRequest request) {
        String method = request.getMethod();
        String uri = request.getRequestURI();
        String queryString = request.getQueryString();
        String fullUrl = queryString == null ? uri : uri + "?" + queryString;

        String contentType = request.getContentType();
        if (contentType == null) {
            contentType = "unknown";
        }

        log.info("Request {} {} | Content-Type: {} | Body: ...",
                method, fullUrl, contentType);
    }

    /**
     * 记录非JSON响应的基本信息
     */
    private void logSimpleResponse(ContentCachingResponseWrapper response, long duration) {
        int status = response.getStatus();
        String contentType = response.getContentType();
        if (contentType == null) {
            contentType = "unknown";
        }

        log.info("Response {}ms {} | Content-Type: {} | Body: ...",
                duration, status, contentType);
    }

    /**
     * 自定义HttpServletRequestWrapper，支持多次读取请求体
     */
    private static class CachedBodyHttpServletRequest extends HttpServletRequestWrapper {
        /**
         * -- GETTER --
         * 获取缓存的请求体字节数组
         */
        @Getter
        private final byte[] cachedBody;

        public CachedBodyHttpServletRequest(HttpServletRequest request, int contentCacheLimit) throws IOException {
            super(request);

            // 读取并缓存请求体，限制大小避免OOM
            try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                ServletInputStream inputStream = request.getInputStream();

                // 直接读取所有字节，但限制大小
                byte[] buffer = new byte[4096];
                int bytesRead;
                int totalBytes = 0;

                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    // 检查是否超过缓存限制
                    if (totalBytes + bytesRead > contentCacheLimit) {
                        log.warn("Request body exceeds cache limit ({} bytes), truncating", contentCacheLimit);
                        break;
                    }

                    baos.write(buffer, 0, bytesRead);
                    totalBytes += bytesRead;
                }

                this.cachedBody = baos.toByteArray();
            }
        }

        @Override
        public ServletInputStream getInputStream() throws IOException {
            // 返回基于缓存的输入流，可多次读取
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(cachedBody);
            return new CachedBodyServletInputStream(byteArrayInputStream);
        }

        @Override
        public BufferedReader getReader() throws IOException {
            String charset = getCharacterEncoding();
            if (charset == null) {
                charset = StandardCharsets.UTF_8.name();
            }
            return new BufferedReader(new InputStreamReader(getInputStream(), charset));
        }

        @Override
        public int getContentLength() {
            return cachedBody.length;
        }

        @Override
        public long getContentLengthLong() {
            return cachedBody.length;
        }

        /**
         * 自定义ServletInputStream
         */
        private static class CachedBodyServletInputStream extends ServletInputStream {
            private final ByteArrayInputStream byteArrayInputStream;

            public CachedBodyServletInputStream(ByteArrayInputStream byteArrayInputStream) {
                this.byteArrayInputStream = byteArrayInputStream;
            }

            @Override
            public int read() throws IOException {
                return byteArrayInputStream.read();
            }

            @Override
            public int read(byte @NonNull [] b, int off, int len) throws IOException {
                return byteArrayInputStream.read(b, off, len);
            }

            @Override
            public boolean isFinished() {
                return byteArrayInputStream.available() == 0;
            }

            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public void setReadListener(ReadListener listener) {
                throw new UnsupportedOperationException("不支持异步读取");
            }
        }
    }

    private void logRequest(CachedBodyHttpServletRequest request) {
        String method = request.getMethod();
        String uri = request.getRequestURI();
        String queryString = request.getQueryString();
        String fullUrl = queryString == null ? uri : uri + "?" + queryString;

        // 使用原有的日志逻辑
        String logBody = getString(request.getContentType(), request.getContentLength(), request.getCachedBody());

        log.info("Request {} {} | {}",
                method, fullUrl, logBody);
    }

    private void logResponse(ContentCachingResponseWrapper response, long duration) {
        // 获取响应体
        String responseBody = getString(response.getContentType(), response.getContentSize(), response.getContentAsByteArray());

        // 获取响应状态码
        int status = response.getStatus();
        log.info("Response {}ms {} | {}",
                duration, status, responseBody);
    }

    private static @Nullable String getString(String contentType, int contentLength, byte[] buffer) {
        if (Strings.isNotBlank(contentType)
                && Objects.nonNull(buffer)
                && buffer.length != 0
                && contentType.toLowerCase().contains("application/json")) {

            try {
                String jsonString = new String(buffer, StandardCharsets.UTF_8);

                // 如果内容太短或看起来不是有效的JSON，直接返回
                if (jsonString.length() < 2 || (!jsonString.trim().startsWith("{") && !jsonString.trim().startsWith("["))) {
                    return jsonString.length() > LOG_LIMIT
                            ? jsonString.substring(0, LOG_LIMIT) + "...[truncated]"
                            : jsonString;
                }

                // 压缩JSON字符串为一行（保持原有逻辑）
                String compressedJson = JacksonUtil.writeValueAsString(JacksonUtil.readTree(jsonString));

                if (compressedJson != null && compressedJson.length() > LOG_LIMIT) {
                    // 截断过长地响应内容
                    return compressedJson.substring(0, LOG_LIMIT) + "...[truncated]";
                }
                return compressedJson;
            } catch (Exception e) {
                // 如果JSON解析失败，返回原始字符串（截断）
                String rawString = new String(buffer, StandardCharsets.UTF_8);
                if (rawString.length() > LOG_LIMIT) {
                    return rawString.substring(0, LOG_LIMIT) + "...[truncated]";
                }
                return rawString;
            }
        }
        return null;
    }

    @Override
    public void destroy() {
    }

    /**
     * 简单的ByteArrayOutputStream替代品，避免引入额外依赖
     */
    private static class ByteArrayOutputStream extends java.io.ByteArrayOutputStream {
        public ByteArrayOutputStream() {
            super();
        }

        public ByteArrayOutputStream(int size) {
            super(size);
        }
    }
}