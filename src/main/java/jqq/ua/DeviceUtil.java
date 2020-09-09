package jqq.ua;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

/**
 * 获取机型与os
 *
 * @author wangsq@ucweb.com
 * @since 2.2.0
 * @createDate 2015年7月10日
 */
public class DeviceUtil {

	/**
	 * 获取机型
	 * 
	 * @param request
	 * @param up
	 * @return
	 * @author wangsq@ucweb.com
	 * @since 2.2.0
	 */
	public static String getMachine(HttpServletRequest request, String up) {

		String machine = null;
		if (StringUtils.isNotBlank(up)) {
			String[] items = up.split("\\|");
			StringBuilder stringBuilder = new StringBuilder("");
			for (String item : items) {
				String[] fields = item.split(":");
				if (fields != null && fields.length == 2) {
					if ("m".equals(fields[0]) && StringUtils.isNotBlank(fields[1])) {
						stringBuilder.append(fields[1]);
					}
				}
			}
			machine = stringBuilder.toString();
		}

		if (StringUtils.isBlank(machine)) { // 从UA获取设备信息
			machine = "Unknown";
			Enumeration<String> headers = request.getHeaderNames();
			String ua = null;
			while (headers.hasMoreElements()) {
				String name = headers.nextElement();
				if ("user-agent".equals(name)) {
					ua  = request.getHeader(name);
					break;
				}
			}
			if (ua != null) {
				machine = UserAgentAnalyzer.analyze(ua).getDevice();
			}
		}
		return machine;
	}

	/**
	 * 获取os
	 * 
	 * @param request
	 * @return
	 * @author wangsq@ucweb.com
	 * @since 2.2.0
	 */
	public static String getOs(HttpServletRequest request) {
		String result = "Unknown";
		Enumeration<String> headers = request.getHeaderNames();
		String ua = null;
		while (headers.hasMoreElements()) {
			String name = headers.nextElement();
			if ("user-agent".equals(name)) {
				ua  = request.getHeader(name);
				break;
			}
		}
		if (ua != null) {
			UserAgent.OS os = UserAgentAnalyzer.analyze(ua).getOs();
			if (os != null) {
				result = os.name();
			}
		}

		return result;
	}
}
