package com.cjoop.ad.domain;

/**
 * 数据库类型
 * @author 陈均
 *
 */
public enum DBType {

	ORACLE("Oracle (Thin driver)","oracle.jdbc.driver.OracleDriver","jdbc:oracle:thin:@<server>[:<1521>]:<database_name>"),
	MYSQL("MySQL Connector/J","com.mysql.jdbc.Driver","jdbc:mysql://<hostname>[<:3306>]/<dbname>?useUnicode=true&characterEncoding=utf8"),
	H2_EMBEDDED("Generic H2 (Embedded)","org.h2.Driver","jdbc:h2:[file:][<path>]<databaseName>"),
	H2_SERVER("Generic H2 (Server)","org.h2.Driver","jdbc:h2:[file:][<path>]<databaseName>");
	
	protected String url;
	protected String name;
	protected String driverClassName;
	
	private DBType(String name,String driverClassName,String url){
		this.name = name;
		this.url = url;
		this.driverClassName = driverClassName;
	}
	
	public String getUrl(){
		return url;
	}
	
	public String getDriverClassName(){
		return driverClassName;
	}
	
	 @Override
    public String toString() {
        return this.name;
    }
	
}
