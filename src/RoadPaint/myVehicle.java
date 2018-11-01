package RoadPaint;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class myVehicle implements ImageObserver {
	private static final String ImgFile = "pos.png";
	private static final int imgSrcWidth = 288;

	private BufferedImage imgORG, imgSCL, imgDST;
	private Graphics gCanvas = null;
	private int xPos = 0, yPos = 0;
	private int Yaw = 0;
	private double defaultScale = 0.0625;
	public myVehicle() {
		try {
			imgORG = ImageIO.read(new File(getClass().getResource(ImgFile).getFile()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		zoom(defaultScale);
		rotate(Yaw);
	}
	public myVehicle(Graphics g) {
		gCanvas = g;
		try {
			imgORG = ImageIO.read(new File(getClass().getResource(ImgFile).getFile()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		zoom(defaultScale);
		rotate(Yaw);
	}

    private void rotate(int deg) {
        deg = deg % 360;
        if (deg < 0) deg = 360 + deg;
        double rad = Math.toRadians(deg);
        double sin_rad = Math.sin(rad);
        double cos_rad = Math.cos(rad);
        int rw = (int) (Math.abs(imgSCL.getWidth() * cos_rad) + Math.abs(imgSCL.getHeight() * sin_rad));
        int rh = (int) (Math.abs(imgSCL.getWidth() * sin_rad) + Math.abs(imgSCL.getHeight() * cos_rad));

        int x = (rw / 2) - (imgSCL.getWidth() / 2);
        int y = (rh / 2) - (imgSCL.getHeight() / 2);

        imgDST = new BufferedImage(rw, rh, imgSCL.getType());
        Graphics2D gs = (Graphics2D) imgDST.getGraphics();
        gs.setColor(new Color(0, 0, 0, 0));
        gs.fillRect(0, 0, rw, rh);

        AffineTransform at = new AffineTransform();
        at.rotate(rad, rw / 2, rh / 2);
        at.translate(x, y);
        new AffineTransformOp(at, AffineTransformOp.TYPE_BICUBIC).filter(imgSCL, imgDST);
    }
    private void zoom(double scale) {
    	imgSCL = new BufferedImage((int)(imgORG.getWidth() * scale), (int)(imgORG.getHeight() * scale), imgORG.getType());
		new AffineTransformOp(AffineTransform.getScaleInstance(scale, scale), null).filter(imgORG, imgSCL);
    }

	public Image getImage() {
		return this.imgDST;
	}
	public void setCanvasGraphic(Graphics g) {
		gCanvas = g;
	}

	public void move(int x, int y) {
		xPos += x;
		yPos += y;
	}
	public void moveTo(int x, int y) {
		xPos = x;
		yPos = y;
	}
	public void setYaw(int yaw) {
		this.Yaw = yaw;
		rotate(Yaw);
	}
	public void setZoom(double z) {
		if(z > 16) z = 16;
		if(z < 0.5) z = 0.5; 
		zoom(defaultScale * z);
		rotate(Yaw);
	}
	public void setZoomTo(int width) {
		if(width < 9) width = 9;
		if(width > imgSrcWidth) width = imgSrcWidth;
		zoom((double)width / imgSrcWidth);
		rotate(Yaw);
	}
	public void update() {
		if(gCanvas != null) {
			gCanvas.drawImage(imgDST, xPos - imgDST.getWidth() / 2, yPos - imgDST.getHeight() / 2, this);
		} else {
			// ...
		}
	}

	@Override
	public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {
		// TODO Auto-generated method stub
		return true;
	}
}
