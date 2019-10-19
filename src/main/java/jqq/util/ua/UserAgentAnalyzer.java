package jqq.util.ua;

import java.util.List;

import com.google.common.collect.ImmutableList;

/**
 * UA分析，获取操作系统和设备名称数据
 * 
 * @author 梁晓峰<liangxf@ucweb.com>
 * @author 冷国宁
 * @crateDate 2014年2月7日
 */
public class UserAgentAnalyzer {

	private static List<UARule> preRules = ImmutableList
			.<UARule> builder()
			//以某某前缀开头的UA
			.add(new PrefixRule(UserAgent.OS.Unknown, "SAMSUNG", 1, new int[] { 0 }, " "))
			.add(new PrefixRule(UserAgent.OS.Unknown, "Lenovo", 1, new int[] { 0 }, " "))
			.add(new PrefixRule(UserAgent.OS.Symbian, "Nokia", 1, new int[] { 0 }, " "))
			.add(new PrefixRule(UserAgent.OS.Unknown, "Haier", 1, new int[] { 0 }, " "))
			.add(new PrefixRule(UserAgent.OS.Unknown, "K-Touch", 1, new int[] { 0 }, " "))
			.add(new PrefixRule(UserAgent.OS.Unknown, "HUAWEI", 2, new int[] { 0, 1 }, " "))
			.add(new PrefixRule(UserAgent.OS.Unknown, "ZTE", 1, new int[] { 0 }, " "))
			.add(new PrefixRule(UserAgent.OS.Unknown, "Coolpad", 1, new int[] { 0 }, " "))
			.add(new PrefixRule(UserAgent.OS.Unknown, "T-smart", 1, new int[] { 0 }, " "))
			.add(new PrefixRule(UserAgent.OS.Unknown, "HS-", 1, new int[] { 0 }, " "))
			.add(new PrefixRule(UserAgent.OS.Unknown, "OBEE", 1, new int[] { 0 }, " "))
			.add(new PrefixRule(UserAgent.OS.Unknown, "POSTCOM", 1, new int[] { 0 }, " "))
			.add(new PrefixRule(UserAgent.OS.Unknown, "DESAY", 1, new int[] { 0 }, " "))
			.add(new PrefixRule(UserAgent.OS.Unknown, "Bird", 2, new int[] { 0, 1 }, " "))
			.add(new PrefixRule(UserAgent.OS.Android, "CMSurfClient", 1, new int[] { 0 }, " ")).build();

	private static List<UARule> rules = ImmutableList
			.<UARule> builder()
			// IOS
			.add(new PrefixRule(UserAgent.OS.iOS, "iPhone; U;", 3, new int[] { 2 }))
			.add(new PrefixRule(UserAgent.OS.iOS, "iPhone;", 2, new int[] { 1 }))
			.add(new PrefixRule(UserAgent.OS.iOS, "iPhone", 2, new int[] { 0 }))
			.add(new PrefixRule(UserAgent.OS.iOS, "iPad; U;", 3, new int[] { 2 }))
			.add(new PrefixRule(UserAgent.OS.iOS, "iPad;", 2, new int[] { 1 }))
			.add(new PrefixRule(UserAgent.OS.iOS, "iOS; U;", 3, new int[] { 2 }))
			.add(new PrefixRule(UserAgent.OS.iOS, "iOS", 2, new int[] { 1 }))
			.add(new PrefixRule(UserAgent.OS.iOS, "iPod; U;", 3, new int[] { 2 }))
			.add(new PrefixRule(UserAgent.OS.iOS, "iPod;", 2, new int[] { 1 }))
			.add(new PrefixRule(UserAgent.OS.iOS, "iPod", 2, new int[] { 0 }))
			.add(new LineRule(UserAgent.OS.iOS, "IUC", 2, new int[] { 1 }))
			.add(new PrefixRule(UserAgent.OS.iOS, "iOS", 2, new int[] { 1 }))
			.add(new LineRule(UserAgent.OS.iOS, "iOS", 2, new int[] { 1 }))

			// BlackBerry
			.add(new PrefixRule(UserAgent.OS.BlackBerry, "BlackBerry; U;", 5, new int[] { 1, 4 }))
			.add(new PrefixRule(UserAgent.OS.BlackBerry, "BlackBerry;", 4, new int[] { 1, 3 }))

