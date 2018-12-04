package uwbRTLS.uiComponent;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

public class uiCoordAxis extends uiComponent {
	private static final int AxisLength = 160;
	private static final int TailLength = 25;
	private static final int ArrowSize = 20;
	private static final double ArrowAngleTan = 0.3;

	private Graphics2D g;
	private boolean X_Mirror = false, Y_Mirror = false;
	public uiCoordAxis(int xp, int yp) {
		super(AxisLength, AxisLength);
		g = (Graphics2D)this.getGraphics();
		g.setColor(Color.BLACK);
		g.setStroke(new BasicStroke(2));
		g.drawLine(0, TailLength, AxisLength, TailLength); // horizon
		g.drawLine(TailLength, 0, TailLength, AxisLength); // vertical
		g.drawLine(AxisLength - ArrowSize, (int)(TailLength - ArrowSize * ArrowAngleTan), AxisLength, TailLength); // h-axis
		g.drawLine(AxisLength - ArrowSize, (int)(TailLength + ArrowSize * ArrowAngleTan), AxisLength, TailLength);
		g.drawLine((int)(TailLength - ArrowSize * ArrowAngleTan), AxisLength - ArrowSize, TailLength, AxisLength); // v-axis
		g.drawLine((int)(TailLength + ArrowSize * ArrowAngleTan), AxisLength - ArrowSize, TailLength, AxisLength);
		this.setPos(xp, yp);
	}
	public void setPos(int x, int y) {
		super.setPos(x - TailLength, y - TailLength);
	}
	public void xMirror(boolean flag) {
		
	}
	public void yMirror(boolean flag) {
		
	}
	private void drawAxis() {
		g.setColor(new Color(0, 0, 0, 0));
		g.fillRect(0, 0, AxisLength, AxisLength);
		g.setColor(Color.BLACK);
		g.setStroke(new BasicStroke(2));
		if(X_Mirror) {
			g.drawLine(AxisLength - TailLength, 0, AxisLength - TailLength, AxisLength);
		} else {
			g.drawLine(TailLength, 0, TailLength, AxisLength);
		}
		if(Y_Mirror) {
			g.drawLine(0, AxisLength - TailLength, AxisLength, AxisLength - TailLength);
		} else {
			g.drawLine(0, TailLength, AxisLength, TailLength);
		}
		if(X_Mirror) {
			if(Y_Mirror) {
//				g.drawLine(AxisLength - ArrowSize, (int)(TailLength - ArrowSize * ArrowAngleTan), AxisLength, TailLength); // x-axis
//				g.drawLine(AxisLength - ArrowSize, (int)(TailLength + ArrowSize * ArrowAngleTan), AxisLength, TailLength);
//				g.drawLine((int)(TailLength - ArrowSize * ArrowAngleTan), AxisLength - ArrowSize, TailLength, AxisLength); // y-axis
//				g.drawLine((int)(TailLength + ArrowSize * ArrowAngleTan), AxisLength - ArrowSize, TailLength, AxisLength);
			} else {
				
			}
		} else {
			if(Y_Mirror) {
				
			} else {
				g.drawLine(AxisLength - ArrowSize, (int)(TailLength - ArrowSize * ArrowAngleTan), AxisLength, TailLength); // h-axis
				g.drawLine(AxisLength - ArrowSize, (int)(TailLength + ArrowSize * ArrowAngleTan), AxisLength, TailLength);
				g.drawLine((int)(TailLength - ArrowSize * ArrowAngleTan), AxisLength - ArrowSize, TailLength, AxisLength); // v-axis
				g.drawLine((int)(TailLength + ArrowSize * ArrowAngleTan), AxisLength - ArrowSize, TailLength, AxisLength);
			}
		}
	}
}
