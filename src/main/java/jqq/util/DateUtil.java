package jqq.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {

	/**
	 * 默认的日期时间格式字符串 yyyy-MM-dd HH:mm:ss
	 */
	public final static String defaultDateTimePatternStr = "yyyy-MM-dd HH:mm:ss";

	/**
	 * 几个月前的时间
	 * 
	 * @author shuqi.wsq
	 * @since v2.8.0.7-p
	 * @createDate Nov 25, 2016
	 * @param month
	 * @return
	 */
	public static Date beforeMonth(int month) {

		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MONTH, month);
		return calendar.getTime();
	}

	/**
	 * 几天前的时间
	 * 
	 * @author shuqi.wsq
	 * @since v2.8.0.7-p
	 * @createDate Nov 25, 2016
	 * @param day
	 * @return
	 */
	public static Date beforeDay(int day) {

		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, day);
		return calendar.getTime();
	}

	/**
	 * 把字符串格式化成日期类型
	 * 
	 * @param dateStr
	 * @param pattern
	 *            日期格式，为空时默认为"yyyy-MM-dd HH:mm:ss"格式
	 * @return
	 * @throws ParseException
	 */
	public static Date parse(String dateStr, String pattern) throws ParseException {
		if (pattern == null)
			pattern = defaultDateTimePatternStr;
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		Date date = simpleDateFormat.parse(dateStr);
		return date;
	}

	public static String format(Date srcDate, String pattern) throws ParseException {
		if (pattern == null)
			pattern = defaultDateTimePatternStr;
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		String date = simpleDateFormat.format(srcDate);
		return date;
	}
}
