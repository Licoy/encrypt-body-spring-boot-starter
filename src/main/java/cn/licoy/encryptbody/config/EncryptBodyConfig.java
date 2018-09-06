package cn.licoy.encryptbody.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author licoy.cn
 * @version 2018/9/6
 */
@ConfigurationProperties(prefix = "encrypt.body")
@Configuration
@Data
public class EncryptBodyConfig {

    private String aesKey;

    private String desKey;



}
