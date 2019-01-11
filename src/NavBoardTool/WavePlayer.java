package NavBoardTool;

import java.awt.BorderLayout;
import java.util.concurrent.Semaphore;

import javax.swing.JPanel;
import javax.swing.UIManager;

import MainFrame.MyMainFrame;
import WaveTool.WaveTool;
import protocol.ComPackage;
import protocol.PackageTypes.TypeNavBoard;
import protocol.event.DecodeEvent;
import protocol.event.DecodeEventListener;

public class WavePlayer extends WaveTool implements Runnable, DecodeEventListener {
	private static final long serialVersionUID = 1L;
	private ComPackage rxData = null;
	private Semaphore semaphore = null;
	public WavePlayer(String Title) {
		super(Title);
		// TODO Auto-generated constructor stub
		for(int i = 0; i < 6; i ++) {
			this.addNewSeries("val" + i);
			this.setSeriesStroke(i, 3);
		}
		semaphore = new Semaphore(1, true);
		(new Thread(this)).start();
	}

	@Override
	public void getNewPackage(DecodeEvent event) {
		// TODO Auto-generated method stub
		rxData = (ComPackage)event.getSource();
		switch(rxData.type) {
		case TypeNavBoard.TYPE_COM_HEARTBEAT:
			break;
		case TypeNavBoard.TYPE_IMU_INFO_Resp:
			for(int i = 0; i < 6; i ++)
				recDist[i] = rxData.readoutFloat(i * 4 + 0);
			semaphore.release();
			break;
			default:break;
		}
	}

	@Override
	public void badCRCEvent(DecodeEvent event) {
		// TODO Auto-generated method stub
		
	}

	private float[] recDist = {0, 0, 0, 0, 0, 0};
	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(true) {
			try {
				semaphore.acquire();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			this.addDataToSeries(0, recDist[0]);
			this.addDataToSeries(1, recDist[1]);
			this.addDataToSeries(2, recDist[2]);
			this.addDataToSeries(3, recDist[3]);
			this.addDataToSeries(4, recDist[4]);
			this.addDataToSeries(5, recDist[5]);
		}
	}
	public static void main(String[] args) {
		try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Couldn't use system look and feel.");
        }
		MyMainFrame mf = new MyMainFrame();
		mf.setTitle("Wave Player Test");
		mf.setFrameSize(1000, 600);
		WavePlayer wp = new WavePlayer("DataWave");
		JPanel mp = mf.getUsrMainPanel();
		mp.setLayout(new BorderLayout());
		mp.add(wp, BorderLayout.CENTER);
		mf.getDecoder().addDecodeListener(wp);
		mf.setResizable(false);
		mf.setVisible(true);
	}
}
