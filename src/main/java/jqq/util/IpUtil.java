package jqq.util;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import org.springframework.util.StringUtils;

public class IpUtil {

	/**
	 * 得到远程IP地址，需要经过nginx代理得到ip
	 *
	 */
	public static String getRemoteIp(HttpServletRequest request) {
        String ip = "";
        String forward_for_ip = "";
        Enumeration<String> headers = request.getHeaderNames();
        while (headers.hasMoreElements()) {
        	String name = headers.nextElement();
        	if ("x-real-ip".equals(name) || "X-Real-IP".equals(name)) {
        		ip = request.getHeader("x-real-ip");
        		if (ip == null) {
					ip = request.getHeader("X-Real-IP");
				}
			}
        	if ("x-forwarded-for".equals(name)) {
				forward_for_ip = request.getHeader("x-forwarded-for");
			}
		}
        if(ip != null && !"".equals(ip.trim()) && !"unknown".equalsIgnoreCase(ip)){
        	return ip;
        }
		ip = forward_for_ip;
        if(ip != null && !"".equals(ip.trim()) && !"unknown".equalsIgnoreCase(ip)){
        	return ip;
        } else{
        	ip = request.getRemoteAddr();
        }
        return getOutnerIp(ip);
    }
	
	public static String getOutnerIp(String ip){   
		if (StringUtils.isEmpty(ip) || "null".equals(ip) || "unknown".equalsIgnoreCase(ip)) {
			return "";
		}
        int index = ip.indexOf(',');
        if (index == -1) {
        	return ip;
        }
        String[] ips = ip.split(",");
        if(ips != null && ips.length > 0){
        	for (int i = 0; i < ips.length; i++) {
        		//不是unknown且不是内网IP,则返回
				if(!"unknown".equalsIgnoreCase(ips[i].trim()) && !isInnerIP(ips[i].trim())){
					return ips[i].trim();
				}
			}
        }
		return null;
	}
	
	
	/**
	 * 是否内部IP
	 * 
	 * @param
	 * @author <a href="mailto:linzc3@ucweb.com">林钊川 </a>
	 * @version 1.0.0
	 * @since 1.0.0
	 * create on: 2014-10-23
	 */
	public static boolean isInnerIP(String ipAddress){   
        boolean isInnerIp = false;   
        long ipNum = getIpNum(ipAddress);   
        /**  
        	私有IP：A类  10.0.0.0-10.255.255.255  
               B类  172.16.0.0-172.31.255.255  
               C类  192.168.0.0-192.168.255.255  
        	当然，还有127这个网段是环回地址  
        **/  
        long aBegin = getIpNum("10.0.0.0");   
        long aEnd = getIpNum("10.255.255.255");   
        long bBegin = getIpNum("172.16.0.0");   
        long bEnd = getIpNum("172.31.255.255");   
        long cBegin = getIpNum("192.168.0.0");   
        long cEnd = getIpNum("192.168.255.255");   
        isInnerIp = isInner(ipNum,aBegin,aEnd) || isInner(ipNum,bBegin,bEnd) || isInner(ipNum,cBegin,cEnd) || ipAddress.equals("127.0.0.1");   
        return isInnerIp;              
	}  

	private static long getIpNum(String ipAddress) {   
	    String [] ip = ipAddress.split("\\.");   
	    long a = Integer.parseInt(ip[0]);   
	    long b = Integer.parseInt(ip[1]);   
	    long c = Integer.parseInt(ip[2]);   
	    long d = Integer.parseInt(ip[3]);   
	  
	    long ipNum = a * 256 * 256 * 256 + b * 256 * 256 + c * 256 + d;   
	    return ipNum;   
	}  

	private static boolean isInner(long userIp,long begin,long end){   
	     return (userIp>=begin) && (userIp<=end);   
	}  
}
