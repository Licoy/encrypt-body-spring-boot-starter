package cn.licoy.encryptbody.advice;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.asymmetric.RSA;
import cn.licoy.encryptbody.annotation.FieldBody;
import cn.licoy.encryptbody.annotation.decrypt.AESDecryptBody;
import cn.licoy.encryptbody.annotation.decrypt.CustomDecryptBody;
import cn.licoy.encryptbody.annotation.decrypt.DESDecryptBody;
import cn.licoy.encryptbody.annotation.decrypt.DecryptBody;
import cn.licoy.encryptbody.annotation.decrypt.RSADecryptBody;
import cn.licoy.encryptbody.bean.DecryptAnnotationInfoBean;
import cn.licoy.encryptbody.bean.DecryptHttpInputMessage;
import cn.licoy.encryptbody.config.EncryptBodyConfig;
import cn.licoy.encryptbody.enums.DecryptBodyMethod;
import cn.licoy.encryptbody.exception.DecryptBodyFailException;
import cn.licoy.encryptbody.exception.DecryptMethodNotFoundException;
import cn.licoy.encryptbody.util.CommonUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdvice;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * 请求数据的加密信息解密处理<br>
 * 本类只对控制器参数中含有<strong>{@link org.springframework.web.bind.annotation.RequestBody}</strong>
 * 以及package为<strong><code>cn.licoy.encryptbody.annotation.decrypt</code></strong>下的注解有效
 *
 * @author licoy.cn
 * @version 2018/9/7
 * @see RequestBodyAdvice
 */
@Order(1)
@ControllerAdvice
@Slf4j
public class DecryptRequestBodyAdvice implements RequestBodyAdvice {

    private final EncryptBodyConfig config;

    private final ObjectMapper objectMapper;

    public DecryptRequestBodyAdvice(ObjectMapper objectMapper, EncryptBodyConfig config) {
        this.objectMapper = objectMapper;
        this.config = config;
    }

