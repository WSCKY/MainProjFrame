package uwbRTLS.InstManager;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import uwbRTLS.CoordTranfer.CoordTrans;
import uwbRTLS.CoordTranfer.CoordTransEvent;
import uwbRTLS.CoordTranfer.CoordTransEventListener;
import uwbRTLS.InstManager.Instance.uwbAnchor;
import uwbRTLS.InstManager.Instance.uwbInstance;
import uwbRTLS.signComponent.signAnchor;

public class AnchorManager extends JPanel implements CoordTransEventListener {
	private static final long serialVersionUID = 1L;

	private static int AnchorCount = 0;
	private static double px = 0;
	private static double py = 0;
	private static double pz = 0;
	private static String[] ColumnNames = {"EN", "ID", "X(m)", "Y(m)", "Z(m)"};
	private DefaultTableModel Model = null;
	private JTable mTable = null;
	private JLabel textLab = null;
	private JTextField xText = null, yText = null, zText = null;
	private JButton addBtn = null, delBtn = null;
	private CoordTrans coordTrans = null;
	private ArrayList<uwbAnchor> AnchorList = new ArrayList<uwbAnchor>();
	private ArrayList<AnchorManagerEventListener> Listeners = new ArrayList<AnchorManagerEventListener>();
	public AnchorManager(CoordTrans coord) {
		coordTrans = coord;
		coordTrans.addCoordTransEventListener(this);
		this.setLayout(new BorderLayout());
		Model = new DefaultTableModel(null, ColumnNames);
		mTable = new JTable(Model);
		addBtn = new JButton("Add"); addBtn.setFont(new Font("Courier New", Font.BOLD, 20));
		delBtn = new JButton("Del"); delBtn.setFont(new Font("Courier New", Font.BOLD, 20));
		Model.addTableModelListener(TableListener);
		DefaultTableCellRenderer tcr = new DefaultTableCellRenderer();
		tcr.setHorizontalAlignment(SwingConstants.CENTER);
		mTable.setDefaultRenderer(Object.class, tcr);
		mTable.getTableHeader().setDefaultRenderer(tcr);
		TableColumn tc = mTable.getColumnModel().getColumn(0);
		tc.setCellEditor(mTable.getDefaultEditor(Boolean.class));
		tc.setCellRenderer(mTable.getDefaultRenderer(Boolean.class));
		mTable.setRowHeight(25);
		mTable.setFont(new Font("Courier New", Font.PLAIN, 14));

		xText = new JTextField("0");
		xText.setPreferredSize(new Dimension(50, 20));
		xText.setFont(new Font("Courier New", Font.PLAIN, 14));
		yText = new JTextField("0");
		yText.setPreferredSize(new Dimension(50, 20));
		yText.setFont(new Font("Courier New", Font.PLAIN, 14));
		zText = new JTextField("2");
		zText.setPreferredSize(new Dimension(50, 20));
		zText.setFont(new Font("Courier New", Font.PLAIN, 14));
		textLab = new JLabel("Add to(m): ");
		textLab.setPreferredSize(new Dimension(90, 20));
		textLab.setFont(new Font("Courier New", Font.PLAIN, 14));
		addBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				addAnchor();
			}
		});
		delBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				delAnchor();
			}
		});

		JScrollPane sp = new JScrollPane();
		sp.setViewportView(mTable);
		this.add(sp, BorderLayout.CENTER);
		JPanel p = new JPanel();
		JPanel p1 = new JPanel();
		JPanel p2 = new JPanel();
		p1.add(textLab); p1.add(xText); p1.add(yText); p1.add(zText);
		p2.setLayout(new FlowLayout(FlowLayout.CENTER, 50, 5));
		p2.add(addBtn); p2.add(delBtn);
		p.setLayout(new GridLayout(2, 1));
		p.add(p1); p.add(p2);
		this.add(p, BorderLayout.SOUTH);
	}
	public AnchorManager(CoordTrans coord, int defaultNumber) {
		this(coord);
		do {
			addAnchor();
			defaultNumber --;
		} while(defaultNumber > 0);
	}
	public int getAnchorNumber() { return AnchorCount; }
	private TableModelListener TableListener = new TableModelListener() {
		@Override
		public void tableChanged(TableModelEvent e) {
			// TODO Auto-generated method stub
			int type = e.getType(); // get event type.
            int row = e.getFirstRow(); // get row index.
            int column = e.getColumn(); // get line index.
            if(type == TableModelEvent.INSERT) {
//                System.out.println("Insert row " + row + ".");
            }else if(type == TableModelEvent.UPDATE) {
//                System.out.println("(" + row + ", " + column + ") Update.");
            	uwbAnchor anchor = AnchorList.get(row);
                uwbInstance inst = anchor.getInst();
                signAnchor ui = anchor.getUI();
                AnchorManagerEvent event = null;
                switch(column) {
	                case 0:
	                	boolean flag = (boolean) mTable.getValueAt(row, 0);
	                	inst.Enable(flag);
	                	ui.enable(flag);
	                	event = new AnchorManagerEvent(anchor, AnchorManagerEvent.STA);
	                	publishListener(event);
	                	break;
	                case 1:
	                	inst.setID(Integer.parseInt((String) mTable.getValueAt(row, 1)));
	                	break;
	                case 2:
	                	inst.setX(Double.valueOf((String) mTable.getValueAt(row, 2)));
	                	Point px = coordTrans.Real2UI(inst.getX(), inst.getY());
	                	ui.setPos(px.x, px.y);
	                	event = new AnchorManagerEvent(anchor, AnchorManagerEvent.MOV);
	                	publishListener(event);
	                	break;
	                case 3:
	                	inst.setY(Double.valueOf((String) mTable.getValueAt(row, 3)));
	                	Point py = coordTrans.Real2UI(inst.getX(), inst.getY());
	                	ui.setPos(py.x, py.y);
	                	event = new AnchorManagerEvent(anchor, AnchorManagerEvent.MOV);
	                	publishListener(event);
	                	break;
	                case 4:
	                	inst.setZ(Double.valueOf((String) mTable.getValueAt(row, 4)));
	                	event = new AnchorManagerEvent(anchor, AnchorManagerEvent.MOV);
	                	publishListener(event);
	                	break;
	                default: break;
                }
            }else if (type == TableModelEvent.DELETE) {
//                System.out.println("Delete row " + row + ".");
            }else {
//                System.out.println("Unknown event.");
            }
		}
	};
	private void publishListener(AnchorManagerEvent event) {
		for(AnchorManagerEventListener lis : Listeners) {
			lis.AnchorUpdated(event);
		}
	}
	public void addAnchorManagerListener(AnchorManagerEventListener listener) {
		Listeners.add(listener);
	}
	public void addAnchor() {
		px = Double.valueOf(xText.getText());
		py = Double.valueOf(yText.getText());
		pz = Double.valueOf(zText.getText());
		uwbInstance inst = new uwbInstance(px, py, pz, AnchorCount);
		Point p = coordTrans.Real2UI(px, py);
		signAnchor sign = new signAnchor(p.x, p.y);
		AnchorList.add(new uwbAnchor(inst, sign));
		Model.addRow(new Object[]{true, String.valueOf(AnchorCount), String.valueOf(px), String.valueOf(py), String.valueOf(pz)});
		px += 2; py += 2;
		xText.setText(""+px); yText.setText(""+py); zText.setText(""+pz);
		AnchorManagerEvent event = new AnchorManagerEvent(AnchorList.get(AnchorCount), AnchorManagerEvent.ADD);
		for(AnchorManagerEventListener lis : Listeners) {
			lis.AnchorUpdated(event);
		}
		AnchorCount ++;
	}
	public void addAnchor(double x, double y, double z) {
		uwbInstance inst = new uwbInstance(x, y, z, AnchorCount);
		Point p = coordTrans.Real2UI(x, y);
		signAnchor sign = new signAnchor(p.x, p.y);
		AnchorList.add(new uwbAnchor(inst, sign));
		Model.addRow(new Object[]{true, String.valueOf(AnchorCount), String.valueOf(px), String.valueOf(py), String.valueOf(pz)});
		px = x + 2; py = y + 2;
		xText.setText(""+px); yText.setText(""+py); zText.setText(""+pz);
		AnchorManagerEvent event = new AnchorManagerEvent(AnchorList.get(AnchorCount), AnchorManagerEvent.ADD);
		for(AnchorManagerEventListener lis : Listeners) {
			lis.AnchorUpdated(event);
		}
		AnchorCount ++;
	}
	public void addAnchor(uwbInstance inst) {
		inst.setID(AnchorCount);
		Point p = coordTrans.Real2UI(inst.getX(), inst.getY());
		signAnchor sign = new signAnchor(p.x, p.y);
		AnchorList.add(new uwbAnchor(inst, sign));
		Model.addRow(new Object[]{inst.isEnable(), String.valueOf(AnchorCount),
				String.valueOf(inst.getX()), String.valueOf(inst.getY()), String.valueOf(inst.getZ())});
		px = inst.getX() + 2; py = inst.getY() +2;
		xText.setText(""+px); yText.setText(""+py); zText.setText(""+pz);
		AnchorManagerEvent event = new AnchorManagerEvent(AnchorList.get(AnchorCount), AnchorManagerEvent.ADD);
		for(AnchorManagerEventListener lis : Listeners) {
			lis.AnchorUpdated(event);
		}
		AnchorCount ++;
	}
	public void delAnchor() {
		if(AnchorCount > 0) {
			AnchorCount --;
			AnchorManagerEvent event = new AnchorManagerEvent(AnchorList.get(AnchorCount), AnchorManagerEvent.DEL);
			for(AnchorManagerEventListener lis : Listeners) {
				lis.AnchorUpdated(event);
			}
			AnchorList.remove(AnchorCount);
			Model.removeRow(AnchorCount);
		}
	}
	@Override
	public void CoordinateUpdate(CoordTransEvent event) {
		// TODO Auto-generated method stub
		CoordTrans coord = (CoordTrans)event.getSource();
		for(uwbAnchor a : AnchorList) {
			uwbInstance inst = a.getInst();
			Point p = coord.Real2UI(inst.getX(), inst.getY());
			a.getUI().setPos(p.x, p.y);
		}
	}
	public uwbAnchor getAnchor(int index) {
		if(index < AnchorCount) {
			return AnchorList.get(index);
		}
		return null;
	}
	public static void main(String[] args) {
		try {
            UIManager.setLookAndFeel(
                UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Couldn't use system look and feel.");
        }
		JFrame tFrame = new JFrame();
		CoordTrans coord = new CoordTrans(600, 600, 4.0, 4.0);
		AnchorManager tas = new AnchorManager(coord, 10);
		tFrame.add(tas);
		tFrame.setSize(280, 500);
		tFrame.setLocation(1000, 300);
		tFrame.setResizable(false);
		tFrame.setTitle("anchor manager");
		tFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		tFrame.setVisible(true);
	}
}
