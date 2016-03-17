package com.cjoop.ad.view;

import javax.swing.JPanel;

import org.springframework.stereotype.Component;

import com.cjoop.ad.domain.DBType;

import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.JPasswordField;

/**
 * 数据库视图
 * @author 陈均
 *
 */
@Component
public class DataBaseView extends JPanel {

	private static final long serialVersionUID = 8464498036587247805L;
	private JTextField textField;
	private JTextField textField_1;
	private JPasswordField passwordField;
	private JComboBox<DBType> cboxType;
	
	public DataBaseView() {
		setLayout(null);
		
		JLabel lblNewLabel = new JLabel("类型：");
		lblNewLabel.setFont(new Font("宋体", Font.PLAIN, 12));
		lblNewLabel.setBounds(126, 62, 36, 15);
		add(lblNewLabel);
		
		cboxType = new JComboBox<DBType>();
		cboxType.setFont(new Font("宋体", Font.PLAIN, 12));
		cboxType.setBounds(172, 59, 218, 21);
		cboxType.addItem(DBType.ORACLE);
		cboxType.addItem(DBType.MYSQL);
		cboxType.addItem(DBType.H2_EMBEDDED);
		cboxType.addItem(DBType.H2_SERVER);
		
		add(cboxType);
		
		JLabel label = new JLabel("连接地址：");
		label.setFont(new Font("宋体", Font.PLAIN, 12));
		label.setBounds(102, 93, 60, 15);
		add(label);
		
		textField = new JTextField();
		textField.setBounds(172, 90, 218, 21);
		add(textField);
		textField.setColumns(10);
		
		JLabel label_1 = new JLabel("用户名：");
		label_1.setFont(new Font("宋体", Font.PLAIN, 12));
		label_1.setBounds(114, 124, 50, 15);
		add(label_1);
		
		textField_1 = new JTextField();
		textField_1.setBounds(172, 121, 218, 21);
		add(textField_1);
		textField_1.setColumns(10);
		
		JLabel label_2 = new JLabel("密码：");
		label_2.setFont(new Font("宋体", Font.PLAIN, 12));
		label_2.setBounds(126, 155, 36, 15);
		add(label_2);
		
		passwordField = new JPasswordField();
		passwordField.setBounds(172, 152, 218, 21);
		add(passwordField);
		
	}

}
