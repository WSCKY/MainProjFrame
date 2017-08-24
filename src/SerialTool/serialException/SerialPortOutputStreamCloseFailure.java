package SerialTool.serialException;

public class SerialPortOutputStreamCloseFailure extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public SerialPortOutputStreamCloseFailure() {}

	@Override
	public String toString() {
		return "error while close OutputStream!";
	}
}
