package tftpUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;
import java.net.UnknownHostException;

import org.apache.commons.net.tftp.TFTP;
import org.apache.commons.net.tftp.TFTPClient;

public class tftpUtil {
	private static TFTPClient tftpc = new TFTPClient();

	public static boolean uploadFile(String HostName, String remoteFileName, InputStream input) {
		tftpc.setDefaultTimeout(10000);
		try {
			tftpc.open();
		} catch (SocketException e) {
			System.err.println("can not open local UDP socket!");
			System.err.println(e.getMessage());
		}

		try {
			tftpc.sendFile(remoteFileName, TFTP.BINARY_MODE, input, HostName);
		} catch (IOException e) {
			System.err.println("�ļ�IO�쳣!");
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			tftpc.close();
			if(input != null) {
				try {
					input.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return true;
	}
	public static boolean uploadFile(String hostname, String remoteFilename, String localFilePath) {
		FileInputStream fileInput=null;
		try {
			fileInput=new FileInputStream(localFilePath);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		return uploadFile(hostname, remoteFilename, fileInput);
	}

	public static boolean downloadFile(String hostname, String localFilename, String remoteFilename, int port) {
		tftpc.setDefaultTimeout(60000);

		try {
			tftpc.open();
		} catch (SocketException e) {
			System.err.println("�޷��򿪱��� UDP socket!");
			System.err.println(e.getMessage());
		}

		boolean closed,success;
		closed = false;
		success = false;
		FileOutputStream output = null;
		File file;

		file = new File(localFilename);
		if(file.exists()){
			System.err.println("�ļ�: " + localFilename + " �Ѿ�����!");
			return success;
		}

		try {
			output = new FileOutputStream(file);
		} catch (IOException e) {
			tftpc.close();
			System.err.println("�޷���Ҫд��ı����ļ�!");
			System.err.println(e.getMessage());
			return success;
		}

		try {
			tftpc.receiveFile(remoteFilename, TFTP.BINARY_MODE, output, hostname, port);
			//tftp.receiveFile(remoteFilename, TFTP.BINARY_MODE, output, hostname);
			success = true;
		} catch (UnknownHostException e) {
			System.err.println("�޷���������!");
			System.err.println(e.getMessage());
			return success;
		} catch (IOException e) {
			System.err.println("�����ļ�ʱ��I/O�쳣!");
			System.err.println(e.getMessage());
			return success;
		} finally {
			// �رձ��� socket ��������ļ�
			tftpc.close();
			try {
				if (null != output) {
					output.close();
				}
				closed = true;
			} catch (IOException e) {
				closed = false;
				System.err.println("�ر��ļ�ʱ����!");
				System.err.println(e.getMessage());
			}
		}
		if(!closed)
			return false;

		return success;
}

	public static void main(String[] args) {
		System.out.println("download file from 192.168.10.104:69");
		downloadFile("192.168.10.104", "C:\\Users\\kyChu\\Desktop\\java_tftp_rec.txt", "...", 69);
		System.out.println("upload file to 192.168.10.104");
		uploadFile("192.168.10.104", "user2.bin", "C:\\Users\\kyChu\\Desktop\\user2.bin");
	}
}
