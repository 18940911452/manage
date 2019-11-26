package wg.user.mobileimsdk.server.tokentools;

import wg.user.mobileimsdk.server.tokentools.AccessToken;

import java.util.Date;
import java.util.Random;

public class AccessTokenSample {
    private String appId = "e54476871b6b4496b7ce21b2b56bbfae";
    private String appCertificate = "be87882ae3aa43c1968ff0dc6e192e80";
    private String channelName = "7d72365eb983485397e3e3f9d460bdda";
    private String uid = "2882341273";
    private int ts = (int) (new Date().getTime() / 1000);
    private int salt = new Random().nextInt();
    private int expiredTs = 0;

    public void testGenerateDynamicKey() throws Exception {
        String expected = "006970CA35de60c44645bbae8a215061b33IACV0fZUBw+72cVoL9eyGGh3Q6Poi8bgjwVLnyKSJyOXR7dIfRBXoFHlEAABAAAAR/QQAAEAAQCvKDdW";
        AccessToken token = new AccessToken(appId, appCertificate, channelName, uid, ts, salt);
        token.addPrivilege(AccessToken.Privileges.kJoinChannel, expiredTs);
      /*  String result = token.build();
        System.out.println(result);*/
    }
}
