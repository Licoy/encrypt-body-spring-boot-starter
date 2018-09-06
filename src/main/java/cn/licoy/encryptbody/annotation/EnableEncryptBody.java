package cn.licoy.encryptbody.annotation;

import cn.licoy.encryptbody.advice.EncryptBodyAdvice;
import cn.licoy.encryptbody.config.EncryptBodyConfig;
import cn.licoy.encryptbody.config.HttpConverterConfig;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author licoy.cn
 * @version 2018/9/6
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import({EncryptBodyConfig.class,HttpConverterConfig.class,EncryptBodyAdvice.class})
public @interface EnableEncryptBody {
}
