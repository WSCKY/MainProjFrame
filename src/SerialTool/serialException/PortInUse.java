package SerialTool.serialException;

public class PortInUse extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public PortInUse() {}

	@Override
	public String toString() {
		return "this port is already occupied!";
	}
	
}
