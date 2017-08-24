package SerialTool.serialException;

public class NotASerialPort extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public NotASerialPort() {}

	@Override
	public String toString() {
		return "fail! not a serial port!";
	}
	
	
}
