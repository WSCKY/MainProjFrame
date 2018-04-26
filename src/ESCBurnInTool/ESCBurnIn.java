package ESCBurnInTool;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
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
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.prefs.Preferences;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSlider;
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
import protocol.PackageTypes.TypePartnerX;
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

public class ESCBurnIn extends JFrame{
	private static final long serialVersionUID = 1L;

	Preferences pref = null;
	private String _Interface = "Uart";
	private String _BurnInDuty = "6";
	private String _BurnInTime = "3";

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
	private JPanel ProgPanel = new JPanel();
	private JPanel BtnsPanel = new JPanel();
	private JProgressBar ESCBurnInBar = new JProgressBar(0, 100);
	private JButton MotorStartBtn = new JButton("启动");
	private JButton StartBurnInBtn = new JButton("开始");
	private JButton StopBurnInBtn = new JButton("停止");

	private JDialog ParamSetDialog = new JDialog(this);
	private JPanel SliderPanel = new JPanel();
	private JSlider ValueSlider = new JSlider(1, 10);
	private JPanel confirmBtnPanel = new JPanel();
	private JButton ConfirmButton = new JButton("确认");
	Dictionary<Integer, Component> labelTable = new Hashtable<Integer, Component>();
	/* 菜单栏 */
	JMenuBar MenuBar = new JMenuBar();
	JMenu setMenu = new JMenu("设置(s)");
	JMenu ItemInterface = new JMenu("接口(i)");
	JMenu ItemParameter = new JMenu("参数(p)");
	JCheckBoxMenuItem ItemUart = null;
	JCheckBoxMenuItem ItemWifi = null;
	JMenuItem ItemDuty = new JMenuItem("转速");
	JMenuItem ItemTime = new JMenuItem("时间");
	ButtonGroup Interface_bg = new ButtonGroup();

	public ESCBurnIn() {
		pref = Preferences.userRoot().node(this.getClass().getName());
		_Interface = pref.get("_burn_Interface", "");
		if(_Interface.equals("")) _Interface = "Uart";
		_BurnInDuty = pref.get("_burn_Duty", "");
		if(_BurnInDuty.equals("")) _BurnInDuty = "6";
		_BurnInTime = pref.get("_burn_Time", "");
		if(_BurnInTime.equals("")) _BurnInTime = "3";
		BurnInDuty = Integer.valueOf(_BurnInDuty) * 10;
		BurnInTime = Integer.valueOf(_BurnInTime) * 60000;

		ItemUart = new JCheckBoxMenuItem("串口", _Interface.equals("Uart"));
		ItemWifi = new JCheckBoxMenuItem("Wifi", _Interface.equals("Wifi"));
		ItemUart.addActionListener(ifl); ItemWifi.addActionListener(ifl);
		ItemDuty.addActionListener(spl); ItemTime.addActionListener(spl);

		setTitle("F1/2调速器老化工具  V1.1.0");
		setSize(600, 220);
		setResizable(false);
		addWindowListener(wl);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setIconImage(getToolkit().getImage(ESCBurnIn.class.getResource("Motor.png")));

		setJMenuBar(MenuBar);
		MenuBar.add(setMenu);
		setMenu.setMnemonic('s');
		setMenu.setFont(new Font("宋体", Font.PLAIN, 14));
		ItemInterface.setMnemonic('i');
		ItemInterface.setFont(new Font("宋体", Font.PLAIN, 14));
		ItemParameter.setMnemonic('p');
		ItemParameter.setFont(new Font("宋体", Font.PLAIN, 14));
		setMenu.add(ItemInterface); setMenu.add(ItemParameter);
		ItemUart.setFont(new Font("宋体", Font.PLAIN, 14));
		Interface_bg.add(ItemUart);
		ItemInterface.add(ItemUart);
		ItemWifi.setFont(new Font("宋体", Font.PLAIN, 14));
		Interface_bg.add(ItemWifi);
		ItemInterface.add(ItemWifi);
		ItemParameter.add(ItemDuty);
		ItemParameter.add(ItemTime);

//		ValueSlider.setMajorTickSpacing(20);
//		ValueSlider.setMinorTickSpacing(5);
		ValueSlider.setPaintLabels(true);
		labelTable.put(1, new JLabel("10%")); labelTable.put(2, new JLabel("20%")); labelTable.put(3, new JLabel("30%"));
		labelTable.put(4, new JLabel("40%")); labelTable.put(5, new JLabel("50%")); labelTable.put(6, new JLabel("60%"));
		labelTable.put(7, new JLabel("70%")); labelTable.put(8, new JLabel("80%")); labelTable.put(9, new JLabel("90%"));
		labelTable.put(10, new JLabel("100%"));
		ValueSlider.setLabelTable(labelTable);
		ValueSlider.setPreferredSize(new Dimension(380, 100));
		SliderPanel.add(ValueSlider);
		ConfirmButton.setPreferredSize(new Dimension(200, 40));
		ConfirmButton.setFont(new Font("宋体", Font.BOLD, 20));
		ConfirmButton.addActionListener(cbl);
		confirmBtnPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 15));
		confirmBtnPanel.add(ConfirmButton);
		ParamSetDialog.setTitle("设置");
		ParamSetDialog.setLayout(new GridLayout(2, 1, 0, 0));
		ParamSetDialog.add(SliderPanel);
		ParamSetDialog.add(confirmBtnPanel);
