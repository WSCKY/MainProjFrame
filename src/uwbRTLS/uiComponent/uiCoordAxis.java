package uwbRTLS.uiComponent;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

public class uiCoordAxis extends uiComponent {
	private static final int AxisLength = 150;
	public uiCoordAxis(int xp, int yp) {
		super(AxisLength, AxisLength);
		Graphics2D g = (Graphics2D)this.getGraphics();
		g.setColor(Color.BLACK);
		g.setStroke(new BasicStroke(2));
		g.drawLine(0, 20, AxisLength, 20);
		g.drawLine(20, 0, 20, AxisLength);
		g.drawLine(AxisLength - 20, 10, AxisLength, 20);
		g.drawLine(AxisLength - 20, 30, AxisLength, 20);
		g.drawLine(10, AxisLength - 20, 20, AxisLength);
		g.drawLine(29, AxisLength - 20, 20, AxisLength);
		this.setPos(xp, yp);
	}
}
