package cn.licoy.encryptbody.exception;

/**
 * <p>加密方式未找到或未定义异常</p>
 * @author licoy.cn
 * @version 2018/9/6
 */
public class DecryptMethodNotFoundException extends RuntimeException {

    public DecryptMethodNotFoundException() {
        super("Decryption method is not defined. (解密方式未定义)");
    }

    public DecryptMethodNotFoundException(String message) {
        super(message);
    }
}
