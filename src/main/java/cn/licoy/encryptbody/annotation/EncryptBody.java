package cn.licoy.encryptbody.annotation;


import cn.licoy.encryptbody.enums.EncryptBodyMethod;
import cn.licoy.encryptbody.enums.SHAEncryptType;

import java.lang.annotation.*;

/**
 * @author licoy.cn
 * @version 2018/9/4
 * 加密响应Body数据，可用于整个控制类或者某个控制器上
 */
@Target(value = {ElementType.METHOD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EncryptBody {

    EncryptBodyMethod value() default EncryptBodyMethod.MD5;

    String otherKey() default "";

    SHAEncryptType shaType() default SHAEncryptType.SHA256;

}
