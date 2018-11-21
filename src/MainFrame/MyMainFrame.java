package MainFrame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
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
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

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
import protocol.decoder.myProtocolDecoder;
import protocol.event.DecodeEvent;
import protocol.event.DecodeEventListener;

public class MyMainFrame extends JFrame implements SerialPortEventListener, DecodeEventListener {
	private static final long serialVersionUID = 1L;

	private static int FrameWidth = 600;
	private static int FrameHeight = 400;
	private static String FrameTitle = "Main Frame";
	public void setWidth(int Width) {FrameWidth = Width; this.setSize(FrameWidth, FrameHeight);}
	public void setHeight(int Height) {FrameHeight = Height; this.setSize(FrameWidth, FrameHeight);}
	public void setFrameSize(int Width, int Height) {FrameWidth = Width; FrameHeight = Height; this.setSize(FrameWidth, FrameHeight);}

	/* Communication */
	Preferences pref = null;
	private String _Interface = "Uart";

//	private static ComPackage rxData = new ComPackage();
	private static ComPackage txData = new ComPackage();
	private static myProtocolDecoder decoder = null;

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
	/* 菜单栏 */
	private JMenuBar MenuBar = new JMenuBar();
	private JMenu setMenu = new JMenu("设置(s)");
	private JMenu ItemInterface = new JMenu("接口(i)");
	private JCheckBoxMenuItem ItemUart = null;
	private JCheckBoxMenuItem ItemWifi = null;
	private ButtonGroup Interface_bg = new ButtonGroup();
	/* 主面板 */
	private JPanel UsrMainPanel = new JPanel();

	public JPanel getUsrMainPanel() {return UsrMainPanel;}
	public MyMainFrame() {this.MainFrameInit();}
	public MyMainFrame(int Width, int Height) {
		FrameWidth = Width;
		FrameHeight = Height;
		this.MainFrameInit();
	}
	public MyMainFrame(int Width, int Height, String Title) {
		FrameWidth = Width;
		FrameHeight = Height;
		FrameTitle = Title;
		this.MainFrameInit();
	}
	private void MainFrameInit() {
		pref = Preferences.userRoot().node(this.getClass().getName());
		_Interface = pref.get("_MyMainFrame_IF", "");
		if(_Interface.equals("")) _Interface = "Uart";

		ItemUart = new JCheckBoxMenuItem("串口", _Interface.equals("Uart"));
		ItemWifi = new JCheckBoxMenuItem("Wifi", _Interface.equals("Wifi"));
		ItemUart.addActionListener(ifl); ItemWifi.addActionListener(ifl);

		this.setTitle(FrameTitle);
		this.setSize(FrameWidth, FrameHeight);
		this.setResizable(false);
		this.addWindowListener(wl);
		this.addComponentListener(wcl);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

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
			debug_info.setPreferredSize(new Dimension(FrameWidth - 320, 30));
			ComPanel.add(debug_info);
		} else if(_Interface.equals("Wifi")) {
			ComPanel.add(ip_lab);
			ComPanel.add(IP_Txt);
			ComPanel.add(port_lab);
			ComPanel.add(Port_Txt);
			debug_info.setPreferredSize(new Dimension(FrameWidth - 313, 30));
			ComPanel.add(debug_info);
		}
		this.add(ComPanel, BorderLayout.NORTH);
		this.add(UsrMainPanel, BorderLayout.CENTER);

		decoder = new myProtocolDecoder();
		decoder.addDecodeListener(this);
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

	public void setDebugString(String s) {
		debug_info.setText(s);
	}

	private static boolean AutoTxEnable = false;
	private static int AutoTxTimeDelay = 100;
	public ComPackage getTxPackage() { return txData; }
	public myProtocolDecoder getDecoder() { return decoder; }
	public void setAutoTxEnable(boolean en) {AutoTxEnable = en;}
	public void setAutoTxDelayTicks(int ticks) {AutoTxTimeDelay = ticks;}
	public void addDecodeEventListener(DecodeEventListener listener) {
		decoder.addDecodeListener(listener);
	}

