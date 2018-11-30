package uwbRTLS;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

public class digitalTracker extends JPanel {
	private static final long serialVersionUID = 1L;
	private static final int UI_Width = 1000;
	private static final int UI_Height = 600;

	private Image img = null;
	private myCanvas canvas = null;
	private Graphics gGraph = null;
	private CoordTrans coordTransfer = null;

	public digitalTracker() {
		img = new BufferedImage(UI_Width, UI_Height, BufferedImage.TYPE_4BYTE_ABGR);
		canvas = new myCanvas(img);
		gGraph = img.getGraphics();
		coordTransfer = new CoordTrans(UI_Width, UI_Height);
		coordTransfer.setRealArea(4.0, 4.0);
		canvas.setCoordTrans(coordTransfer);
		
	}
}

class settingPanel extends JPanel {
	private static final long serialVersionUID = 1L;
}