    @Override
    public boolean supports(MethodParameter methodParameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        if (this.hasDecryptAnnotation(methodParameter.getDeclaringClass())) {
            return true;
        }
        Method method = methodParameter.getMethod();
        if (method != null) {
            if (this.hasDecryptAnnotation(method)) {
                return true;
            }
            Class<?>[] parameterTypes = method.getParameterTypes();
            for (Class<?> parameterType : parameterTypes) {
                if (this.hasDecryptAnnotation(parameterType)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean hasDecryptAnnotation(AnnotatedElement annotatedElement) {
        return annotatedElement.isAnnotationPresent(DecryptBody.class) || annotatedElement.isAnnotationPresent(AESDecryptBody.class) || annotatedElement.isAnnotationPresent(DESDecryptBody.class) || annotatedElement.isAnnotationPresent(RSADecryptBody.class);
    }

    @Override
    public Object handleEmptyBody(Object body, HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        return body;
    }

    @Override
    public HttpInputMessage beforeBodyRead(HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        String body;
        try {
            body = IoUtil.read(inputMessage.getBody(), config.getEncoding());
        } catch (Exception e) {
            throw new DecryptBodyFailException("Unable to get request body data," + " please check if the sending data body or request method is in compliance with the specification." + " (无法获取请求正文数据，请检查发送数据体或请求方法是否符合规范。)");
        }
        if (body == null || StrUtil.isEmpty(body)) {
            throw new DecryptBodyFailException("The request body is NULL or an empty string, so the decryption failed." + " (请求正文为NULL或为空字符串，因此解密失败。)");
        }
        Class<?> targetTypeClass;
        try {
            targetTypeClass = Class.forName(targetType.getTypeName());
        } catch (ClassNotFoundException e) {
            throw new DecryptBodyFailException(e.getMessage());
        }
        String decryptBody = null;
        DecryptAnnotationInfoBean methodAnnotation = this.getDecryptAnnotation(parameter.getMethod());
        if (methodAnnotation != null) {
            decryptBody = switchDecrypt(body, methodAnnotation);
        } else if (this.hasDecryptAnnotation(targetTypeClass)) {
            if (targetTypeClass.isAnnotationPresent(FieldBody.class)) {
                try {
                    Object bodyInstance = objectMapper.readValue(body, targetTypeClass);
                    Object decryptBodyInstance = this.eachClassField(bodyInstance, targetTypeClass);
                    decryptBody = objectMapper.writeValueAsString(decryptBodyInstance);
                } catch (Exception e) {
                    throw new DecryptBodyFailException(e.getMessage());
                }
            } else {
                DecryptAnnotationInfoBean classAnnotation = this.getDecryptAnnotation(targetTypeClass);
                if (classAnnotation != null) {
                    decryptBody = switchDecrypt(body, classAnnotation);
                }
            }
        } else {
            DecryptAnnotationInfoBean classAnnotation = this.getDecryptAnnotation(parameter.getDeclaringClass());
            if (classAnnotation != null) {
                decryptBody = switchDecrypt(body, classAnnotation);
            }
        }
        if (decryptBody == null) {
            throw new DecryptBodyFailException("Decryption error, " + "please check if the selected source data is encrypted correctly." + " (解密错误，请检查选择的源数据的加密方式是否正确。)");
        }
        try {
            return new DecryptHttpInputMessage(IoUtil.toStream(decryptBody, config.getEncoding()), inputMessage.getHeaders());
        } catch (Exception e) {
            throw new DecryptBodyFailException("The string is converted to a stream format exception." + " Please check if the format such as encoding is correct." + " (字符串转换成流格式异常，请检查编码等格式是否正确。)");
        }
    }

    @Override
    public Object afterBodyRead(Object body, HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        return body;
    }

    private Object eachClassField(Object body, Class<?> clazz) {
        Field[] declaredFields = clazz.getDeclaredFields();
        for (Field field : declaredFields) {
            field.setAccessible(true);
            DecryptAnnotationInfoBean decryptAnnotation = this.getDecryptAnnotation(field);
            Class<?> type = field.getType();
            if (decryptAnnotation != null) {
                FieldBody fieldBody = field.getAnnotation(FieldBody.class);
                if (fieldBody != null) {
                    Field setField = ReflectUtil.getField(clazz, fieldBody.field());
                    if (setField != null && setField.getType().equals(String.class)) {
                        Object fieldValue = ReflectUtil.getFieldValue(body, setField);
                        String decryptResult = this.switchDecrypt(String.valueOf(fieldValue), decryptAnnotation);
                        ReflectUtil.setFieldValue(body, field, decryptResult);
                    }
                } else if (type.equals(String.class)) {
                    String decryptResult = this.switchDecrypt(String.valueOf(ReflectUtil.getFieldValue(body, field)), decryptAnnotation);
                    ReflectUtil.setFieldValue(body, field, decryptResult);
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
     * 获取解密注解的数据
     *
     * @param annotatedElement 注解元素
     * @return 解密注解组装数据
     */
    private DecryptAnnotationInfoBean getDecryptAnnotation(AnnotatedElement annotatedElement) {
        if (annotatedElement == null) {
            return null;
        }
        if (annotatedElement.isAnnotationPresent(DecryptBody.class)) {
            DecryptBody decryptBody = annotatedElement.getAnnotation(DecryptBody.class);
            if (decryptBody != null) {
                return DecryptAnnotationInfoBean.builder().decryptBodyMethod(decryptBody.value()).key(decryptBody.otherKey()).build();
            }
        }
        if (annotatedElement.isAnnotationPresent(DESDecryptBody.class)) {
            DESDecryptBody decryptBody = annotatedElement.getAnnotation(DESDecryptBody.class);
            if (decryptBody != null) {
                return DecryptAnnotationInfoBean.builder().decryptBodyMethod(DecryptBodyMethod.DES).key(decryptBody.key()).build();
            }
        }
        if (annotatedElement.isAnnotationPresent(AESDecryptBody.class)) {
            AESDecryptBody decryptBody = annotatedElement.getAnnotation(AESDecryptBody.class);
            if (decryptBody != null) {
                return DecryptAnnotationInfoBean.builder().decryptBodyMethod(DecryptBodyMethod.AES).key(decryptBody.key()).build();
            }
        }
        if (annotatedElement.isAnnotationPresent(RSADecryptBody.class)) {
            RSADecryptBody decryptBody = annotatedElement.getAnnotation(RSADecryptBody.class);
            if (decryptBody != null) {
                return DecryptAnnotationInfoBean.builder().decryptBodyMethod(DecryptBodyMethod.RSA).key(decryptBody.key()).rsaKeyType(decryptBody.type()).build();
            }
        }

        if (annotatedElement.isAnnotationPresent(CustomDecryptBody.class)) {
            CustomDecryptBody decryptBody = annotatedElement.getAnnotation(CustomDecryptBody.class);
            if (decryptBody != null) {
                return DecryptAnnotationInfoBean.builder().decryptBodyMethod(DecryptBodyMethod.CUSTOM).providerClassName(decryptBody.providerClassName()).decryptMethodName(decryptBody.decryptMethodName()).build();
            }
        }

        return null;
    }


    /**
     * 选择加密方式并进行解密
     *
     * @param formatStringBody 目标解密字符串
     * @param infoBean         加密信息
     * @return 解密结果
     */
    private String switchDecrypt(String formatStringBody, DecryptAnnotationInfoBean infoBean) {
        DecryptBodyMethod method = infoBean.getDecryptBodyMethod();
        if (method == null) {
            throw new DecryptMethodNotFoundException();
        }
        String key = infoBean.getKey();
        if (method == DecryptBodyMethod.DES) {
            key = CommonUtils.checkAndGetKey(config.getDesKey(), key, "DES-KEY");
            return SecureUtil.des(key.getBytes()).decryptStr(formatStringBody);
        }
        if (method == DecryptBodyMethod.AES) {
            key = CommonUtils.checkAndGetKey(config.getAesKey(), key, "AES-KEY");
            return SecureUtil.aes(key.getBytes()).decryptStr(formatStringBody);
        }
        if (method == DecryptBodyMethod.RSA) {
            RSA rsa = CommonUtils.infoBeanToRsaInstance(infoBean);
            return rsa.decryptStr(formatStringBody, infoBean.getRsaKeyType().toolType);
        }
        if (method == DecryptBodyMethod.CUSTOM) {
            try {
                Class<?> clazz = Class.forName(infoBean.getProviderClassName());
                Method m = clazz.getMethod(infoBean.getDecryptMethodName(), String.class);
                return m.invoke(null, formatStringBody).toString();
            } catch( Exception e) {
                return "failed to encrypt: " + formatStringBody;
            }
        }
        throw new DecryptBodyFailException();
    }
}
