package com.cjh.maotai.swing;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.border.LineBorder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.util.StringUtils;

import com.cjh.maotai.swing.config.ProxyConfig;
import com.cjh.maotai.swing.beans.MaotaiSkuBean;
import com.cjh.maotai.swing.beans.ReturnResultBean;
import com.cjh.maotai.swing.beans.ViewMsgBean;
import com.cjh.maotai.swing.beans.spring.SpringContextUtils;
import com.cjh.maotai.swing.service.MaotaiService;
import com.cjh.maotai.swing.session.MaotaiSession;
import com.cjh.maotai.swing.task.OrderTask;
import com.cjh.maotai.swing.task.ViewTask;
import com.cjh.maotai.swing.utils.CheckUtil;
import com.cjh.maotai.swing.utils.MaotaiUrlParseUtil;
import com.cjh.maotai.swing.utils.QCodeUtil;
import com.cjh.maotai.swing.utils.SSLTrustUtil;
import com.google.common.collect.Lists;
import com.google.zxing.WriterException;

import java.awt.Color;
import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import javax.swing.JButton;
import javax.swing.JTextPane;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.LinkedBlockingQueue;

import javax.swing.UIManager;
import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.Panel;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableModel;
import javax.swing.border.TitledBorder;
import javax.swing.border.EtchedBorder;

@SpringBootApplication
public class MainFrame extends JFrame {

	private JPanel contentPane;
	private JTextField userNameField;
	private JPasswordField passwordField;
	private JTextField textField;
	private boolean isRunning = false;
	private JTable table;
	private List<Thread> taskThreadList = Lists.newArrayList();
	private Thread viewThread = null;
	public static LinkedBlockingQueue<ViewMsgBean> msgQueue = new LinkedBlockingQueue<ViewMsgBean>();
	private DefaultTableModel dtm = null;
	private Thread defaultTaskThread = null;
	private JTextArea authCode;
	private JTextArea proxyPlan;
	private JCheckBox chckbxNewCheckBox_1;
	private int clickProxyAreaCount = 0;
	private JFormattedTextField lunxuTime;
	private JLabel erweiQcode;

