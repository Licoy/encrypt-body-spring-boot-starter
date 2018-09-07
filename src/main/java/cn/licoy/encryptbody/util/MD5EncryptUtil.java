package cn.licoy.encryptbody.util;

import java.security.MessageDigest;

/**
 * <p>MD5加密工具类</p>
 * @author licoy.cn
 * @version 2017/11/18
 */
public class MD5EncryptUtil {

    /**
     * MD5加密-32位小写
     * */
    public static String encrypt(String encryptStr) {
        MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance("MD5");
            byte[] md5Bytes = md5.digest(encryptStr.getBytes());
            StringBuilder hexValue = new StringBuilder();
            for (byte md5Byte : md5Bytes) {
                int val = ((int) md5Byte) & 0xff;
                if (val < 16)
                    hexValue.append("0");
                hexValue.append(Integer.toHexString(val));
            }
            encryptStr = hexValue.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return encryptStr;
    }

}
