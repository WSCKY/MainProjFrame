package RoadPaint;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JPanel;

public class myCanvas extends JPanel implements MouseListener, MouseMotionListener {
	private static final long serialVersionUID = 1L;

	private Image img = null;
	private int xOff = 0, yOff = 0;
	public myCanvas() {
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
	}
	public void setImage(Image img) {
		if(img != null) {
			this.img = img;
		}
	}
	public void move(int x, int y) {
		xOff += x;
		yOff += y;
	}
	public void moveTo(int x, int y) {
		xOff = x;
		yOff = y;
	}

	public void paintComponent(Graphics g) {
		g.drawImage(img, xOff, yOff, this);
	}

	private int xMouse = 0, yMouse = 0;
	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
		move(e.getX() - xMouse, e.getY() - yMouse);
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
}
