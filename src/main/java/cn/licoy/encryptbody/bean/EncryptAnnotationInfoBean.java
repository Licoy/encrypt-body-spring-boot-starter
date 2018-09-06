package cn.licoy.encryptbody.bean;

import cn.licoy.encryptbody.enums.EncryptBodyMethod;
import cn.licoy.encryptbody.enums.SHAEncryptType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author licoy.cn
 * @version 2018/9/6
 * 加密注解信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EncryptAnnotationInfoBean {

    private EncryptBodyMethod encryptBodyMethod;

    private String key;

    private SHAEncryptType shaEncryptType;

}
