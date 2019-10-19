package jqq.util;

import org.apache.commons.codec.digest.DigestUtils;

/**
 * @author <a href="mailto:wanqi.lwq@alibaba-inc.com">wanqi.lwq</a>
 * @version 1.0.0
 * @description 安全码工具类
 * @since 2019/10/10
 */
public class SecureCodeUtil {

    /**
     * 加密安全码的盐
     */
    private static final String TICKET_SALT = "fh321kjkdas232";

    /**
     * 生成申诉安全码凭证校验码
     *
     * @param ticket 安全码
     *            明文
     * @return 密文
     */
    public static String generateTicketVcode(String orderId, String ticket) {
        return DigestUtils.md5Hex(TICKET_SALT + orderId + ticket);
    }

    public static void main(String[] args) {
        System.out.println(generateTicketVcode("20191011000000000001", "1234"));
    }
}
