package cn.licoy.encryptbody.exception;


import lombok.NoArgsConstructor;

/**
 * <p>未配置KEY运行时异常</p>
 * @author licoy.cn
 * @version 2018/9/6
 */
@NoArgsConstructor
public class KeyNotConfiguredException extends RuntimeException {

    public KeyNotConfiguredException(String message) {
        super(message);
    }
}
