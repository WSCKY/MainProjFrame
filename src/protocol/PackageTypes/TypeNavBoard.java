package protocol.PackageTypes;

public class TypeNavBoard {
	/* ########## package type ########## */
	/* -------- Heart-beat -------- */
	public static final byte TYPE_COM_HEARTBEAT = (byte)0x01;
	/* -------- version -------- */
	public static final byte TYPE_VERSION_REQUEST = (byte)0x02;
	public static final byte TYPE_VERSION_Response = (byte)0x03;
	
	public static final byte TYPE_IMU_INFO_Resp = (byte)0x11;
	public static final byte TYPE_ATT_QUAT_Resp = (byte)0x12;
}
