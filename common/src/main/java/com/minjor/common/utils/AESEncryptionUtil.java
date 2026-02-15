package com.minjor.common.utils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class AESEncryptionUtil {

    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/ECB/PKCS5Padding";

    /**
     * 生成AES密钥
     *
     * @return Base64编码的密钥字符串
     */
    public static String generateKey() {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance(ALGORITHM);
            keyGen.init(256); // AES支持128, 192, 256位长度
            SecretKey secretKey = keyGen.generateKey();
            return Base64.getEncoder().encodeToString(secretKey.getEncoded());
        } catch (Exception e) {
            throw new RuntimeException("密钥生成失败", e);
        }
    }

    /**
     * 加密
     *
     * @param content 明文内容
     * @param key     密钥(Base64编码)
     * @return Base64编码的密文
     */
    public static String encrypt(String content, String key) {
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(Base64.getDecoder().decode(key), ALGORITHM);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            byte[] encryptedBytes = cipher.doFinal(content.getBytes("UTF-8"));
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            throw new RuntimeException("加密失败", e);
        }
    }

    /**
     * 解密
     *
     * @param encryptedContent 密文(Base64编码)
     * @param key              密钥(Base64编码)
     * @return 明文内容
     */
    public static String decrypt(String encryptedContent, String key) {
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(Base64.getDecoder().decode(key), ALGORITHM);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
            byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedContent));
            return new String(decryptedBytes, "UTF-8");
        } catch (Exception e) {
            throw new RuntimeException("解密失败", e);
        }
    }

    // 测试示例
    public static void main(String[] args) {
        // 生成密钥
        String key = generateKey();
        System.out.println("生成的密钥: " + key);

        // 待加密内容
        String originalContent = "这是一个测试内容";
        System.out.println("原始内容: " + originalContent);

        // 加密
        String encryptedContent = encrypt(originalContent, key);
        System.out.println("加密后内容: " + encryptedContent);

        // 解密
        String decryptedContent = decrypt(encryptedContent, key);
        System.out.println("解密后内容: " + decryptedContent);
    }
}