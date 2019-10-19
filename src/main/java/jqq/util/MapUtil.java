package jqq.util;

import java.util.Map;

/**
 * 提供{@link Map}常见的工具集，基本功能与MapUtils相同.
 * <p>
 * 目的是为了解决org.apache.commons.collections.MapUtils方法中存在的一些问题：
 * <ul>
 * <li>1，获取值类型时,为满足接口协议定义不强制转换类型；
 * <li>2，解决获取Integer/Long等数值时出现超长溢出，而返回不确定的情况；
 * <li>3，解析Integer/String/Long值时对于不是该类型的值返回为null,没有转换过程。
 * <li>4，经测试JacksonUtil.decode转换为map时，不会有byte/short/float类型
 * </ul>
 * <p>
 * 内部测试方法<br>
 * <del>byte的取值范围为（-128~127）</del><br>
 * <s>short的取值范围为（-32768~32767）</s><br>
 * int的取值范围为（-2147483648~2147483647）<br>
 * long的取值范围为（-9223372036854774808~9223372036854774807）<br>
 * <s>float的取值范围为（3.402823e+38~1.401298e-45）</s><br>
 * double的取值范围为（1.797693e+308~ 4.9000000e-324）<br>
 * <p>
 * 对外提供的方法有如下:
 *
 *  <ul>
 *  <li>{@link #getObject(Map,Object)}
 *  <li>{@link #getString(Map,Object)}
 *  <li>{@link #getBoolean(Map,Object)}
 *  <li><s>{@link #getByte(Map,Object)}</s>
 *  <li><s>{@link #getShort(Map,Object)}</s>
 *  <li>{@link #getInteger(Map,Object)}
 *  <li>{@link #getLong(Map,Object)}
 *  <li><s>{@link #getFloat(Map,Object)}</s>
 *  <li>{@link #getMap(Map,Object)}
 *  </ul>
 *  
 *  @author <a href="mailto:nieyong.ny@alibaba-inc.com">聂勇</a>
 */
public class MapUtil {

    // Type safe getters
    //-------------------------------------------------------------------------
    /**
     * Gets from a Map in a null-safe manner.
     *
     * @param map  the map to use
     * @param key  the key to look up
     * @return the value in the Map, <code>null</code> if null map input
     */
    public static Object getObject(final Map<?, ?> map, final Object key) {
        if (map != null) {
            return map.get(key);
        }
        return null;
    }

    /**
     * Gets a String from a Map in a null-safe manner.
     * <p>
     * The String is obtained via <code>toString</code>.
     *
     * @param map  the map to use
     * @param key  the key to look up
     * @return the value in the Map as a String, <code>null</code> if null map input
     */
    public static String getString(final Map<?, ?> map, final Object key) {
        if (map != null) {
            Object answer = map.get(key);
            if (answer != null) {
                //强制要求字符串类型只能解析String类型
                if (answer instanceof String) {
                    return answer.toString();
                }
            }
        }
        return null;
    }

    /**
     * Gets a Boolean from a Map in a null-safe manner.
     * <p>
     * If the value is a <code>Boolean</code> it is returned directly.
     * If the value is a <code>String</code> and it equals 'true' ignoring case
     * then <code>true</code> is returned, otherwise <code>false</code>.
     * If the value is a <code>Number</code> an integer zero value returns
     * <code>false</code> and non-zero returns <code>true</code>.
     * Otherwise, <code>null</code> is returned.
     *
     * @param map  the map to use
     * @param key  the key to look up
     * @return the value in the Map as a Boolean, <code>null</code> if null map input
     */
    public static Boolean getBoolean(final Map<?, ?> map, final Object key) {
        if (map != null) {
            Object answer = map.get(key);
            if (answer != null) {
                if (answer instanceof Boolean) {
                    return (Boolean) answer;
                } else if (answer instanceof String) {
                    return new Boolean((String) answer);
                } else if (answer instanceof Number) {
                    Number n = (Number) answer;
                    return (n.intValue() != 0) ? Boolean.TRUE : Boolean.FALSE;
                }
            }
        }
        return null;
    }

    /**
     * Gets a Number from a Map in a null-safe manner.
     * <p>
     * If the value is a <code>Number</code> it is returned directly.
     * Otherwise, <code>null</code> is returned.
     *
     * @param map  the map to use
     * @param key  the key to look up
     * @return the value in the Map as a Number, <code>null</code> if null map input
     */
    public static Number getNumber(final Map<?, ?> map, final Object key) {
        if (map != null) {
            Object answer = map.get(key);
            if (answer != null) {
                //强制要求获取数值时不转换String等其它类型
                if (answer instanceof Number) {
                    return (Number) answer;
                }
            }
        }
        return null;
    }

