package RoadPaint;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Point2D;
import java.util.Dictionary;
import java.util.Hashtable;

import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class CoordTrans extends JPanel {
	private static final long serialVersionUID = 1L;

	private JCheckBox swapCB = new JCheckBox("SWAP");
	private JCheckBox xMirCB = new JCheckBox("MirrorX");
	private JCheckBox yMirCB = new JCheckBox("MirrorY");
	private JSlider scaleSlider = new JSlider(-100, 100);
	Dictionary<Integer, Component> labelTable = new Hashtable<Integer, Component>();

	private int UI_Width = 800, UI_Height = 800; // default 800x800 (pixels)
	private double Real_xSize = 20.0, Real_ySize = 20.0; // default 20.0x20.0 (unit:m)
	private int UI_OrgX = 200, UI_OrgY = 200; // default origin.

	private double TransGain = 1.0;
	private double UserScale = 1.0;

	private boolean XY_SWAP = false;
	private boolean X_Mirror = false;
	private boolean Y_Mirror = false;

	private void initUI() {
		this.setLayout(new BorderLayout());
		JPanel p = new JPanel();
		p.setLayout(new GridLayout(3, 1));
		p.add(swapCB); p.add(xMirCB); p.add(yMirCB);
		swapCB.setFont(new Font("Courier New", Font.BOLD, 16));
		xMirCB.setFont(new Font("Courier New", Font.BOLD, 16));
		yMirCB.setFont(new Font("Courier New", Font.BOLD, 16));
		swapCB.addItemListener(ChangeIL);
		xMirCB.addItemListener(ChangeIL);
		yMirCB.addItemListener(ChangeIL);
		this.add(p, BorderLayout.CENTER);
		scaleSlider.setPaintLabels(true);
		labelTable.put(-100, new JLabel("-")); labelTable.put(0, new JLabel("0")); labelTable.put(100, new JLabel("+"));
		scaleSlider.setLabelTable(labelTable);
		scaleSlider.setPreferredSize(new Dimension(250, 40));
		scaleSlider.addChangeListener(SliderCL);
		scaleSlider.addMouseListener(SliderML);
		JPanel ps = new JPanel();
		ps.add(scaleSlider);
		this.add(ps, BorderLayout.SOUTH);
	}

	public CoordTrans() { initUI(); updateGain(); }
	public CoordTrans(int w, int h) {
		initUI();
		setUIArea(w, h);
		updateGain();
	}
	public CoordTrans(double x, double y) {
		initUI();
		setRealArea(x, y);
		updateGain();
	}
	public CoordTrans(int w, int h, double x, double y) {
		initUI();
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
	}

	ChangeListener SliderCL = new ChangeListener() {
		@Override
		public void stateChanged(ChangeEvent e) {
			// TODO Auto-generated method stub
			int v = ((JSlider)e.getSource()).getValue();
			if(v > 0) UserScale = 1.0 + ((double)v / 100);
			if(v < 0) UserScale = 1.0 + ((double)v / 200);
		}
	};
	ItemListener ChangeIL = new ItemListener() {
		@Override
		public void itemStateChanged(ItemEvent e) {
			// TODO Auto-generated method stub
			JCheckBox cb = (JCheckBox)e.getSource();
			if(cb == swapCB) {
				XY_SWAP = swapCB.isSelected();
//				System.out.println("SWAP:"+XY_SWAP);
			} else if(cb == xMirCB) {
				X_Mirror = xMirCB.isSelected();
//				System.out.println("X:"+X_Mirror);
			} else if(cb == yMirCB) {
				Y_Mirror = yMirCB.isSelected();
//				System.out.println("Y:"+Y_Mirror);
			}
		}
	};
	MouseListener SliderML = new MouseListener() {
		@Override
		public void mouseClicked(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void mousePressed(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void mouseReleased(MouseEvent e) {
			// TODO Auto-generated method stub
			TransGain = TransGain * UserScale;
			UserScale = 1.0;
			scaleSlider.setValue(0);
		}
		@Override
		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}
	};

	public int Real2UI(double l) { return (int) (l * TransGain * UserScale); }
	public Point Real2UI(double x, double y) {
		int p;
		int px = (int) (x * TransGain * UserScale);
		int py = (int) (y * TransGain * UserScale);
		if(XY_SWAP) { p = px; px = py; py = p; }
		if(X_Mirror) { px = -px; }
		if(Y_Mirror) { py = -py; }
		
		return (new Point(px + UI_OrgX, py + UI_OrgY));
	}
	public double UI2Real(int l) { return l / (TransGain * UserScale); }
	public Point2D.Double UI2Real(int x, int y) {
		int p;
		x = x - UI_OrgX; y = y - UI_OrgY;
		if(XY_SWAP) { p = x; x = y; y = p; }
		if(X_Mirror) { x = -x; }
		if(Y_Mirror) { y = -y; }
		return (new Point2D.Double(x / (TransGain * UserScale), y / (TransGain * UserScale)));
	}

	public static void main(String[] args) {
		try {
            UIManager.setLookAndFeel(
                UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Couldn't use system look and feel.");
        }
		JFrame f = new JFrame("coordinate setting");
		CoordTrans coord = new CoordTrans();
		f.add(coord);
		f.setSize(400, 300);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setLocation(800, 300);
		f.setResizable(true);
		f.setVisible(true);
	}
}
