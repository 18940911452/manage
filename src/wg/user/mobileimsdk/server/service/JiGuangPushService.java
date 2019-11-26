package wg.user.mobileimsdk.server.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;

import cn.jiguang.common.ClientConfig;
import cn.jiguang.common.resp.APIConnectionException;
import cn.jiguang.common.resp.APIRequestException;
import cn.jpush.api.JPushClient;
import cn.jpush.api.push.PushResult;
import cn.jpush.api.push.model.Message;
import cn.jpush.api.push.model.Platform;
import cn.jpush.api.push.model.PushPayload;
import cn.jpush.api.push.model.SMS;
import cn.jpush.api.push.model.audience.Audience;
import cn.jpush.api.push.model.audience.AudienceTarget;
import cn.jpush.api.push.model.notification.IosAlert;
import cn.jpush.api.push.model.notification.IosNotification;
import cn.jpush.api.push.model.notification.Notification;
import wg.media.screen.fm.report.IJiGuangPushService;

public class JiGuangPushService implements IJiGuangPushService{
	public static String MSG_CONTENT="test";
	static Prop pro=PropKit.use("a_little_config_ms.txt");
	public static String masterSecret = pro.get("appSecret","946242e3b76ce11379794ab7");
	public static String appKey = pro.get("appKey","942d333e49c94d01c34ad78e");
//	public static String masterSecret = "c25be7a58ffe0781717554e3";
//	public static String appKey = "43edc0bc21851d1b9196944e";
	public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public static String ALERT="这个测试demo";
	public static ClientConfig config = ClientConfig.getInstance();
	{
		config.setApnsProduction(true);
	}
    /***
     * 
     * @param fingerPrint
     * @param fromUserId	主叫者id
     * @param toUserId		被叫者id
     * @param fromUserName	主叫者名称
     * @param toUserName	被叫者名称
     * @param typeu			类型
     * @param message		消息内容
     * @return
     */
	@Override
	public String JpushSendMessage(String fingerPrint, String fromUserId, String toUserId, String fromUserName,
			String toUserName, String typeu, String message) {

		String alert = "";
		// String userMobileType=getPara("userMobileType");;

		if (typeu.equals("11") || typeu.equals("21")) {
			alert = message;
		} else if (typeu.equals("12") || typeu.equals("22")) {
			alert = "[图片]";
		} else if (typeu.equals("13") || typeu.equals("23")) {
			alert = "[语音]";
		} else if (typeu.equals("14") || typeu.equals("24")) {
			alert = "[视频]";
		} else if (typeu.equals("15") || typeu.equals("25")) {
			alert = "[文件]";
		} else if (typeu.equals("16") || typeu.equals("26")) {
			alert = "语音通话";
		} else if (typeu.equals("17") || typeu.equals("27")) {
			alert = "视频通话";
		}
		
//		JPushClient jpushClient = new JPushClient(masterSecret, appKey, null, ClientConfig.getInstance());
		JPushClient jpushClient = new JPushClient(masterSecret, appKey, null, config);
		// For push, all you need do is to build PushPayload object.
		// PushPayload payload = buildPushObject_all_alias_alert();//推送消息默认推送
		String re = "";

		PushPayload iosPayload = buildPushObject_ios_tagAnd_alertWithExtrasAndMessage(toUserId, alert, typeu,
				fromUserName);
		try {
			PushResult iosresult = jpushClient.sendPush(iosPayload);
			System.out.println("Got result - " + iosresult);
			re = re + iosresult;

		} catch (APIConnectionException e) {
			// Connection error, should retry later
			System.out.println("Connection error, should retry later" + e.getStackTrace());

		} catch (APIRequestException e) {
			// Should review the error, and fix the request
			System.out.println("Should review the error, and fix the request" + e.getMessage());
			System.out.println("HTTP Status: " + e.getStatus());
			System.out.println("Error Code: " + e.getErrorCode());
			System.out.println("Error Message: " + e.getErrorMessage());
		}

		PushPayload androidPayload = buildPushObject_android_tag_alertWithTitle(fingerPrint, toUserId, alert, typeu,
				fromUserId, toUserId, fromUserName, toUserName);
		try {
			PushResult androidresult = jpushClient.sendPush(androidPayload);
			System.out.println("Got result - " + androidresult);
			re = re + androidresult;
		} catch (APIConnectionException e) {
			// Connection error, should retry later
			System.out.println("Connection error, should retry later" + e.getStackTrace());

		} catch (APIRequestException e) {
			// Should review the error, and fix the request
			System.out.println("Should review the error, and fix the request" + e.getMessage());
			System.out.println("HTTP Status: " + e.getStatus());
			System.out.println("Error Code: " + e.getErrorCode());
			System.out.println("Error Message: " + e.getErrorMessage());
		}

		return re;
	}
  /***
   * 
   * @param fromUserId 		主叫者id
   * @param toUserId 		被叫者id
   * @param fromUserName	主叫者名称
   * @param toUserName		被叫者名称
   * @param type			类型
   *  		1.视频通话
   *	  	2.语音通话
   *	  	3.文字通知
   *	 	4.图片通知
   *		5.短视频通知
   *	 	6.文件通知
   * @param roomId			视频或者语音的房间号
   * @param message			消息内容
   * @return
   */
	@Override
	public String sendMessage(String fromUserId,String toUserId,String fromUserName,String toUserName,
			String type,String roomId,String message) {
		
		String alert = "";
		// String userMobileType=getPara("userMobileType");;
		// 1.视频通话
		// 2.语音通话
		// 3.文字通知
		// 4.图片通知
		// 5.短视频通知
		// 6.文件通知
		if (type.equals("1")) {
			alert = "邀请你视频通话";
		} else if (type.equals("2")) {
			alert = "邀请你语音通话";
		} else if (type.equals("3")) {
			alert = message;
		} else if (type.equals("4")) {
			alert = "[图片]";
		} else if (type.equals("5")) {
			alert = "[视频]";
		} else if (type.equals("6")) {
			alert = "[文件]";
		}

//		JPushClient jpushClient = new JPushClient(masterSecret, appKey, null, ClientConfig.getInstance());
		JPushClient jpushClient = new JPushClient(masterSecret, appKey, null, config);
		// For push, all you need do is to build PushPayload object.
		// PushPayload payload = buildPushObject_all_alias_alert();//推送消息默认推送
		String re = "";

		PushPayload iosPayload = buildPushObject_ios_tagAnd_alertWithExtrasAndMessageVideo(toUserId, alert, type,
				roomId, fromUserName);
		try {
			PushResult iosresult = jpushClient.sendPush(iosPayload);
			System.out.println("Got result - " + iosresult);
			re = re + iosresult;

		} catch (APIConnectionException e) {
			// Connection error, should retry later
			System.out.println("Connection error, should retry later" + e.getStackTrace());

		} catch (APIRequestException e) {
			// Should review the error, and fix the request
			System.out.println("Should review the error, and fix the request" + e.getMessage());
			System.out.println("HTTP Status: " + e.getStatus());
			System.out.println("Error Code: " + e.getErrorCode());
			System.out.println("Error Message: " + e.getErrorMessage());
		}

		PushPayload androidPayload = buildPushObject_android_tag_alertWithTitleVideo(toUserId, alert, type, roomId,
				fromUserId, toUserId, fromUserName, toUserName);
		try {
			PushResult androidresult = jpushClient.sendPush(androidPayload);
			System.out.println("Got result - " + androidresult);
			re = re + androidresult;
		} catch (APIConnectionException e) {
			// Connection error, should retry later
			System.out.println("Connection error, should retry later" + e.getStackTrace());

		} catch (APIRequestException e) {
			// Should review the error, and fix the request
			System.out.println("Should review the error, and fix the request" + e.getMessage());
			System.out.println("HTTP Status: " + e.getStatus());
			System.out.println("Error Code: " + e.getErrorCode());
			System.out.println("Error Message: " + e.getErrorMessage());
		}

		return re;
	}

