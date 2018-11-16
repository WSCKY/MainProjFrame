package RoadPaint;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.JPanel;

public class myCanvas extends JPanel implements ComponentListener, MouseListener, MouseMotionListener, MouseWheelListener {
	private static final long serialVersionUID = 1L;

	private Image img = null;
	private CoordTrans coord = null;
	private int xOff = 0, yOff = 0;
	public myCanvas() {
		this.addComponentListener(this);
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
	}
	public myCanvas(Image img) {
		if(img != null) {
			this.img = img;
		}
		this.addComponentListener(this);
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		this.addMouseWheelListener(this);
	}
	public void setCoordTrans(CoordTrans c) {
		coord = c;
	}
	public void setImage(Image img) {
		if(img != null) {
			this.img = img;
		}
	}

	public void paintComponent(Graphics g) {
		g.drawImage(img, xOff, yOff, this);
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
