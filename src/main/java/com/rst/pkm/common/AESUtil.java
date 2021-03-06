package com.rst.pkm.common;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Arrays;

/**
 *
 * @author hujia
 * @date 2017/2/28
 */
public class AESUtil {
    public static final String IV_STRING = "bitmain.com12345";

    public static byte[] aesEncrypt(byte[] data, byte[] key) {
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
            byte[] initParam = IV_STRING.getBytes();
            IvParameterSpec ivParameterSpec = new IvParameterSpec(initParam);

            // 指定加密的算法、工作模式和填充方式
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
            return cipher.doFinal(data);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static byte[] aesEncrypt(byte[] data, String key) {
        return aesEncrypt(data, key.getBytes());
    }

    public static byte[] aesDecrypt(byte[] data, String key) {
        return aesDecrypt(data, key.getBytes());
    }

    public static byte[] aesDecrypt(byte[] data, byte[] key) {
        try {
            byte[] enCodeFormat = key;
            SecretKeySpec secretKey = new SecretKeySpec(enCodeFormat, "AES");

            byte[] initParam = IV_STRING.getBytes();
            IvParameterSpec ivParameterSpec = new IvParameterSpec(initParam);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);
            return cipher.doFinal(data);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void wipe(byte[] data) {
        if (data != null) {
            Arrays.fill(data, (byte)0);
            BtRandom r = new BtRandom();
            byte[] bytes = r.nextBytes(data.length);
            System.arraycopy(bytes, 0, data, 0, data.length);
            Arrays.fill(data, (byte)0);
        }
    }

    public static byte[] aesKeyFrom(String seed) {
        byte[] adminBytes = Converter.ripemd160(seed.getBytes());
        byte[] result = new byte[16];
        System.arraycopy(adminBytes, 0, result, 0, 16);

        return result;
    }
}
