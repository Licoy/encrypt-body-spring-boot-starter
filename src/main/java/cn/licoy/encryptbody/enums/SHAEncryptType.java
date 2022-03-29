package cn.licoy.encryptbody.enums;

import cn.hutool.crypto.SecureUtil;
import lombok.AllArgsConstructor;

import java.util.function.Function;

/**
 * <p>SHA加密类型</p>
 * @author licoy.cn
 * @version 2018/9/6
 */
@AllArgsConstructor
public enum  SHAEncryptType {

    /**
     * SHA TYPE
     */
    SHA1,
    SHA256
    ;


}
