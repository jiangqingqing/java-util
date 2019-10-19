package jqq.util;

import org.apache.commons.lang3.StringUtils;

/**
 * <br>==========================
 * <br> 公司：阿里互动娱乐
 * <br> 系统：九游游戏中心客户端后台
 * <br> 开发：qq.jiang(jqq105718@alibaba-inc.com)
 * <br> 创建时间：2019/9/30 下午3:33
 * <br>==========================
 */
public class MobileUtil {

    /**
     * 倒数第几位开始隐藏手机号
     */
    private static final int HIDE_MOBILE_CHARS_START_INDEX = 3;

    /**
     * 隐藏手机号中间多少位
     */
    private static final int HIDE_MOBILE_CHARS_NUM = 4;

    /**
     * 格式化显示带区号的手机号,规则为:显示手机号的前四位和最后三位,中间四位为密文*号显示
     *
     * @param mobile
     * @return
     */
    public static String formatToDisplayMobile(String mobile) {
        String displayMobile = "";
        try {
            if (StringUtils.isNotEmpty(mobile)) {
                int length = mobile.length();
                int endIndex = length - HIDE_MOBILE_CHARS_START_INDEX;
                endIndex = endIndex>0 ? endIndex : 0;
                int startIndex = endIndex - HIDE_MOBILE_CHARS_NUM;
                startIndex = startIndex>0 ? startIndex : 0;
                displayMobile = mobile.substring(0, startIndex) + "****" + mobile.substring(endIndex);
            }
        } catch (Exception e) {
        }

        return displayMobile;
    }

}
