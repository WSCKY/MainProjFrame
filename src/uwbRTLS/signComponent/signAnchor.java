package uwbRTLS.signComponent;

import java.awt.Color;
import java.awt.Graphics;

public class signAnchor extends uiComponent {
	private static final int SIZE_X = 20;
	private static final int SIZE_Y = 20;
	public signAnchor(int xp, int yp) {
		super(SIZE_X, SIZE_Y);
		// TODO Auto-generated constructor stub
		this.setPos(xp, yp);
		Graphics g = this.getGraphics();
		g.setColor(Color.BLUE);
		g.drawLine(0, 0, 20, 20);
		g.drawLine(0, 20, 20, 0);
		g.drawArc(3, 3, 14, 14, 0, 360);
	}
}
