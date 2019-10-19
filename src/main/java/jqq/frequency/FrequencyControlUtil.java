/**
 * 
 */
package jqq.frequency;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import jqq.cache.Cache;
import jqq.frequency.FrequencyControlItem.FrequencyControlType;
import jqq.frequency.FrequencyControlItem.TimeIntervalType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 
 * 频度控制类，使用方需把该类加入到sprin容器然后使用
 *
 * 
 */
public class FrequencyControlUtil {
	private Logger Logger = LoggerFactory.getLogger(FrequencyControlUtil.class);

	/*
	 * 放入缓存的map中的key
	 */
	private static final String MAX_TIMES = "mt";
	private static final String FREQUENCY_TPYE = "ft";
	private static final String START_TIME = "st";
	private static final String LAST_TIME = "lt";
	private static final String EXPIRE_TIME = "et";
	private static final String TIME_INTERVAL_TYPE = "tty";
	private static final String TIME_INTERVAL = "ti";
	private static final String CURRENT_TIMES = "ct";
	private static final String RELATE_VALUES = "rv";
	private static final String DATEFORMATE_HOUR = "yyyy-MM-dd HH";
	private static final String DATEFORMATE_DAY = "yyyy-MM-dd";
	private static final String DATEFORMATE_MONTH = "yyyy-MM";
	private static boolean debugLogEnable = true;

	@Autowired
	private Cache cache;


	/**
	 * 如果不存在或者过期,则创建该item,返回true,存在则忽略,返回false;
	 * 
	 * @param item
	 * @return
	 */
	public  boolean createIfAbsent(FrequencyControlItem item) throws FrequencyExpireException {

		Map<String, Object> map = null;
		int expiration = getExpirationTime(item.getExpireTime());
		if (expiration < 0) {
			Logger.warn("[FrequencyControlUtil]expiration is less than 0. itemid is " + item.getId() + "expireTime is " + item.getExpireTime());
			throw new FrequencyExpireException("item is expire");
		}
		try {
			map = checkExpireAndDelete(item.getId());
			boolean update = checkIntervalAndUpdate(map);
			if (update && expiration != 0) {
				cache.put(item.getId(), serialize(map), expiration);
			}
		} catch (FrequencyExpireException e) {
			//ignore the exception
			cache.put(item.getId(), serialize(item), expiration);
			if (debugLogEnable) {
				Logger.debug("[FrequencyControlUtil]:create,key is " + item.getId());
			}
			return true;

		}
		return false;
	}

	public void deleteIfExist(String id) {
		cache.delete(id);
	}

	/**
	 * 如果存在,则覆盖,返回true,不存在或者过期,无做任何操作,返回false
	 * 
	 * @param item
	 * @return
	 */
	public  boolean replace(FrequencyControlItem item) throws FrequencyExpireException {

		boolean exist = true;
		int expiration = getExpirationTime(item.getExpireTime());
		if (expiration < 0) {
			Logger.error("[FrequencyControlUtil]expiration is less than 0.id is " + item.getId() + "expireTime is " + item.getExpireTime());
			throw new FrequencyExpireException("item is expire");
		}
		try {
			checkExpireAndDelete(item.getId());
		} catch (FrequencyExpireException e) {
			//ignore
			exist = false;
		}
		//存在,则更新
		if (exist) {
			cache.put(item.getId(), serialize(item), expiration);
			if (debugLogEnable) {
				Logger.debug("[FrequencyControlUtil.replace]: replace,key is " + item.getId());
			}
		}
		return exist;
	}