	private byte HeartbatCnt = 0;
	public byte[] CreateSendBuffer() {
		txData.type = ComPackage.TYPE_COM_HEARTBEAT;
		txData.addByte(HeartbatCnt, 0);
		txData.setLength(3);
		HeartbatCnt ++;
		return txData.getSendBuffer();
	}
	private class TxDataThread implements Runnable {
		public void run() {
			while(true) {
				if(AutoTxEnable == true) {
					byte[] SendBuffer = CreateSendBuffer();
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
					TimeUnit.MILLISECONDS.sleep(AutoTxTimeDelay);
				} catch (InterruptedException e) {
					System.err.println("Interrupted");
				}
			}
		}
	}

	public void SignalLostCallback() {}

	private boolean GotResponseFlag = false;
	private void RxDataPreProcess(byte[] rData, int len) {
		try {
			for(int i = 0; i < len; i ++)
				decoder.rx_decode(rData[i]);
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
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
						System.err.println("UDP Socket Receive Exception.");
					}
					byte[] recData = packet.getData();
					RxDataPreProcess(recData, packet.getLength());
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

	@Override
	public void getNewPackage(DecodeEvent event) {
		// TODO Auto-generated method stub
		GotResponseFlag = true;
	}
	@Override
	public void badCRCEvent(DecodeEvent event) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void serialEvent(SerialPortEvent serialPortEvent) {
		// TODO Auto-generated method stub
        switch (serialPortEvent.getEventType()) {
        	case SerialPortEvent.BI: // 10 通讯中断s
//        		JOptionPane.showMessageDialog(null, "communication interrupted!", "error!", JOptionPane.ERROR_MESSAGE);
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
        					RxDataPreProcess(data, data.length);
        				}
        			}
        		} catch (ReadDataFromSerialPortFailure | SerialPortInputStreamCloseFailure e) {
        			// TODO Auto-generated catch block
        			e.printStackTrace();
        		}
        	break;
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
						SignalLostCallback();
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

	private void OpenUartPort(String srName, int bps) throws SerialPortParameterFailure, NotASerialPort, NoSuchPort, PortInUse, TooManyListeners {
		serialPort = SerialTool.openPort(srName, bps);
		SerialTool.addListener(serialPort, this);
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
							OpenUartPort(srName, bps);
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
						debug_info.setPreferredSize(new Dimension(FrameWidth - 320, 30));
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
						debug_info.setPreferredSize(new Dimension(FrameWidth - 313, 30));
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
			pref.put("_MyMainFrame_IF", _Interface);
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

	WindowAdapter wl = new WindowAdapter() {
		public void windowClosing(WindowEvent e) {
			if(serialPort != null) {
				SerialTool.closePort(serialPort);
			}
			if(CommSocket != null) {
				CommSocket.close();
				CommSocket = null;
			}
			System.exit(0);
		}
	};
	
	ComponentAdapter wcl = new ComponentAdapter() {
		public void componentResized(ComponentEvent e) {
			if(_Interface.equals("Uart")) {
				ComPanel.removeAll();

				ComPanel.add(srSelect);
				ComPanel.add(srBaudSet);
				ComPanel.add(OpenPortBtn);
				debug_info.setPreferredSize(new Dimension(FrameWidth - 320, 30));
				ComPanel.add(debug_info);
				repaint();
				ComPanel.validate();
			} else if(_Interface.equals("Wifi")) {
				ComPanel.removeAll();

				ComPanel.add(ip_lab);
				ComPanel.add(IP_Txt);
				ComPanel.add(port_lab);
				ComPanel.add(Port_Txt);
				debug_info.setPreferredSize(new Dimension(FrameWidth - 313, 30));
				ComPanel.add(debug_info);
				repaint();
				ComPanel.validate();
			}
		}
	};

	public static void main(String[] args) {
		try {
            UIManager.setLookAndFeel(
                UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Couldn't use system look and feel.");
        }
		(new MyMainFrame(1000, 600)).setVisible(true);
	}
}
