package jqq.ua;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

/**
 * @author wen
 * @version V1.0
 * @Title: appeal
 * @Package com.aligames.appeal.util.ua
 * @Description: TODO
 * @date 2019-09-29 21:51
 */
public class UAUtil {
    public static String getUA(HttpServletRequest request) {
        Enumeration<String> headers = request.getHeaderNames();
        String ua = null;
        while (headers.hasMoreElements()) {
            String name = headers.nextElement();
            if ("user-agent".equals(name)) {
                ua  = request.getHeader(name);
                break;
            }
        }
        return ua;
    }
}
