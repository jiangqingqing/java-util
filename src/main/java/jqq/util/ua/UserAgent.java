package jqq.util.ua;

/**
 * UserAgent 关键数据封装
 * 
 * @author 梁晓峰<liangxf@ucweb.com>
 * @crateDate 2014年2月7日
 */
public class UserAgent {

	public static enum OS {
		Windows,
		Linux,
		iOS,
		Android,
		Symbian,
		WinMobile,
		WinPhone,
		Java,
		BlackBerry,
		Bada,
		Unknown
	}

	private OS os;
	private String device;

	public UserAgent(OS os, String device) {
		this.os = os;
		this.device = device;
	}

	public boolean isEmpty() {
		if (null == this.device) {
			return true;
		}
		if (0 == this.device.length()) {
			return true;
		}
		return false;
	}

	/**
	 * 操作系统信息
	 */
	public OS getOs() {
		return os;
	}

	/**
	 * 设备名称
	 */
	public String getDevice() {
		if (null == this.device) {
			return "Unknow Device";
		}
		String dev = device.trim();
		int indexOfBuild = dev.indexOf(" Build/");
		if (indexOfBuild > 0) {
			dev = dev.substring(0, indexOfBuild);
		}
		return dev;
	}
}
