package SerialTool.serialException;

public class ReadDataFromSerialPortFailure extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ReadDataFromSerialPortFailure() {}

	@Override
	public String toString() {
		return "error while reading data!";
	}
	
}
