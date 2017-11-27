package com.netty.client.codec;

import java.util.concurrent.atomic.AtomicInteger;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by guochang on 2015/3/16.
 */
public class KeyManager {
//    //    private static KeyManager mInstance;
////    private static final int AES_KEY_LENGTH = 16;
//    private static byte[] aesKey = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16};
//
//    /**
//     * AES编码
//     *
//     * @param contents
//     * @return
//     */
//    public static byte[] encryptAES(byte[] contents) {
//        byte[] encodedBytes = null;
//
//        try {
//            SecretKeySpec skeySpec = new SecretKeySpec(aesKey, "AES");
//            Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
//            IvParameterSpec iv = new IvParameterSpec(aesKey);
//            c.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
//            encodedBytes = c.doFinal(contents);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return encodedBytes;
//    }
//
//    /**
//     * AES解码
//     *
//     * @param contents
//     * @return
//     */
//    public static byte[] decryptAES(byte[] contents) {
//        byte[] decodedBytes = null;
//
//        try {
//            SecretKeySpec skeySpec = new SecretKeySpec(aesKey, "AES");
//            Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
//            IvParameterSpec iv = new IvParameterSpec(aesKey);
//            c.init(Cipher.DECRYPT_MODE, skeySpec, iv);
//            decodedBytes = c.doFinal(contents);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return decodedBytes;
//    }

    public static final int KEY_EXCHANGE_NULL = -1;//未接收到服务端交换密钥的响应
    public static final int KEY_EXCHANGE_SUCC = 1;//密钥交换成功
    public static final int KEY_EXCHANGE_FAIL = 2;//服务端交换密钥的响应返回，但返回状态码！=200
    public static final int KEY_EXCHANGE_RESPONSE_DECODE_ERROR = 3;//交换密钥响应解码错误
    private volatile static KeyManager mInstance;
    private static final int AES_KEY_LENGTH = 16;
    private volatile byte[] bodyAESKey;
    private byte[] aesKey = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16};
    private AtomicInteger mKeyExchangeStatus;//密钥交换的状态

    private KeyManager() {
        mKeyExchangeStatus = new AtomicInteger(KEY_EXCHANGE_NULL);
    }

    public static KeyManager getInstance() {
        if (mInstance == null) {
            synchronized (KeyManager.class) {
                if (mInstance == null) {
                    mInstance = new KeyManager();
                }
            }
        }

        return mInstance;
    }

    /**
     * 设置密钥交换状态
     * @param status
     */
    public void setKeyExchangeStatus(int status) {
        mKeyExchangeStatus.set(status);
    }

    /**
     * 获取密钥交换状态
     * @return
     */
    public int getKeyExchangeStatus() {
        return mKeyExchangeStatus.get();
    }

    public void resetKeyExchangeStatus(){
        setKeyExchangeStatus(KEY_EXCHANGE_NULL);
    }

    public void setBodyAESKey(byte[] bodyAESKey) {
        this.bodyAESKey = bodyAESKey;
    }

    /**
     * 生成真正加密数据AES key
     */
    public byte[] generateAESKey() {
        bodyAESKey = new byte[AES_KEY_LENGTH];
        for (int i = 0; i < bodyAESKey.length; i++) {
            bodyAESKey[i] = (byte) Math.floor(Math.random() * 256);
        }

        return bodyAESKey;
    }

    public byte[] encryptAESKey(byte[] keys) {
        return encryptAES(keys, aesKey);
    }

    public byte[] encryptBody(byte[] contents) {
        return encryptAES(contents, bodyAESKey);
    }

    /**
     * AES编码
     *
     * @param contents
     * @return
     */
    private byte[] encryptAES(byte[] contents, byte[] key) {
        byte[] encodedBytes = null;

        try {
            SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
            Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
            IvParameterSpec iv = new IvParameterSpec(key);
            c.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
            encodedBytes = c.doFinal(contents);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return encodedBytes;
    }

    public byte[] decryptAESKey() {
        return decryptAES(bodyAESKey, aesKey);
    }

    public byte[] decryptBody(byte[] contents) {
        return decryptAES(contents, bodyAESKey);
    }

    /**
     * AES解码
     *
     * @param contents
     * @return
     */
    private byte[] decryptAES(byte[] contents, byte[] key) {
        byte[] decodedBytes = null;

        try {
            SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
            Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
            IvParameterSpec iv = new IvParameterSpec(key);
            c.init(Cipher.DECRYPT_MODE, skeySpec, iv);
            decodedBytes = c.doFinal(contents);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return decodedBytes;
    }
}
