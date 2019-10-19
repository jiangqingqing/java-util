package jqq.util;

/**
 * @author wen
 * @version V1.0
 * @Title: appeal
 * @Package com.aligames.appeal.util
 * @Description: TODO
 * @date 2019-09-29 21:18
 */
public class StringUtil {

    /**
     * 对obj进行toString()操作,如果为null返回""
     *
     * @param obj
     * @return obj.toString()
     */
    public static String sNull(Object obj) {
        return sNull(obj, "");
    }

    /**
     * 对obj进行toString()操作,如果为null返回def中定义的值
     *
     * @param obj
     * @param def
     *            如果obj==null返回的内容
     * @return obj的toString()操作
     */
    public static String sNull(Object obj, String def) {
        return obj != null ? obj.toString() : def;
    }
}
