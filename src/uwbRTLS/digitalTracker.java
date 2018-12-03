package uwbRTLS;

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;

import MainFrame.MyMainFrame;
import uwbRTLS.CoordTranfer.CoordTrans;

public class digitalTracker extends JPanel {
	private static final long serialVersionUID = 1L;
	private static final int UI_Width = 1000;
	private static final int UI_Height = 600;

	private myCanvas canvas = null;
	private CoordTrans coordTransfer = null;

	public digitalTracker() {
		this.setLayout(new BorderLayout());
		canvas = new myCanvas();
		coordTransfer = new CoordTrans(UI_Width, UI_Height);
		coordTransfer.setRealArea(4.0, 4.0);
		canvas.setCoordTrans(coordTransfer);
		this.add(canvas, BorderLayout.CENTER);
		JPanel p = new JPanel();
		JLabel label = new JLabel("TestLabel");
		p.add(label);
		this.add(p, BorderLayout.SOUTH);
	}
	public static void main(String[] args) {
		try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Couldn't use system look and feel.");
        }
		MyMainFrame mf = new MyMainFrame();
		mf.setTitle("Tracker Test");
		mf.setFrameSize(UI_Width, UI_Height);
		digitalTracker sp = new digitalTracker(); 
		JPanel mp = mf.getUsrMainPanel();
		mp.setLayout(new BorderLayout());
		mp.add(sp, BorderLayout.CENTER);
		mf.setResizable(false);
		mf.setVisible(true);
	}
}
