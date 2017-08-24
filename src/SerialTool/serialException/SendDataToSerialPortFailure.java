package SerialTool.serialException;

public class SendDataToSerialPortFailure extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public SendDataToSerialPortFailure() {}

	@Override
	public String toString() {
		return "error while sending data!";
	}
	
}
