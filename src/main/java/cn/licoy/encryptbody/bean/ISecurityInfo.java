package cn.licoy.encryptbody.bean;

import cn.licoy.encryptbody.enums.RSAKeyType;


import java.io.Serializable;

/**
 * 安全信息实体
 *
 * @author Licoy
 * @date 2022/3/29
 */
public interface ISecurityInfo extends Serializable {

    /**
     * 获取key
     *
     * @return key
     */
    String getKey();

    /**
     * 获取rsa的密钥类型
     *
     * @return 密钥类型
     */
    RSAKeyType getRsaKeyType();


    /**
     * Get the class name of custom encryption/decryption provider
     * 
     * @return class name
     */
    String getProviderClassName();

    /**
     * Get the method name to be used to encryption.
     * 
     * @return encryption method name.
     */
    String getEncryptMethodName();

    /**
     * Get the method name to be used to decryption.
     * 
     * @return decryption method name.
     */
    String getDecryptMethodName();

}
