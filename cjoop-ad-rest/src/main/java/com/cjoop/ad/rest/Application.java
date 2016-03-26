package com.cjoop.ad.rest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * 应用程序入口
 * @author 陈均
 *
 */
@SpringBootApplication
@ComponentScan(basePackages={Constant.basePackage_cjoop})
@EntityScan(basePackages = {Constant.basePackage_cjoop})
@EnableJpaRepositories(basePackages={Constant.basePackage_cjoop})
public class Application {

	public static void main(String[] args) throws Exception {
		SpringApplication.run(Application.class, args);
	}
}
