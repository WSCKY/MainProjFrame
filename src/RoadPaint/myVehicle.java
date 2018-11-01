package RoadPaint;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.ImageObserver;

import javax.swing.ImageIcon;

public class myVehicle implements ImageObserver {
	private Image img;
	private Graphics gCanvas = null;
	private int xPos = 0, yPos = 0;
	public myVehicle() {
		img = new ImageIcon(getClass().getResource("pos.png")).getImage().getScaledInstance(18, 26, Image.SCALE_DEFAULT);
	}
	public myVehicle(Graphics g) {
		gCanvas = g;
		img = new ImageIcon(getClass().getResource("pos.png")).getImage().getScaledInstance(18, 26, Image.SCALE_DEFAULT);
	}
	
	public Image getImage() {
		return this.img;
	}
	public void setCanvasGraphic(Graphics g) {
		gCanvas = g;
	}
	
	public void move(int x, int y) {
		xPos += x;
		yPos += y;
	}
	public void moveTo(int x, int y) {
		xPos = x;
		yPos = y;
	}
	public void update() {
		if(gCanvas != null) {
			gCanvas.drawImage(img, xPos, yPos, this);
		} else {
			// ...
		}
	}

	@Override
	public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {
		// TODO Auto-generated method stub
		return true;
	}
}
