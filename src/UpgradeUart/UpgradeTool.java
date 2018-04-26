/*
 * @brief  Firmware upgrade tool.
 * @author kyChu
 * @Date   2017/6/1
 */
package UpgradeUart;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.prefs.Preferences;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;

import FirmwareRule.FileHeader;
import FirmwareRule.FileNameRegular;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import protocol.ComPackage;
import protocol.RxAnalyse;
import protocol.PackageTypes.TypePartnerX;
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

public class UpgradeTool extends JFrame {
	/**
	 * version control.
	 */
	private static final long serialVersionUID = 2L;
	private static final byte Major = 2;
	private static final byte Minor = 3;
	private static final byte FixNumber = 2;

	private static boolean UpgradeStartFlag = false;
	private File srcFile = null;
	private InputStream fs = null;
	private static ComPackage rxData = new ComPackage();
	private static ComPackage txData = new ComPackage();

	private SerialPort serialPort = null;
	private List<String> srList = null;
	private final String[] srBaudRate = {"9600", "57600", "115200", "230400"};

	private JPanel hPanel = new JPanel();
	private JComboBox<String> srSelect = new JComboBox<String>();
	private JComboBox<String> srBaudSet = new JComboBox<String>();
	private JLabel debug_info = new JLabel("ready.");
//	private JButton OpenPortBtn = new JButton("Open");
	private JButton OpenPortBtn = new JButton("打开");
	private JButton OpenFileBtn = new JButton(" ... ");
//	private JButton UpgButton = new JButton("Upgrade");
	private JButton UpgButton = new JButton("开始");
//	private JLabel src_lab = new JLabel("src:");
	private JLabel src_lab = new JLabel("文件:");
	private JTextField src_txt = new JTextField(35);
//	private JLabel Prog_lab = new JLabel("sta:");
	private JLabel Prog_lab = new JLabel("状态:");
	private JProgressBar txProg = new JProgressBar(0, 100);

	private JFileChooser FileChoose = null;

	public UpgradeTool() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				srSelect.setBounds(10, 5, 90, 30);
				srSelect.setFont(srSelect.getFont().deriveFont(Font.BOLD, 14));
				srSelect.setToolTipText("select com port");
				add(srSelect);

				srBaudSet.setBounds(110, 5, 90, 30);
				srBaudSet.setMaximumRowCount(5);
				srBaudSet.setEditable(false);
				for(String s : srBaudRate)
					srBaudSet.addItem(s);
				srBaudSet.setSelectedIndex(2);//default: 115200
				srBaudSet.setFont(srBaudSet.getFont().deriveFont(Font.BOLD, 14));
				srBaudSet.setToolTipText("set baudrate");
				add(srBaudSet);

				OpenPortBtn.setBounds(210, 5, 90, 30);
//				OpenPortBtn.setFont(new Font("Courier New", Font.BOLD, 18));
				OpenPortBtn.setFont(new Font("宋体", Font.BOLD, 18));
				OpenPortBtn.addActionListener(sbl);
				OpenPortBtn.setToolTipText("open com port");
				add(OpenPortBtn);

				debug_info.setBounds(320, 5, 315, 30);
				debug_info.setHorizontalAlignment(SwingConstants.RIGHT);
				debug_info.setVerticalAlignment(SwingConstants.BOTTOM);
				debug_info.setFont(debug_info.getFont().deriveFont(Font.ITALIC));
				debug_info.setToolTipText("debug info");
//				debug_info.setBorder(BorderFactory.createLineBorder(Color.RED));
				add(debug_info);

				hPanel.setBounds(0, 1, 647, 40);
//				hPanel.setBorder(BorderFactory.createEtchedBorder());
//				hPanel.setBorder(BorderFactory.createTitledBorder("Title"));
				hPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 40, 647, new Color(233, 80, 80, 160)));
				add(hPanel);

				src_lab.setBounds(10, 55, 68, 22);
