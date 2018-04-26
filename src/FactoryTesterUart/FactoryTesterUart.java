package FactoryTesterUart;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
//import java.util.Timer;
//import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import protocol.ComPackage;
import protocol.RxAnalyse;
import protocol.PackageTypes.TypePartnerX;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import DSNGenerator.DSNGenerator;
import SerialTool.SerialTool;
import SerialTool.serialException.ExceptionWriter;
import SerialTool.serialException.NoSuchPort;
import SerialTool.serialException.NotASerialPort;
import SerialTool.serialException.PortInUse;
import SerialTool.serialException.ReadDataFromSerialPortFailure;
import SerialTool.serialException.SendDataToSerialPortFailure;
import SerialTool.serialException.SerialPortInputStreamCloseFailure;
import SerialTool.serialException.SerialPortOutputStreamCloseFailure;
import SerialTool.serialException.SerialPortParameterFailure;
import SerialTool.serialException.TooManyListeners;

public class FactoryTesterUart extends JFrame {
	private static final long serialVersionUID = 1L;

	private static int DroneType = 1;

//	private static DatagramSocket CommSocket = null;
//	private static final int CommPort = 6000;
//	private static final String CommIP = "192.168.4.1";
	private SerialPort serialPort = null;
	private List<String> srList = null;
	private final String[] srBaudRate = {"9600", "57600", "115200", "230400"};

	private static ComPackage rxData = new ComPackage();
	private static ComPackage txData = new ComPackage();

	DSNGenerator dsnGenerator = null;

	private JPanel hPanel = new JPanel();
	private JPanel MainPanel = new JPanel();
	private JPanel InfoPanel = new JPanel();
	private JPanel InitRetPanel = new JPanel();
	private JPanel VersionPanel = new JPanel();
	private JPanel VoltagePanel = new JPanel();
	private JPanel LEDPanel = new JPanel();
	private JPanel ESCBurnInPanel = new JPanel();

	private JComboBox<String> srSelect = new JComboBox<String>();
	private JComboBox<String> srBaudSet = new JComboBox<String>();
	private JButton OpenPortBtn = new JButton("Open");

	private JLabel VoltText = new JLabel("0.0");
	private JLabel VelXText = new JLabel("0.0");
	private JLabel VelYText = new JLabel("0.0");
	private JLabel PitchText = new JLabel("0.0");
	private JLabel RollText = new JLabel("0.0");

	private JProgressBar VoltCalBar = new JProgressBar(0, 100);
	private JButton Calib_H = new JButton("高压校准");
	private JButton Calib_L = new JButton("低压校准");

	private JTextField VER_txt = new JTextField(9);
	private JTextField DSN_txt = new JTextField(16);
	private JButton bUpdateDSN = new JButton("更新");

	private JLabel IMUSta = new JLabel();
	private JLabel BAROSta = new JLabel();
	private JLabel MTDSta = new JLabel();
	private JLabel FLOWSta = new JLabel();
	private JLabel TOFSta = new JLabel();

	private JCheckBox Red_Box = new JCheckBox("红");
	private JCheckBox Blue_Box = new JCheckBox("蓝");
	private JCheckBox Green_Box = new JCheckBox("绿");

	private JProgressBar ESCBurnInBar = new JProgressBar(0, 100);
	private JButton MotorStartBtn = new JButton("启动");
	private JButton StartBurnInBtn = new JButton("开始");
	private JButton StopBurnInBtn = new JButton("停止");

	private JDialog DroneSelectDialog = new JDialog(this, "设置机型");
	private JRadioButton F1_sel = new JRadioButton("虹湾F1", true);
	private JRadioButton F2_sel = new JRadioButton("虹湾F2", false);
	private ButtonGroup sel_bg = new ButtonGroup();
	private JButton DroneConfirmButton = new JButton("确定");

