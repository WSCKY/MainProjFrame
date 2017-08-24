package BaroCheck;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
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

public class BaroCheck extends JFrame{
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
	private JLabel BaroLabel = new JLabel();
	/* 菜单栏 */
	JMenuBar MenuBar = new JMenuBar();
	JMenu setMenu = new JMenu("设置(s)");
	JMenu ItemInterface = new JMenu("接口(i)");
	JCheckBoxMenuItem ItemUart = null;
	JCheckBoxMenuItem ItemWifi = null;
	ButtonGroup Interface_bg = new ButtonGroup();

	public BaroCheck() {
		pref = Preferences.userRoot().node(this.getClass().getName());
		_Interface = pref.get("_baro_Interface", "");
		if(_Interface.equals("")) _Interface = "Uart";

		ItemUart = new JCheckBoxMenuItem("串口", _Interface.equals("Uart"));
		ItemWifi = new JCheckBoxMenuItem("Wifi", _Interface.equals("Wifi"));
		ItemUart.addActionListener(ifl); ItemWifi.addActionListener(ifl);

		setTitle("F1/2气压计板检测工具  V1.2.1");
		setSize(600, 380);
		setResizable(false);
		addWindowListener(wl);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setIconImage(getToolkit().getImage(BaroCheck.class.getResource("Tool.png")));

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
		MainPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 15));
		BaroLabel.setIcon(new ImageIcon(new ImageIcon(getClass().getResource("discon.png")).getImage().getScaledInstance(256, 256, Image.SCALE_DEFAULT)));
		MainPanel.add(BaroLabel);
		add(MainPanel, BorderLayout.CENTER);

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
				{
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
				debug_info.setText("connected.");
				switch(rxData.type) {
					case ComPackage.TYPE_FC_Response:
						if((rxData.rData[1] & 0x02) == 0x02) {
							BaroLabel.setIcon(new ImageIcon(new ImageIcon(getClass().getResource("2.png")).getImage().getScaledInstance(256, 256, Image.SCALE_DEFAULT)));
						} else {
							BaroLabel.setIcon(new ImageIcon(new ImageIcon(getClass().getResource("1.png")).getImage().getScaledInstance(256, 256, Image.SCALE_DEFAULT)));
						}
					break;
					default: break;
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
						debug_info.setText("signal lost.");
						ComPanel.setBackground(new Color(233, 80, 80, 160));
						BaroLabel.setIcon(new ImageIcon(new ImageIcon(getClass().getResource("discon.png")).getImage().getScaledInstance(256, 256, Image.SCALE_DEFAULT)));
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
			pref.put("_baro_Interface", _Interface);
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
		new BaroCheck();
	}
}
