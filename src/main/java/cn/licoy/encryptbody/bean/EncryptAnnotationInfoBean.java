package cn.licoy.encryptbody.bean;

import cn.licoy.encryptbody.enums.EncryptBodyMethod;
import cn.licoy.encryptbody.enums.RSAKeyType;
import cn.licoy.encryptbody.enums.SHAEncryptType;
import lombok.*;

/**
 * <p>加密注解信息</p>
 *
 * @author licoy.cn
 * @version 2018/9/6
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EncryptAnnotationInfoBean implements ISecurityInfo {

    private EncryptBodyMethod encryptBodyMethod;

    private SHAEncryptType shaEncryptType;

    private String key;

    private RSAKeyType rsaKeyType;

    private String providerClassName;

    private String encryptMethodName;

    private String decryptMethodName;


}