    /**
     * Gets a Integer from a Map in a null-safe manner.
     * <p>
     * The Integer is obtained from the results of {@link #getNumber(Map,Object)}.
     *
     * @param map  the map to use
     * @param key  the key to look up
     * @return the value in the Map as a Integer, <code>null</code> if null map input
     */
    public static Integer getInteger(final Map<?, ?> map, final Object key) {
        Number answer = getNumber(map, key);
        if (answer == null) {
            return null;
        } else if (answer instanceof Byte) {
            return new Integer(answer.intValue());
        } else if (answer instanceof Short) {
            return new Integer(answer.intValue());
        } else if (answer instanceof Integer) {
            return (Integer) answer;
        }
        return null;
    }

    /**
     * Gets a Long from a Map in a null-safe manner.
     * <p>
     * The Long is obtained from the results of {@link #getNumber(Map,Object)}.
     *
     * @param map  the map to use
     * @param key  the key to look up
     * @return the value in the Map as a Long, <code>null</code> if null map input
     */
    public static Long getLong(final Map<?, ?> map, final Object key) {
        Number answer = getNumber(map, key);
        if (answer == null) {
            return null;
        } else if (answer instanceof Byte) {
            return new Long(answer.longValue());
        } else if (answer instanceof Short) {
            return new Long(answer.longValue());
        } else if (answer instanceof Integer) {
            return new Long(answer.longValue());
        } else if (answer instanceof Long) {
            return (Long) answer;
        }
        return null;
    }

    /**
     * Gets a Double from a Map in a null-safe manner.
     * <p>
     * The Double is obtained from the results of {@link #getNumber(Map,Object)}.
     *
     * @param map  the map to use
     * @param key  the key to look up
     * @return the value in the Map as a Double, <code>null</code> if null map input
     */
    public static Double getDouble(final Map<?, ?> map, final Object key) {
        Number answer = getNumber(map, key);
        if (answer == null) {
            return null;
        } else if (answer instanceof Byte) {
            return new Double(answer.doubleValue());
        } else if (answer instanceof Short) {
            return new Double(answer.doubleValue());
        } else if (answer instanceof Integer) {
            return new Double(answer.doubleValue());
        } else if (answer instanceof Long) {
            return new Double(answer.doubleValue());
        } else if (answer instanceof Float) {
            return new Double(answer.doubleValue());
        } else if (answer instanceof Double) {
            return (Double) answer;
        }
        return null;
    }

    /**
     * Gets a Map from a Map in a null-safe manner.
     * <p>
     * If the value returned from the specified map is not a Map then
     * <code>null</code> is returned.
     *
     * @param map  the map to use
     * @param key  the key to look up
     * @return the value in the Map as a Map, <code>null</code> if null map input
     */
    public static Map<?, ?> getMap(final Map<?, ?> map, final Object key) {
        if (map != null) {
            Object answer = map.get(key);
            if (answer != null && answer instanceof Map) {
                return (Map<?, ?>) answer;
            }
        }
        return null;
    }

    // Type safe getters with default values
    //-------------------------------------------------------------------------
    /**
     *  Looks up the given key in the given map, converting null into the
     *  given default value.
     *
     *  @param map  the map whose value to look up
     *  @param key  the key of the value to look up in that map
     *  @param defaultValue  what to return if the value is null
     *  @return  the value in the map, or defaultValue if the original value
     *    is null or the map is null
     */
    public static Object getObject( Map<?, ?> map, Object key, Object defaultValue ) {
        if ( map != null ) {
            Object answer = map.get( key );
            if ( answer != null ) {
                return answer;
            }
        }
        return defaultValue;
    }

    /**
     *  Looks up the given key in the given map, converting the result into
     *  a string, using the default value if the the conversion fails.
     *
     *  @param map  the map whose value to look up
     *  @param key  the key of the value to look up in that map
     *  @param defaultValue  what to return if the value is null or if the
     *     conversion fails
     *  @return  the value in the map as a string, or defaultValue if the 
     *    original value is null, the map is null or the string conversion
     *    fails
     */
    public static String getString( Map<?, ?> map, Object key, String defaultValue ) {
        String answer = getString( map, key );
        if ( answer == null ) {
            answer = defaultValue;
        }
        return answer;
    }

    /**
     *  Looks up the given key in the given map, converting the result into
     *  a boolean, using the default value if the the conversion fails.
     *
     *  @param map  the map whose value to look up
     *  @param key  the key of the value to look up in that map
     *  @param defaultValue  what to return if the value is null or if the
     *     conversion fails
     *  @return  the value in the map as a boolean, or defaultValue if the 
     *    original value is null, the map is null or the boolean conversion
     *    fails
     */
    public static Boolean getBoolean( Map<?, ?> map, Object key, Boolean defaultValue ) {
        Boolean answer = getBoolean( map, key );
        if ( answer == null ) {
            answer = defaultValue;
        }
        return answer;
    }