	/**
	 * 快捷地构建推送对象：所有平台，所有设备，内容为 ALERT 的通知。
	 * 
	 * @return
	 */
	public PushPayload buildPushObject_all_all_alert() {
		return PushPayload.alertAll(ALERT + sdf.format(new Date()));
	}

	/**
	 * 构建推送对象：所有平台，推送目标是别名为 "alias1"，通知内容为 ALERT。
	 * 
	 * @return
	 */
	public PushPayload buildPushObject_all_alias_alert() {
		return PushPayload.newBuilder().setPlatform(Platform.all()).setAudience(Audience.alias("87"))
				.setNotification(Notification.alert(ALERT)).build();
	}

	/**
	 * 构建推送对象：平台是 Android，目标是 tag 为 "tag1" 的设备，内容是 Android 通知 ALERT，并且标题为 TITLE
	 * 
	 * @return
	 */
	public PushPayload buildPushObject_android_tag_alertWithTitle(String fp, String alias, String alert,
			String typeu, String fromUserId, String toUserId, String fromUserName, String toUserName) {
		// Message.newBuilder().setContentType(contentType);
		Map<String, String> map = new HashMap<String, String>();
		map.put("alias", alias);
		map.put("fingerPrint", fp);
		map.put("fromUserId", fromUserId);
		map.put("toUserId", toUserId);
		map.put("toUserName", toUserName);
		map.put("fromUserName", fromUserName);
		map.put("typeu", typeu);
		map.put("alert", alert);
		return PushPayload.newBuilder().setPlatform(Platform.android()).setAudience(Audience.alias(alias))
				.setNotification(Notification.android(alert, fromUserName, map)).build();
	}

