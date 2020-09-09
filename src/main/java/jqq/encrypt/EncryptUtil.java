package jqq.encrypt;

import java.security.MessageDigest;
import org.apache.commons.codec.binary.Hex;
import org.springframework.util.DigestUtils;

/**
 * @author <a href="mailto:wanqi.lwq@alibaba-inc.com">wanqi.lwq</a>
 * @version 1.0.0
 * @description
 * @since 2019/9/24
 */
public class EncryptUtil {


    private final static String md5Key = "";
    private final static String aesKey = "shfkafhk&^%^&*HD";

    /**
     * 进行md5
     * @param data
     * @return
     */
    public static String encryptWithMd5(String data) {
        return DigestUtils.md5DigestAsHex((data + md5Key).getBytes());
    }


    public static String hexMD5(String value) throws Exception {
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        messageDigest.reset();
        messageDigest.update(value.getBytes("utf-8"));
        byte[] digest = messageDigest.digest();
        return byteToHexString(digest);
    }

    public static String decryptWithAes(String data) throws Exception {
        return AesUtil.decrypt(data,aesKey);
    }

    public static String encryptWithAes(String data) throws Exception {
        return AesUtil.encrypt(data,aesKey);
    }

    private static String byteToHexString(byte[] bytes) {
        return String.valueOf(Hex.encodeHex(bytes));
    }



}
