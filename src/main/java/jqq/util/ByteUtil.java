package jqq.util;

public class ByteUtil {
	/**
	 * 字节转十六进制
	 * 
	 * @author shuqi.wsq
	 * @since v2.6.5.1-t
	 * @createDate Nov 12, 2016
	 * @param buf
	 * @return
	 */
	public static String toHex(byte[] buf) {
		if (buf == null || buf.length == 0) {
			return "";
		}

		StringBuilder out = new StringBuilder();

		for (int i = 0; i < buf.length; i++) {
			out.append(HEX[(buf[i] >> 4) & 0x0f]).append(HEX[buf[i] & 0x0f]);
		}

		return out.toString();
	}

	/**
	 * 十六进制转字节
	 * 
	 * @author shuqi.wsq
	 * @since v2.6.5.1-t
	 * @createDate Nov 12, 2016
	 * @param str
	 * @return
	 */
	public static byte[] hexToBytes(String str) {
		if (str == null) {
			return null;
		}

		char[] hex = str.toCharArray();

		int length = hex.length / 2;
		byte[] raw = new byte[length];
		for (int i = 0; i < length; i++) {
			int high = Character.digit(hex[i * 2], 16);
			int low = Character.digit(hex[i * 2 + 1], 16);
			int value = (high << 4) | low;
			if (value > 127)
				value -= 256;
			raw[i] = (byte) value;
		}
		return raw;
	}

	private final static char[] HEX = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

}
