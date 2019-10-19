package jqq.util;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.InvalidParameterException;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

/**
 * 提供Uri处理相关的有用方法
 * 
 * @author liangzb@ucweb.com
 * @since 1.9.0
 * @createDate 2013-6-28
 */
public class UriUtil {

	/**
	 * 把指定参数加入指定Uri
	 *
	 * @param key
	 * @param value
	 * @return
	 * @since 1.9.0
	 */
	public static String appendParamToUri(String uri, String key, String value) {

		if (StringUtils.isEmpty(uri)) {
			return null;
		}

		String domain = uri;
		String fragment = null;
		if (uri.indexOf("#") != -1) { // 表示含有#
			String[] items = uri.split("#");
			domain = items[0];
			fragment = items[1];
		}

		StringBuilder uriWithParams = new StringBuilder(domain);
		if (uri.indexOf("?") > 0) {
			uriWithParams.append("&");
		} else {
			uriWithParams.append("?");
		}

		if (StringUtils.isEmpty(key)) {
			uriWithParams.append(value);
		} else {
			uriWithParams.append(key).append("=").append(value);
		}

		if (!StringUtils.isEmpty(fragment)) {
			uriWithParams.append("#").append(fragment);
		}

		return uriWithParams.toString();
	}

	/**
	 * 判断两个给定的Uri是否同域
	 * 
	 * @param redirectUrl
	 * @param indexUrl
	 * @return
	 * @since 1.9.0
	 */
	public static boolean isSameDomain(String redirectUrl, String indexUrl) {
		String redirectDomain = getDomainFromUrl(redirectUrl);
		String baseDomain = getDomainFromUrl(indexUrl);

		if (redirectDomain == null || baseDomain == null || !StringUtils.isNotBlank(redirectDomain) || !StringUtils.isNotBlank(baseDomain)) {
			return false;
		}

		redirectDomain = redirectDomain.toLowerCase();
		baseDomain = baseDomain.toLowerCase();
		if (redirectDomain.endsWith(baseDomain) || baseDomain.endsWith(redirectDomain)) {
			return true;
		}
		return false;
	}

	private static String getDomainFromUrl(String url) {


		if (StringUtils.isEmpty(url)) {
			return "";
		}
		String domain = "";
		try {
			if (url.contains("\\")) {
				//按浏览器进行转译变为/
				url = url.replace("\\", "/");
			}
			domain = getDomain(url);
			if (!StringUtils.isEmpty(domain)) {
				if (domain.indexOf("#") == -1) {
					domain = new URL(url).getHost();
				} else {
					domain = new URL(url).getHost();
				}
			}
		} catch (MalformedURLException e) {
		}
		return filterInvalidCharacter(domain);
	}

	public static String getDomain(String url) {

		String domain  = null;
		try {
			url = url.toLowerCase();
			String rst = url.replaceFirst("^(http://)", "").replaceFirst("^(https://)", "").replaceFirst("www.", "");
			int tag1 = rst.indexOf("?");
			int tag2 = rst.indexOf("/");

			if (tag1 > 0 && tag2 < 0) {
				domain = rst.substring(0, tag1);
			} else if (tag2 > 0 && tag1 < 0) {
				domain = rst.substring(0, tag2);
			} else if (tag1 < 0 && tag2 < 0) {
				domain = rst;
			} else if (tag1 > tag2) {
				domain = rst.substring(0, tag2);
			} else {
				domain = rst.substring(0, tag1);
			}
		} catch (Exception e1) {
			return null;
		}
		return domain;
	}
	/**
	 * 过滤url非法的字符
	 * 
	 * @param domain
	 * @return
	 * @since 1.9.5
	 */
	private static String filterInvalidCharacter(String domain) {

		if (StringUtils.isEmpty(domain)) {
			return domain;
		}
		return domain.replaceAll("javascript:", "").replaceAll("<", "").replaceAll(">", "").replaceAll("\\(", "").replaceAll("\\)", "");
	}