    /**
     *  Looks up the given key in the given map, converting the result into
     *  a number, using the default value if the the conversion fails.
     *
     *  @param map  the map whose value to look up
     *  @param key  the key of the value to look up in that map
     *  @param defaultValue  what to return if the value is null or if the
     *     conversion fails
     *  @return  the value in the map as a number, or defaultValue if the 
     *    original value is null, the map is null or the number conversion
     *    fails
     */
    public static Number getNumber( Map<?, ?> map, Object key, Number defaultValue ) {
        Number answer = getNumber( map, key );
        if ( answer == null ) {
            answer = defaultValue;
        }
        return answer;
    }


    /**
     *  Looks up the given key in the given map, converting the result into
     *  an integer, using the default value if the the conversion fails.
     *
     *  @param map  the map whose value to look up
     *  @param key  the key of the value to look up in that map
     *  @param defaultValue  what to return if the value is null or if the
     *     conversion fails
     *  @return  the value in the map as a number, or defaultValue if the 
     *    original value is null, the map is null or the number conversion
     *    fails
     */
    public static Integer getInteger( Map<?, ?> map, Object key, Integer defaultValue ) {
        Integer answer = getInteger( map, key );
        if ( answer == null ) {
            answer = defaultValue;
        }
        return answer;
    }

    /**
     *  Looks up the given key in the given map, converting the result into
     *  a long, using the default value if the the conversion fails.
     *
     *  @param map  the map whose value to look up
     *  @param key  the key of the value to look up in that map
     *  @param defaultValue  what to return if the value is null or if the
     *     conversion fails
     *  @return  the value in the map as a number, or defaultValue if the 
     *    original value is null, the map is null or the number conversion
     *    fails
     */
    public static Long getLong( Map<?, ?> map, Object key, Long defaultValue ) {
        Long answer = getLong( map, key );
        if ( answer == null ) {
            answer = defaultValue;
        }
        return answer;
    }

    /**
     *  Looks up the given key in the given map, converting the result into
     *  a double, using the default value if the the conversion fails.
     *
     *  @param map  the map whose value to look up
     *  @param key  the key of the value to look up in that map
     *  @param defaultValue  what to return if the value is null or if the
     *     conversion fails
     *  @return  the value in the map as a number, or defaultValue if the 
     *    original value is null, the map is null or the number conversion
     *    fails
     */
    public static Double getDouble( Map<?, ?> map, Object key, Double defaultValue ) {
        Double answer = getDouble( map, key );
        if ( answer == null ) {
            answer = defaultValue;
        }
        return answer;
    }

    /**
     *  Looks up the given key in the given map, converting the result into
     *  a map, using the default value if the the conversion fails.
     *
     *  @param map  the map whose value to look up
     *  @param key  the key of the value to look up in that map
     *  @param defaultValue  what to return if the value is null or if the
     *     conversion fails
     *  @return  the value in the map as a number, or defaultValue if the 
     *    original value is null, the map is null or the map conversion
     *    fails
     */
    public static Map<?, ?> getMap( Map<?, ?> map, Object key, Map<?, ?> defaultValue ) {
        Map<?, ?> answer = getMap( map, key );
        if ( answer == null ) {
            answer = defaultValue;
        }
        return answer;
    }
    

    // Type safe primitive getters
    //-------------------------------------------------------------------------
    /**
     * Gets a boolean from a Map in a null-safe manner.
     * <p>
     * If the value is a <code>Boolean</code> its value is returned.
     * If the value is a <code>String</code> and it equals 'true' ignoring case
     * then <code>true</code> is returned, otherwise <code>false</code>.
     * If the value is a <code>Number</code> an integer zero value returns
     * <code>false</code> and non-zero returns <code>true</code>.
     * Otherwise, <code>false</code> is returned.
     *
     * @param map  the map to use
     * @param key  the key to look up
     * @return the value in the Map as a Boolean, <code>false</code> if null map input
     */
    public static boolean getBooleanValue(final Map<?, ?> map, final Object key) {
        Boolean booleanObject = getBoolean(map, key);
        if (booleanObject == null) {
            return false;
        }
        return booleanObject.booleanValue();
    }

