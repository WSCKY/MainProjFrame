package uwbRTLS.uiComponent;

import java.awt.Color;
import java.awt.Graphics;

public class uiAnchor extends uiComponent {
	private static final int SIZE_X = 20;
	private static final int SIZE_Y = 20;
	private int id = 0;
	private Graphics g = null;
	public uiAnchor(int xp, int yp) {
		super(SIZE_X, SIZE_Y);
		// TODO Auto-generated constructor stub
		this.setPos(xp, yp);
		g = this.getGraphics();
		g.setColor(Color.BLUE);
		g.drawLine(0, 0, SIZE_X, SIZE_Y);
		g.drawLine(0, SIZE_Y, SIZE_X, 0);
		g.drawArc((int)(SIZE_X * 0.15), (int)(SIZE_Y * 0.15), (int)(SIZE_X * 0.7), (int)(SIZE_Y * 0.7), 0, 360);
	}
	public uiAnchor(int xp, int yp, int id) {
		this(xp, yp);
		this.id = id;
	}
	public void setID(int id) {
		this.id = id;
	}
	public int getID() {
		return id;
	}
	public void enable(boolean flag) {
		if(flag)
			g.setColor(Color.BLUE);
		else
			g.setColor(Color.RED);
		g.drawLine(0, 0, SIZE_X, SIZE_Y);
		g.drawLine(0, SIZE_Y, SIZE_X, 0);
		g.drawArc((int)(SIZE_X * 0.15), (int)(SIZE_Y * 0.15), (int)(SIZE_X * 0.7), (int)(SIZE_Y * 0.7), 0, 360);
	}
	
	public void setPos(int x, int y) {
		super.setPos(x - (SIZE_X / 2), y - (SIZE_Y / 2));
	}
}