	/**
	 * 返回合法的redirectUrl
	 * 
	 * @param redirectUrl
	 *            请求参数中的redirect或target_redirect_url
	 * @param indexUrl
	 *            该应用的indexUrl
	 * @param backUrls
	 *            该应用配置的backUrls
	 * @return
	 * @since 1.9.0
	 */
	public static String getRedirect(String redirectUrl, String indexUrl, List<String> backUrls) {
		if (StringUtils.isEmpty(redirectUrl)) {
			return getCompleteUri(indexUrl);
		}
		if (isSameDomain(redirectUrl, indexUrl)) {
			return getCompleteUri(redirectUrl);
		}

		for (String backUrl : backUrls) {
			if (isSameDomain(redirectUrl, backUrl)) {
				return getCompleteUri(redirectUrl);
			} else {
				if (StringUtils.isNotBlank(backUrl) && StringUtils.isNotBlank(redirectUrl) && backUrl.startsWith("file://") && redirectUrl.startsWith("file://")) {
					return getCompleteUri(redirectUrl);
				}
			}
		}
		return getCompleteUri(indexUrl);
	}

	/**
	 * 获取请求的uri的queryString
	 * 
	 * @param uri
	 * @return
	 * @since 1.9.0
	 */
	public static String getQueryString(String uri) {

		try {
			URL url = new URL(uri);
			return url.getQuery();
		} catch (MalformedURLException e) {
			return null;
		}
	}

	/**
	 * 校验是不是合法的uri.
	 * 
	 * @param uri
	 * @since 1.9.0
	 */
	public static boolean validateUri(String uri) {
		try {
			new URL(uri);
			return true;
		} catch (MalformedURLException e) {
			return false;
		}
	}

	/**
	 * 拼接address和url组成完整的uri
	 * 
	 * @param address
	 * @param url
	 * @return
	 * @since 1.9.0
	 */
	public static String joinUri(String address, String url) {

		if (StringUtils.isEmpty(address)) {
			throw new InvalidParameterException("address is null");
		}
		if (StringUtils.isEmpty(url)) {
			return address;
		}
		if (address.endsWith("/") && url.startsWith("/")) {
			return address.substring(0, address.length() - 1) + url;
		} else if (!address.endsWith("/") && !url.startsWith("/")) {
			return address + "/" + url;
		} else {
			return address + url;
		}
	}

	/**
	 * 从queryString中移除指定的参数
	 * 
	 * @param queryString
	 * @param paramName
	 * @return
	 * @since 1.9.0
	 */
	public static String removeParam(String queryString, String paramName) {

		if (StringUtils.isEmpty(queryString)) {
			throw new InvalidParameterException("queryString is null");
		}

		if (queryString.contains(paramName)) {
			StringBuilder sb = new StringBuilder();
			List<String> params = Arrays.asList(queryString.split("&"));
			for (String param : params) {
				if (!StringUtils.isEmpty(param)) {
					String[] items = param.split("=");
					if (items.length == 2 && !items[1].equals(paramName)) {
						sb.append(param).append("&");
					}
				}
			}
			if (sb.toString().endsWith("&")) {
				return sb.deleteCharAt(sb.length() - 1).toString();
			}
		}

		return queryString;
	}

	public static String getHost(URL url, HttpServletRequest request) {
		String host;
		if (isHttpsRequest(request)) {
			if (url.getPort() == -1) {
				host = "https://" + url.getHost() + url.getPath();
			} else {
				host = "https://" + url.getHost() + ":" + url.getPort() + url.getPath();
			}
		} else {
			if (url.getPort() == -1) {
				host = "http://" + url.getHost() + url.getPath();
			} else {
				host = "http://" + url.getHost() + ":" + url.getPort() + url.getPath();
			}
		}
		return host;
	}

	/**
	 * 生成完整Uri 例:www.baidu.com会变成:http://www.baidu.com; ext开头的保持不变
	 * 
	 * @return
	 * @since 1.9.9
	 */
	private static String getCompleteUri(String uri) {
		if (!StringUtils.isEmpty(uri)) {
			if (!uri.startsWith("ext") && !uri.startsWith("http") && !uri.startsWith("file")) {
				uri = "http://" + uri;
			}
		}
		return uri;
	}

	/**
	 * 是不是https请求
	 *
	 * @param request
	 * @return
	 */
	public static boolean isHttpsRequest(HttpServletRequest request) {
		String header = request.getHeader("transmis-type");
		if (header != null && "https".equalsIgnoreCase(header)) {
			// https请求
			return true;
		}
		return false;
	}

}