	/**
	 * 构建推送对象：平台是 Android，目标是 tag 为 "tag1" 的设备，内容是 Android 通知 ALERT，并且标题为 TITLE
	 * 
	 * @return
	 */
	public PushPayload buildPushObject_android_tag_alertWithTitleVideo(String alias, String alert, String type,
			String roomId, String fromUserId, String toUserId, String fromUserName, String toUserName) {
		// Message.newBuilder().setContentType(contentType);
		Map<String, String> map = new HashMap<String, String>();
		map.put("alias", alias);
		map.put("roomId", roomId);
		map.put("fromUserId", fromUserId);
		map.put("toUserId", toUserId);
		map.put("toUserName", toUserName);
		map.put("fromUserName", fromUserName);
		map.put("contentType", type);

		return PushPayload.newBuilder().setPlatform(Platform.android()).setAudience(Audience.alias(alias))
				.setNotification(Notification.android(alert, fromUserName, map)).build();
	}

	/**
	 * 构建推送对象：平台是 iOS，推送目标是 "tag1", "tag_all" 的交集，推送内容同时包括通知与消息 - 通知信息是
	 * ALERT，角标数字为 5，通知声音为 "happy"， 并且附加字段 from = "JPush"；消息内容是 MSG_CONTENT。通知是
	 * APNs 推送通道的，消息是 JPush 应用内消息通道的。 APNs 的推送环境是“生产”（如果不显式设置的话，Library
	 * 会默认指定为开发）
	 * 
	 * @return
	 */
	public PushPayload buildPushObject_ios_tagAnd_alertWithExtrasAndMessage(String alias, String alert,
			String type, String fromUserName) {
		String sound = "";
		if (type.equals("1") || type.equals("2")) {
			sound = "voip_call.caf";
		} else {
			sound = "default";
		}
		IosAlert alerts = IosAlert.newBuilder().setTitleAndBody(fromUserName, null, alert).build();

		return PushPayload.newBuilder().setPlatform(Platform.ios()).setAudience(Audience.alias(alias))
				.setNotification(Notification.newBuilder()
						.addPlatformNotification(IosNotification.newBuilder().setAlert(alerts).setSound(sound)// 默认为default，即系统默认声音。
																												// 如果设置为空值，则为静音。
								.build())
						.build())
				.build();
	}

	/**
	 * 构建推送对象：平台是 iOS，推送目标是 "tag1", "tag_all" 的交集，推送内容同时包括通知与消息 - 通知信息是
	 * ALERT，角标数字为 5，通知声音为 "happy"， 并且附加字段 from = "JPush"；消息内容是 MSG_CONTENT。通知是
	 * APNs 推送通道的，消息是 JPush 应用内消息通道的。 APNs 的推送环境是“生产”（如果不显式设置的话，Library
	 * 会默认指定为开发）
	 * 
	 * @return
	 */
	public PushPayload buildPushObject_ios_tagAnd_alertWithExtrasAndMessageVideo(String alias, String alert,
			String type, String roomId, String fromUserName) {
		String sound = "";
		if (type.equals("16") || type.equals("17")) {
			sound = "voip_call.caf";
		} else {
			sound = "default";
		}
		IosAlert alerts = IosAlert.newBuilder().setTitleAndBody(fromUserName, null, alert).build();

		return PushPayload.newBuilder().setPlatform(Platform.ios()).setAudience(Audience.alias(alias))
				.setNotification(Notification.newBuilder()
						.addPlatformNotification(
								IosNotification.newBuilder().setAlert(alerts).setBadge(1).setSound(sound)// 默认为default，即系统默认声音。
																											// 如果设置为空值，则为静音。
										.build())
						.build())
				.build();
	}

	/**
	 * 构建推送对象：平台是 Andorid 与 iOS，推送目标是 （"tag1" 与 "tag2" 的并集）交（"alias1" 与 "alias2"
	 * 的并集）， 推送内容是 - 内容为 MSG_CONTENT 的消息，并且附加字段 from = JPush。
	 * 
	 * @return
	 */
	public PushPayload buildPushObject_ios_audienceMore_messageWithExtras() {

		return PushPayload.newBuilder().setPlatform(Platform.android_ios())
				.setAudience(Audience.newBuilder().addAudienceTarget(AudienceTarget.tag("tag1", "tag2"))
						.addAudienceTarget(AudienceTarget.alias("alias1", "alias2")).build())
				.setMessage(Message.newBuilder().setMsgContent(MSG_CONTENT).addExtra("from", "JPush").build()).build();
	}

	/**
	 * 构建推送对象：推送内容包含SMS信息
	 */
	public void testSendWithSMS() {

		JPushClient jpushClient = new JPushClient(masterSecret, appKey);
		try {
			SMS sms = SMS.content("Test SMS", 10);
			PushResult result = jpushClient.sendAndroidMessageWithAlias("Test SMS", "test sms", sms, "alias1");
			System.out.println("Got result - " + result);
		} catch (APIConnectionException e) {
			System.out.println("Connection error. Should retry later. " + e.getStackTrace());
		} catch (APIRequestException e) {
			System.out.println("Error response from JPush server. Should review and fix it. " + e.getMessage());
			System.out.println("HTTP Status: " + e.getStatus());
			System.out.println("Error Code: " + e.getErrorCode());
			System.out.println("Error Message: " + e.getErrorMessage());
		}
	}
}
