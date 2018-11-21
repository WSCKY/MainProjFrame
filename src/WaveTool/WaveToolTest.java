package WaveTool;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;

import MainFrame.MyMainFrame;

final class WaveToolTest extends MyMainFrame implements Runnable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private WaveTool myWT = null;
	private JPanel MainPanel = null;
	private WaveToolTest() {
		super(1000, 600, "kyChu Wave Tool");
		this.setResizable(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		myWT = new WaveTool("WaveTool");
		MainPanel = this.getUsrMainPanel();
		MainPanel.setLayout(new BorderLayout());
		MainPanel.add(myWT);

		myWT.addNewSeries("Data0");
		myWT.addNewSeries("Data1");
		myWT.addNewSeries("Data2");
		myWT.setTitle("TEST TOOL");
		myWT.setValueAxisLabel("Distance");
		this.setVisible(true);
		new Thread(this).start();
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		double value = 0;
		while(true) {
			value = value + Math.random( ) - 0.5;
			myWT.addDataToSeries(0, value);
			myWT.addDataToSeries(1, value - 1);
			myWT.addDataToSeries(2, value + 1);
			try {
				Thread.sleep(5);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Couldn't use system look and feel.");
        }
		new WaveToolTest();
	}
}
