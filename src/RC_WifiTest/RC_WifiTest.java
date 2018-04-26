package RC_WifiTest;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.UIManager;

import MainFrame.MyMainFrame;
import protocol.ComPackage;
import protocol.RxAnalyse;
import protocol.PackageTypes.TypePartnerX;

public class RC_WifiTest extends MyMainFrame {
	private static final long serialVersionUID = 1L;

	private static final int ChannelNumber = 12;
	private static final int ChannelMinVal = 352;
	private static final int ChannelMaxVal = 1696;

	private static ComPackage rxData = new ComPackage();

	private JPanel MainPanel = null;
	private JLabel[] ChannelVals = new JLabel[ChannelNumber];
	private JLabel[] ChannelNames = new JLabel[ChannelNumber];
	private JPanel[] ChannelPanels = new JPanel[ChannelNumber];
	private JProgressBar[] ChannelProgs = new JProgressBar[ChannelNumber];

	private String[] NameList = {"C0", "C1", "C2", "C3", "C4", "C5", "C6", "C7", "C8", "C9", "C10", "C11"};

	public RC_WifiTest() {
		this.setFrameSize(700, 450);
//		this.setResizable(true);
		MainPanel = this.getUsrMainPanel();
		MainPanel.setLayout(new GridLayout(1, ChannelNumber, 10, 10));
		for(int i = 0; i < ChannelNumber; i ++) {
			ChannelPanels[i] = new JPanel();
			ChannelPanels[i].setLayout(new BorderLayout());
		}
		for(int i = 0; i < ChannelNumber; i ++) {
			ChannelNames[i] = new JLabel(NameList[i]);
			ChannelNames[i].setFont(ChannelNames[i].getFont().deriveFont(Font.BOLD, 20));
			JPanel p = new JPanel(); p.add(ChannelNames[i]);
			ChannelPanels[i].add(p, BorderLayout.NORTH);
		}
		for(int i = 0; i < ChannelNumber; i ++) {
			ChannelProgs[i] = new JProgressBar(JProgressBar.VERTICAL, ChannelMinVal, ChannelMaxVal);
			ChannelProgs[i].setValue(1024);
			ChannelPanels[i].add(ChannelProgs[i], BorderLayout.CENTER);
		}
		for(int i = 0; i < ChannelNumber; i ++) {
			ChannelVals[i] = new JLabel("1024");
			ChannelVals[i].setFont(ChannelVals[i].getFont().deriveFont(Font.BOLD, 20));
			JPanel p = new JPanel(); p.add(ChannelVals[i]);
			ChannelPanels[i].add(p, BorderLayout.SOUTH);
		}
		for(int i = 0; i < ChannelNumber; i ++) {
			MainPanel.add(ChannelPanels[i]);
		}
		this.setVisible(true);
		this.setAutoTxEnable(true);
		this.setDebugString("Ready ...");
	}

	public void RxDataProcess() {
		synchronized(new String("")) {
			try {
				rxData = (ComPackage) RxAnalyse.RecPackage.PackageCopy();
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}
		}
		if(rxData.type == TypePartnerX.TYPE_WIFI_RC_RAW) {
			for(int i = 0; i < ChannelNumber; i ++) {
				ChannelVals[i].setText(String.format("%04d", (int)(rxData.readoutCharacter(i << 1))));
				ChannelProgs[i].setValue((int)(rxData.readoutCharacter(i << 1)));
			}
		}
	}

	public static void main(String[] args) {
		try {
            UIManager.setLookAndFeel(
                UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Couldn't use system look and feel.");
        }
		new RC_WifiTest();
	}
}
