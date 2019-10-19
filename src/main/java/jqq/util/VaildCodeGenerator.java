package jqq.util;

import java.util.ArrayList;
import java.util.List;

/**
 * 验证码生成器
 *
 * @author jqq
 */
public class VaildCodeGenerator {

	private static final String SHUFFLE_STR = "0123456789012345678901234567890123456789";
	private static final int DEFAULT_SMS_CODE_LENGTH = 4;

	/**
	 * 可指定生成验证码的长度
	 * 
	 * @param o 指定长度，不传则默认为：4位
	 * @return
	 */
	public static String generateSmsCode(Object...o) {
		if (o != null && o.length == 1) { // 指定生成code的长度
			int smsCodeLength = (Integer) o[0];
			return shuffle(SHUFFLE_STR).substring(0, smsCodeLength);
		} else {
			return shuffle(SHUFFLE_STR).substring(0, DEFAULT_SMS_CODE_LENGTH);
		}
	}

	/**
	 * 生成随机短信验证码
	 * 
	 * @param input
	 * @return
	 * @author wangsq@ucweb.com
	 * @since 2.0.11
	 */
	private static String shuffle(String input) {
		List<Character> characters = new ArrayList<Character>();
		for (char c : input.toCharArray()) {
			characters.add(c);
		}
		StringBuilder output = new StringBuilder(input.length());
		while (characters.size() != 0) {
			int randPicker = (int) (Math.random() * characters.size());
			output.append(characters.remove(randPicker));
		}
		return output.toString();
	}

}
