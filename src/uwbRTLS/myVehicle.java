package uwbRTLS;

import java.awt.Color;
import java.awt.Font;
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

	private static int INST_CNT = 0;

	private BufferedImage imgORG, imgDST;
	private BufferedImage imgZoom, imgName;
	private Graphics gCanvas = null;
	private String TagName = "TAG";
	private int id = 0;
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
		this.id = myVehicle.INST_CNT ++;
		imgZoom = zoom(imgORG, defaultScale);
		imgName = DrawName(imgZoom);
		imgDST = rotate(imgName, Yaw);
	}
	public myVehicle(Graphics g) {
		gCanvas = g;
		try {
			imgORG = ImageIO.read(new File(getClass().getResource(ImgFile).getFile()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.id = myVehicle.INST_CNT ++;
		imgZoom = zoom(imgORG, defaultScale);
		imgName = DrawName(imgZoom);
		imgDST = rotate(imgName, Yaw);
	}

    private BufferedImage rotate(BufferedImage img, int deg) {
        deg = deg % 360;
        if (deg < 0) deg = 360 + deg;
        double rad = Math.toRadians(deg);
        double sin_rad = Math.sin(rad);
        double cos_rad = Math.cos(rad);
        int rw = (int) (Math.abs(img.getWidth() * cos_rad) + Math.abs(img.getHeight() * sin_rad));
        int rh = (int) (Math.abs(img.getWidth() * sin_rad) + Math.abs(img.getHeight() * cos_rad));

        int x = (rw / 2) - (img.getWidth() / 2);
        int y = (rh / 2) - (img.getHeight() / 2);

        imgDST = new BufferedImage(rw, rh, img.getType());
        Graphics2D gs = (Graphics2D) imgDST.getGraphics();
        gs.setColor(new Color(0, 0, 0, 0));
        gs.fillRect(0, 0, rw, rh);

        AffineTransform at = new AffineTransform();
        at.rotate(rad, rw / 2, rh / 2);
        at.translate(x, y);
        return new AffineTransformOp(at, AffineTransformOp.TYPE_BICUBIC).filter(img, imgDST);
    }
    private BufferedImage zoom(BufferedImage img, double scale) {
    	BufferedImage imgSCL = new BufferedImage((int)(img.getWidth() * scale), (int)(img.getHeight() * scale), img.getType());
		return new AffineTransformOp(AffineTransform.getScaleInstance(scale, scale), null).filter(img, imgSCL);
    }
    private BufferedImage DrawName(BufferedImage img) {
    	String s = TagName + "(" + this.id + ")";
    	Font f = new Font("Courier New", Font.BOLD, 16);
    	Graphics g = img.getGraphics();
    	int cw = g.getFontMetrics(f).stringWidth(s);
    	int ch = g.getFontMetrics(f).getHeight();
    	int ascent = g.getFontMetrics(f).getAscent();
    	int w = Math.max(cw, img.getWidth());
    	int h = ch + img.getHeight();
    	BufferedImage imgName = new BufferedImage(w, h, img.getType());
    	g = imgName.getGraphics(); g.setFont(f);
    	g.drawImage(img, (w - img.getWidth()) / 2, 0, this);
    	g.drawString(s, (w - cw) / 2, h - ch + ascent);
    	return imgName;
    }

	public Image getImage() {
		return this.imgDST;
	}
	public int getID() {
		return this.id;
	}
	public void setCanvasGraphic(Graphics g) {
		gCanvas = g;
	}
	public void setName(String str) {
		TagName = str;
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
		rotate(imgName, Yaw);
	}
	public void setZoom(double z) {
		if(z > 16) z = 16;
		if(z < 0.5) z = 0.5; 
		imgZoom = zoom(imgORG, defaultScale * z);
		imgName = DrawName(imgZoom);
		rotate(imgName, Yaw);
	}
	public void setZoomTo(int width) {
		if(width < 9) width = 9;
		if(width > imgSrcWidth) width = imgSrcWidth;
		imgZoom = zoom(imgORG, (double)width / imgSrcWidth);
		imgName = DrawName(imgZoom);
		rotate(imgName, Yaw);
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
