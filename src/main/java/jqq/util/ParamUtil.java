package jqq.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 参数常用方法类。包括：拼接参数字符串；生成参数签名。 <br/>
 * 
 * @author <a href="mailto:nieyong.ny@alibaba-inc.com">聂勇</a>
 */
public class ParamUtil {
	private static Logger logger = LoggerFactory.getLogger(ParamUtil.class);

    /**
     * 对参数及其值进行拼接。
     * 
     * @param params 需进行拼接的参数集合
     * @param separator 参数拼接时参数之间的分隔符。如果传入null，将转换成空字符串("")
     * @return 对参数及其值进行拼接后的字符串
     * 
     * @see #assembleSignData(Map, String[])
     */
    public static String assembleSignData(Map<String, Object> params, String separator) {
        return assembleSignData(params, null, separator);
    }
    
    /**
     * 对参数及其值进行拼接。
     * 
     * @param params 需进行拼接的参数集合(只有基础数据类型才加入签名，而对象则不加入签名)
     * @param separator 参数拼接时参数之间的分隔符。如果传入null，将转换成空字符串("")
     * @return 对参数及其值进行拼接后的字符串
     * 
     * @see #assembleSignData(Map, String[])
     */
    public static String assembleSignDataWithoutObjectParam(Map<String, Object> params, String separator) {
        List<String> notIn = new ArrayList<String>();
        if(params != null){
            for(String key : params.keySet()){
            	if(params.get(key) != null && 
            			!(params.get(key) instanceof String 
            					|| params.get(key) instanceof Number
			            		|| params.get(key) instanceof Boolean
		            			|| params.get(key) instanceof Character)){
            		notIn.add(key);
            	}
            }
        }
    	return assembleSignData(params, notIn, separator);
    }

    /**
     * 对参数及其值进行拼接。拼接时先按参数名进行升序排序，然后根据排序后的结果逐个拼接参数，
     * 参数与参数值之间用英文等于号(=)隔开。如果参数值为null，用空字符串参与拼接。拼接表达式如下： <br/>
     * <pre>
     * key1 + "=" + value1 + separator + key2 + "=" + value2 + separator + ......keyn + "=" + valuen
     * 
     * 例：
     * 原始参数集合为：[{"dkey", "cc"}, {"akey", 90}, {"bkey", null}]
     * 分隔符为空字符串("")，所有参数参与拼接后的字符串为：akey=90bkey=dkey=cc
     * </pre>
     * 
     * @param params 需进行拼接的参数集合
     * @param notIn 不参与签名的参数名称数组。如果为null或空数组表示所有参数都参与签名
     * @param separator 参数拼接时参数之间的分隔符。如果传入null，将转换成空字符串("")
     * @return 对参数及其值进行拼接后的字符串。如果传入的参数集合为null或空集合，返回空字符串("")
     */
    public static String assembleSignData(Map<String, Object> params, List<String> notIn, String separator) {
        if (null == params || params.isEmpty()) {
            return "";
        }
        
        if (null == separator) {
            separator = "";
        }

        // 按key升序排序
        List<String> keys = new ArrayList<String>(params.keySet());
        Collections.sort(keys);

        // 拼接参数
        StringBuilder content = new StringBuilder(128);
        int size = keys.size();
        for (int i = 0; i < size; i++) {
            String key = keys.get(i);
            
            // 排除不参与拼接的参数
            if (notIn != null && notIn.contains(key)) {
                continue;
            } 
            
            Object srcValue = params.get(key);
            String value = srcValue==null ? "" : srcValue.toString();
            content.append(i > 0 ? separator : "")
                .append(key)
                .append("=")
                .append(value);
        }
        
        return content.toString();
    }

    /**
     * 使用md5哈希对参数进行签名，md5使用RFC 1321标准，编码后需要转换回全小写。算法如下（表达式中的“+”号表示字符串接，并不存在）：
     * <pre>
     * md5(prefix + sorted_compact_data + secretKey)
     * sorted_compact_data=join("", sorted_data_items)
     * sorted_data_item=key+"="+value
     * </pre>
     * 更多细节请查看wiki：<a href="http://wiki.dev1.g.uc.cn/index.php?title=%E5%85%AC%E5%85%B1%E6%8E%A5%E5%8F%A3%E5%8D%8F%E8%AE%AE%E8%A7%84%E8%8C%83">公共接口规范</a>
     * 
     * @param params 需进行拼接的参数集合
     * @param separator 参数拼接时参数之间的分隔符。如果传入null，将转换成空字符串("")
     * @param prefix 前缀
     * @param secretKey 签名密钥
     * @return 参数拼接后md5签名生成的字符串
     * 
     * @see #assembleSignData(Map, String)
     * @see #assembleSignData(Map, List, String)
     */
    public static String createSign(Map<String, Object> params, String separator, 
            String prefix, String secretKey) {
        prefix = null == prefix ? "" : prefix;
        secretKey = null == secretKey ? "" : secretKey;
        String join=prefix + assembleSignData(params, separator) + secretKey;
        logger.debug("[签名原文]{}",join);
        String str=DigestUtils.md5Hex(join);
        return str.toLowerCase();
    }
    
