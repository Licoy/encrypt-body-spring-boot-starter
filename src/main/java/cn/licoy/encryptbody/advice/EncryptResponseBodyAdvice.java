package cn.licoy.encryptbody.advice;

import cn.hutool.core.util.ReflectUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.asymmetric.RSA;
import cn.licoy.encryptbody.annotation.FieldBody;
import cn.licoy.encryptbody.annotation.encrypt.*;
import cn.licoy.encryptbody.bean.EncryptAnnotationInfoBean;
import cn.licoy.encryptbody.config.EncryptBodyConfig;
import cn.licoy.encryptbody.enums.EncryptBodyMethod;
import cn.licoy.encryptbody.enums.SHAEncryptType;
import cn.licoy.encryptbody.exception.EncryptBodyFailException;
import cn.licoy.encryptbody.exception.EncryptMethodNotFoundException;
import cn.licoy.encryptbody.util.CommonUtils;
import cn.licoy.encryptbody.util.ShaEncryptUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;



/**
 * 响应数据的加密处理<br>
 * 本类只对控制器参数中含有<strong>{@link org.springframework.web.bind.annotation.ResponseBody}</strong>
 * 或者控制类上含有<strong>{@link org.springframework.web.bind.annotation.RestController}</strong>
 * 以及package为<strong><code>cn.licoy.encryptbody.annotation.encrypt</code></strong>下的注解有效
 *
 * @author licoy.cn
 * @version 2018/9/4
 * @see ResponseBodyAdvice
 */
@Order(1)
@ControllerAdvice
@Slf4j
public class EncryptResponseBodyAdvice implements ResponseBodyAdvice<Object> {

    private final ObjectMapper objectMapper;

    private final EncryptBodyConfig config;

