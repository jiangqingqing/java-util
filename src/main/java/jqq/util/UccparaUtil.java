package jqq.util;

import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import jqq.encrypt.M9Util;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;

public class UccparaUtil {

	public static Map<String, String> PARA_NAMES = new HashMap<String, String>();
	public static Map<String, String> URL_ENCODE_PARA_NAMES = new HashMap<String, String>();
	private Map<String, String> headerParas = new HashMap<String, String>(); //存放header格式化后的uccpara
	private Map<String, String> cookieParas = new HashMap<String, String>(); //存放cookie格式化后的uccpara
	private Map<String, String> urlParas = new HashMap<String, String>(); //存放url格式化后的uccpara
	private Map<String, String> _decodeUccParam = new HashMap<String, String>(); //解码后的uccpara
	private String _headerUccparaStr = "";
	private String _cookieUccparaStr = "";
	private String _urlUccparaStr = "";
	private String _headerAcceptStr = "";
	private int _initLevel = 0;//初始化级别（0:未初始化；1:初始化header；2:初始化cookie 3:初始化url；4:找不到uccpara）

	private UccparaUtil() {
		//设置url中没有使用m9加密的参数
		URL_ENCODE_PARA_NAMES.put("ni", "ni");
		URL_ENCODE_PARA_NAMES.put("ei", "ei");
		URL_ENCODE_PARA_NAMES.put("si", "si");
		URL_ENCODE_PARA_NAMES.put("li", "li");
		URL_ENCODE_PARA_NAMES.put("gi", "gi");
		URL_ENCODE_PARA_NAMES.put("wi", "wi");

		//url公共参数与header/cookie公共参数名称的对应关系
		PARA_NAMES.put("ni", "sn");
		PARA_NAMES.put("ei", "imei");
		PARA_NAMES.put("si", "imsi");
		PARA_NAMES.put("ve", "ver");
		PARA_NAMES.put("pf", "pfid");
		PARA_NAMES.put("bi", "bid");
		PARA_NAMES.put("pr", "prd");
		PARA_NAMES.put("la", "lang");
		PARA_NAMES.put("bt", "btype");
		PARA_NAMES.put("bm", "bmode");
		PARA_NAMES.put("pv", "pver");
		PARA_NAMES.put("mi", "ua");

		//旧公共参数名称，往下兼容
		PARA_NAMES.put("sn", "sn");
		PARA_NAMES.put("imei", "imei");
		PARA_NAMES.put("imsi", "imsi");
		PARA_NAMES.put("ver", "ver");
		PARA_NAMES.put("pfid", "pfid");
		PARA_NAMES.put("bid", "bid");
		PARA_NAMES.put("prd", "prd");
		PARA_NAMES.put("lang", "lang");
		PARA_NAMES.put("btype", "btype");
		PARA_NAMES.put("bmode", "bmode");
		PARA_NAMES.put("pver", "pver");
		PARA_NAMES.put("ua", "ua");

	}

	public UccparaUtil(String headerUccparaStr, String cookieUccparaStr, String urlUccparaStr, String headerAcceptStr) {
		this();

		this._headerUccparaStr = headerUccparaStr;
		this._cookieUccparaStr = cookieUccparaStr;
		this._urlUccparaStr = urlUccparaStr;
		this._headerAcceptStr = headerAcceptStr;
		this._initHeaderParas();
		this._initLevel = 1;

		if (this.headerParas.size() < 1) {
			this._initCookieParas();
			this._initLevel = 2;

			if (this.cookieParas.size() < 1) {
				this._initUrlParas();
				this._initLevel = 3;

				if (this.urlParas.size() < 1) {
					this._initLevel = 4; //找不到uccpara
				}
			}
		}

		this._initDn();
	}

	public String getParamString() {
		if (!StringUtils.isBlank(_urlUccparaStr)) {
			return _urlUccparaStr;

		} else if (!StringUtils.isBlank(_cookieUccparaStr)) {
			return _cookieUccparaStr;

		} else if (!StringUtils.isBlank(_headerUccparaStr)) {
			return _headerUccparaStr;

		} else {
			return "";
		}
	}

