package cn.licoy.encryptbody.annotation.encrypt;

import cn.licoy.encryptbody.enums.RSAKeyType;

import java.lang.annotation.*;

/**
 * @author licoy.cn
 * @version 2018/9/4
 * @see EncryptBody
 */
@Target(value = {ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RSAEncryptBody {

    /**
     * 自定义私钥
     *
     * @return 私钥
     */
    String key() default "";

    /**
     * 类型
     *
     * @return 公钥
     */
    RSAKeyType type() default RSAKeyType.PUBLIC;

}
