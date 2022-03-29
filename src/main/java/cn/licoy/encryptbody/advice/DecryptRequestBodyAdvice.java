package cn.licoy.encryptbody.advice;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.asymmetric.RSA;
import cn.licoy.encryptbody.annotation.decrypt.AESDecryptBody;
import cn.licoy.encryptbody.annotation.decrypt.DESDecryptBody;
import cn.licoy.encryptbody.annotation.decrypt.DecryptBody;
import cn.licoy.encryptbody.annotation.decrypt.RSADecryptBody;
import cn.licoy.encryptbody.annotation.encrypt.RSAEncryptBody;
import cn.licoy.encryptbody.bean.DecryptAnnotationInfoBean;
import cn.licoy.encryptbody.bean.DecryptHttpInputMessage;
import cn.licoy.encryptbody.config.EncryptBodyConfig;
import cn.licoy.encryptbody.enums.DecryptBodyMethod;
import cn.licoy.encryptbody.exception.DecryptBodyFailException;
import cn.licoy.encryptbody.exception.DecryptMethodNotFoundException;
import cn.licoy.encryptbody.util.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdvice;

import java.lang.annotation.Annotation;
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

    public DecryptRequestBodyAdvice(EncryptBodyConfig config) {
        this.config = config;
    }

    @Override
    public boolean supports(MethodParameter methodParameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        Annotation[] annotations = methodParameter.getDeclaringClass().getAnnotations();
        if (annotations.length > 0) {
            for (Annotation annotation : annotations) {
                if (annotation instanceof DecryptBody || annotation instanceof AESDecryptBody || annotation instanceof DESDecryptBody || annotation instanceof RSADecryptBody) {
                    return true;
                }
            }
        }
        Method method = methodParameter.getMethod();
        if (method == null) {
            return false;
        }
        return method.isAnnotationPresent(DecryptBody.class) || method.isAnnotationPresent(AESDecryptBody.class) || method.isAnnotationPresent(DESDecryptBody.class) || method.isAnnotationPresent(RSADecryptBody.class);
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
        String decryptBody = null;
        DecryptAnnotationInfoBean methodAnnotation = this.getMethodAnnotation(parameter);
        if (methodAnnotation != null) {
            decryptBody = switchDecrypt(body, methodAnnotation);
        } else {
            DecryptAnnotationInfoBean classAnnotation = this.getClassAnnotation(parameter.getDeclaringClass());
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

    /**
     * 获取方法控制器上的加密注解信息
     *
     * @param methodParameter 控制器方法
     * @return 加密注解信息
     */
    private DecryptAnnotationInfoBean getMethodAnnotation(MethodParameter methodParameter) {
        Method method = methodParameter.getMethod();
        if (method == null) {
            return null;
        }
        if (method.isAnnotationPresent(DecryptBody.class)) {
            DecryptBody decryptBody = methodParameter.getMethodAnnotation(DecryptBody.class);
            if (decryptBody != null) {
                return DecryptAnnotationInfoBean.builder().decryptBodyMethod(decryptBody.value()).key(decryptBody.otherKey()).build();
            }
        }
        if (method.isAnnotationPresent(DESDecryptBody.class)) {
            DESDecryptBody decryptBody = methodParameter.getMethodAnnotation(DESDecryptBody.class);
            if (decryptBody != null) {
                return DecryptAnnotationInfoBean.builder().decryptBodyMethod(DecryptBodyMethod.DES).key(decryptBody.key()).build();
            }
        }
        if (method.isAnnotationPresent(AESDecryptBody.class)) {
            AESDecryptBody decryptBody = methodParameter.getMethodAnnotation(AESDecryptBody.class);
            if (decryptBody != null) {
                return DecryptAnnotationInfoBean.builder().decryptBodyMethod(DecryptBodyMethod.AES).key(decryptBody.key()).build();
            }
        }
        if (method.isAnnotationPresent(RSADecryptBody.class)) {
            RSADecryptBody decryptBody = methodParameter.getMethodAnnotation(RSADecryptBody.class);
            if (decryptBody != null) {
                return DecryptAnnotationInfoBean.builder().decryptBodyMethod(DecryptBodyMethod.RSA).key(decryptBody.key()).rsaKeyType(decryptBody.type()).build();
            }
        }
        return null;
    }

    /**
     * 获取类控制器上的加密注解信息
     *
     * @param clazz 控制器类
     * @return 加密注解信息
     */
    private DecryptAnnotationInfoBean getClassAnnotation(Class<?> clazz) {
        Annotation[] annotations = clazz.getDeclaredAnnotations();
        if (annotations.length > 0) {
            for (Annotation annotation : annotations) {
                if (annotation instanceof DecryptBody) {
                    DecryptBody decryptBody = (DecryptBody) annotation;
                    return DecryptAnnotationInfoBean.builder().decryptBodyMethod(decryptBody.value()).key(decryptBody.otherKey()).build();
                }
                if (annotation instanceof DESDecryptBody) {
                    return DecryptAnnotationInfoBean.builder().decryptBodyMethod(DecryptBodyMethod.DES).key(((DESDecryptBody) annotation).key()).build();
                }
                if (annotation instanceof AESDecryptBody) {
                    return DecryptAnnotationInfoBean.builder().decryptBodyMethod(DecryptBodyMethod.AES).key(((AESDecryptBody) annotation).key()).build();
                }
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
        throw new DecryptBodyFailException();
    }
}
