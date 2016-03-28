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
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.cjoop.ad.Constant;
import com.cjoop.ad.domain.ADInfo;
import com.cjoop.ad.domain.DBType;
import com.cjoop.ad.domain.DataImportResult;
import com.mchange.v2.c3p0.ComboPooledDataSource;

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
	JLabel lblCount;
	DataSource dataSource;
	JdbcTemplate jdbcTemplate;
	@Autowired
	private PropertiesConfiguration dbConfig;
	protected Log logger = LogFactory.getLog(getClass());

	public DataBaseView() {

	}

	protected void saveDBConfig() {
		DBType dbType = (DBType) cboxType.getSelectedItem();
		dbConfig.setProperty(Constant.dbtype, dbType.getName());
		dbConfig.setProperty(Constant.username, txtUserName.getText());
		dbConfig.setProperty(Constant.url, txtUrl.getText());
		try {
			dbConfig.save();
		} catch (ConfigurationException ce) {
			logger.error("保存db配置信息失败", ce);
		}
	}

	/**
	 * 构造数据源信息
	 */
	private void buildDataSource() {
		try {
			DBType dbType = (DBType) cboxType.getSelectedItem();
			ComboPooledDataSource cpds = new ComboPooledDataSource();
			cpds.setDriverClass(dbType.getDriverClassName());
			cpds.setJdbcUrl(txtUrl.getText());
			cpds.setUser(txtUserName.getText());
			cpds.setPassword(new String(txtPassword.getPassword()));
			dataSource = cpds;
		} catch (Exception e) {
			logger.error("构造数据源失败", e);
			dataSource = null;
		}
	}

	/**
	 * 导入数据操作
	 */
	public synchronized void importData() {
		Assert.notNull(jdbcTemplate, "The jdbcTemplate must not be null");
		if (!importAction) {
			importAction = true;
			new Thread(new Runnable() {
				@Override
				public void run() {
					File provinceDir = new File(System.getProperty("user.dir"), "province");
					List<DataImportTask> dataImportTasks = new ArrayList<DataImportTask>();
					for (File file : provinceDir.listFiles()) {
						DataImportTask dataImportTask = new DataImportTask(file,jdbcTemplate);
						dataImportTasks.add(dataImportTask);
					}
					jdbcTemplate.update("delete from adinfo");
					long sum = 0,endTime=0,total=0,provinceCount=0,cityCount=0,countyCount=0,townCount=0,villageCount=0;
					long startTime = System.currentTimeMillis();
					StringBuffer error = new StringBuffer();
					try {
						ExecutorService exec = Executors.newCachedThreadPool();
						List<Future<DataImportResult>> futures = exec.invokeAll(dataImportTasks);
						for (Future<DataImportResult> future : futures) {
							DataImportResult result = future.get();
							sum += result.getSuccessCount();
							total+=result.getTotalCount();
							provinceCount++;
							cityCount+=result.getCityCount();
							countyCount+=result.getCountyCount();
							townCount+=result.getTownCount();
							villageCount+=result.getVillageCount();
							if (result.getEndTime() > endTime) {
								endTime = result.getEndTime();
							}
							if(result.getError()==1){
								error.append(result.getErrorText());
							}
						}
						long exeTime = endTime - startTime;
						lblTip.setForeground(Constant.dark_green);
						lblTip.setText("总的数据量" + total +"成功导入数据" + sum + "条,执行时间" + exeTime + "毫秒." + error);
						lblCount.setText("省("+provinceCount+"),市("+cityCount+"),县("+countyCount+"),镇("+townCount+"),乡("+villageCount+")");
						importAction = false;
					} catch (Exception e) {
						lblTip.setText("统计结果出错:" + e.getMessage());
					}
				}
			}).start();
		}
	}

	/**
	 * 初始化数据库类型下拉信息
	 */
	protected void initDBType() {
		cboxType = new JComboBox<DBType>();
		cboxType.setFont(Constant.font_song_12);
		cboxType.setBounds(102, 59, 218, 21);
		add(cboxType);
		cboxType.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				DBType dbType = (DBType) e.getItem();
				if (e.getStateChange() == ItemEvent.SELECTED) {
					txtUrl.setText(dbType.getUrl());
					txtUserName.setText("");
					txtPassword.setText("");
				}
			}
		});
		for (DBType dbType : DBType.values()) {
			cboxType.addItem(dbType);
		}
		String type = dbConfig.getString(Constant.dbtype);
		if (StringUtils.isNotBlank(type)) {
			cboxType.setSelectedItem(DBType.valueOf(type));
			String url = dbConfig.getString(Constant.url);
			txtUrl.setText(url);
			String username = dbConfig.getString(Constant.username);
			txtUserName.setText(username);
		}
	}

	public void init() {
		setLayout(null);
		initCompInfo();
		initDBType();
		initBtnImport();
	}

	/**
	 * 初始化组件信息
	 */
	private void initCompInfo() {
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
		lblTip.setBounds(32, 12, 588, 15);
		lblTip.setFont(Constant.font_song_12);
		lblTip.setForeground(Color.RED);
		add(lblTip);
		
		lblCount = new JLabel("");
		lblCount.setBounds(32,32,588,15);
		lblCount.setFont(Constant.font_song_12);
		lblCount.setForeground(Constant.dark_green);
		add(lblCount);
		
	}

	/**
	 * 初始化导入按钮信息
	 */
	private void initBtnImport() {
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
					saveDBConfig();
					initTableInfo();
					importData();
				} catch (SQLException ex) {
					lblTip.setText("连接数据库失败,检查配置是否正确-_-!");
				}
			}
		});
		add(btnImport);
	}

	/**
	 * 初始化行政区划表信息
	 */
	protected void initTableInfo() {
		Assert.notNull(dataSource, "The dataSource must not be null");
		Configuration configiguration = new Configuration().setProperty(Environment.SHOW_SQL, "true")
				.setProperty(Environment.HBM2DDL_AUTO, "update");
		configiguration.addAnnotatedClass(ADInfo.class);
		configiguration.buildSessionFactory(
				new StandardServiceRegistryBuilder().applySetting(Environment.DATASOURCE, dataSource)
						.applySettings(configiguration.getProperties()).build());
	}

}
