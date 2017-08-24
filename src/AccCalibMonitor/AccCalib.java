package AccCalibMonitor;

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
import java.util.concurrent.TimeUnit;
import java.util.prefs.Preferences;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
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

public class AccCalib extends JFrame{
	private static final long serialVersionUID = 1L;

	Preferences pref = null;
	private String _Interface = "Uart";

	private static ComPackage rxData = new ComPackage();
	private static ComPackage txData = new ComPackage();

	private static final int CommPort = 6000;
	private static final String CommIP = "192.168.4.1";
	private static DatagramSocket CommSocket = null;

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

	private JPanel InfoPanel = new JPanel(); private JLabel Info_lab = new JLabel("连接断开");
	private JPanel ProgPanel = new JPanel();
	private JLabel SampleSta_s1 = new JLabel(); private JProgressBar ProgBar_s1 = new JProgressBar(0, 100); private JPanel ps1 = new JPanel();
	private JLabel SampleSta_s2 = new JLabel(); private JProgressBar ProgBar_s2 = new JProgressBar(0, 100); private JPanel ps2 = new JPanel();
	private JLabel SampleSta_s3 = new JLabel(); private JProgressBar ProgBar_s3 = new JProgressBar(0, 100); private JPanel ps3 = new JPanel();
	private JLabel SampleSta_s4 = new JLabel(); private JProgressBar ProgBar_s4 = new JProgressBar(0, 100); private JPanel ps4 = new JPanel();
	private JLabel SampleSta_s5 = new JLabel(); private JProgressBar ProgBar_s5 = new JProgressBar(0, 100); private JPanel ps5 = new JPanel();
	private JLabel SampleSta_s6 = new JLabel(); private JProgressBar ProgBar_s6 = new JProgressBar(0, 100); private JPanel ps6 = new JPanel();
	private JPanel BtnPanel = new JPanel();
	private JButton StartButton = new JButton("开 始");
	/* 菜单栏 */
	JMenuBar MenuBar = new JMenuBar();
	JMenu setMenu = new JMenu("设置(s)");
	JMenu ItemInterface = new JMenu("接口(i)");
	JCheckBoxMenuItem ItemUart = null;
	JCheckBoxMenuItem ItemWifi = null;
	ButtonGroup Interface_bg = new ButtonGroup();

	public AccCalib() {
		pref = Preferences.userRoot().node(this.getClass().getName());
		_Interface = pref.get("_acc_Interface", "");
		if(_Interface.equals("")) _Interface = "Uart";

		ItemUart = new JCheckBoxMenuItem("串口", _Interface.equals("Uart"));
		ItemWifi = new JCheckBoxMenuItem("Wifi", _Interface.equals("Wifi"));
		ItemUart.addActionListener(ifl); ItemWifi.addActionListener(ifl);

		setTitle("F1/2加速度六面校准工具  V1.1.0");
		setSize(660, 420);
		setResizable(false);
		addWindowListener(wl);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setIconImage(getToolkit().getImage(AccCalib.class.getResource("Tool.png")));

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
//		debug_info.setBorder(BorderFactory.createLineBorder(Color.RED));
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
			debug_info.setPreferredSize(new Dimension(340, 30));
			ComPanel.add(debug_info);
		} else if(_Interface.equals("Wifi")) {
			ComPanel.add(ip_lab);
			ComPanel.add(IP_Txt);
			ComPanel.add(port_lab);
			ComPanel.add(Port_Txt);
			debug_info.setPreferredSize(new Dimension(347, 30));
			ComPanel.add(debug_info);
		}
		add(ComPanel, BorderLayout.NORTH);

