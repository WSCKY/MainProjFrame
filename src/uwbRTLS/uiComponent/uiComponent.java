package uwbRTLS.uiComponent;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;

public class uiComponent {
	private int xPos = 0;
	private int yPos = 0;
	private int xSize = 0;
	private int ySize = 0;
	private Image img = null;
	private Graphics g = null;

	public uiComponent(int xs, int ys) {
		xSize = xs;
		ySize = ys;
		img = new BufferedImage(xSize, ySize, BufferedImage.TYPE_4BYTE_ABGR);
		g = img.getGraphics();
	}
	public void setSize(int w, int h) {
		xSize = w;
		ySize = h;
		img = new BufferedImage(xSize, ySize, BufferedImage.TYPE_4BYTE_ABGR);
		g = img.getGraphics();
	}
	public void setPos(int x, int y) {
		xPos = x;
		yPos = y;
	}
	public int getXPos() {
		return xPos;
	}
	public int getYPos() {
		return yPos;
	}
	public Graphics getGraphics() {
		return g;
	}
	public Image getImage() {
		return img;
	}
}
