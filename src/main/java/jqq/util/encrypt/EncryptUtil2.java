package jqq.util.encrypt;

public class EncryptUtil2 {

	private static final int MAX_JUMP = 3;

	/**
	 * 为data加密函数，只是简单的换对data的位置进行调换 方法：<br/>
	 * 1. 先根据data的最一个字母ascii值算出jump值，为与3取模加2,即jump可取2,3,4 <br/>
	 * 2. 对data编号: 0,1,2,3,....,n, 从n开始往前遍历 <br/>
	 * 3. 如果当前编号是jump的倍数，则从目标串的前面往后面插入，否则从目标串的后面往前插入 <br/>
	 * 4. 遍历完成后，在目标串最后依次补上jump数字的ascii值 5. 完成，目标串比最初的多1位，可做校验
	 * 
	 * @param at
	 *            data
	 * @param at_len
	 *            data的长度
	 * @param sec
	 *            加密后的目标串保存在这个指针里,这个指针的空间要比access_token多1
	 */
	public static String encrypt(String data) {
		char[] at = data.toCharArray();
		int atLen = at.length;

		//获取jump值
		int jump = at[atLen - 1] % (MAX_JUMP - 1) + 2;
		//比源串多一个字符
		char[] encryptArr = new char[atLen + 1];

		int pi = 0, qi = atLen - 1;
		for (int i = atLen - 1; i >= 0; i--) {
			if (i % jump == 0) {
				encryptArr[pi++] = at[i];
			} else {
				encryptArr[qi--] = at[i];
			}
		}
		//在最后补上可显示的jump值
		encryptArr[atLen] = (char) (jump + 48);
		return new String(encryptArr);
	}

	/**
	 * 从加密串中解析出data，并校验是否合法
	 * 
	 * @param sec
	 *            已经加密的data
	 * @param at
	 *            最终求解的data会保存在这里
	 */
	public static String descrypt(String data) {
		try {
			int atLen = data.length() - 1;
			char[] encryptArr = data.toCharArray();
			int jump = encryptArr[atLen] - 48;//获取jump值

			char[] descryptResult = new char[atLen];

			//校验jump的值是否合法
			if (jump < 2 || jump > MAX_JUMP) {
				return "";
			}

			int pi = 0;
			int qi = atLen - 1;
			for (int i = atLen - 1; i >= 0; i--) {
				if (i % jump == 0) {
					descryptResult[i] = encryptArr[pi++];
				} else {
					descryptResult[i] = encryptArr[qi--];
				}
			}

			//校验jump值是否合法
			if (jump != (descryptResult[atLen - 1] % (MAX_JUMP - 1) + 2)) {
				return "";
			}
			return new String(descryptResult);
		} catch (Exception e) {
			return null;
		}
	}
}
