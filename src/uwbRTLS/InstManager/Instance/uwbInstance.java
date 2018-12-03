package uwbRTLS.InstManager.Instance;

public class uwbInstance {
	public static int LISTENER = 0;
	public static int TAG = 1;
	public static int ANCHOR = 2;

	private double x = 0; /* unit: m */
	private double y = 0;
	private double z = 0;
	private int id = 0;
	private boolean enable = true;
	private int type = ANCHOR;
	public uwbInstance() {
		x = 0;
		y = 0;
		z = 0;
		id = 0;
		type = ANCHOR;
		enable = true;
	}
	public uwbInstance(int id) {
		this();
		this.id = id;
	}
	public uwbInstance(boolean e) {
		this();
		enable = e;
	}
	public uwbInstance(int id, int type) {
		this();
		this.id = id;
		this.type = type;
	}
	public uwbInstance(double x, double y, double z, int id) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.id = id;
		type = ANCHOR;
	}
	public double getX() { return x; }
	public double getY() { return y; }
	public double getZ() { return z; }
	public int getID() { return id; }
	public int getType() { return type; }
	public boolean isEnable() { return enable; }
	public void setX(double x) { this.x = x; }
	public void setY(double y) { this.y = y; }
	public void setZ(double z) { this.z = z; }
	public void setID(int id) { this.id = id; }
	public void Enable(boolean e) { enable = e; }
	public void setType(int type) { this.type = type; }
}
