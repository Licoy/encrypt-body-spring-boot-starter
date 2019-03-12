package cn.licoy.encryptbody.advice;

import cn.licoy.encryptbody.annotation.encrypt.*;
import cn.licoy.encryptbody.bean.EncryptAnnotationInfoBean;
import cn.licoy.encryptbody.enums.EncryptBodyMethod;
import cn.licoy.encryptbody.enums.SHAEncryptType;
import cn.licoy.encryptbody.exception.EncryptBodyFailException;
import cn.licoy.encryptbody.exception.EncryptMethodNotFoundException;
import cn.licoy.encryptbody.util.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import cn.licoy.encryptbody.config.EncryptBodyConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.lang.annotation.Annotation;


/**
 * 响应数据的加密处理<br>
 *     本类只对控制器参数中含有<strong>{@link org.springframework.web.bind.annotation.ResponseBody}</strong>
 *     或者控制类上含有<strong>{@link org.springframework.web.bind.annotation.RestController}</strong>
 *     以及package为<strong><code>cn.licoy.encryptbody.annotation.encrypt</code></strong>下的注解有效
 * @see ResponseBodyAdvice
 * @author licoy.cn
 * @version 2018/9/4
 */
@Order(1)
@ControllerAdvice
@Slf4j
public class EncryptResponseBodyAdvice implements ResponseBodyAdvice {

    private final ObjectMapper objectMapper;

    private final EncryptBodyConfig config;

    @Autowired
    public EncryptResponseBodyAdvice(ObjectMapper objectMapper, EncryptBodyConfig config) {
        this.objectMapper = objectMapper;
        this.config = config;
    }


    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        Annotation[] annotations = returnType.getDeclaringClass().getAnnotations();
        if(annotations!=null && annotations.length>0){
            for (Annotation annotation : annotations) {
                if(annotation instanceof EncryptBody ||
                    annotation instanceof AESEncryptBody ||
                    annotation instanceof DESEncryptBody ||
                    annotation instanceof RSAEncryptBody ||
                    annotation instanceof MD5EncryptBody ||
                    annotation instanceof SHAEncryptBody){
                    return true;
                }
            }
        }
        return returnType.getMethod().isAnnotationPresent(EncryptBody.class) ||
                returnType.getMethod().isAnnotationPresent(AESEncryptBody.class) ||
                returnType.getMethod().isAnnotationPresent(DESEncryptBody.class) ||
                returnType.getMethod().isAnnotationPresent(RSAEncryptBody.class) ||
                returnType.getMethod().isAnnotationPresent(MD5EncryptBody.class) ||
                returnType.getMethod().isAnnotationPresent(SHAEncryptBody.class);
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                  Class selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        if(body==null) return null;
        response.getHeaders().setContentType(MediaType.TEXT_PLAIN);
        String str = null;
        try {
            str = objectMapper.writeValueAsString(body);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        EncryptAnnotationInfoBean classAnnotation = getClassAnnotation(returnType.getDeclaringClass());
        if(classAnnotation!=null){
            return switchEncrypt(str, classAnnotation);
        }
        EncryptAnnotationInfoBean methodAnnotation = getMethodAnnotation(returnType);
        if(methodAnnotation!=null){
            return switchEncrypt(str, methodAnnotation);
        }
        throw new EncryptBodyFailException();
    }

