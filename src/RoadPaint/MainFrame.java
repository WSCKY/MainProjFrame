package RoadPaint;

import java.awt.BorderLayout;
import java.awt.Font;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;

import MainFrame.MyMainFrame;
import WaveTool.WaveTool;

/**
 * Main Program Entrance.
 */
public class MainFrame extends MyMainFrame {
	private static final long serialVersionUID = 1L;
	private static final int PainterWidth = 1000;
	private static final int PainterHeight = 600;

	private JPanel MainPanel = null;
	private JTabbedPane MainTabPane = null;
	
	private SimulatorPane Simulator = null;
	public MainFrame() {
		this.setTitle("UWB MONITOR");
		this.setFrameSize(PainterWidth, PainterHeight);
		MainPanel = this.getUsrMainPanel();
		MainPanel.setLayout(new BorderLayout());
		Simulator = new SimulatorPane();
		MainTabPane = new JTabbedPane();
		MainTabPane.setFont(MainTabPane.getFont().deriveFont(Font.BOLD, 16));
		MainTabPane.addTab("MainPane", null, Simulator, "Main Panel");
		MainTabPane.addTab("WaveTool", null, new WaveTool("Distance"), "Wave Tool");
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
