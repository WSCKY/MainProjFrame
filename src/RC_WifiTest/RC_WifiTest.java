package RC_WifiTest;

import java.awt.FlowLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;

import MainFrame.MyMainFrame;
import protocol.ComPackage;
import protocol.RxAnalyse;

public class RC_WifiTest extends MyMainFrame {
	private static final long serialVersionUID = 1L;

	private static ComPackage rxData = new ComPackage();

	private JPanel MainPanel = null;
	private JLabel ChannelLab_1 = new JLabel("0000");
	private JLabel ChannelLab_2 = new JLabel("0000");
	private JLabel ChannelLab_3 = new JLabel("0000");
	private JLabel ChannelLab_4 = new JLabel("0000");

	public RC_WifiTest() {
		MainPanel = this.getUsrMainPanel();
		MainPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		MainPanel.add(ChannelLab_1); MainPanel.add(ChannelLab_2); MainPanel.add(ChannelLab_3); MainPanel.add(ChannelLab_4);
		this.setVisible(true);
		this.setAutoTxEnable(true);
	}

	public void RxDataProcess() {
		synchronized(new String("")) {
			try {
				rxData = (ComPackage) RxAnalyse.RecPackage.PackageCopy();
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}
		}
		if(rxData.type == ComPackage.TYPE_WIFI_RC_RAW) {
			ChannelLab_1.setText(String.format("%04d", (int)(rxData.readoutCharacter(0))));
			ChannelLab_2.setText(String.format("%04d", (int)(rxData.readoutCharacter(2))));
			ChannelLab_3.setText(String.format("%04d", (int)(rxData.readoutCharacter(4))));
			ChannelLab_4.setText(String.format("%04d", (int)(rxData.readoutCharacter(6))));
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
