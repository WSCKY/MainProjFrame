package RoadPaint;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.concurrent.TimeUnit;

import javax.swing.JPanel;
import javax.swing.UIManager;

import MainFrame.MyMainFrame;

public class RoadPainter extends MyMainFrame {

	/**
	 * start at 2018/10/29
	 */
	private static final long serialVersionUID = 1L;

	private static final int PainterWidth = 1000;
	private static final int PainterHeight = 600;
	private static final Color backColor = new Color(180, 180, 180);

	private JPanel MainPanel = null;
	private Image img = null;
	private Graphics gPointer = null;
	private myCanvas Drawer = new myCanvas();
	
	private myVehicle myTag = null;

	public RoadPainter() {
		this.setFrameSize(PainterWidth, PainterHeight);
		MainPanel = this.getUsrMainPanel();
		MainPanel.setLayout(new BorderLayout());
		MainPanel.add(Drawer, BorderLayout.CENTER);
		img = new BufferedImage(PainterWidth, PainterHeight, BufferedImage.TYPE_INT_RGB);
		Drawer.setImage(img);
		gPointer = img.getGraphics();
		myTag = new myVehicle(gPointer);
		myTag.update();

		this.setResizable(true);
		this.setVisible(true);

		new Thread(new TestThread()).start();
	}

	private class TestThread implements Runnable {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			while(true) {
				testUpdate();
				testTril(gPointer);
				myTag.update();
				try {
					TimeUnit.MILLISECONDS.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public double[] CompPosition(double x1, double y1, double d1,
            double x2, double y2, double d2,
            double x3, double y3, double d3) {
		double[] d = {0.0, 0.0};
		double a11 = 2 * (x1 - x3);
		double a12 = 2 * (y1 - y3);
		double b1 = Math.pow(x1, 2) - Math.pow(x3, 2)
		+ Math.pow(y1, 2) - Math.pow(y3, 2)
		+ Math.pow(d3, 2) - Math.pow(d1, 2);
		double a21 = 2 * (x2 - x3);
		double a22 = 2 * (y2 - y3);
		double b2 = Math.pow(x2, 2) - Math.pow(x3, 2)
		+ Math.pow(y2, 2) - Math.pow(y3, 2)
		+ Math.pow(d3, 2) - Math.pow(d2, 2);

		d[0] = (b1 * a22 - a12 * b2) / (a11 * a22 - a12 * a21);
		d[1] = (a11 * b2 - b1 * a21) / (a11 * a22 - a12 * a21);

		return d;
	}

	Point ExpPoint = new Point(0, 0);
	int degree = 0;
	int wd = 18;
	public void testUpdate() {
		degree += 8; if(degree >= 360) degree -= 360;
		ExpPoint.x = (int) (100 + 30 * Math.sin(Math.toRadians(degree)));
		ExpPoint.y = (int) (100 + 30 * Math.cos(Math.toRadians(degree)));
		wd = (int) (27 + 9 * Math.sin(Math.toRadians(degree)));
		myTag.setZoomTo(wd);
		myTag.setYaw(-degree + 90);
	}

	int dist = 150;
	int r0 = 0, rx = 0, ry = 0;
	public void testTril(Graphics g) {
		g.setColor(backColor);
		g.fillRect(0, 0, PainterWidth, PainterHeight);
		g.setColor(Color.BLUE);
		Point OrgPoint = new Point(0, 0);
		DrawSignPoint(g, offPoint(OrgPoint));
		Point xPoint = new Point(dist, 0);
		DrawSignPoint(g, offPoint(xPoint));
		DrawLine(g, offPoint(OrgPoint), offPoint(xPoint));
		Point yPoint = new Point(0, dist);
		DrawSignPoint(g, offPoint(yPoint));
		DrawLine(g, offPoint(OrgPoint), offPoint(yPoint));
		r0 = (int) distance(ExpPoint, OrgPoint);
		rx = (int) distance(ExpPoint, xPoint);
		ry = (int) distance(ExpPoint, yPoint);
		myCircle c0 = new myCircle(offPoint(OrgPoint).x, offPoint(OrgPoint).y, r0); DrawCircle(g, c0);
		myCircle cx = new myCircle(offPoint(xPoint).x, offPoint(xPoint).y, rx); DrawCircle(g, cx);
		myCircle cy = new myCircle(offPoint(yPoint).x, offPoint(yPoint).y, ry); DrawCircle(g, cy);
		double[] ret = CompPosition(offPoint(OrgPoint).x, offPoint(OrgPoint).y, r0,
				offPoint(xPoint).x, offPoint(xPoint).y, rx,
				offPoint(yPoint).x, offPoint(yPoint).y, ry);
		myTag.moveTo((int)ret[0], (int)ret[1]);
	}
	
	public Point offPoint(Point p) {
		int off_x = 300;
		int off_y = 200;
		return new Point(p.x + off_x, p.y + off_y);
	}

	public void DrawSignPoint(Graphics g, Point p) {
		Color org_color = g.getColor();
		g.setColor(Color.BLUE);
		g.fillArc(p.x - 5, p.y - 5, 10, 10, 0, 360);
		g.drawString("("+ p.x + ", " + p.y + ")", p.x - 25, p.y - 5);
		g.setColor(org_color);
	}
	public void DrawCircle(Graphics g, myCircle c) {
		Color org_color = g.getColor();
		g.setColor(Color.RED);
		g.drawArc((int)(c.getX() - c.getRadius()), (int)(c.getY() - c.getRadius()), (int)c.getRadius() * 2, (int)c.getRadius() * 2, 0, 360);
		g.setColor(org_color);
	}
	public void DrawLine(Graphics g, Point p1, Point p2) {
		Color org_color = g.getColor();
		g.setColor(Color.RED);
		g.drawLine(p1.x, p1.y, p2.x, p2.y);
		g.setColor(org_color);
	}

	public double distance(Point px, Point py) {
		return Math.sqrt((px.x - py.x) * (px.x - py.x) + (px.y - py.y) * (px.y - py.y));
	}

	public static void main(String[] args) {
		try {
            UIManager.setLookAndFeel(
                UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Couldn't use system look and feel.");
        }
		new RoadPainter();
	}
}
