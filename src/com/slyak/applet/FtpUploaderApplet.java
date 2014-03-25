/**
 * 
 */
package com.slyak.applet;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Properties;

import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;

import netscape.javascript.JSObject;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

/**
 * The Class FtpUploader.
 *
 * @author stormning@163.com
 */
public class FtpUploaderApplet extends JApplet {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The self. */
	private JApplet self;

	private JSObject window;

	/** The selected file. */
	private File selectedFile;
	// private JTextField fileLocation = new JTextField(26);

	/** The progress bar. */
	private JProgressBar progressBar;
	
	/** The progress info. */
	private JLabel progressInfo;

	/** The file chooser. */
	private JFileChooser fileChooser;
	
	/** The file name field. */
	private JTextField fileNameField;
	
	/** The choose btn. */
	private JButton chooseBtn;
	
	/** The upload btn. */
	private JButton uploadBtn;
	
	/** The select panel. */
	private JPanel selectPanel;
	
	/** The progress panel. */
	private JPanel progressPanel;

	/** FTP配置. */
	private FtpConfig ftpConfig;

	/** 文件上传路径. */
	private String uploadPath;
	
	/** 允许上传的后缀名. */
	private String allowTypes;
	
	/** 文件大小限制. */
	private long maxSize;

	/** 国际化文件. */
	private static Properties messageSource = new Properties();
	
	private static Properties properties = new Properties();

	/** ftpConnector. */
	private FtpConnector ftpConnector;
	
	private String fileName;
	
	/** call back js on upload finished.*/
	private String beforeUpload;
	
	private String onUploadFinished;
	
	private String separator;
	
	/* (non-Javadoc)
	 * @see java.applet.Applet#init()
	 */
	@Override
	public void init() {
		loadConfig();
		initUploadParams();
		initFtpConnector();
		initLayout();
		initEvents();
	}

	/**
	 * Load config.
	 */
	private void loadConfig() {
		try {
			
			if(properties.isEmpty()) {
				URL url = new URL(this.getCodeBase(), Constants.FTP_CONFIG_FILE_NAME);
				URLConnection uc = url.openConnection();
				properties.load(uc.getInputStream());
			}

			List<FtpConfig> ftpConfigs = JSON.parseObject(properties.getProperty(Constants.FTP_CONFIGS),new TypeReference<List<FtpConfig>>() {});
			int cfgIndex = (int) (Math.random() * (ftpConfigs.size() - 1));
			this.ftpConfig = ftpConfigs.get(cfgIndex);
			this.separator = ftpConfig.getOs() == OS.WINDOWS? "\\" : "/";
			
			if(messageSource.isEmpty()) {
				URL murl = new URL(this.getCodeBase(), Constants.MESSAGE_SOURCE_NAME);
				URLConnection muc = murl.openConnection();
				messageSource.load(muc.getInputStream());
			}
		} catch (IOException e) {
			JOptionPane.showMessageDialog(this, getMessage(MessageCode.FTP_CONFIG_ERROR));
		}
	}
	
	/**
	 * Inits the upload params.
	 */
	private void initUploadParams() {
		this.uploadPath = getParameter(Constants.PARAM_UPLOAD_PATH);
		this.allowTypes = getParameter(Constants.PARAM_ALLOW_TYPES);
		this.maxSize = NumberUtils.toLong(getParameter(Constants.PARAM_MAX_SIZE), -1);
		if(uploadPath == null) {
			JOptionPane.showMessageDialog(this, getMessage(MessageCode.FTP_UPLOAD_PARAM_ERROR));
		}
		this.beforeUpload = getParameter(Constants.PARAM_BEFORE_UPLOAD);
		this.onUploadFinished = getParameter(Constants.PARAM_ON_UPLOAD_FINISHED);
	}

	/**
	 * Inits the ftp connector.
	 */
	private void initFtpConnector() {
		try {
			this.ftpConnector = new FtpConnector(ftpConfig.getUname(), ftpConfig.getUpwd(), ftpConfig.getHost(), ftpConfig.getPort());
			this.ftpConnector.setControlEncoding(ftpConfig.getControlEncoding());
		} catch (IOException e) {
			JOptionPane.showMessageDialog(self,getMessage(MessageCode.FTP_INIT_ERROR));
		}
	}

