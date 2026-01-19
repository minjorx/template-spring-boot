package com.minjor.demo.concurrent;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadVsVirtualThreadTest {
    
    private static final String URL = "http://localhost:8080/thread/delay";
    private static final int TOTAL_REQUESTS = 1000;
    private static final int CONCURRENT_THREADS = Integer.getInteger(
            "jdk.virtualThreadScheduler.parallelism",
            Runtime.getRuntime().availableProcessors()
    );;
    
    public static void main(String[] args) throws Exception {
        System.out.println("开始对比测试...\n");
        System.out.println("测试参数:");
        System.out.printf("总请求数: %d\n", TOTAL_REQUESTS);
        System.out.printf("并发线程数: %d\n", CONCURRENT_THREADS);
        
        // 测试1: 传统线程池
        System.out.println("=== 传统线程池测试 ===");
        testPlatformThreads();
        
        // 测试2: 虚拟线程
        System.out.println("\n=== 虚拟线程测试 ===");
        testVirtualThreads();
    }
    
    static void testPlatformThreads() throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(CONCURRENT_THREADS);
        runTest(executor, "传统线程");
        executor.shutdown();
    }
    
    static void testVirtualThreads() throws Exception {
        ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
        runTest(executor, "虚拟线程");
        executor.shutdown();
    }
    
    static void runTest(ExecutorService executor, String testName) throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        AtomicInteger success = new AtomicInteger(0);
        CountDownLatch latch = new CountDownLatch(TOTAL_REQUESTS);
        
        Instant start = Instant.now();
        
        for (int i = 0; i < TOTAL_REQUESTS; i++) {
            executor.submit(() -> {
                try {
                    HttpRequest request = HttpRequest.newBuilder()
                            .uri(URI.create(URL))
                            .timeout(Duration.ofSeconds(5))
                            .build();
                    
                    HttpResponse<String> response = client.send(request, 
                            HttpResponse.BodyHandlers.ofString());
                    
                    if (response.statusCode() == 200) {
                        success.incrementAndGet();
                    }
                } catch (Exception e) {
                    // 忽略错误，只统计成功数
                    System.out.println("请求失败: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }
        
        latch.await();
        Instant end = Instant.now();
        
        long time = Duration.between(start, end).toMillis();
        System.out.printf("测试: %s\n", testName);
        System.out.printf("总请求: %d\n", TOTAL_REQUESTS);
        System.out.printf("成功: %d\n", success.get());
        System.out.printf("耗时: %d ms\n", time);
        System.out.printf("吞吐量: %.2f 请求/秒\n\n", 
                (double) success.get() / time * 1000);
    }
}