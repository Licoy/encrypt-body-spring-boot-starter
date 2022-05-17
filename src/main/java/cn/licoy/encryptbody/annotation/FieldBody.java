package cn.licoy.encryptbody.annotation;

import java.lang.annotation.*;

/**
 * 字段编码/加解密主体
 *
 * @author Licoy
 * @date 2022/5/15
 */
@Target(value = {ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FieldBody {

    /**
     * <p>设置字段</p>
     * <p>如果需要编码/加密的字段不是String类型，则加密结果是无法写入的，所以需要定义写入到指定字段。</p>
     * <p>此方法只在字段属性上生效</p>
     *
     * @return 字段名称
     */
    String field() default "";

    /**
     * 若{setField}生效，则编码/加密成功之后清除原字段上的值
     *
     * @return 是否清除
     */
    boolean clearValue() default true;

}
