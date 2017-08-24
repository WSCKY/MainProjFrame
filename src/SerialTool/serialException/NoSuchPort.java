package SerialTool.serialException;

public class NoSuchPort extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public NoSuchPort() {}

	@Override
	public String toString() {
		return "fail! can not found this port!";
	}
}
