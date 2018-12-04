package uwbRTLS;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
//import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JPanel;

import uwbRTLS.CoordTranfer.CoordTrans;
import uwbRTLS.uiComponent.uiComponent;

public class myCanvas extends JPanel implements ComponentListener, MouseListener, MouseMotionListener, MouseWheelListener {
	private static final long serialVersionUID = 1L;
	private static final Color BackColor = new Color(180, 180, 180);

	private CoordTrans coord = null;
	private Image DeskTop = null;
	private ArrayList<uiComponent> Layers = new ArrayList<uiComponent>();
	public myCanvas() {
		Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
		DeskTop = new BufferedImage(screensize.width, screensize.height, BufferedImage.TYPE_4BYTE_ABGR);
		Graphics g = DeskTop.getGraphics();
		g.setColor(BackColor);
		g.fillRect(0, 0, screensize.width, screensize.height);
		this.addComponentListener(this);
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		this.addMouseWheelListener(this);
	}

	public void setCoordTrans(CoordTrans c) {
		coord = c;
	}
	public void addLayer(uiComponent ui) {
		Layers.add(ui);
	}
	public void delLayer(uiComponent ui) {
		Layers.remove(ui);
	}
	public void setDeskTop(Image img) {
		DeskTop = img;
	}

	public void paintComponent(Graphics g) {
		g.drawImage(DeskTop, 0, 0, this);
		for(uiComponent ui : Layers) {
			g.drawImage(ui.getImage(), ui.getXPos(), ui.getYPos(), this);
		}
	}

	private int xMouse = 0, yMouse = 0;
	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
		coord.move(e.getX() - xMouse, e.getY() - yMouse);
		xMouse = e.getX();
		yMouse = e.getY();
	}
	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		xMouse = e.getX();
		yMouse = e.getY();
	}
	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void componentResized(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void componentMoved(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void componentShown(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void componentHidden(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		// TODO Auto-generated method stub
		coord.zoom(-e.getWheelRotation() * 0.2 + 1.0);
	}
}
