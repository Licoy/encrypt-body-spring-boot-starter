package cn.licoy.encryptbody.bean;

import cn.licoy.encryptbody.enums.DecryptBodyMethod;
import cn.licoy.encryptbody.enums.EncryptBodyMethod;
import cn.licoy.encryptbody.enums.SHAEncryptType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>解密注解信息</p>
 * @author licoy.cn
 * @version 2018/9/6
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DecryptAnnotationInfoBean {

    private DecryptBodyMethod decryptBodyMethod;

    private String key;

}
