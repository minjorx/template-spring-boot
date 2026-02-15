package com.minjor.auth.util;// 生成 512 位密钥的方法
import java.security.SecureRandom;
import java.util.Base64;

public class SecureKeyGenerator {
    public static String generateSecureKey() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[64]; // 512 bits
        random.nextBytes(bytes);
        return Base64.getEncoder().encodeToString(bytes);
    }

    static void main() {
        IO.println(SecureKeyGenerator.generateSecureKey());
    }
}
