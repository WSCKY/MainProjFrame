package protocol.PackageTypes;

public class TypeDeepblue {
	/* ########## package type ########## */
	/* -------- Heart-beat -------- */
	public static final byte TYPE_COM_HEARTBEAT = (byte)0x01;
	/* -------- device response -------- */
	public static final byte TYPE_DEV_Response = (byte)0x11;
	/* -------- programmable -------- */
	public static final byte TYPE_CtrlCmd1 = (byte)0x21;
	public static final byte TYPE_CtrlCmd2 = (byte)0x22;
	public static final byte TYPE_CtrlCmd3 = (byte)0x23;
	/* -------- version -------- */
	public static final byte TYPE_VERSION_REQUEST = (byte)0x66;
	public static final byte TYPE_VERSION_Response = (byte)0x67;
}
