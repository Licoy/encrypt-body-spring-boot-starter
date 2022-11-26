[简体中文](./README.md) | English
## Introduce
`encrypt-body-spring-boot-starter` is a unified annotation processing method for response body encoding/encryption and request body decryption for `springboot` controller, and supports MD5/SHA/AES/DES/RSA.

[![](https://img.shields.io/github/release/Licoy/encrypt-body-spring-boot-starter.svg)]()
[![](https://img.shields.io/github/issues/Licoy/encrypt-body-spring-boot-starter.svg)]()
[![](https://img.shields.io/github/issues-pr/Licoy/encrypt-body-spring-boot-starter.svg)]()
[![](https://img.shields.io/badge/author-Licoy-ff69b4.svg)]()
## Support
- The ways in which encoding/encryption can be performed are:
    - - [x] MD5
    - - [x] SHA-1/SHA-256
    - - [x] AES
    - - [x] DES
    - - [x] RSA
    - - [x] CUSTOM
- The methods that can be decrypted are:
    - - [x] AES
    - - [x] DES
    - - [x] RSA
    - - [x] CUSTOM
## Import registration
### Import dependencies
Introduce dependencies in the project's `pom.xml`:
````xml
<dependency>
    <groupId>cn.licoy</groupId>
    <artifactId>encrypt-body-spring-boot-starter</artifactId>
    <version>1.2.3</version>
</dependency>
````
### Enable component
- Add the `@EnableEncryptBody` annotation to the `Application` class corresponding to the project, such as:
````java
@EnableEncryptBody
@SpringBootApplication
public class Application {
    
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
````
### Configuration parameters
Add parameter configuration in the `application.yml` or `application.properties` file of the project, for example:
````yaml
encrypt:
    body:
      aes-key: 12345678 #AES encryption key
      des-key: 12345678 #DES encryption key
      # more...
````
## Use
### Valid for the entire controller
````java
@RestController
@EncryptBody
@RequestMapping("/test")
public class TestController {

    @GetMapping
    public String test(){
        return "hello world";
    }

}
````
### Valid for a single request
````java
@Controller
@RequestMapping("/test")
public class TestController {

    @GetMapping
    @ResponseBody
    @EncryptBody(value = EncryptBodyMethod.AES)
    public String test(){
        return "hello world";
    }

}
````
### Effective on the declared class of the response
````java
@Data
@EncryptBody
public class User implements Serializable {

    private String name;

    private String email;

    private Integer number;

    private String numberValue;

}
````
### Effective for a single attribute of the declared class
````java
@Data
@EncryptBody
@FieldBody
public class User implements Serializable {

    private String name;

    @FieldBody
    @AESEncryptBody(key = "1234567812345678")
    private String email;

    @FieldBody(field = "numberValue", clearValue = true)
    @DESEncryptBody(key = "1234567812345678")
    private Integer number;

    private String numberValue;

}
````

## Using CUSTOM encryption/decryption
````java
@Data
@EncryptBody
@FieldBody
public class User implements Serializable {

    @CustomDecryptBody(providerClassName = "com.myorg.mypkg.MyCryptoProvider", decryptMethodName = "myDecryptionFunction")
    private String name;

    @CustomEncryptBody(providerClassName = "com.myorg.mypkg.MyCryptoProvider", encryptMethodName = "myEncryptionFunction")
    private String numberValue;



}
````

Implement the `MyCryptoProvider` class with `myEncryptionFunction` as shown below.
```java
package com.myorg.mypkg;

public class MyCryptoProvider {
    String myEncryptionFunction(String input) {
        // code to encrypt input and return the expected encrypted data
    }

    String myDecryptionFunction(String input) {
        // code to decrypt input and return the expected decrypted data
    }

}
```



## Annotation list
- [Encryption/Encryption Annotation List](https://github.com/Licoy/encrypt-body-spring-boot-starter/wiki/加密注解一览表)
- [Decryption Annotation List](https://github.com/Licoy/encrypt-body-spring-boot-starter/wiki/解密注解一览表)
## License
[Apache 2.0](/LICENSE)