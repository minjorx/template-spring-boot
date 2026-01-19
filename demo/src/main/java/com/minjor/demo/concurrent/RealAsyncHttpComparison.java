package com.minjor.demo.concurrent;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class RealAsyncHttpComparison {
    
    public static void testRealHttpRequests() {
        // 目标API：返回延迟响应的测试端点
        String[] urls = {
                "http://localhost:8080/thread/delay"
        };
        // 创建HTTP客户端 - 使用真正的异步API
        try (HttpClient client1 = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(60))
                .executor(Executors.newVirtualThreadPerTaskExecutor()) // 重要：使用虚拟线程执行器
                .build();
            HttpClient client2 = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(60))
                .executor(Executors.newCachedThreadPool())
                .build()){
            System.out.println("使用真正的异步HTTP客户端测试...");

            // 虚拟线程方案
            System.out.println("=== 虚拟线程方案 ===");
            testWithClient(client1, urls, 400);
            System.out.println("=== 虚拟线程方案结束 ===");
            System.out.println();
            // 线程池方案
            System.out.println("=== 线程池方案 ===");
            testWithClient(client2, urls, 400);
            System.out.println("=== 线程池方案结束 ===");

        }
    }
    
    private static void testWithClient(HttpClient client, String[] urls, int requestCount) {
        Instant start = Instant.now();
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        AtomicInteger completed = new AtomicInteger();
        
        for (int i = 0; i < requestCount; i++) {
            String url = urls[i % urls.length];
            CompletableFuture<Void> future = client
                .sendAsync(
                    HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .timeout(Duration.ofSeconds(10))
                        .build(),
                    HttpResponse.BodyHandlers.ofString()
                )
                .thenAccept(response -> {
                    completed.incrementAndGet();
                    // 真正的响应处理，不是sleep
                    String body = response.body();
                    // 处理响应数据
                    if (body.length() > 100) {
                        body = body.substring(0, 100);
                    }
                });
            
            futures.add(future);
        }
        
        // 等待所有请求完成
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        
        Duration duration = Duration.between(start, Instant.now());
        System.out.printf("线程完成 %d 个请求，耗时: %d ms%n",
            completed.get(), duration.toMillis());
    }

    public static void main(String[] args) {
        testRealHttpRequests();
    }
}