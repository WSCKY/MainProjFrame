package RepairTool;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
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
import protocol.PackageTypes.TypePartnerX;

public class RepairTool extends JFrame{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final int CommPort = 6000;

	private static DatagramSocket CommSocket = null;
	private static ComPackage txData = new ComPackage();

	private JButton EraseButton = null;
	private JButton CalibButton = null;
	private JTextField SNT = new JTextField(16);

	public RepairTool() {
		/* GUI */
		EraseButton = new JButton("EraseMTD");
		EraseButton.setPreferredSize(new Dimension(95, 30));
		EraseButton.addActionListener(bl);
		CalibButton = new JButton("AccCalib");
		CalibButton.setPreferredSize(new Dimension(95, 30));
		CalibButton.addActionListener(bl);

		this.setLayout(new FlowLayout());
		this.add(EraseButton);
		this.add(CalibButton);
		SNT.setFont(new Font("Curier New", Font.BOLD, 20));
		this.add(SNT);

		this.setSize(450, 130);
		this.setLocation(800, 400);
		this.setTitle("kyChu.RepairTool");
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
			if(Btn.equals("EraseMTD")) {
				txData.type = TypePartnerX.TYPE_CALIB_MTD_OptReq;
				txData.addByte(TypePartnerX.MTD_OprErase, DataCnt); DataCnt ++;
				txData.addByte((byte)(TypePartnerX.MTD_OprErase ^ 0xDD), DataCnt); DataCnt ++;
			} else if(Btn.equals("AccCalib")) {
				txData.type = TypePartnerX.TYPE_ACC_CALIBRATE;
				txData.addByte(TypePartnerX.ACC_CALIBRATE_VERIFY, DataCnt); DataCnt ++;
			} else {
				return;
			}
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
		new RepairTool();
	}
}
