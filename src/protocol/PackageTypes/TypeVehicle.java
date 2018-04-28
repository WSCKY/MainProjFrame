package protocol.PackageTypes;

public class TypeVehicle {
	/* ########## package type ########## */
	/* -------- Heart-beat -------- */
	public static final byte TYPE_COM_HEARTBEAT = (byte)0x01;
	/* -------- device response -------- */
	public static final byte TYPE_DEV_Response = (byte)0x11;
	/* -------- Control Command -------- */
	public static final byte TYPE_SetCtrlMode = (byte)0x20;
	public static final byte TYPE_NormalCtrl = (byte)0x21;
	/* -------- version -------- */
	public static final byte TYPE_VERSION_REQUEST = (byte)0x66;
	public static final byte TYPE_VERSION_Response = (byte)0x67;
	/* -------- Debug Command -------- */
	public static final byte TYPE_WheelsCtrl = (byte)0xA0;

	/* control mode */
	public static final byte _Mode_Normal = (byte)0x00;
	public static final byte _Mode_Avoid1 = (byte)0x01;
	public static final byte _Mode_Avoid2 = (byte)0x02;
	public static final byte _Mode_Avoid3 = (byte)0x03;
	public static final byte _Mode_AutoRun = (byte)0x04;
	public static final byte _Mode_Debug = (byte)0x0F;
}