    /**
     * 使用md5哈希对参数进行签名，md5使用RFC 1321标准，编码后需要转换回全小写。算法如下（表达式中的“+”号表示字符串接，并不存在）：
     * <pre>
     * md5(prefix + sorted_compact_data + secretKey)
     * sorted_compact_data=join("", sorted_data_items)
     * sorted_data_item=key+"="+value
     * </pre>
     * 更多细节请查看wiki：<a href="http://wiki.dev1.g.uc.cn/index.php?title=%E5%85%AC%E5%85%B1%E6%8E%A5%E5%8F%A3%E5%8D%8F%E8%AE%AE%E8%A7%84%E8%8C%83">公共接口规范</a>
     * 
     * @param params 需进行拼接的参数集合(只有基础数据类型才加入签名，而对象则不加入签名)
     * @param separator 参数拼接时参数之间的分隔符。如果传入null，将转换成空字符串("")
     * @param prefix 前缀
     * @param secretKey 签名密钥
     * @return 参数拼接后md5签名生成的字符串
     * 
     * @see #assembleSignData(Map, String)
     * @see #assembleSignData(Map, List, String)
     */
    public static String createSignWithoutObjectParam(Map<String, Object> params, String separator, 
    		String prefix, String secretKey) {
    	prefix = null == prefix ? "" : prefix;
    	secretKey = null == secretKey ? "" : secretKey;
    	String join=prefix + assembleSignDataWithoutObjectParam(params, separator) + secretKey;
    	logger.debug("[签名原文]{}",join);
    	String str=DigestUtils.md5Hex(join);
    	return str.toLowerCase();
    }
    
    /**
     * 使用md5哈希对参数进行签名，md5使用RFC 1321标准，编码后需要转换回全小写。算法如下（表达式中的“+”号表示字符串接，并不存在）：
     * <pre>
     * md5(prefix + sorted_compact_data + secretKey)
     * sorted_compact_data=join("", sorted_data_items)
     * sorted_data_item=key+"="+value
     * </pre>
     * 更多细节请查看wiki：<a href="http://wiki.dev1.g.uc.cn/index.php?title=%E5%85%AC%E5%85%B1%E6%8E%A5%E5%8F%A3%E5%8D%8F%E8%AE%AE%E8%A7%84%E8%8C%83">公共接口规范</a>
     * 
     * @param params 需进行拼接的参数集合
     * @param separator 参数拼接时参数之间的分隔符。如果传入null，将转换成空字符串("")
     * @param prefix 前缀
     * @param secretKey 签名密钥
     * @param filterChars 需要过滤的字符数组
     * @return 参数拼接后md5签名生成的字符串
     * 
     * @see #assembleSignData(Map, String)
     * @see #assembleSignData(Map, List, String)
     */
    public static String createSignWithCharFilter(Map<String, Object> params, String separator, 
    		String prefix, String secretKey, String[] filterChars) {
    	prefix = null == prefix ? "" : prefix;
    	secretKey = null == secretKey ? "" : secretKey;
    	String join = prefix + assembleSignData(params, separator) + secretKey;
    	if(null != filterChars){
    		for(String charStr : filterChars){
    			join = StringUtils.trim(join).replaceAll(charStr, "");
    		}
    	}
    	logger.debug("[签名原文]{}",join);
    	String str=DigestUtils.md5Hex(join);
    	return str.toLowerCase();
    }
    
    /**
     * 使用md5哈希对参数进行签名，md5使用RFC 1321标准，编码后需要转换回全小写。算法如下（表达式中的“+”号表示字符串接，并不存在）：
     * <pre>
     * md5(prefix + sorted_compact_data + secretKey)
     * sorted_compact_data=join("", sorted_data_items)
     * sorted_data_item=key+"="+value
     * </pre>
     * 更多细节请查看wiki：<a href="http://wiki.dev1.g.uc.cn/index.php?title=%E5%85%AC%E5%85%B1%E6%8E%A5%E5%8F%A3%E5%8D%8F%E8%AE%AE%E8%A7%84%E8%8C%83">公共接口规范</a>
     * 
     * @param params 需进行拼接的参数集合
     * @param notIn 不参与签名的参数名称数组。如果为null或空数组表示所有参数都参与签名
     * @param separator 参数拼接时参数之间的分隔符。如果传入null，将转换成空字符串("")
     * @param prefix 前缀
     * @param secretKey 签名密钥
     * @return 参数拼接后md5签名生成的字符串
     */
    public static String createSign(Map<String, Object> params, List<String> notIn, String separator, 
            String prefix, String secretKey) {
        prefix = null == prefix ? "" : prefix;
        secretKey = null == secretKey ? "" : secretKey;
        String join=prefix + assembleSignData(params, notIn, separator) + secretKey;
        logger.debug("[签名原文]{}",join);
        String str=DigestUtils.md5Hex(join);
        
        return str.toLowerCase();
    }

