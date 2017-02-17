package org.gloria.zhihu.util;

import org.apache.commons.codec.binary.Base64;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

/**
 * MD5生成工具
 * @author gloria
 *
 */
public class Md5Util {
	private static final char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
		'a', 'b', 'c', 'd', 'e', 'f' };// 用来将字节转换成 16 进制表示的字符
	
	private static char[] base64_table = new char[] { '0', '1', '2', '3', '4',
		'5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h',
		'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u',
		'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H',
		'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U',
		'V', 'W', 'X', 'Y', 'Z', '-', '_' };
	
	/**
	 * create an identity for this image serial
	 * 
	 * @return the identity
	 */
	public static String generateIdentity() {
		String str = System.currentTimeMillis() + " " + Math.random() + " " + new Object().hashCode();
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(str.getBytes());
			byte[] tmp = md.digest();
			return Base64.encodeBase64URLSafeString(tmp);
		} catch (NoSuchAlgorithmException e) {
			Random rand = new Random();
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < 22; i++) {
				sb.append(base64_table[rand.nextInt() % 64]);
			}
			return sb.toString();
		}
	}
	
	/**
	 * 根据url生成md5
	 * @param url
	 * @return
	 */
	public static String generateMd5ByUrl(String url){
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(url.getBytes("UTF-8"));
			byte[] temp = md.digest();
			
			char str[] = new char[16 * 2]; //每个字节用 16 进制表示的话，使用两个字符，所以表示成 16 进制需要 32 个字符
			int k = 0;// 表示转换结果中对应的字符位置
			for (int i = 0; i < 16; i++) {// 从第一个字节开始，对 MD5 的每一个字节转换成 16 进制字符的转换
				byte byte0 = temp[i];// 取第 i 个字节
				str[k++] = hexDigits[byte0 >>> 4 & 0xf];//取字节中高 4 位的数字转换,>>> 为逻辑右移，将符号位一起右移
				str[k++] = hexDigits[byte0 & 0xf]; //取字节中低 4 位的数字转换
			}
			return new String(str);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	/**
	 * 生成md5
	 * @return
	 */
	public static String generateMd5(byte[] data){
		try {
			data = getMd5Byte128(data);
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(data);
			byte tmp[] = md.digest(); // MD5 的计算结果是一个 128 位的长整数，用字节表示就是 16 个字节
			char str[] = new char[16 * 2]; //每个字节用 16 进制表示的话，使用两个字符，所以表示成 16 进制需要 32 个字符
			int k = 0;// 表示转换结果中对应的字符位置
			for (int i = 0; i < 16; i++) {// 从第一个字节开始，对 MD5 的每一个字节转换成 16 进制字符的转换
				byte byte0 = tmp[i];// 取第 i 个字节
				str[k++] = hexDigits[byte0 >>> 4 & 0xf];//取字节中高 4 位的数字转换,>>> 为逻辑右移，将符号位一起右移
				str[k++] = hexDigits[byte0 & 0xf]; //取字节中低 4 位的数字转换
			}
			
			return new String(str);// 换后的结果转换为字符串
		} catch (Exception e) {
			return null;
		}
	}

	private static byte[] getMd5Byte128(byte[] data) {
		int remainder = data.length%6;
		int divisor = data.length/6;
		
		byte[][] splitData = new byte[6][];
		int srcPos = 0;
		for(int i=0;i<6;i++){
			int len = divisor;
			if(i<remainder){
				len += 1;
			}
			
			byte[] temp = new byte[len];
			System.arraycopy(data, srcPos, temp, 0, len);
			splitData[i] = temp;
			srcPos += len;
		}
		
		int destPos = 0;
		byte[] md5Byte = new byte[128];
		for(int i=1;i<splitData.length-1;i++){
			byte[] temp = splitData[i];
			if(temp.length < 32)
				return null;
			
			System.arraycopy(temp, 0, md5Byte, destPos, 32);
			destPos += 32;
		}
		
		return md5Byte;
	}
	
	public static void main(String[] args)  {

	}
}