	public FactoryTesterUart() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				hPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 5));
				hPanel.setPreferredSize(new Dimension(1000, 42));
				hPanel.setBackground(new Color(233, 80, 80, 160));
				srSelect.setPreferredSize(new Dimension(90, 30));
				srSelect.setFont(srSelect.getFont().deriveFont(Font.BOLD, 14));
				srSelect.setToolTipText("select com port");
				hPanel.add(srSelect);

				srBaudSet.setPreferredSize(new Dimension(90, 30));
				srBaudSet.setMaximumRowCount(5);
				srBaudSet.setEditable(false);
				for(String s : srBaudRate)
					srBaudSet.addItem(s);
				srBaudSet.setSelectedIndex(2);//default: 115200
				srBaudSet.setFont(srBaudSet.getFont().deriveFont(Font.BOLD, 14));
				srBaudSet.setToolTipText("set baudrate");
				hPanel.add(srBaudSet);
				OpenPortBtn.setPreferredSize(new Dimension(90, 30));
				OpenPortBtn.setFont(new Font("Courier New", Font.BOLD, 18));
				OpenPortBtn.addActionListener(sbl);
				OpenPortBtn.setToolTipText("open com port");
				hPanel.add(OpenPortBtn);

				InitRetPanel.setLayout(new GridLayout(1, 5, 0, 0));
				InitRetPanel.setBorder(BorderFactory.createTitledBorder(null, "飞控外设", 0, 2, new Font("宋体", Font.PLAIN, 16)));
				IMUSta.setIcon(new ImageIcon(new ImageIcon(getClass().getResource("wait_s.gif")).getImage().getScaledInstance(25, 25, Image.SCALE_DEFAULT)));
				BAROSta.setIcon(new ImageIcon(new ImageIcon(getClass().getResource("wait_s.gif")).getImage().getScaledInstance(25, 25, Image.SCALE_DEFAULT)));
				MTDSta.setIcon(new ImageIcon(new ImageIcon(getClass().getResource("wait_s.gif")).getImage().getScaledInstance(25, 25, Image.SCALE_DEFAULT)));
				FLOWSta.setIcon(new ImageIcon(new ImageIcon(getClass().getResource("wait_s.gif")).getImage().getScaledInstance(25, 25, Image.SCALE_DEFAULT)));
				TOFSta.setIcon(new ImageIcon(new ImageIcon(getClass().getResource("wait_s.gif")).getImage().getScaledInstance(25, 25, Image.SCALE_DEFAULT)));
				JLabel NameLabel = new JLabel("IMU: "); NameLabel.setFont(new Font("宋体", Font.BOLD, 20));
				JPanel p = new JPanel(); p.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 8));
//				p.setBorder(BorderFactory.createLineBorder(Color.RED));
				p.add(NameLabel); p.add(IMUSta); InitRetPanel.add(p);

				NameLabel = new JLabel("气压计: "); NameLabel.setFont(new Font("宋体", Font.BOLD, 20));
				p = new JPanel(); p.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 8));
//				p.setBorder(BorderFactory.createLineBorder(Color.RED));
				p.add(NameLabel); p.add(BAROSta); InitRetPanel.add(p);

				NameLabel = new JLabel("FLASH: "); NameLabel.setFont(new Font("宋体", Font.BOLD, 20));
				p = new JPanel(); p.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 8));
//				p.setBorder(BorderFactory.createLineBorder(Color.RED));
				p.add(NameLabel); p.add(MTDSta); InitRetPanel.add(p);

				NameLabel = new JLabel("光流: "); NameLabel.setFont(new Font("宋体", Font.BOLD, 20));
				p = new JPanel(); p.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 8));
//				p.setBorder(BorderFactory.createLineBorder(Color.RED));
				p.add(NameLabel); p.add(FLOWSta); InitRetPanel.add(p);

				NameLabel = new JLabel("红外: "); NameLabel.setFont(new Font("宋体", Font.BOLD, 20));
				p = new JPanel(); p.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 8));
