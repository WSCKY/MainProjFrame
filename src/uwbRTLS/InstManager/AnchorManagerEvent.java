package uwbRTLS.InstManager;

import java.util.EventObject;

public class AnchorManagerEvent extends EventObject {
	private static final long serialVersionUID = 0xA0;

	public static final int ADD = 0;
	public static final int DEL = 1;
	public static final int MOV = 2;
	public static final int STA = 3;

	private int type = ADD;
	public AnchorManagerEvent(Object source, int type) {
		super(source);
		// TODO Auto-generated constructor stub
		this.type = type;
	}

	public void setType(int type) {
		this.type = type;
	}
	public int getType() {
		return type;
	}
}
