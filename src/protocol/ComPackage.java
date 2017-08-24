/*
 * @brief  Communication package structure.
 * @author kyChu
 * @Date   2017/8/14
 */
package protocol;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import protocol.math.CalculateCRC;

public class ComPackage implements Cloneable {
	/* file data cache size */
	public static final int FILE_DATA_CACHE = 80;

	private static final byte Header1 = (byte)0x55;
	private static final byte Header2 = (byte)0xAA;
	private static final char CRC_INIT = (char)0x66;
	private static final int CACHE_SIZE = FILE_DATA_CACHE + 5;

	/* ########## package type ########## */
	/* -------- Heartbeat -------- */
	public static final byte TYPE_FC_APP_HEARTBEAT = (byte)0x01;
	/* -------- Common communication -------- */
	public static final byte TYPE_FC_Response = (byte)0x11;
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
	public static final byte DSN_FORCE_UPDATE_VERIFY = (byte)0xBF;
	/* ESC BurnIn Command */
	public static final byte ESC_VERIFY_DATA = (byte)0xCC;
	/* ACC calibrate command */
	public static final byte ACC_CALIBRATE_VERIFY = (byte)0x5A;
	/* -------- Repair Support -------- */
	public static final byte TYPE_CALIB_MTD_OptReq = (byte)0xC0;
	/* MTD Operation */
	public static final byte MTD_OprNone = (byte)0x0;
	public static final byte MTD_OprRead = (byte)0x1;
	public static final byte MTD_OprWrite = (byte)0x2;
	public static final byte MTD_OprErase = (byte)0x3;

	public byte stx1;
	public byte stx2;
	public int length;
	public int type;
	public byte[] rData;
	public char crc;

	public ComPackage() {
		stx1 = Header1;
		stx2 = Header2;
		length = 0;
		type = 0;
		rData = new byte[CACHE_SIZE];
		crc = 0;
	}

	public void setLength(int len) {
		length = len;
	}

	public void addBytes(byte[] c, int len, int pos) {
		System.arraycopy(c, 0, rData, pos, len);
	}
	public void addByte(byte c, int pos) {
		rData[pos] = c;
	}
	public void addFloat(float f, int pos) {
		int d = Float.floatToRawIntBits(f);
		byte[] c = new byte[]{(byte)(d >> 0), (byte)(d >> 8), (byte)(d >> 16), (byte)(d >> 24)};
		addBytes(c, 4, pos);
	}
	public void addInteger(int d, int pos) {
		byte[] c = new byte[]{(byte)(d >> 0), (byte)(d >> 8), (byte)(d >> 16), (byte)(d >> 24)};
		addBytes(c, 4, pos);
	}
	public void addCharacter(char d, int pos) {
		byte[] c = new byte[]{(byte)(d >> 8), (byte)(d >> 0)};
		addBytes(c, 2, pos);
	}
	public float readoutFloat(int pos) {
		byte[] b = {rData[pos + 3], rData[pos + 2], rData[pos + 1], rData[pos + 0]};
		DataInputStream dis = new DataInputStream(new ByteArrayInputStream(b));
		float f = 0.0f;
		try {
			f = dis.readFloat();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return f;
	}
	public int readoutInteger(int pos) {
		int c = (rData[pos] & 0xFF) | ((rData[pos + 1] << 8) & 0xFF00) | ((rData[pos + 2] << 24) >>> 8) | (rData[pos + 3] << 24);
		return c;
	}
	public char readoutCharacter(int pos) {
		char c = (char) (rData[pos] & 0xFF | ((rData[pos + 1] << 8) & 0xFF00));
		return c;
	}
	public String readoutString(int pos, int len) {
		byte[] c = new byte[len];
		System.arraycopy(rData, pos, c, 0, len);
		return new String(c);
	}

	public byte[] getCRCBuffer() {
		byte[] c = new byte[length];
		System.arraycopy(rData, 0, c, 2, length - 2);
		c[0] = (byte)length;
		c[1] = (byte)type;
		return c;
	}

	public byte[] getSendBuffer() {
		byte[] c = new byte[length + 3];
		c[0] = stx1;
		c[1] = stx2;
		c[2] = (byte)length;
		c[3] = (byte)type;
		System.arraycopy(rData, 0, c, 4, length - 2);
		c[length + 2] = ComputeCRC();
		return c;
	}

	public byte ComputeCRC() {
		return (byte)CalculateCRC.ComputeCRC8(getCRCBuffer(), length, CRC_INIT);
	}

	public Object PackageCopy() throws CloneNotSupportedException {
		return super.clone();
	} 
}
