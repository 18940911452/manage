package wg.user.mobileimsdk.server.controller;

import java.io.IOException;
import java.net.SocketAddress;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.core.Controller;
import com.jfinal.kit.PropKit;

import io.netty.channel.Channel;
import wg.openmob.mobileimsdk.server.listener.ServerEventListenerImpl;
import wg.openmob.mobileimsdk.server.processor.LogicProcessor;
import wg.openmob.mobileimsdk.server.protocal.Protocal;
import wg.openmob.mobileimsdk.server.utils.GlobalSendHelper;
import wg.user.mobileimsdk.server.service.InformService;
import wg.user.mobileimsdk.server.service.MessageService;
import wg.user.mobileimsdk.server.util.SendPost;

/**
 * 类名称：InformController 
 * 类描述：通知
 * 创建时间：2018年4月17日 下午6:30:58 
 * @author gongxiang.pang
 * @version V1.0
 */
public class InformController extends Controller{
	private static int cpus = Runtime.getRuntime().availableProcessors();
	private static ExecutorService executes = Executors.newFixedThreadPool((int) Math.pow(2, cpus));
	public static String  webServerIpPort=PropKit.get("webServerIpPort");
	/**
	 * 
	 * 名称：addActionPlan 
	 * 描述：添加计划
	 * @author gongxiang.pang
	 * @parameter
	 * @return
	 */
	public void addInformPlan(){
		String uid=getPara("uid");
		String toUid=getPara("toUid");
		String title=getPara("title");
		String content=getPara("content");
		String url=getPara("url");
		String type=getPara("type","1");
		JSONObject result=new JSONObject();
		boolean addActionPlan = InformService.ser.addInformPlan(uid,toUid,title,content,url,type);
		if(addActionPlan){
			result.put("result", "success");
			result.put("status", "1");
			result.put("message", "成功");
			//发送提示至移动端
			String[] split = toUid.split(",");
			if(split!=null && split.length>0){
				for (int i = 0; i < split.length; i++) {
					String touid=split[i].toString();
//					InformToAppMessage(title,uid,touid,41);//向移动端发送消息提醒
				}
			}
			
		}else{
			result.put("result", "false");
			result.put("status", "-1");
			result.put("message", "失败");
		}
		renderJson(result);
	}
	
	
	/**
	 * 名称：setReadInform
	 * 描述：将未读变为已读
	 * @author gongxiang.pang
	 * @parameter
	 * @return
	 */
	public void setReadInform(){
		String id=getPara("id");
		JSONObject result=new JSONObject();
		boolean setReadAtionPlan =false;
		if(id!=null){
			 setReadAtionPlan = InformService.ser.setReadInform(id);
		}
		
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
	
	
	/**
	 * 名称：getAtionPlanList 
	 * 描述:获取行动预案列表
	 * @author gongxiang.pang
	 * @parameter
	 * @return
	 */
	public void getInformPlanList(){
		String uid =getPara("uid");
		Integer pageNo = getParaToInt("pageNo",1);
		Integer pageSize = getParaToInt("pageSize",20);
		JSONObject result=new JSONObject();
		JSONObject actionPlan = InformService.ser.getInformPlan(uid,pageNo,pageSize);
		if(actionPlan!=null && actionPlan.size()==0){
			result.put("result", "");
			result.put("status", "-1");
			result.put("message", "暂无数据");
		}else{
			result.put("result", actionPlan.get("result"));
			result.put("total", actionPlan.get("total"));
			result.put("status", "1");
			result.put("message", "成功");
		}
		renderJson(result);
	}
	/**
	 * 
	 * 名称：planToAppMessage 
	 * 描述：向移动端通知行预案消息数量通知
	 * @author gongxiang.pang
	 * @parameter
	 * @return 
	 * @param message
	 * @param fromUserId
	 * @param toUserId
	 * @param typeu
	 */
	public void InformToAppMessage(String message,String fromUserId,String toUserId,Integer typeu){
		//int type, String dataContent, String from, String to, boolean QoS, String fingerPrint, int typeu
		String fingerPrint=fromUserId+new Date().getTime()+"000#" + toUserId;
		Protocal Protocal=new Protocal(2, message, fromUserId, toUserId, true, fingerPrint, typeu);
		try {
			Set<Channel> set = ServerEventListenerImpl.ser.rooms.get(toUserId);
			if(set!=null){
				for (Channel s : set) {
//					if(s.isOpen()){
						String remoteAddress = clientInfoToString(s,fromUserId);
						GlobalSendHelper.ser.sendDataC2C(null, s, Protocal, remoteAddress, LogicProcessor.serverCoreHandler);
//					}
				}
			}else {
				MessageService.ser.saveHisMessage(fingerPrint, fromUserId, toUserId, message, 41, 2, 1,1);
			}
			//向网页端发送消息
			executes.submit(new Runnable(){
				@Override
				public void run() {
					//逐一发送各个客户端
					try {
						//向网页端发送信息
						toWebInfor( message, fromUserId,toUserId, typeu,new Date().getTime());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		renderText("ok");
	}
	/**
	 * 
	 * 名称：clientInfoToString 
	 * 描述：构造地址对象
	 * @author gongxiang.pang
	 * @parameter
	 * @return 
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
	public void toWebInfor(String message,String fromUserId ,String toUserId,Integer  typeu,long fingerPrints) {
		Map<String, Object> map=new HashMap<String, Object>();
		map.put("groupId", message.split(",")[0]);
		map.put("toUserId",toUserId);
	    map.put("message", message);
		String url=webServerIpPort+"/apptoweb/AppToWebMessage";
		map.put("typeu", typeu);
		map.put("fromUserId",fromUserId);
		map.put("fingerPrints",fingerPrints);
		
//		System.out.println(url);
//		System.out.println(map.get("groupId"));
//		System.out.println(map.get("toUserId"));
//		System.out.println(map.get("message"));
//		System.out.println(map.get("typeu"));
//		System.out.println(map.get("fromUserId"));
//		System.out.println(map.get("fingerPrints"));
		SendPost.sendPost(url,map);
	}
}
