package NavBoardTool;

import java.awt.BorderLayout;
import java.awt.Font;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;

import MainFrame.MyMainFrame;
import Module3D.MyCube3D;

public class MainFrame extends MyMainFrame {
	private static final long serialVersionUID = 1L;

	private JPanel MainPanel = null;
	private JTabbedPane MainTabPane = null;

	private MainFrame() {
		this.setTitle("kyChu.NavBoard Monitor");
		this.setFrameSize(1000, 600);
		MainPanel = this.getUsrMainPanel();
		MainPanel.setLayout(new BorderLayout());
		MainTabPane = new JTabbedPane();
		MainTabPane.setFont(MainTabPane.getFont().deriveFont(Font.BOLD, 16));
		
		MyCube3D cube = new MyCube3D();
		WavePlayer wave = new WavePlayer("DataWave");
		MainTabPane.addTab("Simulator", null, cube, "3D View");
		MainTabPane.addTab("WaveTool", null, wave, "Wave Tool");
		
		this.addDecodeEventListener(cube);
		this.addDecodeEventListener(wave);
		
		MainPanel.add(MainTabPane);
		this.setResizable(false);
		this.setVisible(true);
	}
	public static void main(String[] args) {
		try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Couldn't use system look and feel.");
        }
		new MainFrame();
	}
}