	/**
	 * 取uccpara某个公共参数的值
	 *
	 * @param paraKey
	 *            参数名称
	 */
	public String get(String paraKey) {
		if (this._initLevel < 1 || this._initLevel > 3) {
			return null;
		}
		if (this._initLevel == 1 || this._initLevel == 2) {
			String oldParaKey = paraKey;
			if (PARA_NAMES.containsKey(paraKey)) {
				oldParaKey = PARA_NAMES.get(paraKey);
			}

			if (this._initLevel == 1) {
				if (this.headerParas.containsKey(oldParaKey)) {
					return this.headerParas.get(oldParaKey);
				}
			}

			if (this._initLevel == 2) {
				if (this.cookieParas.containsKey(oldParaKey)) {
					return this.cookieParas.get(oldParaKey);
				}
			}

			return this._extendSearch(paraKey); //若在当前level找不到，则进行扩展查询。

		}
		//查url
		if (!URL_ENCODE_PARA_NAMES.containsKey(paraKey)) {
			return this.urlParas.get(paraKey);
		}

		if (this._decodeUccParam.containsKey(paraKey)) {
			return this._decodeUccParam.get(paraKey);
		}
		try {
			if (this.urlParas.containsKey(paraKey)) {
				String encodeValue = this.urlParas.get(paraKey);

				String decodeValue = this._deCodeUrlUccpara(encodeValue);

				this._decodeUccParam.put(paraKey, decodeValue);
				return decodeValue;
			}
		} catch (Exception e) {
			//System.out.println(e.getMessage());
		}

		return null;
	}

	private String _deCodeUrlUccpara(String value) throws Exception {
		//解码url编码
		String decodeUrl = URLDecoder.decode(value, "UTF-8");

		byte[] dcb = Base64.decodeBase64(decodeUrl);
		byte[] dcm9 = M9Util.m9_decode(dcb);

		return new String(dcm9, "UTF-8");
	}

	/**
	 * 扩展查询，根据初始化级别查询下一级别是否包含待查参数。 二级参数未实现
	 *
	 * @param paraKey
	 *            参数名称
	 */
	private String _extendSearch(String paraKey) {
		if (this._initLevel == 1) {
			//扩展到查询cookie
			this._initCookieParas();

			if (this.cookieParas.size() < 1) {
				this._extendSearchInLevel2(paraKey);
			} else {
				//若查询cookie有uccpara参数，则判断是否包含该参数，若有返回值，若无，继续查url
				String oldParamKey = paraKey;
				if (PARA_NAMES.containsKey(paraKey)) {
					oldParamKey = PARA_NAMES.get(paraKey);
				}

				if (this.cookieParas.containsKey(oldParamKey)) {
					return this.cookieParas.get(oldParamKey);
				}
				this._extendSearchInLevel2(paraKey);
			}

		}

		if (this._initLevel == 2) {
			return this._extendSearchInLevel2(paraKey);
		}

		return null;
	}

	private String _extendSearchInLevel2(String paraKey) {
		//扩展到查url
		this._initUrlParas();
		if (this.urlParas.size() < 1) {
			return null; //扩展查询找不到内容
		} else {
			//todo:解密
			return urlParas.get(paraKey);
		}
	}

	/**
	 * 初始化hearderParas集合
	 */
	private void _initHeaderParas() {
		if (!StringUtils.isBlank(_headerUccparaStr)) {
			if (this.headerParas.size() < 1) {
				this.headerParas = String2Map(this._headerUccparaStr, "`", "=");
			}
		}
	}

	/**
	 * 初始化cookieParas集合
	 */
	private void _initCookieParas() {
		if (!StringUtils.isBlank(_cookieUccparaStr)) {
			if (this.cookieParas.size() < 1) {
				this.cookieParas = String2Map(this._cookieUccparaStr, "`", "=");
			}
		}
	}

	/**
	 * 初始化urlParas集合
	 */
	private void _initUrlParas() {
		if (!StringUtils.isBlank(_urlUccparaStr)) {
			if (urlParas.size() < 1) {
				this.urlParas = String2Map(this._urlUccparaStr, "&", "=");
			}
		}
	}

	private void _initDn() {
		String headerAcceptStr = this._headerAcceptStr;
		if (StringUtils.isBlank(headerAcceptStr)) {
			return;
		}

		//根据初始化等级把dn设置到对应的集合中
		String[] acceptStrs = headerAcceptStr.split(",|;");
		for (String acceptStr : acceptStrs) {
			if (acceptStr.contains("dn")) {
				String[] dnArray = acceptStr.split("/");
				if (dnArray.length > 1) {
					String dn = dnArray[1];
					switch (_initLevel) {
					case 1:
						this.headerParas.put("dn", dn);
						break;
					case 2:
						this.cookieParas.put("dn", dn);
						break;
					default:
						break;
					}
				}

				break;
			}
		}
	}

