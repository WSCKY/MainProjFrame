package FileEncrypterPermit;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.prefs.Preferences;

public class EncrypterPermit {
	private static char cmd = ' ';
	public static void main(String[] args) {
		System.out.println("F1/2�ɿع̼����ܹ������֤ (V0.0.1)  >>>");
		System.out.println(" -> �� '1' ��װ���֤");
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
				System.out.println(" -> �� '1' ��װ���֤");
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
		Date CurrentTime = new Date(); /* get current time */
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Preferences p = Preferences.userRoot().node("kyChuLicenseDate");
		if(p.get("_fisrt_day", "").equals("")) {
			p.put("_fisrt_day", df.format(CurrentTime));
		}
		if(cmd == '1') {
			System.out.println("��ʼ��װ���֤��...");
			p.put("_license_key", "_kychu_permit");
			System.out.println("��װ���,��֤��װ...");
			String s = p.get("_license_key", "");
			if(s.equals("_kychu_permit"))
				System.out.println("��ɳɹ���");
			else
				System.out.println("���ʧ�ܣ�");
		} else if(cmd == '2') {
			System.out.println("��ʼ�Ƴ����֤...");
			p.remove("_license_key");
			System.out.println("�Ƴ����,��֤���...");
			String s = p.get("_license_key", "");
			if(s.equals("_kychu_permit"))
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
