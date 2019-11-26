package wg.user.mobileimsdk.server.controller;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.json.FastJson;
import com.jfinal.kit.HttpKit;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import com.jfinal.core.Controller;

import wg.media.screen.fm.model.commandscreen.Umeng;
import wg.media.screen.fm.model.commandscreencommon.ManageUserInfo;
import wg.user.mobileimsdk.server.umpush.AndroidNotification;
import wg.user.mobileimsdk.server.umpush.PushClient;
import wg.user.mobileimsdk.server.umpush.android.AndroidBroadcast;
import wg.user.mobileimsdk.server.umpush.android.AndroidCustomizedcast;
import wg.user.mobileimsdk.server.umpush.android.AndroidFilecast;
import wg.user.mobileimsdk.server.umpush.android.AndroidGroupcast;
import wg.user.mobileimsdk.server.umpush.android.AndroidUnicast;
import wg.user.mobileimsdk.server.umpush.ios.IOSBroadcast;
import wg.user.mobileimsdk.server.umpush.ios.IOSCustomizedcast;
import wg.user.mobileimsdk.server.umpush.ios.IOSFilecast;
import wg.user.mobileimsdk.server.umpush.ios.IOSGroupcast;
import wg.user.mobileimsdk.server.umpush.ios.IOSUnicast;


/**
 * 友盟推送
 * @author DELL
 *
 */
public class UmPush extends Controller{

	public static UmPush push=new UmPush();
	public void index() throws Exception  {
		JSONObject result=new JSONObject();
		result.put("code", "200");
		result.put("msg", "发送成功");
		renderText(result.toString());
		
	}
	/**
	 * 名词解释：
	 *  Appkey：应用唯一标识。友盟消息推送服务提供的appkey和友盟统计分析平台使用的同一套appkey。
		App Master Secret：服务器秘钥，用于服务器端调用API请求时对发送内容做签名验证。
		Device Token：友盟消息推送服务对设备的唯一标识。Android的device_token是44位字符串，iOS的device_token是64位。
		Alias：开发者自有账号，开发者可以在SDK中调用setAlias(alias, alias_type)接口将alias+alias_type与device_token做绑定，之后开发者就可以根据自有业务逻辑筛选出alias进行消息推送。
		单播(unicast)：向指定的设备发送消息。
		列播(listcast)：向指定的一批设备发送消息。
		广播(broadcast)：向安装该App的所有设备发送消息。
		组播(groupcast):：向满足特定条件的设备集合发送消息，例如: “特定版本”、”特定地域”等。
		文件播(filecast)：开发者将批量的device_token或者alias存放到文件，通过文件ID进行消息发送。
		自定义播(customizedcast)：开发者通过自有的alias进行推送，可以针对单个或者一批alias进行推送，也可以将alias存放到文件进行发送。
		通知-Android(notification)：消息送达到用户设备后，由友盟SDK接管处理并在通知栏上显示通知内容。
		消息-Android(message)：消息送达到用户设备后，消息内容透传给应用自身进行解析处理。
		通知-iOS：和APNs定义一致。
		静默推送-iOS：和APNs定义一致。
		测试模式：在广播、组播等大规模发送消息的情况下，为了防止开发者误将测试消息大面积发给线上用户，特增加了测试模式。 测试模式下，只会将消息发送给测试设备。测试设备需要到Web后台上手工添加。
		测试模式-Android：Android的测试设备是正式设备的一个子集
		测试模式-iOS： iOS的测试模式对应APNs的开发环境(sandbox), 正式模式对应APNs的生产环境(prod)，测试设备和正式设备完全隔离。
		签名： 为了保证调用API的请求是合法者发送且参数没有被篡改，需要在调用API时对发送的所有内容进行签名。签名附加在调用地址后面，签名的计算方式参见附录K。
		推送类型：单播(unicast)、列播(listcast)、自定义播(customizedcast且不带file_id)统称为单播类消息，Web后台不会展示此类消息详细信息，仅展示前一天的汇总数据；广播(broadcast)、文件播(filecast)、组播(groupcast)、自定义播(customizedcast且file_id不为空)统称为任务类消息，任务类消息支持API查询、撤销操作，Web后台会展示任务类消息详细信息。
	 */
	//安卓
	public static String android_appkey = "5d5cbc284ca357ae790005ae";
	public static String android_appMasterSecret = "wb3omltvlhcr9qsmfc0fhtmiokravxvo";
	//个推推送key masterSecret
	public static String android_appkey2 = "5bnARcrBc1ALY7z9aFGv46";
	public static String android_appMasterSecret2 = "bVYw9vl4ck9D27DdEna7u5";
	//苹果
	public static String ios_appkey = "5d6ccfce0cafb26ab500076a";
	public static String ios_appMasterSecret = "4jhzsshqe4guzbmbbmj1wdic8qaeml3r";

