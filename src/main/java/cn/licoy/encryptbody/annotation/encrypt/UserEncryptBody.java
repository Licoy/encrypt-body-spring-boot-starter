package cn.licoy.encryptbody.annotation.encrypt;




import java.lang.annotation.*;

@Target(value = {ElementType.METHOD, ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface UserEncryptBody {

     public String crypto() default "";

}
