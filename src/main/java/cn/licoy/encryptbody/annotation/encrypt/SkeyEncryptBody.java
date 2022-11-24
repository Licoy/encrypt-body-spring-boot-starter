package cn.licoy.encryptbody.annotation.encrypt;

import cn.licoy.encryptbody.enums.RSAKeyType;

import java.lang.annotation.*;

@Target(value = {ElementType.METHOD, ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SkeyEncryptBody {

    String key() default "";

    RSAKeyType type() default RSAKeyType.PUBLIC;    
}