//		ParamSetDialog.setResizable(false);
		ParamSetDialog.setSize(400, 180);
		ParamSetDialog.setLocationRelativeTo(null);
		ParamSetDialog.setModal(true);
		ParamSetDialog.getRootPane().setDefaultButton(ConfirmButton);

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
			debug_info.setPreferredSize(new Dimension(280, 30));
			ComPanel.add(debug_info);
		} else if(_Interface.equals("Wifi")) {
			ComPanel.add(ip_lab);
			ComPanel.add(IP_Txt);
			ComPanel.add(port_lab);
			ComPanel.add(Port_Txt);
			debug_info.setPreferredSize(new Dimension(287, 30));
			ComPanel.add(debug_info);
		}
		add(ComPanel, BorderLayout.NORTH);

		ESCBurnInBar.setPreferredSize(new Dimension(540, 34));
		ESCBurnInBar.setStringPainted(true); ESCBurnInBar.setString("");
		ProgPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 25));
		ProgPanel.add(ESCBurnInBar);
		MotorStartBtn.setPreferredSize(new Dimension(120, 36));
		StartBurnInBtn.setPreferredSize(new Dimension(120, 36));
		StopBurnInBtn.setPreferredSize(new Dimension(120, 36));
		MotorStartBtn.setFont(new Font("宋体", Font.BOLD, 20));
		StartBurnInBtn.setFont(new Font("宋体", Font.BOLD, 20));
		StopBurnInBtn.setFont(new Font("宋体", Font.BOLD, 20));
		MotorStartBtn.addActionListener(mstartl);
		StartBurnInBtn.addActionListener(bstartl); StopBurnInBtn.addActionListener(bstopl);
		MotorStartBtn.setEnabled(false); StartBurnInBtn.setEnabled(false); StopBurnInBtn.setEnabled(false);
		BtnsPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 40, 15));
		BtnsPanel.add(MotorStartBtn); BtnsPanel.add(StartBurnInBtn); BtnsPanel.add(StopBurnInBtn);

		MainPanel.setLayout(new GridLayout(2, 1, 0, 0));
		MainPanel.add(ProgPanel); MainPanel.add(BtnsPanel);
		add(MainPanel, BorderLayout.CENTER);
		getRootPane().setDefaultButton(MotorStartBtn);

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

	private byte HeartbatCnt = 0;
	private class TxDataThread implements Runnable {
		public void run() {
			while(true) {
				if(ESCBurnInRunningFlag == true) {
					txData.type = TypePartnerX.TYPE_ESC_BURN_IN_TEST;
					txData.addByte(ESCBurnExpSpeed, 0);
					txData.addByte((byte)(ESCBurnExpSpeed ^ 0xCC), 1);
					txData.setLength(4);
				} else {
					txData.type = TypePartnerX.TYPE_FC_APP_HEARTBEAT;
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
					case TypePartnerX.TYPE_UPGRADE_FC_ACK:
						debug_info.setText("fc firmware lost.");
					break;
					default:
						debug_info.setText("connected.");
						StopBurnInBtn.setEnabled(true);
						if(ESCBurnInRunningFlag == false) {
							MotorStartBtn.setEnabled(true);
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

						ESCBurnExpSpeed = 0;
						ESCBurnInBar.setString("");
						StartBurnInBtn.setEnabled(false);
						ESCBurnInRunningFlag = false;
						ESCBurnInStartFlag = false;

						MotorStartBtn.setEnabled(false);
						StartBurnInBtn.setEnabled(false);
						StopBurnInBtn.setEnabled(false);
						ESCBurnInBar.setValue(0);
						ESCBurnInBar.setString("");
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

	private static boolean ESCBurnInRunningFlag = false;
	private static boolean MotorStartFlag = false;
	private ActionListener mstartl = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			if(ESCBurnInRunningFlag == false) {
				ESCBurnInRunningFlag = true;
				MotorStartFlag = true;
				ESCBurnExpSpeed = 0;
				MotorStartBtn.setEnabled(false);
				StartBurnInBtn.setEnabled(true);
				ESCBurnInBar.setString("怠速准备...");
			}
		}
	};

	private static boolean ESCBurnInStartFlag = false;
	private Thread bThread = null;
	private ActionListener bstartl = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			if(MotorStartFlag == true) {
				ESCBurnInStartFlag = true;
				StartBurnInBtn.setEnabled(false);
				bThread = new Thread(new ESCBurnInThread());
				bThread.start();
			}
		}
	};

	private byte ESCBurnExpSpeed = 0;
	private static long BurnInTime = 180000;
	private static int BurnInDuty = 60;
	private static final int StartUpTime = 30;
	private class ESCBurnInThread implements Runnable {
		public void run() {
			int TimeCnt = 0;
			long TimeStart = 0;
			while(ESCBurnInStartFlag) {
				if(TimeCnt < StartUpTime) {
					TimeCnt ++;
					ESCBurnInBar.setValue(TimeCnt * 100 / StartUpTime);
					ESCBurnInBar.setString("提速中...");
					TimeStart = System.currentTimeMillis();
				} else if((System.currentTimeMillis() - TimeStart) <= BurnInTime) { //3min
					long t = (System.currentTimeMillis() - TimeStart);
					int min = (int) (t / 60000);
					int sec = (int) ((t % 60000) / 1000);
					ESCBurnInBar.setString(min + "分" + sec + "秒");
					ESCBurnInBar.setValue((int) ((System.currentTimeMillis() - TimeStart) / (BurnInTime / 100)));
				} else {
					ESCBurnInStartFlag = false;
				}
				ESCBurnExpSpeed = (byte) (TimeCnt * BurnInDuty / StartUpTime);
				try {
					TimeUnit.MILLISECONDS.sleep(100);//100ms loop.
				} catch (InterruptedException e) {
					bThread = null;
					System.err.println("Interrupted");
				}
			}
			ESCBurnInBar.setValue(0);
			while(TimeCnt > 0) {
				TimeCnt --;
				ESCBurnInBar.setValue(TimeCnt * 100 / StartUpTime);
				ESCBurnInBar.setString("降速中...");
				ESCBurnExpSpeed = (byte) (TimeCnt * BurnInDuty / StartUpTime);
				try {
					TimeUnit.MILLISECONDS.sleep(100);//100ms loop.
				} catch (InterruptedException e) {
					bThread = null;
					System.err.println("Interrupted");
				}
			}
			ESCBurnInBar.setString("");
			ESCBurnInRunningFlag = false;
			bThread = null;
		}
	}

	private ActionListener bstopl = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			if(ESCBurnInStartFlag == true || ESCBurnExpSpeed > 0) {
				ESCBurnInStartFlag = false;
			} else {
				ESCBurnInBar.setString("");
				StartBurnInBtn.setEnabled(false);
				ESCBurnInRunningFlag = false;
			}
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

	private String SetParamItem = "duty";
	private ActionListener cbl = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			int d = ValueSlider.getValue();
			if(SetParamItem.equals("duty")) {
				_BurnInDuty = String.valueOf(d);
//				System.out.println(_BurnInDuty);
//				System.out.println("转速 = " + d);
				BurnInDuty = d * 10;
			} else if(SetParamItem.equals("time")) {
				_BurnInTime = String.valueOf(d);
//				System.out.println(_BurnInTime);
//				System.out.println("时间  = " + ValueSlider.getValue());
				BurnInTime = d * 60000;
			}
			ParamSetDialog.setVisible(false);
			pref.put("_burn_Duty", _BurnInDuty);
			pref.put("_burn_Time", _BurnInTime);
		}
	};

	private ActionListener spl = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			if(ESCBurnInRunningFlag == false) {
				String name = ((JMenuItem)e.getSource()).getText();
				ParamSetDialog.setTitle("设置" + name);
				if(name.equals("转速")) {
					labelTable.put(1, new JLabel("10%")); labelTable.put(2, new JLabel("20%")); labelTable.put(3, new JLabel("30%"));
					labelTable.put(4, new JLabel("40%")); labelTable.put(5, new JLabel("50%")); labelTable.put(6, new JLabel("60%"));
					labelTable.put(7, new JLabel("70%")); labelTable.put(8, new JLabel("80%")); labelTable.put(9, new JLabel("90%"));
					labelTable.put(10, new JLabel("100%"));
					ValueSlider.setValue(Integer.valueOf(_BurnInDuty));
					SetParamItem = "duty";
				} else if(name.equals("时间")) {
					labelTable.put(1, new JLabel("1min")); labelTable.put(2, new JLabel("2min")); labelTable.put(3, new JLabel("3min"));
					labelTable.put(4, new JLabel("4min")); labelTable.put(5, new JLabel("5min")); labelTable.put(6, new JLabel("6min"));
					labelTable.put(7, new JLabel("7min")); labelTable.put(8, new JLabel("8min")); labelTable.put(9, new JLabel("9min"));
					labelTable.put(10, new JLabel("10min"));
					ValueSlider.setValue(Integer.valueOf(_BurnInTime));
					SetParamItem = "time";
				}
				ValueSlider.setLabelTable(labelTable);
				ValueSlider.validate();
				ParamSetDialog.setVisible(true);
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
			pref.put("_burn_Interface", _Interface);
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

	public static void main(String[] args) {
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
		new ESCBurnIn();
	}
}
