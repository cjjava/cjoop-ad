package com.cjoop.ad.util;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 爬虫工具类，只用于该项目使用
 * @author 陈均
 *
 */
@Component
public class CrawlerUtil {
	protected Log logger = LogFactory.getLog(getClass());
	
	HttpClient httpClient = HttpClients.createDefault();
	@Autowired
	RequestConfig requestConfig;
	
	
	/**
	 * 根据路径获取网页内容,如果失败了会重新访问,直到成功为止。
	 * @param url 抓取的网页地址
	 * @return html 返回html内容
	 */
	public String getHtml(String url){
		HttpEntity httpEntity = null;
		try {
			HttpGet httpGet = new HttpGet(url);
			httpGet.setConfig(requestConfig);
			HttpResponse httpResponse = httpClient.execute(httpGet);
			httpEntity = httpResponse.getEntity();
			return EntityUtils.toString(httpEntity,"gb2312");
		} catch (Exception e) {
			logger.warn("解析地址失败:" + e.getMessage());
			return getHtml(url);
		}finally {
			if(httpEntity!=null){
				try {
					EntityUtils.consume(httpEntity);
				} catch (IOException e) {
					logger.warn(e.getMessage());
				}
			}
		}
	}
	
}
