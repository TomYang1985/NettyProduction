package com.tencent.tvmanager.netty.codec;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by guochang on 2015/3/16.
 */
public class Algorithm {
    //    private static Algorithm mInstance;
//    private static final int AES_KEY_LENGTH = 16;
    private static byte[] aesKey = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16};

    /**
     * AES编码
     *
     * @param contents
     * @return
     */
    public static byte[] encryptAES(byte[] contents) {
        byte[] encodedBytes = null;

        try {
            SecretKeySpec skeySpec = new SecretKeySpec(aesKey, "AES");
            Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
            IvParameterSpec iv = new IvParameterSpec(aesKey);
            c.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
            encodedBytes = c.doFinal(contents);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return encodedBytes;
    }

    /**
     * AES解码
     *
     * @param contents
     * @return
     */
    public static byte[] decryptAES(byte[] contents) {
        byte[] decodedBytes = null;

        try {
            SecretKeySpec skeySpec = new SecretKeySpec(aesKey, "AES");
            Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
            IvParameterSpec iv = new IvParameterSpec(aesKey);
            c.init(Cipher.DECRYPT_MODE, skeySpec, iv);
            decodedBytes = c.doFinal(contents);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return decodedBytes;
    }

}
