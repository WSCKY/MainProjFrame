package FactoryTester;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
//import java.util.Timer;
//import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.prefs.Preferences;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import protocol.ComPackage;
import protocol.RxAnalyse;
import SerialTool.SerialTool;
import SerialTool.serialException.NoSuchPort;
import SerialTool.serialException.NotASerialPort;
import SerialTool.serialException.PortInUse;
import SerialTool.serialException.ReadDataFromSerialPortFailure;
import SerialTool.serialException.SendDataToSerialPortFailure;
import SerialTool.serialException.SerialPortInputStreamCloseFailure;
import SerialTool.serialException.SerialPortOutputStreamCloseFailure;
import SerialTool.serialException.SerialPortParameterFailure;
import SerialTool.serialException.TooManyListeners;

public class FactoryTester extends JFrame {
	private static final long serialVersionUID = 1L;

	Preferences pref = null;
	private String _Interface = "Uart";

	private static DatagramSocket CommSocket = null;
	private static final int CommPort = 6000;
	private static final String CommIP = "192.168.4.1";

	private static ComPackage rxData = new ComPackage();
	private static ComPackage txData = new ComPackage();

	private SerialPort serialPort = null;
	private List<String> srList = null;

	private JLabel debug_info = new JLabel("ready.");
	/* 串口连接 */
	private JPanel ComPanel = new JPanel();
	private JComboBox<String> srSelect = new JComboBox<String>();
	private JComboBox<String> srBaudSet = new JComboBox<String>();
	private final String[] srBaudRate = {"9600", "57600", "115200", "230400"};
	private JButton OpenPortBtn = new JButton("连接");
	/* wifi */
	private JLabel ip_lab = new JLabel("IP:");
	private JTextField IP_Txt = new JTextField(CommIP);
	private JLabel port_lab = new JLabel("port:");
	private JTextField Port_Txt = new JTextField("6000");
	/* 主面板 */
	private JPanel MainPanel = new JPanel();

	private JPanel InfoPanel = new JPanel();
	private JPanel InitRetPanel = new JPanel();
	private JPanel VersionPanel = new JPanel();
	private JPanel LEDPanel = new JPanel();
	private JPanel ESCBurnInPanel = new JPanel();

	private JLabel VoltText = new JLabel("0.0");
	private JLabel VelXText = new JLabel("0.0");
	private JLabel VelYText = new JLabel("0.0");
	private JLabel PitchText = new JLabel("0.0");
	private JLabel RollText = new JLabel("0.0");

	private JTextField VER_txt = new JTextField(9);
	private JTextField DSN_txt = new JTextField(16);

	private JLabel IMUSta = new JLabel();
	private JLabel BAROSta = new JLabel();
	private JLabel MTDSta = new JLabel();
	private JLabel FLOWSta = new JLabel();
	private JLabel TOFSta = new JLabel();

	private JCheckBox Red_Box = new JCheckBox("红");
	private JCheckBox Blue_Box = new JCheckBox("蓝");
	private JCheckBox Green_Box = new JCheckBox("绿");

//	private JProgressBar ESCBurnInBar = new JProgressBar(0, 100);
	private JButton MotorStartBtn = new JButton("启动");
//	private JButton StartBurnInBtn = new JButton("开始");
	private JButton StopBurnInBtn = new JButton("停止");
	/* 菜单栏 */
	JMenuBar MenuBar = new JMenuBar();
	JMenu setMenu = new JMenu("设置(s)");
	JMenu ItemInterface = new JMenu("接口(i)");
	JCheckBoxMenuItem ItemUart = null;
	JCheckBoxMenuItem ItemWifi = null;
	ButtonGroup Interface_bg = new ButtonGroup();

