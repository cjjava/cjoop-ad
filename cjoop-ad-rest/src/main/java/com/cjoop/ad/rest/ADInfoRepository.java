package com.cjoop.ad.rest;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 行政区划数据接口
 * @author 陈均
 *
 */
public interface ADInfoRepository extends JpaRepository<ADInfo, String>{
	
	/**
	 * 根据父级行政区划获取子级行政区划数据集合
	 * @param id 父级ID
	 * @return 子级数据集合
	 */
	List<ADInfo> findByPidOrderById(String id);

}
