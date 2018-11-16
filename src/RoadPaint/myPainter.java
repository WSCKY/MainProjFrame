package RoadPaint;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;

public class myPainter {
	private Graphics gCanvas = null;

	private static final int MAX_POINT = 50;
	private static int pointIndexCnt = 0;
	private static Point[] pointList = new Point[MAX_POINT];
	public myPainter(Graphics g) {
		gCanvas = g;
		for(int i = 0; i < MAX_POINT; i ++) {
			pointList[i] = new Point(0, 0);
		}
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
	public void drawBoldLine(Point a, Point b, int w, Color c) {
		Graphics2D g2d = (Graphics2D)gCanvas;
		g2d.setStroke(new BasicStroke(w));
		g2d.drawLine(a.x, a.y, b.x, b.y);
	}

	public void addLocusPoint(Point p) {
		pointList[pointIndexCnt] = p;
		pointIndexCnt ++;
		if(pointIndexCnt >= MAX_POINT)
			pointIndexCnt = 0;
	}
	public void drawLocus() {
		int i = pointIndexCnt, j = 0;
		int colorStep = 255 / MAX_POINT;
		int alpha = 255;
		Point p1, p2 = new Point(0, 0);
		Graphics2D g2d = (Graphics2D)gCanvas;
		g2d.setStroke(new BasicStroke(5));
		do {
			alpha = j * colorStep;
			g2d.setColor(new Color(255, 0, 0, alpha));
			p1 = pointList[i ++];
			if(i >= MAX_POINT) i = 0;
			if(j > 0) {
				g2d.drawLine(p2.x, p2.y, p1.x, p1.y);
			}
			p2 = p1;
			j ++;
		} while(j < MAX_POINT);
	}
}
