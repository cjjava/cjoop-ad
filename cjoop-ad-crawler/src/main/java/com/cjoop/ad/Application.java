package com.cjoop.ad;

import java.awt.EventQueue;
import java.io.File;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.config.RequestConfig.Builder;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.cjoop.ad.view.MainView;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 应用程序入口
 * @author 陈均
 *
 */
@SpringBootApplication
@ComponentScan(basePackages = {Constant.basePackage_cjoop})
@EntityScan(basePackages = {Constant.basePackage_cjoop})
@EnableJpaRepositories(basePackages = {Constant.basePackage_cjoop})
public class Application implements ApplicationContextAware,InitializingBean{
	protected ApplicationContext applicationContext ;
	
	@Bean
	public ExecutorService executorService(){
		return Executors.newFixedThreadPool(5);
	}
	
	@Bean
	public ObjectMapper objectMapper(){
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.setTimeZone(TimeZone.getDefault());
		objectMapper.setSerializationInclusion(Include.NON_NULL);
		return objectMapper;
	}
	
	/**
	 * md5校验信息
	 * @return PropertiesConfiguration
	 * @throws Exception
	 */
	@Bean
	public PropertiesConfiguration md5Config() throws Exception{
		String fileName = "md5.properties";
		File appPropFile = new File(fileName);
		if(!appPropFile.exists()){
			appPropFile.createNewFile();
		}
		return new PropertiesConfiguration(fileName);
	}
	
	/**
	 * db配置信息
	 * @return PropertiesConfiguration
	 * @throws Exception
	 */
	@Bean
	public PropertiesConfiguration dbConfig() throws Exception{
		String fileName = "db.properties";
		File appPropFile = new File(fileName);
		if(!appPropFile.exists()){
			appPropFile.createNewFile();
		}
		return new PropertiesConfiguration(fileName);
	}
	
	/**
	 * http request 配置信息
	 * @return RequestConfig
	 */
	@Bean
	public RequestConfig requestConfig(){
		Builder builder = RequestConfig.custom().setSocketTimeout(2000)
				.setConnectTimeout(2000);
		return builder.build();
	}
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
	
	public static void main(String[] args) throws Exception {
		System.setProperty("java.awt.headless", "false");
		SpringApplication.run(Application.class, args);
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainView mainView = applicationContext.getBean(MainView.class);
					mainView.init();
					mainView.setLocationRelativeTo(null);
					mainView.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	
}
