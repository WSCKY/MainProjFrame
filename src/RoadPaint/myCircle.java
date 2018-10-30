package RoadPaint;

import java.awt.geom.Point2D;

public class myCircle {
	private double x = 0;
	private double y = 0;
	private double radius = 1;
	

	public myCircle() {}
	public myCircle(double x, double y) {
		this.x = x;
		this.y = y;
	}
	public myCircle(double x, double y, double r) {
		this.x = x;
		this.y = y;
		this.radius = r;
	}
	
	public double getX() {
		return x;
	}
	public double getY() {
		return y;
	}
	public double getRadius() {
		return radius;
	}

	public void setX(double x) {
		if(x >= 0)
			this.x = x;
	}
	public void setY(double y) {
		if(y >= 0)
			this.y = y;
	}
	public void setRadius(double r) {
		if(r > 0)
			this.radius = r;
	}
	
	public double getArea() {
		return Math.PI * radius * radius;
	}
	public double getPerimeter() {
		return Math.PI * 2 * radius;
	}
	public int CrossCircle(myCircle circle, Point2D.Double[] pCross) {
		if(double_equals(this.x, circle.x) && double_equals(this.y, circle.y) && double_equals(this.radius, circle.radius)) return -1;

		double d = comp_distance(this.x, this.y, circle.x, circle.y);
		if(d > (this.radius + circle.radius) || d < Math.abs(this.radius - circle.radius)) return 0;

		double a = 2.0 * this.radius * (this.x - circle.x);
		double b = 2.0 * this.radius * (this.y - circle.y);
		double c = circle.radius * circle.radius - this.radius * this.radius - distance_sqr(this.x, this.y, circle.x, circle.y);
		double p = a * a + b * b;
		double q = -2.0 * a * c;
		if(double_equals(d, circle.radius + circle.radius) || 
		   double_equals(d, Math.abs(this.radius - circle.radius))) {
			double cosine = -q / p / 2.0;
			double sine = Math.sqrt(1 - cosine *  cosine);
			double cx = this.radius * cosine + this.x;
			double cy = this.radius * sine + this.y;
			if(!double_equals(distance_sqr(cx, cy, circle.x, circle.y), circle.radius * circle.radius)) {
				cy = this.y - this.radius * sine;
			}
			pCross[0].setLocation(cx, cy);
			return 1;
		}
		double r = c * c - b * b;
		double cos_0 = (Math.sqrt(q * q - 4.0 * p * r) - q) / p / 2.0;
		double cos_1 = (-Math.sqrt(q * q - 4.0 * p * r) - q) / p / 2.0;
		double sin_0 = Math.sqrt(1 - cos_0 * cos_0);
		double sin_1 = Math.sqrt(1 - cos_1 * cos_1);
		double cx0 = this.radius * cos_0 + this.x;
		double cx1 = this.radius * cos_1 + this.x;
		double cy0 = this.radius * sin_0 + this.y;
		double cy1 = this.radius * sin_1 + this.y;
		if(!double_equals(distance_sqr(cx0, cy0, circle.x, circle.y), circle.radius * circle.radius)) {
			cy0 = this.y - this.radius * sin_0;
		}
		if(!double_equals(distance_sqr(cx1, cy1, circle.x, circle.y), circle.radius * circle.radius)) {
			cy1 = this.y - this.radius * sin_1;
		}
		if(double_equals(cy0, cy1) && double_equals(cx0, cx1)) {
			if(cy0 > 0) {
				cy1 = -cy1;
			} else {
				cy0 = -cy0;
			}
		}
		pCross[0].setLocation(cx0, cy0);
		pCross[1].setLocation(cx1, cy1);
		return 2;
	}

	private boolean double_equals(double a, double b) {
		double ZERO = 1e-9;
		return (Math.abs(a - b) < ZERO);
	}
	private double distance_sqr(double x1, double y1, double x2, double y2) {
		double deltax = x2 - x1;
		double deltay = y2 - y1;
		return deltax * deltax + deltay * deltay;
	}
	private double comp_distance(double x1, double y1, double x2, double y2) {
		double deltax = x2 - x1;
		double deltay = y2 - y1;
		return Math.sqrt(deltax * deltax + deltay * deltay);
	}
	public static void main(String[] args) {
		myCircle c1 = new myCircle(0, 0, 2);
		myCircle c2 = new myCircle(2, 0, 2);
		Point2D.Double[] pcros = new Point2D.Double[2];
		pcros[0] = new Point2D.Double(0, 0);
		pcros[1] = new Point2D.Double(0, 0);
		int idx = c1.CrossCircle(c2, pcros);
		if(idx == -1)
			System.err.println("Same Circle.");
		else if(idx == 0)
			System.out.println("No Cross point");
		else if(idx == 1) {
			System.out.println("1p, x:" + pcros[0].x + ", y:" + pcros[0].y);
		} else if(idx == 2) {
			System.out.println("2p, x:" + pcros[0].x + ", y:" + pcros[0].y);
			System.out.println("2p, x:" + pcros[1].x + ", y:" + pcros[1].y);
		}
	}
}
