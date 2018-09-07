## 介绍
`encrypt-body-spring-boot-starter`是对SpringBoot控制器统一的响应体加密与请求体解密的注解处理方式，支持MD5/SHA/AES/DES/RSA。
## 加密解密支持
- 可进行加密的方式有：
    - MD5
    - SHA-224 / 256 / 384 / 512
    - AES
    - DES
    - RSA (TODO)
- 可进行解密的方式有：
    - AES (TODO)
    - DES (TODO)
    - RSA (TODO)
## 使用方法
- 在`pom.xml`中引入依赖：
```xml
<dependency>
    <groupId>cn.licoy</groupId>
    <artifactId>encrypt-body-spring-boot-starter</artifactId>
    <version>0.1</version>
</dependency>
```
- 在工程对应的`Application`类中增加@EnableEncryptBody注解，例如：
```java
@EnableEncryptBody
@SpringBootApplication
public class Application {
    
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
```
- 参数配置
在项目的`application.yml`或`application.properties`文件中进行参数配置，例如：
```yaml
encrypt:  
    body:
      aes-key: 123456 #AES加密秘钥
      des-key: 123456 #DES加密秘钥
```
- 对控制器响应体进行加密
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
或者使用`@RestController`对整个控制器的方法响应体都进行加密：
```java
@RestController
@RequestMapping("/test")
public class TestController {

    @GetMapping
    @EncryptBody
    public String test(){
        return "hello world";
    }

}
```
## 开源协议
[Apache 2.0](/LICENSE)
