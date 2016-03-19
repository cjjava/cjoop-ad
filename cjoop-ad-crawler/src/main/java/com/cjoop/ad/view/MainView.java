package com.cjoop.ad.view;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.border.EmptyBorder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cjoop.ad.Constant;

/**
 * 主界面
 * @author 陈均
 *
 */
@Component
public class MainView extends JFrame {
	
	private static final long serialVersionUID = -3366163508308472516L;
	private JPanel contentPane;
	private JTabbedPane tabbedPane;
	@Autowired
	private IndexView indexView;
	@Autowired
	private DataBaseView dataBaseView;

	public MainView() {
		
	}

	public void init(){
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("2014年统计用区划代码和城乡划分代码");
		setBounds(100, 100, 950, 400);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setFont(Constant.font_song_12);
		contentPane.add(tabbedPane, BorderLayout.CENTER);
		indexView.init();
		dataBaseView.init();
		tabbedPane.addTab("1.元素据提取",indexView);
		tabbedPane.addTab("2.元素据导入",dataBaseView);
		
	}
}
