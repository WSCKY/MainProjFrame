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
			System.err.println("文件IO异常!");
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
			System.err.println("无法打开本地 UDP socket!");
			System.err.println(e.getMessage());
		}

		boolean closed,success;
		closed = false;
		success = false;
		FileOutputStream output = null;
		File file;

		file = new File(localFilename);
		if(file.exists()){
			System.err.println("文件: " + localFilename + " 已经存在!");
			return success;
		}

		try {
			output = new FileOutputStream(file);
		} catch (IOException e) {
			tftpc.close();
			System.err.println("无法打开要写入的本地文件!");
			System.err.println(e.getMessage());
			return success;
		}

		try {
			tftpc.receiveFile(remoteFilename, TFTP.BINARY_MODE, output, hostname, port);
			//tftp.receiveFile(remoteFilename, TFTP.BINARY_MODE, output, hostname);
			success = true;
		} catch (UnknownHostException e) {
			System.err.println("无法解析主机!");
			System.err.println(e.getMessage());
			return success;
		} catch (IOException e) {
			System.err.println("接收文件时有I/O异常!");
			System.err.println(e.getMessage());
			return success;
		} finally {
			// 关闭本地 socket 和输出的文件
			tftpc.close();
			try {
				if (null != output) {
					output.close();
				}
				closed = true;
			} catch (IOException e) {
				closed = false;
				System.err.println("关闭文件时出错!");
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
