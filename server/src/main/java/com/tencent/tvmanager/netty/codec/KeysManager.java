package com.tencent.tvmanager.netty.codec;

import com.tencent.tvmanager.util.L;

import java.util.concurrent.ConcurrentHashMap;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by guochang on 2015/3/16.
 * AES key的管理类
 */
public class KeysManager {
    private volatile static KeysManager mInstance;
    private byte[] aesKey = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16};
    private ConcurrentHashMap<String, byte[]> mKeysMap;//管理客户端传过来的key

    private KeysManager() {
        mKeysMap = new ConcurrentHashMap<>();
    }

    public static KeysManager getInstance() {
        if (mInstance == null) {
            synchronized (KeysManager.class) {
                if (mInstance == null) {
                    mInstance = new KeysManager();
                }
            }
        }

        return mInstance;
    }

    /**
     * 添加key
     *
     * @param id  客户端id(host:port)
     * @param key
     */
    public void putKey(String id, byte[] key) {
        mKeysMap.put(id, key);
    }

    /**
     * @param id
     */
    public void removeKey(String id) {
        mKeysMap.remove(id);
    }

    /**
     * 获取key
     *
     * @param id 客户端id(host:port)
     * @return 返回key
     */
    public byte[] getKey(String id) {
        return mKeysMap.get(id);
    }

    /**
     * AES编码
     *
     * @param contents
     * @return
     */
    public byte[] encryptBody(byte[] contents, byte[] key) {
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

    public byte[] decryptAESKey(byte[] contents) {
        return decryptBody(contents, aesKey);
    }

    /**
     * AES解码
     *
     * @param contents
     * @return
     */
    public byte[] decryptBody(byte[] contents, byte[] key) {
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

    public void onDestory() {
        mInstance = null;
    }
}
