package RoadPaint;

//import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
//import java.awt.event.MouseAdapter;
//import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
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

	private JPanel MainPanel = null;
	private Image img = null;
	private Graphics gPointer = null;
	private myPanel Drawer = new myPanel();

	private Point2D.Double[] pCross = new Point2D.Double[2];

	public RoadPainter() {
		this.setFrameSize(1000, 600);
		MainPanel = this.getUsrMainPanel();
		MainPanel.setLayout(new BorderLayout());
		MainPanel.add(Drawer, BorderLayout.CENTER);
		img = new BufferedImage(1000, 600, BufferedImage.TYPE_INT_RGB);
		Drawer.setImage(img);
		gPointer = img.getGraphics();
		this.setResizable(true);
		this.setVisible(true);

		pCross[0] = new Point2D.Double(0, 0);
		pCross[1] = new Point2D.Double(0, 0);

		new Thread(new TestThread()).start();
	}
	
	private class TestThread implements Runnable {
		private Point pTest = new Point(0, 60);
		@Override
		public void run() {
			// TODO Auto-generated method stub
			MainPaint(gPointer);
			while(true) {
				pTest.x += 60;
				if(pTest.x >= 600) {
					pTest.x = 60;
					pTest.y += 60;
					if(pTest.y >= 600) {
						pTest.y = 60;
					}
				}
				DrawSignPoint(img.getGraphics(), pTest);
				try {
					TimeUnit.SECONDS.sleep(1);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public void MainPaint(Graphics g) {
		g.setColor(Color.BLUE);
		Point OrgPoint = new Point(this.getWidth() / 2, this.getHeight() / 2);
		DrawSignPoint(g, OrgPoint);
		int ta1 = 100, ta2 = 80, ta3 = 80;
		Point AncPoint1 = new Point(OrgPoint.x, OrgPoint.y - ta1);
		DrawSignPoint(g, AncPoint1);
		g.setColor(Color.red);
		g.drawLine(OrgPoint.x, OrgPoint.y, OrgPoint.x, OrgPoint.y - ta1);
		myCircle c1 = new myCircle(OrgPoint.x, OrgPoint.y, ta2);
		myCircle c2 = new myCircle(AncPoint1.x, AncPoint1.y, ta3);
		DrawCircle(g, c1);
		DrawCircle(g, c2);
		int idx = c1.CrossCircle(c2, pCross);
		if(idx >= 1) DrawSignPoint(g, new Point((int)pCross[0].x, (int)pCross[0].y));
		if(idx == 2) DrawSignPoint(g, new Point((int)pCross[1].x, (int)pCross[1].y));
	}
	public void DrawSignPoint(Graphics g, Point p) {
		Color org_color = g.getColor();
		g.setColor(Color.BLUE);
		g.fillArc(p.x - 5, p.y - 5, 10, 10, 0, 360);
		g.drawString("("+ p.x + ", " + p.y + ")", p.x, p.y);
		g.setColor(org_color);
	}
	public void DrawCircle(Graphics g, myCircle c) {
		Color org_color = g.getColor();
		g.setColor(Color.RED);
		g.drawArc((int)(c.getX() - c.getRadius()), (int)(c.getY() - c.getRadius()), (int)c.getRadius() * 2, (int)c.getRadius() * 2, 0, 360);
		g.setColor(org_color);
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

class myPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	private Image img;
	public myPanel() {}
	public void setImage(Image img) {
		if(img != null) {
			this.img = img;
		}
	}

	public void paint(Graphics g) {
		g.drawImage(img, 0, 0, this);
	}
}