	/**
	 * 如果不存在或者过期,则创建该item,否则更新规则(但保证currentTime不变) 规则相同是指:FrequencyControlItem中的 maxTimes, expireTime, type, timeInterval 其中一个参数的值不一样，则就是规则不一样
	 * 
	 * @param item
	 * @return
	 * @throws FrequencyTypeError
	 *             当存在该记录，且该记录的频度类型跟参数中的频度类型不一致时，抛出该异常
	 */
	public  void createOrChangeRegular(FrequencyControlItem item) throws FrequencyExpireException, FrequencyTypeError {

		//检查参数
		int expiration = getExpirationTime(item.getExpireTime());
		if (expiration < 0) {
			Logger.error("[FrequencyControlUtil]expiration is less than 0.id is " + item.getId() + "expireTime is " + item.getExpireTime());
			throw new FrequencyExpireException("item is expire");
		}

		if (!createIfAbsent(item)) {
			//存在该记录
			Map<String, Object> map = null;
			try {
				map = checkExpireAndDelete(item.getId());

				//判断原来的频度类型与现在的频度类型是否一样,不一样抛出异常
				if (FrequencyControlType.get(getInteger(map, FREQUENCY_TPYE)) != item.getFrequencyControlType()) {
					Logger.error("[FrequencyControlUtil]createOrChangeRegular ,frequencyTpye is cannot change ");
					throw new FrequencyTypeError("frequencyTpye is cannot change");
				}

				boolean needChangeRegular = false;

				if (getInteger(map, MAX_TIMES).intValue() != item.getMaxTimes() || getInteger(map, FREQUENCY_TPYE) != item.getFrequencyControlType().ordinal()) {
					needChangeRegular = true;
				}
				if (getInteger(map, TIME_INTERVAL_TYPE) == item.getTimeIntervalType().ordinal()) {
					//绝对时长还需要比较Interval
					if (getInteger(map, TIME_INTERVAL_TYPE) == TimeIntervalType.ABOSULTE_TIME_PERIOD.ordinal()) {
						if (getLong(map, TIME_INTERVAL).longValue() != item.getTimeInterval()) {
							needChangeRegular = true;
						}
					}
				} else {
					needChangeRegular = true;
				}
				//判断过期时间是否不一样
				boolean expireTimeChange = true;
				if (item.getExpireTime() == null && map.get(EXPIRE_TIME) == null) {
					expireTimeChange = false;
				} else if (item.getExpireTime() != null && map.get(EXPIRE_TIME) != null) {
					if (item.getExpireTime().getTime() == Long.valueOf((String) map.get(EXPIRE_TIME)).longValue()) {
						expireTimeChange = false;
					}
				}
				if (expireTimeChange) {
					needChangeRegular = true;
				}

				if (needChangeRegular) {
					map.put(MAX_TIMES, String.valueOf(item.getMaxTimes()));
					map.put(FREQUENCY_TPYE, String.valueOf(item.getFrequencyControlType().ordinal()));
					map.put(TIME_INTERVAL_TYPE, String.valueOf(item.getTimeIntervalType().ordinal()));
					map.put(TIME_INTERVAL, String.valueOf(item.getTimeInterval()));
					if (item.getExpireTime() != null) {
						map.put(EXPIRE_TIME, String.valueOf(item.getExpireTime().getTime()));
					}
					//更新周期(由于改变了规则可能引起了Inteval被改变)
					checkIntervalAndUpdate(map);
					cache.put(item.getId(), serialize(map), expiration);
				}
				if (debugLogEnable) {
					Logger.debug("[FrequencyControlUtil.createOrChangeRegular] ChangeRegular: " + needChangeRegular + "key is " + item.getId());
				}
			} catch (FrequencyExpireException e) {
				Logger.error("[FrequencyControlUtil.createOrReplaceIfConflict] cannot reach this :FrequencyExpireException");
			}
		}
	}

	/**
	 * 不存在,则创建,存在,在替换
	 * 
	 * @param item
	 * @throws FrequencyExpireException
	 */
	public  void createOrReplace(FrequencyControlItem item) throws FrequencyExpireException {
		if (!createIfAbsent(item)) {
			replace(item);
		}
	}

