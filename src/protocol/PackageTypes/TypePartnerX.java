package protocol.PackageTypes;

public class TypePartnerX {
	/* ########## package type ########## */
	/* -------- Heartbeat -------- */
	public static final byte TYPE_FC_APP_HEARTBEAT = (byte)0x01;
	/* -------- Common communication -------- */
	public static final byte TYPE_FC_Response = (byte)0x11;
	public static final byte TYPE_FC_VirtualCtrl = (byte)0x15;
	/* -------- programmable -------- */
	public static final byte TYPE_ProgrammableTX = (byte)0x22;
	public static final byte TYPE_ProgrammableACK = (byte)0x23;
	/* action number */
	public static final byte Program_Hover = (byte)0x00;
	public static final byte Program_Takeoff = (byte)0x01;
	public static final byte Program_Land = (byte)0x02;
	public static final byte Program_Forward = (byte)0x03;
	public static final byte Program_Backward = (byte)0x04;
	public static final byte Program_TwLeft = (byte)0x05;
	public static final byte Program_TwRight = (byte)0x06;
	public static final byte Program_UpWard = (byte)0x07;
	public static final byte Program_DownWard = (byte)0x08;
	public static final byte Program_RotateLeft = (byte)0x09;
	public static final byte Program_RotateRight = (byte)0x0A;
	/* -------- device check -------- */
	public static final byte TYPE_DeviceCheckReq = (byte)0x32;
	public static final byte TYPE_DeviceCheckAck = (byte)0x33;
	/* device */
	public static final byte _dev_Rev = (byte)0x0;
	public static final byte _dev_IMU = (byte)0x1;
	public static final byte _dev_Baro = (byte)0x2;
	public static final byte _dev_TOF = (byte)0x3;
	public static final byte _dev_Flow = (byte)0x4;
	public static final byte _dev_ADC = (byte)0x5;
	public static final byte _dev_ESC = (byte)0x6;
	public static final byte _dev_MTD = (byte)0x7;
	public static final byte _dev_LED = (byte)0x8;
	/* -------- Emergency -------- */
	public static final byte TYPE_USER_ForceCmd = (byte)0x44;
	/* Force Command */
	public static final byte Force_Cutoff = (byte)0x01;
	public static final byte Force_Poweroff = (byte)0x02;
	public static final byte Force_Land = (byte)0x03;
	/* -------- version & DSN -------- */
	public static final byte TYPE_VERSION_REQUEST = (byte)0x66;
	public static final byte TYPE_VERSION_Response = (byte)0x67;
	/* -------- upgrade -------- */
	public static final byte TYPE_UPGRADE_REQUEST = (byte)0x80;
	public static final byte TYPE_UPGRADE_DATA = (byte)0x81;
	public static final byte TYPE_UPGRADE_FC_ACK = (byte)0x82;
	/* firmware type */
	public static final byte FW_TYPE_NONE = (byte)0x0;
	public static final byte FW_TYPE_FC = (byte)0x1;
	/* upgrade state */
	public static final byte FC_STATE_READY = (byte)0x0;
	public static final byte FC_STATE_ERASE = (byte)0x1;
	public static final byte FC_STATE_UPGRADE = (byte)0x2;
	public static final byte FC_STATE_REFUSED = (byte)0x3;
	public static final byte FC_STATE_JUMPFAILED = (byte)0x4;
	/* upgrade refused */
	public static final byte FC_REFUSED_BUSY = (byte)0x0;
	public static final byte FC_REFUSED_VERSION_OLD = (byte)0x1;
	public static final byte FC_REFUSED_OVER_SIZE = (byte)0x2;
	public static final byte FC_REFUSED_TYPE_ERROR = (byte)0x3;
	public static final byte FC_REFUSED_LOW_VOLTAGE = (byte)0x4;
	public static final byte FC_REFUSED_FW_TYPE_ERROR = (byte)0x5;
	public static final byte FC_REFUSED_UNKNOWERROR = (byte)0x6;
	public static final byte FC_REFUSED_NO_ERROR = (byte)0xF;
	/* -------- Factory Test -------- */
	public static final byte TYPE_DSN_UPDATE = (byte)0xA0;
	public static final byte TYPE_ADC_CALIBRATE = (byte)0xA1;
	public static final byte TYPE_ADC_CALIB_ACK = (byte)0xA2;
	public static final byte TYPE_ESC_BURN_IN_TEST = (byte)0xA3;
	public static final byte TYPE_ACC_CALIBRATE = (byte)0xA4;
	public static final byte TYPE_ACC_CALIB_ACK = (byte)0xA5;
	/* ADC calibrate command */
	public static final byte ADC_CALIBRATE_H = (byte)0x33;
	public static final byte ADC_CALIBRATE_L = (byte)0x44;
	public static final byte VOLT_VERIFY_DATA = (byte)0xAA;
	/* DSN Update command */
	public static final byte DSN_VERIFY_DATA = (byte)0xBB;
	/* ESC BurnIn Command */
	public static final byte ESC_VERIFY_DATA = (byte)0xCC;
	/* ACC calibrate command */
	public static final byte ACC_CALIBRATE_VERIFY = (byte)0x5A;
	/* -------- OptFlow Check -------- */
	public static final byte TYPE_IMG_INFO_Req = (byte)0xB0;
	public static final byte TYPE_IMG_INFO_Ack = (byte)0xB1;
	public static final byte TYPE_FLOW_IMG_Req = (byte)0xB2;
	public static final byte TYPE_FLOW_IMG_Ack = (byte)0xB3;
	public static final byte TYPE_FLOW_IMG_DAT = (byte)0xB4;

	public static final byte TYPE_DIST_RAW_DAT = (byte)0xBA;
	public static final byte FLOW_INF_REQ_CMD = (byte)0xAF;
	public static final byte FLOW_IMG_REQ_CMD = (byte)0xFA;
	/* -------- Repair Support -------- */
	public static final byte TYPE_CALIB_MTD_OptReq = (byte)0xC0;
	/* MTD Operation */
	public static final byte MTD_OprNone = (byte)0x0;
	public static final byte MTD_OprRead = (byte)0x1;
	public static final byte MTD_OprWrite = (byte)0x2;
	public static final byte MTD_OprErase = (byte)0x3;
	/* -------- Design for Debug -------- */
	public static final byte TYPE_DEBUG_CMD = (byte)0xE0;
	public static final byte TYPE_WIFI_RC_RAW = (byte)0xE1;
}