		MainPanel.setLayout(new BorderLayout(5, 10));
		Info_lab.setFont(new Font("楷体", Font.BOLD, 32));
		InfoPanel.setBackground(new Color(233, 80, 80, 160));
//		MainPanel.add(Info_lab, BorderLayout.NORTH);
		InfoPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 20));
		InfoPanel.add(Info_lab);
		MainPanel.add(InfoPanel, BorderLayout.NORTH);
		ProgPanel.setLayout(new GridLayout(3, 2, 5, 10));
		ProgPanel.add(ps1); ProgPanel.add(ps2);
		ProgPanel.add(ps3); ProgPanel.add(ps4);
		ProgPanel.add(ps5); ProgPanel.add(ps6);
		SampleSta_s1.setIcon(new ImageIcon(new ImageIcon(getClass().getResource("wait_s.gif")).getImage().getScaledInstance(25, 25, Image.SCALE_DEFAULT)));
		SampleSta_s2.setIcon(new ImageIcon(new ImageIcon(getClass().getResource("wait_s.gif")).getImage().getScaledInstance(25, 25, Image.SCALE_DEFAULT)));
		SampleSta_s3.setIcon(new ImageIcon(new ImageIcon(getClass().getResource("wait_s.gif")).getImage().getScaledInstance(25, 25, Image.SCALE_DEFAULT)));
		SampleSta_s4.setIcon(new ImageIcon(new ImageIcon(getClass().getResource("wait_s.gif")).getImage().getScaledInstance(25, 25, Image.SCALE_DEFAULT)));
		SampleSta_s5.setIcon(new ImageIcon(new ImageIcon(getClass().getResource("wait_s.gif")).getImage().getScaledInstance(25, 25, Image.SCALE_DEFAULT)));
		SampleSta_s6.setIcon(new ImageIcon(new ImageIcon(getClass().getResource("wait_s.gif")).getImage().getScaledInstance(25, 25, Image.SCALE_DEFAULT)));
		ProgBar_s1.setPreferredSize(new Dimension(250, 25)); ProgBar_s2.setPreferredSize(new Dimension(250, 25));
		ProgBar_s3.setPreferredSize(new Dimension(250, 25)); ProgBar_s4.setPreferredSize(new Dimension(250, 25));
		ProgBar_s5.setPreferredSize(new Dimension(250, 25)); ProgBar_s6.setPreferredSize(new Dimension(250, 25));
		ProgBar_s1.setStringPainted(true); ProgBar_s2.setStringPainted(true);
		ProgBar_s3.setStringPainted(true); ProgBar_s4.setStringPainted(true);
		ProgBar_s5.setStringPainted(true); ProgBar_s6.setStringPainted(true);
		ps1.add(ProgBar_s1); ps1.add(SampleSta_s1); ps2.add(ProgBar_s2); ps2.add(SampleSta_s2);
		ps3.add(ProgBar_s3); ps3.add(SampleSta_s3); ps4.add(ProgBar_s4); ps4.add(SampleSta_s4);
		ps5.add(ProgBar_s5); ps5.add(SampleSta_s5); ps6.add(ProgBar_s6); ps6.add(SampleSta_s6);
		MainPanel.add(ProgPanel, BorderLayout.CENTER);
		StartButton.setFont(new Font("黑体", Font.BOLD, 30));
		StartButton.setPreferredSize(new Dimension(260, 46));
		StartButton.setEnabled(false); StartButton.addActionListener(sbl);
		BtnPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 20));
		BtnPanel.add(StartButton);
		MainPanel.add(BtnPanel, BorderLayout.SOUTH);
		add(MainPanel, BorderLayout.CENTER);
		getRootPane().setDefaultButton(StartButton);

		setVisible(true);

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
		new Thread(new TxDataThread()).start();
		new Thread(new RepaintThread()).start();
		new Thread(new SignalTestThread()).start();
	}

	private boolean CalibSendEnable = false;
	private boolean CalibStartedFlag = false;
	private byte HeartbatCnt = 0;
	private class TxDataThread implements Runnable {
		public void run() {
			while(true) {
				if(CalibSendEnable == true) {
					txData.type = ComPackage.TYPE_ACC_CALIBRATE;
					txData.addByte(ComPackage.ACC_CALIBRATE_VERIFY, 0);
					txData.setLength(3);
				} else {
					txData.type = ComPackage.TYPE_FC_APP_HEARTBEAT;
					txData.addByte(HeartbatCnt, 0);
					txData.setLength(3);
					HeartbatCnt ++;
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

	private boolean GotResponseFlag = false;
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
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	            break;
	        }
	    }
	}

	private void RxDataProcess(byte[] rData, int len) {
		try {
			for(int i = 0; i < len; i ++)
				RxAnalyse.rx_decode(rData[i]);
			if(RxAnalyse.GotNewPackage()) {
				synchronized(new String("")) {
					try {
						rxData = (ComPackage) RxAnalyse.RecPackage.PackageCopy();
					} catch (CloneNotSupportedException e) {
						e.printStackTrace();
					}
				}
				GotResponseFlag = true;
				switch(rxData.type) {
					case ComPackage.TYPE_UPGRADE_FC_ACK:
						Info_lab.setText("飞控固件异常！");
						InfoPanel.setBackground(new Color(250, 0, 0));
						debug_info.setText("fc firmware lost.");
					break;
					case ComPackage.TYPE_ACC_CALIB_ACK:
						int CalibSides = rxData.rData[0];
						int CurrentSide = rxData.rData[1];
						int CurrentProg = (rxData.rData[2] > 100) ? 100 : rxData.rData[2];
						int CalibStepInfo = rxData.rData[3];
						if(CalibStepInfo > 1 && CalibStartedFlag == false) {
							CalibSendEnable = false;
							CalibStartedFlag = true;
							StartButton.setEnabled(false);
							SampleSta_s1.setIcon(new ImageIcon(new ImageIcon(getClass().getResource("drop.gif")).getImage().getScaledInstance(25, 25, Image.SCALE_DEFAULT)));
							SampleSta_s2.setIcon(new ImageIcon(new ImageIcon(getClass().getResource("drop.gif")).getImage().getScaledInstance(25, 25, Image.SCALE_DEFAULT)));
							SampleSta_s3.setIcon(new ImageIcon(new ImageIcon(getClass().getResource("drop.gif")).getImage().getScaledInstance(25, 25, Image.SCALE_DEFAULT)));
							SampleSta_s4.setIcon(new ImageIcon(new ImageIcon(getClass().getResource("drop.gif")).getImage().getScaledInstance(25, 25, Image.SCALE_DEFAULT)));
							SampleSta_s5.setIcon(new ImageIcon(new ImageIcon(getClass().getResource("drop.gif")).getImage().getScaledInstance(25, 25, Image.SCALE_DEFAULT)));
							SampleSta_s6.setIcon(new ImageIcon(new ImageIcon(getClass().getResource("drop.gif")).getImage().getScaledInstance(25, 25, Image.SCALE_DEFAULT)));
						}
						switch(CalibStepInfo) {
							case 0:
								Info_lab.setText("校准未启动");
								InfoPanel.setBackground(new Color(233, 80, 80, 160));
							break;
							case 1:
							break;
							case 2:
								Info_lab.setText("校准初始化");
								InfoPanel.setBackground(new Color(0, 250, 250));//ice blue.
								debug_info.setText("calib init.");
							break;
							case 3:
								Info_lab.setText("温度过低警报");
								InfoPanel.setBackground(new Color(255, 220, 0));//yellow.
							break;
							case 4:
								Info_lab.setText("未静置");
								InfoPanel.setBackground(new Color(255, 220, 0));//yellow.
							break;
							case 5:
								Info_lab.setText("未摆正或已校准");
								InfoPanel.setBackground(new Color(255, 220, 0));//yellow.
							break;
							case 6:
								Info_lab.setText("采样中...");
								InfoPanel.setBackground(new Color(0, 0, 250));//blue.
								switch(CurrentSide) {
									case 1:
										ProgBar_s1.setValue(CurrentProg);
										ProgBar_s1.setString(CurrentProg + "%");
										SampleSta_s1.setIcon(new ImageIcon(new ImageIcon(getClass().getResource("drop.gif")).getImage().getScaledInstance(25, 25, Image.SCALE_DEFAULT)));
									break;
									case 2:
										ProgBar_s2.setValue(CurrentProg);
										ProgBar_s2.setString(CurrentProg + "%");
										SampleSta_s2.setIcon(new ImageIcon(new ImageIcon(getClass().getResource("drop.gif")).getImage().getScaledInstance(25, 25, Image.SCALE_DEFAULT)));
									break;
									case 3:
										ProgBar_s3.setValue(CurrentProg);
										ProgBar_s3.setString(CurrentProg + "%");
										SampleSta_s3.setIcon(new ImageIcon(new ImageIcon(getClass().getResource("drop.gif")).getImage().getScaledInstance(25, 25, Image.SCALE_DEFAULT)));
									break;
									case 4:
										ProgBar_s4.setValue(CurrentProg);
										ProgBar_s4.setString(CurrentProg + "%");
										SampleSta_s4.setIcon(new ImageIcon(new ImageIcon(getClass().getResource("drop.gif")).getImage().getScaledInstance(25, 25, Image.SCALE_DEFAULT)));
									break;
									case 5:
										ProgBar_s5.setValue(CurrentProg);
										ProgBar_s5.setString(CurrentProg + "%");
										SampleSta_s5.setIcon(new ImageIcon(new ImageIcon(getClass().getResource("drop.gif")).getImage().getScaledInstance(25, 25, Image.SCALE_DEFAULT)));
									break;
									case 6:
										ProgBar_s6.setValue(CurrentProg);
										ProgBar_s6.setString(CurrentProg + "%");
										SampleSta_s6.setIcon(new ImageIcon(new ImageIcon(getClass().getResource("drop.gif")).getImage().getScaledInstance(25, 25, Image.SCALE_DEFAULT)));
									break;
								}
								if((CalibSides & 0x01) == 0x01) {
									ProgBar_s1.setValue(100); ProgBar_s1.setString("100%");
									SampleSta_s1.setIcon(new ImageIcon(new ImageIcon(getClass().getResource("dui.png")).getImage().getScaledInstance(25, 25, Image.SCALE_DEFAULT)));
								}
								if((CalibSides & 0x02) == 0x02) {
									ProgBar_s2.setValue(100); ProgBar_s2.setString("100%");
									SampleSta_s2.setIcon(new ImageIcon(new ImageIcon(getClass().getResource("dui.png")).getImage().getScaledInstance(25, 25, Image.SCALE_DEFAULT)));
								}
								if((CalibSides & 0x04) == 0x04) {
									ProgBar_s3.setValue(100); ProgBar_s3.setString("100%");
									SampleSta_s3.setIcon(new ImageIcon(new ImageIcon(getClass().getResource("dui.png")).getImage().getScaledInstance(25, 25, Image.SCALE_DEFAULT)));
								}
								if((CalibSides & 0x08) == 0x08) {
									ProgBar_s4.setValue(100); ProgBar_s4.setString("100%");
									SampleSta_s4.setIcon(new ImageIcon(new ImageIcon(getClass().getResource("dui.png")).getImage().getScaledInstance(25, 25, Image.SCALE_DEFAULT)));
								}
								if((CalibSides & 0x10) == 0x10) {
									ProgBar_s5.setValue(100); ProgBar_s5.setString("100%");
									SampleSta_s5.setIcon(new ImageIcon(new ImageIcon(getClass().getResource("dui.png")).getImage().getScaledInstance(25, 25, Image.SCALE_DEFAULT)));
								}
								if((CalibSides & 0x20) == 0x20) {
									ProgBar_s6.setValue(100); ProgBar_s6.setString("100%");
									SampleSta_s6.setIcon(new ImageIcon(new ImageIcon(getClass().getResource("dui.png")).getImage().getScaledInstance(25, 25, Image.SCALE_DEFAULT)));
								}
								debug_info.setText("Sampling...");
							break;
							case 7:
								Info_lab.setText("计算中...");
								InfoPanel.setBackground(new Color(250, 250, 250));//white.
								debug_info.setText("Computing...");
								if((CalibSides & 0x01) == 0x01)
									SampleSta_s1.setIcon(new ImageIcon(new ImageIcon(getClass().getResource("dui.png")).getImage().getScaledInstance(25, 25, Image.SCALE_DEFAULT)));
								if((CalibSides & 0x02) == 0x02)
									SampleSta_s2.setIcon(new ImageIcon(new ImageIcon(getClass().getResource("dui.png")).getImage().getScaledInstance(25, 25, Image.SCALE_DEFAULT)));
								if((CalibSides & 0x04) == 0x04)
									SampleSta_s3.setIcon(new ImageIcon(new ImageIcon(getClass().getResource("dui.png")).getImage().getScaledInstance(25, 25, Image.SCALE_DEFAULT)));
								if((CalibSides & 0x08) == 0x08)
									SampleSta_s4.setIcon(new ImageIcon(new ImageIcon(getClass().getResource("dui.png")).getImage().getScaledInstance(25, 25, Image.SCALE_DEFAULT)));
								if((CalibSides & 0x10) == 0x10)
									SampleSta_s5.setIcon(new ImageIcon(new ImageIcon(getClass().getResource("dui.png")).getImage().getScaledInstance(25, 25, Image.SCALE_DEFAULT)));
								if((CalibSides & 0x20) == 0x20)
									SampleSta_s6.setIcon(new ImageIcon(new ImageIcon(getClass().getResource("dui.png")).getImage().getScaledInstance(25, 25, Image.SCALE_DEFAULT)));
							break;
							case 8:
								Info_lab.setText("校准失败！");
								InfoPanel.setBackground(new Color(250, 0, 0));//red.
//								CalibStartedFlag = false;
							break;
							case 9:
								Info_lab.setText("校准成功！");
								InfoPanel.setBackground(new Color(0, 250, 0));//green.
//								CalibStartedFlag = false;
							break;
							default: break;
						}
					break;
					default:
						if(CalibStartedFlag == false && CalibSendEnable == false) {
							StartButton.setEnabled(true);
							Info_lab.setText("点击开始启动校准");
							InfoPanel.setBackground(new Color(233, 80, 80, 160));
						}
					break;
				}
			}
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
	}

	private static int SignalLostCnt = 0;
	private class SignalTestThread implements Runnable {
		public void run() {
			while(true) {
				if(GotResponseFlag == false) {
					if(SignalLostCnt < 20)
						SignalLostCnt ++;
					else {
						SignalLostCnt = 0;
						CalibStartedFlag = false;
						Info_lab.setText("连接断开");
						StartButton.setEnabled(false);
						InfoPanel.setBackground(new Color(233, 80, 80, 160));
						ProgBar_s1.setValue(0); ProgBar_s1.setString("0%"); ProgBar_s2.setValue(0); ProgBar_s2.setString("0%");
						ProgBar_s3.setValue(0); ProgBar_s3.setString("0%"); ProgBar_s4.setValue(0); ProgBar_s4.setString("0%");
						ProgBar_s5.setValue(0); ProgBar_s5.setString("0%"); ProgBar_s6.setValue(0); ProgBar_s6.setString("0%");
						SampleSta_s1.setIcon(new ImageIcon(new ImageIcon(getClass().getResource("wait_s.gif")).getImage().getScaledInstance(25, 25, Image.SCALE_DEFAULT)));
						SampleSta_s2.setIcon(new ImageIcon(new ImageIcon(getClass().getResource("wait_s.gif")).getImage().getScaledInstance(25, 25, Image.SCALE_DEFAULT)));
						SampleSta_s3.setIcon(new ImageIcon(new ImageIcon(getClass().getResource("wait_s.gif")).getImage().getScaledInstance(25, 25, Image.SCALE_DEFAULT)));
						SampleSta_s4.setIcon(new ImageIcon(new ImageIcon(getClass().getResource("wait_s.gif")).getImage().getScaledInstance(25, 25, Image.SCALE_DEFAULT)));
						SampleSta_s5.setIcon(new ImageIcon(new ImageIcon(getClass().getResource("wait_s.gif")).getImage().getScaledInstance(25, 25, Image.SCALE_DEFAULT)));
						SampleSta_s6.setIcon(new ImageIcon(new ImageIcon(getClass().getResource("wait_s.gif")).getImage().getScaledInstance(25, 25, Image.SCALE_DEFAULT)));
						debug_info.setText("signal lost.");
						ComPanel.setBackground(new Color(233, 80, 80, 160));
					}
				} else {
					SignalLostCnt = 0;
					GotResponseFlag = false;
					ComPanel.setBackground(new Color(80, 233, 80, 160));
				}
				try {
					TimeUnit.MILLISECONDS.sleep(50);//50ms loop.
				} catch (InterruptedException e) {
					System.err.println("Interrupted");
				}
			}
		}
	}

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
						debug_info.setPreferredSize(new Dimension(280, 30));
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
						debug_info.setPreferredSize(new Dimension(287, 30));
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
			pref.put("_acc_Interface", _Interface);
		}
	};

	private ActionListener sbl = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			if(CalibStartedFlag == false) {
//				CalibStartedFlag = true;
				CalibSendEnable = true;
				StartButton.setEnabled(false);
			}
		}
	};

	WindowAdapter wl = new WindowAdapter() {
		public void windowClosing(WindowEvent e) {
			if(serialPort != null) {
				SerialTool.closePort(serialPort);
			}
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

	public static void main(String args[]) {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Date Today = new Date();
		try {
			Date InvalidDay = df.parse("2018-6-1");
			if(Today.getTime() > InvalidDay.getTime()) {
				System.err.println("System error.");
//				JOptionPane.showMessageDialog(null, "Sorry, Exit With Unknow Error!", "error!", JOptionPane.ERROR_MESSAGE);
				System.exit(0);
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		new AccCalib();
	}
}
