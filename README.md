简体中文 | [English](./README_EN.md)
## 介绍
`encrypt-body-spring-boot-starter`是对SpringBoot控制器统一的响应体加密与请求体解密的注解处理方式，支持MD5/SHA/AES/DES/RSA。

 [![](https://img.shields.io/github/release/Licoy/encrypt-body-spring-boot-starter.svg)]()
 [![](https://img.shields.io/github/issues/Licoy/encrypt-body-spring-boot-starter.svg)]()
 [![](https://img.shields.io/github/issues-pr/Licoy/encrypt-body-spring-boot-starter.svg)]()
 [![](https://img.shields.io/badge/author-Licoy-ff69b4.svg)]()
## 加密解密支持
- 可进行加密的方式有：
    - - [x] MD5
    - - [x] SHA-224 / 256 / 384 / 512
    - - [x] AES
    - - [x] DES
    - - [ ] RSA
- 可进行解密的方式有：
    - - [x] AES
    - - [x] DES
    - - [ ] RSA
## 使用方法
- 在`pom.xml`中引入依赖：
```xml
<dependency>
    <groupId>cn.licoy</groupId>
    <artifactId>encrypt-body-spring-boot-starter</artifactId>
    <version>1.0.4.RELEASE</version>
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
      aes-key: 12345678 #AES加密秘钥
      des-key: 12345678 #DES加密秘钥
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
@EncryptBody
@RequestMapping("/test")
public class TestController {

    @GetMapping
    public String test(){
        return "hello world";
    }

}
```
## 注解一览表
- [加密注解一览表](https://github.com/Licoy/encrypt-body-spring-boot-starter/wiki/加密注解一览表)
- [解密注解一览表](https://github.com/Licoy/encrypt-body-spring-boot-starter/wiki/解密注解一览表)
## 讨论
- QQ群：30261540  &nbsp; [点我加入QQ群讨论](https://shang.qq.com/wpa/qunwpa?idkey=c3541f1d0dbe443456228e3aebf23f6795b614a94d5df6a32f0b2b1c759bb99b)

- 作者博客：[https://www.licoy.cn](https://www.licoy.cn)

![讨论](./dist/discuss.png)
## 开源协议
[Apache 2.0](/LICENSE)
## 推荐
[![华为云](./dist/hwy-student.jpg)](https://www.licoy.cn/go/ad/?t=huaweiyun__hongkong_and_xueshengji_190801)
