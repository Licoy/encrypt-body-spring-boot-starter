package cn.licoy.encryptbody.advice;

import cn.licoy.encryptbody.annotation.*;
import cn.licoy.encryptbody.enums.EncryptBodyMethod;
import cn.licoy.encryptbody.enums.SHAEncryptType;
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


/**
 * @author licoy.cn
 * @version 2018/9/4
 */
@Order(1)
@ControllerAdvice
@Slf4j
public class EncryptBodyAdvice implements ResponseBodyAdvice {

    private final ObjectMapper objectMapper;

    private final EncryptBodyConfig config;

    @Autowired
    public EncryptBodyAdvice(ObjectMapper objectMapper,EncryptBodyConfig config) {
        this.objectMapper = objectMapper;
        this.config = config;
    }


    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        return returnType.getMethod().isAnnotationPresent(EncryptBody.class) ||
            returnType.getMethod().isAnnotationPresent(AESEncryptBody.class) ||
            returnType.getMethod().isAnnotationPresent(DESEncryptBody.class) ||
            returnType.getMethod().isAnnotationPresent(RSAEncryptBody.class) ||
            returnType.getMethod().isAnnotationPresent(MD5EncryptBody.class) ||
            returnType.getMethod().isAnnotationPresent(SHAEncryptBody.class)
        ;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                  Class selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        if(body==null) return null;
        EncryptBodyMethod encryptBodyMethod = null;
        if(returnType.getMethod().isAnnotationPresent(EncryptBody.class)){
            EncryptBody encryptBody = returnType.getMethodAnnotation(EncryptBody.class);
            encryptBodyMethod = encryptBody.value();
        }
        response.getHeaders().setContentType(MediaType.TEXT_PLAIN);
        String s = null;
        try {
            s = objectMapper.writeValueAsString(body);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return switchEncrypt(s, encryptBodyMethod);
    }

    private String switchEncrypt(String formatStringBody,EncryptBodyMethod method){
        if(method == EncryptBodyMethod.MD5){
            return MD5EncryptUtil.encrypt(formatStringBody);
        }
        if(method == EncryptBodyMethod.SHA){
            return SHAEncryptUtil.encrypt(formatStringBody,SHAEncryptType.SHA256);
        }
        if(method == EncryptBodyMethod.DES){
            if(StringUtils.isNullOrEmpty(config.getDesKey())){
                log.error("未配置des-key / Des-key not configured");
                throw new RuntimeException("未配置des-key / Des-key not configured");
            }
            return DESEncryptUtil.encrypt(formatStringBody,config.getDesKey());
        }
        if(method == EncryptBodyMethod.AES){
            if(StringUtils.isNullOrEmpty(config.getAesKey())){
                log.error("未配置aes-key / AES-key not configured");
                throw new RuntimeException("未配置aes-key / AES-key not configured");
            }
            return AESEncryptUtil.encrypt(formatStringBody,config.getAesKey());
        }
        return "encrypt data fail";
    }


}
