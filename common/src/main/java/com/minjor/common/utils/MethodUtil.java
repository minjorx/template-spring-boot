package com.minjor.common.utils;

public class MethodUtil {
    
    /**
     * 获取当前方法名
     */
    public static String getCurrentMethodName() {
        return getMethodName(0);
    }
    
    /**
     * 获取调用者的方法名
     */
    public static String getCallerMethodName() {
        return getMethodName(1);
    }
    
    /**
     * 获取指定深度的栈帧方法名
     * @param depth 0=当前方法, 1=调用者, 2=调用者的调用者...
     */
    public static String getMethodName(int depth) {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        // 注意：数组索引
        // stackTrace[0] = getStackTrace
        // stackTrace[1] = getMethodName
        // stackTrace[2] = 调用getMethodName的方法（当前方法）
        // stackTrace[3] = 调用者方法
        int index = depth + 2;
        
        if (index < stackTrace.length) {
            return stackTrace[index].getMethodName();
        }
        throw new IndexOutOfBoundsException("Stack trace too short");
    }
    
    /**
     * 获取完整的方法信息（类名+方法名）
     */
    public static String getFullMethodName() {
        return getFullMethodName(0);
    }
    
    public static String getFullMethodName(int depth) {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        int index = depth + 2;
        
        if (index < stackTrace.length) {
            StackTraceElement element = stackTrace[index];
            return element.getClassName() + "." + element.getMethodName();
        }
        return "Unknown";
    }
}
