package SalesCtrlBoard;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;

import MainFrame.MyMainFrame;
import protocol.ComPackage;
import protocol.RxAnalyse;

public class SalesCtrl extends MyMainFrame {
	private static final long serialVersionUID = 1L;
	
	private static ComPackage txData = null;
	private static ComPackage rxData = new ComPackage();

	private JPanel MainPanel = null;
	private JPanel SubPanel_1 = new JPanel();
	private JPanel SubPanel_2 = new JPanel();
	private JPanel SubPanel_3 = new JPanel();
	private JPanel SubPanel_4 = new JPanel();

	private JLabel CurrentBarMode = new JLabel("Mode 0");
	private JComboBox<String> BarModeSelecter = new JComboBox<String>();
	private String[] BarMode = {"Mode0", "Mode1", "Mode2", "Mode3", "Mode4", "Mode5", "Mode6", "Mode7"};

	private JButton DoorCtrlBtnA = new JButton("开门A");
	private JButton DoorCtrlBtnB = new JButton("开门B");

	private JCheckBox BitCtrl_1 = new JCheckBox("电透膜");
	private JCheckBox BitCtrl_2 = new JCheckBox("掌纹灯");
	private JCheckBox BitCtrl_3 = new JCheckBox("上照明");
	private JCheckBox BitCtrl_4 = new JCheckBox("下照明");

	private boolean VersionReqFlag = true;
	private JLabel VersionInfo = new JLabel("V0.0.0");
	private JLabel VoltageInfo = new JLabel("0.00 V");

	public SalesCtrl() {
		this.setFrameSize(700, 450);
		txData = this.getTxPackage();
		MainPanel = this.getUsrMainPanel();
		MainPanel.setLayout(new GridLayout(4, 1, 10, 10));
		SubPanel_1.setLayout(new GridLayout(1, 2, 10, 10));
		SubPanel_2.setLayout(new FlowLayout(FlowLayout.CENTER, 200, 5));
		SubPanel_3.setLayout(new FlowLayout(FlowLayout.CENTER, 50, 5));
		SubPanel_4.setLayout(new FlowLayout(FlowLayout.CENTER, 100, 5));
		SubPanel_1.setBorder(BorderFactory.createTitledBorder(null, "灯带控制", 0, 2, new Font("宋体", Font.PLAIN, 16)));
		SubPanel_2.setBorder(BorderFactory.createTitledBorder(null, "门禁控制", 0, 2, new Font("宋体", Font.PLAIN, 16)));
		SubPanel_3.setBorder(BorderFactory.createTitledBorder(null, "状态控制", 0, 2, new Font("宋体", Font.PLAIN, 16)));
		SubPanel_4.setBorder(BorderFactory.createTitledBorder(null, "系统信息", 0, 2, new Font("宋体", Font.PLAIN, 16)));

		JPanel _p = new JPanel(); _p.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		JLabel NameLabel = new JLabel("设置："); NameLabel.setFont(new Font("宋体", Font.BOLD, 20)); _p.add(NameLabel);
		for(String s : BarMode) BarModeSelecter.addItem(s);
		BarModeSelecter.setMaximumRowCount(8);
		BarModeSelecter.setEditable(false);
		BarModeSelecter.setSelectedIndex(0);
		BarModeSelecter.setPreferredSize(new Dimension(90, 30));
		BarModeSelecter.setFont(BarModeSelecter.getFont().deriveFont(Font.BOLD, 14));
		BarModeSelecter.setToolTipText("select left bar mode"); _p.add(BarModeSelecter);
		BarModeSelecter.addActionListener(bsl);
		SubPanel_1.add(_p);
		_p = new JPanel(); _p.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		NameLabel = new JLabel("当前："); NameLabel.setFont(new Font("宋体", Font.BOLD, 20)); _p.add(NameLabel);
		CurrentBarMode.setFont(new Font("宋体", Font.BOLD, 20)); _p.add(CurrentBarMode);
		SubPanel_1.add(_p);

		DoorCtrlBtnA.setPreferredSize(new Dimension(120, 40));
		DoorCtrlBtnA.addActionListener(dbl_a);
		DoorCtrlBtnA.setFont(DoorCtrlBtnA.getFont().deriveFont(Font.BOLD, 20));
		SubPanel_2.add(DoorCtrlBtnA);
		DoorCtrlBtnB.setPreferredSize(new Dimension(120, 40));
		DoorCtrlBtnB.addActionListener(dbl_b);
		DoorCtrlBtnB.setFont(DoorCtrlBtnB.getFont().deriveFont(Font.BOLD, 20));
		SubPanel_2.add(DoorCtrlBtnB);

		BitCtrl_1.setFont(new Font("宋体", Font.BOLD, 24));
		BitCtrl_2.setFont(new Font("宋体", Font.BOLD, 24));
		BitCtrl_3.setFont(new Font("宋体", Font.BOLD, 24));
		BitCtrl_4.setFont(new Font("宋体", Font.BOLD, 24));
		BitCtrl_1.addActionListener(bcl); BitCtrl_2.addActionListener(bcl);
		BitCtrl_3.addActionListener(bcl); BitCtrl_4.addActionListener(bcl);
		SubPanel_3.add(BitCtrl_1); SubPanel_3.add(BitCtrl_2); SubPanel_3.add(BitCtrl_3); SubPanel_3.add(BitCtrl_4);

		_p = new JPanel(); _p.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		NameLabel = new JLabel("版本："); NameLabel.setFont(new Font("宋体", Font.BOLD, 20));
		VersionInfo.setFont(new Font("Courier New", Font.BOLD, 20));
		_p.add(NameLabel); _p.add(VersionInfo); SubPanel_4.add(_p);
		_p = new JPanel(); _p.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		NameLabel = new JLabel("电压："); NameLabel.setFont(new Font("宋体", Font.BOLD, 20));
		VoltageInfo.setFont(new Font("Courier New", Font.BOLD, 20));
		_p.add(NameLabel); _p.add(VoltageInfo); SubPanel_4.add(_p);

		MainPanel.add(SubPanel_1); MainPanel.add(SubPanel_2); MainPanel.add(SubPanel_3); MainPanel.add(SubPanel_4);
		this.setVisible(true);
		this.setAutoTxEnable(true);
		this.setDebugString("Ready ...");
	}

