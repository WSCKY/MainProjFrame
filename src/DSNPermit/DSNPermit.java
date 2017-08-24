package DSNPermit;

import java.io.IOException;
import java.util.prefs.Preferences;

public class DSNPermit {
	private static char cmd = ' ';
	public static void main(String[] args) {
		System.out.println("F1/2序列号写入许可证工具 (V1.0.1)  >>>");
		System.out.println(" -> 按 '1' 写入许可证");
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
				System.out.println(" -> 按 '1' 写入许可证");
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
		Preferences p = Preferences.userRoot().node("/common");
		if(cmd == '1') {
			System.out.println("开始写入注册表信息...");
			p.put("_dsn_allowed", "kyChuPermitted");
			System.out.println("写入完成,验证写入...");
			String s = p.get("_dsn_allowed", "");
			if(s.equals("kyChuPermitted"))
				System.out.println("许可成功！");
			else
				System.out.println("许可失败！");
		} else if(cmd == '2') {
			System.out.println("开始移除许可证...");
			p.remove("_dsn_allowed");
			System.out.println("移除完成,验证结果...");
			String s = p.get("_dsn_allowed", "");
			if(s.equals("kyChuPermitted"))
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