	public FactoryTester() {
		pref = Preferences.userRoot().node(this.getClass().getName());
		_Interface = pref.get("_fact_Interface", "");
		if(_Interface.equals("")) _Interface = "Uart";

		ItemUart = new JCheckBoxMenuItem("串口", _Interface.equals("Uart"));
		ItemWifi = new JCheckBoxMenuItem("Wifi", _Interface.equals("Wifi"));
		ItemUart.addActionListener(ifl); ItemWifi.addActionListener(ifl);

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				setTitle("F1/2飞控主板测试工具  V2.3.0");
				setSize(1000, 520);
				setResizable(false);
				addWindowListener(wl);
				setLocationRelativeTo(null);
				setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				setIconImage(getToolkit().getImage(FactoryTester.class.getResource("FactoryTest.png")));

				setJMenuBar(MenuBar);
				MenuBar.add(setMenu);
				setMenu.setMnemonic('s');
				setMenu.setFont(new Font("宋体", Font.PLAIN, 14));
				ItemInterface.setMnemonic('i');
				ItemInterface.setFont(new Font("宋体", Font.PLAIN, 14));
				setMenu.add(ItemInterface);
				ItemUart.setFont(new Font("宋体", Font.PLAIN, 14));
				Interface_bg.add(ItemUart);
				ItemInterface.add(ItemUart);
				ItemWifi.setFont(new Font("宋体", Font.PLAIN, 14));
				Interface_bg.add(ItemWifi);
				ItemInterface.add(ItemWifi);

				ComPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5));
				ComPanel.setBackground(new Color(233, 80, 80, 160));
				debug_info.setHorizontalAlignment(SwingConstants.RIGHT);
				debug_info.setVerticalAlignment(SwingConstants.BOTTOM);
				debug_info.setFont(debug_info.getFont().deriveFont(Font.ITALIC));
//				debug_info.setBorder(BorderFactory.createLineBorder(Color.RED));
				debug_info.setToolTipText("debug info");
				/* Uart */
				srSelect.setPreferredSize(new Dimension(90, 30));
				srSelect.setFont(srSelect.getFont().deriveFont(Font.BOLD, 14));
				srSelect.setToolTipText("select com port");

				srBaudSet.setPreferredSize(new Dimension(90, 30));
				srBaudSet.setMaximumRowCount(5);
				srBaudSet.setEditable(false);
				for(String s : srBaudRate)
					srBaudSet.addItem(s);
				srBaudSet.setSelectedIndex(2);//default: 115200
				srBaudSet.setFont(srBaudSet.getFont().deriveFont(Font.BOLD, 14));
				srBaudSet.setToolTipText("set baudrate");

				OpenPortBtn.setPreferredSize(new Dimension(90, 30));
				OpenPortBtn.setFont(new Font("宋体", Font.BOLD, 18));
				OpenPortBtn.addActionListener(opl);
				OpenPortBtn.setToolTipText("open com port");
				/* Wifi */
				ip_lab.setPreferredSize(new Dimension(28, 30));
				ip_lab.setFont(ip_lab.getFont().deriveFont(Font.ITALIC, 18));

				IP_Txt.setPreferredSize(new Dimension(130, 30));
				IP_Txt.setFont(new Font("Courier New", Font.BOLD, 18));
				IP_Txt.setToolTipText("IP Address");
				IP_Txt.setHorizontalAlignment(JTextField.CENTER);
				IP_Txt.setEditable(false);

				port_lab.setPreferredSize(new Dimension(45, 30));
				port_lab.setFont(ip_lab.getFont().deriveFont(Font.ITALIC, 18));

				Port_Txt.setPreferredSize(new Dimension(50, 30));
				Port_Txt.setFont(new Font("Courier New", Font.BOLD, 18));
				Port_Txt.setToolTipText("UDP Port");
				Port_Txt.setHorizontalAlignment(JTextField.CENTER);
				Port_Txt.setEditable(false);
				if(_Interface.equals("Uart")) {
					ComPanel.add(srSelect);
					ComPanel.add(srBaudSet);
					ComPanel.add(OpenPortBtn);
					debug_info.setPreferredSize(new Dimension(680, 30));
					ComPanel.add(debug_info);
				} else if(_Interface.equals("Wifi")) {
					ComPanel.add(ip_lab);
					ComPanel.add(IP_Txt);
					ComPanel.add(port_lab);
					ComPanel.add(Port_Txt);
					debug_info.setPreferredSize(new Dimension(687, 30));
					ComPanel.add(debug_info);
				}
				add(ComPanel, BorderLayout.NORTH);

				InitRetPanel.setLayout(new GridLayout(1, 5, 0, 0));
				InitRetPanel.setBorder(BorderFactory.createTitledBorder(null, "飞控外设", 0, 2, new Font("宋体", Font.PLAIN, 16)));
				IMUSta.setIcon(new ImageIcon(new ImageIcon(getClass().getResource("wait_s.gif")).getImage().getScaledInstance(25, 25, Image.SCALE_DEFAULT)));
				BAROSta.setIcon(new ImageIcon(new ImageIcon(getClass().getResource("wait_s.gif")).getImage().getScaledInstance(25, 25, Image.SCALE_DEFAULT)));
				MTDSta.setIcon(new ImageIcon(new ImageIcon(getClass().getResource("wait_s.gif")).getImage().getScaledInstance(25, 25, Image.SCALE_DEFAULT)));
				FLOWSta.setIcon(new ImageIcon(new ImageIcon(getClass().getResource("wait_s.gif")).getImage().getScaledInstance(25, 25, Image.SCALE_DEFAULT)));
				TOFSta.setIcon(new ImageIcon(new ImageIcon(getClass().getResource("wait_s.gif")).getImage().getScaledInstance(25, 25, Image.SCALE_DEFAULT)));
				JLabel NameLabel = new JLabel("IMU: "); NameLabel.setFont(new Font("宋体", Font.BOLD, 20));
				JPanel p = new JPanel(); p.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 8));
