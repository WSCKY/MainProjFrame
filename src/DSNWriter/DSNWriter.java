package DSNWriter;

import java.awt.BorderLayout;
import java.awt.Color;
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
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.prefs.Preferences;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
//import javax.swing.JMenuItem;
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
import DSNGenerator.DSNGenerator;
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

public class DSNWriter extends JFrame {
	private static final long serialVersionUID = 1L;
	Preferences pref = null;

	private String _DroneType = "F1";
	private String _SalesArea = "CN";
	private String _Interface = "Uart";

	DSNGenerator dsnGenerator = null;

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

	private JPanel SNPanel = new JPanel();
	private JTextField DSN_txt = new JTextField(25);
	private JPanel InfoPan = new JPanel();
	private JLabel InfoLab = new JLabel();
	private JPanel BtnPanel = new JPanel();
	private JButton WriteBtn = new JButton("写 入");
	/* 菜单栏 */
	JMenuBar MenuBar = new JMenuBar();
	JMenu setMenu = new JMenu("设置(s)");
	JMenu ItemInterface = new JMenu("接口(i)");
	JMenu ItemDroneType = new JMenu("机型(t)");
	JMenu ItemSalesArea = new JMenu("区域(a)");
	JCheckBoxMenuItem ItemUart = null;
	JCheckBoxMenuItem ItemWifi = null;
	ButtonGroup Interface_bg = new ButtonGroup();

	JCheckBoxMenuItem ItemDroneF1 = null;
	JCheckBoxMenuItem ItemDroneF2 = null;
	ButtonGroup DroneType_bg = new ButtonGroup();

	JCheckBoxMenuItem ItemAreaCN = null;
	JCheckBoxMenuItem ItemAreaEU = null;
	JCheckBoxMenuItem ItemAreaAA = null;
	ButtonGroup Area_bg = new ButtonGroup();

