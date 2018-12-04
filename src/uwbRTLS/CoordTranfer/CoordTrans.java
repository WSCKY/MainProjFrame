package uwbRTLS.CoordTranfer;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;

public class CoordTrans {
	private int UI_Width = 800, UI_Height = 800; // default 800x800 (pixels)
	private double Real_xSize = 20.0, Real_ySize = 20.0; // default 20.0x20.0 (unit:m)
	private int UI_OrgX = 200, UI_OrgY = 200; // default origin.

	private double TransGain = 1.0;

	private boolean XY_SWAP = false;
	private boolean X_Mirror = false;
	private boolean Y_Mirror = false;

	private ArrayList<CoordTransEventListener> Listeners = new ArrayList<CoordTransEventListener>();

	public CoordTrans() { updateGain(); }
	public CoordTrans(int w, int h) {
		setUIArea(w, h);
		updateGain();
	}
	public CoordTrans(double x, double y) {
		setRealArea(x, y);
		updateGain();
	}
	public CoordTrans(int w, int h, double x, double y) {
		setUIArea(w, h);
		setRealArea(x, y);
		updateGain();
	}

	public void setUIArea(int w, int h) {
		UI_Width = w;
		UI_Height = h;
		updateGain();
	}
	public void setRealArea(double x, double y) {
		Real_xSize = x;
		Real_ySize = y;
		updateGain();
	}

	public void setTransGain(double gain) {
		this.TransGain = gain;
		publishListener();
	}
	public double getTransGain() {
		return TransGain;
	}

	public void swap(boolean flag) {
		XY_SWAP = flag;
		publishListener();
	}
	public void x_mirror(boolean flag) {
		X_Mirror = flag;
		publishListener();
	}
	public void y_mirror(boolean flag) {
		Y_Mirror = flag;
		publishListener();
	}
	public boolean isXY_SWAP() {
		return XY_SWAP;
	}
	public boolean isX_Mirror() {
		return X_Mirror;
	}
	public boolean isY_Mirror() {
		return Y_Mirror;
	}

	public void move(int x, int y) {
		UI_OrgX += x;
		UI_OrgY += y;
		publishListener();
	}
	public void moveTo(int x, int y) {
		UI_OrgX = x;
		UI_OrgY = y;
		publishListener();
	}
	public void zoom(double scale) {
		TransGain *= scale;
		publishListener();
	}

	private void publishListener() {
		CoordTransEvent event = new CoordTransEvent(this);
		for(CoordTransEventListener listener : Listeners) {
			listener.CoordinateUpdate(event);
		}
	}
	public void addCoordTransEventListener(CoordTransEventListener listener) {
		Listeners.add(listener);
	}

	public void updateGain() {
		double s1, s2;
		int gap = (int) (((UI_Width < UI_Height) ? UI_Width : UI_Height) * 0.07);
		if(gap > 50) gap = 50;
		if(XY_SWAP) {
			s1 = (double)(UI_Width - gap * 2) / Real_xSize;
			s2 = (double)(UI_Height - gap * 2) / Real_ySize;
		} else {
			s1 = (double)(UI_Width - gap * 2) / Real_ySize;
			s2 = (double)(UI_Height - gap * 2) / Real_xSize;
		}

		TransGain = (s1 < s2) ? s1 : s2;
		publishListener();
	}

	public int Real2UI(double l) { return (int) (l * TransGain); }
	public Point Real2UI(double x, double y) {
		int p;
		int px = (int) (x * TransGain);
		int py = (int) (y * TransGain);
		if(XY_SWAP) { p = px; px = py; py = p; }
		if(X_Mirror) { px = -px; }
		if(Y_Mirror) { py = -py; }
		
		return (new Point(px + UI_OrgX, py + UI_OrgY));
	}
	public double UI2Real(int l) { return l / TransGain; }
	public Point2D.Double UI2Real(int x, int y) {
		int p;
		x = x - UI_OrgX; y = y - UI_OrgY;
		if(XY_SWAP) { p = x; x = y; y = p; }
		if(X_Mirror) { x = -x; }
		if(Y_Mirror) { y = -y; }
		return (new Point2D.Double(x / TransGain, y / TransGain));
	}
}