//				src_lab.setBorder(BorderFactory.createLineBorder(Color.RED));
//				src_lab.setFont(new Font("Courier New", Font.BOLD, 24));
				src_lab.setFont(new Font("宋体", Font.BOLD, 24));
				add(src_lab);

				src_txt.setBounds(78, 50, 500, 32);
//				src_txt.setFont(new Font("Courier New", Font.BOLD, 24));
				src_txt.setFont(new Font("宋体", Font.BOLD, 24));
				src_txt.setEditable(false);
				src_txt.setToolTipText("file path");
				add(src_txt);
				new DropTarget(src_txt, DnDConstants.ACTION_COPY_OR_MOVE, txtl, true);

				OpenFileBtn.setBounds(583, 50, 42, 32);
				OpenFileBtn.setPreferredSize(new Dimension(42, 32));
				OpenFileBtn.setFont(new Font("Courier New", Font.BOLD, 18));
				OpenFileBtn.addActionListener(obl);
				OpenFileBtn.setToolTipText("choose file");
				add(OpenFileBtn);

				Prog_lab.setBounds(10, 102, 68, 22);
//				Prog_lab.setFont(new Font("Courier New", Font.BOLD, 24));
				Prog_lab.setFont(new Font("宋体", Font.BOLD, 24));
//				Prog_lab.setBorder(BorderFactory.createLineBorder(Color.RED));
				add(Prog_lab);

				txProg.setBounds(78, 97, 500, 32);
				txProg.setValue(0);
				txProg.setStringPainted(true);
				txProg.setFont(txProg.getFont().deriveFont(Font.ITALIC | Font.BOLD, 16));
//				txProg.setToolTipText("upgrade state");
				txProg.setToolTipText("升级状态");
				add(txProg);

				UpgButton.setBounds(218, 144, 200, 40);
//				UpgButton.setFont(new Font("Courier New", Font.BOLD, 24));
				UpgButton.setFont(new Font("宋体", Font.BOLD, 24));
				UpgButton.addActionListener(ubl);
				UpgButton.setToolTipText("upgrade");
				add(UpgButton);

				getRootPane().setDefaultButton(UpgButton);
				setLayout(null);
				Toolkit tool = getToolkit();
				setIconImage(tool.getImage(UpgradeTool.class.getResource("upp.png")));
				setResizable(false);

//				setTitle("kyChu.UpgradeTool V" + Major + "." + Minor + "." + FixNumber);
				setTitle("F1/2飞控固件烧写工具  V" + Major + "." + Minor + "." + FixNumber);
				setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				addWindowListener(wl);
				setSize(652, 222);
				setLocationRelativeTo(null);
				setVisible(true);
			}
		});
	}

	private ActionListener sbl = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			String name = ((JButton)e.getSource()).getText();
			if(name.equals("打开")) {//Open
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
							((JButton)e.getSource()).setText("关闭");//Close
							srSelect.setEnabled(false);
							srBaudSet.setEnabled(false);
							debug_info.setText(srName + " opened.");
							hPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 40, 647, new Color(80, 233, 80, 160)));
						} catch (SerialPortParameterFailure | NotASerialPort | NoSuchPort | PortInUse | TooManyListeners e1) {
							JOptionPane.showMessageDialog(null, e1, "error!", JOptionPane.ERROR_MESSAGE);
						}
					}
				}
			} else if(name.equals("关闭")) {//Close
				SerialTool.closePort(serialPort);

				serialPort = null;
				srSelect.setEnabled(true);
				srBaudSet.setEnabled(true);
				ExitUpgrade();
				((JButton)e.getSource()).setText("打开");//Open
				debug_info.setText(srSelect.getSelectedItem() + " closed.");
				hPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 40, 647, new Color(233, 80, 80, 160)));
			}
		}
	};

	private ActionListener obl = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			FileSystemView fsv = FileSystemView.getFileSystemView();
			FileChoose = new JFileChooser();
			FileNameExtensionFilter filter = new FileNameExtensionFilter("pnx file(*.pnx)", "pnx");
			FileChoose.setFileFilter(filter);
			Preferences pref = Preferences.userRoot().node(this.getClass().getName());
			String lastPath = pref.get("lastPath", "");//get it from System registry.
			if(!lastPath.equals("")) {
				File LastFilePath = new File(lastPath);
				if(LastFilePath.exists())
					FileChoose.setCurrentDirectory(new File(lastPath));
				else
					FileChoose.setCurrentDirectory(fsv.getHomeDirectory());
			} else
				FileChoose.setCurrentDirectory(fsv.getHomeDirectory());
