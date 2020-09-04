package cdac.in.payment;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class CheckSumUtil {

	public static void main(String arg[]){

	}

	public static String checkSumMD5(String plaintext){

		MessageDigest md = null;

		try{
			md = MessageDigest.getInstance("MD5");
			md.update(plaintext.getBytes("UTF-8"));
		}
		catch (Exception e){

			md = null;
		}

		StringBuffer ls_sb = new StringBuffer();

		byte raw[] = md.digest();

		for (int i = 0; i < raw.length; i++){

			ls_sb.append(char2hex(raw[i]));
		}
		return ls_sb.toString().toLowerCase(); 
	}

	public static String checkSumSHA256(String data) {

		MessageDigest md = null;
		StringBuffer hexString = new StringBuffer();

		try {
			md = MessageDigest.getInstance("SHA-256");
			md.update(data.getBytes());
			byte byteData[] = md.digest();
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < byteData.length; i++) {
				sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
			}
			for (int i=0;i<byteData.length;i++) {
				String hex=Integer.toHexString(0xff & byteData[i]);
				if(hex.length()==1) hexString.append('0');
				hexString.append(hex);

			}
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		return hexString.toString();
	}

	public static String char2hex(byte x)
	{
		char arr[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
		char c[] = { arr[(x & 0xF0) >> 4], arr[x & 0x0F] };

		return (new String(c));
	}
}
