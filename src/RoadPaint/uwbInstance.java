package RoadPaint;

public class uwbInstance {
	private int x = 0;
	private int y = 0;
	private int id = 0;
	private boolean enable = true;
	public uwbInstance() {
		x = 0;
		y = 0;
		id = 0;
		enable = true;
	}
	public uwbInstance(int id) {
		x = 0;
		y = 0;
		enable = true;
		this.id = id;
	}
	public uwbInstance(boolean e) {
		x = 0;
		y = 0;
		id = 0;
		enable = e;
	}
	public uwbInstance(int x, int y, int id) {
		this.x = x;
		this.y = y;
		this.id = id;
	}
	public int getX() { return x; }
	public int getY() { return y; }
	public int getID() { return id; }
	public boolean isEnable() { return enable; }
	public void setX(int x) { this.x = x; }
	public void setY(int y) { this.y = y; }
	public void setID(int id) { this.id = id; }
	public void Enable(boolean e) { enable = e; }
}
