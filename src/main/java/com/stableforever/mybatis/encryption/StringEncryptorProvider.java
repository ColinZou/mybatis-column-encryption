package com.stableforever.mybatis.encryption;

import com.stableforever.mybatis.encryption.api.NoopStringEncryptor;
import com.stableforever.mybatis.encryption.spi.StringEncryptor;
import lombok.extern.slf4j.Slf4j;

import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * 字符串加密工具提供者
 *
 * @author colin
 * @version 0.1
 */
@Slf4j
public class StringEncryptorProvider {
    /**
     * 读取字符串加密器
     *
     * @return StringEncryptor
     */
    public static StringEncryptor getEncryptor() {
        // 加载StringEncryptor
        ServiceLoader<StringEncryptor> encryptors = ServiceLoader.load(StringEncryptor.class);
        Iterator<StringEncryptor> encryptorIterator = encryptors.iterator();
        StringEncryptor defaultEncryptor = null;
        StringEncryptor alternativeEncryptor = null;
        while (encryptorIterator.hasNext()) {
            StringEncryptor item = encryptorIterator.next();
            // find the default one
            if (item.getClass().equals(NoopStringEncryptor.class)) {
                defaultEncryptor = item;
            } else {
                alternativeEncryptor = item;
            }
        }
        if (null != alternativeEncryptor) {
            return alternativeEncryptor;
        }
        if (null != defaultEncryptor) {
            return defaultEncryptor;
        }
        log.error("No implementation for {} found", StringEncryptor.class.getCanonicalName());
        return null;
    }
}
