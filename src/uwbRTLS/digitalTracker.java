package uwbRTLS;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;

import MainFrame.MyMainFrame;

public class digitalTracker extends JPanel {
	private static final long serialVersionUID = 1L;
	private static final int UI_Width = 1000;
	private static final int UI_Height = 600;

	private Image img = null;
	private myCanvas canvas = null;
	private Graphics gGraph = null;
	private CoordTrans coordTransfer = null;

	public digitalTracker() {
		this.setLayout(new BorderLayout());
		img = new BufferedImage(UI_Width, UI_Height, BufferedImage.TYPE_4BYTE_ABGR);
		canvas = new myCanvas(img);
		gGraph = img.getGraphics();
		coordTransfer = new CoordTrans(UI_Width, UI_Height);
		coordTransfer.setRealArea(4.0, 4.0);
		canvas.setCoordTrans(coordTransfer);
		this.add(canvas, BorderLayout.CENTER);
		JPanel p = new JPanel();
		JLabel label = new JLabel("TestLabel");
		p.add(label);
		this.add(p, BorderLayout.SOUTH);
		gGraph.setColor(Color.BLACK);
		gGraph.drawLine(50, 50, 200, 200);
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
