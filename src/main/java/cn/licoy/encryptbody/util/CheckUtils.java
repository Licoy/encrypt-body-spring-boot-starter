package cn.licoy.encryptbody.util;

import cn.licoy.encryptbody.exception.KeyNotConfiguredException;

/**
 * <p>辅助检测工具类</p>
 * @author licoy.cn
 * @version 2018/9/7
 */
public class CheckUtils {

    public static String checkAndGetKey(String k1,String k2,String keyName){
        if(StringUtils.isNullOrEmpty(k1) && StringUtils.isNullOrEmpty(k2)){
            throw new KeyNotConfiguredException(String.format("%s is not configured (未配置%s)", keyName,keyName));
        }
        if(k1==null) return k2;
        return k1;
    }

}
