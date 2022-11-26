package cn.licoy.encryptbody.annotation.encrypt;




import java.lang.annotation.*;

@Target(value = {ElementType.METHOD, ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CustomEncryptBody {

     /**
      * Custom encryption/decryption provider class name.
      * 
      * @return provider class name
      */
     public String providerClassName() default "";

     /**
      * Name of the static method in provider class to be used for encryption.
      * NOTE: encryption method should have a signature like `public String encrypt(String)`.
      * 
      * @return
      */
     public String encryptMethodName() default "encrypt";

     /**
      * Name of the static method in provider class to be used for decryption.
      * NOTE: decryption method should have a signature like `public String decrypt(String)`.
      * 
      * @return
      */
      public String decryptMethodName() default "decrypt";

}