	/**
	 * 该方法用在累加类型的频度控制 次数自增并检查是否超过次数(超过次数,返回true,否则返回false
	 * 
	 * @param id
	 * @return
	 * @throws FrequencyExpireException
	 * @throws FrequencyTypeError
	 */
	public  boolean incrementAndCheck(String id) throws FrequencyExpireException, FrequencyTypeError {

		Map<String, Object> map = checkExpireAndDelete(id);
		if (getFrequencyType(map) != FrequencyControlType.SIMPLE_CUMULATE_FREQUENCY) {
			throw new FrequencyTypeError("frequencyControlType is wrong, the id is other type ");
		}
		int expiration = getExpirationTime(getDate(map, EXPIRE_TIME));
		boolean update = checkIntervalAndUpdate(map);
		int currentTimes = getInteger(map, CURRENT_TIMES);
		// 判断是否超过次数
		if (debugLogEnable) {
			Logger.debug("[FrequencyControlUtil.incrementAndCheck] key is " + id);
		}
		if (currentTimes + 1 <= getInteger(map, MAX_TIMES)) {
			map.put(CURRENT_TIMES, String.valueOf(currentTimes + 1));
			cache.put(id, serialize(map), expiration);
			return false;
		} else {
			if (update || expiration != 0) {
				cache.put(id, serialize(map), expiration);
			}
			return true;
		}

	}

	/**
	 * 该方法用在累减类型的频度控制 次数自减
	 * 
	 * @param id
	 * @return
	 * @throws FrequencyExpireException
	 * @throws FrequencyTypeError
	 */
	public  void subtract(String id) throws FrequencyExpireException, FrequencyTypeError {

		Map<String, Object> map = checkExpireAndDelete(id);
		if (getFrequencyType(map) != FrequencyControlType.SIMPLE_CUMULATE_FREQUENCY) {
			throw new FrequencyTypeError("frequencyControlType is wrong, the id is other type ");
		}
		int expiration = getExpirationTime(getDate(map, EXPIRE_TIME));
		boolean update = checkIntervalAndUpdate(map);
		int currentTimes = getInteger(map, CURRENT_TIMES);
		// 判断是否超过次数
		if (debugLogEnable) {
			Logger.debug("[FrequencyControlUtil.incrementAndCheck] key is " + id);
		}

		if (currentTimes > 0) {
			map.put(CURRENT_TIMES, String.valueOf(currentTimes - 1));
			cache.put(id, serialize(map), expiration);
		} else {
			if (update || expiration != 0) {
				cache.put(id, serialize(map), expiration);
			}
		}
	}

	/**
	 * 该方法用在关联排重的频度控制,超过返回true,否则返回false
	 * 
	 * @param id
	 * @param relateValue
	 * @return
	 * @throws FrequencyExpireException
	 * @throws FrequencyTypeError
	 */
	public  boolean relateCheck(String id, String relateValue) throws FrequencyExpireException, FrequencyTypeError {
		Map<String, Object> map = checkExpireAndDelete(id);
		if (map == null) {
			throw new IllegalArgumentException("itemId can not be find :" + id);
		} else {
			if (getFrequencyType(map) != FrequencyControlType.RELATE_UNIQE_FREQUENCY) {
				throw new FrequencyTypeError("frequencyControlType is wrong, the id is other type ");
			}
			int expiration = getExpirationTime(getDate(map, EXPIRE_TIME));
			boolean update = checkIntervalAndUpdate(map);
			if (debugLogEnable) {
				Logger.debug("[FrequencyControlUtil.relateCheck] need to update :" + update + ",key is :" + id);
			}
			int currentTimes = getInteger(map, CURRENT_TIMES).intValue();
			@SuppressWarnings("unchecked")
			Set<String> valueSet = (HashSet<String>) map.get(RELATE_VALUES);
			if (currentTimes != valueSet.size()) {
				Logger.error("[FrequencyControlUtil.relateCheck] currentTimes is not equal valueSet.size()");
			}
			int oldValueSize = valueSet.size();
			valueSet.add(relateValue);
			//已经是重复的,无需检查下面
			if (oldValueSize == valueSet.size()) {
				if (update || expiration != 0) {
					cache.put(id, serialize(map), expiration);
				}
				return false;
			} else {
				// 判断是否超过次数
				if ((getInteger(map, CURRENT_TIMES)) + 1 <= getInteger(map, MAX_TIMES)) {
					map.put(CURRENT_TIMES, String.valueOf(currentTimes + 1));
					cache.put(id, serialize(map), expiration);
					return false;
				} else {
					//超过次数,需要回滚后写入缓存
					if (update || expiration != 0) {
						valueSet.remove(relateValue);
						map.put(RELATE_VALUES, valueSet);
						map.put(CURRENT_TIMES, String.valueOf(currentTimes));
						cache.put(id, serialize(map), expiration);
					}
					return true;
				}
			}
		}
	}

