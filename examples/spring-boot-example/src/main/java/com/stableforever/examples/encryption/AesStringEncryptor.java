package com.stableforever.examples.encryption;

import com.stableforever.mybatis.encryption.spi.StringEncryptor;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class AesStringEncryptor implements StringEncryptor {
    private static final String AES_KEY_STRING = "3gdpbXghjRA62rR7XjjniA==";
    private static final Logger LOGGER = LoggerFactory.getLogger(AesStringEncryptor.class);
    private static final String CIPHER_NAME = "AES/ECB/PKCS5Padding";
    private static final Charset CHARSET = StandardCharsets.UTF_8;
    private static final Key AES_KEY;

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
            byte[] bytes = cipher.doFinal(raw.getBytes(CHARSET));
            return Hex.encodeHexString(bytes);
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
        try {
            Cipher cipher = Cipher.getInstance(CIPHER_NAME);
            cipher.init(Cipher.DECRYPT_MODE, AES_KEY);
            byte[] encrytpedBytes = Hex.decodeHex(encrypted.toCharArray());
            byte[] bytes = cipher.doFinal(encrytpedBytes);
            return new String(bytes, CHARSET);
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