    /**
     * Gets an int from a Map in a null-safe manner.
     * <p>
     * The int is obtained from the results of {@link #getNumber(Map,Object)}.
     *
     * @param map  the map to use
     * @param key  the key to look up
     * @return the value in the Map as an int, <code>0</code> if null map input
     */
    public static int getIntValue(final Map<?, ?> map, final Object key) {
        Integer integerObject = getInteger(map, key);
        if (integerObject == null) {
            return 0;
        }
        return integerObject.intValue();
    }

    /**
     * Gets a long from a Map in a null-safe manner.
     * <p>
     * The long is obtained from the results of {@link #getNumber(Map,Object)}.
     *
     * @param map  the map to use
     * @param key  the key to look up
     * @return the value in the Map as a long, <code>0L</code> if null map input
     */
    public static long getLongValue(final Map<?, ?> map, final Object key) {
        Long longObject = getLong(map, key);
        if (longObject == null) {
            return 0L;
        }
        return longObject.longValue();
    }

    /**
     * Gets a double from a Map in a null-safe manner.
     * <p>
     * The double is obtained from the results of {@link #getNumber(Map,Object)}.
     *
     * @param map  the map to use
     * @param key  the key to look up
     * @return the value in the Map as a double, <code>0.0</code> if null map input
     */
    public static double getDoubleValue(final Map<?, ?> map, final Object key) {
        Double doubleObject = getDouble(map, key);
        if (doubleObject == null) {
            return 0d;
        }
        return doubleObject.doubleValue();
    }

    // Type safe primitive getters with default values
    //-------------------------------------------------------------------------
    /**
     * Gets a boolean from a Map in a null-safe manner,
     * using the default value if the the conversion fails.
     * <p>
     * If the value is a <code>Boolean</code> its value is returned.
     * If the value is a <code>String</code> and it equals 'true' ignoring case
     * then <code>true</code> is returned, otherwise <code>false</code>.
     * If the value is a <code>Number</code> an integer zero value returns
     * <code>false</code> and non-zero returns <code>true</code>.
     * Otherwise, <code>defaultValue</code> is returned.
     *
     * @param map  the map to use
     * @param key  the key to look up
     * @param defaultValue  return if the value is null or if the
     *     conversion fails
     * @return the value in the Map as a Boolean, <code>defaultValue</code> if null map input
     */
    public static boolean getBooleanValue(final Map<?, ?> map, final Object key, boolean defaultValue) {
        Boolean booleanObject = getBoolean(map, key);
        if (booleanObject == null) {
            return defaultValue;
        }
        return booleanObject.booleanValue();
    }

    /**
     * Gets an int from a Map in a null-safe manner,
     * using the default value if the the conversion fails.     
     * <p>
     * The int is obtained from the results of {@link #getNumber(Map,Object)}.
     *
     * @param map  the map to use
     * @param key  the key to look up
     * @param defaultValue  return if the value is null or if the
     *     conversion fails
     * @return the value in the Map as an int, <code>defaultValue</code> if null map input
     */
    public static int getIntValue(final Map<?, ?> map, final Object key, int defaultValue) {
        Integer integerObject = getInteger(map, key);
        if (integerObject == null) {
            return defaultValue;
        }
        return integerObject.intValue();
    }

    /**
     * Gets a long from a Map in a null-safe manner,
     * using the default value if the the conversion fails.     
     * <p>
     * The long is obtained from the results of {@link #getNumber(Map,Object)}.
     *
     * @param map  the map to use
     * @param key  the key to look up
     * @param defaultValue  return if the value is null or if the
     *     conversion fails
     * @return the value in the Map as a long, <code>defaultValue</code> if null map input
     */
    public static long getLongValue(final Map<?, ?> map, final Object key, long defaultValue) {
        Long longObject = getLong(map, key);
        if (longObject == null) {
            return defaultValue;
        }
        return longObject.longValue();
    }

    /**
     * Gets a double from a Map in a null-safe manner,
     * using the default value if the the conversion fails.     
     * <p>
     * The double is obtained from the results of {@link #getNumber(Map,Object)}.
     *
     * @param map  the map to use
     * @param key  the key to look up
     * @param defaultValue  return if the value is null or if the
     *     conversion fails
     * @return the value in the Map as a double, <code>defaultValue</code> if null map input
     */
    public static double getDoubleValue(final Map<?, ?> map, final Object key, double defaultValue) {
        Double doubleObject = getDouble(map, key);
        if (doubleObject == null) {
            return defaultValue;
        }
        return doubleObject.doubleValue();
    }
    
    //-----------------------------------------------------------------------
    public static boolean isEmpty(Map<?, ?> map) {
        return (map == null || map.isEmpty());
    }
    
    public static boolean isNotEmpty(Map<?, ?> map) {
        return !MapUtil.isEmpty(map);
    }
    
}
