package wg.user.mobileimsdk.server.controller;


import java.security.NoSuchAlgorithmException;

import java.util.Date;
import java.util.Random;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.aop.Clear;
import com.jfinal.core.Controller;
import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;

import wg.user.mobileimsdk.server.service.LoginService;
import wg.user.mobileimsdk.server.service.SignalingTokenService;
import wg.user.mobileimsdk.server.tokentools.DynamicKey5;

/**
 * 获取证书签名，用户视频
 * @author DELL
 *
 */
public class SignalingTokenController  extends Controller{
	public static LoginService ser;
	static Prop pro;
	public static String appCertificate;
	public static String appID;

	static {
		SignalingTokenController.pro = PropKit.use("a_little_config_ms.txt");
		appCertificate = SignalingTokenController.pro.get("appCertificate", "946242e3b76ce11379794ab7");
		appID = SignalingTokenController.pro.get("appID", "942d333e49c94d01c34ad78e");
	}
	/**
	 * 
	 * @method:getToken 
	 * @describe:TODO
	 * @author:  gongxiangPang 
	 * @param :TODO
	 *  appId             = "C5D15F8FD394285DA5227B533302A518" //App ID
		appCertificate    = "fe1a0437bf217bdd34cd65053fb0fe1d" //App Certificate
		expiredTime       = "1546271999" // 授权时间戳
		account           = "test@agora.io" //客户端定义的用户 ID
	 */
	public void getToken(){
		
		String appId=getPara("appId",appID);
		String certificate=getPara("certificate",appCertificate);
		String account=getPara("account","2019");
		long expiredTsInSeconds=getParaToInt("expiredTsInSeconds",1546271999);//
		//expiredTsInSeconds = new Date().getTime() / 1000+ expiredTsInSeconds;
		JSONObject jo=new JSONObject();
		try {
			String token = SignalingTokenService.ser.getToken(appId, certificate, account, expiredTsInSeconds);
			jo.put("result", token);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		renderJson(jo);
	}
	
	/**
	 * 频道
	 */
	public void getKey(){
		  String channel=getPara("channel","name");
		  int ts = (int)(new Date().getTime()/1000);
		  int r = new Random().nextInt();
		  long uid = 0;
		  int expiredTs = 1672502399;
		JSONObject jo=new JSONObject();
		  try {
			 jo.put("channelKey", DynamicKey5.generateMediaChannelKey(appID, appCertificate, channel, ts, r, uid, expiredTs));
			 jo.put("recordingKey", DynamicKey5.generateRecordingKey(appID, appCertificate, channel, ts, r, uid, expiredTs));
			 System.out.println(DynamicKey5.generateMediaChannelKey(appID, appCertificate, channel, ts, r, uid, expiredTs));
			 System.out.println(DynamicKey5.generateRecordingKey(appID, appCertificate, channel, ts, r, uid, expiredTs));
		     System.out.println(DynamicKey5.generateInChannelPermissionKey(appID, appCertificate, channel, ts, r, uid, expiredTs, DynamicKey5.noUpload));
		     System.out.println(DynamicKey5.generateInChannelPermissionKey(appID, appCertificate, channel, ts, r, uid, expiredTs, DynamicKey5.audioVideoUpload));
		} catch (Exception e) {
			e.printStackTrace();
		}
	       
		renderJson(jo);
		  
	}
	
	
	/**
	 * 频道
	 */
	@Clear
	public void getChannelKey(){
		  String channel=getPara("channel","test");
		  int ts = (int)(new Date().getTime()/1000);
		  int r = new Random().nextInt();
		  long uid = 0;
		  int expiredTs = 1669910399;
		  String key="";
		  try {
			   key= DynamicKey5.generateMediaChannelKey(appID, appCertificate, channel, ts, r, uid, expiredTs);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		renderText(key);
	}
	
	
	
	
	
	
	
	
	
	
	
}
