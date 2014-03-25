/**
 * 
 */
package com.slyak.applet;

/**
 * The Class FtpConfig.
 *
 * @author stormning@163.com
 */
public class FtpConfig {

	/** 用户名. */
	private String uname;
	
	/** 密码. */
	private String upwd;
	
	
	/** 端口号. */
	private int port;
	
	/** IP地址. */
	private String host;
	
	
	/** FTP服务器编码格式. */
	private String controlEncoding = "ISO-8859-1";
	
	/** 网盘路径. */
	private String diskPath;
	
	private OS os = OS.WINDOWS;

	/**
	 * Gets the uname.
	 *
	 * @return the uname
	 */
	public String getUname() {
		return uname;
	}

	/**
	 * Sets the uname.
	 *
	 * @param uname the uname to set
	 */
	public void setUname(String uname) {
		this.uname = uname;
	}

	/**
	 * Gets the upwd.
	 *
	 * @return the upwd
	 */
	public String getUpwd() {
		return upwd;
	}

	/**
	 * Sets the upwd.
	 *
	 * @param upwd the upwd to set
	 */
	public void setUpwd(String upwd) {
		this.upwd = upwd;
	}

	/**
	 * Gets the port.
	 *
	 * @return the port
	 */
	public int getPort() {
		return port;
	}

	/**
	 * Sets the port.
	 *
	 * @param port the port to set
	 */
	public void setPort(int port) {
		this.port = port;
	}

	/**
	 * Gets the host.
	 *
	 * @return the host
	 */
	public String getHost() {
		return host;
	}

	/**
	 * Sets the host.
	 *
	 * @param host the host to set
	 */
	public void setHost(String host) {
		this.host = host;
	}

	/**
	 * Gets the disk path.
	 *
	 * @return the diskPath
	 */
	public String getDiskPath() {
		return diskPath;
	}

	/**
	 * Sets the disk path.
	 *
	 * @param diskPath the diskPath to set
	 */
	public void setDiskPath(String diskPath) {
		this.diskPath = diskPath;
	}

	/**
	 * Gets the control encoding.
	 *
	 * @return the controlEncoding
	 */
	public String getControlEncoding() {
		return controlEncoding;
	}

	/**
	 * Sets the control encoding.
	 *
	 * @param controlEncoding the controlEncoding to set
	 */
	public void setControlEncoding(String controlEncoding) {
		this.controlEncoding = controlEncoding;
	}

	/**
	 * @return the os
	 */
	public OS getOs() {
		return os;
	}

	/**
	 * @param os the os to set
	 */
	public void setOs(OS os) {
		this.os = os;
	}
}