	public DSNWriter() {
		pref = Preferences.userRoot().node(this.getClass().getName());
		_DroneType = pref.get("_dsn_DroneType", "");
		if(_DroneType.equals("")) _DroneType = "F1";
		_SalesArea = pref.get("_dsn_SalesArea", "");
		if(_SalesArea.equals("")) _SalesArea = "CN";
		_Interface = pref.get("_dsn_Interface", "");
		if(_Interface.equals("")) _Interface = "Uart";

		ItemUart = new JCheckBoxMenuItem("串口", _Interface.equals("Uart"));
		ItemWifi = new JCheckBoxMenuItem("Wifi", _Interface.equals("Wifi"));
		ItemDroneF1 = new JCheckBoxMenuItem("F1", _DroneType.equals("F1"));
		ItemDroneF2 = new JCheckBoxMenuItem("F2", _DroneType.equals("F2"));
		ItemAreaCN = new JCheckBoxMenuItem("中国", _SalesArea.equals("CN"));
		ItemAreaEU = new JCheckBoxMenuItem("欧洲", _SalesArea.equals("EU"));
		ItemAreaAA = new JCheckBoxMenuItem("北美", _SalesArea.equals("AA"));

		ItemUart.addActionListener(ifl); ItemWifi.addActionListener(ifl);
		ItemDroneF1.addActionListener(dtl); ItemDroneF2.addActionListener(dtl);
		ItemAreaCN.addActionListener(sal); ItemAreaEU.addActionListener(sal); ItemAreaAA.addActionListener(sal);

		if(_DroneType.equals("F1"))
			dsnGenerator = new DSNGenerator(1);
		else if(_DroneType.equals("F2"))
			dsnGenerator = new DSNGenerator(2);
		dsnGenerator.SetCountry(_SalesArea);

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				setTitle("F1/2序列号写入工具  V2.1.0");
				setSize(600, 280);
				setResizable(false);
				addWindowListener(wl);
				setLocationRelativeTo(null);
				setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				setIconImage(getToolkit().getImage(DSNWriter.class.getResource("gitkitty.png")));

				setJMenuBar(MenuBar);
				MenuBar.add(setMenu);
				setMenu.setMnemonic('s');
				setMenu.setFont(new Font("宋体", Font.PLAIN, 14));
				ItemInterface.setMnemonic('i');
				ItemInterface.setFont(new Font("宋体", Font.PLAIN, 14));
				setMenu.add(ItemInterface);
				setMenu.addSeparator();
				ItemDroneType.setMnemonic('t');
				ItemDroneType.setFont(new Font("宋体", Font.PLAIN, 14));
				setMenu.add(ItemDroneType);
				setMenu.addSeparator();
				ItemSalesArea.setMnemonic('a');
				ItemSalesArea.setFont(new Font("宋体", Font.PLAIN, 14));
				setMenu.add(ItemSalesArea);
				ItemUart.setFont(new Font("宋体", Font.PLAIN, 14));
				Interface_bg.add(ItemUart);
				ItemInterface.add(ItemUart);
				ItemWifi.setFont(new Font("宋体", Font.PLAIN, 14));
				Interface_bg.add(ItemWifi);
				ItemInterface.add(ItemWifi);
				ItemDroneF1.setFont(new Font("宋体", Font.PLAIN, 14));
				DroneType_bg.add(ItemDroneF1);
				ItemDroneType.add(ItemDroneF1);
				ItemDroneF2.setFont(new Font("宋体", Font.PLAIN, 14));
				DroneType_bg.add(ItemDroneF2);
				ItemDroneType.add(ItemDroneF2);

				ItemAreaCN.setFont(new Font("宋体", Font.PLAIN, 14));
				Area_bg.add(ItemAreaCN);
				ItemSalesArea.add(ItemAreaCN);
				ItemAreaEU.setFont(new Font("宋体", Font.PLAIN, 14));
				Area_bg.add(ItemAreaEU);
				ItemSalesArea.add(ItemAreaEU);
				ItemAreaAA.setFont(new Font("宋体", Font.PLAIN, 14));
				Area_bg.add(ItemAreaAA);
				ItemSalesArea.add(ItemAreaAA);

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
				MainPanel.setLayout(new GridLayout(3, 1));

				DSN_txt.setEditable(false);
				DSN_txt.setHorizontalAlignment(JTextField.CENTER);
				DSN_txt.setFont(new Font("Courier New", Font.BOLD, 32));
				SNPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 10));
				SNPanel.add(DSN_txt); MainPanel.add(SNPanel);
				InfoLab.setText("机型: " + _DroneType + " - 区域: " + GetAreaName(_SalesArea));
				InfoLab.setFont(new Font("楷体", Font.BOLD, 30));
				InfoPan.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 10));
				InfoPan.add(InfoLab); MainPanel.add(InfoPan);
				WriteBtn.setEnabled(false);
				WriteBtn.addActionListener(ubl);
				WriteBtn.setFont(new Font("黑体", Font.BOLD, 30));
				WriteBtn.setPreferredSize(new Dimension(280, 46));
				getRootPane().setDefaultButton(WriteBtn);
				BtnPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
				BtnPanel.add(WriteBtn); MainPanel.add(BtnPanel);
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
		new Thread(new TxDataThread()).start();
		new Thread(new RepaintThread()).start();
		new Thread(new SignalTestThread()).start();
	}

	private boolean VersionReqFlag = true;
	private boolean WriteNewDSNFlag = false;
	private static int _wDSN_CmdTog = 0;
	private byte HeartbatCnt = 0;
	private class TxDataThread implements Runnable {
		public void run() {
			while(true) {
				if(VersionReqFlag) {
					txData.type = ComPackage.TYPE_VERSION_REQUEST;
					txData.addByte((byte)0x0F, 0);
					txData.setLength(3);
				} else if(WriteNewDSNFlag) {
					if(_wDSN_CmdTog % 2 == 0) {
						txData.type = ComPackage.TYPE_DSN_UPDATE;
						txData.addBytes(_NewDSN.getBytes(), 16, 0);
						txData.addByte((byte)0xBB, 16);
						txData.setLength(19);
					} else {
						txData.type = ComPackage.TYPE_VERSION_REQUEST;
						txData.addByte((byte)0x0F, 0);
						txData.setLength(3);
					}
					_wDSN_CmdTog ++;
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
				if(rxData.type == ComPackage.TYPE_UPGRADE_FC_ACK) {
					debug_info.setText("fc firmware lost.");
				} else if(rxData.type == ComPackage.TYPE_VERSION_Response) {
					VersionReqFlag = false;
					char ver = rxData.readoutCharacter(0);
					debug_info.setText("Version: V" + (ver >> 12) + "." + ((ver >> 8) & 0x0F) + "." + (ver & 0x00FF));
					String curDSN = rxData.readoutString(4, 16);
					DSN_txt.setText(curDSN);
					if(curDSN.equals("PXyyMMwwxxxxFn##")) {
						WriteBtn.setEnabled(true);
					} else {
						WriteBtn.setEnabled(false);
					}
					if(WriteNewDSNFlag) {
						if(curDSN.equals(_NewDSN)) {
							WriteNewDSNFlag = false;
							WriteBtn.setEnabled(false);
							dsnGenerator.SaveThisDSN();
						}
					}
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
						VersionReqFlag = true;
						WriteBtn.setEnabled(false);
						DSN_txt.setText("");
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

	private String _NewDSN = "PXyyMMwwxxxxFn##";
	private ActionListener ubl = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			WriteNewDSNFlag = true;
			WriteBtn.setEnabled(false);
			_NewDSN = dsnGenerator.GotNewDSN();
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
			pref.put("_dsn_Interface", _Interface);
		}
	};
	private ActionListener dtl = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			dsnGenerator.GeneratorClose();
			dsnGenerator = null;
			if(ItemDroneF1.isSelected()) {
				_DroneType = "F1";
				dsnGenerator = new DSNGenerator(1);
			}
			if(ItemDroneF2.isSelected()) {
				_DroneType = "F2";
				dsnGenerator = new DSNGenerator(2);
			}
			if(dsnGenerator != null)
				dsnGenerator.SetCountry(_SalesArea);

			InfoLab.setText("机型: " + _DroneType + " - 区域: " + GetAreaName(_SalesArea));
			pref.put("_dsn_DroneType", _DroneType);
		}
	};
	private ActionListener sal = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			if(ItemAreaCN.isSelected()) _SalesArea = "CN";
			if(ItemAreaEU.isSelected()) _SalesArea = "EU";
			if(ItemAreaAA.isSelected()) _SalesArea = "AA";
			dsnGenerator.SetCountry(_SalesArea);
			InfoLab.setText("机型: " + _DroneType + " - 区域: " + GetAreaName(_SalesArea));
			pref.put("_dsn_SalesArea", _SalesArea);
		}
	};

	WindowAdapter wl = new WindowAdapter() {
		public void windowClosing(WindowEvent e) {
			dsnGenerator.GeneratorClose();
			if(serialPort != null) {
				SerialTool.closePort(serialPort);
			}
			System.exit(0);
		}
	};

	private class RepaintThread implements Runnable {
		public void run() {
			while(true) {
//				repaint();
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

	private static String GetAreaName(String n) {
		String Name = null;
		if(n.equals("CN")) Name = "中国";
		if(n.equals("EU")) Name = "欧洲";
		if(n.equals("AA")) Name = "北美";
		return Name;
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
		Preferences p = Preferences.userRoot().node("/common");
		String allow = p.get("_dsn_allowed", "");
		if(!allow.equals("kyChuPermitted")) {
			JOptionPane.showMessageDialog(null, "抱歉，此电脑未获得序列号写入许可！", "错误！", JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}
		new DSNWriter();
	}
}
