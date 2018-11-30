package uwbRTLS;

public class uwbInstance {
	private double x = 0; /* unit: m */
	private double y = 0;
	private double z = 0;
	private int id = 0;
	private boolean enable = true;
	public uwbInstance() {
		x = 0;
		y = 0;
		z = 0;
		id = 0;
		enable = true;
	}
	public uwbInstance(int id) {
		x = 0;
		y = 0;
		z = 0;
		enable = true;
		this.id = id;
	}
	public uwbInstance(boolean e) {
		x = 0;
		y = 0;
		z = 0;
		id = 0;
		enable = e;
	}
	public uwbInstance(double x, double y, double z, int id) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.id = id;
	}
	public double getX() { return x; }
	public double getY() { return y; }
	public double getZ() { return z; }
	public int getID() { return id; }
	public boolean isEnable() { return enable; }
	public void setX(double x) { this.x = x; }
	public void setY(double y) { this.y = y; }
	public void setZ(double z) { this.z = z; }
	public void setID(int id) { this.id = id; }
	public void Enable(boolean e) { enable = e; }
}
