package jqq.util;

import java.util.UUID;

import com.google.common.base.Strings;
import org.apache.commons.lang3.StringUtils;

/**
 * uuid格式检验
 * 
 * @author wangsq@ucweb.com
 * @since 1.9.13
 * @createDate 2014年4月3日
 */
public class UuidUtil {
	public static boolean valifyUuid(String uuid) {
		if (!StringUtils.isEmpty(uuid)) {
			String regex = "[-\\w]*";
			return uuid.matches(regex);
		}
		return false;
	}
	
	/**
	 * 生成UUID
	 * @return
	 */
	public static String generateUuidStr() {
		return UUID.randomUUID().toString();
	}

	public static void main(String[] args) {
		String abc = "_--_q";
		String regex = "[-_]*";
		System.out.print(abc.matches(regex));
	}
}
