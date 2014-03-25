/**
 * 
 */
package com.slyak.applet;

/**
 * The Interface UploadProcessor.
 *
 * @author stormning@163.com
 */
public interface UploadProcessor {
	
	void beforeUpload(long total,long offset,String fileName);
	
	void onUpload(long read,long costTime);
	
	void onUploadFinished();
}
