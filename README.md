简体中文 | [English](./README_EN.md)
## 介绍
`encrypt-body-spring-boot-starter`是对`springboot`控制器统一的响应体编码/加密与请求体解密的注解处理方式，支持MD5/SHA/AES/DES/RSA。

 [![](https://img.shields.io/github/release/Licoy/encrypt-body-spring-boot-starter.svg)]()
 [![](https://img.shields.io/github/issues/Licoy/encrypt-body-spring-boot-starter.svg)]()
 [![](https://img.shields.io/github/issues-pr/Licoy/encrypt-body-spring-boot-starter.svg)]()
 [![](https://img.shields.io/badge/author-Licoy-ff69b4.svg)]()
## 编码/加密解密支持
- 可进行编码/加密的方式有：
    - - [x] MD5
    - - [x] SHA-1 / SHA-256
    - - [x] AES
    - - [x] DES
    - - [x] RSA
    - - [x] CUSTOM
    
- 可进行解密的方式有：
    - - [x] AES
    - - [x] DES
    - - [x] RSA
    - - [x] CUSTOM
## 引入注册
### 导入依赖
在项目的`pom.xml`中引入依赖：
```xml
<dependency>
    <groupId>cn.licoy</groupId>
    <artifactId>encrypt-body-spring-boot-starter</artifactId>
    <version>1.2.3</version>
</dependency>
```
### 启用组件
- 在工程对应的`Application`类中增加`@EnableEncryptBody`注解，如：
```java
@EnableEncryptBody
@SpringBootApplication
public class Application {
    
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
```
### 配置参数
在项目的`application.yml`或`application.properties`文件中增加参数配置，例如：
```yaml
encrypt:  
    body:
      aes-key: 12345678 #AES加密秘钥
      des-key: 12345678 #DES加密秘钥
      # more...
```
## 使用
### 对整个控制器生效
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
### 对单一请求生效
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
### 对响应的声明类生效
```java
@Data
@EncryptBody
public class User implements Serializable {

    private String name;

    private String email;

    private Integer number;

    private String numberValue;

}
```
### 对声明类单一属性生效
```java
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
```

## 使用自定义加密/解密
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

使用 myEncryptionFunction 实现 MyCryptoProvider 类，如下所示。
```java
package com.myorg.mypkg;

public class MyCryptoProvider {
    String myEncryptionFunction(String input) {
        // 用于加密输入并返回预期加密数据的代码
    }

    String myDecryptionFunction(String input) {
        // 解密输入并返回预期解密数据的代码
    }

}
```

## 注解一览表
- [编码/加密注解一览表](https://github.com/Licoy/encrypt-body-spring-boot-starter/wiki/加密注解一览表)
- [解密注解一览表](https://github.com/Licoy/encrypt-body-spring-boot-starter/wiki/解密注解一览表)
## 开源协议
[Apache 2.0](/LICENSE)
