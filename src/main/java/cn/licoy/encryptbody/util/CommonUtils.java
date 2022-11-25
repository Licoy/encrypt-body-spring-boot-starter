package cn.licoy.encryptbody.util;

import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.asymmetric.RSA;
import cn.licoy.encryptbody.bean.ISecurityInfo;
import cn.licoy.encryptbody.exception.EncryptBodyFailException;
import cn.licoy.encryptbody.exception.IllegalSecurityTypeException;
import cn.licoy.encryptbody.exception.KeyNotConfiguredException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * <p>工具类</p>
 *
 * @author licoy.cn
 * @version 2018/9/7
 */
public class CommonUtils {

    public static String checkAndGetKey(String k1, String k2, String keyName) {
        if (StrUtil.isEmpty(k1) && StrUtil.isEmpty(k2)) {
            throw new KeyNotConfiguredException(String.format("%s is not configured (未配置%s)", keyName, keyName));
        }
        if (k1 == null) {
            return k2;
        }
        return k1;
    }

    /**
     * 根据信息对象获取RSA实例
     *
     * @param info 信息
     * @return rsa
     */
    public static RSA infoBeanToRsaInstance(ISecurityInfo info) {
        RSA rsa;

        try {
            switch (info.getRsaKeyType()) {
                case PUBLIC:
                    rsa = loadRsaPublicKey(info.getKey());
                    break;
                case PRIVATE:
                    rsa = loadRsaPrivateKey(info.getKey());
                    break;
                default:
                    throw new IllegalSecurityTypeException();
            }
        } catch(Exception e) {
            e.printStackTrace();
            throw new RuntimeException("failed to load rsa, " + e.getMessage());
        }
        

        return rsa;
    }

    private static RSA loadRsaPublicKey(String key) throws Exception {
        //Try to load the key as value. If exception occurs, treat it as a file and try again.
        try {
            return new RSA(null, SecureUtil.decode(key));
        } catch (Exception e) {
            key = new String(java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(key)), java.nio.charset.StandardCharsets.UTF_8);
            return new RSA(null, SecureUtil.decode(key));
        } 
    }

    private static RSA loadRsaPrivateKey(String key) throws Exception {
        //Try to load the key as value. If exception occurs, treat it as a file and try again.
        try {
            return new RSA(SecureUtil.decode(key), null);
        } catch (Exception e) {
            key = java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(key)).toString();
            return new RSA(SecureUtil.decode(key), null);
        } 

    }


    /**
     * 是否转换为string
     *
     * @param clazz class
     * @return 是否
     */
    public static boolean isConvertToString(Class<?> clazz) {
        return clazz.equals(String.class) || ClassUtil.isPrimitiveWrapper(clazz);
    }

    /**
     * 转换为string
     *
     * @param val    数据
     * @param mapper jackson
     * @return string
     */
    public static String convertToStringOrJson(Object val, ObjectMapper mapper) {
        if (isConvertToString(val.getClass())) {
            return String.valueOf(val);
        }
        try {
            return mapper.writeValueAsString(val);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new EncryptBodyFailException(e.getMessage());
        }
    }

}
