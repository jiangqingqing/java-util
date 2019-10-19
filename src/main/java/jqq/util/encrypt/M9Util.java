package jqq.util.encrypt;

/**
 * M9工具类
 * 
 * @author Administrator
 *
 */
public class M9Util {

	private static final String SERVER = "aa171021f9438cb2";
	private static final String JAVA = "20c60107f6363a18";
	private static final String SIS = "b59e216a8067d108";
	private static final String WM = "27c8f15b3967e95d";
	private static final String ANDROID = "e19237a3a933f7eb";
	private static final String IPHONE = "f02a170bc7fcb713";
	private static final String MTK = "93e54269a67fa3f0";
	private static final String OTHER = "3ac9717c60927455";

	private static byte[] getKey(int _platform) {
		switch (_platform) {
		case 0: // '\0'
			return "aa171021f9438cb2".getBytes();

		case 1: // '\001'
			return "20c60107f6363a18".getBytes();

		case 2: // '\002'
			return "b59e216a8067d108".getBytes();

		case 3: // '\003'
			return "27c8f15b3967e95d".getBytes();

		case 4: // '\004'
			return "e19237a3a933f7eb".getBytes();

		case 5: // '\005'
			return "f02a170bc7fcb713".getBytes();

		case 6: // '\006'
			return "93e54269a67fa3f0".getBytes();
		}
		return "3ac9717c60927455".getBytes();
	}

	private static int getRand() {
		return (int) (System.currentTimeMillis() & 0xfffffffL);
	}

	public static byte[] m8_encode(byte _src[]) {
		byte dest[] = new byte[_src.length + 2];
		byte mask[] = { -18, -71, -23, -77, -127, -114, -105, -89 };
		byte masks = 0;
		for (int i = 0; i < _src.length; i++) {
			byte a = _src[i];
			byte b = (byte) (a ^ mask[i % 8]);
			dest[i] = b;
			masks ^= a;
		}

		dest[_src.length] = (byte) (masks ^ mask[0]);
		dest[_src.length + 1] = (byte) (masks ^ mask[1]);
		return dest;
	}

	public static byte[] m8_decode(byte _src[]) throws Exception {
		if (_src.length < 2) {
			throw new Exception("invalid src");
		}
		byte dest[] = new byte[_src.length - 2];
		byte mask[] = { -18, -71, -23, -77, -127, -114, -105, -89 };
		byte maskS = 0;
		for (int i = 0; i < _src.length - 2; i++) {
			byte a = _src[i];
			byte b = (byte) (a ^ mask[i % 8]);
			dest[i] = b;
			maskS ^= b;
		}

		if (_src[_src.length - 2] != (byte) (maskS ^ mask[0]) || _src[_src.length - 1] != (byte) (maskS ^ mask[1])) {
			throw new Exception("invalid mask");
		} else {
			return dest;
		}
	}

