/*
 * @brief  Communication package structure.
 * @author kyChu
 * @Date   2017/8/24
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

	/* -------- Heart-beat type -------- */
	public static final byte TYPE_COM_HEARTBEAT = (byte)0x01;

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
