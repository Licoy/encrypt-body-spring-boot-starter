package cn.licoy.encryptbody.util;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.asymmetric.RSA;
import cn.licoy.encryptbody.bean.ISecurityInfo;
import cn.licoy.encryptbody.exception.IllegalSecurityTypeException;
import cn.licoy.encryptbody.exception.KeyNotConfiguredException;

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

    public static RSA infoBeanToRsaInstance(ISecurityInfo info) {
        RSA rsa;
        switch (info.getRsaKeyType()) {
            case PUBLIC:
                rsa = new RSA(null, SecureUtil.decode(info.getKey()));
                break;
            case PRIVATE:
                rsa = new RSA(SecureUtil.decode(info.getKey()), null);
                break;
            default:
                throw new IllegalSecurityTypeException();
        }
        return rsa;
    }

}
