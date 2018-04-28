package CarControl;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.KeyEvent;

import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;

import MainFrame.MyMainFrame;

public class CarCtrl extends MyMainFrame {

	private static final long serialVersionUID = 1L;

	private JPanel MainPanel = null;
	private JTabbedPane TabPanel = null;

	private JPanel Panel_Normal = new JPanel();
	private JProgressBar ProgBarA = new JProgressBar(JProgressBar.VERTICAL, -100, 100);
	private JProgressBar ProgBarB = new JProgressBar(JProgressBar.VERTICAL, -100, 100);

	private JPanel Panel_Debug = new JPanel();

	private JPanel Panel_Status = new JPanel();

	public CarCtrl() {
		this.setFrameSize(700, 450);
		MainPanel = this.getUsrMainPanel();
		MainPanel.setLayout(new BorderLayout());

		ProgBarA.setValue(0); ProgBarB.setValue(0);
		ProgBarA.setPreferredSize(new Dimension(40, 200));
		ProgBarB.setPreferredSize(new Dimension(40, 200));
		Panel_Normal.add(ProgBarA); Panel_Normal.add(ProgBarB);

		TabPanel = new JTabbedPane(JTabbedPane.LEFT);
		TabPanel.setFont(new Font("Courier", Font.PLAIN, 20));
		TabPanel.add(" Normal ", Panel_Normal); TabPanel.setMnemonicAt(0, KeyEvent.VK_1);
		TabPanel.add(" Debug ", Panel_Debug); TabPanel.setMnemonicAt(1, KeyEvent.VK_2);
		TabPanel.add(" Status ", Panel_Status); TabPanel.setMnemonicAt(2, KeyEvent.VK_3);
		MainPanel.add(TabPanel, BorderLayout.CENTER);

		this.setVisible(true);
		this.setAutoTxEnable(true);
		this.setResizable(true);
		this.setDebugString("Ready ...");
	}

	public static void main(String[] args) {
		try {
            UIManager.setLookAndFeel(
                UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Couldn't use system look and feel.");
        }
		new CarCtrl();
	}
}
