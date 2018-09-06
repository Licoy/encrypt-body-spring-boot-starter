package cn.licoy.encryptbody.exception;


/**
 * @author licoy.cn
 * @version 2018/9/6
 * 未配置KEY运行时异常
 */
public class KeyNotConfiguredException extends RuntimeException {

    public KeyNotConfiguredException() {
        super();
    }

    public KeyNotConfiguredException(String message) {
        super(message);
    }
}
