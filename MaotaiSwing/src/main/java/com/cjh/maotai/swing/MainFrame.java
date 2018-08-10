package com.cjh.maotai.swing;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.border.LineBorder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.util.StringUtils;

import com.cjh.maotai.swing.beans.ReturnResultBean;
import com.cjh.maotai.swing.beans.spring.SpringContextUtils;
import com.cjh.maotai.swing.service.MaotaiService;
import com.cjh.maotai.swing.session.MaotaiSession;

import java.awt.Color;
import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import javax.swing.JButton;
import javax.swing.JTextPane;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.UIManager;

@SpringBootApplication
public class MainFrame extends JFrame {

	private JPanel contentPane;
	private JTextField userNameField;
	private JPasswordField passwordField;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		SpringApplication.run(MainFrame.class, args);
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainFrame frame = new MainFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public MainFrame() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1200, 600);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JPanel panel = new JPanel();
		panel.setBorder(new LineBorder(new Color(0, 0, 0)));
		panel.setBounds(10, 10, 205, 154);
		contentPane.add(panel);
		panel.setLayout(null);

		JPanel panel_1 = new JPanel();
		panel_1.setBounds(10, 40, 185, 59);
		panel.add(panel_1);

		JLabel lblNewLabel = new JLabel("登录信息");
		lblNewLabel.setFont(new Font("宋体", Font.PLAIN, 14));
		lblNewLabel.setBounds(10, 10, 68, 26);
		panel.add(lblNewLabel);
		panel_1.setLayout(null);

		JLabel lblNewLabel_1 = new JLabel("用户名");
		lblNewLabel_1.setBounds(0, 8, 46, 15);
		panel_1.add(lblNewLabel_1);

		userNameField = new JTextField();
		userNameField.setBounds(56, 5, 119, 21);
		panel_1.add(userNameField);
		userNameField.setColumns(10);

		passwordField = new JPasswordField();
		passwordField.setBounds(56, 30, 119, 21);
		panel_1.add(passwordField);

		JLabel label = new JLabel("密码");
		label.setBounds(0, 33, 54, 15);
		panel_1.add(label);

		JTextPane textPane = new JTextPane();
		textPane.setBounds(10, 40, 185, 59);
		panel.add(textPane);
		textPane.setVisible(false);
		textPane.setEditable(false);
		textPane.setText("");
		textPane.setBackground(UIManager.getColor("Button.background"));

		JButton btnNewButton = new JButton("确认登录");
		btnNewButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				MaotaiService maotaiService = (MaotaiService) SpringContextUtils.getContext().getBean("maotaiServiceImpl");
				if (!MaotaiSession.isLogined()) {
					if (StringUtils.isEmpty(userNameField.getText())) {
						JOptionPane.showMessageDialog(panel, "用户名不能为空");
					} else if (passwordField.getPassword().length <= 0) {
						JOptionPane.showMessageDialog(panel, "密码不能为空");
					} else {
						ReturnResultBean resultBean = maotaiService.login(userNameField.getText(),
								new String(passwordField.getPassword()));
						if (resultBean.getResultCode() != 0) {
							JOptionPane.showMessageDialog(panel, resultBean.getReturnMsg());
						} else {
							panel_1.setVisible(false);
							textPane.setVisible(true);
							textPane.setText("您已成功登录，用户名为: " + userNameField.getText());
							btnNewButton.setText("退出登录");
						}
					}
				} else {
					ReturnResultBean resultBean = maotaiService.logout(userNameField.getText());
					panel_1.setVisible(true);
					textPane.setVisible(false);
					JOptionPane.showMessageDialog(panel, resultBean.getReturnMsg());
					btnNewButton.setText("确认登录");
				}
			}
		});
		btnNewButton.setBounds(64, 109, 101, 23);
		panel.add(btnNewButton);
	}
}
