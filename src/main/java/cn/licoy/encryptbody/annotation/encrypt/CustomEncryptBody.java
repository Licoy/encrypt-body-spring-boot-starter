package cn.licoy.encryptbody.annotation.encrypt;




import java.lang.annotation.*;

@Target(value = {ElementType.METHOD, ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CustomEncryptBody {

     public String providerClassName() default "";

     public String encryptMethodName() default "encrypt";

     public String decryptMethodName() default "decrypt";

}
