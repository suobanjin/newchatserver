package zzuli.zw.main.utils;

import java.security.SecureRandom;

public class SecureTokenUtil {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    /**
     * 生成指定长度的安全随机字符串
     */
    public static String generateSecureToken(int length) {
        StringBuilder token = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            token.append(CHARACTERS.charAt(SECURE_RANDOM.nextInt(CHARACTERS.length())));
        }
        return token.toString();
    }

    /**
     * 生成默认长度为32的Session ID
     */
    public static String generateSessionId() {
        return generateSecureToken(32);
    }
}