//				p.setBorder(BorderFactory.createLineBorder(Color.RED));
				p.add(NameLabel); p.add(TOFSta); InitRetPanel.add(p);

				InfoPanel.setLayout(new GridLayout(1, 5));
				InfoPanel.setBorder(BorderFactory.createTitledBorder(null, "状态信息", 0, 2, new Font("宋体", Font.PLAIN, 16)));
				NameLabel = new JLabel("电压: "); NameLabel.setFont(new Font("宋体", Font.BOLD, 20));
				p = new JPanel(); p.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 8));
				VoltText.setFont(VoltText.getFont().deriveFont(Font.BOLD, 28));
				p.add(NameLabel); p.add(VoltText); InfoPanel.add(p);

				NameLabel = new JLabel("速度X: "); NameLabel.setFont(new Font("宋体", Font.BOLD, 20));
				p = new JPanel(); p.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 8));
				VelXText.setFont(VelXText.getFont().deriveFont(Font.BOLD, 28));
				p.add(NameLabel); p.add(VelXText); InfoPanel.add(p);

				NameLabel = new JLabel("速度Y: "); NameLabel.setFont(new Font("宋体", Font.BOLD, 20));
				p = new JPanel(); p.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 8));
				VelYText.setFont(VelYText.getFont().deriveFont(Font.BOLD, 28));
				p.add(NameLabel); p.add(VelYText); InfoPanel.add(p);

				NameLabel = new JLabel("俯仰: "); NameLabel.setFont(new Font("宋体", Font.BOLD, 20));
				p = new JPanel(); p.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 8));
				PitchText.setFont(PitchText.getFont().deriveFont(Font.BOLD, 28));
				p.add(NameLabel); p.add(PitchText); InfoPanel.add(p);

				NameLabel = new JLabel("横滚: "); NameLabel.setFont(new Font("宋体", Font.BOLD, 20));
				p = new JPanel(); p.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 8));
				RollText.setFont(RollText.getFont().deriveFont(Font.BOLD, 28));
				p.add(NameLabel); p.add(RollText); InfoPanel.add(p);

				VoltagePanel.setBorder(BorderFactory.createTitledBorder(null, "电压校准", 0, 2, new Font("宋体", Font.PLAIN, 16)));
				VoltagePanel.setLayout(new FlowLayout(FlowLayout.CENTER, 25, 0));
				VoltCalBar.setPreferredSize(new Dimension(700, 30)); VoltCalBar.setString("");
				VoltCalBar.setFont(VoltCalBar.getFont().deriveFont(Font.ITALIC | Font.BOLD, 16));
				VoltCalBar.setStringPainted(true); VoltagePanel.add(VoltCalBar);
				Calib_H.setPreferredSize(new Dimension(100, 40)); VoltagePanel.add(Calib_H); Calib_H.setEnabled(false);
				Calib_L.setPreferredSize(new Dimension(100, 40)); VoltagePanel.add(Calib_L); Calib_L.setEnabled(false);
				Calib_H.addActionListener(hbl); Calib_L.addActionListener(lbl);

				VersionPanel.setBorder(BorderFactory.createTitledBorder(null, "版本管理", 0, 2, new Font("宋体", Font.PLAIN, 16)));
				VersionPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 3));
				NameLabel = new JLabel("版本: "); NameLabel.setFont(new Font("宋体", Font.BOLD, 20));
				VersionPanel.add(NameLabel);
				VER_txt.setFont(new Font("Courier New", Font.BOLD, 26));
				VER_txt.setEditable(false); VersionPanel.add(VER_txt);
				NameLabel = new JLabel("序列号: "); NameLabel.setFont(new Font("宋体", Font.BOLD, 20));
				VersionPanel.add(NameLabel);
				DSN_txt.setFont(new Font("Courier New", Font.BOLD, 26));
				DSN_txt.setEditable(false); VersionPanel.add(DSN_txt);
				bUpdateDSN.setPreferredSize(new Dimension(100, 40));
				bUpdateDSN.setFont(new Font("宋体", Font.BOLD, 20));
				bUpdateDSN.setEnabled(false); bUpdateDSN.addActionListener(ubl);
				VersionPanel.add(bUpdateDSN);

				LEDPanel.setBorder(BorderFactory.createTitledBorder(null, "主状态灯", 0, 2, new Font("宋体", Font.PLAIN, 16)));
				LEDPanel.setBackground(new Color(120, 120, 120)); LEDPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 150, 0));
				Red_Box.setFont(new Font("宋体", Font.BOLD, 30)); Red_Box.addActionListener(ledl);
				Blue_Box.setFont(new Font("宋体", Font.BOLD, 30)); Blue_Box.addActionListener(ledl);
				Green_Box.setFont(new Font("宋体", Font.BOLD, 30)); Green_Box.addActionListener(ledl);
				LEDPanel.add(Red_Box); LEDPanel.add(Blue_Box); LEDPanel.add(Green_Box);

				ESCBurnInPanel.setBorder(BorderFactory.createTitledBorder(null, "老化测试", 0, 2, new Font("宋体", Font.PLAIN, 16)));
				ESCBurnInPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 22, 5));
				ESCBurnInBar.setPreferredSize(new Dimension(586, 30)); ESCBurnInBar.setString("");
				ESCBurnInBar.setFont(ESCBurnInBar.getFont().deriveFont(Font.ITALIC | Font.BOLD, 16));
				ESCBurnInBar.setStringPainted(true); ESCBurnInPanel.add(ESCBurnInBar);
				MotorStartBtn.setPreferredSize(new Dimension(100, 40)); MotorStartBtn.setEnabled(false);
				MotorStartBtn.setFont(MotorStartBtn.getFont().deriveFont(Font.BOLD, 20));
				MotorStartBtn.addActionListener(mstartl); ESCBurnInPanel.add(MotorStartBtn);
				StartBurnInBtn.setPreferredSize(new Dimension(100, 40)); StartBurnInBtn.setEnabled(false);
				StartBurnInBtn.setFont(StartBurnInBtn.getFont().deriveFont(Font.BOLD, 20));
				StartBurnInBtn.addActionListener(bstartl); ESCBurnInPanel.add(StartBurnInBtn);
				StopBurnInBtn.setPreferredSize(new Dimension(100, 40)); StopBurnInBtn.setEnabled(false);
				StopBurnInBtn.setFont(StopBurnInBtn.getFont().deriveFont(Font.BOLD, 20));
				StopBurnInBtn.addActionListener(bstopl); ESCBurnInPanel.add(StopBurnInBtn);

				setLayout(new BorderLayout());/* 需要在前面 */
				add(hPanel, BorderLayout.NORTH);
				MainPanel.setLayout(new GridLayout(6, 1));
				MainPanel.add(InitRetPanel);
				MainPanel.add(InfoPanel);
				MainPanel.add(VoltagePanel);
				MainPanel.add(VersionPanel);
				MainPanel.add(LEDPanel);
				MainPanel.add(ESCBurnInPanel);
				add(MainPanel, BorderLayout.CENTER);

				Toolkit tool = getToolkit();
				setIconImage(tool.getImage(FactoryTesterUart.class.getResource("FactoryTest.png")));

				setResizable(false);