//			FileChoose.setFileSelectionMode(JFileChooser.FILES_ONLY);
			int ret = FileChoose.showDialog(null, "Choose");
			if(ret == JFileChooser.APPROVE_OPTION ) {
				File file = FileChoose.getSelectedFile();
				pref.put("lastPath", file.getParent()); //Save it to System registry.
				String prefix = file.getName().substring(file.getName().lastIndexOf("."));
				if(prefix.equals(".pnx")) {
					if(FileNameRegular.IsValidName(file.getName(), ".pnx")) {
						srcFile = file;
						src_txt.setText(srcFile.getPath());
						debug_info.setText("file size: " + srcFile.length() + " Bytes.");
					} else {
						JOptionPane.showMessageDialog(null, "file name error!", "error!", JOptionPane.ERROR_MESSAGE);
					}
				} else {
					JOptionPane.showMessageDialog(null, "file type error!", "error!", JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	};

	private DropTargetListener txtl = new DropTargetListener() {
		public void dragEnter(DropTargetDragEvent dtde) {}
		public void dragOver(DropTargetDragEvent dtde) {}
		public void dropActionChanged(DropTargetDragEvent dtde) {}
		public void dragExit(DropTargetEvent dte) {}

		@Override
		public void drop(DropTargetDropEvent dtde) {
			// TODO Auto-generated method stub
			try {
				if(dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
					dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);// important!
					@SuppressWarnings("unchecked")
					/* attention: should "import java.util.List;" (not awt.List) */
					List<File> list = (List<File>)(dtde.getTransferable().getTransferData(DataFlavor.javaFileListFlavor));
					for(File file : list) {
						if(file.isFile()) {
							String prefix = file.getName().substring(file.getName().lastIndexOf("."));
//							System.out.println(prefix);
							if(prefix.equals(".pnx")) {
								if(FileNameRegular.IsValidName(file.getName(), ".pnx")) {
//									System.out.println(file.getPath());
									srcFile = file;
									src_txt.setText(srcFile.getPath());
									debug_info.setText("file size: " + srcFile.length() + " Bytes.");
									break;
								} else {
//									JOptionPane.showMessageDialog(null, "file name error!", "error!", JOptionPane.ERROR_MESSAGE);
									JOptionPane.showMessageDialog(null, "文件名错误！", "出错！", JOptionPane.ERROR_MESSAGE);
								}
							} else {
//								JOptionPane.showMessageDialog(null, "file type error!", "error!", JOptionPane.ERROR_MESSAGE);
								JOptionPane.showMessageDialog(null, "文件类型错误！", "出错！", JOptionPane.ERROR_MESSAGE);
							}
						}
					}
					dtde.dropComplete(true);
				} else {
					dtde.rejectDrop();
				}
			} catch (UnsupportedFlavorException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};

	private ActionListener ubl = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			if(serialPort != null) {
				if(srcFile != null) {
					UpgButton.setEnabled(false);
					OpenFileBtn.setEnabled(false);
					OpenPortBtn.setEnabled(false);
					try {
						fs = new FileInputStream(srcFile);
					} catch (FileNotFoundException e1) {
						srcFile = null;
						src_txt.setText("");
						e1.printStackTrace();
						JOptionPane.showMessageDialog(null, "Exception: \"FileNotFoundException\"", "error!", JOptionPane.ERROR_MESSAGE);
					}
					debug_info.setText("upgrade start.");
					UpgradeStartFlag = true; /* Start upgrade. */
				} else {
//					JOptionPane.showMessageDialog(null, "select file first!", "error!", JOptionPane.ERROR_MESSAGE);
					JOptionPane.showMessageDialog(null, "请先打开文件！", "出错！", JOptionPane.ERROR_MESSAGE);
				}
			} else {
//				JOptionPane.showMessageDialog(null, "open the port first!", "error!", JOptionPane.ERROR_MESSAGE);
				JOptionPane.showMessageDialog(null, "请先打开端口！", "出错！", JOptionPane.ERROR_MESSAGE);
			}
		}
	};
//private static long TimeMes_M = 0;
	private static boolean ErrorShownFlag = false;
	private static boolean RefusedShownFlag = false;
	private class SerialListener implements SerialPortEventListener {
	    public void serialEvent(SerialPortEvent serialPortEvent) {
	        switch (serialPortEvent.getEventType()) {
	            case SerialPortEvent.BI: // 10 通讯中断
	            	ExitUpgrade();
//	            	JOptionPane.showMessageDialog(null, "communication interrupted!", "error!", JOptionPane.ERROR_MESSAGE);
	            	JOptionPane.showMessageDialog(null, "通讯被中断！", "出错！", JOptionPane.ERROR_MESSAGE);
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
	            	//System.out.println("found data");
					byte[] data = null;
					try {
						if (serialPort == null) {
//							JOptionPane.showMessageDialog(null, "serial port = null", "error!", JOptionPane.ERROR_MESSAGE);
							JOptionPane.showMessageDialog(null, "端口为空！", "出错！", JOptionPane.ERROR_MESSAGE);
						}
						else {
							data = SerialTool.readFromPort(serialPort);//read data from port.
							if (data == null || data.length < 1) {//check data.
//								JOptionPane.showMessageDialog(null, "no valid data!", "error!", JOptionPane.ERROR_MESSAGE);
								JOptionPane.showMessageDialog(null, "未收到有效数据！", "出错！", JOptionPane.ERROR_MESSAGE);
								System.exit(0);
							}
							else {
								for(int i = 0; i < data.length; i ++)
									RxAnalyse.rx_decode(data[i]);
								if(UpgradeStartFlag == true) {
									if(RxAnalyse.GotNewPackage()) {
										synchronized(new String("")) {//unnecessary (copy).
											try {
												rxData = (ComPackage) RxAnalyse.RecPackage.PackageCopy();
											} catch (CloneNotSupportedException e) {
												// TODO Auto-generated catch block
												e.printStackTrace();
											}
										}
										GotResponseFlag = true;
										if(rxData.type == TypePartnerX.TYPE_UPGRADE_FC_ACK) {
											switch(rxData.rData[0]) {
												case TypePartnerX.FC_STATE_ERASE:
													ErrorShownFlag = false;
													RefusedShownFlag = false;
													if(UpgradeStep == UpgradeSendRequest) {
														debug_info.setText("Erasing flash.");
														txProg.setValue(rxData.rData[1]);
														txProg.setString("擦除中...   " + rxData.rData[1] + "%");//Erasing
													}
												break;
												case TypePartnerX.FC_STATE_UPGRADE:
													ErrorShownFlag = false;
													RefusedShownFlag = false;
													/* state must be updated before "UpdateBufferFlag = true;" */
													if(UpgradeStep == UpgradeSendRequest) {
														UpgradeStep = UpgradeSendFileData;
													}
													int rdID = rxData.readoutInteger(1);
													if(rdID == PackageIndex) {//dangerous
														UpdateBufferFlag = true;
														debug_info.setText("got file data:   " + rdID + "/" + NumberOfPackage);
														if(NumberOfPackage != 0) {
															int progress = ((rdID * 100) / NumberOfPackage);
															txProg.setValue(progress);
															txProg.setString("升级中...   " + progress + "%");//Upgrading
														}
													}
//													TimeMes_M = System.currentTimeMillis();
												break;
												case TypePartnerX.FC_STATE_REFUSED:
													if(UpgradeStep == UpgradeSendRequest) {
														ExitUpgrade();
														if(RefusedShownFlag == false) {
															RefusedShownFlag = true;
															switch(rxData.rData[1]) {
																case TypePartnerX.FC_REFUSED_BUSY:
//																	JOptionPane.showMessageDialog(null, "fc busy.", "fc refused!", JOptionPane.ERROR_MESSAGE);
																	JOptionPane.showMessageDialog(null, "主控繁忙.", "拒绝升级！", JOptionPane.ERROR_MESSAGE);
																break;
																case TypePartnerX.FC_REFUSED_VERSION_OLD:
//																	JOptionPane.showMessageDialog(null, "version too old.", "fc refused!", JOptionPane.ERROR_MESSAGE);
																	JOptionPane.showMessageDialog(null, "版本过旧.", "拒绝升级！", JOptionPane.ERROR_MESSAGE);
																break;
																case TypePartnerX.FC_REFUSED_OVER_SIZE:
//																	JOptionPane.showMessageDialog(null, "over size.", "fc refused!", JOptionPane.ERROR_MESSAGE);
																	JOptionPane.showMessageDialog(null, "文件过大.", "拒绝升级！", JOptionPane.ERROR_MESSAGE);
																break;
																case TypePartnerX.FC_REFUSED_TYPE_ERROR:
//																	JOptionPane.showMessageDialog(null, "type error.", "fc refused!", JOptionPane.ERROR_MESSAGE);
																	JOptionPane.showMessageDialog(null, "机型错误.", "拒绝升级！", JOptionPane.ERROR_MESSAGE);
																break;
																case TypePartnerX.FC_REFUSED_LOW_VOLTAGE:
//																	JOptionPane.showMessageDialog(null, "low voltage.", "fc refused!", JOptionPane.ERROR_MESSAGE);
																	JOptionPane.showMessageDialog(null, "电压过低.", "拒绝升级！", JOptionPane.ERROR_MESSAGE);
																break;
																case TypePartnerX.FC_REFUSED_FW_TYPE_ERROR:
//																	JOptionPane.showMessageDialog(null, "fw type error.", "fc refused!", JOptionPane.ERROR_MESSAGE);
																	JOptionPane.showMessageDialog(null, "固件错误.", "拒绝升级！", JOptionPane.ERROR_MESSAGE);
																break;
																case TypePartnerX.FC_REFUSED_UNKNOWERROR:
																default:
//																	JOptionPane.showMessageDialog(null, "fc unkonw error.", "fc refused!", JOptionPane.ERROR_MESSAGE);
																	JOptionPane.showMessageDialog(null, "未知错误.", "拒绝升级！", JOptionPane.ERROR_MESSAGE);
																break;
															}
														}
													}
												break;
												case TypePartnerX.FC_STATE_JUMPFAILED:
													if(ErrorShownFlag == false) {
														ErrorShownFlag = true;
//														JOptionPane.showMessageDialog(null, "fc jump to application failed!", "fc error!", JOptionPane.ERROR_MESSAGE);
														JOptionPane.showMessageDialog(null, "跳转失败.", "飞控异常！", JOptionPane.ERROR_MESSAGE);
													}
												break;
												default:
												break;
											}
										}
									}
								}
							}
						}
					} catch (ReadDataFromSerialPortFailure | SerialPortInputStreamCloseFailure e) {
						ExitUpgrade();
						JOptionPane.showMessageDialog(null, e, "error!", JOptionPane.ERROR_MESSAGE);
						System.exit(0);
					} catch (CloneNotSupportedException e) {
						ExitUpgrade();
						e.printStackTrace();
					}
					break;
	        }
	    }
	}

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
					ExitUpgrade();
					String err = ExceptionWriter.getErrorInfoFromException(e);
					JOptionPane.showMessageDialog(null, err, "error!", JOptionPane.ERROR_MESSAGE);
					System.exit(0);
				}
			}
		}
	}

	private static final int UpgradeSendRequest = 0;
	private static final int UpgradeSendFileData = 1;

	private static int UpgradeStep = UpgradeSendRequest;
	private static boolean UpdateBufferFlag = true;
	private static int NumberOfPackage = 0;
	private static int PackageIndex = 0;//  zero(0) reserved!
	private static byte[] FileHeaderData = null;

	private static boolean EnableSendFlag = false;//enable transmit once.
	private static long SendTimeStart = 0;
	private static long SendTimeOut = 500;//500ms
	private class UpgradeTxThread implements Runnable {
		private byte[] SendBuffer = null;
		private byte[] fileread = new byte[ComPackage.FILE_DATA_CACHE];
		private boolean NeedExit = false;
		public void run() {
			while(true) {
				if(UpgradeStartFlag == true) {
					synchronized(new String("")) {
						if(UpdateBufferFlag == true) {
							switch(UpgradeStep) {
								case UpgradeSendRequest:
									//tx data
									int DataCnt = 0;
									int FileSize = (int)srcFile.length();
									NumberOfPackage = ((FileSize - FileHeader.HeaderSize) / ComPackage.FILE_DATA_CACHE);
									if((FileSize - FileHeader.HeaderSize) % ComPackage.FILE_DATA_CACHE != 0) NumberOfPackage += 1;

									FileHeaderData = new byte[FileHeader.HeaderSize];
									try {
										fs.read(FileHeaderData);
									} catch (IOException e1) {
										// TODO Auto-generated catch block
										e1.printStackTrace();
									}
									FileHeader fh = new FileHeader(FileHeaderData);
									if(FileHeader.IsValid(fh)) {
										txData.type = TypePartnerX.TYPE_UPGRADE_REQUEST;
										txData.addByte(TypePartnerX.FW_TYPE_FC, DataCnt); DataCnt += 1;
										txData.addInteger(NumberOfPackage, DataCnt); DataCnt += 4;
										txData.addInteger(FileSize, DataCnt); DataCnt += 4;
										txData.addCharacter(fh.getVersion(), DataCnt); DataCnt += 2;
										txData.addByte(fh.getType(), DataCnt); DataCnt += 1;
										txData.addInteger(fh.getCRC(), DataCnt); DataCnt += 4;
										txData.setLength(DataCnt + 2);
										SendBuffer = txData.getSendBuffer();
										SendTimeStart = System.currentTimeMillis();
										SendTimeOut = 500;
										EnableSendFlag = true;
									} else {
										NeedExit = true;
										srcFile = null;
										src_txt.setText("");
//										JOptionPane.showMessageDialog(null, "invalid pnx file!", "error!", JOptionPane.ERROR_MESSAGE);
										JOptionPane.showMessageDialog(null, "无效的飞控固件！", "error!", JOptionPane.ERROR_MESSAGE);
									}
								break;
								case UpgradeSendFileData:
									try {
										int nRead = 0;
										if((nRead = fs.read(fileread)) != -1) {
											PackageIndex ++;//package index, count from 1.
											txData.type = TypePartnerX.TYPE_UPGRADE_DATA;
											txData.addInteger(PackageIndex, 0);
											txData.addByte((byte)nRead, 4);
											txData.addBytes(fileread, ComPackage.FILE_DATA_CACHE, 5);
											txData.setLength(ComPackage.FILE_DATA_CACHE + 7);
											SendBuffer = txData.getSendBuffer();//update.
											SendTimeStart = System.currentTimeMillis();
											SendTimeOut = 50;
											EnableSendFlag = true;
										} else {
	//										ExitUpgrade();
											NeedExit = true;
//											JOptionPane.showMessageDialog(null, "Upgrade Success!", "info", JOptionPane.INFORMATION_MESSAGE);
											JOptionPane.showMessageDialog(null, "升级完成！", "提示", JOptionPane.INFORMATION_MESSAGE);
										}
									} catch (IOException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
										JOptionPane.showMessageDialog(null, "Excepte while read file!", "error!", JOptionPane.ERROR_MESSAGE);
										//need exit!
									}
								break;
								default:
									SendTimeStart = System.currentTimeMillis();
									SendTimeOut = 500;
									EnableSendFlag = false;
								break;
							}
							UpdateBufferFlag = false;
						}
						if(NeedExit == true) {
							NeedExit = false;
							ExitUpgrade();
						}
					}
					if(EnableSendFlag == true) {
						try {
							EnableSendFlag = false;
							SerialTool.sendToPort(serialPort, SendBuffer);
//							System.out.println(System.currentTimeMillis() - TimeMes_M);
						} catch (SendDataToSerialPortFailure | SerialPortOutputStreamCloseFailure e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
					if((System.currentTimeMillis() - SendTimeStart) >= SendTimeOut) {
						SendTimeStart = System.currentTimeMillis();
						EnableSendFlag = true;
					}
				}

//				Thread.yield();/* we can't do it! */
				try {
					TimeUnit.MICROSECONDS.sleep(10);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					System.err.println("Interrupted");
				}
			}
		}
	}

	private static boolean GotResponseFlag = false;
	private static int SignalLostCnt = 0;
	private static int SignalLost_1s = 0;
	private class UpgradeRxThread implements Runnable {
		public void run() {
			while(true) {
				if(UpgradeStartFlag == true) {
					if(GotResponseFlag == false) {
						if(SignalLostCnt < 20)
							SignalLostCnt ++;
						else {
							SignalLostCnt = 0;
								debug_info.setText("signal lost.");
							if(SignalLost_1s < 10)
								SignalLost_1s ++;
						}
					} else {
						SignalLostCnt = 0;
						SignalLost_1s = 0;
						GotResponseFlag = false;
					}
					if(SignalLost_1s >= 10) {
						SignalLost_1s = 0;
//						Object[] options = {"EXIT", "WAIT"};
						Object[] options = {"退出", "等待"}; 
//						int ret = JOptionPane.showOptionDialog(null, "no response from fc!\nexit?", "warning", JOptionPane.DEFAULT_OPTION, 
//						JOptionPane.WARNING_MESSAGE,null, options, options[0]);
						int ret = JOptionPane.showOptionDialog(null, "飞控无响应！\n是否退出？", "警告", JOptionPane.DEFAULT_OPTION, 
								JOptionPane.WARNING_MESSAGE,null, options, options[0]);
						if(ret == -1)
							ret = 0;
						if(ret == 0)
							ExitUpgrade();
					}
				} else {
					SignalLostCnt = 0;
					SignalLost_1s = 0;
					GotResponseFlag = false;
				}
				try {
					TimeUnit.MILLISECONDS.sleep(50);//50ms loop.
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					System.err.println("Interrupted");
				}
			}
		}
	}

	private synchronized void ExitUpgrade() {
		UpgradeStartFlag = false;
		UpdateBufferFlag = true;
		NumberOfPackage = 0;
		PackageIndex = 0;
		SendTimeOut = 500;
		UpgradeStep = UpgradeSendRequest;
		EnableSendFlag = false;

		if(fs != null) {
			try {
				fs.close();
			} catch (IOException e) {
				srcFile = null;
				src_txt.setText("");
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		debug_info.setText("ready.");
		txProg.setValue(0);
		txProg.setString("0%");
		UpgButton.setEnabled(true);
		OpenFileBtn.setEnabled(true);
		OpenPortBtn.setEnabled(true);
	}

	private void StartAllThread() {
		new Thread(new RepaintThread()).start();
		new Thread(new UpgradeRxThread()).start();
		new Thread(new UpgradeTxThread()).start();
	}

	WindowAdapter wl = new WindowAdapter() {
		public void windowClosing(WindowEvent e) {
			if(fs != null) {
				try {
					fs.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			if(serialPort != null) {
				SerialTool.closePort(serialPort);
				serialPort = null;
			}
			System.exit(0);
		}
	};

	//Main Function.
	public static void main(String[] args) throws InterruptedException {
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
		UpgradeTool t = new UpgradeTool();
		TimeUnit.MILLISECONDS.sleep(10);
		t.StartAllThread();
	}
}
