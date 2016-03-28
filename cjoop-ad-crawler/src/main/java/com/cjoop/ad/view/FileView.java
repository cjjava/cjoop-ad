package com.cjoop.ad.view;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.cjoop.ad.Constant;
import com.cjoop.ad.domain.DBType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mchange.v2.c3p0.ComboPooledDataSource;

/**
 * 生成文件视图
 * 
 * @author 陈均
 *
 */
@Component
public class FileView extends JPanel {

	private static final String SELECT_FROM_ADINFO_WHERE_PID = "select * from adinfo where pid = ?";
	private static final long serialVersionUID = -8065172197241339431L;
	protected Log logger = LogFactory.getLog(getClass());
	JdbcTemplate jdbcTemplate;
	@Autowired
	private PropertiesConfiguration dbConfig;
	@Autowired
	private ObjectMapper objectMapper;
	JLabel lblTip;
	boolean create = false;

	/**
	 * Create the panel.
	 */
	public FileView() {
		setLayout(null);
		JButton btnJSFile = new JButton("生成js数据文件");
		btnJSFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				createJSFile();
			}
		});
		btnJSFile.setFont(Constant.font_song_12);
		btnJSFile.setBounds(27, 22, 124, 23);
		add(btnJSFile);

		lblTip = new JLabel("");
		lblTip.setFont(Constant.font_song_12);
		lblTip.setBounds(27, 57, 261, 15);
		lblTip.setForeground(Color.red);
		add(lblTip);
	}

	private synchronized void createJSFile() {
		if (!create) {
			create = true;
			lblTip.setText("正在生成js数据文件...");
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						String type = dbConfig.getString(Constant.dbtype);
						if (StringUtils.isNotBlank(type)) {
							String url = dbConfig.getString(Constant.url);
							String username = dbConfig.getString(Constant.username);
							String password = dbConfig.getString(Constant.username);
							ComboPooledDataSource cpds = new ComboPooledDataSource();
							cpds.setDriverClass(DBType.valueOf(type).getDriverClassName());
							cpds.setJdbcUrl(url);
							cpds.setUser(username);
							cpds.setPassword(password);
							jdbcTemplate = new JdbcTemplate(cpds);

							List<Map<String, Object>> provinceItems = findADInfoByPID("0");
							for (Map<String, Object> province : provinceItems) {
								List<Map<String, Object>> cityItems = findADInfoByPID(province.get("i"));
								province.put("c", cityItems);
								for (Map<String, Object> city : cityItems) {
									List<Map<String, Object>> countyItems = findADInfoByPID(city.get("i"));
									city.put("c", countyItems);
								}
							}
							cpds.close();
							String json ="window.adData = " + objectMapper.writeValueAsString(provinceItems) + ";";
							File jsonFile = new File(System.getProperty("user.dir"), "angular-ad-data.js");
							FileUtils.writeStringToFile(jsonFile, json);
							lblTip.setForeground(Constant.dark_green);
							lblTip.setText("生成js数据文件结束...");
						}
					} catch (Exception e) {
						logger.error("生成js数据文件", e);
					}
					create = false;
				}
			}).start();
		}
	}

	public String string2Unicode(String string) {
	    StringBuffer unicode = new StringBuffer();
	    for (int i = 0; i < string.length(); i++) {
	        char c = string.charAt(i);
	        unicode.append("\\u" + Integer.toHexString(c));
	    }
	    return unicode.toString();
	}
	
	private List<Map<String, Object>> findADInfoByPID(Object pid) {
		return jdbcTemplate.query(SELECT_FROM_ADINFO_WHERE_PID, new Object[] { pid },
			new RowMapper<Map<String, Object>>() {
				@Override
				public Map<String, Object> mapRow(ResultSet rs, int rowNum) throws SQLException {
					Map<String, Object> item = new HashMap<String, Object>();
					item.put("i", rs.getString("id"));
					item.put("n", string2Unicode(rs.getString("name")));
					return item;
				}
			});
	}
}
