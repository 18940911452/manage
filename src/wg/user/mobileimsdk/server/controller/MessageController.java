package wg.user.mobileimsdk.server.controller;

import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.core.Controller;

import wg.media.screen.fm.model.commandscreencommon.HistoryDialogue;
import wg.media.screen.fm.model.commandscreencommon.ImHistoryMessage;
import wg.user.mobileimsdk.server.service.GroupService;
import wg.user.mobileimsdk.server.service.MessageService;
import wg.user.mobileimsdk.server.util.DesUtil;
/**
 * 消息处理相关接口
 * @author panggongxiang
 *
 */
public class MessageController extends Controller{	
	
	/**
	 * 获取离线消息
	 * 参数:uid  用户id
	 */
	public void getOfflineMessage(){
		String uid=getPara("uid","12");
		JSONObject offlineMessage = MessageService.ser.getOfflineMessage(uid);
		renderJson(offlineMessage);
	}
	/**
	 * 回调消息处理
	 */
	public void callBackOfflineMessage(){
		String fingerPrintStr=getPara("fingerPrintStr");
		JSONObject offlineMessage = MessageService.ser.callBackOfflineMessage(fingerPrintStr);
		renderJson(offlineMessage);
	}
	
	/**
	 * 根据id获取历史对话框
	 * 用户登录后获取历史对话框列表
	*/
	public void getDialogueList(){
		String uid=getPara("uid");
		List<HistoryDialogue>  result = MessageService.ser.getDialogueList(uid);
		if(result!=null){
			renderJson(result);
		}else{
			renderJson("");
		}
	}
	
	/**
	 * 
	 */
	public void addDialogue(){
		String uid=getPara("user_id");
		String dtype=getPara("dtype","1");
		String to_user_id=getPara("to_user_id");
		String group_id=getPara("group_id");
		String result = MessageService.ser.addDialogue(uid,dtype,to_user_id,group_id);
		JSONObject json=new JSONObject();
		json.put("result",result);
		renderJson(json);
		
	}
	
	/**
	 * @return 
	 * 
	 */
	public void sendMessage(){
		ImHistoryMessage msg = new ImHistoryMessage();
		String toUserId;
		String fromUserId="";
		String fromUserIdgroup="";
		String fingerPrint="";
		String time=getPara("fingerPrint");
		Integer ctype=getParaToInt("ctype");
		Integer typeu = getParaToInt("typeu");
		String content = getPara("content");
		Integer sourceType = getParaToInt("sourceType");
		Integer sendStatus =  getParaToInt("sendStatus");
		toUserId=getPara("toUserId");
		Integer res = 0;
			try {
				content = DesUtil.encrypt(content, "zhongkewenge2018");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		if(ctype ==1) {
			 fromUserId=getPara("fromUserId");
			 fingerPrint=fromUserId+time+"000";
			 msg.setFingerPrint(fingerPrint);
			 res = MessageService.ser.saveHisMessage(fingerPrint, fromUserId, toUserId, content, typeu, ctype, sendStatus,sourceType);
		}else {
                if(!"".equals(toUserId)) {
					 Integer groupId=getParaToInt("groupId");
					 fromUserId=getPara("fromUserId")+"#"+groupId;
					 String [] toUserIds = toUserId.split(",");
					 for (int i = 0; i < toUserIds.length; i++) {
						 //若该成员没有对话框添加对话框
//				        HistoryDialogue dialogue = new HistoryDialogue();
//				        dialogue.setUserId(Integer.parseInt(toUserIds[i]));
//				        dialogue.setGroupId(groupId);
//				        dialogue.setDtype(2);
//				        dialogue.setStatus(1);
					    //GroupService.ser.addDialogue(dialogue);    
						fingerPrint=getPara("fromUserId")+time+"000#"+toUserIds[i];
						res += MessageService.ser.saveHisMessage(fingerPrint, fromUserId, toUserIds[i], content, typeu, ctype, sendStatus,sourceType);
					}
					 fingerPrint=getPara("fromUserId")+time+"000#"+getPara("fromUserId");
					 res += MessageService.ser.saveHisMessage(fingerPrint, fromUserId, getPara("fromUserId"), content, typeu, ctype, sendStatus,sourceType);
				 }
			 }
		             renderText(res.toString());
			}
	public void setReadMessage(){
		String ctype=getPara("ctype");
		String fromUserId=getPara("fromUserId");
		String toUserId=getPara("toUserId");
		String groupId=getPara("groupId");
		String fingerPrint=getPara("fingerPrint");
		JSONObject result=new JSONObject();
		boolean setReadAtionPlan =false;
			 setReadAtionPlan = MessageService.setReadMessage(ctype,fromUserId,toUserId,groupId,fingerPrint);
		if(setReadAtionPlan){
			result.put("result", "success");
			result.put("status", "1");
			result.put("message", "成功");
		}else{
			result.put("result", "flase");
			result.put("status", "-1");
			result.put("message", "设置失败或id不能为空");
		}
		renderJson(result);
	}
}
