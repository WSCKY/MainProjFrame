package CarControl;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Image;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.UIManager;

import MainFrame.MyMainFrame;

public class CarCtrl extends MyMainFrame {

	private static final long serialVersionUID = 1L;
	
	private JPanel MainPanel = null;
	private JPanel CtrlPanel = new JPanel();
	private JPanel VelhPanel = new JPanel();
	
	private JPanel FrontPanel = new JPanel(); private JButton FrontBtn = new JButton();
	private JPanel BackPanel = new JPanel(); private JButton BackBtn = new JButton();
	private JPanel LeftPanel = new JPanel(); private JButton LeftBtn = new JButton();
	private JPanel RightPanel = new JPanel(); private JButton RightBtn = new JButton();
	
	public CarCtrl() {
		this.setFrameSize(700, 450);
		MainPanel = this.getUsrMainPanel();
		MainPanel.setLayout(new GridLayout(1, 2, 10, 10));
		
		CtrlPanel.setLayout(new GridLayout(3, 3, 5, 5));
		CtrlPanel.setBorder(BorderFactory.createLineBorder(Color.RED));
		FrontPanel.setLayout(new BorderLayout());//(new FlowLayout(FlowLayout.CENTER, 5, 5));
		BackPanel.setLayout(new BorderLayout());//(new FlowLayout(FlowLayout.CENTER, 5, 5));
		LeftPanel.setLayout(new BorderLayout());//(new FlowLayout(FlowLayout.CENTER, 5, 5));
		RightPanel.setLayout(new BorderLayout());//(new FlowLayout(FlowLayout.CENTER, 5, 5));
		FrontBtn.setIcon(new ImageIcon(new ImageIcon(getClass().getResource("forward.png")).getImage().getScaledInstance(60, 90, Image.SCALE_DEFAULT)));
		LeftBtn.setIcon(new ImageIcon(new ImageIcon(getClass().getResource("left.png")).getImage().getScaledInstance(90, 60, Image.SCALE_DEFAULT)));
		RightBtn.setIcon(new ImageIcon(new ImageIcon(getClass().getResource("right.png")).getImage().getScaledInstance(90, 60, Image.SCALE_DEFAULT)));
		BackBtn.setIcon(new ImageIcon(new ImageIcon(getClass().getResource("backward.png")).getImage().getScaledInstance(60, 90, Image.SCALE_DEFAULT)));
		FrontPanel.add(FrontBtn); BackPanel.add(BackBtn); LeftPanel.add(LeftBtn); RightPanel.add(RightBtn);
		FrontPanel.setBorder(BorderFactory.createLineBorder(Color.RED));
		BackPanel.setBorder(BorderFactory.createLineBorder(Color.RED));
		LeftPanel.setBorder(BorderFactory.createLineBorder(Color.RED));
		RightPanel.setBorder(BorderFactory.createLineBorder(Color.RED));
		CtrlPanel.add(new JPanel()); CtrlPanel.add(FrontPanel); CtrlPanel.add(new JPanel());
		CtrlPanel.add(LeftPanel); CtrlPanel.add(new JPanel()); CtrlPanel.add(RightPanel);
		CtrlPanel.add(new JPanel()); CtrlPanel.add(BackPanel); CtrlPanel.add(new JPanel());
		
		MainPanel.add(CtrlPanel);
		MainPanel.add(VelhPanel);

		this.setVisible(true);
		this.setAutoTxEnable(true);
		this.setResizable(true);
		this.setDebugString("Ready ...");
	}

	public static void main(String[] args) {
		try {
            UIManager.setLookAndFeel(
                UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Couldn't use system look and feel.");
        }
		new CarCtrl();
	}
}
