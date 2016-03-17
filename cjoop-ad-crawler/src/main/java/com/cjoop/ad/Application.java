package com.cjoop.ad;

import java.awt.EventQueue;
import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.config.RequestConfig.Builder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.cjoop.ad.view.MainView;

/**
 * 应用程序入口
 * @author 陈均
 *
 */
@Configuration
@ComponentScan
public class Application{
	
	private static ApplicationContext applicationContext ;
	
	@Bean
	public ExecutorService executorService(){
		return Executors.newFixedThreadPool(5);
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
	 * http request 配置信息
	 * @return RequestConfig
	 */
	@Bean
	public RequestConfig requestConfig(){
		Builder builder = RequestConfig.custom().setSocketTimeout(2000)
				.setConnectTimeout(2000);
		return builder.build();
	}
	
	public static void main(String[] args) throws Exception {
		applicationContext = new AnnotationConfigApplicationContext(Application.class);
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
