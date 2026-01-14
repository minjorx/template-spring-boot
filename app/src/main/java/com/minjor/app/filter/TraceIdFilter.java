package com.minjor.app.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@WebFilter(filterName = "traceIdFilter", urlPatterns = "/*")
public class TraceIdFilter implements Filter {

    /**
     * 日志跟踪标识
     */
    private static final String TRACE_ID = "TRACE_ID";

    /**
     * 请求头中的 traceId 字段名（可根据实际情况调整）
     */
    private static final String TRACE_ID_HEADER = "X-Trace-Id";

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        String traceId;
        if (servletRequest instanceof HttpServletRequest httpRequest) {
            String headerTraceId = httpRequest.getHeader(TRACE_ID_HEADER);

            if (headerTraceId != null && !headerTraceId.trim().isEmpty()) {
                traceId = headerTraceId.trim();
            } else {
                traceId = generateTraceId();
            }
            if (servletResponse instanceof HttpServletResponse httpResponse) {
                httpResponse.setHeader(TRACE_ID_HEADER, traceId);
            }
        } else {
            traceId = generateTraceId();
        }
        MDC.put(TRACE_ID, traceId);
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {
        MDC.clear();
    }

    /**
     * 生成 traceId
     */
    private String generateTraceId() {
        return UUID.randomUUID().toString().replace("-", "").toLowerCase();
    }

    public static String getTraceId() {
        return MDC.get(TRACE_ID);
    }
}