package com.stableforever.examples.encryption;

import javax.crypto.KeyGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class AesKeyGenerator {
    public static void main(String[] args) throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        byte[] keyBytes = keyGenerator.generateKey().getEncoded();
        String base64 = Base64.getUrlEncoder().encodeToString(keyBytes);
        System.out.println(base64);
    }
}
