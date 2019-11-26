package wg.user.mobileimsdk.server.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.eclipse.jetty.util.TypeUtil;
import org.eclipse.jetty.util.log.Log;

import com.alibaba.fastjson.JSONObject;

import wg.media.screen.fm.model.commandscreencommon.HistoryDialogue;
import wg.media.screen.fm.model.commandscreencommon.ImHistoryMessage;
import wg.media.screen.fm.model.commandscreencommon.ManageGroup;
import wg.media.screen.fm.model.commandscreencommon.ManageUserInfo;
import wg.user.mobileimsdk.server.controller.UmPush;
import wg.user.mobileimsdk.server.util.DesUtil;

public class MessageService {
	public static MessageService ser=new MessageService();
	
	static int maxSize = 200;
	static Map<String, Integer> messageMap = new LinkedHashMap<String, Integer>(){
	      private static final long    serialVersionUID    = 1L;
	      @Override
	      protected boolean removeEldestEntry(java.util.Map.Entry<String, Integer> pEldest) {
	          return size() > maxSize;
	      }
	  };
	  
	
	 
	 /**
	  * 
	  * @method:saveMassage 
	  * @describe:保存离线消息
	  * @author:  gongxiangPang 
	  * @param 	fingerPrint 消息指纹避免重复，来源用户id   from_user_id ,发送给用户id  to_user_id,内容 content,发送类型    utype ,发送时间 send_time,聊天类型：1单聊 ， 2群聊   ctype,   1离线消息，0历史消息：sendStatus
	  * @return
	  * 根据指纹码 
	  */
	 public int saveMessage(String fingerPrint,String from_user_id ,String to_user_id,String content,Integer typeu ,Integer ctype,Integer sendStatus){
		 int result=0;
		 if(messageMap.containsKey(fingerPrint)  ){/*
			 //
			 Integer num=messageMap.get(fingerPrint);
			 if((num.equals(1) && sendStatus.equals(1))){
				 messageMap.put(fingerPrint, 2);
			}
			 if(num.equals(0)){
				 return 0;
			 }
			if(num.equals(1) && sendStatus.equals(0)){
				messageMap.put(fingerPrint, sendStatus);//第一遍失败第二遍成功，更新数据
				 String sql="select id,send_num from im_history_message where fingerPrint='"+fingerPrint+"'";
				System.err.println("save+"+sql);
				 ImHistoryMessage findFirst =  ImHistoryMessage.dao.findFirst(sql);
				 if(findFirst==null){
					 System.out.println("1111111:"+findFirst);
				 }else{
					 System.out.println("2222:"+findFirst);
				 }
			
				 
				 ImHistoryMessage message=findFirst;
				 Integer id=findFirst.getId();
				 message.set("id", id);
				 message.set("fingerPrint", fingerPrint);
				 message.set("send_status", sendStatus);
				 if(message.update()){
					 return 0;
				 }else{
					 return -1;
				 }
			}
			
			if((num.equals(2) && sendStatus.equals(1))){
				 if(messageMap.containsKey(fingerPrint)) {
					  if(messageMap.get(fingerPrint)==4) {
						  return -1;
					  }
				  }
				 messageMap.put(fingerPrint, 4);
				 System.out.println(fingerPrint+"离线处理+++++++++++++++消息推送");
				 String decmessage="";
				 try {
					 decmessage=DesUtil.decrypt(content, "zhongkewenge2018");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//存储消息
				if(ctype.equals(1)){//聊天类型：1单聊 ， 2群聊  
					String sql="select nick_name,user_name from manage_user_info where user_id=?";
					 ManageUserInfo manageUserInfo=ManageUserInfo.dao.findFirst(sql,new Object[]{from_user_id});
					 String fromName="";
					 if(manageUserInfo!=null){
						 fromName=manageUserInfo.get("nick_name");
						 if(fromName.equals(null)||fromName.isEmpty()){
							 fromName=manageUserInfo.get("user_name");
						 }
					 }
					 JiGuangPush.Jpush.JpushSendMessage( fingerPrint,from_user_id, to_user_id, fromName, "", typeu.toString(), decmessage);
					 
				}else if(ctype.equals(2)&&typeu/10==2){
					 String groupid = from_user_id.split("#")[1].toString();
					 String sql="SELECT group_name from manage_group where group_id=?";
					 ManageGroup manageGroup=ManageGroup.dao.findFirst(sql,new Object[]{groupid});
					 String fromName="";
					 if(manageGroup!=null){
						 fromName=manageGroup.get("group_name");
					 }
					 System.out.println(fingerPrint+"----------"+to_user_id+"");
					 JiGuangPush.Jpush.JpushSendMessage( fingerPrint,from_user_id, to_user_id, fromName, "", typeu.toString(), decmessage);
				}
				 messageMap.put(fingerPrint, 3);//超过三次舍弃 
			}
		 */}else{
			 messageMap.put(fingerPrint, sendStatus);
			 //不包含就保存
			 ImHistoryMessage im=new ImHistoryMessage();
			 im.setFromUserId(from_user_id);
			 im.setFingerPrint(fingerPrint);
			 im.setToUserId(to_user_id);
//			 try {
//				 //解密
//				 //content=DesUtil.decrypt(content, "zhongkewenge2018");
//				} catch (Exception e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
			 im.setContent(content);
			 im.setUtype(typeu);
			 im.setCType(ctype);
			 im.setSendStatus(sendStatus);//1离线消息， 0历史消息
			 im.setStatus(1);
			 im.setSendNum(1);
			 if(ctype == 1) {
				 System.out.println("截取前" + fingerPrint);
				 System.out.println("截取后" +fingerPrint.substring(fingerPrint.length()-16,fingerPrint.length()-3));
				 im.setSendTime(fingerPrint.substring(fingerPrint.length()-16,fingerPrint.length()-3));
			 }else {
				 System.out.println("截取前" +fingerPrint);
				 fingerPrint = fingerPrint.substring(0, fingerPrint.indexOf("#")-3);
				 System.out.println("截取后" +fingerPrint.substring(fingerPrint.length()-13));
				 im.setSendTime(fingerPrint.substring(fingerPrint.length()-13));
			 }
			try {
				 if(im.save()){
					 
					 return 0;
				 }else{
					 return -1;
				 }
			} catch (Exception e) {
				// TODO: handle exception
			}
		 }
		 return 0;
		
	 }
	 /**
	  * 
	  * @method:saveMassage 
	  * @describe:保存发送消息
	  * @author:  gongxiangPang 
	  * @param 	fingerPrint 消息指纹避免重复，来源用户id   from_user_id ,发送给用户id  to_user_id,内容 content,发送类型    utype ,发送时间 send_time,聊天类型：1单聊 ， 2群聊   ctype,   1离线消息，0历史消息：sendStatus
	  * @return
	  * 根据指纹码 
	  */
	 public int saveHisMessage(String fingerPrint,String from_user_id ,String to_user_id,String content,Integer utype ,Integer ctype,Integer sendStatus,Integer sourceType){
		 String sql="select id from im_history_message where fingerPrint='"+fingerPrint+"'";
		 ImHistoryMessage findFirst =  ImHistoryMessage.dao.findFirst(sql);
		 if(findFirst==null){
			 ImHistoryMessage im=new ImHistoryMessage();
			 im.setFromUserId(from_user_id);
			 im.setFingerPrint(fingerPrint);
			 im.setSourceType(sourceType);
			 im.setToUserId(to_user_id);
			 im.setContent(content);
			 im.setUtype(utype);
			 im.setCType(ctype);
			 im.setStatus(1);
			 im.setSendStatus(sendStatus);//1离线消息，0历史消息
			 if(ctype == 1) {
				 System.out.println("截取前" + fingerPrint);
				 System.out.println("截取后" +fingerPrint.substring(fingerPrint.length()-16,fingerPrint.length()-3));
				 im.setSendTime(fingerPrint.substring(fingerPrint.length()-16,fingerPrint.length()-3));
			 }else {
				 System.out.println("截取前" +fingerPrint);
				 fingerPrint = fingerPrint.substring(0, fingerPrint.indexOf("#")-3);
				 System.out.println("截取后" +fingerPrint.substring(fingerPrint.length()-13));
				 im.setSendTime(fingerPrint.substring(fingerPrint.length()-13));
			 }
			 if(im.save()){
				 return 0;
			 }else{
				 return -1;
			 }
			 
		 }else{
			 if(sendStatus.equals(1)){
				 ImHistoryMessage message=findFirst;
				 message.set("send_status", 1);
				 if(message.update()){
					 return 0;
				 }else{
					 return -1;
				 }
			 }
			 return -1;
		 }
	 }
	 /**
	  * 
	  * @return
	  */
	 @SuppressWarnings("unchecked")
	public JSONObject getOfflineMessage(String uid ){
		 JSONObject  jo=new JSONObject();
		 System.out.println("获取历史消息:"+uid);
		 List<ImHistoryMessage> list=new ArrayList<ImHistoryMessage>();
		 String sql="select to_user_id,content,id,send_time,from_user_id,fingerPrint,utype,c_type from im_history_message where  send_status=1 and to_user_id="+uid + " order by send_time desc";
		  List<ImHistoryMessage> find = ImHistoryMessage.dao.find(sql);
		  if(find!=null && find.size()>0){
			  for (int i = 0; i < find.size(); i++) {
				ImHistoryMessage im = find.get(i);                  
				list.add(im);
			}
			  jo.put("status", 1);
			  jo.put("message", "Have Data");
			  jo.put("data", list);
		  }else{
			  jo.put("status", 0);
			  jo.put("message", "No Offline Data");
			  jo.put("data", list);
		  }
		 return  jo;
	 }
	 
