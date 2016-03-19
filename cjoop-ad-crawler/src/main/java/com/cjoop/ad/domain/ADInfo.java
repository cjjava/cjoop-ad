package com.cjoop.ad.domain;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 行政区划信息
 * 
 * @author 陈均
 *
 */
@Entity
@Table(name = "adinfo")
public class ADInfo implements Serializable {

	private static final long serialVersionUID = 1L;
	/**
	 * 主键id
	 */
	@Id
	private String id;
	/**
	 * 名称
	 */
	protected String name;

	/**
	 * 父级id
	 */
	protected String pid;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

}
