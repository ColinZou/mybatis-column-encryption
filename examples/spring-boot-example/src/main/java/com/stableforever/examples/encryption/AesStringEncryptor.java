package com.stableforever.examples.encryption;

import com.stableforever.mybatis.encryption.spi.StringEncryptor;
import org.apache.commons.codec.DecoderException;
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
import org.apache.commons.codec.binary.Hex;

public class AesStringEncryptor implements StringEncryptor {
    private static final String AES_KEY_STRING = "3gdpbXghjRA62rR7XjjniA==";
    private static final Logger LOGGER = LoggerFactory.getLogger(AesStringEncryptor.class);
    private static final String CIPHER_NAME = "AES/ECB/PKCS5Padding";
    private static final Charset CHARSET = StandardCharsets.UTF_8;
    private static final Key AES_KEY;
    private static final int MULTIPLE = 32;

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
            final ByteBuffer buffer = ByteBuffer.allocate(2);
            for (char c : raw.toCharArray()) {
                buffer.clear();
                buffer.putChar(c);
                buffer.flip();
                byte[] encrypted = cipher.doFinal(buffer.array());
                String hex = Hex.encodeHexString(encrypted);
                stringBuilder.append(hex);
            }
            buffer.clear();
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
            final ByteBuffer sliceBuffer = ByteBuffer.allocate(2);
            final StringBuilder stringBuilder = new StringBuilder();
            int length = encrypted.length();
            for (int i = 0; i < length ; i += stepSize) {
                byte[] encryptedBytes = Hex.decodeHex(encrypted.substring(i, i + stepSize));
                byte[] rawBytes = cipher.doFinal(encryptedBytes);
                sliceBuffer.clear();
                sliceBuffer.put(rawBytes);
                sliceBuffer.flip();
                stringBuilder.append(sliceBuffer.getChar());
            }
            sliceBuffer.clear();
            return stringBuilder.toString();
        } catch (NoSuchAlgorithmException |
                NoSuchPaddingException |
                InvalidKeyException |
                BadPaddingException |
                IllegalBlockSizeException |
                DecoderException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return encrypted;
    }
}