	/**
	 * 单纯检查该id是否已经超过次数,
	 * 
	 * @param id
	 * @return
	 * @throws FrequencyExpireException
	 * @throws FrequencyTypeError
	 */
	public  boolean check(String id) {
		try{
			Map<String, Object> map = checkExpireAndDelete(id);
			int expiration = getExpirationTime(getDate(map, EXPIRE_TIME));
			boolean update = checkIntervalAndUpdate(map);
			if (debugLogEnable) {
				Logger.debug("[FrequencyControlUtil.check], id is %s  need update: %s", id, update);
			}
			if (update && expiration != 0) {
				cache.put(id, serialize(map), expiration);
			}
			int currentTimes = getInteger(map, CURRENT_TIMES);
			int maxTimes = getInteger(map, MAX_TIMES);
			if (currentTimes >= maxTimes) {
				return true;
			}
		}catch (Exception ex){
			Logger.warn("[FrequencyControlUtil.check]:",ex);
		}

		return false;
	}

	/**
	 * 取得剩余次数,如果该id不存在,则返回0
	 *
	 * @return
	 * @throws IllegalArgumentException
	 */
	public  int getRemainTimes(String id) {

		//异常或者null,都返回0;
		Map<String, Object> map;
		try {
			map = checkExpireAndDelete(id);
		} catch (FrequencyExpireException e) {
			Logger.info("[FrequencyControlUtil.getRemainTimes]: id is %s FrequencyExpireException appear return 0 times ", id);
			return 0;
		}
		//判断是否需要更新（interval）
		int expiration = getExpirationTime(getDate(map, EXPIRE_TIME));
		if (checkIntervalAndUpdate(map) && expiration != 0) {
			cache.put(id, serialize(map), expiration);
		}
		int remainTimes = getInteger(map, MAX_TIMES) - getInteger(map, CURRENT_TIMES);
		if (debugLogEnable) {
			Logger.debug("[FrequencyControlUtil.getRemainTimes]: id: %s ,remainTimes %s ", id, remainTimes);
		}
		return remainTimes;
	}

	//*******************************************辅助性方法****************************************//

	private static int getExpirationTime(Date expireTime) {
		int expiration = 0;
		if (expireTime != null) {
			expiration = (int) ((expireTime.getTime() / 1000) - (new Date().getTime() / 1000));
			return expiration;
		}
		return expiration;
	}

	private  String serialize(FrequencyControlItem item) {

		StringBuilder sb = new StringBuilder();
		String sep = "`";
		sb.append(MAX_TIMES).append("=").append(String.valueOf(item.getMaxTimes())).append(sep);
		sb.append(FREQUENCY_TPYE).append("=").append(String.valueOf(item.getFrequencyControlType().ordinal())).append(sep);
		if (item.getStartTime() != null) {
			sb.append(START_TIME).append("=").append(String.valueOf(item.getStartTime().getTime())).append(sep);
		}
		if (item.getExpireTime() != null) {
			sb.append(EXPIRE_TIME).append("=").append(String.valueOf(item.getExpireTime().getTime())).append(sep);
		}
		sb.append(LAST_TIME).append("=").append(String.valueOf(new Date().getTime())).append(sep);
		sb.append(TIME_INTERVAL_TYPE).append("=").append(String.valueOf(item.getTimeIntervalType().ordinal())).append(sep);
		sb.append(TIME_INTERVAL).append("=").append(String.valueOf(item.getTimeInterval())).append(sep);
		sb.append(CURRENT_TIMES).append("=").append("0");
		//关联排重类型
		if (item.getFrequencyControlType() == FrequencyControlType.RELATE_UNIQE_FREQUENCY) {
			sb.append(sep).append(RELATE_VALUES).append("");
		}
		if (debugLogEnable) {
			Logger.debug("[FrequencyControlUtil.inner(serialize):]" + sb.toString());
		}
		return sb.toString();
	}

