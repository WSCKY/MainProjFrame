package RoadPaint;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.geom.Point2D.Double;
import java.awt.image.BufferedImage;
import java.util.concurrent.TimeUnit;

import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.UIManager;

import MainFrame.MyMainFrame;
import protocol.ComPackage;
import protocol.RxAnalyse;
import protocol.PackageTypes.TypeUWB;

public class RoadPainter extends MyMainFrame {

	/**
	 * start at 2018/10/29
	 */
	private static final long serialVersionUID = 1L;

	private static final int PainterWidth = 1000;
	private static final int PainterHeight = 600;
	private static final Color backColor = new Color(180, 180, 180);

//	private static ComPackage txData = null;
	private static ComPackage rxData = new ComPackage();

	private JSplitPane SplitPanel = null;
	private JSplitPane toolSplit = null;
	private JPanel MainPanel = null;
	private Image img = null;
	private Graphics gGraph = null;
	private myCanvas Drawer = null;

	private myVehicle myTag = null;
	private myPainter Painter = null;
	private AnchorManager anchorManager = null;
	private CoordTrans coordTrans = null;
//	private uwbInstance instTag = null;

	public RoadPainter() {
		this.setFrameSize(PainterWidth, PainterHeight);
		MainPanel = this.getUsrMainPanel();
		MainPanel.setLayout(new BorderLayout());
		SplitPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		img = new BufferedImage(PainterWidth, PainterHeight, BufferedImage.TYPE_4BYTE_ABGR);
		Drawer = new myCanvas(img);
		gGraph = img.getGraphics();
		myTag = new myVehicle(gGraph);
		myTag.update();
		myTag.setName("kyChu");
//		instTag = new uwbInstance(0, 0, 0);
		Painter = new myPainter(gGraph);
		anchorManager = new AnchorManager();
		coordTrans = new CoordTrans(PainterWidth, PainterHeight);
		coordTrans.setRealArea(4.0, 4.0);
		toolSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		toolSplit.setTopComponent(anchorManager);
		toolSplit.setBottomComponent(coordTrans);
		toolSplit.setOneTouchExpandable(true);
		toolSplit.setDividerSize(10);
		toolSplit.setDividerLocation(PainterHeight - 300);
		SplitPanel.setLeftComponent(Drawer);
		SplitPanel.setRightComponent(toolSplit);
		SplitPanel.setDividerLocation(PainterWidth - 300);
//		SplitPanel.setDividerSize(20);
		SplitPanel.setEnabled(false);
//		SplitPanel.setOneTouchExpandable(true);
		MainPanel.add(SplitPanel);
//		MainPanel.add(Drawer, BorderLayout.CENTER);
		this.setResizable(false);
		this.setVisible(true);

		new Thread(new TestThread()).start();
	}

	private class TestThread implements Runnable {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			while(true) {
//				testUpdate();
//				testTril(gGraph);
				refreshCanvas();
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

