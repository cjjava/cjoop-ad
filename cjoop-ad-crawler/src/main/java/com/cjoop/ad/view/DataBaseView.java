package com.cjoop.ad.view;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.sql.DataSource;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.cjoop.ad.Constant;
import com.cjoop.ad.domain.DBType;

/**
 * 数据库视图
 * 
 * @author 陈均
 *
 */
@Component
public class DataBaseView extends JPanel {

	private static final long serialVersionUID = 8464498036587247805L;
	private JTextField txtUrl;
	private JTextField txtUserName;
	private JPasswordField txtPassword;
	private JComboBox<DBType> cboxType;
	private boolean importAction = false;
	JLabel lblTip;
	DataSource dataSource;
	JdbcTemplate jdbcTemplate;
	@Autowired
	private PropertiesConfiguration dbConfig;
	protected Log logger = LogFactory.getLog(getClass());

	public DataBaseView() {
		setLayout(null);

		JLabel lblDBType = new JLabel("类型：");
		lblDBType.setFont(Constant.font_song_12);
		lblDBType.setBounds(56, 62, 36, 15);
		add(lblDBType);

		JLabel lblURL = new JLabel("连接地址：");
		lblURL.setFont(Constant.font_song_12);
		lblURL.setBounds(32, 93, 60, 15);
		add(lblURL);

		txtUrl = new JTextField();
		txtUrl.setBounds(102, 90, 321, 21);
		add(txtUrl);
		txtUrl.setColumns(10);

		initDBType();

		JLabel lblUserName = new JLabel("用户名：");
		lblUserName.setFont(Constant.font_song_12);
		lblUserName.setBounds(44, 124, 50, 15);
		add(lblUserName);

		txtUserName = new JTextField();
		txtUserName.setBounds(102, 121, 321, 21);
		add(txtUserName);
		txtUserName.setColumns(10);

		JLabel lblPassword = new JLabel("密码：");
		lblPassword.setFont(Constant.font_song_12);
		lblPassword.setBounds(56, 155, 36, 15);
		add(lblPassword);

		txtPassword = new JPasswordField();
		txtPassword.setBounds(102, 152, 321, 21);
		add(txtPassword);

		lblTip = new JLabel("配置数据库信息");
		lblTip.setBounds(32, 22, 288, 15);
		lblTip.setFont(Constant.font_song_12);
		lblTip.setForeground(Color.RED);
		add(lblTip);

		JButton btnImport = new JButton("导入");
		btnImport.setBounds(328, 193, 93, 23);
		btnImport.setFont(Constant.font_song_12);

		btnImport.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				lblTip.setForeground(Color.RED);
				lblTip.setText("正在进行数据导入操作,请耐心等待...");
				buildDataSource();
				try {
					dataSource.getConnection();
					jdbcTemplate = new JdbcTemplate(dataSource);
					saveDBConfig();//保存db配置信息,下次回显
					importData();
				} catch (SQLException ex) {
					lblTip.setText("连接数据库失败,检查配置是否正确-_-!");
				}
			}
		});
		add(btnImport);
	}

	protected void saveDBConfig() {
		DBType dbType = (DBType) cboxType.getSelectedItem();
		dbConfig.setProperty("driverClassName", dbType.getDriverClassName());
		dbConfig.setProperty("username", txtUserName.getText());
		dbConfig.setProperty("url", txtUserName.getText());
		try {
			dbConfig.save();
		} catch (ConfigurationException ce) {
			logger.error("保存db配置信息失败",ce);
		}
	}

	/**
	 * 构造数据源信息
	 */
	private void buildDataSource() {
		DriverManagerDataSource driverManagerDataSource = new DriverManagerDataSource();
		DBType dbType = (DBType) cboxType.getSelectedItem();
		driverManagerDataSource.setDriverClassName(dbType.getDriverClassName());
		driverManagerDataSource.setUrl(txtUrl.getText());
		driverManagerDataSource.setUsername(txtUserName.getText());
		driverManagerDataSource.setPassword(new String(txtPassword.getPassword()));
		dataSource = driverManagerDataSource;
	}

	/**
	 * 导入数据操作
	 */
	public synchronized void importData() {
		if (!importAction) {
			importAction = true;
			new Thread(new Runnable() {
				@Override
				public void run() {
					Assert.notNull(jdbcTemplate, "The jdbcTemplate must not be null");
					File provinceDir = new File(System.getProperty("user.dir"), "province");
					List<DataImportTask> dataImportTasks = new ArrayList<DataImportTask>();
					for (File file : provinceDir.listFiles()) {
						DataImportTask dataImportTask = new DataImportTask(file);
						dataImportTasks.add(dataImportTask);
					}
					long sum = 0;
					try {
						 ExecutorService exec = Executors.newCachedThreadPool();
						List<Future<DataImportResult>> futures = exec.invokeAll(dataImportTasks);
						for (Future<DataImportResult> future : futures) {
							DataImportResult result = future.get();
							sum += result.getSuccessCount();
						}
						lblTip.setForeground(Constant.dark_green);
						lblTip.setText("成功导入数据" + sum + "条.");
						importAction = false;
					} catch (Exception e) {
						lblTip.setText("统计结果出错:" + e.getMessage());
					}
				}
			}).start();
		}
	}

	/**
	 * 数据导入结果信息
	 * @author 陈
	 *
	 */
	protected class DataImportResult {
		private long successCount = 0;
		private long failCount = 0;

		public long getSuccessCount() {
			return successCount;
		}

		public void setSuccessCount(long successCount) {
			this.successCount = successCount;
		}

		public long getFailCount() {
			return failCount;
		}

		public void setFailCount(long failCount) {
			this.failCount = failCount;
		}

	}

	/**
	 * 数据导入任务
	 * 
	 * @author 陈均
	 *
	 */
	protected class DataImportTask implements Callable<DataImportResult> {
		File file;

		public DataImportTask(File file) {
			this.file = file;
		}

		@Override
		public DataImportResult call() throws Exception {
			String data = FileUtils.readFileToString(file);
			String[] list = data.split("#");
			for (String item : list) {
				String[] array = item.split(",");
				System.out.println("insert into " + array[0] + "," + array[1]);
			}
			DataImportResult dataImportResult = new DataImportResult();
			dataImportResult.setSuccessCount(list.length);
			return dataImportResult;
		}

	}

	/**
	 * 初始化数据库类型下拉信息
	 */
	private void initDBType() {
		cboxType = new JComboBox<DBType>();
		cboxType.setFont(Constant.font_song_12);
		cboxType.setBounds(102, 59, 218, 21);

		add(cboxType);

		cboxType.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				DBType dbType = (DBType) e.getItem();
				if (e.getStateChange() == ItemEvent.SELECTED) {
					txtUrl.setText(dbType.getUrl());
				}
			}
		});

		cboxType.addItem(DBType.ORACLE);
		cboxType.addItem(DBType.MYSQL);
		cboxType.addItem(DBType.H2_EMBEDDED);
		cboxType.addItem(DBType.H2_SERVER);

	}
}