			// Nokia and Symbian
			.add(new PrefixRule(UserAgent.OS.Symbian, "SymbianOs", 3, new int[] { 2 }))
			.add(new PrefixRule(UserAgent.OS.Symbian, "SymbianOs", 2, new int[] { 1 }))
			.add(new PrefixRule(UserAgent.OS.Symbian, "Symbian; U;", 5, new int[] { 4 }))
			.add(new PrefixRule(UserAgent.OS.Symbian, "Symbian;", 4, new int[] { 3 }))
			.add(new PrefixRule(UserAgent.OS.Symbian, "Symbian3; U;", 4, new int[] { 3 }))
			.add(new PrefixRule(UserAgent.OS.Symbian, "Symbian/3;", 2, new int[] { 1 }))
			.add(new PrefixRule(UserAgent.OS.Symbian, "S60V3; U;", 4, new int[] { 3 }))
			.add(new PrefixRule(UserAgent.OS.Symbian, "S60V5; U;", 4, new int[] { 3 }))
			.add(new PrefixRule(UserAgent.OS.Symbian, "Series40;", 2, new int[] { 1 }))
			.add(new ContainRule(UserAgent.OS.WinPhone, "Lumia", 7, new int[] { 5, 6 }))

			// Bada
			.add(new ContainRule(UserAgent.OS.Bada, "Bada", 4, new int[] { 1 }))

			// Android
			.add(new PrefixRule(UserAgent.OS.Android, "Linux; U;", 5, new int[] { 4 }))
			.add(new PrefixRule(UserAgent.OS.Android, "Linux; U;", 4, new int[] { 3 }))
			.add(new PrefixRule(UserAgent.OS.Android, "Linux; U;", 3, new int[] { 2 }))
			.add(new PrefixRule(UserAgent.OS.Android, "Linux;U;", 5, new int[] { 4 }))
			.add(new PrefixRule(UserAgent.OS.Android, "Linux ; U ;", 5, new int[] { 4 }))
			.add(new PrefixRule(UserAgent.OS.Android, "Android", 5, new int[] { 0, 1 }))
			.add(new ContainRule(UserAgent.OS.Android, "; Adr", 5, new int[] { 4 }))

			// Linux
			.add(new ContainRule(UserAgent.OS.Linux, "Linux", 2, new int[] { 1 }))
			.add(new PrefixRule(UserAgent.OS.Linux, "Linux", 1, new int[] { 0 }))

			// Windows
			.add(new ContainRule(UserAgent.OS.WinMobile, "Windows CE", 4, new int[] { 2, 3 }))
			.add(new PrefixRule(UserAgent.OS.Windows, "Windows NT", 1, new int[] { 0 }))
			.add(new ContainRule(UserAgent.OS.Windows, "Windows NT", 3, new int[] { 2 }))
			.add(new ContainRule(UserAgent.OS.WinMobile, "Windows Mobile", 2, new int[] { 0 }))
			.add(new ContainRule(UserAgent.OS.WinPhone, "Windows Phone", 7, new int[] { 5, 6 }))
			.add(new PrefixRule(UserAgent.OS.WinPhone, "Windows; U;", 6, new int[] { 4, 5 }))
			.add(new PrefixRule(UserAgent.OS.WinPhone, "Windows;", 5, new int[] { 3, 4 }))
			//如果不是window系列，又包含Nokia关键字，才为Nokia手机，OS为Unknown
			.add(new SimpleLineRule(UserAgent.OS.Unknown, "Nokia", 1, new int[] { 0 }, " "))

			// Java
			.add(new PrefixRule(UserAgent.OS.Java, "Java; U;", 5, new int[] { 4 }))
			.add(new PrefixRule(UserAgent.OS.Java, "Java;", 4, new int[] { 3 }))
			.add(new LineRule(UserAgent.OS.Java, "JUC", 5, new int[] { 4 }))

			//QQBrowser 360Browser BaiduBrowser
			.add(new LineRule(UserAgent.OS.Unknown, "MQQBrowser", 1, new int[] { 0 }, "/"))
			.add(new SimpleLineRule(UserAgent.OS.Unknown, "360browser", 1, new int[] { 0 }, " "))
			.add(new SimpleLineRule(UserAgent.OS.Unknown, "Baidu", 1, new int[] { 0 }, " "))

			//BRAND (HW Huawei ZTE KONKA SAMSUNG NEXUS)
			.add(new PrefixRule(UserAgent.OS.Unknown, "HW-", 1, new int[] { 0 }))
			.add(new ContainRule(UserAgent.OS.Android, "Nexus 7", 3, new int[] { 2 })).build();

	private static final UAProcesser UA_PROCESSER = new UAProcesser(preRules, rules);

	/**
	 * 对传入的完整UA做分析，输出
	 * 
	 * @param fullUA
	 * @return
	 */
	public static UserAgent analyze(String fullUA) {
		return UA_PROCESSER.process(fullUA);
	}

	public static String truncateLongUA(String userAgent, int maxChar) {
		if (maxChar <= 10) {
			maxChar = 10;
		}
		if (userAgent.length() <= maxChar) {
			return userAgent;
		}
		return userAgent.substring(0, maxChar - 3) + "...";
	}
}
