package cn.licoy.encryptbody.config;

import cn.licoy.encryptbody.advice.EncryptResponseBodyAdvice;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.context.annotation.Configuration;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * <p>加密数据配置读取类</p>
 * <p>在SpringBoot项目中的application.yml中添加配置信息即可</p>
 * <pre>
 *     encrypt:
 *      body:
 *       aes-key: 12345678 # AES加密秘钥
 *       des-key: 12345678 # DES加密秘钥
 * </pre>
 *
 * @author licoy.cn
 * @version 2018/9/6
 */
@ConfigurationProperties(prefix = "encrypt.body")
@Configuration
@Data
public class EncryptBodyConfig {

    private String aesKey;

    private String desKey;

    private Charset encoding = StandardCharsets.UTF_8;

    /**
     * aes 或者 des 加密后使用编码处理密文(base64 或 hex, 防止特殊字符转码)
     *
     * @see EncryptResponseBodyAdvice#switchEncrypt(java.lang.String, cn.licoy.encryptbody.bean.EncryptAnnotationInfoBean)
     */
    @NestedConfigurationProperty
    private EncryptEncodeType encryptEncodeType = EncryptEncodeType.HEX;

    public enum EncryptEncodeType {
        HEX,
        BASE64
    }

}
