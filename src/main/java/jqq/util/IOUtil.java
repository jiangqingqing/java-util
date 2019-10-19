package jqq.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class IOUtil {

	public static byte[] convertTo(InputStream in) throws IOException {

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte[] data = new byte[1024];
		int len = -1;
		while ((len = in.read(data)) != -1) {
			out.write(data, 0, len);
		}
		return out.toByteArray();
	}

}
