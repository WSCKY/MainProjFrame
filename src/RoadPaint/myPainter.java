package RoadPaint;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

public class myPainter {
	private Graphics gCanvas = null;
	public myPainter(Graphics g) {
		gCanvas = g;
	}
	public void drawAnchorSign(Point p) {
		gCanvas.setColor(Color.BLUE);
		gCanvas.drawLine(p.x-10, p.y-10, p.x+10, p.y+10);
		gCanvas.drawLine(p.x-10, p.y+10, p.x+10, p.y-10);
		gCanvas.drawArc(p.x-7, p.y-7, 14, 14, 0, 360);
	}
	public void drawCoordinate(Point p) {
		gCanvas.setColor(Color.BLACK);
		gCanvas.drawLine(p.x-50, p.y-1, p.x+50, p.y-1);
		gCanvas.drawLine(p.x-50, p.y, p.x+50, p.y);
		gCanvas.drawLine(p.x-50, p.y+1, p.x+50, p.y+1);

		gCanvas.drawLine(p.x-1, p.y-50, p.x-1, p.y+50);
		gCanvas.drawLine(p.x, p.y-50, p.x, p.y+50);
		gCanvas.drawLine(p.x+1, p.y-50, p.x+1, p.y+50);
	}
	public void drawCircleShip(Point p, int r) {
		gCanvas.setColor(Color.RED);
		gCanvas.drawArc(p.x-r, p.y-r, r*2, r*2, 0, 360);
	}
}
