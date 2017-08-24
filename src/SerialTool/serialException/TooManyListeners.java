package SerialTool.serialException;

public class TooManyListeners extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public TooManyListeners() {}

	@Override
	public String toString() {
		return "error! too many listeners!";
	}
	
}