	private  Map<String, Object> reSerialize(String str) {

		if (debugLogEnable) {
			Logger.debug("[FrequencyControlUtil.innner(reSerialize)],reSerialize String:" + str);
		}

		Map<String, Object> map = new HashMap<String, Object>();
		String strs[] = str.split("`");
		for (String item : strs) {
			String[] keyAndValue = item.split("=");
			//关联排重类型(urlEncode)
			if (keyAndValue[0].equals(RELATE_VALUES)) {
				Set<String> relateValueSet = new HashSet<String>();
				if (keyAndValue.length == 2) {
					String[] relateValues = keyAndValue[1].split("&");
					for (String relateValue : relateValues) {
						try {
							relateValueSet.add(URLDecoder.decode(relateValue, "UTF-8"));
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						}
					}
				}
				map.put(RELATE_VALUES, relateValueSet);
			} else if (keyAndValue.length == 2) {
				map.put(keyAndValue[0], keyAndValue[1]);
			}
		}
		if (map == null || map.size() == 0) {
			throw new RuntimeException("[FrequencyControlUtil.reSerialize]  return map is null, before reSerialize is  " + str);
		}
		return map;
	}

	private  String serialize(Map<String, Object> map) {
		Iterator<Entry<String, Object>> iterotor = map.entrySet().iterator();
		StringBuilder sb = new StringBuilder();
		String sep = "`";
		for (; iterotor.hasNext();) {
			Entry<String, Object> entry = iterotor.next();
			if (entry.getKey().equals(RELATE_VALUES)) {
				StringBuilder values = new StringBuilder("");
				@SuppressWarnings("unchecked")
				Set<String> relateValueSet = (Set<String>) entry.getValue();
				for (String value : relateValueSet) {
					try {
						values.append(URLEncoder.encode(value, "UTF-8")).append("&");
					} catch (UnsupportedEncodingException e) {
					}
				}
				sb.append(RELATE_VALUES).append("=").append(values.toString()).append(sep);
			} else {
				sb.append(entry.getKey()).append("=").append(entry.getValue()).append(sep);
			}
		}
		if (debugLogEnable) {
			Logger.debug("[FrequencyControlUtil.inner(serialize)],serialize map,String is " + sb.toString());
		}
		return sb.toString();
	}

	//如果过期或者已经不存在,则删除并抛异常,否则返回查找到的map对象
	private  Map<String, Object> checkExpireAndDelete(String id) throws FrequencyExpireException {
		String str = (String) cache.get(id);
		Map<String, Object> map = null;
		if (str == null) {
			throw new FrequencyExpireException("expired");
		} else {
			map = reSerialize(str);
			String expireTimeStr = (String) (map.get(EXPIRE_TIME));
			//判断是否过期
			if ((expireTimeStr != null) && !addAndCrossNow(Long.valueOf(expireTimeStr), 0)) {
				cache.delete(id);
				if (debugLogEnable) {
					Logger.debug("[FrequencyControlUtil.checkExpireAndDelete]:expired ,delete key is :" + id);
				}
				throw new FrequencyExpireException("expired");
			}
			if (debugLogEnable) {
				Logger.debug("[FrequencyControl.checkExpireAndDelete]:  no expired ..... key is :" + id);
			}
			return map;
		}
	}

	public  void deleteRegular(String fcId) {

		cache.delete(fcId);
	}

