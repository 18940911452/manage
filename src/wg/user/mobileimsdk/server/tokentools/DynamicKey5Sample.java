package wg.user.mobileimsdk.server.tokentools;

import wg.user.mobileimsdk.server.tokentools.DynamicKey5;

import java.util.Date;
import java.util.Random;

/**
 * Created by Li on 10/1/2016.
 */
public class DynamicKey5Sample {
    static String appID = "e54476871b6b4496b7ce21b2b56bbfae";
    static String appCertificate = "be87882ae3aa43c1968ff0dc6e192e80";
    static String channel = "7d72365eb983485397e3e3f9d460bdda";
    static int ts = (int)(new Date().getTime()/1000);
    static int r = new Random().nextInt();
    static long uid = 0;
    static int expiredTs = 0;

    public static void main(String[] args) throws Exception {
        System.out.println(DynamicKey5.generateMediaChannelKey(appID, appCertificate, channel, ts, r, uid, expiredTs));
        System.out.println(DynamicKey5.generateRecordingKey(appID, appCertificate, channel, ts, r, uid, expiredTs));
        System.out.println(DynamicKey5.generateInChannelPermissionKey(appID, appCertificate, channel, ts, r, uid, expiredTs, DynamicKey5.noUpload));
        System.out.println(DynamicKey5.generateInChannelPermissionKey(appID, appCertificate, channel, ts, r, uid, expiredTs, DynamicKey5.audioVideoUpload));
    }
}
