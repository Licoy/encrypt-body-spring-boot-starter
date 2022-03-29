[简体中文](./README.md) | English
> This English document is translated by Google Translate. If you are willing to assist us with the documentation, please submit the relevant Pull Request.
## Introduction
`encrypt-body-spring-boot-starter` it is a unified processing method for response body encryption and request body decryption of SpringBoot controller, and supports MD5/SHA/AES/DES/RSA.

 [![](https://img.shields.io/github/release/Licoy/encrypt-body-spring-boot-starter.svg)]()
 [![](https://img.shields.io/github/issues/Licoy/encrypt-body-spring-boot-starter.svg)]()
 [![](https://img.shields.io/github/issues-pr/Licoy/encrypt-body-spring-boot-starter.svg)]()
 [![](https://img.shields.io/badge/author-Licoy-ff69b4.svg)]()
## Encryption and decryption support
- There are ways to encrypt：
    - - [x] MD5
    - - [x] SHA-1 / SHA-256
    - - [x] AES
    - - [x] DES
    - - [x] RSA
- There are ways to decrypt：
    - - [x] AES
    - - [x] DES
    - - [x] RSA
## Usage method
- Introducing dependencies in `pom.xml`：
```xml
<dependency>
    <groupId>cn.licoy</groupId>
    <artifactId>encrypt-body-spring-boot-starter</artifactId>
    <version>1.1.0</version>
</dependency>
```
- Add the @EnableEncryptBody annotation to the `Application` class corresponding to the project, for example:
```java
@EnableEncryptBody
@SpringBootApplication
public class Application {
    
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
```
- Parameter configuration
Configure the parameters in the project's `application.yml` or `application.properties` file, for example:
```yaml
encrypt:  
    body:
      aes-key: 12345678 #AES encryption key
      des-key: 12345678 #DES encryption key
```
- Encrypt the controller response body
```java
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
```
Or use `@RestController` to encrypt the method response body of the entire controller:
```java
@RestController
@EncryptBody
@RequestMapping("/test")
public class TestController {

    @GetMapping
    public String test(){
        return "hello world";
    }

}
```
## Annotated list
- [Encrypted annotation list](https://github.com/Licoy/encrypt-body-spring-boot-starter/wiki/加密注解一览表)
- [Decryption annotation list](https://github.com/Licoy/encrypt-body-spring-boot-starter/wiki/解密注解一览表)
## Discuss

- Author blog：[https://www.licoy.cn](https://www.licoy.cn)
## Open source agreement
[Apache 2.0](/LICENSE)