	/**
	 * 新闻app推送
	 */
//	public void pushNews() throws Exception {
//		JSONObject json=new JSONObject();
//		String title=getPara("title");
//		String content=getPara("text");
//		String url=getPara("url");
//		String id=getPara("id");
//		String appInfoId=getHeader("app_info_id");
//		String type=getPara("type");
//		String jsonData=getPara("data");
//		String xwAppUrl="http://hongqi.wengetech.com:9001/news/getNewsDetailData";
//		Map<String, String> headers = new HashMap<String, String>(16);
//		headers.put("Content-Type", "application/json");
//		JSONObject jsonObject=new JSONObject();
//		jsonObject.put("type",type);
//		jsonObject.put("data",jsonData);
//		jsonObject.put("appInfoId",appInfoId);
//		String xw=HttpKit.post(xwAppUrl,null,jsonObject.toString(),headers);
//		JSONObject xsJson=new JSONObject(xw);
//		JSONArray jsonArray=xsJson.getJSONArray("data");
//		JSONObject pathJson=new JSONObject();
//		for(int i=0;i<jsonArray.length();i++){
//			pathJson=jsonArray.getJSONObject(i);
//			pathJson.remove("content");
//			pathJson.remove("imageCount");
//			pathJson.remove("reviewEditor");
//			pathJson.remove("shareUrl");
//			pathJson.remove("content");
//			pathJson.remove("pubDate");
//			pathJson.remove("content");
//		}
////		String xsPath=pathJson.toString();
//		JSONObject object=new JSONObject();
//		object.put("path",pathJson);
//		String xwPath=object.toString();
//		//安卓key sercet
//		String xwAndroidAppkey=null;
//		String xwAndroidSercet=null;
//		//IOS key sercet
//		String xwIosAppkey=null;
//		String xwIosSercet=null;
//		//安卓包路径
//		String xwAndroidPath=null;
//		//Ios包路径
//		String xwIosPath=null;
//		//安卓别名
//		String xwAndroidAlias=null;
//		//IOS 别名
//		String xwIosAlias=null;
//		//安卓自定义行为
//		String xwAndroidCustom=null;
//		//IOS 自定义行为
//		String xwIosCustom=null;
//		if(StringUtils.isNotBlank(appInfoId)){
//			Umeng umeng=getUmengList(appInfoId);
//			xwAndroidAppkey=umeng.getAndroidAppkey();
//			xwAndroidSercet=umeng.getAndroidAppmastersecret();
//			xwIosAppkey=umeng.getIosAppkey();
//			xwIosSercet=umeng.getIosAppmastersecret();
//			xwAndroidPath=umeng.getAndroidPath();
//			xwIosPath=umeng.getIosPath();
//			xwAndroidAlias=umeng.getAndroidAlias();
//			xwIosAlias=umeng.getIosAlias();
//			xwAndroidCustom=umeng.getAndroidCustom();
//			xwIosCustom=umeng.getIosCustom();
//		}
//		/**
//		 * 安卓广播
//		 */
//		PushClient client = new PushClient();
//		AndroidBroadcast broadcast = new AndroidBroadcast(xwAndroidAppkey,xwAndroidSercet);
//		broadcast.setTicker( "Android broadcast ticker");
//		broadcast.setTitle(title);
//		broadcast.setText(content);
//		broadcast.goCustomAfterOpen(xwAndroidCustom);
//		broadcast.setDisplayType(AndroidNotification.DisplayType.NOTIFICATION);
//		//正式模式
////		broadcast.setProductionMode();
//		//测试模式
//		broadcast.setTestMode();
//		broadcast.setPredefinedKeyValue("mipush", true);
//		broadcast.setPredefinedKeyValue("mi_activity", xwAndroidPath);
//		//自定义参数
//		broadcast.setExtraField("path",xwPath);
//		//自定义行为
//		broadcast.setCustomField(xwPath);
//		boolean a=client.send(broadcast);
//		/**
//		 * ios广播
//		 */
//		PushClient iosClient = new PushClient();
//		org.json.JSONObject isoJson=new org.json.JSONObject();
//		isoJson.put("title",title);
//		isoJson.put("text",content);
//		isoJson.put("url",url);
////		isoJson.put("path",xsPath);
//		IOSBroadcast iosBroadcast = new IOSBroadcast(xwIosAppkey,xwIosSercet);
//		iosBroadcast.setAlert(isoJson);
//		iosBroadcast.setBadge( 0);
//		iosBroadcast.setDescription(content);
//		iosBroadcast.setSound( "default");
//		// 测试模式
//		iosBroadcast.setTestMode();
//		// 正式模式
////		iosBroadcast.setProductionMode();
//		iosBroadcast.setPredefinedKeyValue("mipush", true);
//		iosBroadcast.setPredefinedKeyValue("mi_activity", xwIosPath);
//		//自定义行为
//		iosBroadcast.setCustomizedField("path", xwPath);
//		iosClient.send(iosBroadcast);
//		if(true==a){
//			json.put("title",title);
//			json.put("content",content);
//			json.put("url",url);
//			json.put("path",xwPath);
//			json.put("id",id);
//			json.put("code","200");
//			json.put("msg","已发送广播");
//		}
//		if(false==a){
//			json.put("code","400");
//			json.put("msg","false");
//		}
//		renderJson(json.toString());
//	}