	 /**
	  * 离线消息接受后回调处理
	  * @param fingerPrint
	  * @return
	  */
	 public JSONObject  callBackOfflineMessage(String fingerPrint){
		 JSONObject re=new JSONObject();
		 String[] split = fingerPrint.split(",");
		 if(split!=null && split.length>0){
			 for (int i = 0; i < split.length; i++) {
				String finger=split[i].toString();
				 String sql="select * from im_history_message where  fingerPrint='"+finger+"'";
				 ImHistoryMessage findFirst = ImHistoryMessage.dao.findFirst(sql);
				if(findFirst!=null){
					findFirst.set("send_status", 0);
					findFirst.update();
				}
			}
		 }
		 re.put("status", 1);
		 re.put("message", "success");
		 return re;
	 }
	 
	 /**
	  * 
	  * @param uid
	  * @return
	  */
	 public List<HistoryDialogue>  getDialogueList(String uid){
		 String sql="select * from history_dialogue where  user_id ="+uid;
		 List<HistoryDialogue> find = HistoryDialogue.dao.find(sql);
		 if(find!=null&& find.size()>0){
			 return find;
		 }
		 return null;
	 }
	 
	 /**
	  * 
	  * @param uid
	  * @param dtype
	  * @param to_user_id
	  * @param group_id
	  * @return
	  */
	 public String addDialogue(String uid,String dtype,String to_user_id,String group_id){
		 HistoryDialogue historyDialogue = new HistoryDialogue(); 
		 if(uid==null){
			 return "false";
		 }
		 historyDialogue.setUserId(Integer.valueOf(uid));
		 historyDialogue.setDtype(Integer.valueOf(dtype));
		 if(to_user_id!=null){
			 historyDialogue.setToUserId(Integer.valueOf(to_user_id));
		 }
		 if(group_id!=null){
			 historyDialogue.setGroupId(Integer.valueOf(group_id));
		 }
		 if(historyDialogue.save()){
			 return "success";
		 }else{
			 return "false";
		 }
	 }
	 /**
	  * 消息标位已读
	  * @param ctype
	  * @param fromUserId
	  * @param toUserId
	  * @param groupId
	  * @param fingerPrint
	  * @return
	  */
	public static boolean setReadMessage(String ctype, String fromUserId, String toUserId, String groupId,
			String fingerPrint) {
		// TODO Auto-generated method stub
		if("1".equals(ctype)) {
			fingerPrint = fromUserId + fingerPrint ;
		}
		String sql = "select * from   im_history_message where fingerPrint like '%"+fingerPrint+"%' and send_status = 1 and to_user_id = " +toUserId + " order by send_time desc";
	     ImHistoryMessage imHistoryMessage = ImHistoryMessage.dao.findFirst(sql);
		if (imHistoryMessage != null) {
			imHistoryMessage.setSendStatus(0);
			imHistoryMessage.update();
			return true;
		}
		return false;
	}
	/**
	 * 判断消息发送失败，并发送推送回调推送
	 * @param fingerPrints
	 */
	public void pushMessage(String fingerPrint,String from_user_id ,String to_user_id,String content,Integer typeu ,Integer ctype,Integer sendStatus) {
	
			String decmessage="";
			try {
				decmessage=DesUtil.decrypt(content, "zhongkewenge2018");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//存储消息
			if(ctype.equals(1)){//聊天类型：1单聊 ， 2群聊  
				String sql1="select nick_name,user_name,app_status from manage_user_info where user_id=?";
				ManageUserInfo manageUserInfo=ManageUserInfo.dao.findFirst(sql1,new Object[]{from_user_id});
				ManageUserInfo to_user=ManageUserInfo.dao.findFirst(sql1,new Object[]{to_user_id});
				
				String fromName="";
				if(manageUserInfo!=null){
					fromName=manageUserInfo.get("nick_name");
					if(fromName.equals(null)||fromName.isEmpty()){
						fromName=manageUserInfo.get("user_name");
					}
				}
				try {
					//获取用户当前的状态，是否正在打开应用还是在后台 0在后台    1正在打开应用
					int appstatus=to_user.getAppStatus();
					System.out.println("是否正在打开应用还是在后台"+appstatus);
					if(appstatus==0){
						System.out.println("单聊@@@@@@@@@@@@@@@@:"+fromName+"###"+fingerPrint+"-----"+to_user_id+",typeu:"+typeu+",decmessage:"+decmessage);
//						if(typeu!=41&&typeu!=51&&typeu!=61&&typeu!=71){//当发送消息为通知，会议，任务，审稿
						if(typeu < 40){///当发送消息为通知，会议，任务，审稿
							UmPush.push.sendUnicast2( fingerPrint,from_user_id, to_user_id, fromName, "", typeu.toString(), decmessage,null,null,null);
						}

					}
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}else if(ctype.equals(2)&&typeu/10==2){
				String groupid = from_user_id.split("#")[1].toString();
				String sql2="SELECT group_name from manage_group where group_id=?";
				ManageGroup manageGroup=ManageGroup.dao.findFirst(sql2,new Object[]{groupid});
				String fromName="";
				//获取用户当前的状态，是否正在打开应用还是在后台
				String sql1="select nick_name,user_name,app_status from manage_user_info where user_id=?";
				ManageUserInfo to_user=ManageUserInfo.dao.findFirst(sql1,new Object[]{to_user_id});
				if(manageGroup!=null){
					fromName=manageGroup.get("group_name");
				}
				
				try {
					int appstatus=to_user.getAppStatus();
					if(appstatus==0){
						System.out.println("群聊@@@@@@@@@@@@@@@@:"+fromName+"###"+fingerPrint+"-----"+to_user_id+",typeu:"+typeu+",decmessage:"+decmessage);
//						if(typeu!=41&&typeu!=51&&typeu!=61&&typeu!=71){//当发送消息为通知，会议，任务，审稿
//							UmPush.push.sendUnicast2( fingerPrint,from_user_id, to_user_id, fromName, "", typeu.toString(), decmessage,null,null,null);
//						}
						if(21==typeu){
							UmPush.push.sendUnicast2( fingerPrint,from_user_id, to_user_id, fromName, "", typeu.toString(), decmessage,null,null,null);
						}else if(22==typeu){
							UmPush.push.sendUnicast2( fingerPrint,from_user_id, to_user_id, fromName, "", typeu.toString(), decmessage,null,null,null);
						}else if(23==typeu){
							UmPush.push.sendUnicast2( fingerPrint,from_user_id, to_user_id, fromName, "", typeu.toString(), decmessage,null,null,null);
						}else if(24==typeu){
							UmPush.push.sendUnicast2( fingerPrint,from_user_id, to_user_id, fromName, "", typeu.toString(), decmessage,null,null,null);
						}else if(25==typeu){
							UmPush.push.sendUnicast2( fingerPrint,from_user_id, to_user_id, fromName, "", typeu.toString(), decmessage,null,null,null);
						}else if(27==typeu){
							UmPush.push.sendUnicast2( fingerPrint,from_user_id, to_user_id, fromName, "", typeu.toString(), decmessage,null,null,null);
						}
					}
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	 
	 
	 

