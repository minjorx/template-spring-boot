package com.minjor.demo.concurrent;

import com.google.common.base.Stopwatch;

import java.util.concurrent.CountDownLatch;

/**
 * 异步任务执行示例
 */
public class TaskRunner {
    public final static int SIMULATE_FILE_SIZE_KB = 1000;

    static void main() throws InterruptedException {
        final int fileSizeKB = 10000;
        final int threadCount = 10000;
//        TaskRunner.runComputeByPlatformThread(threadCount);
//        TaskRunner.runComputeByVisualThread(threadCount);
        TaskRunner.runIOByPlatformThread(fileSizeKB, threadCount);
        TaskRunner.runIOByVisualThread(fileSizeKB, threadCount);
    }

    /**
     * 传统的异步任务(Thread, Runnable, Callable)
     */
    /**
     * Thread
     * 直接调用的情况下，对Thread对象创建不可控，以及线程的资源创建耗时
     */
    public static void runComputeByPlatformThread(int threadCount) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(threadCount);
        Stopwatch taskStopwatch = Stopwatch.createStarted();
        for (int i = 0; i < threadCount; i++) {
            final int threadNum = i;
            Thread.ofPlatform().start(() -> {
                try {
//                    System.out.printf("Thread %d %f", threadNum, TaskProvider.computePiMonteCarlo(threadNum));
                    TaskProvider.computePiMonteCarlo(threadNum);
                } finally {
                    latch.countDown();
                }
            });
        }
        // 等待所有任务完成
        latch.await();
        taskStopwatch.stop();
        System.out.println("\nThreadOfPlatform 异步任务执行完成，耗时：" + taskStopwatch.elapsed());
    }

    /**
     * VisualThread
     * 直接调用的情况下，对Thread对象创建不可控，以及线程的资源创建耗时
     */
    public static void runComputeByVisualThread(int threadCount) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(threadCount);
        Stopwatch taskStopwatch = Stopwatch.createStarted();
        for (int i = 0; i < threadCount; i++) {
            final int threadNum = i;
            Thread.ofVirtual().start(() -> {
                try {
//                    System.out.printf("Thread %d %f", threadNum, TaskProvider.computePiMonteCarlo(threadNum));
                    TaskProvider.computePiMonteCarlo(threadNum);
                } finally {
                    latch.countDown();
                }
            });
        }
        // 等待所有任务完成
        latch.await();
        taskStopwatch.stop();
        System.out.println("\nThreadOfVisual 异步任务执行完成，耗时：" + taskStopwatch.elapsed());
    }

    /**
     * 传统的异步任务(Thread, Runnable, Callable)
     */
    /**
     * Thread
     * 直接调用的情况下，对Thread对象创建不可控，以及线程的资源创建耗时
     */
    public static void runIOByPlatformThread(int fileSize, int threadCount) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(threadCount);
        Stopwatch taskStopwatch = Stopwatch.createStarted();
        for (int i = 0; i < threadCount; i++) {
            final int threadNum = i;
            Thread.ofPlatform().start(() -> {
                try {
//                    System.out.printf("Thread %d %f", threadNum, TaskProvider.computePiMonteCarlo(threadNum));
                    TaskProvider.simulateFileProcessing(fileSize, threadNum);
                } finally {
                    latch.countDown();
                }
            });
        }
        // 等待所有任务完成
        latch.await();
        taskStopwatch.stop();
        System.out.println("\nThreadOfPlatform 异步任务执行完成，耗时：" + taskStopwatch.elapsed());
    }

    /**
     * VisualThread
     * 直接调用的情况下，对Thread对象创建不可控，以及线程的资源创建耗时
     */
    public static void runIOByVisualThread(int fileSize, int threadCount) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(threadCount);
        Stopwatch taskStopwatch = Stopwatch.createStarted();
        for (int i = 0; i < threadCount; i++) {
            final int threadNum = i;
            Thread.ofVirtual().start(() -> {
                try {
//                    System.out.printf("Thread %d %f", threadNum, TaskProvider.computePiMonteCarlo(threadNum));
                    TaskProvider.simulateFileProcessing(fileSize, threadNum);
                } finally {
                    latch.countDown();
                }
            });
        }
        // 等待所有任务完成
        latch.await();
        taskStopwatch.stop();
        System.out.println("\nThreadOfVisual 异步任务执行完成，耗时：" + taskStopwatch.elapsed());
    }
}
