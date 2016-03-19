package com.cjoop.ad.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.MatteBorder;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.cjoop.ad.Constant;
import com.cjoop.ad.util.CrawlerUtil;

/**
 * 省级数据项组件
 * 
 * @author 陈均
 *
 */
@Component
@Scope("prototype")
public class ProvinceItemView extends JPanel{

	private static final long serialVersionUID = 2880219552219000482L;
	protected Log logger = LogFactory.getLog(getClass());
	/**
	 * 省级数据信息
	 */
	protected ADInfoVO provinceInfo;
	/**
	 * 是否正在下载
	 */
	protected boolean download = false;
	/**
	 * 勾选组件
	 */
	JCheckBox checkBox = new JCheckBox("");
	/**
	 * 省级名称
	 */
	JLabel lblTitle = new JLabel("");
	/**
	 * 背景颜色
	 */
	Color oldBg;
	
	@Autowired
	private CrawlerUtil crawlerUtil;
	/**
	 * md5校验信息,目的是防止重复下载
	 */
	@Autowired
	private PropertiesConfiguration md5Config;
	@Autowired
	private ExecutorService executorService;
	
	/**
	 * 市级数据缓存
	 */
	private List<ADInfoVO> cityList = new LinkedList<ADInfoVO>();
	/**
	 * 县级数据缓存
	 */
	private List<ADInfoVO> countyList = new LinkedList<ADInfoVO>();
	/**
	 * 镇级数据缓存
	 */
	private List<ADInfoVO> townList = new LinkedList<ADInfoVO>();
	/**
	 * 乡级数据缓存
	 */
	private List<ADInfoVO> villageList = new LinkedList<ADInfoVO>();
	/**
	 * 数据包存放文件
	 */
	private File provinceFile;
	
	public ProvinceItemView() {
		this(null);
	}

	public ProvinceItemView(ADInfoVO adInfo) {
		setLayout(null);
		initComponent();
		setAdInfo(adInfo);
		setBorder(new MatteBorder(0, 0, 0, 1, Color.LIGHT_GRAY));
		setMinimumSize(new Dimension(120, 50));
		initListener();
	}
	
	/**
	 * 初始化组件
	 */
	protected void initComponent(){
		lblTitle.setFont(new Font("宋体", Font.PLAIN, 12));
		lblTitle.setBounds(20, 12, 295, 15);
		checkBox.setEnabled(false);
		checkBox.setBounds(0, 10, 20, 20);
		checkBox.setVisible(false);
		add(lblTitle);
		add(checkBox);
	}
	
