package com.minjor.demo.concurrent;

import com.google.common.base.Stopwatch;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 异步任务执行示例
 */
public class TaskRunner {
    public final static int SIMULATE_FILE_SIZE_KB = 1000;

    static void main() throws InterruptedException {
        final int threadCount = 10000;
        TaskRunner.runIOByThread(threadCount);
        TaskRunner.runIOByRunnable(threadCount);
    }

    /**
     * 传统的异步任务(Thread, Runnable, Callable)
     */
    /**
     * Thread
     * 直接调用的情况下，对Thread对象创建不可控，以及线程的资源创建耗时
     */
    public static void runIOByThread(int threadCount) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(threadCount);
        Stopwatch taskStopwatch = Stopwatch.createStarted();
        for (int i = 0; i < threadCount; i++) {
            final int threadNum = i;
            new Thread(() -> {
                try {
//                    System.out.printf("Thread %d %f", threadNum, TaskProvider.computePiMonteCarlo(threadNum));
                    TaskProvider.computePiMonteCarlo(threadNum);
                } finally {
                    latch.countDown();
                }
            }).start();
        }
        // 等待所有任务完成
        latch.await();
        taskStopwatch.stop();
        System.out.println("\nThread 异步任务执行完成，耗时：" + taskStopwatch.elapsed());
    }

    public static void runIOByRunnable(int threadCount) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(threadCount);
        Stopwatch taskStopwatch = Stopwatch.createStarted();
        for (int i = 0; i < threadCount; i++) {
            final int threadNum = i;
            ((Runnable) () -> {
                try {
//                    System.out.printf("Runnable %d %f", threadNum, TaskProvider.computePiMonteCarlo(threadNum));
                    TaskProvider.computePiMonteCarlo(threadNum);
                } finally {
                    latch.countDown();
                }
            }).run();
        }
        // 等待所有任务完成
        latch.await();
        taskStopwatch.stop();
        System.out.println("\nRunnable 异步任务执行完成，耗时：" + taskStopwatch.elapsed());
    }
}