	public static byte[] m9_decode(byte _src[]) throws Exception {
		if (_src.length < 10 || _src[0] != 109 || _src[1] != 57 || _src[2] != 48) {
			return m8_decode(_src);
		}
		byte dest[] = new byte[_src.length - 10];
		byte key[] = getKey(_src[3]);
		byte mask[] = new byte[8];
		mask[0] = key[0];
		mask[1] = key[1];
		mask[2] = key[2];
		mask[3] = key[3];
		mask[4] = key[4];
		mask[5] = key[5];
		mask[6] = key[6];
		mask[7] = key[7];
		byte inc[] = new byte[8];
		inc[0] = key[8];
		inc[1] = key[9];
		inc[2] = key[10];
		inc[3] = key[11];
		inc[4] = key[12];
		inc[5] = key[13];
		inc[6] = key[14];
		inc[7] = key[15];
		byte rnd[] = new byte[8];
		rnd[0] = _src[4];
		rnd[1] = _src[5];
		rnd[2] = _src[6];
		rnd[3] = _src[7];
		rnd[4] = (byte) ((rnd[0] + 87) % 256);
		rnd[5] = (byte) ((rnd[1] + 29) % 256);
		rnd[6] = (byte) ((rnd[2] + 171) % 256);
		rnd[7] = (byte) ((rnd[3] + 148) % 256);
		char maskS = '\0';
		for (int i = 8; i < _src.length - 2; i++) {
			if (i % 8 == 0) {
				mask[0] = (byte) ((mask[0] + inc[0] + rnd[0]) % 256);
				mask[1] = (byte) ((mask[1] + inc[1] + rnd[1]) % 256);
				mask[2] = (byte) ((mask[2] + inc[2] + rnd[2]) % 256);
				mask[3] = (byte) ((mask[3] + inc[3] + rnd[3]) % 256);
				mask[4] = (byte) ((mask[4] + inc[4] + rnd[4]) % 256);
				mask[5] = (byte) ((mask[5] + inc[5] + rnd[5]) % 256);
				mask[6] = (byte) ((mask[6] + inc[6] + rnd[6]) % 256);
				mask[7] = (byte) ((mask[7] + inc[7] + rnd[7]) % 256);
			}
			byte a = _src[i];
			byte b = (byte) (a ^ mask[i % 8]);
			dest[i - 8] = b;
			maskS ^= b;
		}

		if (_src[_src.length - 2] != (byte) (maskS ^ mask[0]) || _src[_src.length - 1] != (byte) (maskS ^ mask[1])) {
			throw new Exception("invalid mask");
		} else {
			return dest;
		}
	}

	public static byte[] m9_encode(byte _src[]) {
		byte dest[] = new byte[_src.length + 10];
		byte key[] = getKey(0);
		byte mask[] = new byte[8];
		mask[0] = key[0];
		mask[1] = key[1];
		mask[2] = key[2];
		mask[3] = key[3];
		mask[4] = key[4];
		mask[5] = key[5];
		mask[6] = key[6];
		mask[7] = key[7];
		byte inc[] = new byte[8];
		inc[0] = key[8];
		inc[1] = key[9];
		inc[2] = key[10];
		inc[3] = key[11];
		inc[4] = key[12];
		inc[5] = key[13];
		inc[6] = key[14];
		inc[7] = key[15];
		byte rnd[] = new byte[8];
		int rand = getRand();
		rnd[0] = (byte) (rand >> 24 & 0xff);
		rnd[1] = (byte) (rand >> 16 & 0xff);
		rnd[2] = (byte) (rand >> 8 & 0xff);
		rnd[3] = (byte) (rand & 0xff);
		rnd[4] = (byte) ((rnd[0] + 87) % 256);
		rnd[5] = (byte) ((rnd[1] + 29) % 256);
		rnd[6] = (byte) ((rnd[2] + 171) % 256);
		rnd[7] = (byte) ((rnd[3] + 148) % 256);
		dest[0] = 109;
		dest[1] = 57;
		dest[2] = 48;
		dest[3] = 0;
		dest[4] = rnd[0];
		dest[5] = rnd[1];
		dest[6] = rnd[2];
		dest[7] = rnd[3];
		char maskS = '\0';
		for (int i = 0; i < _src.length; i++) {
			if (i % 8 == 0) {
				mask[0] = (byte) ((mask[0] + inc[0] + rnd[0]) % 256);
				mask[1] = (byte) ((mask[1] + inc[1] + rnd[1]) % 256);
				mask[2] = (byte) ((mask[2] + inc[2] + rnd[2]) % 256);
				mask[3] = (byte) ((mask[3] + inc[3] + rnd[3]) % 256);
				mask[4] = (byte) ((mask[4] + inc[4] + rnd[4]) % 256);
				mask[5] = (byte) ((mask[5] + inc[5] + rnd[5]) % 256);
				mask[6] = (byte) ((mask[6] + inc[6] + rnd[6]) % 256);
				mask[7] = (byte) ((mask[7] + inc[7] + rnd[7]) % 256);
			}
			byte a = _src[i];
			byte b = (byte) (a ^ mask[i % 8]);
			dest[8 + i] = b;
			maskS ^= a;
		}

		dest[8 + _src.length] = (byte) (maskS ^ mask[0]);
		dest[8 + _src.length + 1] = (byte) (maskS ^ mask[1]);
		return dest;
	}
}
