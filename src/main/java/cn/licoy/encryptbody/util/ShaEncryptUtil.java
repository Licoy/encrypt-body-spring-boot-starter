

package cn.licoy.encryptbody.util;


import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.digest.Digester;
import cn.licoy.encryptbody.enums.SHAEncryptType;
import cn.licoy.encryptbody.exception.DecryptMethodNotFoundException;

/**
 * <p>SHA加密工具类</p>
 *
 * @author licoy.cn
 * @version 2018/9/5
 */
public class ShaEncryptUtil {

    /**
     * SHA加密公共方法
     *
     * @param str  目标字符串
     * @param type 加密类型 {@link SHAEncryptType}
     */
    public static String encrypt(String str, SHAEncryptType type) {
        Digester digester;
        switch (type) {
            case SHA1:
                digester = SecureUtil.sha1();
                break;
            case SHA256:
                digester = SecureUtil.sha256();
                break;
            default:
                throw new DecryptMethodNotFoundException();
        }
        return String.valueOf(digester.digestHex(str));
    }
}