	/**
	 * Inits the layout.
	 */
	private void initLayout() {
		self = this;
		window = JSObject.getWindow(self);
		// fileLocation.setEditable(false);
		fileNameField = new JTextField(25);
		fileNameField.setEditable(false);
		fileNameField.setBackground(Color.WHITE);
		
		chooseBtn = new JButton();
		chooseBtn.setText(getMessage(MessageCode.FTP_FILE_CHOOSE));
		
		uploadBtn = new JButton();
		uploadBtn.setText(getMessage(MessageCode.FTP_FILE_UPLOAD));
		uploadBtn.setEnabled(false);
		
		fileChooser = new JFileChooser();
		fileChooser.setDialogTitle(getMessage(MessageCode.FTP_FILECHOOSER_TITLE));
		fileChooser.setAcceptAllFileFilterUsed(false);
		
		final boolean filterTypes = StringUtils.isNotBlank(allowTypes);
		final boolean filterMaxSize = maxSize>0;
		
		if(filterTypes||filterMaxSize) {
			fileChooser.setFileFilter(new FileFilter() {
				@Override
				public String getDescription() {
					return filterTypes? getMessage(MessageCode.ALLOW_ALL_FILE) : getMessage(MessageCode.ALLOW_SOME_FILE) + allowTypes;
				}
				
				@Override
				public boolean accept(File f) {
					
					if (f.isDirectory()) {
						return true;
					}
					
					boolean flag = true;
					if(filterTypes) {
						flag = false;
						String ext = FilenameUtils.getExtension(f.getName());
						String[] types = StringUtils.split(allowTypes,",");
						for (String t : types) {
							if(StringUtils.equalsIgnoreCase(t, ext)) {
								flag = true;
								break;
							}
						}
					}
					
					if(flag && filterMaxSize) {
						flag = FileUtils.sizeOf(f) <= maxSize;
					}
					
					return flag;
				}
			});
		}
		
		progressInfo = new JLabel(getMessage(MessageCode.FTP_UPLOAD_PROGRESS));
		
		progressBar = new JProgressBar(0, 100);
		progressBar.setPreferredSize(new Dimension(380,20));
		// progressBar.setString("请选择文件");
		progressBar.setStringPainted(true);
		progressBar.setBorderPainted(true);
		// progressBar.setBackground(Color.gray);
		// progressBar.setForeground(Color.blue);
		
		progressPanel = new JPanel();
		progressPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		progressPanel.add(progressInfo);
		progressPanel.add(progressBar);
		progressPanel.setBackground(Color.WHITE);
		progressPanel.setVisible(false);

		// main.add(fileLocation);
		selectPanel = new JPanel();
		selectPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		selectPanel.add(fileNameField);
		selectPanel.add(chooseBtn);
		selectPanel.add(uploadBtn);
		selectPanel.setBackground(Color.WHITE);
		
		GridLayout layout = new GridLayout(2, 1);
		Container container = self.getContentPane();
		container.setLayout(layout);
		container.add(selectPanel);
		container.add(progressPanel);
		container.setBackground(Color.WHITE);
	}

	/**
	 * Inits the events.
	 */
	private void initEvents() {
		chooseBtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (chooseBtn.isEnabled()) {
					if (fileChooser.showOpenDialog(self) == JFileChooser.APPROVE_OPTION) {
						selectedFile = fileChooser.getSelectedFile();
						fileNameField.setText(selectedFile.getPath());
						uploadBtn.setEnabled(true);
					}
				}
			}
		});

		uploadBtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (uploadBtn.isEnabled()) {

					// disable btns
					uploadBtn.setEnabled(false);
					chooseBtn.setEnabled(false);
					showProgressBar();

					new Thread() {
						@Override
						public void run() {
							try {
								ftpConnector.upload(selectedFile, uploadPath, new FtpUploadProcessor());
							} catch (IOException e) {
								JOptionPane.showMessageDialog(self, getMessage(MessageCode.FTP_UPLOAD_ERROR));
							}
						}
					}.start();

				}
			}

		});
	}

	/**
	 * Show progress bar.
	 */
	private void showProgressBar() {
		progressPanel.setVisible(true);
		progressBar.setValue(0);
	}

	/* (non-Javadoc)
	 * @see java.applet.Applet#destroy()
	 */
	@Override
	public void destroy() {
		super.destroy();
		ftpConnector.destory();
	}
	
	private String getMessage(String code) {
		return messageSource.getProperty(code,code);
	}
	
	
	public class FtpUploadProcessor implements UploadProcessor {
		private long total = 0;
		private long offset = 0;
		private long readTotal = 0;
		private double costTotal = 1;
		
		private String totalDisplay;

		@Override
		public void beforeUpload(long total, long offset ,String fname) {
			fileName = fname;
			this.total = total;
			this.offset = offset;
			totalDisplay = FileUtils.byteCountToDisplaySize(total);
			progressBar.setString(getMessage(MessageCode.FTP_UPLOAD_WAIT));
			drawProgressBar();
			if(beforeUpload != null) {
				window.call(beforeUpload,null);
			}
		}

		@Override
		public void onUpload(long read, long costTime) {
			readTotal += read;
			costTotal += costTime;
			drawProgressBar();
		}
		
		private void drawProgressBar() {
			String uploadedDisplay = FileUtils.byteCountToDisplaySize(offset+readTotal);
			int percent = (int) Math.floor((offset + readTotal) * 100 / total);
			String speedDisplay = FileUtils.byteCountToDisplaySize((long)Math.ceil((readTotal/(int)Math.ceil(costTotal/1000))));
			
			StringBuffer display = new StringBuffer();
			display.append(uploadedDisplay).append('/').append(totalDisplay)
					.append('(').append(percent).append("%,")
					.append(getMessage(MessageCode.AVERAGE))
					.append(speedDisplay).append('/')
					.append(getMessage(MessageCode.SECOND)).append(')');
			
			progressBar.setValue(percent);
			progressBar.setString(display.toString());
		}

		@Override
		public void onUploadFinished() {
			progressBar.setString(getMessage(MessageCode.FTP_UPLOAD_FINISHED));
			chooseBtn.setEnabled(true);
			
			if(onUploadFinished != null) {
				String ext = FilenameUtils.getExtension(fileName);
				window.call(onUploadFinished, new Object[] {getFilePath(fileName),fileName,total,ext});
			}
			JOptionPane.showMessageDialog(self, getMessage(MessageCode.FTP_UPLOAD_FINISHED));
		}
	}
	
	private String getFilePath(String fname) {
		return clean(ftpConfig.getDiskPath()) + clean(uploadPath) + separator +fname;
	}
	
	private String clean(String path) {
		return StringUtils.replaceEach(path, new String[] {"/","\\"}, new String[] {separator,separator});
	}
}