//				setTitle("kyChu.FactoryTester V1.0.0");
				setTitle("F1/2飞控板测试工具  V1.1.0");
				setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				addWindowListener(wl);
				setSize(1000, 580);
				setLocationRelativeTo(null);
				setVisible(true);

				/* Drone Type Selector. */
				F1_sel.setFont(F1_sel.getFont().deriveFont(Font.BOLD, 16));
				F1_sel.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						DroneType = 1;
					}
				});
				F2_sel.setFont(F2_sel.getFont().deriveFont(Font.BOLD, 16));
				F2_sel.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						DroneType = 2;
					}
				});
				sel_bg.add(F1_sel); sel_bg.add(F2_sel);
				DroneConfirmButton.setPreferredSize(new Dimension(90, 36));
				DroneConfirmButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						DroneSelectDialog.dispose();
					}
				});
				DroneSelectDialog.setResizable(false);
				DroneSelectDialog.setSize(280, 140);
				DroneSelectDialog.setLocationRelativeTo(null);
				DroneSelectDialog.setModal(true);
				DroneSelectDialog.setLayout(new FlowLayout(FlowLayout.CENTER, 50, 15));
				DroneSelectDialog.add(F1_sel); DroneSelectDialog.add(F2_sel);
				DroneSelectDialog.add(DroneConfirmButton);
				DroneSelectDialog.getRootPane().setDefaultButton(DroneConfirmButton);
				DroneSelectDialog.setVisible(true);

				dsnGenerator = new DSNGenerator(DroneType);
			}
		});

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

	private boolean GotVersionFlag = false;
	private boolean WriteNewDSNFlag = false;
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
								for(int i = 0; i < data.length; i ++)
									RxAnalyse.rx_decode(data[i]);
								if(RxAnalyse.GotNewPackage()) {
									GotResponseFlag = true;
									StopBurnInBtn.setEnabled(true);
									if(ESCBurnInRunningFlag == false && VoltCalibStartFlag == false) {
										Calib_H.setEnabled(true);
										Calib_L.setEnabled(true);
										MotorStartBtn.setEnabled(true);
									}
									synchronized(new String("")) {//unnecessary (copy).
										try {
											rxData = (ComPackage) RxAnalyse.RecPackage.PackageCopy();
											if(rxData.type == TypePartnerX.TYPE_FC_Response) {
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
											} else if(rxData.type == TypePartnerX.TYPE_VERSION_Response) {
												GotVersionFlag = true;
												char ver = rxData.readoutCharacter(0);
												VER_txt.setText("V" + (ver >> 12) + "." + ((ver >> 8) & 0x0F) + "." + (ver & 0x00FF));
												String curDSN = rxData.readoutString(4, 16);
												DSN_txt.setText(curDSN);
												if(curDSN.equals("PXyyMMwwxxxxFn##")) {
													bUpdateDSN.setEnabled(true);
												} else {
													bUpdateDSN.setEnabled(false);
												}
												if(WriteNewDSNFlag == true) {
													if(curDSN.equals(_NewDSN)) {
														WriteNewDSNFlag = false;
														bUpdateDSN.setEnabled(false);
														dsnGenerator.SaveThisDSN();
													}
												}
											} else if(rxData.type == TypePartnerX.TYPE_ADC_CALIB_ACK) {
												if(rxData.rData[2] != 0x0) { /* Exception. */
													Calib_H.setEnabled(true);
													Calib_L.setEnabled(true);
													MotorStartBtn.setEnabled(true);
													VoltCalBar.setValue(0);
													VoltCalBar.setString("");
													VoltCalibStartFlag = false;
													VoltCalibState = 0;/* Exit Calibrate. */
													if(rxData.rData[2] == 0x1)
														JOptionPane.showMessageDialog(null, "电压错误！", "error!", JOptionPane.ERROR_MESSAGE);
//													else if(rxData.rData[2] == 0x2)
//														JOptionPane.showMessageDialog(null, "采样错误！", "error!", JOptionPane.ERROR_MESSAGE);
													else
														JOptionPane.showMessageDialog(null, "未知错误！", "error!", JOptionPane.ERROR_MESSAGE);
												} else {
													VoltCalBar.setValue(rxData.rData[1]);
													VoltCalBar.setString((rxData.rData[0] == TypePartnerX.ADC_CALIBRATE_H ? "H" : "L") + " Sampling ..." + rxData.rData[1] + "%");
													if(rxData.rData[1] >= 100) {//complete.
														Calib_H.setEnabled(true);
														Calib_L.setEnabled(true);
														MotorStartBtn.setEnabled(true);
														VoltCalBar.setValue(0);
														VoltCalBar.setString("");
														VoltCalibStartFlag = false;
														VoltCalibState = 0;/* Exit Calibrate. */
														JOptionPane.showMessageDialog(null, "校准完成！", "ok!", JOptionPane.INFORMATION_MESSAGE);
													}
												}
											}
										} catch (CloneNotSupportedException e) {
											e.printStackTrace();
										}
									}
								}
							}
						}
	            	} catch (ReadDataFromSerialPortFailure | SerialPortInputStreamCloseFailure | CloneNotSupportedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	            break;
	        }
	    }
	}

	private static byte VoltCalibState = 0;
	private static int _wDSN_CmdTog = 0;
	private class UpgradeTxThread implements Runnable {
		public void run() {
			while(true) {
				if(VoltCalibState == TypePartnerX.ADC_CALIBRATE_H || VoltCalibState == TypePartnerX.ADC_CALIBRATE_L) {
					txData.type = TypePartnerX.TYPE_ADC_CALIBRATE;
					txData.addByte(VoltCalibState, 0);
					txData.addByte((byte)(VoltCalibState ^ 0xAA), 1);
					txData.setLength(4);
				} else if(ESCBurnInRunningFlag == true) {
					txData.type = TypePartnerX.TYPE_ESC_BURN_IN_TEST;
					txData.addByte(ESCBurnExpSpeed, 0);
					txData.addByte((byte)(ESCBurnExpSpeed ^ 0xCC), 1);
					txData.setLength(4);
				} else if(GotVersionFlag == false) {
					txData.type = TypePartnerX.TYPE_VERSION_REQUEST;
					txData.addByte((byte)0x0F, 0);
					txData.setLength(3);
				} else if(WriteNewDSNFlag == true) {
					if(_wDSN_CmdTog % 2 == 0) {
						txData.type = TypePartnerX.TYPE_DSN_UPDATE;
						txData.addBytes(_NewDSN.getBytes(), 16, 0);
						txData.addByte((byte)0xBB, 16);
						txData.setLength(19);
					} else {
						txData.type = TypePartnerX.TYPE_VERSION_REQUEST;
						txData.addByte((byte)0x0F, 0);
						txData.setLength(3);
					}
					_wDSN_CmdTog ++;
				} else {/* no operation */
					txData.type = TypePartnerX.TYPE_DeviceCheckReq;
					txData.addByte(TypePartnerX._dev_LED, 0);
					txData.addByte(LEDValue, 1);
					txData.addFloat(0.0f, 5);
					txData.addByte((byte)0, 9);
					txData.addFloat(0.0f, 10);
					txData.setLength(10);
				}
				byte[] SendBuffer = txData.getSendBuffer();
				if(UartStateFlag == true) {
					try {
						SerialTool.sendToPort(serialPort, SendBuffer);
					} catch (SendDataToSerialPortFailure e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (SerialPortOutputStreamCloseFailure e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
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
						GotVersionFlag = false;
						VER_txt.setText(""); DSN_txt.setText("");
						bUpdateDSN.setEnabled(false);
						Calib_H.setEnabled(false); Calib_L.setEnabled(false);
						MotorStartBtn.setEnabled(false);
						StartBurnInBtn.setEnabled(false); StopBurnInBtn.setEnabled(false);
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

	private static boolean VoltCalibStartFlag = false;
	private static byte VoltCalibReqVal = 0;
	private ActionListener hbl = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			if(VoltCalibStartFlag == false) {
				Calib_H.setEnabled(false);
				Calib_L.setEnabled(false);
				MotorStartBtn.setEnabled(false);
				VoltCalibStartFlag = true;
				VoltCalibReqVal = TypePartnerX.ADC_CALIBRATE_H;
				new Thread(new VoltSampleWaitThread()).start();
			}
		}
	};

	private ActionListener lbl = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			if(VoltCalibStartFlag == false) {
				Calib_H.setEnabled(false);
				Calib_L.setEnabled(false);
				MotorStartBtn.setEnabled(false);
				VoltCalibStartFlag = true;
				VoltCalibReqVal = TypePartnerX.ADC_CALIBRATE_L;
				new Thread(new VoltSampleWaitThread()).start();
			}
		}
	};

	private class VoltSampleWaitThread implements Runnable {
		public void run() {
			int tCnt = 0;
			VoltCalBar.setString("Waiting ...");
			for(tCnt = 0; tCnt < 21; tCnt ++) {
				VoltCalBar.setValue((int) (tCnt * 5));
				try {
					TimeUnit.MILLISECONDS.sleep(100);//100ms loop.
				} catch (InterruptedException e) {
					System.err.println("Interrupted");
				}
			}
			VoltCalibState = VoltCalibReqVal;
		}
	}

	private String _NewDSN = "PXyyMMwwxxxxFn##";
	private ActionListener ubl = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			if(ESCBurnInRunningFlag == false && VoltCalibStartFlag == false) {
				WriteNewDSNFlag = true;
				bUpdateDSN.setEnabled(false);
				_NewDSN = dsnGenerator.GotNewDSN();
			}
		}
	};

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
			}
			else if(name.equals("蓝")) {
				if(Blue_Box.isSelected()) {
					Red_Box.setSelected(false);
					Green_Box.setSelected(false);
					blue = 255; LEDValue |= (byte)0x02;
				}
			}
			else if(name.equals("绿")) {
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
	private static boolean MotorStartFlag = false;
	private ActionListener mstartl = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			if(ESCBurnInRunningFlag == false) {
				ESCBurnInRunningFlag = true;
				MotorStartFlag = true;
				ESCBurnExpSpeed = 0;
				Calib_H.setEnabled(false);
				Calib_L.setEnabled(false);
				MotorStartBtn.setEnabled(false);
				StartBurnInBtn.setEnabled(true);
				ESCBurnInBar.setString("怠速准备...");
			}
		}
	};

	private static boolean ESCBurnInStartFlag = false;
	private ActionListener bstartl = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			if(MotorStartFlag == true) {
				ESCBurnInStartFlag = true;
				StartBurnInBtn.setEnabled(false);
				new Thread(new ESCBurnInThread()).start();
			}
		}
	};

	private byte ESCBurnExpSpeed = 0;
	private class ESCBurnInThread implements Runnable {
		public void run() {
			int TimeCnt = 0;
			long TimeStart = 0;
			while(ESCBurnInStartFlag) {
				if(TimeCnt < 25) {
					TimeCnt ++;
					ESCBurnInBar.setValue(TimeCnt * 4);
					ESCBurnInBar.setString("提速中...");
					TimeStart = System.currentTimeMillis();
				} else if((System.currentTimeMillis() - TimeStart) <= 300000) { //5min
					long t = (System.currentTimeMillis() - TimeStart);
					int min = (int) (t / 60000);
					int sec = (int) ((t % 60000) / 1000);
					ESCBurnInBar.setString(min + "分" + sec + "秒");
					ESCBurnInBar.setValue((int) ((System.currentTimeMillis() - TimeStart) / 3000));
				} else {
					ESCBurnInStartFlag = false;
				}
				ESCBurnExpSpeed = (byte) (TimeCnt * 2);
				try {
					TimeUnit.MILLISECONDS.sleep(100);//100ms loop.
				} catch (InterruptedException e) {
					System.err.println("Interrupted");
				}
			}
			ESCBurnInBar.setValue(0);
			while(TimeCnt > 0) {
				TimeCnt --;
				ESCBurnInBar.setValue(TimeCnt * 4);
				ESCBurnInBar.setString("降速中...");
				ESCBurnExpSpeed = (byte) (TimeCnt * 2);
				try {
					TimeUnit.MILLISECONDS.sleep(100);//100ms loop.
				} catch (InterruptedException e) {
					System.err.println("Interrupted");
				}
			}
			ESCBurnInBar.setString("");
			ESCBurnInRunningFlag = false;
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

	private static boolean UartStateFlag = false;
	private ActionListener sbl = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			String name = ((JButton)e.getSource()).getText();
			if(name.equals("Open")) {
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
							((JButton)e.getSource()).setText("Close");
							srSelect.setEnabled(false);
							srBaudSet.setEnabled(false);
							hPanel.setBackground(new Color(80, 233, 80, 160));
							UartStateFlag = true;
						} catch (SerialPortParameterFailure | NotASerialPort | NoSuchPort | PortInUse | TooManyListeners e1) {
							JOptionPane.showMessageDialog(null, e1, "error!", JOptionPane.ERROR_MESSAGE);
						}
					}
				}
			} else if(name.equals("Close")) {
				UartStateFlag = false;
				SerialTool.closePort(serialPort);

				serialPort = null;
				srSelect.setEnabled(true);
				srBaudSet.setEnabled(true);
				((JButton)e.getSource()).setText("Open");
				hPanel.setBackground(new Color(233, 80, 80, 160));
			}
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
					TimeUnit.MILLISECONDS.sleep(30);//30ms loop.
				} catch (InterruptedException e) {
					String err = ExceptionWriter.getErrorInfoFromException(e);
					JOptionPane.showMessageDialog(null, err, "error!", JOptionPane.ERROR_MESSAGE);
					System.exit(0);
				}
			}
		}
	}

	WindowAdapter wl = new WindowAdapter() {
		public void windowClosing(WindowEvent e) {
			dsnGenerator.GeneratorClose();
			System.exit(0);
		}
	};

	public static void main(String[] args) {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Date Today = new Date();
		try {
			Date InvalidDay = df.parse("2018-6-01");
			if(Today.getTime() > InvalidDay.getTime()) {
				JOptionPane.showMessageDialog(null, "Sorry, Exit With Unknow Error!", "error!", JOptionPane.ERROR_MESSAGE);
				System.exit(0);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
//		System.err.println(System.getProperty("user.dir"));//get program running directory.
		new FactoryTesterUart();
	}
}
