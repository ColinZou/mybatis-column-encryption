package com.stableforever.mybatis.encryption.api;

import com.stableforever.mybatis.encryption.spi.StringEncryptor;
import lombok.extern.slf4j.Slf4j;

/**
 * 什么也不做的string加密
 * @author colin
 * @version 0.1
 */
@Slf4j
public class NoopStringEncryptor implements StringEncryptor {
    @Override
    public String encrypt(String raw) {
        log.debug("Encrypting string {}", raw);
        return raw;
    }
    @Override
    public String decrypt(String encrypted) {
        log.debug("Decrypting string {}", encrypted);
        return encrypted;
    }
}
