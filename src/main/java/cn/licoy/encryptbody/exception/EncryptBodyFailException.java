package cn.licoy.encryptbody.exception;

/**
 * @author licoy.cn
 * @version 2018/9/6
 */
public class EncryptBodyFailException  extends RuntimeException {

    public EncryptBodyFailException() {
        super("encrypt body fail!");
    }

    public EncryptBodyFailException(String message) {
        super(message);
    }
}