package com.cjoop.ad.domain;

/**
 * 数据库类型
 * @author 陈均
 *
 */
public enum DBType {

	ORACLE("ORACLE","Oracle (Thin driver)","oracle.jdbc.driver.OracleDriver","jdbc:oracle:thin:@<server>[:<1521>]:<database_name>"),
	MYSQL("MYSQL","MySQL Connector/J","com.mysql.jdbc.Driver","jdbc:mysql://<hostname>[<:3306>]/<dbname>?useUnicode=true&characterEncoding=utf8"),
	H2("H2","Generic H2","org.h2.Driver","jdbc:h2:[file:][<path>]<databaseName>");
	
	protected String url;
	protected String name;
	protected String driverClassName;
	protected String desc;
	
	private DBType(String name,String desc,String driverClassName,String url){
		this.name = name;
		this.desc = desc;
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
        return this.desc;
    }

	public String getName() {
		return name;
	}
}