	/**
	 * 字符串转为map
	 *
	 * @param str
	 *            待转换字符串
	 * @param separator1
	 *            组件分隔符
	 * @param separator2
	 *            键值分隔符
	 */
	public static Map<String, String> String2Map(String str, String separator1, String separator2) {

		//判断参数是否合法，若不合法抛出异常
		if (separator1 == null || separator1.equals("") || separator2 == null || separator2.equals("")) {
			throw new IllegalArgumentException("separator must not null or empty");
		}

		Map<String, String> map = new HashMap<String, String>();

		//判断待转字符串是否合法，若不合法，返回空map
		if (str == null || str.equals("")) {
			return map;
		}

		//转换数据开始，每个组键值中包含的分隔符超过一个，则不转换该组键值
		String[] strArray = str.split(separator1);//根据分隔符1切分各item
		for (String item : strArray) {
			if (item != null && !item.equals("")) {
				String[] keyValue = item.split(separator2);
				if (keyValue.length == 2) {
					map.put(keyValue[0], keyValue[1]);//插入数据到map
				}
			}
		}

		return map;
	}

	/**
	 * 获取当前请求中，对应key参数的公参值
	 * 
	 * @param request
	 * @param key
	 * @return
	 */
	public static String getUcPara(HttpServletRequest request, String key) {
		// 初始化uccpara
		String headerUccpara = "";
		String cookieUccpara = "";
		String hearderAcceptStr = "";
		Enumeration<String> headers = request.getHeaderNames();
		Cookie[] cookies = request.getCookies();
		while (headers.hasMoreElements()) {
			String name = headers.nextElement();
			if ("uccpara".equals(name)) {
				headerUccpara = request.getHeader("uccpara");
			}
			if ("accept".equals(name)) {
				hearderAcceptStr = request.getHeader("accept");
			}
		}
		for (Cookie cookie : cookies) {
			if (cookie.getName().equals("uccpara")) {
				cookieUccpara = cookie.getValue();
			}
		}



		String queryStr = request.getQueryString();
		UccparaUtil uccpara = new UccparaUtil(headerUccpara, cookieUccpara, queryStr, hearderAcceptStr);
		return uccpara.get(key);
	}

	/**
	 *
	 * 判断是否手机登陆
	 *
	 * @param request
	 * @return
	 * @author <a href="mailto:linzc3@ucweb.com">林钊川 </a> create on: 2014-9-9
	 */
	public static boolean judgeIsMoblie(HttpServletRequest request) {
		boolean isMoblie = false;
		String[] mobileAgents = { "iphone", "android", "phone", "mobile", "wap", "netfront", "java", "opera mobi", "opera mini", "ucweb", "windows ce", "symbian", "series",
				"webos", "sony", "blackberry", "dopod", "nokia", "samsung", "palmsource", "xda", "pieplus", "meizu", "midp", "cldc", "motorola", "foma", "docomo", "up.browser",
				"up.link", "blazer", "helio", "hosin", "huawei", "novarra", "coolpad", "webos", "techfaith", "palmsource", "alcatel", "amoi", "ktouch", "nexian", "ericsson",
				"philips", "sagem", "wellcom", "bunjalloo", "maui", "smartphone", "iemobile", "spice", "bird", "zte-", "longcos", "pantech", "gionee", "portalmmm", "jig browser",
				"hiptop", "benq", "haier", "^lct", "320x320", "240x320", "176x220", "w3c ", "acs-", "alav", "alca", "amoi", "audi", "avan", "benq", "bird", "blac", "blaz", "brew",
				"cell", "cldc", "cmd-", "dang", "doco", "eric", "hipt", "inno", "ipaq", "java", "jigs", "kddi", "keji", "leno", "lg-c", "lg-d", "lg-g", "lge-", "maui", "maxo",
				"midp", "mits", "mmef", "mobi", "mot-", "moto", "mwbp", "nec-", "newt", "noki", "oper", "palm", "pana", "pant", "phil", "play", "port", "prox", "qwap", "sage",
				"sams", "sany", "sch-", "sec-", "send", "seri", "sgh-", "shar", "sie-", "siem", "smal", "smar", "sony", "sph-", "symb", "t-mo", "teli", "tim-", "tosh", "tsm-",
				"upg1", "upsi", "vk-v", "voda", "wap-", "wapa", "wapi", "wapp", "wapr", "webc", "winw", "winw", "xda", "xda-", "Googlebot-Mobile" };
		Enumeration<String> headers = request.getHeaderNames();
		String ua = "";
		while (headers.hasMoreElements()) {
			String name = headers.nextElement();
			if ("user-agent".equals(name)) {
				ua = request.getHeader(name);
			}
		}
		if (StringUtils.isNotBlank(ua)) {
			for (String mobileAgent : mobileAgents) {
				if (ua.toLowerCase().indexOf(mobileAgent) >= 0) {
					isMoblie = true;
					break;
				}
			}

		}
		return isMoblie;
	}
}
