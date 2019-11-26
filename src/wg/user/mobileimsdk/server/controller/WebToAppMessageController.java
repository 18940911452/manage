package wg.user.mobileimsdk.server.controller;

import java.io.IOException;
import java.net.SocketAddress;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jfinal.core.Controller;
import io.netty.channel.Channel;
import wg.media.screen.fm.model.commandscreencommon.ManageUserInfo;
import wg.openmob.mobileimsdk.server.listener.ServerEventListenerImpl;
import wg.openmob.mobileimsdk.server.processor.LogicProcessor;
import wg.openmob.mobileimsdk.server.processor.OnlineProcessor;
import wg.openmob.mobileimsdk.server.protocal.Protocal;
import wg.openmob.mobileimsdk.server.utils.GlobalSendHelper;
import wg.user.mobileimsdk.server.service.MessageService;
import wg.user.mobileimsdk.server.util.DesUtil;
/**
 * 网页端消息转发到app 中转站
 * @author DELL
 *
 */
public class WebToAppMessageController extends Controller{
	
	private static Logger logger = LoggerFactory.getLogger(OnlineProcessor.class); 
	
	/**
	 * 
	 * 名称：webToAppMessage 
	 * 描述：网页端消息发送到客户端
	 * @author gongxiang.pang
	 * @parameter
	 * @return
	 */
	public void webToAppMessage(){
		//int type, String dataContent, String from, String to, boolean QoS, String fingerPrint, int typeu
		String message=getPara("message");
		String str= "";
		try {
			 str=new String(message.getBytes(),"UTF-8");
			System.out.println(str);
			System.out.println(DesUtil.decrypt(str, "zhongkewenge2018"));
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String fromUserId=getPara("fromUserId");
		String toUserId=getPara("toUserId");
		Integer typeu=getParaToInt("typeu");
		String groupId = getPara("groupId");
		String fingerPrint = getPara("fingerPrint");
		if (typeu/10 == 1) {
			Protocal Protocal=new Protocal(2, message, fromUserId, toUserId, true, fromUserId + fingerPrint + "000", typeu);
			Set<Channel> set = ServerEventListenerImpl.ser.rooms.get(toUserId);
			if(set!=null){
				System.out.println("set不为null----------------------------set个数：" + set.size());
				for (Channel s : set) {
						System.out.println("isOpen--------------" + s);
						String remoteAddress = clientInfoToString(s,fromUserId);
						try {
							GlobalSendHelper.ser.sendDataC2C(null, s, Protocal, remoteAddress, LogicProcessor.serverCoreHandler);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
				}
			}else {
				String sql="select nick_name,user_name from manage_user_info where user_id=?";
				 ManageUserInfo manageUserInfo=ManageUserInfo.dao.findFirst(sql,new Object[]{fromUserId});
				 String fromName="";
				 if(manageUserInfo!=null){
					 fromName=manageUserInfo.get("nick_name");
					 if(fromName.equals(null)||fromName.isEmpty()){
						 fromName=manageUserInfo.get("user_name");
					 }
				 }
				 try {
					 System.out.println(fromName);
					 //UmPush.push.sendUnicast2( fingerPrint,fromUserId, toUserId, fromName, "", typeu.toString(), DesUtil.decrypt(str, "zhongkewenge2018"),null,null,null);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println("set为null----------------------------" + toUserId);
			}
		}else {
			String [] toUserIds = toUserId.split(",");
			for (String userId : toUserIds) {
				Protocal Protocal=new Protocal(2, message, fromUserId+"#"+groupId, userId, true,  fromUserId + fingerPrint + "000#" + userId, typeu);
				Set<Channel> set = ServerEventListenerImpl.ser.rooms.get(userId);
				if(set!=null){
					System.out.println("set不为null----------------------------set个数：" + set.size());
					for (Channel s : set) {
							String remoteAddress = clientInfoToString(s,fromUserId);
							try {
								GlobalSendHelper.ser.sendDataC2C(null, s, Protocal, remoteAddress, LogicProcessor.serverCoreHandler);
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
					}
				}else {
					
					String sql="select nick_name,user_name from manage_user_info where user_id=?";
					 ManageUserInfo manageUserInfo=ManageUserInfo.dao.findFirst(sql,new Object[]{fromUserId});
					 String fromName="";
					 if(manageUserInfo!=null){
						 fromName=manageUserInfo.get("nick_name");
						 if(fromName.equals(null)||fromName.isEmpty()){
							 fromName=manageUserInfo.get("user_name");
						 }
					 }
					 try {
						 System.out.println(fromName);
						// UmPush.push.sendUnicast2( fingerPrint,fromUserId, userId, fromName, "", typeu.toString(), DesUtil.decrypt(str, "zhongkewenge2018"),null,null,null);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					System.out.println("set为null----------------------------" + toUserId);
				}
			}
		}
	/*	try {
			Protocal Protocal=new Protocal(2, message, fromUserId, toUserId, true, uuid.toString(), typeu);
			Set<Channel> set = ServerEventListenerImpl.ser.rooms.get(toUserId);
			if(set!=null){
				for (Channel s : set) {
					if(s.isOpen()){
						String remoteAddress = clientInfoToString(s,fromUserId);
						GlobalSendHelper.ser.sendDataC2C(null, s, Protocal, remoteAddress, LogicProcessor.serverCoreHandler);
					}
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		renderText("ok");
	}
	
	/**
	 * 
	 * 名称：webToAppMessage 
	 * 描述：网页端消息发送到客户端
	 * @author gongxiang.pang
	 * @parameter
	 * @return
	 * 	/**
		 * 		 10 单聊的阅后即焚
		 * 		 11	表示文本(单聊) 发送者uid 接收者uid 文本内容
		//		 12	表示图片(单聊) 发送者uid 接收者uid 缩略图url和原始图url
		//		 13	表示语音(单聊) 发送者uid 接收者uid 语音url 语音时长 +语音大小
		//		 14	表示视频(单聊) 发送者uid 接收者uid 视频截图url 视频url +视频大小
		//		 15	表示文件(单聊) 发送者uid 接收者uid 文件url 文件大小
				
				 20阅后即焚
		//		 21	表示文本(群聊)
		//		 22	表示图片(群聊)
		//		 23	表示语音(群聊)
		//		 24	表示视频(群聊)
		//		 25	表示文件(群聊)
		//		 31	行动预案消息提醒
		//		 32	事件上报提醒
		 		* 50单聊回执
		 		* 51群聊回执
		//		 101	通知
		 * 	 	 102	会议
		 * 	     103	任务
	 * http://hongqitest.wengetech.com:9007/commandSys-Im/webToApp/mettingToAppMessage?fromUserId=2351&toUserId=2350&fromUserName=1111&toUserName=222&typeu=41&message=666666666666666666&fingerPrint=123123
	 */
	public void mettingToAppMessage(){
		//int type, String dataContent, String from, String to, boolean QoS, String fingerPrint, int typeu
		
		String fromUserId=getPara("fromUserId","0");//发送者的id
		String toUserId=getPara("toUserId");//接受者的id
		String fromUserName=getPara("fromUserName");//发送者的名称
		String toUserName=getPara("toUserName");//接受者的名称
		Integer typeu=getParaToInt("typeu");//通知101   会议102   任务103   审稿104
		String message=getPara("message");
		String fingerPrint=fromUserId+new Date().getTime()+"000" ;
		try {
			message=new String(message.getBytes(),"UTF-8");
			System.out.println(message);
			message=DesUtil.encrypt(message, "zhongkewenge2018");
			System.out.println(message);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
			Protocal Protocal=new Protocal(2, message, fromUserId, toUserId, true, fingerPrint, typeu);
			
			if(OnlineProcessor.onlineSessions.containsKey(toUserId))
			{
				
				Channel session=OnlineProcessor.onlineSessions.get(toUserId);
				// TODO 同一账号的重复登陆情况可在此展开处理逻辑
				try {
					GlobalSendHelper.ser.sendDataC2C(null, session, Protocal, null, LogicProcessor.serverCoreHandler);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else{
				//如果用户不在线就报存离线消息
				MessageService.ser.saveMessage(fingerPrint,fromUserId, toUserId, message, typeu, 1, 1);
			}
			
		
		renderText("ok");
	}
	
	public static String genFingerPrint()
	{
		return UUID.randomUUID().toString();//System.currentTimeMillis();
	}
	
	/**
	 * @param session 
	 * @param from
	 * @return
	 */
	public static String clientInfoToString(Channel session,String from){
		SocketAddress remoteAddress = session.remoteAddress();
		String s1 = remoteAddress.toString();
		StringBuilder sb = new StringBuilder()
		.append("{uid:")
		.append(from)
		.append("}")
		.append(s1);
		return sb.toString();
	}
	
}
