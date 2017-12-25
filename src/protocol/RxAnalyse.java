/**
 * @brief  Received data decoder.
 * @author kyChu
 * @Date   2017/8/22
 */
package protocol;

import protocol.math.CalculateCRC;

public class RxAnalyse {
	public enum DECODE_STATE {
		DECODE_STATE_UNSYNCED,
		DECODE_STATE_GOT_STX1,
		DECODE_STATE_GOT_STX2,
		DECODE_STATE_GOT_LEN,
		DECODE_STATE_GOT_TYPE,
		DECODE_STATE_GOT_DATA;
	}

	private RxAnalyse() {}

	private static ComPackage rPacket = new ComPackage();
	public static ComPackage RecPackage = new ComPackage();
	private static boolean GotNewPackage = false;

	private static final byte Header1 = (byte)0x55;
	private static final byte Header2 = (byte)0xAA;

	private static final char CRC_INIT = (char)0x66;

	private static int _rxlen = 0;
	private static DECODE_STATE _decode_state = DECODE_STATE.DECODE_STATE_UNSYNCED;

	public static final boolean GotNewPackage() {
		boolean ret = GotNewPackage;
		GotNewPackage = false;
		return ret;
	}

	public static final DECODE_STATE rx_decode(byte data) throws CloneNotSupportedException {
		switch(_decode_state) {
			case DECODE_STATE_UNSYNCED:
				if(data == Header1) {
					_decode_state = DECODE_STATE.DECODE_STATE_GOT_STX1;
					rPacket.stx1 = Header1;
				}
			break;
			case DECODE_STATE_GOT_STX1:
				if(data == Header2) {
					_decode_state = DECODE_STATE.DECODE_STATE_GOT_STX2;
					rPacket.stx2 = Header2;
				}
				else
					_decode_state = DECODE_STATE.DECODE_STATE_UNSYNCED;
			break;
			case DECODE_STATE_GOT_STX2:
				rPacket.length = data;
				_rxlen = 0;
				_decode_state = DECODE_STATE.DECODE_STATE_GOT_LEN;
			break;
			case DECODE_STATE_GOT_LEN:
				rPacket.type = data;
//				_rxlen ++;
				_rxlen = 1;
				_decode_state = DECODE_STATE.DECODE_STATE_GOT_TYPE;
			break;
			case DECODE_STATE_GOT_TYPE:
				rPacket.rData[_rxlen - 1] = data;
				_rxlen ++;
				if(_rxlen == rPacket.length - 1) {
					_decode_state = DECODE_STATE.DECODE_STATE_GOT_DATA;
				}
				if(_rxlen > rPacket.rData.length) {
					_decode_state = DECODE_STATE.DECODE_STATE_UNSYNCED;
				}
			break;
			case DECODE_STATE_GOT_DATA:
				rPacket.crc = (char) (data & 0xFF);/* get unsigned value */
				_rxlen ++;
				if(CalculateCRC.ComputeCRC8(rPacket.getCRCBuffer(), _rxlen, CRC_INIT) == rPacket.crc) {
					synchronized(new String("")) {//critical
						RecPackage = (ComPackage)rPacket.PackageCopy();
						GotNewPackage = true;
					}
				} else {
					//err package.
//					System.err.println("Bad CRC!");
				}
				_decode_state = DECODE_STATE.DECODE_STATE_UNSYNCED;
			break;
			default:
				_decode_state = DECODE_STATE.DECODE_STATE_UNSYNCED;
			break;
		}
		return _decode_state;
	}
}
