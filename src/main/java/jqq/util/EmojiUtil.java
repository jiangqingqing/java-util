package jqq.util;

import org.apache.commons.lang3.StringUtils;

/**
 * 一个检测和过滤4字节UTF8表情字符(e.g 😄)的工具类
 *
 * @author liangxf@ucweb.com
 * @since 1.9.15
 * @createDate 2014年4月11日
 */
public class EmojiUtil {

    /**
     * 检测是否有emoji字符
     * @param source
     * @return 存在Emoji字符则返回true
     */
    public static boolean containsEmoji(String source) {
        if (StringUtils.isEmpty(source)) {
            return false;
        }

        int len = source.length();
        for (int i = 0; i < len; i++) {
            char codePoint = source.charAt(i);
            if (!isNotEmojiCharacter(codePoint)) {
                //do nothing，判断到了这里表明，确认有表情字符
                return true;
            }
        }

        return false;
    }

    private static boolean isNotEmojiCharacter(char codePoint) {
        return (codePoint == 0x0) ||
                (codePoint == 0x9) ||
                (codePoint == 0xA) ||
                (codePoint == 0xD) ||
                ((codePoint >= 0x20) && (codePoint <= 0xD7FF)) ||
                ((codePoint >= 0xE000) && (codePoint <= 0xFFFD)) ||
                ((codePoint >= 0x10000) && (codePoint <= 0x10FFFF));
    }

    /**
     * 过滤emoji 或者 其他非文字类型的字符
     * @param source
     * @return
     */
    public static String filterEmoji(String source) {
    	if (source == null) {
    		return null;
    	}
        StringBuilder buf = new StringBuilder(source.length());
        int len = source.length();
        for (int i = 0; i < len; i++) {
            char codePoint = source.charAt(i);
            if (isNotEmojiCharacter(codePoint)) {
            	buf.append(codePoint);
            }
        }
        if (buf.length() == len) {//这里的意义在于尽可能少的toString，因为会重新生成字符串
            return source;
        } else {
            return buf.toString();
        }
    }
    
	public static void main(String[] args) {
		String nick = "Let's Go，UC™是我们の🆓🏧😂";
		System.out.println("Nickname:" + nick + (containsEmoji(nick) ? " has " : " hasn't ") + "emoji");
		System.out.println("Nickname filtered:"+filterEmoji(nick));
	}
}
