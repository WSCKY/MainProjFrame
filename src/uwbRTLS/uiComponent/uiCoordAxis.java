package uwbRTLS.uiComponent;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

public class uiCoordAxis extends uiComponent {
	private static final int AxisLength = 160;
	private static final int TailLength = 25;
	private static final int ArrowSize = 20;
	private static final double ArrowAngleTan = 0.3;

	private Image img;
	private Graphics2D g;
	private int Pos_x = 0, Pos_y = 0;
	private int Off_X = TailLength, Off_Y = TailLength;
	private boolean X_Mirror = false, Y_Mirror = false;
	public uiCoordAxis(int xp, int yp) {
		drawAxis();
		this.setPos(xp, yp);
	}
	public void setPos(int x, int y) {
		Pos_x = x;
		Pos_y = y;
		super.setPos(x - Off_X, y - Off_Y);
	}
	public void xMirror(boolean flag) {
		if(X_Mirror != flag) {
			X_Mirror = flag;
			drawAxis();
			this.setPos(Pos_x, Pos_y);
		}
	}
	public void yMirror(boolean flag) {
		if(Y_Mirror != flag) {
			Y_Mirror = flag;
			drawAxis();
			this.setPos(Pos_x, Pos_y);
		}
	}
	private void drawAxis() {
		img = new BufferedImage(AxisLength, AxisLength, BufferedImage.TYPE_4BYTE_ABGR);
		this.setImage(img, AxisLength, AxisLength);
		g = (Graphics2D)img.getGraphics();
		g.setColor(Color.BLACK);
		g.setStroke(new BasicStroke(2));
		if(X_Mirror) {
			Off_X = AxisLength - TailLength;
			g.drawLine(AxisLength - TailLength, 0, AxisLength - TailLength, AxisLength);
		} else {
			Off_X = TailLength;
			g.drawLine(TailLength, 0, TailLength, AxisLength);
		}
		if(Y_Mirror) {
			Off_Y = AxisLength - TailLength;
			g.drawLine(0, AxisLength - TailLength, AxisLength, AxisLength - TailLength);
		} else {
			Off_Y = TailLength;
			g.drawLine(0, TailLength, AxisLength, TailLength);
		}
		if(X_Mirror) {
			if(Y_Mirror) {
				g.drawLine(0, AxisLength - TailLength, ArrowSize, (int)(AxisLength - TailLength - ArrowSize * ArrowAngleTan)); // h-axis
				g.drawLine(0, AxisLength - TailLength, ArrowSize, (int)(AxisLength - TailLength + ArrowSize * ArrowAngleTan));
				g.drawLine(AxisLength - TailLength, 0, (int)(AxisLength - TailLength - ArrowSize * ArrowAngleTan), ArrowSize); // v-axis
				g.drawLine(AxisLength - TailLength, 0, (int)(AxisLength - TailLength + ArrowSize * ArrowAngleTan), ArrowSize);
			} else {
				g.drawLine(0, TailLength, ArrowSize, (int)(TailLength - ArrowSize * ArrowAngleTan));
				g.drawLine(0, TailLength, ArrowSize, (int)(TailLength + ArrowSize * ArrowAngleTan));
				g.drawLine(AxisLength - TailLength, AxisLength, (int)(AxisLength - TailLength - ArrowSize * ArrowAngleTan), AxisLength - ArrowSize);
				g.drawLine(AxisLength - TailLength, AxisLength, (int)(AxisLength - TailLength + ArrowSize * ArrowAngleTan), AxisLength - ArrowSize);
			}
		} else {
			if(Y_Mirror) {
				g.drawLine(AxisLength, AxisLength - TailLength, AxisLength - ArrowSize, (int)(AxisLength - TailLength - ArrowSize * ArrowAngleTan));
				g.drawLine(AxisLength, AxisLength - TailLength, AxisLength - ArrowSize, (int)(AxisLength - TailLength + ArrowSize * ArrowAngleTan));
				g.drawLine(TailLength, 0, (int)(TailLength - ArrowSize * ArrowAngleTan), ArrowSize);
				g.drawLine(TailLength, 0, (int)(TailLength + ArrowSize * ArrowAngleTan), ArrowSize);
			} else {
				g.drawLine(AxisLength - ArrowSize, (int)(TailLength - ArrowSize * ArrowAngleTan), AxisLength, TailLength); // h-axis
				g.drawLine(AxisLength - ArrowSize, (int)(TailLength + ArrowSize * ArrowAngleTan), AxisLength, TailLength);
				g.drawLine((int)(TailLength - ArrowSize * ArrowAngleTan), AxisLength - ArrowSize, TailLength, AxisLength); // v-axis
				g.drawLine((int)(TailLength + ArrowSize * ArrowAngleTan), AxisLength - ArrowSize, TailLength, AxisLength);
			}
		}
	}
}