    /**
     * 获取方法控制器上的加密注解信息
     * @param methodParameter 控制器方法
     * @return 加密注解信息
     */
    private EncryptAnnotationInfoBean getMethodAnnotation(MethodParameter methodParameter){
        if(methodParameter.getMethod().isAnnotationPresent(EncryptBody.class)){
            EncryptBody encryptBody = methodParameter.getMethodAnnotation(EncryptBody.class);
            return EncryptAnnotationInfoBean.builder()
                    .encryptBodyMethod(encryptBody.value())
                    .key(encryptBody.otherKey())
                    .shaEncryptType(encryptBody.shaType())
                    .build();
        }
        if(methodParameter.getMethod().isAnnotationPresent(MD5EncryptBody.class)){
            return EncryptAnnotationInfoBean.builder()
                    .encryptBodyMethod(EncryptBodyMethod.MD5)
                    .build();
        }
        if(methodParameter.getMethod().isAnnotationPresent(SHAEncryptBody.class)){
            return EncryptAnnotationInfoBean.builder()
                    .encryptBodyMethod(EncryptBodyMethod.SHA)
                    .shaEncryptType(methodParameter.getMethodAnnotation(SHAEncryptBody.class).value())
                    .build();
        }
        if(methodParameter.getMethod().isAnnotationPresent(DESEncryptBody.class)){
            return EncryptAnnotationInfoBean.builder()
                    .encryptBodyMethod(EncryptBodyMethod.DES)
                    .key(methodParameter.getMethodAnnotation(DESEncryptBody.class).otherKey())
                    .build();
        }
        if(methodParameter.getMethod().isAnnotationPresent(AESEncryptBody.class)){
            return EncryptAnnotationInfoBean.builder()
                    .encryptBodyMethod(EncryptBodyMethod.AES)
                    .key(methodParameter.getMethodAnnotation(AESEncryptBody.class).otherKey())
                    .build();
        }
        return null;
    }

    /**
     * 获取类控制器上的加密注解信息
     * @param clazz 控制器类
     * @return 加密注解信息
     */
    private EncryptAnnotationInfoBean getClassAnnotation(Class clazz){
        Annotation[] annotations = clazz.getDeclaredAnnotations();
        if(annotations!=null && annotations.length>0){
            for (Annotation annotation : annotations) {
                if(annotation instanceof EncryptBody){
                    EncryptBody encryptBody = (EncryptBody) annotation;
                    return EncryptAnnotationInfoBean.builder()
                            .encryptBodyMethod(encryptBody.value())
                            .key(encryptBody.otherKey())
                            .shaEncryptType(encryptBody.shaType())
                            .build();
                }
                if(annotation instanceof MD5EncryptBody){
                    return EncryptAnnotationInfoBean.builder()
                            .encryptBodyMethod(EncryptBodyMethod.MD5)
                            .build();
                }
                if(annotation instanceof SHAEncryptBody){
                    return EncryptAnnotationInfoBean.builder()
                            .encryptBodyMethod(EncryptBodyMethod.SHA)
                            .shaEncryptType(((SHAEncryptBody) annotation).value())
                            .build();
                }
                if(annotation instanceof DESEncryptBody){
                    return EncryptAnnotationInfoBean.builder()
                            .encryptBodyMethod(EncryptBodyMethod.DES)
                            .key(((DESEncryptBody) annotation).otherKey())
                            .build();
                }
                if(annotation instanceof AESEncryptBody){
                    return EncryptAnnotationInfoBean.builder()
                            .encryptBodyMethod(EncryptBodyMethod.AES)
                            .key(((AESEncryptBody) annotation).otherKey())
                            .build();
                }
            }
        }
        return null;
    }


    /**
     * 选择加密方式并进行加密
     * @param formatStringBody 目标加密字符串
     * @param infoBean 加密信息
     * @return 加密结果
     */
    private String switchEncrypt(String formatStringBody,EncryptAnnotationInfoBean infoBean){
        EncryptBodyMethod method = infoBean.getEncryptBodyMethod();
        if(method==null){
            throw new EncryptMethodNotFoundException();
        }
        if(method == EncryptBodyMethod.MD5){
            return MD5EncryptUtil.encrypt(formatStringBody);
        }
        if(method == EncryptBodyMethod.SHA){
            SHAEncryptType shaEncryptType = infoBean.getShaEncryptType();
            if(shaEncryptType==null) shaEncryptType = SHAEncryptType.SHA256;
            return SHAEncryptUtil.encrypt(formatStringBody,shaEncryptType);
        }
        String key = infoBean.getKey();
        if(method == EncryptBodyMethod.DES){
            key = CheckUtils.checkAndGetKey(config.getDesKey(),key,"DES-KEY");
            return DESEncryptUtil.encrypt(formatStringBody,key);
        }
        if(method == EncryptBodyMethod.AES){
            key = CheckUtils.checkAndGetKey(config.getAesKey(),key,"AES-KEY");
            return AESEncryptUtil.encrypt(formatStringBody,key);
        }
        throw new EncryptBodyFailException();
    }


}