//				p.setBorder(BorderFactory.createLineBorder(Color.RED));
				p.add(NameLabel); p.add(IMUSta); InitRetPanel.add(p);

				NameLabel = new JLabel("气压计: "); NameLabel.setFont(new Font("宋体", Font.BOLD, 20));
				p = new JPanel(); p.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 8));
//				p.setBorder(BorderFactory.createLineBorder(Color.RED));
				p.add(NameLabel); p.add(BAROSta); InitRetPanel.add(p);

				NameLabel = new JLabel("FLASH: "); NameLabel.setFont(new Font("宋体", Font.BOLD, 20));
				p = new JPanel(); p.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 8));
//				p.setBorder(BorderFactory.createLineBorder(Color.RED));
				p.add(NameLabel); p.add(MTDSta); InitRetPanel.add(p);

				NameLabel = new JLabel("光流: "); NameLabel.setFont(new Font("宋体", Font.BOLD, 20));
				p = new JPanel(); p.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 8));
//				p.setBorder(BorderFactory.createLineBorder(Color.RED));
				p.add(NameLabel); p.add(FLOWSta); InitRetPanel.add(p);

				NameLabel = new JLabel("红外: "); NameLabel.setFont(new Font("宋体", Font.BOLD, 20));
				p = new JPanel(); p.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 8));