    /**
     * 将"参数名称=>参数值"键值对列表转换成URL字符串。默认采用utf-8进行URL编码。
     * 
     * @param params "参数名称=>参数值"键值对列表
     * @param notIn 需过滤的参数（名称）列表
     * @return URL字符串
     * 
     * @throws IllegalCharsetNameException 如果不支持指定的编码字符集，将抛出此异常
     * 
     * @see #mapToUrl(Map, String[], String)
     */
    public static String mapToUrl(Map<String, Object> params, String[] notIn) {
        return mapToUrl(params, notIn, "utf-8");
    }

    /**
     * 将"参数名称=>参数值"键值对列表转换成URL字符串。默认采用utf-8进行URL编码。
     * 
     * @param params "参数名称=>参数值"键值对列表
     * @return URL字符串
     * 
     * @throws IllegalCharsetNameException 如果不支持指定的编码字符集，将抛出此异常
     * 
     * @see #mapToUrl(Map, String[], String)
     */
    public static String mapToUrl(Map<String, Object> params) {
        return mapToUrl(params, null, "utf-8");
    }

    /**
     * 将"参数名称=>参数值"键值对列表转换成URL字符串。
     * 
     * @param params "参数名称=>参数值"键值对列表
     * @param charset 参数值编码的字符集
     * @return URL字符串
     * 
     * @throws IllegalCharsetNameException 如果不支持指定的编码字符集，将抛出此异常
     * 
     * @see #mapToUrl(Map, String[], String)
     */
    public static String mapToUrl(Map<String, Object> params, String charset) {
        return mapToUrl(params, null, charset);
    }

    /**
     * 将"参数名称=>参数值"键值对列表转换成URL字符串。例：
     * <pre>
     * 参数键值对列表：
     * {
     * "key1"=>123,
     * "key2"=>"hello",
     * 100=>"num"
     * }
     * 
     * 转换后的URL字符串：key1=123&key2=hello&100=num
     * </pre>
     * 
     * @param params "参数名称=>参数值"键值对列表
     * @param notIn 需过滤的参数（名称）列表
     * @param charset 参数值编码的字符集
     * @return URL字符串
     * 
     * @throws IllegalCharsetNameException 如果不支持指定的编码字符集，将抛出此异常
     */
    public static String mapToUrl(Map<String, Object> params, String[] notIn,
            String charset) {
        if (!StringUtils.isBlank(charset) && ! Charset.isSupported(charset)) {
            throw new IllegalCharsetNameException("不支持的编码字符集:"+charset);
        }
        
        StringBuilder buffer = new StringBuilder();
        List<String> notInList = null;
        if (null != notIn && notIn.length > 0) {
            notInList = Arrays.asList(notIn);
        }
        boolean isFirst = true;
        for (Object key : params.keySet()) {
            Object value = params.get(key);
            if (notIn != null && notInList.contains(key.toString())) {
                continue;
            }
            
            if (isFirst) {
                isFirst = false;
            } else {
                buffer.append("&");
            }
            
            buffer.append(key).append("=");
            if (value != null) {
                try {
                        buffer.append(charset == null ? value.toString() : URLEncoder.encode(value.toString(), charset));
                } catch (UnsupportedEncodingException e) {
                    // ignore
                }
            }
        }
        
        return buffer.toString();
    }

    /**
     * 将URL字符串转换成"参数名称=>参数值"键值对列表。默认采用utf-8进行URL解码。
     * 
     * @param url URL字符串
     * @return "参数名称=>参数值"键值对列表。如果URL字符串为空，将返回空的键值对列表。
     * 
     * @see #urlToMap(String, String)
     */
    public static Map<String, String> urlToMap(String url) {
        return urlToMap(url, "utf-8");
    }

    /**
     * 将URL字符串转换成"参数名称=>参数值"键值对列表。
     * 
     * @param url URL字符串
     * @param charset URL解码字符集
     * @return "参数名称=>参数值"键值对列表。如果URL字符串为空，将返回空的键值对列表。
     */
    public static Map<String, String> urlToMap(String url, String charset) {
        Map<String, String> params = new HashMap<String, String>();
        if (StringUtils.isEmpty(url)) {
            return params;
        }
        
        // 剔除最前面的问号(?)
        int start = url.indexOf("?");
        if (start != -1) {
            url = url.substring(start);
        }
        
        String[] keyValues = url.split("&");
        for (String keyValue : keyValues) {
            int index = keyValue.indexOf("=");
            if (index != -1) {
                String key = keyValue.substring(0, index);
                String value = keyValue.substring(index + 1);
                try {
                    params.put(key.trim(), URLDecoder.decode(value.trim(), charset));
                } catch (UnsupportedEncodingException e) {
                    // ignore
                }
            }
        }
        
        return params;
    }

}
