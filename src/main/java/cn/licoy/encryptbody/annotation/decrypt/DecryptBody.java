package cn.licoy.encryptbody.annotation.decrypt;

import cn.licoy.encryptbody.enums.DecryptBodyMethod;
import cn.licoy.encryptbody.enums.SHAEncryptType;

import java.lang.annotation.*;

/**
 * <p>解密含有{@link org.springframework.web.bind.annotation.RequestBody}注解的参数请求数据，可用于整个控制类或者某个控制器上</p>
 * @author licoy.cn
 * @version 2018/9/7
 */
@Target(value = {ElementType.METHOD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DecryptBody {

    DecryptBodyMethod value() default DecryptBodyMethod.AES;

    String otherKey() default "";

}
