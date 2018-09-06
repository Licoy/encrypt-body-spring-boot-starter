## 介绍
`encrypt-body-spring-boot-starter`是可以对SpringBoot的ResponseBody响应体进行加密的一款工具包，支持暂支持MD5/SHA/AES/DES
## 加密支持
可进行加密方式有：
    
- MD5 
- SHA-224 / 256 / 384 / 512
- AES
- DES
- RSA(暂不支持)
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
    @EncryptBody(value = EncryptBodyMethod.AES)
    public String test(){
        return "hello world";
    }

}
```
## 开源协议
[Apache 2.0](/LICENSE)