//				p.setBorder(BorderFactory.createLineBorder(Color.RED));
				p.add(NameLabel); p.add(TOFSta); InitRetPanel.add(p);

				InfoPanel.setLayout(new GridLayout(1, 5));
				InfoPanel.setBorder(BorderFactory.createTitledBorder(null, "状态信息", 0, 2, new Font("宋体", Font.PLAIN, 16)));
				NameLabel = new JLabel("电压: "); NameLabel.setFont(new Font("宋体", Font.BOLD, 20));
				p = new JPanel(); p.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 8));
				VoltText.setFont(VoltText.getFont().deriveFont(Font.BOLD, 28));
				p.add(NameLabel); p.add(VoltText); InfoPanel.add(p);

				NameLabel = new JLabel("速度X: "); NameLabel.setFont(new Font("宋体", Font.BOLD, 20));
				p = new JPanel(); p.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 8));
				VelXText.setFont(VelXText.getFont().deriveFont(Font.BOLD, 28));
				p.add(NameLabel); p.add(VelXText); InfoPanel.add(p);

				NameLabel = new JLabel("速度Y: "); NameLabel.setFont(new Font("宋体", Font.BOLD, 20));
				p = new JPanel(); p.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 8));
				VelYText.setFont(VelYText.getFont().deriveFont(Font.BOLD, 28));
				p.add(NameLabel); p.add(VelYText); InfoPanel.add(p);

				NameLabel = new JLabel("俯仰: "); NameLabel.setFont(new Font("宋体", Font.BOLD, 20));
				p = new JPanel(); p.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 8));
				PitchText.setFont(PitchText.getFont().deriveFont(Font.BOLD, 28));
				p.add(NameLabel); p.add(PitchText); InfoPanel.add(p);

				NameLabel = new JLabel("横滚: "); NameLabel.setFont(new Font("宋体", Font.BOLD, 20));
				p = new JPanel(); p.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 8));
				RollText.setFont(RollText.getFont().deriveFont(Font.BOLD, 28));
				p.add(NameLabel); p.add(RollText); InfoPanel.add(p);

				VersionPanel.setBorder(BorderFactory.createTitledBorder(null, "版本信息", 0, 2, new Font("宋体", Font.PLAIN, 16)));
				VersionPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 3));
				NameLabel = new JLabel("版本: "); NameLabel.setFont(new Font("宋体", Font.BOLD, 20));
				VersionPanel.add(NameLabel);
				VER_txt.setFont(new Font("Courier New", Font.BOLD, 26));
				VER_txt.setEditable(false); VersionPanel.add(VER_txt);
				NameLabel = new JLabel("序列号: "); NameLabel.setFont(new Font("宋体", Font.BOLD, 20));
				VersionPanel.add(NameLabel);
				DSN_txt.setFont(new Font("Courier New", Font.BOLD, 26));
				DSN_txt.setEditable(false); VersionPanel.add(DSN_txt);

				LEDPanel.setBorder(BorderFactory.createTitledBorder(null, "主状态灯", 0, 2, new Font("宋体", Font.PLAIN, 16)));
				LEDPanel.setBackground(new Color(120, 120, 120)); LEDPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 150, 0));
				Red_Box.setFont(new Font("宋体", Font.BOLD, 30)); Red_Box.addActionListener(ledl);
				Blue_Box.setFont(new Font("宋体", Font.BOLD, 30)); Blue_Box.addActionListener(ledl);
				Green_Box.setFont(new Font("宋体", Font.BOLD, 30)); Green_Box.addActionListener(ledl);
				LEDPanel.add(Red_Box); LEDPanel.add(Blue_Box); LEDPanel.add(Green_Box);

				ESCBurnInPanel.setBorder(BorderFactory.createTitledBorder(null, "马达控制", 0, 2, new Font("宋体", Font.PLAIN, 16)));
				ESCBurnInPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 60, 5));
				MotorStartBtn.setPreferredSize(new Dimension(120, 40)); MotorStartBtn.setEnabled(false);
				MotorStartBtn.setFont(MotorStartBtn.getFont().deriveFont(Font.BOLD, 20));
				MotorStartBtn.addActionListener(mstartl); ESCBurnInPanel.add(MotorStartBtn);
				StopBurnInBtn.setPreferredSize(new Dimension(120, 40)); StopBurnInBtn.setEnabled(false);
				StopBurnInBtn.setFont(StopBurnInBtn.getFont().deriveFont(Font.BOLD, 20));
				StopBurnInBtn.addActionListener(bstopl); ESCBurnInPanel.add(StopBurnInBtn);

				MainPanel.setLayout(new GridLayout(5, 1));
				MainPanel.add(InitRetPanel);
				MainPanel.add(InfoPanel);
				MainPanel.add(LEDPanel);
				MainPanel.add(VersionPanel);
				MainPanel.add(ESCBurnInPanel);
				add(MainPanel, BorderLayout.CENTER);

				setVisible(true);
			}
		});

		if(_Interface.equals("Wifi")) {
			if(CommSocket == null) {
				try {
					CommSocket = new DatagramSocket(CommPort);
					debug_info.setText("udp port opened, ready...");
				} catch (SocketException e) {
					JOptionPane.showMessageDialog(null, e, "error!", JOptionPane.ERROR_MESSAGE);
					System.exit(0);
				}
				new Thread(new WifiRxThread()).start();
			}
		}

		new Thread(new RepaintThread()).start();
		new Thread(new UpgradeTxThread()).start();
		new Thread(new SignalTestThread()).start();
