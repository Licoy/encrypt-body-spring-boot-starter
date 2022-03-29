package cn.licoy.encryptbody.enums;

import cn.hutool.crypto.asymmetric.KeyType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

/**
 * RSA的密钥类型
 *
 * @author Licoy
 * @date 2022/3/28
 */
@AllArgsConstructor
@Getter
public enum RSAKeyType implements Serializable {

    /**
     * 公钥
     */
    PUBLIC(1, KeyType.PublicKey),

    /**
     * 私钥
     */
    PRIVATE(2, KeyType.PrivateKey);

    public final int type;

    public final KeyType toolType;

    }
