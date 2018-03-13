package FileEncrypterPermit;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.prefs.Preferences;

public class EncrypterPermit {
	private static char cmd = ' ';
	public static void main(String[] args) {
		System.out.println("F1/2飞控固件加密工具许可证 (V0.0.1)  >>>");
		System.out.println(" -> 按 '1' 安装许可证");
		System.out.println(" -> 按 '2' 移除许可证");
		System.out.println(" -> 按 'h' 获取帮助");
		System.out.println(" -> 按 'q' 退出系统");
		while(true) {
			try {
				cmd = (char) System.in.read();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(cmd == '1' || cmd == '2') {
				break;
			} else if(cmd == 'h') {
				System.out.println(" -> 按 '1' 安装许可证");
				System.out.println(" -> 按 '2' 移除许可证");
				System.out.println(" -> 按 'h' 获取帮助");
				System.out.println(" -> 按 'q' 退出系统");
			} else if(cmd == 'q') {
				System.exit(0);
			} else if(cmd != '\r' && cmd != '\n') {
				System.out.println("错误，请重新输入！");
			}
		}
		System.out.println("准备更新您的系统...");
		Date CurrentTime = new Date(); /* get current time */
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Preferences p = Preferences.userRoot().node("kyChuLicenseDate");
		if(p.get("_fisrt_day", "").equals("")) {
			p.put("_fisrt_day", df.format(CurrentTime));
		}
		if(cmd == '1') {
			System.out.println("开始安装许可证书...");
			p.put("_license_key", "_kychu_permit");
			System.out.println("安装完成,验证安装...");
			String s = p.get("_license_key", "");
			if(s.equals("_kychu_permit"))
				System.out.println("许可成功！");
			else
				System.out.println("许可失败！");
		} else if(cmd == '2') {
			System.out.println("开始移除许可证...");
			p.remove("_license_key");
			System.out.println("移除完成,验证结果...");
			String s = p.get("_license_key", "");
			if(s.equals("_kychu_permit"))
				System.out.println("移除失败！");
			else
				System.out.println("移除成功！");
		} else {
			System.out.println("命令出错！");
		}
		System.out.println("按 'q' 退出...");
		while(true) {
			try {
				cmd = (char) System.in.read();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(cmd == 'q')
				break;
		}
	}
}
