package com.cjoop.ad.view;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.MatteBorder;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cjoop.ad.Constant;
import com.cjoop.ad.domain.ADInfo;
import com.cjoop.ad.util.CrawlerUtil;
import com.cjoop.ad.util.SpringUtil;

/**
 * 主界面,提供下载各个省数据,包括省,市,县,镇,乡
 * @author 陈均
 *
 */
@Component
public class IndexView extends JFrame {

	private static final long serialVersionUID = 3555577950459620172L;
	private JPanel contentPane;
	private JPanel toolPanel;
	
	List<ADInfo> provinceList = new ArrayList<ADInfo>();
	List<ProvinceItemView> provinceItemViews = new ArrayList<ProvinceItemView>();
	/**
	 * 消息提示
	 */
	private JLabel lblDownloadTip;
	@Autowired
	private SpringUtil springUtil;
	@Autowired
	private CrawlerUtil crawlerUtil;

	public IndexView() {
	}
	
	public void init(){
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("2014年统计用区划代码和城乡划分代码");
		contentPane = new JPanel();
		setContentPane(contentPane);
		setBounds(100, 100, 950, 300);
		contentPane.setLayout(null);
		toolPanel = new JPanel();
		toolPanel.setLayout(null);
		toolPanel.setBounds(10,10, 950,40);
		
		initAllSelect();
		initReversedSelect();
		initBtnDownload();
		initDownloadTip();
		add(toolPanel);
		parseProvinceInfo();
		initProvincePanel();
	}

	/**
	 * 初始化省级数据展示面板
	 */
	public void initProvincePanel(){
		JPanel bodyPanel = new JPanel();
		int rows = (provinceList.size() + 8 - 1) / 8;
		int len = rows * 8;
		bodyPanel.setBorder(new MatteBorder(1, 1, 1, 0, Color.LIGHT_GRAY));
		bodyPanel.setLayout(new GridLayout(rows, 8, 0, 0));
		bodyPanel.setBounds(10,50, 900, 48*rows);
		Color color = Color.WHITE;
		for (int i = 0; i < len; i++) {
			ProvinceItemView provinceItemView = springUtil.getBean(ProvinceItemView.class);
			if(i%8==0){
				color = (color == Constant.light_gray)?Color.WHITE:Constant.light_gray;
			}
			if(i<provinceList.size()){
				provinceItemView.setAdInfo(provinceList.get(i));
				provinceItemViews.add(provinceItemView);
			}
			provinceItemView.setBackground(color);
			bodyPanel.add(provinceItemView);
		}
		add(bodyPanel);
	}
	
	/**
	 * 初始化下载提示功能
	 */
	private void initDownloadTip() {
		lblDownloadTip = new JLabel();
		lblDownloadTip.setText("提示:选择省份进行数据抓取,绿色表示已下载!");
		lblDownloadTip.setForeground(Color.RED);
		lblDownloadTip.setBounds(270, 10, 600, 23);
		lblDownloadTip.setFont(Constant.font_song_12);
		toolPanel.add(lblDownloadTip);
	}

	/**
	 * 初始化下载按钮功能
	 */
	private void initBtnDownload() {
		JButton btnDownload = new JButton("抓取数据包");
		btnDownload.setFont(Constant.font_song_12);
		btnDownload.setBounds(130, 10, 120, 23);
		btnDownload.addActionListener(new DownloadHandler());
		toolPanel.add(btnDownload);
	}

	/**
	 * 初始化反选功能
	 */
	private void initReversedSelect() {
		JCheckBox checkBoxReversed = new JCheckBox("反选");
		checkBoxReversed.setFont(Constant.font_song_12);
		checkBoxReversed.setBounds(70, 10, 60, 20);
		checkBoxReversed.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				for (ProvinceItemView provinceItemView : provinceItemViews) {
					provinceItemView.checkBox.setSelected(!provinceItemView.checkBox.isSelected());
				}
			}
		});
		toolPanel.add(checkBoxReversed);
	}

	/**
	 * 初始化全选功能
	 */
	private void initAllSelect() {
		final JCheckBox checkBoxAll = new JCheckBox("全选");
		checkBoxAll.setFont(Constant.font_song_12);
		checkBoxAll.setBounds(0, 10, 60, 20);
		checkBoxAll.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				for (ProvinceItemView provinceItemView : provinceItemViews) {
					provinceItemView.checkBox.setSelected(checkBoxAll.isSelected());
				}
			}
		});
		toolPanel.add(checkBoxAll);
	}
	
	/**
	 * 下载监听器
	 * @author 陈均
	 *
	 */
	protected class DownloadHandler implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			for (ProvinceItemView provinceItemView : provinceItemViews) {
				if(provinceItemView.isCheck() && !provinceItemView.isDownload()){
					provinceItemView.download();
				}
			}
		}
		
	}
	
	/**
	 * 解析官网省级数据
	 */
	public void parseProvinceInfo(){
		String html = crawlerUtil.getHtml(Constant.homeUrl);
		Document doc = Jsoup.parse(html);
		Elements alist = doc.select("table.provincetable a");
		for (Element a : alist) {
			String href = a.attr("href");
			ADInfo adInfo = new ADInfo();
			adInfo.setName(a.text());
			adInfo.setUrl(href);
			adInfo.setCode(href.split("\\.")[0]);
			provinceList.add(adInfo);
		}
	}
	
}
