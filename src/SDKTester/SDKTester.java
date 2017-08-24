package SDKTester;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextField;

import protocol.ComPackage;

public class SDKTester extends JFrame{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final int CommPort = 6000;

	private static DatagramSocket CommSocket = null;
	private static ComPackage txData = new ComPackage();

	private JButton TakeoffButton = null;
	private JButton LandButton = null;
	private JButton ForwardButton = null;
	private JButton BackwardButton = null;
	private JButton LeftButton = null;
	private JButton RightButton = null;

	private JTextField rect = null;

	public SDKTester() {
		/* GUI */
		TakeoffButton = new JButton("Takeoff");
		TakeoffButton.setPreferredSize(new Dimension(95, 30));
		TakeoffButton.addActionListener(bl);
		LandButton = new JButton("Land");
		LandButton.setPreferredSize(new Dimension(95, 30));
		LandButton.addActionListener(bl);
		ForwardButton = new JButton("forward");
		ForwardButton.setPreferredSize(new Dimension(95, 30));
		ForwardButton.addActionListener(bl);
		BackwardButton = new JButton("Backward");
		BackwardButton.setPreferredSize(new Dimension(95, 30));
		BackwardButton.addActionListener(bl);
		LeftButton = new JButton("Left");
		LeftButton.setPreferredSize(new Dimension(95, 30));
		LeftButton.addActionListener(bl);
		RightButton = new JButton("Right");
		RightButton.setPreferredSize(new Dimension(95, 30));
		RightButton.addActionListener(bl);

		rect = new JTextField(36);
		rect.setText("");
		rect.setEditable(false);

		this.setLayout(new FlowLayout());
		this.add(ForwardButton);
		this.add(BackwardButton);
		this.add(LeftButton);
		this.add(RightButton);
		this.add(TakeoffButton);
		this.add(LandButton);
		this.add(rect);

		this.setSize(450, 130);
		this.setLocation(800, 400);
		this.setTitle("kyChu.Tester");
		this.addWindowListener(wl);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setResizable(false);
		this.setVisible(true);

		/* communication */
		try {
			CommSocket = new DatagramSocket(CommPort);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		new Thread(new RxThread()).start();
		new Thread(new TxThread()).start();
	}

	private ActionListener bl = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			String Btn = ((JButton)e.getSource()).getText();

			int DataCnt = 0;
			txData.type = ComPackage.TYPE_ProgrammableTX;
			if(Btn.equals("forward")) {
				txData.addByte(ComPackage.Program_Forward, DataCnt); DataCnt ++;
			} else if(Btn.equals("Backward")) {
				txData.addByte(ComPackage.Program_Backward, DataCnt); DataCnt ++;
			} else if(Btn.equals("Left")) {
				txData.addByte(ComPackage.Program_TwLeft, DataCnt); DataCnt ++;
			} else if(Btn.equals("Right")) {
				txData.addByte(ComPackage.Program_TwRight, DataCnt); DataCnt ++;
			} else if(Btn.equals("Takeoff")) {
				txData.addByte(ComPackage.Program_Takeoff, DataCnt); DataCnt ++;
			} else if(Btn.equals("Land")) {
				txData.addByte(ComPackage.Program_Land, DataCnt); DataCnt ++;
			} else {
				return;
			}
			txData.addFloat(100.0f, DataCnt); DataCnt += 4;/* unit: cm */
			txData.addFloat(0, DataCnt); DataCnt += 4;
			txData.addByte((byte)0, DataCnt); DataCnt += 1;
			DataCnt += 4; /* reserve bytes */
			txData.setLength(DataCnt + 2);
			byte[] SendBuffer = txData.getSendBuffer();
			DatagramPacket packet = new DatagramPacket(SendBuffer, 0, SendBuffer.length, new InetSocketAddress("192.168.4.1", 6000));
			
			try {
				CommSocket.send(packet);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	};

	private class RxThread implements Runnable {
		public void run() {
			// TODO Auto-generated method stub
//			while(!CommSocket.isClosed()) {
//				byte[] buff = new byte[90];
//				DatagramPacket packet = new DatagramPacket(buff, 0, buff.length);
//				try {
//					CommSocket.receive(packet);
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				rect.setText(new String(packet.getData(), 0, packet.getLength()));
//			}
		}
	}

	private class TxThread implements Runnable {
		public void run() {
			// TODO Auto-generated method stub
			
		}
	}

	WindowAdapter wl = new WindowAdapter() {
		public void windowClosing(WindowEvent e) {
			CommSocket.close();
		}
	};

	public static void main(String[] args) {
		new SDKTester();
	}
}
