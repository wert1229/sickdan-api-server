package com.kdpark.sickdan.util;

import io.netty.handler.codec.base64.Base64Encoder;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class CryptUtil {
    public static final String KEY= "0123456789012345";

    public static String encrypt(String text) {
        SecretKeySpec aes = new SecretKeySpec(KEY.getBytes(), "AES");

        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, aes);
            byte[] result = cipher.doFinal(text.getBytes());

            return Base64.getEncoder().encodeToString(result);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String decrypt(String text) {
        if (text.length() % 16 != 0) return "";

        SecretKeySpec aes = new SecretKeySpec(KEY.getBytes(), "AES");

        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, aes);
            byte[] result = cipher.doFinal(Base64.getDecoder().decode(text));

            return new String(result);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}
