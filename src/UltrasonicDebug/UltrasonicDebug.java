package UltrasonicDebug;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
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

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
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
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

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
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import protocol.ComPackage;
import protocol.RxAnalyse;

public class UltrasonicDebug extends JFrame {
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
	private JPanel CtrlPanel = new JPanel();
	private JPanel BtnsPanel = new JPanel();
	private JButton CmdSendBtn = new JButton("发送配置");

	private JLabel DisLab = new JLabel("0.0cm");

	private JSlider ValueSlider = new JSlider(0, 127);
	Dictionary<Integer, Component> labelTable = new Hashtable<Integer, Component>();
	private JLabel SliderValLab = new JLabel("000");
	Dictionary<Integer, Component> AdjPercentTab = new Hashtable<Integer, Component>();
	private JSlider DeadbandSlider = new JSlider(0, 100);
	private JLabel DeadbandValLab = new JLabel("46000");
	private JCheckBox ASW_EN = new JCheckBox("固定增益");
	private JCheckBox SND_EN = new JCheckBox("发送使能");
	private JCheckBox AutoSND_EN = new JCheckBox("自动发送");

	/* 菜单栏 */
	JMenuBar MenuBar = new JMenuBar();
	JMenu setMenu = new JMenu("设置(s)");
	JMenu ItemInterface = new JMenu("接口(i)");
	JCheckBoxMenuItem ItemUart = null;
	JCheckBoxMenuItem ItemWifi = null;
	ButtonGroup Interface_bg = new ButtonGroup();

	public UltrasonicDebug() {
		pref = Preferences.userRoot().node(this.getClass().getName());
		_Interface = pref.get("_UltrasonicDebug_IF", "");
		if(_Interface.equals("")) _Interface = "Uart";

		ItemUart = new JCheckBoxMenuItem("串口", _Interface.equals("Uart"));
		ItemWifi = new JCheckBoxMenuItem("Wifi", _Interface.equals("Wifi"));
		ItemUart.addActionListener(ifl); ItemWifi.addActionListener(ifl);

		setTitle("超声波调试工具 V0.0.1");
		setSize(600, 460);
		setResizable(false);
		addWindowListener(wl);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

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

		CtrlPanel.setLayout(new GridLayout(4, 1, 0, 0));

		DisLab.setFont(new Font("宋体", Font.BOLD, 40));
		JPanel p3 = new JPanel(); p3.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 10));
		p3.add(DisLab); CtrlPanel.add(p3);

		ValueSlider.setPaintLabels(true);
		ValueSlider.setValue(0);
		labelTable.put(0, new JLabel("000"));
		labelTable.put(64, new JLabel("064"));
		labelTable.put(127, new JLabel("128"));
		ValueSlider.setLabelTable(labelTable);
		ValueSlider.addChangeListener(scl);
		ValueSlider.setCursor(new Cursor(Cursor.HAND_CURSOR));
		ValueSlider.setPreferredSize(new Dimension(500, 50));
//		ValueSlider.setBorder(BorderFactory.createLineBorder(Color.RED));
		SliderValLab.setFont(SliderValLab.getFont().deriveFont(Font.BOLD, 28));
