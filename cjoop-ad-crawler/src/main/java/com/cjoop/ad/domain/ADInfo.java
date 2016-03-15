package com.cjoop.ad.domain;

/**
 * 行政区划信息
 * 
 * @author 陈均
 *
 */
public class ADInfo {

	/**
	 * 名称
	 */
	protected String name;
	/**
	 * 详细地址
	 */
	protected String url;
	/**
	 * 代码
	 */
	protected String code;

	public ADInfo(){}
	
	public ADInfo(String name, String code) {
		this.name = name;
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	@Override
	public String toString() {
		return name + "," + code;
	}
}
