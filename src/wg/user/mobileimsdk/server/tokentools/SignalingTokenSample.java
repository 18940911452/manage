package wg.user.mobileimsdk.server.tokentools;

import wg.user.mobileimsdk.server.tokentools.SignalingToken;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

public class SignalingTokenSample {

    public static void main(String []args) throws NoSuchAlgorithmException{

        String appId = "e54476871b6b4496b7ce21b2b56bbfae";
        String certificate = "be87882ae3aa43c1968ff0dc6e192e80";
        String account = "TestAccount";
        //Use the current time plus an available time to guarantee the only time it is obtained
        int expiredTsInSeconds = 1446455471 + (int) (new Date().getTime()/1000l);
        String result = SignalingToken.getToken(appId, certificate, account, expiredTsInSeconds);
        System.out.println(result);

    }
}
