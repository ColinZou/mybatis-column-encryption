package com.stableforever.mybatis.encryption.spi;

/**
 * 字符串加密工具
 * @author colin
 * @version 0.1
 */
public interface StringEncryptor {
    /**
     * 加密
     * @param raw   raw string
     * @return encrypted/encoded string
     */
    String encrypt(String raw);

    /**
     * 解密
     * @param encrypted encrypted string
     * @return decrypted/decoded string
     */
    String decrypt(String encrypted);
}
