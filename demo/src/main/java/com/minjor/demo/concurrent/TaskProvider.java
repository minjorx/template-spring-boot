package com.minjor.demo.concurrent;

import lombok.experimental.UtilityClass;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

@UtilityClass
public class TaskProvider {
    static void main() {
        for (int i = 0; i < 1000; i++) {
            System.out.printf("%010d: %.6f%n", i, TaskProvider.computePiMonteCarlo(i));
        }
    }

    /**
     * 蒙特卡洛方法计算π - 计算密集型任务
     * @param totalPoints 总点数
     * @return π的近似值
     */
    public static double computePiMonteCarlo(long totalPoints) {
        long insideCircle = 0;

        for (long i = 0; i < totalPoints; i++) {
            double x = Math.random();
            double y = Math.random();

            // 计算点是否在单位圆内
            if (x * x + y * y <= 1.0) {
                insideCircle++;
            }
        }

        // π ≈ 4 * (圆内点数 / 总点数)
        return 4.0 * insideCircle / totalPoints;
    }


    /**
     * 模拟文件读取和处理任务
     * @param fileSizeKB 模拟的文件大小(KB)
     * @param complexity 处理复杂度因子 (1-10)
     * @return 处理结果字符串
     */
    public static String simulateFileProcessing(int fileSizeKB, int complexity) {
        // 模拟IO等待 - 文件读取时间与文件大小相关
        try {
            // 每KB约0.5ms的读取时间（实际IO延迟）
            Thread.sleep((long) (fileSizeKB * 0.5));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // 模拟数据处理（少量CPU计算）
        StringBuilder result = new StringBuilder();
        Random random = ThreadLocalRandom.current();
        int dataSize = fileSizeKB * 10; // 模拟数据量

        for (int i = 0; i < dataSize; i++) {
            // 模拟数据处理逻辑
            char processedChar = (char) ('A' + random.nextInt(26));
            result.append(processedChar);

            // 增加处理复杂度
            if (complexity > 5) {
                // 模拟额外处理步骤
                String temp = result.toString();
                if (temp.length() > 100) {
                    result = new StringBuilder(temp.substring(0, 100));
                }
            }
        }

        // 模拟写入操作
        try {
            Thread.sleep((long) (fileSizeKB * 0.3));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        return result.toString();
    }
}
