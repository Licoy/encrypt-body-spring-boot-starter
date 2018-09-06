package cn.licoy.encryptbody.exception;

/**
 * @author licoy.cn
 * @version 2018/9/6
 */
public class EncryptMethodNotFoundException extends RuntimeException {

    public EncryptMethodNotFoundException() {
        super("encrypt method not found!");
    }

    public EncryptMethodNotFoundException(String message) {
        super(message);
    }
}