	//检查是否需要更新lastUpdate,与currentTime是否需要清零,更新则返回true,否则返回false
	private boolean checkIntervalAndUpdate(Map<String, Object> map) {
		String lastUpdateStr = (String) (map.get(LAST_TIME));
		Date lastUpdate = null;
		if (lastUpdateStr != null) {
			lastUpdate = getDate(Long.valueOf(lastUpdateStr));
		}
		TimeIntervalType type = TimeIntervalType.get(getInteger(map, TIME_INTERVAL_TYPE));
		boolean flag = false;
		//绝对时长的验证
		if (type == null || type == TimeIntervalType.ABOSULTE_TIME_PERIOD) {
			if (!addAndCrossNow(Long.valueOf(lastUpdateStr), getLong(map, TIME_INTERVAL))) {
				flag = true;
			}
		} else if (type == TimeIntervalType.NATRUAL_HOUR) {
			if (!isSameHour(lastUpdate, new Date())) {
				flag = true;
			}
		} else if (type == TimeIntervalType.NATRUAL_DAY) {
			if (!isSameDay(lastUpdate, new Date())) {
				flag = true;
			}
		} else if (type == TimeIntervalType.NATRUAL_MONTH) {
			if (!isSameMonth(lastUpdate, new Date())) {
				flag = true;
			}
		}
		if (flag) {
			map.put(LAST_TIME, String.valueOf(new Date().getTime()));
			map.put(CURRENT_TIMES, "0");
			//对应关联类型,还需要清空关联的value
			if (FrequencyControlType.get(getInteger(map, FREQUENCY_TPYE)) == FrequencyControlType.RELATE_UNIQE_FREQUENCY) {
				map.put(RELATE_VALUES, new HashSet<String>());
			}
		}
		if (debugLogEnable) {
			Logger.debug("[FrequencyControlUtil.checkIntervalAndUpdate]:update :" + flag);
		}
		return flag;
	}

	private static FrequencyControlType getFrequencyType(Map<String, Object> map) {
		Integer type = Integer.valueOf((String) map.get(FREQUENCY_TPYE));
		if (type != null) {
			return FrequencyControlType.get(type);
		} else {
			return null;
		}
	}

	// 获取某年某月某日,以yyyyMMdd返回
	private static Date getDate(long time) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(time);
		return cal.getTime();
		// cal.add(Calendar.DATE, -1);
	}

	private static Integer getInteger(Map<String, Object> map, String key) {
		Object obj = map.get(key);
		if (obj != null) {
			return Integer.valueOf((String) (obj));
		}
		return null;
	}

	private static Long getLong(Map<String, Object> map, String key) {
		Object obj = map.get(key);
		if (obj != null) {
			return Long.valueOf((String) (obj));
		}
		return null;
	}

	private static Date getDate(Map<String, Object> map, String key) {
		Object obj = map.get(key);
		if (obj != null) {
			return getDate(Long.valueOf((String) (obj)));
		}
		return null;
	}

	//比较两个时间是否在同一个小时（自然小时）
	private static boolean isSameHour(Date date1, Date date2) {
		String str1 = getDateStr(date1, DATEFORMATE_HOUR);
		String str2 = getDateStr(date2, DATEFORMATE_HOUR);
		return str1.equals(str2);
	}

	//比较两个时间是否在同一个小时（自然天）
	private static boolean isSameDay(Date date1, Date date2) {
		String str1 = getDateStr(date1, DATEFORMATE_DAY);
		String str2 = getDateStr(date2, DATEFORMATE_DAY);
		return str1.equals(str2);
	}

	//比较两个时间是否在同一个小时（自然月）
	private static boolean isSameMonth(Date date1, Date date2) {
		String str1 = getDateStr(date1, DATEFORMATE_MONTH);
		String str2 = getDateStr(date2, DATEFORMATE_MONTH);
		return str1.equals(str2);
	}

	// 获取某年某月某日,以yyyyMMdd返回
	private static String getDateStr(Date date, String format) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
		return simpleDateFormat.format(date);
	}

	//返回true,就是表示比现在时间大,否则就是比现在小
	private static boolean addAndCrossNow(long time, long interval) {
		if ((time + interval * 1000 - new Date().getTime()) > 0) {
			return true;
		} else {
			return false;
		}
	}
}