	/**
	 * 初始化监听
	 */
	protected void initListener(){
		checkBox.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				checkBox.setSelected(!checkBox.isSelected());
			}
		});
		
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				setBackground(Constant.gainsboro);
			}

			@Override
			public void mouseExited(MouseEvent e) {
				setBackground(oldBg);
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				checkBox.setSelected(!checkBox.isSelected());
			}
			
		});
	}
	
	/**
	 * 设置组件背景
	 */
	@Override
	public void setBackground(Color bg) {
		oldBg = getBackground();
		super.setBackground(bg);
		if(checkBox!=null){
			checkBox.setBackground(bg);
		}
	}

	/**
	 * 设置省级数据信息,比对md5值
	 * @param adInfo 升级数据信息
	 */
	public void setAdInfo(ADInfoVO adInfo) {
		this.provinceInfo = adInfo;
		if (adInfo != null) {
			lblTitle.setText(adInfo.getName());
			checkBox.setVisible(true);
			String filename= provinceInfo.getName() + "_" + provinceInfo.getCode() + ".txt";
			File provinceDir = new File(System.getProperty("user.dir"),"province");
			provinceDir.mkdirs();
			provinceFile = new File(provinceDir,filename);
			if(verifyMD5()){
				checkBox.setSelected(true);
				lblTitle.setForeground(Constant.dark_green);
			}
		}
	}
	
	/**
	 * 验证文件的MD5
	 * @return boolean
	 */
	public boolean verifyMD5(){
		try {
			if(provinceFile.exists()){
				String md5 = DigestUtils.md5Hex(FileUtils.readFileToByteArray(provinceFile));
				String md5Val = md5Config.getString(provinceInfo.getCode());
				return md5.equals(md5Val);
			}
		} catch (IOException e) {
			logger.warn(e.getMessage());
		}
		return false;
	}

	/**
	 * 是否正在下载
	 * @return true正在下载,false未在下载
	 */
	public boolean isDownload() {
		return download;
	}

	/**
	 * 下载数据
	 */
	public synchronized void download() {
		download = true;
		cityList = new LinkedList<ADInfoVO>();
		countyList = new LinkedList<ADInfoVO>();
		townList = new LinkedList<ADInfoVO>();
		villageList = new LinkedList<ADInfoVO>();
		if(!verifyMD5()){
			DownloadThread downloadThread = new DownloadThread();
			executorService.execute(downloadThread);
		}
	}
	
	/**
	 * 下载数据线程
	 * @author 陈均
	 *
	 */
	class DownloadThread implements Runnable{

		@Override
		public void run() {
			logger.info("------------正在抓取数据中,耐心等待...------------");
			long start = System.currentTimeMillis();
			parseCityWebSite();
			writeFile();
			long end = System.currentTimeMillis();
			logger.info("------------下载省级数据:" + provinceInfo.getName() + "完成("+(end-start)/1000+"秒)------------");
			download = false;
		}
		
	}
	
	/**
	 * 把元数据写入到文件中去
	 */
	private void writeFile() {
		provinceFile.delete();
		try{
			FileUtils.writeLines(provinceFile, cityList, "#",true);
			FileUtils.writeLines(provinceFile, countyList, "#",true);
			FileUtils.writeLines(provinceFile, townList, "#",true);
			FileUtils.writeLines(provinceFile, villageList, "#",true);
			String md5 = DigestUtils.md5Hex(FileUtils.readFileToByteArray(provinceFile));
			md5Config.setProperty(provinceInfo.getCode(), md5);
			try {
				md5Config.save();
				lblTitle.setForeground(Constant.dark_green);
			} catch (ConfigurationException ce) {
				logger.error("保存md5信息失败",ce);
			}
		} catch (IOException e) {
			logger.error("元素据保存文件失败:",e);
			provinceFile.delete();
		}
	}

	/**
	 * 解析dom元素
	 * @param doc 文档对象
	 * @param cssQuery 路径
	 * @return 解析后的数据集合
	 */
	protected List<ADInfoVO> parseDoc(Document doc,String cssQuery){
		List<ADInfoVO> list = new ArrayList<ADInfoVO>();
		Elements trlist = doc.select(cssQuery);
		for (Element tr : trlist) {
			Elements a = tr.select("a");
			ADInfoVO adinfo = new ADInfoVO();
			if(a.size()==2){
				Element a_code = a.first();
				Element a_name = a.last();
				String href = a_name.attr("href");
				adinfo.setName(a_name.text());
				adinfo.setUrl(href);
				adinfo.setCode(a_code.text());
				list.add(adinfo);
			}else{
				Elements td = tr.select("td");
				if(td.size()==2){
					Element td_code = td.first();
					Element td_name = td.last();
					adinfo.setName(td_name.text());
					adinfo.setCode(td_code.text());
					list.add(adinfo);
				}
			}
		}
		return list;
	}
	
	/**
	 * 解析市级数据信息
	 */
	protected void parseCityWebSite(){
		String html = crawlerUtil.getHtml(Constant.rootUrl  +  provinceInfo.getUrl());
		Document doc = Jsoup.parse(html);
		List<ADInfoVO> cityArray = parseDoc(doc, "table.citytable .citytr");
		cityList.addAll(cityArray);
		for (ADInfoVO city : cityArray) {
			parseCountyWebSite(city.getUrl());
		}
	}
	
	/**
	 * 解析县级数据信息
	 * @param cityUrl 市级访问地址
	 */
	public void parseCountyWebSite(String cityUrl){
		String html = crawlerUtil.getHtml(Constant.rootUrl + cityUrl);
		Document doc = Jsoup.parse(html);
		List<ADInfoVO> countyArray = parseDoc(doc, "table.countytable .countytr");
		countyList.addAll(countyArray);
		for (ADInfoVO county : countyArray) {
			String url = county.getUrl();
			if(StringUtils.isNotBlank(url)){
				parseTownWebSite(url);
			}
		}
	}

	
	/**
	 * 解析镇数据信息
	 * @param countyUrl 县级访问地址
	 */
	public void parseTownWebSite(String countyUrl){
		String html = crawlerUtil.getHtml(Constant.rootUrl + "/" + provinceInfo.getCode() + "/" + countyUrl);
		Document doc = Jsoup.parse(html);
		String cityCode = countyUrl.split("/")[0];
		List<ADInfoVO> townArray = parseDoc(doc, "table.towntable .towntr");
		townList.addAll(townArray);
		for (ADInfoVO town : townArray) {
			parseVillageWebSite(cityCode, town.getUrl());
		}
	}
	
	/**
	 * 解析乡数据信息
	 * @param cityCode 市级代码
	 * @param townUrl 镇级数据地址
	 */
	public void parseVillageWebSite(String cityCode,String townUrl){
		String html =crawlerUtil.getHtml(Constant.rootUrl + "/" + provinceInfo.getCode() + "/" + cityCode + "/" + townUrl);
		Document doc = Jsoup.parse(html);
		Elements trlist = doc.select("table.villagetable .villagetr");
		for (Element tr : trlist) {
			Elements td = tr.select("td");
			if(td.size()==3){
				Element td_code = td.first();
				Element td_name = td.last();
				ADInfoVO village = new ADInfoVO();
				village.setName(td_name.text());
				village.setCode(td_code.text());
				villageList.add(village);
			}
		}
	}
	
	/**
	 * 是否勾上
	 * @return true勾上,false未勾上
	 */
	public boolean isCheck() {
		return checkBox.isSelected();
	}
	
	
}
