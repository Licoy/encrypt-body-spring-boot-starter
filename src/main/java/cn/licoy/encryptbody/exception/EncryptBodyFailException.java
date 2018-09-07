package cn.licoy.encryptbody.exception;

/**
 * <p>加密数据失败异常</p>
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