	float[] recDist = {0, 0, 0, 0};
	public void RxDataProcess() {
		synchronized(new String("")) {
			try {
				rxData = (ComPackage) RxAnalyse.RecPackage.PackageCopy();
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}
		}
		if(rxData.type == TypeUWB.TYPE_COM_HEARTBEAT) {
//			System.out.println("_cnt: " + (int)rxData.rData[0]);
		} else if(rxData.type == TypeUWB.TYPE_DIST_Response) {
			recDist[0] = rxData.readoutInteger(3) / 10.0f;
			recDist[1] = rxData.readoutInteger(7) / 10.0f;
			recDist[2] = rxData.readoutInteger(11) / 10.0f;
			recDist[3] = rxData.readoutInteger(15) / 10.0f;
		} else if(rxData.type == TypeUWB.TYPE_DIST_GROUP_Resp) {
			int cnt = rxData.rData[1];
			for(int i = 0; i < cnt; i ++) {
				recDist[rxData.rData[i * 5 + 3]] = rxData.readoutFloat(i * 5 + 4);
			}
			if(recDist[0] < 0) recDist[0] = 0;
			if(recDist[1] < 0) recDist[1] = 0;
			if(recDist[2] < 0) recDist[2] = 0;
			if(recDist[0] < 40)
				r0 = recDist[0] * 0.1 + r0 * 0.9;
			if(recDist[1] < 40)
				r1 = recDist[1] * 0.1 + r1 * 0.9;
			if(recDist[2] < 40)
				r2 = recDist[2] * 0.1 + r2 * 0.9;
//			System.out.println(String.format("%d", rxData.rData[2] & 0xff));
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

//	Point2D.Double ExpPoint = new Point2D.Double(0, 0);
//	int degree = 0;
//	int wd = 18;
//	public void testUpdate() {
//		degree += 8; if(degree >= 360) degree -= 360;
//		ExpPoint.x = (6 + Math.sin(Math.toRadians(degree)));
//		ExpPoint.y = (6 + Math.cos(Math.toRadians(degree)));
//		wd = (int) (27 + 9 * Math.sin(Math.toRadians(degree)));
//		myTag.setZoomTo(wd);
//		myTag.setYaw(-degree + 90);
//	}

//	double dist = 5;
//	double r0 = 0, rx = 0, ry = 0;
//	public void testTril(Graphics g) {
//		g.setColor(backColor);
//		g.fillRect(0, 0, PainterWidth, PainterHeight);
//		g.setColor(Color.BLUE);
//		Painter.drawCoordinate(coordTrans.Real2UI(0, 0)); // draw coordinate.
//		Point2D.Double OrgPoint = new Point2D.Double(-2, -2);
//		Painter.drawAnchorSign(coordTrans.Real2UI(OrgPoint.x, OrgPoint.y));
//		Point2D.Double xPoint = new Point2D.Double(dist, 0);
//		Painter.drawAnchorSign(coordTrans.Real2UI(xPoint.x, xPoint.y));
//		Point2D.Double yPoint = new Point2D.Double(0, dist);
//		Painter.drawAnchorSign(coordTrans.Real2UI(yPoint.x, yPoint.y));
//		r0 = distance(ExpPoint, OrgPoint);
//		rx = distance(ExpPoint, xPoint);
//		ry = distance(ExpPoint, yPoint);
//		double[] ret = CompPosition(OrgPoint.x, OrgPoint.y, r0, xPoint.x, xPoint.y, rx, yPoint.x, yPoint.y, ry);
//		Point p = coordTrans.Real2UI(ret[0], ret[1]);
//		myTag.moveTo(p.x, p.y);
//	}
double r0 = 0, r1 = 0, r2 = 0;
double x1,y1,x2,y2,x3,y3;
	public void refreshCanvas() {
		gGraph.setColor(backColor);
		gGraph.fillRect(0, 0, PainterWidth, PainterHeight);
		int n = anchorManager.getAnchorNumber();
		uwbInstance inst = null;
		if(n > 4) n = 4;
		for(int i = 0; i < n; i ++) {
			inst = anchorManager.getAnchor(i);
			if(inst != null) {
				Painter.drawAnchorSign(coordTrans.Real2UI(inst.getX(), inst.getY()));
			}
		}
		if(n >= 3) {
			inst = anchorManager.getAnchor(0);
			x1 = inst.getX(); y1 = inst.getY();
			Painter.drawCircleShip(coordTrans.Real2UI(x1, y1), coordTrans.Real2UI(r0));
			inst = anchorManager.getAnchor(1);
			x2 = inst.getX(); y2 = inst.getY();
			Painter.drawCircleShip(coordTrans.Real2UI(x2, y2), coordTrans.Real2UI(r1));
			inst = anchorManager.getAnchor(2);
			x3 = inst.getX(); y3 = inst.getY();
			Painter.drawCircleShip(coordTrans.Real2UI(x3, y3), coordTrans.Real2UI(r2));
			double[] ret = CompPosition(x1, y1, r0, x2, y2, r1, x3, y3, r2);
			Point p = coordTrans.Real2UI(ret[0], ret[1]);
			myTag.moveTo(p.x, p.y);
		}
	}

	public double distance(Double expPoint2, Double orgPoint) {
		return Math.sqrt((expPoint2.x - orgPoint.x) * (expPoint2.x - orgPoint.x) + (expPoint2.y - orgPoint.y) * (expPoint2.y - orgPoint.y));
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
