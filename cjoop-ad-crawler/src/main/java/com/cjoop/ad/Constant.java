package com.cjoop.ad;

import java.awt.Color;
import java.awt.Font;

/**
 * 整个程序用到的常量信息
 * @author 陈均
 *
 */
public interface Constant {
	/**
	 * 12号宋体
	 */
	Font font_song_12 = new Font("宋体", Font.PLAIN, 12);
	/**
	 * 14号宋体
	 */
	Font font_song_14 = new Font("宋体", Font.PLAIN, 14);
	/**
	 * 深灰
	 */
	Color gainsboro  = new Color(Integer.parseInt("DCDCDC", 16));
	/**
	 * 深绿
	 */
	Color dark_green  = new Color(Integer.parseInt("228B22", 16));
	/**
	 * 浅灰色
	 */
	Color light_gray  = new Color(Integer.parseInt("f5f5f5", 16));
	///////////////////////////////http请求头常量信息////////////////////////////////
	String UTF_8 = "UTF-8";
	String content_length = "Content-Length";
	String accept_json = "application/json, text/javascript, */*; q=0.01";
	String x_requested_with = "X-Requested-With";
	String xmlHttpRequest = "XMLHttpRequest";
	String host = "Host";
	String user_agent_chrome = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/47.0.2526.106 Safari/537.36";
	String content_type_form_utf8 = "application/x-www-form-urlencoded; charset=UTF-8"; 
	////////////////////////////行政区划地址信息/////////////////////////////////
	String rootUrl = "http://www.stats.gov.cn/tjsj/tjbz/tjyqhdmhcxhfdm/2014/";
	String homeUrl = rootUrl + "index.html";
	////////////////////////////db//////////////////////////////////
	String driverClassName = "driverClassName";
	String url = "url";
	String username = "username";
	String dbtype = "type";
	
}
