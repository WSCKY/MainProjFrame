/**
 * @brief  DSN Generator.
 * @author '^_^'
 * @Date   2017/8/11
 */
package DSNGenerator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.swing.filechooser.FileSystemView;

public class DSNGenerator {
	private static String _FilePath = null;
	private static String _FileName = null;
	private static DateFormat _SysDF = null;
	private static Calendar _SysCalendar = null;
	private static OutputStream _FileOutStream = null;
	private static int DroneType = 1;
	private static String Country = "CN";
	private static String _LastDSN = "PX1707000000FxCN"; /* default */
	private static int SerialNumber = 0;

	public DSNGenerator(int WhichDrone) {
		DroneType = WhichDrone;
		_FilePath = FileSystemView.getFileSystemView().getDefaultDirectory().getPath() + File.separator + "PartnerxF" + DroneType + File.separator;
		_SysDF = new SimpleDateFormat("yyMM");
		_SysCalendar = Calendar.getInstance();
		_FileName = _SysDF.format(new Date()) + ".csv";
		File dstFile = new File(_FilePath + _FileName);
		if(!dstFile.getParentFile().exists()) {
			dstFile.getParentFile().mkdirs();
//			System.err.println("Create New Directory.");
		}
		if(!dstFile.exists()) {
//			System.err.println("Create New File.");
			try {
				dstFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			_FileOutStream = new FileOutputStream(dstFile, true);/* write to the end of file */
			if(dstFile.length() >= 18) {
				@SuppressWarnings("resource")
				RandomAccessFile rf = new RandomAccessFile(_FilePath + _FileName, "r");
				long FileLength = rf.length();
				long start = rf.getFilePointer();
				long readIndex = start + FileLength - 18;
				rf.seek(readIndex);
				_LastDSN = rf.readLine(); /* 16 Bytes. */
				if(_SysCalendar.get(Calendar.WEEK_OF_MONTH) == GetWeeksFromDSN(_LastDSN))
					SerialNumber = GetSerialNumberFromDSN(_LastDSN);
				else
					SerialNumber = 0;
			} else {
				SerialNumber = 0;
//				_LastDSN = GotNewDSN();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void GeneratorClose() {
		try {
			_FileOutStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void SetCountry(String c) {
		if(c.length() == 2)
			Country = c;
	}

	public String GotNewDSN() {
		String _dsn = "PX";
		_SysCalendar.setTime(new Date());
		_dsn = _dsn.concat(_SysDF.format(new Date())).concat(String.format("%02d", _SysCalendar.get(Calendar.WEEK_OF_MONTH)));
		_dsn = _dsn.concat(String.format("%04d", (SerialNumber + 1))).concat("F" + DroneType + Country);
		_LastDSN = _dsn;
		return _dsn;
	}

	public void SaveThisDSN() {
		try {
			_FileOutStream.write(((SerialNumber + 1) + "," + _LastDSN + "\r\n").getBytes());
			_FileOutStream.flush();
			SerialNumber ++;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String GetDateFromDSN(String DSN) {
		return (DSN.substring(2, 6));
	}

	public int GetSerialNumberFromDSN(String DSN) {
		String sSN = DSN.substring(8, 12);
		return Integer.valueOf(sSN, 10); /* Ê®½øÖÆ  */
	}

	public int GetWeeksFromDSN(String DSN) {
		return Integer.valueOf(DSN.substring(6, 8));
	}
}
