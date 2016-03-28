package com.cjoop.ad.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * 行政区划rest服务
 * @author 陈均
 *
 */
@RestController
@RequestMapping("/ad")
public class ADInfoCtrl {

	@Autowired
	private ADInfoRepository adInfoRepository;
	
	/**
	 * 获取指定区域的子级区域数据<br/>
	 * 针对跨域访问可以进行细化,比如是否激活跨域访问,跨域访问的域名有哪些.
	 * @param id 行政区划代码
	 * @return 子级行政区划集合
	 */
	@CrossOrigin
	@RequestMapping("{id}/childs")
	public List<ADInfo> childs(@PathVariable("id") String id){
		List<ADInfo> childs = adInfoRepository.findByPidOrderById(id);
		return childs;
	}
	
	/**
	 * 获取指定行政区划信息
	 * @param id 行政区划代码
	 * @return 行政区划信息
	 */
	@CrossOrigin
	@RequestMapping("{id}")
	public ADInfo get(@PathVariable("id") String id){
		return adInfoRepository.findOne(id);
	}
	
}
