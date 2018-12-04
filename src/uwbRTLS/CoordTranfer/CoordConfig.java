package uwbRTLS.CoordTranfer;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
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

public class CoordConfig extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private CoordTrans Coord = null;
	private double UserScale = 1.0;

	private JCheckBox swapCB = new JCheckBox("SWAP");
	private JCheckBox xMirCB = new JCheckBox("MirrorX");
	private JCheckBox yMirCB = new JCheckBox("MirrorY");
	private JSlider scaleSlider = new JSlider(-100, 100);
	Dictionary<Integer, Component> labelTable = new Hashtable<Integer, Component>();

	public CoordConfig(CoordTrans coord) {
		Coord = coord;
		initUI();
	}

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
				Coord.swap(swapCB.isSelected());
			} else if(cb == xMirCB) {
				Coord.x_mirror(xMirCB.isSelected());
			} else if(cb == yMirCB) {
				Coord.y_mirror(yMirCB.isSelected());
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
			Coord.setTransGain((Coord.getTransGain() * UserScale));
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

	public static void main(String[] args) {
		try {
            UIManager.setLookAndFeel(
                UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Couldn't use system look and feel.");
        }
		JFrame f = new JFrame("coordinate setting");
		CoordTrans coord = new CoordTrans();
		CoordConfig configPane = new CoordConfig(coord);
		f.add(configPane);
		f.setSize(400, 300);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setLocation(800, 300);
		f.setResizable(true);
		f.setVisible(true);
	}
}
