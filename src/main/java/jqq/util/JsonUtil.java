package jqq.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class JsonUtil {

	public static JsonObject parseString(String content) {

		try {
			return new JsonParser().parse(content).getAsJsonObject();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
