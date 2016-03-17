package com.cjoop.ad.domain;

/**
 * 数据库类型
 * @author 陈均
 *
 */
public enum DBType {

	ORACLE("Oracle (Thin driver)","jdbc:oracle:thin:@<server>[:<1521>]:<database_name>"),
	MYSQL("MySQL Connector/J","jdbc:mysql://<hostname>[<:3306>]/<dbname>?useUnicode=true&characterEncoding=utf8"),
	H2_EMBEDDED("Generic H2 (Embedded)","jdbc:h2:[file:][<path>]<databaseName>"),
	H2_SERVER("Generic H2 (Server)","jdbc:h2:[file:][<path>]<databaseName>");
	
	protected String url;
	protected String name;
	
	private DBType(String name,String url){
		this.name = name;
		this.url = url;
	}
	
	 @Override
    public String toString() {
        return this.name;
    }
	
}