	/**
	 * Launch the application.
	 * 
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
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
		contentPane.setForeground(Color.DARK_GRAY);
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
				if (!btnNewButton.isEnabled()) {
					return;
				}
				MaotaiService maotaiService = (MaotaiService) SpringContextUtils.getContext()
						.getBean("maotaiServiceImpl");
				if (!MaotaiSession.isLogined()) {
					if (StringUtils.isEmpty(userNameField.getText())) {
						JOptionPane.showMessageDialog(panel, "用户名不能为空");
					} else if (passwordField.getPassword().length <= 0) {
						JOptionPane.showMessageDialog(panel, "密码不能为空");
					} else {
						ReturnResultBean resultBean = maotaiService.login(userNameField.getText(),
								new String(passwordField.getPassword()), "");
						if (resultBean.getResultCode() != 0) {
							JOptionPane.showMessageDialog(panel, resultBean.getReturnMsg());
						} else {
							panel_1.setVisible(false);
							textPane.setVisible(true);
							textPane.setText("您已成功登录，用户名为：" + userNameField.getText() + "， " + "收货地址为："
									+ MaotaiSession.getAddress());
							authCode.setText("auth=" + MaotaiSession.getValidAuth(userNameField.getText()));
							btnNewButton.setText("退出登录");
							textField.requestFocus();
						}
					}
				} else {
					ReturnResultBean resultBean = maotaiService.logout(userNameField.getText());
					panel_1.setVisible(true);
					textPane.setVisible(false);
					JOptionPane.showMessageDialog(panel, resultBean.getReturnMsg());
					btnNewButton.setText("确认登录");
					authCode.setText("");
				}
			}
		});
		btnNewButton.setBounds(64, 109, 101, 23);
		panel.add(btnNewButton);

		JPanel panel_2 = new JPanel();
		panel_2.setBorder(new LineBorder(new Color(0, 0, 0)));
		panel_2.setBounds(259, 10, 909, 154);
		contentPane.add(panel_2);
		panel_2.setLayout(null);

		JLabel lblNewLabel_5 = new JLabel("");
		lblNewLabel_5.setBounds(646, 105, 110, 18);
		panel_2.add(lblNewLabel_5);

		JLabel lblNewLabel_2 = new JLabel("购买商品的url地址");
		lblNewLabel_2.setBounds(14, 16, 201, 18);
		panel_2.add(lblNewLabel_2);

		textField = new JTextField();
		textField.setText(
				"https://www.emaotai.cn/smartsales-b2c-web-pc/details/1180731799924468740-1173773178264259584.html?skuId=1180731799931808771");
		textField.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				String url = textField.getText();
				ReturnResultBean resultBean = MaotaiUrlParseUtil.parseSkuUrl(url);
				if (resultBean.getResultCode() != 0) {
					JOptionPane.showMessageDialog(panel_2, resultBean.getReturnMsg());
					textField.requestFocus();
				}
				MaotaiService maotaiService = (MaotaiService) SpringContextUtils.getContext()
						.getBean("maotaiServiceImpl");
				try {
					ReturnResultBean priceResultBean = maotaiService.getPrice(url, userNameField.getText());
					if (priceResultBean.getResultCode() == 0) {
						lblNewLabel_5.setText("销售价：" + ((Double) priceResultBean.getReturnObj()));
					}
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		textField.setBounds(229, 13, 524, 24);
		panel_2.add(textField);
		textField.setColumns(10);

		JLabel lblNewLabel_3 = new JLabel("购买的商品是否是预购模式");
		lblNewLabel_3.setBounds(14, 59, 201, 18);
		panel_2.add(lblNewLabel_3);

		JCheckBox chckbxNewCheckBox = new JCheckBox("是");
		chckbxNewCheckBox.setSelected(true);
		chckbxNewCheckBox.setBounds(225, 55, 63, 27);
		panel_2.add(chckbxNewCheckBox);

		NumberFormat nf = NumberFormat.getIntegerInstance();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		// -------------
		JLabel lblNewLabel_4 = new JLabel("任务执行时间（格式按样例）");
		lblNewLabel_4.setBounds(14, 105, 201, 18);
		panel_2.add(lblNewLabel_4);

		Date now = new Date();
		String strBeginTime = new SimpleDateFormat("yyyy-MM-dd").format(now) + " 15:00:00";
		Date beginTime = null;
		try {
			beginTime = df.parse(strBeginTime);
		} catch (ParseException e3) {
			// TODO Auto-generated catch block
			e3.printStackTrace();
		}
		Date endTime = new Date(beginTime.getTime() + 1000 * 60 * 5L);

		JFormattedTextField formattedTextField = new JFormattedTextField(df);
		formattedTextField.setBounds(229, 99, 173, 24);
		formattedTextField.setValue(beginTime);
		panel_2.add(formattedTextField);

		JLabel label_1 = new JLabel("到");
		label_1.setBounds(416, 105, 15, 18);
		panel_2.add(label_1);

		JFormattedTextField formattedTextField_1 = new JFormattedTextField(df);
		formattedTextField_1.setBounds(445, 99, 173, 24);
		formattedTextField_1.setValue(endTime);
		panel_2.add(formattedTextField_1);
		// -------------

		// -------------
		JLabel label_2 = new JLabel("购买个数");
		label_2.setBounds(447, 59, 56, 18);
		panel_2.add(label_2);

		JFormattedTextField formattedTextField_4 = new JFormattedTextField(nf);
		formattedTextField_4.setBounds(503, 57, 32, 24);
		formattedTextField_4.setValue(1);
		panel_2.add(formattedTextField_4);
		// -------------

		// -------------
		JLabel label_3 = new JLabel("并发执行任务个数");
		label_3.setBounds(560, 59, 128, 18);
		panel_2.add(label_3);

		JFormattedTextField bingfaNum = new JFormattedTextField(nf);
		bingfaNum.setEditable(false);
		bingfaNum.setBounds(697, 56, 56, 24);
		bingfaNum.setValue(1);
		panel_2.add(bingfaNum);
		// -------------

		JButton btnNewButton_1 = new JButton("开始执行");
		btnNewButton_1.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (isRunning) {
					OrderTask.getTaskFinishFlag().set(true);
					if (defaultTaskThread != null) {
						try {
							defaultTaskThread.join();
						} catch (InterruptedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
					for (Thread taskThread : taskThreadList) {
						try {
							taskThread.join();
						} catch (InterruptedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
					if (viewThread != null) {
						try {
							viewThread.join();
						} catch (InterruptedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
					while (true) {
						try {
							msgQueue.remove();
						} catch (NoSuchElementException e2) {
							break;
						}
					}
					isRunning = false;
					btnNewButton_1.setText("开始执行");
					textField.setEditable(true);
					formattedTextField.setEditable(true);
					formattedTextField_1.setEditable(true);
					formattedTextField_4.setEditable(true);
					chckbxNewCheckBox.setEnabled(true);
					btnNewButton.setEnabled(true);
					proxyPlan.setEditable(true);
					chckbxNewCheckBox_1.setEnabled(true);
					taskThreadList.clear();
					int rowCount = table.getRowCount();
					while (rowCount > 0) {
						dtm.removeRow(0);
						rowCount--;
					}
					table.validate();
				} else {
					String areaAuth = authCode.getText();
					OrderTask.sleepTime = Long
							.valueOf(StringUtils.isEmpty(lunxuTime.getText()) ? "1" : lunxuTime.getText()) * 1000;
					if (MaotaiSession.isLogined() || !StringUtils.isEmpty(areaAuth)) {
						if (chckbxNewCheckBox_1.isSelected()) {
							if (StringUtils.isEmpty(proxyPlan.getText())) {
								JOptionPane.showMessageDialog(panel_2, "代理服务器地址不能为空");
								return;
							}
							String[] addresses = proxyPlan.getText().split(",");
							if (addresses.length < 0) {
								JOptionPane.showMessageDialog(panel_2, "代理服务器地址配置不正确，注意：多个地址以逗号分割");
								return;
							}
							List<String> address = Lists.newArrayList(addresses);
							for (String adrs : address) {
								if (!CheckUtil.checkProxy(adrs)) {
									JOptionPane.showMessageDialog(panel_2, "代理服务器" + adrs + "不可用，注意：多个地址以逗号分割");
									return;
								}
							}
							ProxyConfig.getInstance().setAddress(address);
							ProxyConfig.getInstance().setUseFlag(true);
						} else {
							ProxyConfig.getInstance().setUseFlag(false);
						}
						OrderTask.getTaskFinishFlag().set(false);
						Date beginTime = (Date) formattedTextField.getValue();
						Date endTime = (Date) formattedTextField_1.getValue();
						String auth = "";
						if (MaotaiSession.isLogined()) {
							auth = MaotaiSession.getValidAuth(userNameField.getText());
						} else if (!StringUtils.isEmpty(areaAuth)) {
							auth = areaAuth;
							MaotaiService maotaiService = (MaotaiService) SpringContextUtils.getContext()
									.getBean("maotaiServiceImpl");
							maotaiService.login("", "", auth);
						}
						String num = formattedTextField_4.getText();
						String purchaseWay = chckbxNewCheckBox.isSelected() ? "1" : "0";
						String url = textField.getText();
						ReturnResultBean resultBean = MaotaiUrlParseUtil.parseSkuUrl(url);
						if (resultBean.getResultCode() != 0) {
							JOptionPane.showMessageDialog(panel_2, resultBean.getReturnMsg());
							textField.requestFocus();
							return;
						}
						MaotaiSkuBean skuBean = (MaotaiSkuBean) (resultBean.getReturnObj());
						MaotaiService maotaiService = (MaotaiService) SpringContextUtils.getContext()
								.getBean("maotaiServiceImpl");
						ReturnResultBean priceResultBean = maotaiService.getPrice(url, userNameField.getText());
						if (priceResultBean.getResultCode() == 0) {
							DecimalFormat df = new DecimalFormat("#.00");
							skuBean.setSellPrice(df.format(((Double) priceResultBean.getReturnObj())));
						}
						// OrderTask defaultTask = new OrderTask(new
						// Date(beginTime.getTime() - 100 * 1), endTime, auth,
						// skuBean, num, purchaseWay, "0");
						// defaultTaskThread = new Thread(defaultTask,
						// "系统默认任务-0");
						// String row1[] = { "系统默认任务-0", "", "" };
						// dtm.addRow(row1);
						// taskThreadList.add(defaultTaskThread);
						// defaultTaskThread.start();
						for (int i = 1; i < Integer.valueOf(bingfaNum.getText()) + 1; i++) {
							String row[] = { "用户工作任务-" + i, "", "" };
							dtm.addRow(row);
							OrderTask task = new OrderTask(new Date(beginTime.getTime() + 100 * (i - 1)), endTime, auth,
									skuBean, num, purchaseWay, i + "");
							Thread taskThread = new Thread(task, "用户工作任务-" + i);
							taskThreadList.add(taskThread);
							taskThread.start();
						}
						ViewTask viewTask = new ViewTask(table);
						viewThread = new Thread(viewTask, "下单结果显示任务");
						viewThread.start();
						isRunning = true;
						btnNewButton_1.setText("停止");
						textField.setEditable(false);
						formattedTextField.setEditable(false);
						formattedTextField_1.setEditable(false);
						bingfaNum.setEditable(false);
						formattedTextField_4.setEditable(false);
						chckbxNewCheckBox.setEnabled(false);
						btnNewButton.setEnabled(false);
						proxyPlan.setEditable(false);
						chckbxNewCheckBox_1.setEnabled(false);
					} else {
						JOptionPane.showMessageDialog(panel_2, "请登录");
					}
				}
			}
		});
		btnNewButton_1.setBounds(782, 12, 113, 111);
		panel_2.add(btnNewButton_1);

		JLabel lblNewLabel_6 = new JLabel("轮询时间");
		lblNewLabel_6.setBounds(304, 61, 56, 15);
		panel_2.add(lblNewLabel_6);

		lunxuTime = new JFormattedTextField();
		lunxuTime.setBounds(370, 58, 32, 21);
		lunxuTime.setText("10");
		panel_2.add(lunxuTime);

		JPanel panel_3 = new JPanel(new BorderLayout());
		panel_3.setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_3.setBounds(10, 191, 1158, 154);
		contentPane.add(panel_3);

		String[] columnNames = { "任务号", "时间", "执行描述" };
		dtm = new DefaultTableModel(columnNames, 0);
		table = new JTable(dtm);
		table.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		table.setShowGrid(false);
		table.getColumnModel().getColumn(0).setPreferredWidth(120);
		table.getColumnModel().getColumn(0).setMaxWidth(160);
		table.getColumnModel().getColumn(0).sizeWidthToFit();
		table.getColumnModel().getColumn(1).setPreferredWidth(120);
		table.getColumnModel().getColumn(1).setMaxWidth(160);
		table.getColumnModel().getColumn(1).sizeWidthToFit();
		table.getColumnModel().getColumn(2).sizeWidthToFit();

		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		panel_3.add(table.getTableHeader(), BorderLayout.NORTH);
		panel_3.add(table, BorderLayout.CENTER);

		authCode = new JTextArea();
		authCode.setEditable(false);
		authCode.setBounds(259, 355, 909, 108);
		authCode.setLineWrap(true); // 激活自动换行功能
		authCode.setWrapStyleWord(true);
		authCode.setVisible(true);
		contentPane.add(authCode);

		chckbxNewCheckBox_1 = new JCheckBox("使用代理");
		chckbxNewCheckBox_1.setBounds(1075, 489, 89, 27);
		chckbxNewCheckBox_1.setVisible(false);
		chckbxNewCheckBox_1.setSelected(false);
		contentPane.add(chckbxNewCheckBox_1);

		proxyPlan = new JTextArea();
		proxyPlan.setText("139.199.89.119");
		proxyPlan.setBounds(818, 491, 251, 49);
		proxyPlan.setLineWrap(true); // 激活自动换行功能
		proxyPlan.setWrapStyleWord(true);
		proxyPlan.setVisible(false);
		contentPane.add(proxyPlan);

		JPanel panel_5 = new JPanel();
		panel_5.setBounds(10, 355, 205, 108);
		panel_5.setLayout(null);
		contentPane.add(panel_5);

		erweiQcode = new JLabel("");
		erweiQcode.setBounds(98, 0, 107, 108);
		panel_5.add(erweiQcode);

		JButton btnNewButton_2 = new JButton("刷二维码");
		btnNewButton_2.setBounds(0, 0, 87, 23);
		panel_5.add(btnNewButton_2);
		btnNewButton_2.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				try {
					String areaAuth = authCode.getText();
					if (MaotaiSession.isLogined() || !StringUtils.isEmpty(areaAuth)) {
						MaotaiService maotaiService = (MaotaiService) SpringContextUtils.getContext()
								.getBean("maotaiServiceImpl");
						String auth = "";
						if (MaotaiSession.isLogined()) {
							auth = MaotaiSession.getValidAuth(userNameField.getText());
						} else if (!StringUtils.isEmpty(areaAuth)) {
							auth = areaAuth;
							maotaiService.login("", "", auth);
						}
						ReturnResultBean result = maotaiService.getOrderQCode(auth);
						if (result.getResultCode() != 0) {
							JOptionPane.showMessageDialog(panel_2, result.getReturnMsg());
						} else {
							Icon icon = QCodeUtil.generateQCode((String) result.getReturnObj());
							erweiQcode.setIcon(icon);
						}
					} else {
						JOptionPane.showMessageDialog(panel_2, "请登录");
					}
				} catch (WriterException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
	}
}
