package DSNPermit;

import java.io.IOException;
import java.util.prefs.Preferences;

public class DSNPermit {
	private static char cmd = ' ';
	public static void main(String[] args) {
		System.out.println("F1/2���к�д�����֤���� (V1.0.1)  >>>");
		System.out.println(" -> �� '1' д�����֤");
		System.out.println(" -> �� '2' �Ƴ����֤");
		System.out.println(" -> �� 'h' ��ȡ����");
		System.out.println(" -> �� 'q' �˳�ϵͳ");
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
				System.out.println(" -> �� '1' д�����֤");
				System.out.println(" -> �� '2' �Ƴ����֤");
				System.out.println(" -> �� 'h' ��ȡ����");
				System.out.println(" -> �� 'q' �˳�ϵͳ");
			} else if(cmd == 'q') {
				System.exit(0);
			} else if(cmd != '\r' && cmd != '\n') {
				System.out.println("�������������룡");
			}
		}
		System.out.println("׼����������ϵͳ...");
		Preferences p = Preferences.userRoot().node("/common");
		if(cmd == '1') {
			System.out.println("��ʼд��ע�����Ϣ...");
			p.put("_dsn_allowed", "kyChuPermitted");
			System.out.println("д�����,��֤д��...");
			String s = p.get("_dsn_allowed", "");
			if(s.equals("kyChuPermitted"))
				System.out.println("��ɳɹ���");
			else
				System.out.println("���ʧ�ܣ�");
		} else if(cmd == '2') {
			System.out.println("��ʼ�Ƴ����֤...");
			p.remove("_dsn_allowed");
			System.out.println("�Ƴ����,��֤���...");
			String s = p.get("_dsn_allowed", "");
			if(s.equals("kyChuPermitted"))
				System.out.println("�Ƴ�ʧ�ܣ�");
			else
				System.out.println("�Ƴ��ɹ���");
		} else {
			System.out.println("�������");
		}
		System.out.println("�� 'q' �˳�...");
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