//		SliderValLab.setBorder(BorderFactory.createLineBorder(Color.RED));
		JPanel p = new JPanel(); p.setLayout(new FlowLayout(FlowLayout.CENTER, 12, 0));
		p.add(ValueSlider); p.add(SliderValLab);
		p.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "动态增益", TitledBorder.LEFT, TitledBorder.TOP));
		CtrlPanel.add(p);
		DeadbandSlider.setPaintLabels(true); DeadbandSlider.setValue(50);
		AdjPercentTab.put(0, new JLabel("23000"));
		AdjPercentTab.put(50, new JLabel("46000"));
		AdjPercentTab.put(100, new JLabel("69000")); DeadbandSlider.setLabelTable(AdjPercentTab);
		DeadbandSlider.addChangeListener(scl);
		DeadbandSlider.setPreferredSize(new Dimension(470, 50));
		DeadbandValLab.setFont(DeadbandValLab.getFont().deriveFont(Font.BOLD, 28));
		JPanel p1 = new JPanel(); p1.setLayout(new FlowLayout(FlowLayout.CENTER, 12, 0));
		p1.add(DeadbandSlider); p1.add(DeadbandValLab);
		p1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "死区时间", TitledBorder.LEFT, TitledBorder.TOP));
		CtrlPanel.add(p1);

		ASW_EN.setFont(new Font("宋体", Font.BOLD, 24)); ASW_EN.setSelected(false);
		SND_EN.setFont(new Font("宋体", Font.BOLD, 24)); SND_EN.setSelected(true);
		AutoSND_EN.setFont(new Font("宋体", Font.BOLD, 24)); AutoSND_EN.setSelected(false); AutoSND_EN.addChangeListener(asl);
		JPanel p2 = new JPanel(); p2.setLayout(new FlowLayout(FlowLayout.CENTER, 65, 35));
		p2.add(ASW_EN); p2.add(SND_EN); p2.add(AutoSND_EN);
		CtrlPanel.add(p2);

		CmdSendBtn.setPreferredSize(new Dimension(160, 40));
		CmdSendBtn.setFont(new Font("宋体", Font.BOLD, 20));
		CmdSendBtn.addActionListener(csl);
		CmdSendBtn.setEnabled(true);
		BtnsPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 20));
		BtnsPanel.add(CmdSendBtn);

		MainPanel.setLayout(new BorderLayout(0, 0));
		MainPanel.add(CtrlPanel, BorderLayout.CENTER); MainPanel.add(BtnsPanel, BorderLayout.SOUTH);
		add(MainPanel, BorderLayout.CENTER);
		getRootPane().setDefaultButton(CmdSendBtn);

		Toolkit.getDefaultToolkit().addAWTEventListener(new AWTEventListener() {
			public void eventDispatched(AWTEvent event) {
				if(((KeyEvent)event).getID()==KeyEvent.KEY_PRESSED) {
//					System.out.println("Pressed: " + KeyEvent.getKeyText(((KeyEvent)event).getKeyCode()));
					switch(((KeyEvent)event).getKeyCode()) {
					case KeyEvent.VK_1:
						ASW_EN.setSelected(!ASW_EN.isSelected());
					break;
					case KeyEvent.VK_2:
						SND_EN.setSelected(!SND_EN.isSelected());
					break;
					case KeyEvent.VK_3:
						AutoSND_EN.setSelected(!AutoSND_EN.isSelected());
					break;
					case KeyEvent.VK_LEFT:
					case KeyEvent.VK_DOWN:
						if(!ValueSlider.hasFocus() && !DeadbandSlider.hasFocus()) {
							if(ValueSlider.getValue() > 0)
								ValueSlider.setValue(ValueSlider.getValue() - 1);
						}
					break;
					case KeyEvent.VK_RIGHT:
					case KeyEvent.VK_UP:
						if(!ValueSlider.hasFocus() && !DeadbandSlider.hasFocus()) {
							if(ValueSlider.getValue() < 127)
								ValueSlider.setValue(ValueSlider.getValue() + 1);
						}
					break;
					case KeyEvent.VK_A:
					case KeyEvent.VK_S:
						if(DeadbandSlider.getValue() > 0)
							DeadbandSlider.setValue(DeadbandSlider.getValue() - 1);
					break;
					case KeyEvent.VK_W:
					case KeyEvent.VK_D:
						if(DeadbandSlider.getValue() < 100)
							DeadbandSlider.setValue(DeadbandSlider.getValue() + 1);
					break;
					}
				}
			}
		}, AWTEvent.KEY_EVENT_MASK);

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

	private byte[] CreateSendBuffer() {
		txData.type = ComPackage.TYPE_DEBUG_CMD;
		txData.addByte((byte) ValueSlider.getValue(), 0);
		txData.addByte((byte) (ASW_EN.isSelected() ? 1 : 0), 1);
		txData.addByte((byte) (SND_EN.isSelected() ? 1 : 0), 2);
		txData.addInteger(DeadbandSlider.getValue() * 460 + 23000, 3);
		txData.addInteger(0, 7);
		txData.setLength(13);
		return txData.getSendBuffer();
	}

	private class TxDataThread implements Runnable {
		public void run() {
			while(true) {
				if(AutoSND_EN.isSelected()) {
					byte[] SendBuffer = CreateSendBuffer();//txData.getSendBuffer();
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
						debug_info.setText("fc firmware lost.");
					break;
					case ComPackage.TYPE_DIST_RAW_DAT:
						if(rxData.rData[0] == (byte)0xAE)
							DisLab.setText(String.format("%.1fcm", rxData.readoutFloat(1)));
					break;
					default:
						debug_info.setText("connected.");
					break;
				}
			}
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
	}

	private ChangeListener asl = new ChangeListener() {
		public void stateChanged(ChangeEvent e) {
			if((JCheckBox)e.getSource() == AutoSND_EN) {
				if(AutoSND_EN.isSelected())
					CmdSendBtn.setEnabled(false);
				else
					CmdSendBtn.setEnabled(true);
			}
		}
	};

	private ActionListener csl = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			byte[] SendBuffer = CreateSendBuffer();
			if(_Interface.equals("Wifi") && CommSocket != null) {
				DatagramPacket packet = new DatagramPacket(SendBuffer, 0, SendBuffer.length, new InetSocketAddress(CommIP, CommPort));
				try {
					CommSocket.send(packet);
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			} else if(_Interface.equals("Uart") && serialPort != null) {
				try {
					SerialTool.sendToPort(serialPort, SendBuffer);
				} catch (SendDataToSerialPortFailure ex) {
					ex.printStackTrace();
				} catch (SerialPortOutputStreamCloseFailure ex) {
					ex.printStackTrace();
				}
			} else {
				JOptionPane.showMessageDialog(null, "请先建立连接!", "ERROR", JOptionPane.ERROR_MESSAGE);
			}
		}
	};

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

	private ChangeListener scl = new ChangeListener() {
		public void stateChanged(ChangeEvent e) {
			if((JSlider)e.getSource() == ValueSlider) {
				SliderValLab.setText(String.format("%03d", ValueSlider.getValue()));
			} else if((JSlider)e.getSource() == DeadbandSlider) {
				DeadbandValLab.setText(String.format("%d", DeadbandSlider.getValue() * 460 + 23000));
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
			pref.put("_UltrasonicDebug_IF", _Interface);
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
				System.exit(0);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		try {
            UIManager.setLookAndFeel(
                UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Couldn't use system look and feel.");
        }
		new UltrasonicDebug();
	}
}
