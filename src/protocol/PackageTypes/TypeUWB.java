package protocol.PackageTypes;

public class TypeUWB {
	/* ########## package type ########## */
	/* -------- Heart-beat -------- */
	public static final byte TYPE_COM_HEARTBEAT = (byte)0x01;
	/* -------- version -------- */
	public static final byte TYPE_VERSION_REQUEST = (byte)0x02;
	public static final byte TYPE_VERSION_Response = (byte)0x03;
	
	public static final byte TYPE_DIST_Response = (byte)0x11;
	public static final byte TYPE_DIST_GROUP_Resp = (byte)0x12;
}
