package com.csp.app.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.TreeMap;

/**

 * @author chengsp
 * @version 1.0
 * @since 1.0
 */
public class MD5Encoder {
    private final static Logger logger = LoggerFactory.getLogger(MD5Encoder.class);

    public static String encode(String plainText) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(plainText.getBytes());
            byte b[] = md.digest();
            int i;
            StringBuffer buf = new StringBuffer("");
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0) {
                    i += 256;
                }
                if (i < 16) {
                    buf.append("0");
                }
                buf.append(Integer.toHexString(i));
            }
            return buf.toString();
        } catch (NoSuchAlgorithmException e) {
            logger.error(e.toString(), e);
        }
        return null;
    }

    public static String getKeyedDigestGBKWithMap(TreeMap<String, String> tm, String key) {
        StringBuffer buf = new StringBuffer();
        for (Map.Entry<String, String> en : tm.entrySet()) {
            String name = en.getKey();
            String value = en.getValue();
            if (value != null && value.length() > 0 && !"null".equals(value)) {
                buf.append(name).append('=').append(value).append('&');
            }
        }
        String _buf = buf.toString();

        String verifyReq = getKeyedDigestGBK(_buf.substring(0, _buf.length() - 1),key);

        return verifyReq;
    }

    public static String getKeyedDigestGBK(String strSrc, String key) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(strSrc.getBytes("GBK"));

            String result="";
            byte[] temp;
            temp=md5.digest(key.getBytes("UTF8"));
            for (int i=0; i<temp.length; i++){
                result+=Integer.toHexString((0x000000ff & temp[i]) | 0xffffff00).substring(6);
            }

            return result.toUpperCase();

        } catch (NoSuchAlgorithmException e) {

            e.printStackTrace();

        }catch(Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
	 * 16位小写
	 * @param plainText
	 * @return
	 */
    public static String encode16(String plainText) {
        String result = "";
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(plainText.getBytes());
            byte b[] = md.digest();
            int i;
            StringBuffer buf = new StringBuffer("");
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0) {
                    i += 256;
                }
                if (i < 16) {
                    buf.append("0");
                }
                buf.append(Integer.toHexString(i));
            }
            //默认32位，截取为16位
            result = buf.toString().substring(8, 24);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 字节数组转16进制字符串
     */
    public static String bytes2HexString(byte[] b) {
        String ret = "";
        for (int i = 0; i < b.length; i++) {
            String hex = Integer.toHexString(b[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            ret += hex;
        }
        return ret;
    }

    /**
     * 字符串md5加密再转16进制
     *
     * @param input     要加密的字符串
     * @param algorithm 算法 MD5,sha1等
     * @return
     */
    public static String stringAlgorithm2HexString(String input, String algorithm) {
        MessageDigest messageDigest;
        try {
            messageDigest = MessageDigest.getInstance(algorithm);//MD5,sha1等
            messageDigest.update(input.getBytes("utf-8"));
            return byteArrayToString(messageDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            logger.error(e.toString(), e);
        } catch (UnsupportedEncodingException e) {
            logger.error(e.toString(), e);
        }
        return null;
    }

    public static String getMdSign(String secret,String timestamp){
        String s = timestamp + secret;
        return encode(s).substring(16);
    }

    // 下面这个函数用于将字节数组换成成16进制的字符串
    public static String byteArrayToString(byte[] byteArray) {
        // 首先初始化一个字符数组，用来存放每个16进制字符
        char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        // new一个字符数组，这个就是用来组成结果字符串的（解释一下：一个byte是八位二进制，也就是2位十六进制字符（2的8次方等于16的2次方））
        char[] resultCharArray = new char[byteArray.length * 2];
        // 遍历字节数组，通过位运算（位运算效率高），转换成字符放到字符数组中去
        int index = 0;
        for (byte b : byteArray) {
            resultCharArray[index++] = hexDigits[b >>> 4 & 0xf];
            resultCharArray[index++] = hexDigits[b & 0xf];
        }
        // 字符数组组合成字符串返回
        return new String(resultCharArray);
    }

    /**
     * 把中文转成Unicode码
     *
     * @param str
     * @return
     */
    public static String chinaToUnicode(String str) {
        String result = "";
        for (int i = 0; i < str.length(); i++) {
            int chr1 = (char) str.charAt(i);
            // 汉字范围 \u4e00-\u9fa5 (中文)
            if (chr1 >= 19968 && chr1 <= 171941) {
                result += "\\u" + Integer.toHexString(chr1);
            } else {
                result += str.charAt(i);
            }
        }
        return result;
    }

}
