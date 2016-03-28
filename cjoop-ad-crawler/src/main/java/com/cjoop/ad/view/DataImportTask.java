package com.cjoop.ad.view;

import java.io.File;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import com.cjoop.ad.domain.DataImportResult;

/**
 * 数据导入任务
 * 
 * @author 陈均
 *
 */
public class DataImportTask implements Callable<DataImportResult> {
	static Pattern cityPattern = Pattern.compile("^((\\d{2})\\d{2})0{8}$");// 市级数据判断
	static Pattern countyPattern = Pattern.compile("^((\\d{4})\\d[1-9]{1})0{6}$");// 县级数据判断
	static Pattern townPattern = Pattern.compile("^((\\d{6})\\d{2}[1-9]{1})0{3}$");// 镇级数据判断
	static Pattern villagePattern = Pattern.compile("^((\\d{9})\\d{3})$");// 乡级数据判断
	protected Log logger = LogFactory.getLog(getClass());
	File file;
	JdbcTemplate jdbcTemplate;

	public DataImportTask(File file,JdbcTemplate jdbcTemplate) {
		this.file = file;
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	public DataImportResult call() throws Exception {
		DataImportResult dataImportResult = new DataImportResult();
		dataImportResult.setStartTime(System.currentTimeMillis());
		try {
			int successCount = 0;
			String baseName = FilenameUtils.getBaseName(file.getName());
			String[] array = baseName.split("_");
			String name = array[0], code = array[1];
			jdbcTemplate.update("INSERT INTO adinfo(id,name,pid) VALUES (?,?,?)", code, name, "0");
			successCount++;
			String data = FileUtils.readFileToString(file);
			String[] list = data.split("#");
			Pattern[] patterns = { cityPattern, countyPattern, townPattern, villagePattern };
			Integer[]counts = {new Integer(0),new Integer(0),new Integer(0),new Integer(0)};
			for (String item : list) {
				array = item.split(",");
				name = array[0];
				code = array[1];
				for (int i = 0; i < patterns.length; i++) {
					Pattern pattern = patterns[i];
					Matcher matcher = pattern.matcher(code);
					if (matcher.find()) {
						counts[i]++;
						String id = matcher.group(1);
						String pid = matcher.group(2);
						logger.debug("insert into adinfo(id,name,pid) values (?,?,?)");
						jdbcTemplate.update("insert into adinfo(id,name,pid) values (?,?,?)", id, name, pid);
						successCount++;
						break;
					}
					if(i==3){
						System.out.println(code);
					}
				}
			}
			dataImportResult.setEndTime(System.currentTimeMillis());
			dataImportResult.setSuccessCount(successCount);
			dataImportResult.setCityCount(counts[0]);
			dataImportResult.setCountyCount(counts[1]);
			dataImportResult.setTownCount(counts[2]);
			dataImportResult.setVillageCount(counts[3]);
			dataImportResult.setTotalCount(list.length+1);
			dataImportResult.setExeTime(dataImportResult.getEndTime() - dataImportResult.getStartTime());
			logger.info(file.getName()+"导入结束");
		} catch (Exception e) {
			dataImportResult.setError(1);
			dataImportResult.setErrorText(e.getMessage());
		}
		return dataImportResult;
	}
}
