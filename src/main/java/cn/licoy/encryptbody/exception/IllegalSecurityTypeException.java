package cn.licoy.encryptbody.exception;

/**
 * <p>非法的安全类型</p>
 *
 * @author licoy.cn
 * @version 2022/3/29
 */
public class IllegalSecurityTypeException extends RuntimeException {

    public IllegalSecurityTypeException() {
        super("illegal security type. (非法的安全类型)");
    }

    public IllegalSecurityTypeException(String message) {
        super(message);
    }
}