//		Timer timer = new Timer();
//		timer.schedule(new TimerTask() {
//			public void run() {
//				System.out.println("/* 2000ms.... */");
//			}
//		}, 2000);
	}

	private class WifiRxThread implements Runnable {
		public void run() {
			while(true) {
				if(_Interface.equals("Wifi") && CommSocket != null) {
					byte[] data = new byte[100];
					DatagramPacket packet = new DatagramPacket(data, 0, data.length);
					try {
						CommSocket.receive(packet);
					} catch (IOException e) {
						e.printStackTrace();
					}
					byte[] recData = packet.getData();
					RxDataProcess(recData, packet.getLength());
				} else {
					try {
						TimeUnit.MILLISECONDS.sleep(10);//wait 10ms
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	private class SerialListener implements SerialPortEventListener {
	    public void serialEvent(SerialPortEvent serialPortEvent) {
	        switch (serialPortEvent.getEventType()) {
	            case SerialPortEvent.BI: // 10 通讯中断s
//	            	JOptionPane.showMessageDialog(null, "communication interrupted!", "error!", JOptionPane.ERROR_MESSAGE);
	            break;
	            case SerialPortEvent.OE: // 7 溢位（溢出）错误
	            case SerialPortEvent.FE: // 9 帧错误
	            case SerialPortEvent.PE: // 8 奇偶校验错误
	            case SerialPortEvent.CD: // 6 载波检测
	            case SerialPortEvent.CTS: // 3 清除待发送数据
	            case SerialPortEvent.DSR: // 4 待发送数据准备好了
	            case SerialPortEvent.RI: // 5 振铃指示
	            case SerialPortEvent.OUTPUT_BUFFER_EMPTY: // 2 输出缓冲区已清空
	            break;
	            case SerialPortEvent.DATA_AVAILABLE: // 1 串口存在可用数据
	            	byte[] data = null;
	            	try {
		            	if (serialPort == null) {
							JOptionPane.showMessageDialog(null, "serial port = null", "error!", JOptionPane.ERROR_MESSAGE);
						} else {
							data = SerialTool.readFromPort(serialPort);//read data from port.
							if (data == null || data.length < 1) {//check data.
								JOptionPane.showMessageDialog(null, "no valid data!", "error!", JOptionPane.ERROR_MESSAGE);
								System.exit(0);
							} else {
								RxDataProcess(data, data.length);
							}
						}
	            	} catch (ReadDataFromSerialPortFailure | SerialPortInputStreamCloseFailure e) {
						e.printStackTrace();
					}
	            break;
	        }
	    }
	}
	private boolean GotVersionFlag = false;
	private void RxDataProcess(byte[] rData, int len) {
		try {
			for(int i = 0; i < len; i ++)
				RxAnalyse.rx_decode(rData[i]);
			if(RxAnalyse.GotNewPackage()) {
				GotResponseFlag = true;
				StopBurnInBtn.setEnabled(true);
				if(ESCBurnInRunningFlag == false) {
					MotorStartBtn.setEnabled(true);
				}
				synchronized(new String("")) {//unnecessary (copy).
					try {
						rxData = (ComPackage) RxAnalyse.RecPackage.PackageCopy();
						if(rxData.type == ComPackage.TYPE_FC_Response) {
							if((rxData.rData[1] & 0x01) == 0x01) {
								IMUSta.setIcon(new ImageIcon(new ImageIcon(getClass().getResource("cha.png")).getImage().getScaledInstance(25, 25, Image.SCALE_DEFAULT)));
							} else {
								IMUSta.setIcon(new ImageIcon(new ImageIcon(getClass().getResource("dui.png")).getImage().getScaledInstance(25, 25, Image.SCALE_DEFAULT)));
							}
							if((rxData.rData[1] & 0x02) == 0x02) {
								BAROSta.setIcon(new ImageIcon(new ImageIcon(getClass().getResource("cha.png")).getImage().getScaledInstance(25, 25, Image.SCALE_DEFAULT)));
							} else {
								BAROSta.setIcon(new ImageIcon(new ImageIcon(getClass().getResource("dui.png")).getImage().getScaledInstance(25, 25, Image.SCALE_DEFAULT)));
							}
							if((rxData.rData[1] & 0x08) == 0x08) {
								MTDSta.setIcon(new ImageIcon(new ImageIcon(getClass().getResource("cha.png")).getImage().getScaledInstance(25, 25, Image.SCALE_DEFAULT)));
							} else {
								MTDSta.setIcon(new ImageIcon(new ImageIcon(getClass().getResource("dui.png")).getImage().getScaledInstance(25, 25, Image.SCALE_DEFAULT)));
							}
							if((rxData.rData[2] & 0x08) == 0x08) {
								TOFSta.setIcon(new ImageIcon(new ImageIcon(getClass().getResource("cha.png")).getImage().getScaledInstance(25, 25, Image.SCALE_DEFAULT)));
							} else {
								if((rxData.rData[2] & 0x04) == 0x04) {
									TOFSta.setIcon(new ImageIcon(new ImageIcon(getClass().getResource("warning_s.png")).getImage().getScaledInstance(25, 25, Image.SCALE_DEFAULT)));
								} else {
									TOFSta.setIcon(new ImageIcon(new ImageIcon(getClass().getResource("dui.png")).getImage().getScaledInstance(25, 25, Image.SCALE_DEFAULT)));
								}
							}
							if((rxData.rData[2] & 0x20) == 0x20) {
								FLOWSta.setIcon(new ImageIcon(new ImageIcon(getClass().getResource("cha.png")).getImage().getScaledInstance(25, 25, Image.SCALE_DEFAULT)));
							} else {
								if((rxData.rData[2] & 0x10) == 0x10) {
									FLOWSta.setIcon(new ImageIcon(new ImageIcon(getClass().getResource("warning_s.png")).getImage().getScaledInstance(25, 25, Image.SCALE_DEFAULT)));
								} else {
									FLOWSta.setIcon(new ImageIcon(new ImageIcon(getClass().getResource("dui.png")).getImage().getScaledInstance(25, 25, Image.SCALE_DEFAULT)));
								}
							}
							int val = rxData.rData[14] & 0xFF;
							VoltText.setText(String.format("%.2f", ((float)val + 640.0)/71.0));
							PitchText.setText(String.format("%.2f", rxData.readoutFloat(5)));
							RollText.setText(String.format("%.2f", rxData.readoutFloat(9)));
							VelXText.setText(String.format("%.2f", rxData.readoutFloat(19)));
							VelYText.setText(String.format("%.2f", rxData.readoutFloat(23)));
						} else if(rxData.type == ComPackage.TYPE_VERSION_Response) {
							GotVersionFlag = true;
							char ver = rxData.readoutCharacter(0);
							VER_txt.setText("V" + (ver >> 12) + "." + ((ver >> 8) & 0x0F) + "." + (ver & 0x00FF));
							String curDSN = rxData.readoutString(4, 16);
							DSN_txt.setText(curDSN);
						}
					} catch (CloneNotSupportedException e) {
						e.printStackTrace();
					}
				}
			}
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
	}

	private class UpgradeTxThread implements Runnable {
		public void run() {
			while(true) {
				if(ESCBurnInRunningFlag == true) {
					txData.type = ComPackage.TYPE_ESC_BURN_IN_TEST;
					txData.addByte((byte) 0, 0);//speed: 0%
					txData.addByte((byte)(0 ^ 0xCC), 1);
					txData.setLength(4);
				} else if(GotVersionFlag == false) {
					txData.type = ComPackage.TYPE_VERSION_REQUEST;
					txData.addByte((byte)0x0F, 0);
					txData.setLength(3);
				} else {/* no operation */
					txData.type = ComPackage.TYPE_DeviceCheckReq;
					txData.addByte(ComPackage._dev_LED, 0);
					txData.addByte(LEDValue, 1);
					txData.addFloat(0.0f, 5);
					txData.addByte((byte)0, 9);
					txData.addFloat(0.0f, 10);
					txData.setLength(10);
				}
				byte[] SendBuffer = txData.getSendBuffer();
				if(_Interface.equals("Wifi") && CommSocket != null) {
					DatagramPacket packet = new DatagramPacket(SendBuffer, 0, SendBuffer.length, new InetSocketAddress(CommIP, CommPort));
					try {
						CommSocket.send(packet);
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else if(_Interface.equals("Uart") && serialPort != null) {
					try {
						SerialTool.sendToPort(serialPort, SendBuffer);
					} catch (SendDataToSerialPortFailure e) {
						e.printStackTrace();
					} catch (SerialPortOutputStreamCloseFailure e) {
						e.printStackTrace();
					}
				}
				try {
					TimeUnit.MILLISECONDS.sleep(100);//100ms
				} catch (InterruptedException e) {
					System.err.println("Interrupted");
				}
			}
		}
	}

	private static boolean GotResponseFlag = false;
	private static int SignalLostCnt = 0;
	private class SignalTestThread implements Runnable {
		public void run() {
			while(true) {
				if(GotResponseFlag == false) {
					if(SignalLostCnt < 20)
						SignalLostCnt ++;
					else {
						SignalLostCnt = 0;
						debug_info.setText("signal lost.");
						ComPanel.setBackground(new Color(233, 80, 80, 160));
						GotVersionFlag = false;
						VER_txt.setText(""); DSN_txt.setText("");
						MotorStartBtn.setEnabled(false); StopBurnInBtn.setEnabled(false);
						IMUSta.setIcon(new ImageIcon(new ImageIcon(getClass().getResource("wait_s.gif")).getImage().getScaledInstance(25, 25, Image.SCALE_DEFAULT)));
						BAROSta.setIcon(new ImageIcon(new ImageIcon(getClass().getResource("wait_s.gif")).getImage().getScaledInstance(25, 25, Image.SCALE_DEFAULT)));
						MTDSta.setIcon(new ImageIcon(new ImageIcon(getClass().getResource("wait_s.gif")).getImage().getScaledInstance(25, 25, Image.SCALE_DEFAULT)));
						FLOWSta.setIcon(new ImageIcon(new ImageIcon(getClass().getResource("wait_s.gif")).getImage().getScaledInstance(25, 25, Image.SCALE_DEFAULT)));
						TOFSta.setIcon(new ImageIcon(new ImageIcon(getClass().getResource("wait_s.gif")).getImage().getScaledInstance(25, 25, Image.SCALE_DEFAULT)));
					}
				} else {
					SignalLostCnt = 0;
					GotResponseFlag = false;
				}
				try {
					TimeUnit.MILLISECONDS.sleep(50);//50ms loop.
				} catch (InterruptedException e) {
					System.err.println("Interrupted");
				}
			}
		}
	}

	private byte LEDValue = 0;
	private ActionListener ledl = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			LEDValue = 0;
			int red = 0, blue = 0, green = 0;
			String name = ((JCheckBox)e.getSource()).getText();
			if(name.equals("红")) {
				if(Red_Box.isSelected()) {
					Blue_Box.setSelected(false);
					Green_Box.setSelected(false);
					red = 255; LEDValue |= (byte)0x01;
				}
			} else if(name.equals("蓝")) {
				if(Blue_Box.isSelected()) {
					Red_Box.setSelected(false);
					Green_Box.setSelected(false);
					blue = 255; LEDValue |= (byte)0x02;
				}
			} else if(name.equals("绿")) {
				  if(Green_Box.isSelected()) {
					Red_Box.setSelected(false);
					Blue_Box.setSelected(false);
					green = 255; LEDValue |= (byte)0x04;
				}
			}
			if(red == 0 && blue == 0 && green == 0)
				red = blue = green = 120;
			LEDPanel.setBackground(new Color(red, green, blue));
		}
	};

	private static boolean ESCBurnInRunningFlag = false;
	private ActionListener mstartl = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			if(ESCBurnInRunningFlag == false) {
				ESCBurnInRunningFlag = true;
				MotorStartBtn.setEnabled(false);
			}
		}
	};

	private ActionListener bstopl = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			ESCBurnInRunningFlag = false;
		}
	};

	private ActionListener opl = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			String name = ((JButton)e.getSource()).getText();
			if(name.equals("连接")) {
				String srName = (String) srSelect.getSelectedItem();
				String srBaud = (String) srBaudSet.getSelectedItem();
				if(srName == null || srName.equals("")) { // check serial port
					JOptionPane.showMessageDialog(null, "no serial port!", "error!", JOptionPane.ERROR_MESSAGE);
				} else {
					if(srBaud == null || srBaud.equals("")) {
						JOptionPane.showMessageDialog(null, "baudrate error!", "error!", JOptionPane.ERROR_MESSAGE);
					} else {
						int bps = Integer.parseInt(srBaud);

						try {
							serialPort = SerialTool.openPort(srName, bps);
							SerialTool.addListener(serialPort, new SerialListener());
							((JButton)e.getSource()).setText("断开");
							srSelect.setEnabled(false);
							srBaudSet.setEnabled(false);
							debug_info.setText("Uart port opened.");
						} catch (SerialPortParameterFailure | NotASerialPort | NoSuchPort | PortInUse | TooManyListeners e1) {
							JOptionPane.showMessageDialog(null, e1, "error!", JOptionPane.ERROR_MESSAGE);
						}
					}
				}
			} else if(name.equals("断开")) {
				SerialTool.closePort(serialPort);

				serialPort = null;
				srSelect.setEnabled(true);
				srBaudSet.setEnabled(true);
				((JButton)e.getSource()).setText("连接");
				debug_info.setText("Uart port closed.");
			}
		}
	};

	private ActionListener ifl = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			if(ItemUart.isSelected()) _Interface = "Uart";
			if(ItemWifi.isSelected()) _Interface =  "Wifi";
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					if(_Interface.equals("Uart")) {
						ComPanel.removeAll();

						ComPanel.add(srSelect);
						ComPanel.add(srBaudSet);
						ComPanel.add(OpenPortBtn);
						debug_info.setPreferredSize(new Dimension(680, 30));
						debug_info.setText("uart selected.");
						ComPanel.add(debug_info);
						repaint();
						ComPanel.validate();
					} else if(_Interface.equals("Wifi")) {
						ComPanel.removeAll();

						ComPanel.add(ip_lab);
						ComPanel.add(IP_Txt);
						ComPanel.add(port_lab);
						ComPanel.add(Port_Txt);
						debug_info.setPreferredSize(new Dimension(687, 30));
						ComPanel.add(debug_info);
						debug_info.setText("wifi selected.");
						repaint();
						ComPanel.validate();

						if(serialPort != null) {
							SerialTool.closePort(serialPort);
	
							serialPort = null;
							srSelect.setEnabled(true);
							srBaudSet.setEnabled(true);
							OpenPortBtn.setText("连接");
						}

						if(CommSocket == null) {
							try {
								CommSocket = new DatagramSocket(CommPort);
								debug_info.setText("udp port opened, ready...");
							} catch (SocketException e) {
								JOptionPane.showMessageDialog(null, e, "error!", JOptionPane.ERROR_MESSAGE);
								System.exit(0);
							}
							new Thread(new WifiRxThread()).start();
						}
					}
				}
			});
			pref.put("_fact_Interface", _Interface);
		}
	};

	WindowAdapter wl = new WindowAdapter() {
		public void windowClosing(WindowEvent e) {
			System.exit(0);
		}
	};

	private class RepaintThread implements Runnable {
		public void run() {
			while(true) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						repaint();
					}
				});

				srList = SerialTool.findPort();//find serial port.
				if(srList != null && srList.size() > 0) {
					//add new
					for(String s : srList) {
						boolean srExist = false;
						for(int i = 0; i < srSelect.getItemCount(); i ++) {
							if(s.equals(srSelect.getItemAt(i))) {
								srExist = true;
								break;
							}
						}
						if(srExist == true)
							continue;
						else
							srSelect.addItem(s);
					}

					//remove invalid
					for(int i = 0; i < srSelect.getItemCount(); i ++) {
						boolean srInvalid = true;
						for(String s : srList) {
							if(s.equals(srSelect.getItemAt(i))) {
								srInvalid = false;
								break;
							}
						}
						if(srInvalid == true)
							srSelect.removeItemAt(i);
						else
							continue;
					}
				} else {
					srSelect.removeAllItems();//should NOT be removeAll();
				}

				try {
					TimeUnit.MILLISECONDS.sleep(10);//10ms loop.
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static void main(String[] args) {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Date Today = new Date();
		try {
			Date InvalidDay = df.parse("2018-6-01");
			if(Today.getTime() > InvalidDay.getTime()) {
				System.err.println("System error.");
//				JOptionPane.showMessageDialog(null, "Sorry, Exit With Unknow Error!", "error!", JOptionPane.ERROR_MESSAGE);
				System.exit(0);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
//		System.err.println(System.getProperty("user.dir"));//get program running directory.
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		new FactoryTester();
	}
}
