package RoadPaint;

import java.awt.BorderLayout;
import java.util.concurrent.Semaphore;

import javax.swing.JPanel;
import javax.swing.UIManager;

import MainFrame.MyMainFrame;
import WaveTool.WaveTool;
import protocol.ComPackage;
import protocol.PackageTypes.TypeUWB;
import protocol.event.DecodeEvent;
import protocol.event.DecodeEventListener;

public class WavePlayer extends WaveTool implements Runnable, DecodeEventListener {
	private static final long serialVersionUID = 1L;
	private static final int Max_Lines = 4;
	
	private ComPackage rxData = null;
	private Semaphore semaphore = null;

	public WavePlayer(String Title) {
		super(Title);
		// TODO Auto-generated constructor stub
		for(int i = 0; i < Max_Lines; i ++) {
			this.addNewSeries("dist" + i);
			this.setSeriesStroke(i, 3);
		}
		semaphore = new Semaphore(1, true);
		(new Thread(this)).start();
	}

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
		}
	}

	private int recNum = 0;
	private float[] recDist = {0, 0, 0, 0};
	@Override
	public void getNewPackage(DecodeEvent event) {
		// TODO Auto-generated method stub
		rxData = (ComPackage)event.getSource();
		if(rxData.type == TypeUWB.TYPE_COM_HEARTBEAT) {}
		else if(rxData.type == TypeUWB.TYPE_DIST_Response) {}
		else if(rxData.type == TypeUWB.TYPE_DIST_GROUP_Resp) {
			recNum = rxData.rData[1];
			for(int i = 0; i < recNum; i ++) {
				recDist[rxData.rData[i * 5 + 3]] = rxData.readoutFloat(i * 5 + 4);
			}
//			if(recDist[0] < 0) recDist[0] = 0;
//			if(recDist[1] < 0) recDist[1] = 0;
//			if(recDist[2] < 0) recDist[2] = 0;
//			if(recDist[0] < 40)
//				dist[0] = recDist[0] * 0.1 + dist[0] * 0.9;
//			if(recDist[1] < 40)
//				dist[1] = recDist[1] * 0.1 + dist[1] * 0.9;
//			if(recDist[2] < 40)
//				dist[2] = recDist[2] * 0.1 + dist[2] * 0.9;
			semaphore.release();
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
		mf.setTitle("Wave Player Test");
		mf.setFrameSize(1000, 600);
		WavePlayer wp = new WavePlayer("Distance");
		JPanel mp = mf.getUsrMainPanel();
		mp.setLayout(new BorderLayout());
		mp.add(wp, BorderLayout.CENTER);
		mf.getDecoder().addDecodeListener(wp);
		mf.setResizable(false);
		mf.setVisible(true);
	}
}
