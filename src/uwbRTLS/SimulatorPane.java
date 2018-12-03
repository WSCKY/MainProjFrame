package uwbRTLS;

import java.awt.BorderLayout;
import java.util.concurrent.TimeUnit;

import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.UIManager;

import MainFrame.MyMainFrame;
import protocol.ComPackage;
import protocol.PackageTypes.TypeUWB;
import protocol.event.DecodeEvent;
import protocol.event.DecodeEventListener;
import uwbRTLS.CoordTranfer.CoordTrans;
import uwbRTLS.InstManager.AnchorManager;
import uwbRTLS.InstManager.AnchorManagerEvent;
import uwbRTLS.InstManager.AnchorManagerEventListener;
import uwbRTLS.InstManager.Instance.uwbAnchor;

public class SimulatorPane extends JPanel implements Runnable, DecodeEventListener, AnchorManagerEventListener {
	private static final long serialVersionUID = 1L;
	private static final int PainterWidth = 1000;
	private static final int PainterHeight = 600;
	private static final int DistDataNumber = 4;
//	private static final Color backColor = new Color(180, 180, 180);

	private ComPackage rxData = null;
	private double[] dist = new double[DistDataNumber];
	
	private JSplitPane SplitPanel = null;
	private JSplitPane toolSplit = null;
//	private Image img = null;
//	private Graphics gGraph = null;
	private myCanvas Canvas = null;

//	private myVehicle myTag = null;
//	private myPainter Painter = null;
	private AnchorManager anchorManager = null;
	private CoordTrans coordTrans = null;
	public SimulatorPane() {
		SplitPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
//		img = new BufferedImage(PainterWidth, PainterHeight, BufferedImage.TYPE_4BYTE_ABGR);
		Canvas = new myCanvas();//img
//		gGraph = img.getGraphics();
//		myTag = new myVehicle(gGraph);
//		myTag.update();
//		myTag.setName("kyChu");
//		instTag = new uwbInstance(0, 0, 0);
//		Painter = new myPainter(gGraph);
		coordTrans = new CoordTrans(PainterWidth, PainterHeight);
		coordTrans.setRealArea(4.0, 4.0);
		Canvas.setCoordTrans(coordTrans);
		anchorManager = new AnchorManager(coordTrans);
		anchorManager.addAnchorManagerListener(this);
		toolSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		toolSplit.setTopComponent(anchorManager);
		toolSplit.setBottomComponent(coordTrans);
		toolSplit.setOneTouchExpandable(true);
		toolSplit.setDividerSize(10);
		toolSplit.setDividerLocation(PainterHeight - 300);
		SplitPanel.setLeftComponent(Canvas);
		SplitPanel.setRightComponent(toolSplit);
		SplitPanel.setDividerLocation(PainterWidth - 300);
//		SplitPanel.setDividerSize(20);
		SplitPanel.setEnabled(false);
//		SplitPanel.setOneTouchExpandable(true);

		this.setLayout(new BorderLayout());
		this.add(SplitPanel, BorderLayout.CENTER);

		for(int i = 0; i < DistDataNumber; i ++) {
			dist[i] = 0;
		}
		(new Thread(this)).start();
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(true) {
//			refreshCanvas();
//			myTag.update();
//			testLocus();
			try {
				TimeUnit.MILLISECONDS.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void UpdateDistance(int index, double val) {
		if(index < DistDataNumber) {
			dist[index] = val;
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

	int deg = 0;
//	public void testLocus() {
////		gGraph.setColor(backColor);
////		gGraph.fillRect(0, 0, PainterWidth, PainterHeight);
//		double x = 3.0 + 0.8 * Math.sin(Math.toRadians(deg));
//		double y = 2.0 + 0.5 * Math.cos(Math.toRadians(deg));
//		deg += 6; if(deg > 360) deg -= 360;
//		Painter.addLocusPoint(coordTrans.Real2UI(x, y));
//		Painter.drawLocus();
//	}

//double x1,y1,x2,y2,x3,y3;
//	public void refreshCanvas() {
////		gGraph.setColor(backColor);
////		gGraph.fillRect(0, 0, PainterWidth, PainterHeight);
//		int n = anchorManager.getAnchorNumber();
//		uwbInstance inst = null;
//		if(n > 4) n = 4;
//		for(int i = 0; i < n; i ++) {
//			inst = anchorManager.getAnchor(i);
//			if(inst != null) {
//				Painter.drawAnchorSign(coordTrans.Real2UI(inst.getX(), inst.getY()));
//			}
//		}
//		if(n >= 3) {
//			inst = anchorManager.getAnchor(0);
//			x1 = inst.getX(); y1 = inst.getY();
//			Painter.drawCircleShip(coordTrans.Real2UI(x1, y1), coordTrans.Real2UI(dist[0]));
//			inst = anchorManager.getAnchor(1);
//			x2 = inst.getX(); y2 = inst.getY();
//			Painter.drawCircleShip(coordTrans.Real2UI(x2, y2), coordTrans.Real2UI(dist[1]));
//			inst = anchorManager.getAnchor(2);
//			x3 = inst.getX(); y3 = inst.getY();
//			Painter.drawCircleShip(coordTrans.Real2UI(x3, y3), coordTrans.Real2UI(dist[2]));
//			double[] ret = CompPosition(x1, y1, dist[0], x2, y2, dist[1], x3, y3, dist[2]);
//			Point p = coordTrans.Real2UI(ret[0], ret[1]);
//			myTag.moveTo(p.x, p.y);
//		}
//	}

	@Override
	public void AnchorUpdated(AnchorManagerEvent event) {
		// TODO Auto-generated method stub
		uwbAnchor anchor = (uwbAnchor)event.getSource();
		switch(event.getType()) {
		case AnchorManagerEvent.ADD:
			Canvas.addLayer(anchor.getUI());
			break;
		case AnchorManagerEvent.DEL:
			Canvas.delLayer(anchor.getUI());
			break;
		case AnchorManagerEvent.MOV:
			break;
		case AnchorManagerEvent.STA:
			break;
		default: break;
		}
	}

	float[] recDist = {0, 0, 0, 0};
	@Override
	public void getNewPackage(DecodeEvent event) {
		// TODO Auto-generated method stub
		rxData = (ComPackage)event.getSource();
		if(rxData.type == TypeUWB.TYPE_COM_HEARTBEAT) {}
		else if(rxData.type == TypeUWB.TYPE_DIST_Response) {}
		else if(rxData.type == TypeUWB.TYPE_DIST_GROUP_Resp) {
			int cnt = rxData.rData[1];
			for(int i = 0; i < cnt; i ++) {
				recDist[rxData.rData[i * 5 + 3]] = rxData.readoutFloat(i * 5 + 4);
			}
			if(recDist[0] < 0) recDist[0] = 0;
			if(recDist[1] < 0) recDist[1] = 0;
			if(recDist[2] < 0) recDist[2] = 0;
			if(recDist[0] < 40)
				dist[0] = recDist[0] * 0.1 + dist[0] * 0.9;
			if(recDist[1] < 40)
				dist[1] = recDist[1] * 0.1 + dist[1] * 0.9;
			if(recDist[2] < 40)
				dist[2] = recDist[2] * 0.1 + dist[2] * 0.9;
		}
	}
	@Override
	public void badCRCEvent(DecodeEvent event) {
		// TODO Auto-generated method stub
		
	}

	public static void main(String[] args) {
		try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Couldn't use system look and feel.");
        }
		MyMainFrame mf = new MyMainFrame();
		mf.setTitle("Simulator Test");
		mf.setFrameSize(PainterWidth, PainterHeight);
		SimulatorPane sp = new SimulatorPane(); 
		JPanel mp = mf.getUsrMainPanel();
		mp.setLayout(new BorderLayout());
		mp.add(sp, BorderLayout.CENTER);
		mf.getDecoder().addDecodeListener(sp);
		mf.setResizable(false);
		mf.setVisible(true);
	}
}