    @Autowired
    public EncryptResponseBodyAdvice(ObjectMapper objectMapper, EncryptBodyConfig config) {
        this.objectMapper = objectMapper;
        this.config = config;
    }


    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        Class<?> declaringClass = returnType.getDeclaringClass();
        if (this.hasEncryptAnnotation(declaringClass)) {
            return true;
        }
        Method method = returnType.getMethod();
        if (method != null) {
            Class<?> returnValueType = method.getReturnType();
            return this.hasEncryptAnnotation(method) || this.hasEncryptAnnotation(returnValueType);
        }
        return false;
    }

    private boolean hasEncryptAnnotation(AnnotatedElement annotatedElement) {
        if (annotatedElement == null) {
            return false;
        }
        return annotatedElement.isAnnotationPresent(EncryptBody.class) 
            || annotatedElement.isAnnotationPresent(AESEncryptBody.class) 
            || annotatedElement.isAnnotationPresent(DESEncryptBody.class) 
            || annotatedElement.isAnnotationPresent(RSAEncryptBody.class) 
            || annotatedElement.isAnnotationPresent(MD5EncryptBody.class) 
            || annotatedElement.isAnnotationPresent(SHAEncryptBody.class)
            || annotatedElement.isAnnotationPresent(CustomEncryptBody.class);
    }



    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        if (body == null) {
            return null;
        }
        String str = CommonUtils.convertToStringOrJson(body, objectMapper);

        Method method = returnType.getMethod();
        if (method != null) {

            //check if only some fields is response should be encrypted or the whole response should be encrypted.
            //if only field encryption is required, we should not change the content-type and keep the returned object intact.
            Class<?> methodReturnType = method.getReturnType();
            if (methodReturnType.isAnnotationPresent(FieldBody.class)) {
                return this.eachClassField(body, method.getReturnType());
            }

            EncryptAnnotationInfoBean methodAnnotation = this.getEncryptAnnotation(method);
            if (methodAnnotation != null) {
                response.getHeaders().setContentType(MediaType.TEXT_PLAIN);
                return switchEncrypt(str, methodAnnotation);
            }

            EncryptAnnotationInfoBean returnTypeClassAnnotation = this.getEncryptAnnotation(methodReturnType);
            if (returnTypeClassAnnotation != null) {
                response.getHeaders().setContentType(MediaType.TEXT_PLAIN);
                return switchEncrypt(str, returnTypeClassAnnotation);
            }
        }

        // 从声明类上
        EncryptAnnotationInfoBean classAnnotation = this.getEncryptAnnotation(returnType.getDeclaringClass());
        if (classAnnotation != null) {
            response.getHeaders().setContentType(MediaType.TEXT_PLAIN);
            return switchEncrypt(str, classAnnotation);
        }


        throw new EncryptBodyFailException();
    }

    private Object eachClassField(Object body, Class<?> returnTypeClass) {
        Field[] fields = returnTypeClass.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            Class<?> type = field.getType();
            EncryptAnnotationInfoBean encryptAnnotation = this.getEncryptAnnotation(field);
            if (encryptAnnotation != null) {
                Object fieldValue = ReflectUtil.getFieldValue(body, field);
                if (fieldValue != null) {
                    String str = CommonUtils.convertToStringOrJson(fieldValue, objectMapper);
                    String encryptResult = this.switchEncrypt(str, encryptAnnotation);
                    if (type.equals(String.class)) {
                        ReflectUtil.setFieldValue(body, field, encryptResult);
                    } else {
                        FieldBody fieldBody = field.getAnnotation(FieldBody.class);
                        if (fieldBody != null) {
                            Field setField = ReflectUtil.getField(returnTypeClass, fieldBody.field());
                            if (setField != null && setField.getType().equals(String.class)) {
                                ReflectUtil.setFieldValue(body, fieldBody.field(), encryptResult);
                                if (fieldBody.clearValue()) {
                                    ReflectUtil.setFieldValue(body, field, null);
                                }
                            }
                        }
                    }
                }
            } else if (!CommonUtils.isConvertToString(type)) {
                Object fieldValue = ReflectUtil.getFieldValue(body, field);
                if (fieldValue != null) {
                    this.eachClassField(fieldValue, type);
                }
            }
        }
        return body;
    }

    /**
     * 获取加密注解的数据
     *
     * @param annotatedElement 注解元素
     * @return 加密注解组装数据
     */
    private EncryptAnnotationInfoBean getEncryptAnnotation(AnnotatedElement annotatedElement) {
        if (annotatedElement == null) {
            return null;
        }
        if (annotatedElement.isAnnotationPresent(EncryptBody.class)) {
            EncryptBody encryptBody = annotatedElement.getAnnotation(EncryptBody.class);
            if (encryptBody != null) {
                return EncryptAnnotationInfoBean.builder().encryptBodyMethod(encryptBody.value()).key(encryptBody.otherKey()).shaEncryptType(encryptBody.shaType()).build();
            }
        }
        if (annotatedElement.isAnnotationPresent(MD5EncryptBody.class)) {
            return EncryptAnnotationInfoBean.builder().encryptBodyMethod(EncryptBodyMethod.MD5).build();
        }
        if (annotatedElement.isAnnotationPresent(SHAEncryptBody.class)) {
            SHAEncryptBody encryptBody = annotatedElement.getAnnotation(SHAEncryptBody.class);
            if (encryptBody != null) {
                return EncryptAnnotationInfoBean.builder().encryptBodyMethod(EncryptBodyMethod.SHA).shaEncryptType(encryptBody.value()).build();
            }
        }
        if (annotatedElement.isAnnotationPresent(DESEncryptBody.class)) {
            DESEncryptBody encryptBody = annotatedElement.getAnnotation(DESEncryptBody.class);
            if (encryptBody != null) {
                return EncryptAnnotationInfoBean.builder().encryptBodyMethod(EncryptBodyMethod.DES).key(encryptBody.key()).build();
            }

        }
        if (annotatedElement.isAnnotationPresent(AESEncryptBody.class)) {
            AESEncryptBody encryptBody = annotatedElement.getAnnotation(AESEncryptBody.class);
            if (encryptBody != null) {
                return EncryptAnnotationInfoBean.builder().encryptBodyMethod(EncryptBodyMethod.AES).key(encryptBody.key()).build();
            }
        }
        if (annotatedElement.isAnnotationPresent(RSAEncryptBody.class)) {
            RSAEncryptBody encryptBody = annotatedElement.getAnnotation(RSAEncryptBody.class);
            if (encryptBody != null) {
                return EncryptAnnotationInfoBean.builder().encryptBodyMethod(EncryptBodyMethod.RSA).key(encryptBody.key()).rsaKeyType(encryptBody.type()).build();
            }
        }
        if (annotatedElement.isAnnotationPresent(CustomEncryptBody.class)) {
            CustomEncryptBody encryptBody = annotatedElement.getAnnotation(CustomEncryptBody.class);
            if (encryptBody != null) {
                return EncryptAnnotationInfoBean.builder().encryptBodyMethod(EncryptBodyMethod.CUSTOM).providerClassName(encryptBody.providerClassName()).encryptMethodName(encryptBody.encryptMethodName()).build();
            }
        }
        return null;
    }


    /**
     * 选择加密方式并进行加密
     *
     * @param formatStringBody 目标加密字符串
     * @param infoBean         加密信息
     * @return 加密结果
     */
    private String switchEncrypt(String formatStringBody, EncryptAnnotationInfoBean infoBean) {
        EncryptBodyMethod method = infoBean.getEncryptBodyMethod();
        if (method == null) {
            throw new EncryptMethodNotFoundException();
        }
        if (method == EncryptBodyMethod.MD5) {
            return SecureUtil.md5().digestHex(formatStringBody);
        }
        if (method == EncryptBodyMethod.SHA) {
            SHAEncryptType shaEncryptType = infoBean.getShaEncryptType();
            if (shaEncryptType == null) {
                shaEncryptType = SHAEncryptType.SHA256;
            }
            return ShaEncryptUtil.encrypt(formatStringBody, shaEncryptType);
        }
        String key = infoBean.getKey();
        if (method == EncryptBodyMethod.DES) {
            key = CommonUtils.checkAndGetKey(config.getDesKey(), key, "DES-KEY");
            return SecureUtil.des(key.getBytes()).encryptHex(formatStringBody);
        }
        if (method == EncryptBodyMethod.AES) {
            key = CommonUtils.checkAndGetKey(config.getAesKey(), key, "AES-KEY");
            return SecureUtil.aes(key.getBytes()).encryptHex(formatStringBody);
        }
        if (method == EncryptBodyMethod.RSA) {
            RSA rsa = CommonUtils.infoBeanToRsaInstance(infoBean);
            return rsa.encryptHex(formatStringBody, infoBean.getRsaKeyType().toolType);
        }
        if (method == EncryptBodyMethod.CUSTOM) {
            try {
                Class<?> clazz = Class.forName(infoBean.getProviderClassName());
                Method m = clazz.getMethod(infoBean.getEncryptMethodName(), String.class);
                return m.invoke(null, formatStringBody).toString();
            } catch(Exception e) {
                return "failed to encrypt: " + formatStringBody;
            }
            
        }
        throw new EncryptBodyFailException();
    }

}