	private int CurrentMode = 0;
	private boolean BarUpdateMode = false;
	private ActionListener bsl = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			if(BarModeSelecter.getSelectedIndex() != CurrentMode) {
				BarUpdateMode = true;
			}
		}
	};

	private byte CurDoorState = 0;
	private byte DoorCtrlCmd = 0;
	private boolean DoorCmdUpdate = false;
	private ActionListener dbl_a = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			String name = ((JButton)e.getSource()).getText();
			if(name.equals("开门A")) {
				DoorCtrlCmd &= 0xF0;
				DoorCtrlCmd |= 0x01;
			} else if(name.equals("关门A")) {
				DoorCtrlCmd &= 0xF0;
			}
			DoorCmdUpdate = true;
			DoorCtrlBtnA.setEnabled(false);
		}
	};
	private ActionListener dbl_b = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			String name = ((JButton)e.getSource()).getText();
			if(name.equals("开门B")) {
				DoorCtrlCmd &= 0x0F;
				DoorCtrlCmd |= 0x10;
			} else if(name.equals("关门B")) {
				DoorCtrlCmd &= 0x0F;
			}
			DoorCmdUpdate = true;
			DoorCtrlBtnB.setEnabled(false);
		}
	};

	private byte BitCtrlCurVal = 0;
	private byte BitCtrlExpVal = 0;
	private boolean BitCtrlUpdate = false;
	private ActionListener bcl = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			String name = ((JCheckBox)e.getSource()).getText();
			if(name.equals("下照明")) {
				if(BitCtrl_4.isSelected())
					BitCtrlExpVal |= (byte)0x01;
				else
					BitCtrlExpVal &= (byte)0xFE;
			} else if(name.equals("上照明")) {
				if(BitCtrl_3.isSelected())
					BitCtrlExpVal |= (byte)0x02;
				else
					BitCtrlExpVal &= (byte)0xFD;
			} else if(name.equals("掌纹灯")) {
				if(BitCtrl_2.isSelected())
					BitCtrlExpVal |= (byte)0x04;
				else
					BitCtrlExpVal &= (byte)0xFB;
			} else if(name.equals("电透膜")) {
				if(BitCtrl_1.isSelected())
					BitCtrlExpVal |= (byte)0x08;
				else
					BitCtrlExpVal &= (byte)0xF7;
			}
			if(BitCtrlExpVal != (BitCtrlCurVal & 0x0F))
				BitCtrlUpdate = true;
		}
	};

	byte HeartBeatCnt = 0;
	public byte[] CreateSendBuffer() {
		if(BarUpdateMode) {
			txData.type = ComPackage.TYPE_ProgrammableTX;
			txData.addByte((byte)BarModeSelecter.getSelectedIndex(), 0);
			txData.setLength(3);
		} else if(DoorCmdUpdate) {
			txData.type = ComPackage.TYPE_ProgrammableACK;
			txData.addByte(DoorCtrlCmd, 0);
			txData.setLength(3);
		} else if(BitCtrlUpdate) {
			txData.type = (byte) 0x21;
			txData.addByte(BitCtrlExpVal, 0);
			txData.setLength(3);
		} else if(VersionReqFlag) {
			txData.type = ComPackage.TYPE_VERSION_REQUEST;
			txData.addByte((byte) 0x0F, 0);
			txData.setLength(3);
		} else {
			txData.type = ComPackage.TYPE_FC_APP_HEARTBEAT;
			txData.addByte(HeartBeatCnt ++, 0);
			txData.setLength(3);
		}
		return txData.getSendBuffer();
	}

	public void SignalLostCallback() {
		VersionReqFlag = true;
	}

	public void RxDataProcess() {
		synchronized(new String("")) {
			try {
				rxData = (ComPackage) RxAnalyse.RecPackage.PackageCopy();
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}
		}
		if(rxData.type == ComPackage.TYPE_FC_APP_HEARTBEAT) {
			
		} else if(rxData.type == ComPackage.TYPE_FC_Response) {
			float val = rxData.readoutFloat(1);
			VoltageInfo.setText(String.format("%.2f", val));
			BitCtrlCurVal = (byte) rxData.rData[5];
			if(BitCtrlExpVal == (BitCtrlCurVal & 0x0F))
				BitCtrlUpdate = false;
			CurrentMode = (int)rxData.rData[6];
			CurrentBarMode.setText("Mode " + CurrentMode);
			if(BarModeSelecter.getSelectedIndex() == CurrentMode)
				BarUpdateMode = false;
			CurDoorState = (byte) rxData.rData[7];
			int DoorA = CurDoorState & 0x0F;
			int DoorB = (CurDoorState >> 4) & 0x0F;

			if(DoorA == 0) { DoorCtrlBtnA.setText("开门A"); DoorCtrlBtnA.setEnabled(true); }
			else if(DoorA == 1) { DoorCtrlBtnA.setText("关门A"); DoorCtrlBtnA.setEnabled(true); }
			else if(DoorA == 2) {
				DoorCtrlBtnA.setText("关门中..."); DoorCtrlBtnA.setEnabled(false);
				if(DoorCmdUpdate && (DoorCtrlCmd & 0x0F) == 0x00) {
					DoorCmdUpdate = false;
				}
			}
			else if(DoorA == 3) {
				DoorCtrlBtnA.setText("开门中..."); DoorCtrlBtnA.setEnabled(false);
				if(DoorCmdUpdate && (DoorCtrlCmd & 0x0F) == 0x01) {
					DoorCmdUpdate = false;
				}
			}

			if(DoorB == 0) { DoorCtrlBtnB.setText("开门B"); DoorCtrlBtnB.setEnabled(true); }
			else if(DoorB == 1) { DoorCtrlBtnB.setText("关门B"); DoorCtrlBtnB.setEnabled(true); }
			else if(DoorB == 2) {
				DoorCtrlBtnB.setText("关门中..."); DoorCtrlBtnB.setEnabled(false);
				if(DoorCmdUpdate && (DoorCtrlCmd & 0xF0) == 0x00) {
					DoorCmdUpdate = false;
				}
			}
			else if(DoorB == 3) {
				DoorCtrlBtnB.setText("开门中..."); DoorCtrlBtnB.setEnabled(false);
				if(DoorCmdUpdate && (DoorCtrlCmd & 0xF0) == 0x10) {
					DoorCmdUpdate = false;
				}
			}
		} else if(rxData.type == ComPackage.TYPE_VERSION_Response) {
			VersionReqFlag = false;
			char ver = rxData.readoutCharacter(0);
			VersionInfo.setText("V" + (ver >> 12) + "." + ((ver >> 8) & 0x0F) + "." + (ver & 0x00FF));
		}
	}

	public static void main(String[] args) {
		try {
            UIManager.setLookAndFeel(
                UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Couldn't use system look and feel.");
        }
		new SalesCtrl();
	}
}
