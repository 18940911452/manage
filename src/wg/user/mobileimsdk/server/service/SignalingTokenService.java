package wg.user.mobileimsdk.server.service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

public class SignalingTokenService {
	
	public static  SignalingTokenService ser =new SignalingTokenService();
	
	
	/**
	 * 
	 * @method:getToken 
	 * @describe:TODO
	 * @author:  gongxiangPang 
	 * @param :TODO
	 * @param appId    					98e5e4f243ec429fb86e3a615b7314d3
	 * @param certificate				95fca9ea65584772838e781951273be6
	 * @param account					zhongkewenge@163.com
	 * @param expiredTsInSeconds     	3600
	 * @return
	 * @throws NoSuchAlgorithmException
	 * 
	 */
	public static String getToken(String appId, String certificate, String account, long expiredTsInSeconds) throws NoSuchAlgorithmException {

        StringBuilder digest_String = new StringBuilder().append(account).append(appId).append(certificate).append(expiredTsInSeconds);
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        md5.update(digest_String.toString().getBytes());
        byte[] output = md5.digest();
        String token = hexlify(output);
        String token_String = new StringBuilder().append("1").append(":").append(appId).append(":").append(expiredTsInSeconds).append(":").append(token).toString();
        return token_String;
    }
	
	
    public static String hexlify(byte[] data) {
        char[] DIGITS_LOWER = {'0', '1', '2', '3', '4', '5',
                '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        char[] toDigits = DIGITS_LOWER;
        int l = data.length;
        char[] out = new char[l << 1];
        // two characters form the hex value.
        for (int i = 0, j = 0; i < l; i++) {
            out[j++] = toDigits[(0xF0 & data[i]) >>> 4];
            out[j++] = toDigits[0x0F & data[i]];
        }
        return String.valueOf(out);
    }

}
