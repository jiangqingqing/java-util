/**
 * 创建时间：2019年9月29日
 */
package jqq.encrypt;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Hex;

/**
 * AES对称加密工具类。
 * 
 * @author <a href="mailto:nieyong.ny@alibaba-inc.com">聂勇</a>
 */
public class AesUtil {

    private final static String KEY_AES = "AES";
    
    /**
     * AES解密。
     * 
     * @param src 待解密的字符串
     * @param key AES密钥
     * @return 解密后的字符串
     * @throws Exception
     */
    public static String decrypt(String src, String key) throws Exception {
        if (key == null || key.length() != 16) {
            throw new Exception("aes key 不满足条件");
        }
        
        byte[] raw = key.getBytes();
        SecretKeySpec skeySpec = new SecretKeySpec(raw, KEY_AES);
        Cipher cipher = Cipher.getInstance(KEY_AES);
        cipher.init(Cipher.DECRYPT_MODE, skeySpec);
        byte[] encrypted1 = Hex.decodeHex(src);
        byte[] original = cipher.doFinal(encrypted1);
        String originalStr = new String(original);
        
        return originalStr;
    }

    /**
     * AES加密
     */
    public static String encrypt(String content, String key) throws Exception {
        if (key == null || key.length() != 16) {
            throw new Exception("aes key 不满足条件");
        }
        SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes(), KEY_AES);
        Cipher cipher = Cipher.getInstance(KEY_AES);// 创建密码器
        byte[] byteContent = content.getBytes("utf-8");
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);// 初始化为加密模式的密码器
        byte[] result = cipher.doFinal(byteContent);// 加密
        return String.valueOf(Hex.encodeHex(result));
    }


    public static void main(String[] args) throws Exception {
        String test = "jqq1234";
        String key = "shfkafhk&^%^&*HD";
        String encrypt = encrypt(test, key);
        System.out.println(encrypt);
        System.out.println(decrypt(encrypt,key));
    }

}