	/**
	 * 掌上融媒推送
	 */
	public void pushAppMessage(){
		String fromUserId=getPara("fromUserId");//发送者的id
		String toUserName=getPara("toUserName");//接受者的名称
		String toUserId=getPara("toUserId");//接受者的id
		String fromUserName=getPara("fromUserName");//发送者的名称
		String typeu=getPara("typeu");
		String message=getPara("message");
		String fingerPrint=getPara("fingerPrint",genFingerPrint());
		String tId=getPara("tId");//通知 任务 会议 审稿的id
		String libraryId=getPara("libraryId");//采编
		String roomId=getPara("roomId");//视频通话房间id
//		 String ticker=getPara("ticker");///通知栏提示文字
//		 String icon=getPara("icon");////状态栏图标ID, R.drawable.[smallIcon],如果没有, 默认使用应用图标。
//		 String largeIcon=getPara("largeIcon");///
//		 String img=getPara("img");///通知栏大图标的URL链接。该字段的优先级大于largeIcon。该字段要求以http或者https开头。
//		 String play_vibrate=getPara("play_vibrate");////收到通知是否震动,默认为"true"
//		 String play_lights=getPara("play_lights");//收到通知是否闪灯,默认为"true"
//		 String play_sound=getPara("play_sound");///收到通知是否发出声音,默认为"true"
//		 String sound=getPara("sound");//通知声音，R.raw.[sound]. 如果该字段为空，采用SDK默认的声音
		
		try {
			sendUnicast2( fingerPrint,fromUserId, toUserId, fromUserName, toUserName, typeu, message,tId,libraryId,roomId);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	

	/**
	 * @discribtion 自定义单播服务API
	 * @author gongxiag.pang
	 * @throws Exception 
	 * @time  20190904
	 * @vertion v 1.0.01
	 * 
	 */
	public void sendUnicast	(String fingerPrint,String  fromUserId,String toUserId,String fromUserName,String toUserName,String typeu,String message)throws Exception{
		  PushClient client = new PushClient();
		JSONObject result=new JSONObject();
		 String uid=toUserId;//用户id 
		 String title=fromUserName;//通知标题
		 String text=message;//通知文字描述
		 String path="com.zkwg.rm.ui.SDKWebViewActivity";//
		
		
		 if(typeu.equals("11")||typeu.equals("21")){
			 text=text;//正常消息
		 }else if(typeu.equals("12") ||typeu.equals("22") ){
			 text="[图片]";
		 }else if(typeu.equals("13") ||typeu.equals("23") ){
			 text="[语音]";
		 }else if(typeu.equals("14") ||typeu.equals("24") ){
			 text="[视频]";
		 }else if(typeu.equals("15") ||typeu.equals("25") ){
			 text="[文件]";
		 }else if(typeu.equals("16") ||typeu.equals("26") ){
			 text="语音通话";
		 }else if(typeu.equals("17") ||typeu.equals("27")){
			 text="视频通话";
		 }else if(typeu.equals("41") ||typeu.equals("51")||typeu.equals("61")){//tuiso
			 text=text;//会议，通知，任务
		 }
		 
		 System.out.println("消息推送："+fromUserName+"发送给"+toUserId+",指纹码："+fingerPrint+",typeu:"+typeu);
		 
		//构造安卓自定义推送
		 AndroidCustomizedcast customizedcast = new AndroidCustomizedcast(android_appkey,android_appMasterSecret);
		// TODO Set your alias here, and use comma to split them if there are multiple alias.
		// And if you have many alias, you can also upload a file containing these alias, then
		// use file_id to send customized notification.
		customizedcast.setAlias(uid, "1");//接受者id
		customizedcast.setTitle(fromUserName);//发送者名称
		customizedcast.setText( text);
		customizedcast.goAppAfterOpen();
		customizedcast.setPredefinedKeyValue("mipush", "true");
		customizedcast.setPredefinedKeyValue("mi_activity", path);
		customizedcast.setDisplayType(AndroidNotification.DisplayType.NOTIFICATION);
		// TODO Set 'production_mode' to 'false' if it's a test device.
		// For how to register a test device, please see the developer doc.
		customizedcast.setProductionMode();
		//com.zkwg.rm.ui.SDKWebViewActivity
		//com.zkwg.rm.ui.ChatActivity
		//customizedcast.setMi_activity("com.zkwg.rm.ui.SDKWebViewActivity");
		//customizedcast.setMipush(true);
		client.send(customizedcast);

		org.json.JSONObject json_alert=new org.json.JSONObject();
		 json_alert.put("title", title);
		 json_alert.put("body", text);
		//构造苹果自定义推送
		IOSCustomizedcast ios_customizedcast = new IOSCustomizedcast(ios_appkey,ios_appMasterSecret);
		// TODO Set your alias and alias_type here, and use comma to split them if there are multiple alias.
		// And if you have many alias, you can also upload a file containing these alias, then
		// use file_id to send customized notification.
		ios_customizedcast.setAlias(uid, "ZSRM");
		ios_customizedcast.setAlert(json_alert);
		ios_customizedcast.setBadge(0);
		ios_customizedcast.setSound("default");
		ios_customizedcast.setProductionMode();
		ios_customizedcast.setPredefinedKeyValue("mipush", "true");
		ios_customizedcast.setPredefinedKeyValue("mi_activity", path);
		// TODO set 'production_mode' to 'true' if your app is under production mode

		System.out.println("发送推送："+client.send(ios_customizedcast));
		result.put("code", "200");
		result.put("msg", "发送成功");
		
//		renderText(result.toString());
	}

	public void sendUnicast2 (String fingerPrint,String  fromUserId,String toUserId,String fromUserName,String toUserName,String typeu,String message,String tId,String libraryId,String roomId)throws Exception{
		JSONObject result=new JSONObject();
		String uid=toUserId;//用户id
		String title=fromUserName;//通知标题
		String text=message;//通知文字描述
		String path="com.zkwg.rm";//包路径

		if(typeu.equals("11")||typeu.equals("21")){
			text=text;//正常消息
		}else if(typeu.equals("12") ||typeu.equals("22") ){
			text="[图片]";
		}else if(typeu.equals("13") ||typeu.equals("23") ){
			text="[语音]";
		}else if(typeu.equals("14") ||typeu.equals("24") ){
			text="[视频]";
		}else if(typeu.equals("15") ||typeu.equals("25") ){
			text="[文件]";
		}else if(typeu.equals("16") ||typeu.equals("26") ){
			text="语音通话";
		}else if(typeu.equals("17") ||typeu.equals("27")){
			text="视频通话";
		}else if(typeu.equals("41") ){//tuiso
			text=text;//通知
		}else if(typeu.equals("51") ){//tuiso
			text=text;//会议
		}else if(typeu.equals("61")){//tuiso
			text=text;//任务
		}else if(Integer.parseInt(typeu)>=70 && Integer.parseInt(typeu)<=80){//，审稿
			text=text;
			if(StringUtils.isNotBlank(toUserName)){
				String sql="select * from manage_user_info where user_name = '"+toUserName+"' ";
				ManageUserInfo info=ManageUserInfo.dao.findFirst(sql);
				if(info!=null){
					uid=info.getUserId().toString();
				}if(info==null){
					result.put("status",-1);
					result.put("message","推送失败");
					renderJson(result);
				}
			}else{
				result.put("status",-1);
				result.put("message","推送失败");
				renderJson(result);
			}
		}
		if(StringUtils.isNotBlank(uid)){
			System.out.println("消息推送："+fromUserName+"发送给"+toUserId+",指纹码："+fingerPrint+",typeu:"+typeu);
			//个推推送
			String url="http://hongqi.wengetech.com:9505/push/sendUnicast";
			JSONObject jsonObject=new JSONObject();
			jsonObject.put("appKey",android_appkey2);
			jsonObject.put("masterSecret",android_appMasterSecret2);
			jsonObject.put("appId","6cs2dlC9Dx9nykilT8Xb92");
			jsonObject.put("uid",uid);
			jsonObject.put("title",fromUserName);
			jsonObject.put("text",text);
			jsonObject.put("path",path);
			jsonObject.put("typeu",typeu);
			jsonObject.put("typeId",tId);
			jsonObject.put("roomId",roomId);
			jsonObject.put("fromUserId",fromUserId);
			jsonObject.put("libraryId",libraryId);
			String a=UmPush.post(jsonObject,url);
			System.out.println("个推a"+a);
			JSONObject jsonObject1=JSONObject.parseObject(a);
			JSONArray jsonArray = jsonObject1.getJSONArray("data");
			String isPush=null;
			JSONObject isPushJson=null;
			for(int i=0;i<jsonArray.size();i++){
				JSONObject jsonObject2=jsonArray.getJSONObject(i);
				Object as=jsonObject2.get("response");
				isPushJson=JSONObject.parseObject(as.toString());
				isPush= (String) isPushJson.get("result");
			}
			if("ok".equals(isPush)){
				result.put("status", 1);
				result.put("message", "推送成功");
				result.put("data", isPushJson);
			}else{
				result.put("status", -1);
				result.put("message", "推送失败");
				result.put("data", isPushJson);
			}

			renderJson(result);
//		renderText(jsonObject1.toString());
		}

	}
	public static String post(JSONObject json, String url){
		String result = "";
		HttpPost post = new HttpPost(url);
		try{
			CloseableHttpClient httpClient = HttpClients.createDefault();

			post.setHeader("Content-Type","application/json;charset=utf-8");
			post.addHeader("Authorization", "Basic YWRtaW46");
			StringEntity postingString = new StringEntity(json.toString(),"utf-8");
			post.setEntity(postingString);
			HttpResponse response = httpClient.execute(post);

			InputStream in = response.getEntity().getContent();
			BufferedReader br = new BufferedReader(new InputStreamReader(in, "utf-8"));
			StringBuilder strber= new StringBuilder();
			String line = null;
			while((line = br.readLine())!=null){
				strber.append(line+'\n');
			}
			br.close();
			in.close();
			result = strber.toString();
			if(response.getStatusLine().getStatusCode()!= HttpStatus.SC_OK){
				result = "服务器异常";
			}
		} catch (Exception e){
			System.out.println("请求异常");
			throw new RuntimeException(e);
		} finally{
			post.abort();
		}
		return result;
	}

	
	
	/**
	 * 安卓--广播(broadcast)：向安装该App的所有设备发送消息。
	 * @throws Exception
	 */
	public void sendAndroidBroadcast() throws Exception {
		  PushClient client = new PushClient();
		AndroidBroadcast broadcast = new AndroidBroadcast(android_appkey,android_appMasterSecret);
		broadcast.setTicker( "Android broadcast ticker");
		broadcast.setTitle(  "中文的title");
		broadcast.setText(   "Android broadcast text");
		broadcast.goAppAfterOpen();
		broadcast.setDisplayType(AndroidNotification.DisplayType.NOTIFICATION);
		// TODO Set 'production_mode' to 'false' if it's a test device. 
		// For how to register a test device, please see the developer doc.
		broadcast.setProductionMode();
		// Set customized fields
		broadcast.setExtraField("test", "helloworld");//mi_activity
		client.send(broadcast);
		renderText("已发送广播");
	}

	/**
	 * 安卓--单播(unicast)：向指定的设备发送消息。
	 * @throws Exception
	 */
	public void sendAndroidUnicast() throws Exception {
		  PushClient client = new PushClient();
		AndroidUnicast unicast = new AndroidUnicast(android_appkey,android_appMasterSecret);
		// TODO Set your device token
		unicast.setDeviceToken( "your device token");
		unicast.setTicker( "Android unicast ticker");
		unicast.setTitle(  "中文的title");
		unicast.setText(   "Android unicast text");
		unicast.goAppAfterOpen();
		unicast.setDisplayType(AndroidNotification.DisplayType.NOTIFICATION);
		// TODO Set 'production_mode' to 'false' if it's a test device. 
		// For how to register a test device, please see the developer doc.
		unicast.setProductionMode();
		// Set customized fields
		unicast.setExtraField("test", "helloworld");
		client.send(unicast);
	}
	
	/**
	 * 安卓--自定义播(customizedcast)：开发者通过自有的alias进行推送，可以针对单个或者一批alias进行推送，也可以将alias存放到文件进行发送。
	 * @throws Exception
	 */
	public void sendAndroidCustomizedcast() throws Exception {
		  PushClient client = new PushClient();
		String uid=getPara("uid","2969");
		String title=getPara("title","这是一个标题");
		String text=getPara("text","测试内容");
		AndroidCustomizedcast customizedcast = new AndroidCustomizedcast(android_appkey,android_appMasterSecret);
		// TODO Set your alias here, and use comma to split them if there are multiple alias.
		// And if you have many alias, you can also upload a file containing these alias, then 
		// use file_id to send customized notification.
		customizedcast.setAlias(uid, "1");
		customizedcast.setTicker( "Android customizedcast ticker");
		customizedcast.setTitle(  "中文的title");
		customizedcast.setText(   "Android customizedcast text");
		customizedcast.goAppAfterOpen();
		
		customizedcast.setDisplayType(AndroidNotification.DisplayType.NOTIFICATION);
		// TODO Set 'production_mode' to 'false' if it's a test device. 
		// For how to register a test device, please see the developer doc.
		customizedcast.setProductionMode();
		client.send(customizedcast);
		renderText("已发送个人推送");
	}
	/**
	 * 安卓--组播(groupcast):向满足特定条件的设备集合发送消息，例如: “特定版本”、”特定地域”等。
	 * @throws Exception
	 */
	public void sendAndroidGroupcast() throws Exception {
//		  PushClient client = new PushClient();
//		AndroidGroupcast groupcast = new AndroidGroupcast(android_appkey,android_appMasterSecret);
//		/*  TODO
//		 *  Construct the filter condition:
//		 *  "where":
//		 *	{
//    	 *		"and":
//    	 *		[
//      	 *			{"tag":"test"},
//      	 *			{"tag":"Test"}
//    	 *		]
//		 *	}
//		 */
//		JSONObject filterJson = new JSONObject();
//		JSONObject whereJson = new JSONObject();
//		JSONArray tagArray = new JSONArray();
//		JSONObject testTag = new JSONObject();
//		JSONObject TestTag = new JSONObject();
//		testTag.put("tag", "test");
//		TestTag.put("tag", "Test");
//		tagArray.put(testTag);
//		tagArray.put(TestTag);
//		whereJson.put("and", tagArray);
//		filterJson.put("where", whereJson);
//		System.out.println(filterJson.toString());
//
//		groupcast.setFilter(filterJson);
//		groupcast.setTicker( "Android groupcast ticker");
//		groupcast.setTitle(  "中文的title");
//		groupcast.setText(   "Android groupcast text");
//		groupcast.goAppAfterOpen();
//		groupcast.setDisplayType(AndroidNotification.DisplayType.NOTIFICATION);
//		// TODO Set 'production_mode' to 'false' if it's a test device.
//		// For how to register a test device, please see the developer doc.
//		groupcast.setProductionMode();
//		client.send(groupcast);
	}
	
	public void sendAndroidCustomizedcastFile() throws Exception {
		  PushClient client = new PushClient();
		AndroidCustomizedcast customizedcast = new AndroidCustomizedcast(android_appkey,android_appMasterSecret);
		// TODO Set your alias here, and use comma to split them if there are multiple alias.
		// And if you have many alias, you can also upload a file containing these alias, then 
		// use file_id to send customized notification.
		String fileId = client.uploadContents(android_appkey,android_appMasterSecret,"aa"+"\n"+"bb"+"\n"+"alias");
		customizedcast.setFileId(fileId, "alias_type");
		customizedcast.setTicker( "Android customizedcast ticker");
		customizedcast.setTitle(  "中文的title");
		customizedcast.setText(   "Android customizedcast text");
		customizedcast.goAppAfterOpen();
		customizedcast.setDisplayType(AndroidNotification.DisplayType.NOTIFICATION);
		// TODO Set 'production_mode' to 'false' if it's a test device. 
		// For how to register a test device, please see the developer doc.
		customizedcast.setProductionMode();
		client.send(customizedcast);
	}
	
	public void sendAndroidFilecast() throws Exception {
		  PushClient client = new PushClient();
		AndroidFilecast filecast = new AndroidFilecast(android_appkey,android_appMasterSecret);
		// TODO upload your device tokens, and use '\n' to split them if there are multiple tokens 
		String fileId = client.uploadContents(android_appkey,android_appMasterSecret,"aa"+"\n"+"bb");
		filecast.setFileId( fileId);
		filecast.setTicker( "Android filecast ticker");
		filecast.setTitle(  "中文的title");
		filecast.setText(   "Android filecast text");
		filecast.goAppAfterOpen();
		filecast.setDisplayType(AndroidNotification.DisplayType.NOTIFICATION);
		client.send(filecast);
	}
	
	/**
	 * 苹果-广播
	 * @throws Exception
	 */
	
	public void sendIOSBroadcast() throws Exception {
		  PushClient client = new PushClient();
		IOSBroadcast broadcast = new IOSBroadcast(ios_appkey,ios_appMasterSecret);

		broadcast.setAlert("IOS 广播测试");
		broadcast.setBadge( 0);
		broadcast.setSound( "default");
		// TODO set 'production_mode' to 'true' if your app is under production mode
		broadcast.setTestMode();
		// Set customized fields
		broadcast.setCustomizedField("test", "helloworld");
		client.send(broadcast);
		renderText("广播发送成功");
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	public void sendIOSCustomizedcast() throws Exception {
//		  PushClient client = new PushClient();
//		 String uid=getPara("uid");//用户id
//		 String title=getPara("title");//通知标题
//		 String text=getPara("text");//通知文字描述
//		 JSONObject json=new JSONObject();
//		 json.put("title", title);
//		 json.put("body", text);
//
//		IOSCustomizedcast customizedcast = new IOSCustomizedcast(ios_appkey,ios_appMasterSecret);
//		// TODO Set your alias and alias_type here, and use comma to split them if there are multiple alias.
//		// And if you have many alias, you can also upload a file containing these alias, then
//		// use file_id to send customized notification.
//		customizedcast.setAlias(uid, "ZSRM");
//		customizedcast.setAlert(json);
//		customizedcast.setBadge( 0);
//		customizedcast.setSound( "default");
//		customizedcast.setProductionMode();
//		// TODO set 'production_mode' to 'true' if your app is under production mode
//		client.send(customizedcast);
//		System.out.println("推出发送成功");
//		//renderText("推出发送成功");
		
	}
	
	
	/**
	 * 苹果-单播
	 * @throws Exception
	 */
	public void sendIOSUnicast() throws Exception {
		  PushClient client = new PushClient();
		IOSUnicast unicast = new IOSUnicast(ios_appkey,ios_appMasterSecret);
		// TODO Set your device token
		unicast.setDeviceToken( "xx");
		unicast.setAlert("IOS 单播测试");
		unicast.setBadge( 0);
		unicast.setSound( "default");
		// TODO set 'production_mode' to 'true' if your app is under production mode
		unicast.setTestMode();
		// Set customized fields
		unicast.setCustomizedField("test", "helloworld");
		client.send(unicast);
	}
	

	
	/**
	 * 苹果-组播
	 * @throws Exception
	 */
	public void sendIOSGroupcast() throws Exception {
//		  PushClient client = new PushClient();
//		IOSGroupcast groupcast = new IOSGroupcast(ios_appkey,ios_appMasterSecret);
//		/*  TODO
//		 *  Construct the filter condition:
//		 *  "where":
//		 *	{
//    	 *		"and":
//    	 *		[
//      	 *			{"tag":"iostest"}
//    	 *		]
//		 *	}
//		 */
//		JSONObject filterJson = new JSONObject();
//		JSONObject whereJson = new JSONObject();
//		JSONArray tagArray = new JSONArray();
//		JSONObject testTag = new JSONObject();
//		testTag.put("tag", "iostest");
//		tagArray.put(testTag);
//		whereJson.put("and", tagArray);
//		filterJson.put("where", whereJson);
//		System.out.println(filterJson.toString());
//
//		// Set filter condition into rootJson
//		groupcast.setFilter(filterJson);
//		groupcast.setAlert("IOS 组播测试");
//		groupcast.setBadge( 0);
//		groupcast.setSound( "default");
//		// TODO set 'production_mode' to 'true' if your app is under production mode
//		groupcast.setTestMode();
//		client.send(groupcast);
	}
	
	public void sendIOSFilecast() throws Exception {
		  PushClient client = new PushClient();
		IOSFilecast filecast = new IOSFilecast(ios_appkey,ios_appMasterSecret);
		// TODO upload your device tokens, and use '\n' to split them if there are multiple tokens 
		String fileId = client.uploadContents(ios_appkey,ios_appMasterSecret,"aa"+"\n"+"bb");
		filecast.setFileId( fileId);
		filecast.setAlert("IOS 文件播测试");
		filecast.setBadge( 0);
		filecast.setSound( "default");
		// TODO set 'production_mode' to 'true' if your app is under production mode
		filecast.setTestMode();
		client.send(filecast);
	}
	
	public static String genFingerPrint()
	{
		return UUID.randomUUID().toString();//System.currentTimeMillis();
	}
	
/*	public UmPush(String key, String secret) {
		try {
			ios_appkey = key;
			ios_appMasterSecret = secret;
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}*/
	/**
	 * 测试入口
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO set your appkey and master secret here
//	UmPush demo = new UmPush(ios_appkey,ios_appMasterSecret);
		try {
			//demo.sendIOSCustomizedcast();
			
			/* TODO these methods are all available, just fill in some fields and do the test
			 * demo.sendAndroidCustomizedcastFile();
			 * demo.sendAndroidBroadcast();
			 * demo.sendAndroidGroupcast();
			 * demo.sendAndroidCustomizedcast();
			 * demo.sendAndroidFilecast();
			 * 
			 * demo.sendIOSBroadcast();
			 * demo.sendIOSUnicast();
			 * demo.sendIOSGroupcast();
			 * demo.sendIOSCustomizedcast();
			 * demo.sendIOSFilecast();
			 */
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	
	private Umeng getUmengList(String appInfoId){
		String sql ="select * from umeng where app_info_id = '"+appInfoId+"'";
		Umeng um=Umeng.dao.findFirst(sql);
		return um;
	}
	
	
}
