package com.stableforever.examples.encryption;

import com.stableforever.mybatis.encryption.spi.StringEncryptor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Slf4j
public class AesStringEncryptor implements StringEncryptor {
    /**
     * AES加密的密钥
     */
    private static final String AES_KEY_STRING = "3gdpbXghjRA62rR7XjjniA==";
    private static final Logger LOGGER = LoggerFactory.getLogger(AesStringEncryptor.class);
    /**
     * AES加密的cipher名称
     */
    private static final String CIPHER_NAME = "AES/ECB/PKCS5Padding";
    private static final Charset CHARSET = StandardCharsets.UTF_8;
    private static final Key AES_KEY;
    /**
     * AES128加密，每个字符加密以后的字节为16，两个字节一个字符
     */
    private static final int MULTIPLE = 8;
    /**
     * 字符的字节大小
     */
    private static final int CHAR_SIZE = 2;

    static {
        byte[] keyBytes = AES_KEY_STRING.getBytes(CHARSET);
        AES_KEY = new SecretKeySpec(Base64.getUrlDecoder().decode(keyBytes), "AES");
    }

    @Override
    public String encrypt(String raw) {
        if (StringUtils.isEmpty(raw)) {
            return raw;
        }
        try {
            Cipher cipher = Cipher.getInstance(CIPHER_NAME);
            cipher.init(Cipher.ENCRYPT_MODE, AES_KEY);
            final StringBuilder stringBuilder = new StringBuilder();
            // 用于转换字节为字符的buffer
            final ByteBuffer buffer = ByteBuffer.allocate(CHAR_SIZE);
            // 按单个字符做加密
            char[] chars = raw.toCharArray();
            for (int i = 0; i < chars.length; i++) {
                char c = chars[i];
                buffer.putChar(c);
                buffer.flip();
                // 加密
                byte[] encrypted = cipher.doFinal(buffer.array());
                buffer.clear();
                // 转换加密以后的字节为字符
                String part = bytesToString(encrypted);
                log.debug("Encrypted {} to {}, encrypted length = {}", c, part, part.length());
                stringBuilder.append(part);
            }
            return stringBuilder.toString();
        } catch (NoSuchAlgorithmException |
                NoSuchPaddingException |
                InvalidKeyException |
                BadPaddingException |
                IllegalBlockSizeException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return raw;
    }

    /**
     * 转换为字符串
     *
     * @param bytes
     * @return
     */
    private String bytesToString(final byte[] bytes) {
        int length = bytes.length;
        StringBuilder builder = new StringBuilder(length / CHAR_SIZE);
        ByteBuffer buffer = ByteBuffer.allocate(CHAR_SIZE);
        for (int i = 0; i < length; i += CHAR_SIZE) {
            buffer.put(bytes[i]);
            buffer.put(bytes[i + 1]);
            buffer.flip();
            builder.append(buffer.getChar());
            buffer.clear();
        }
        return builder.toString();
    }

    /**
     * 转换string为byte
     *
     * @param input
     * @return
     */
    private byte[] stringToBytes(final String input) {
        ByteBuffer buffer = ByteBuffer.allocate(input.length() * CHAR_SIZE);
        for (char c : input.toCharArray()) {
            buffer.putChar(c);
        }
        byte[] bytes = buffer.array();
        buffer.clear();
        return bytes;
    }

    @Override
    public String decrypt(String encrypted) {
        if (StringUtils.isEmpty(encrypted)) {
            return encrypted;
        }
        int stepSize = MULTIPLE;
        if (encrypted.length() % stepSize != 0) {
            LOGGER.warn("{} was not encrypted");
            return encrypted;
        }
        try {
            Cipher cipher = Cipher.getInstance(CIPHER_NAME);
            cipher.init(Cipher.DECRYPT_MODE, AES_KEY);
            // 每个字字符的buffer
            final ByteBuffer sliceBuffer = ByteBuffer.allocate(CHAR_SIZE);
            final StringBuilder stringBuilder = new StringBuilder();
            int length = encrypted.length();
            for (int i = 0; i < length; i += stepSize) {
                // 取出待解码的字符串
                String stringPart = encrypted.substring(i, i + stepSize);
                byte[] encryptedBytes = stringToBytes(stringPart);
                byte[] rawBytes = cipher.doFinal(encryptedBytes);
                sliceBuffer.put(rawBytes);
                sliceBuffer.flip();
                stringBuilder.append(sliceBuffer.getChar());
                sliceBuffer.clear();
            }
            return stringBuilder.toString();
        } catch (NoSuchAlgorithmException |
                NoSuchPaddingException |
                InvalidKeyException |
                BadPaddingException |
                IllegalBlockSizeException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return encrypted;
    }
}
