package wg.openmob.mobileimsdk.server.listener;

import io.netty.channel.Channel;
import wg.media.screen.fm.model.commandscreencommon.ManageUserInfo;
import wg.openmob.mobileimsdk.server.event.ServerEventListener;
import wg.openmob.mobileimsdk.server.processor.LogicProcessor;
import wg.openmob.mobileimsdk.server.processor.OnlineProcessor;
import wg.openmob.mobileimsdk.server.protocal.Protocal;
import wg.openmob.mobileimsdk.server.protocal.ProtocalFactory;
import wg.openmob.mobileimsdk.server.utils.GlobalSendHelper;
import wg.openmob.mobileimsdk.server.utils.LocalSendHelper;
import wg.user.mobileimsdk.server.controller.UmPush;
import wg.user.mobileimsdk.server.service.LoginService;
import wg.user.mobileimsdk.server.service.MessageService;
import wg.user.mobileimsdk.server.util.DesUtil;
import wg.user.mobileimsdk.server.util.SendPost;

import java.io.IOException;
import java.net.SocketAddress;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jfinal.kit.PropKit;

/**
 * 与客服端的所有数据交互事件在此ServerEventListener子类中实现即可。
 * 
 * @version 1.0
 * @since 3.1
 */
public class ServerEventListenerImpl implements ServerEventListener
{
	private static Logger logger = LoggerFactory.getLogger(ServerEventListenerImpl.class);  
	public static Map<String, Set<Channel>> rooms = new HashMap<String, Set<Channel>>();
	private static int cpus = Runtime.getRuntime().availableProcessors();
	public static String  webServerIpPort=PropKit.get("webServerIpPort");
	
	static int maxSize = 200;
	public static Map<String, Long> messageMap = new LinkedHashMap<String, Long>(){
	      private static final long    serialVersionUID    = 1L;
	      @Override
	      protected boolean removeEldestEntry(java.util.Map.Entry<String, Long> pEldest) {
	          return size() > maxSize;
	      }
	  };
	/**
	 * 任务 短生命周期，并发量大，IO访问频繁。
	 */
	private static ExecutorService executes = Executors.newFixedThreadPool((int) Math.pow(2, cpus));
	public static ServerEventListenerImpl ser=new ServerEventListenerImpl();
	
	/**
	 * 用户身份验证回调方法定义.
	 * <p>
	 * 
	 * 服务端的应用层可在本方法中实现用户登陆验证。
	 * <br>
	 * 注意：本回调在一种特殊情况下——即用户实际未退出登陆但再次发起来登陆包时，本回调是不会被调用的！
	 * <p>
	 * 根据MobileIMSDK的算法实现，本方法中用户验证通过（即方法返回值=0时）后
	 * ，将立即调用回调方法 {@link #onUserLoginAction_CallBack(int, String, IoSession)}。
	 * 否则会将验证结果（本方法返回值错误码通过客户端的 ChatBaseEvent.onLoginMessage(int dwUserId, int dwErrorCode)
	 * 方法进行回调）通知客户端）。
	 * 
	 * @param userId 传递过来的准一id，保证唯一就可以通信，可能是登陆用户名、也可能是任意不重复的id等，具体意义由业务层决定
	 * @param token 用于身份鉴别和合法性检查的token，它可能是登陆密码，也可能是通过前置单点登陆接口拿到的token等，具体意义由业务层决定
	 * @param extra 额外信息字符串。本字段目前为保留字段，供上层应用自行放置需要的内容
	 * @param session 此客户端连接对应的MINA会话
	 * @return 0 表示登陆验证通过，否则可以返回用户自已定义的错误码，错误码值应为：>=1025的整数
	 */
	@Override
	public int onVerifyUserCallBack(String userId, String token, String extra, Channel session)
	{
		        int code= 0;
				logger.debug("【IM_回调通知OnUserLoginAction_CallBack】用户："+userId+" 上线了！");
				return code;
		}

	/**
	 * 用户登录验证成功后的回调方法定义（可理解为上线通知回调）.
	 * <p>
	 * 服务端的应用层通常可在本方法中实现用户上线通知等。
	 * <br>
	 * 注意：本回调在一种特殊情况下——即用户实际未退出登陆但再次发起来登陆包时，回调也是一定会被调用。
	 * 
	 * @param userId 传递过来的准一id，保证唯一就可以通信，可能是登陆用户名、也可能是任意不重复的id等，具体意义由业务层决定
	 * @param extra 额外信息字符串。本字段目前为保留字段，供上层应用自行放置需要的内容。为了丰富应用层处理的手段，在本回调中也把此字段传进来了
	 * @param session 成功登陆后建立的MINA会话
	 */
	@Override
	public void onUserLoginAction_CallBack(String userId, String extra, Channel session)
	{
		//登录成功后放入MAP session集合
		String wsid = userId;
		if(null == wsid){
			
		}else{
			Set<Channel> set = rooms.get(wsid);
			if(null == set){
				set = new HashSet<Channel>();
				rooms.put(wsid, set);
			}
			set.add(session);
		}
		//上线后发送离线消息
		sendOffLineMessage(userId);
		
		
	}
	
	/**
	 * 当用户上线后调用该程序，获取该用户离线消息，发送离线消息给上线用户
	 * 单个用户上线后，先获取离线消息，定时发给这人
	 */
	public static void sendOffLineMessage( String  toUserId){

		//int type, String dataContent, String from, String to, boolean QoS, String fingerPrint, int typeu
		//获取该用户的全部离线消息
		String sql_offmessage="SELECT * from im_history_message where  to_user_id=? and  send_status=1";
		 List<ManageUserInfo> offmessageList=ManageUserInfo.dao.find(sql_offmessage,new Object[]{toUserId});
		
		 for (int i = 0; i < offmessageList.size(); i++) {
			 	String message=offmessageList.get(i).getStr("content");
			 	String fromUserId=offmessageList.get(i).getStr("from_user_id");
			 	Integer typeu=offmessageList.get(i).getInt("utype");
			 	String fingerPrint=offmessageList.get(i).getStr("fingerPrint");
				//构造一个客户端
				Protocal Protocal=new Protocal(2, message, fromUserId, toUserId, true, fingerPrint, typeu);
				Set<Channel> set = ServerEventListenerImpl.ser.rooms.get(toUserId);
				if(set!=null){
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
				}
		}
		
		
	}
	
	/**
	 * 用户退出登录回调方法定义（可理解为下线通知回调）。
	 * <p>
	 * 服务端的应用层通常可在本方法中实现用户下线通知等。
	 * 
	 * @param userId 下线的用户user_id
	 * @param obj
	 * @param session 此客户端连接对应的MINA会话
	 */
	@Override
	public void onUserLogoutAction_CallBack(String userId, Object obj, Channel session)
	{
		//退出后从MAP session集合删除
		if(null != userId){
			Set<Channel> set = rooms.get(userId);
			set.remove(session);
			if(set.isEmpty()){
				rooms.remove(userId);
			}
		}
		
		logger.debug("【DEBUG_回调通知OnUserLogoutAction_CallBack】用户："+userId+" 离线了！");
	}

	/**
	 * 通用数据回调方法定义（客户端发给服务端的（即接收user_id="0"））.
	 * <p>
	 * MobileIMSDK在收到客户端向user_id=0(即接收目标是服务器)的情况下通过
	 * 本方法的回调通知上层。上层通常可在本方法中实现如：添加好友请求等业务实现。
	 * 
	 * @param userId 接收方的user_id（本方法接收的是发给服务端的消息，所以此参数的值肯定==0）
	 * @param from_user_id 发送方的user_id
	 * @param dataContent 数据内容（文本形式）
	 * @param session 此客户端连接对应的MINA会话
	 * @return true表示本方法已成功处理完成，否则表示未处理成功。此返回值目前框架中并没有特殊意义，仅作保留吧
	 */
	@Override
	public boolean onTransBuffer_CallBack(String userId, String from_user_id,
			String dataContent, String fingerPrint, int typeu, Channel session)
	{
		logger.debug("【DEBUG_回调通知】[typeu="+typeu+"]收到了客户端"+from_user_id+"发给服务端的消息：str="+dataContent);
		return true;
	}

	/**
	 * 通道数据回调函数定义（客户端发给客户端的（即接收方user_id不为“0”的情况））.
	 * <p>
	 * <b>注意：</b>本方法当且仅当在数据被服务端成功在线发送出去后被回调调用.
	 * <p>
	 * 上层通常可在本方法中实现用户聊天信息的收集，以便后期监控分析用户的行为等^_^。
	 * <p>
	 * 提示：如果开启消息QoS保证，因重传机制，本回调中的消息理论上有重复的可能，请以参数 #fingerPrint
	 * 作为消息的唯一标识ID进行去重处理。
	 * 
	 * @param userId 接收方的user_id（本方法接收的是客户端发给客户端的，所以此参数的值肯定>0）
	 * @param from_user_id 发送方的user_id
	 * @param dataContent
	 */
	@Override
	public void onTransBuffer_C2C_CallBack(final String userId, final String from_user_id,
			final String dataContent,  String fingerPrint, final int typeu)
	{
		 int ctype=0;
		 String fp="";
		//存储消息
		//来源用户id   from_user_id ,发送给用户id  to_user_id,内容 content,发送类型    utype ,发送时间 send_time,聊天类型：1单聊 ， 2群聊   ctype,   1离线消息，0历史消息：sendStatus
		if(typeu/10==2){
			ctype=2;
			if(!fingerPrint.contains("#")) {
				fp=fingerPrint+"#"+userId;
			}else {
				fp=fingerPrint;
			}
		}else{
			ctype=1;
			fp=fingerPrint;
		}
		final int c=ctype;
		final String fingerPrints=fp;
		executes.submit(new Runnable(){
			@Override
			public void run() {
				//逐一发送各个客户端
				try {
					MessageService.ser.saveMessage(fingerPrints,from_user_id, userId, dataContent, typeu, c, 0);
					logger.info("来自：【"+from_user_id+"】,发送给【"+userId+"】,消息指纹码："+fingerPrints+"---------发送成功并保存");
					
					//如果能收到信息，但是APP 在待机状态没有打开应用，也要
					String sql1="select nick_name,user_name,app_status from manage_user_info where user_id=?";
					ManageUserInfo to_user=ManageUserInfo.dao.findFirst(sql1,new Object[]{userId});
					//System.out.println("@@@@@@@@@@@@@@@@@@@@/"+to_user.getAppStatus());
					int appstatus=to_user.getAppStatus();
					if(appstatus==0){
						System.out.println("##################/"+to_user.getAppStatus()+"+type:"+typeu+"+c:"+c);
//						if(typeu!=41&&typeu!=51&&typeu!=61&&typeu!=71) {//当发送消息为通知，会议，任务，审稿
						if(typeu < 40){///当发送消息为通知，会议，任务，审稿
							MessageService.ser.pushMessage(fingerPrints,from_user_id, userId, dataContent, typeu, c, 1);
						}
					}
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		logger.debug("【DEBUG_回调通知】[typeu="+typeu+"]收到了客户端"+from_user_id+"发给客户端"+userId+"的消息：str="+dataContent);
		
	}

	/**
	 * 通用数据实时发送失败后的回调函数定义（客户端发给客户端的（即接收方user_id不为“0”的情况））.
	 * <p>
	 * 注意：本方法当且仅当在数据被服务端<u>在线发送</u>失败后被回调调用.
	 * <p>
	 * <b>此方法存的意义何在？</b><br>
	 * 发生此种情况的场景可能是：对方确实不在线（那么此方法里就可以作为离线消息处理了）、
	 * 或者在发送时判断对方是在线的但服务端在发送时却没有成功（这种情况就可能是通信错误
	 * 或对方非正常通出但尚未到达会话超时时限）。<br><u>应用层在此方法里实现离线消息的处理即可！</u>
	 * 
	 * @param userId 接收方的user_id（本方法接收的是客户端发给客户端的，所以此参数的值肯定>0），此id在本方法中不一定保证有意义
	 * @param from_user_id 发送方的user_id
	 * @param dataContent 消息内容
	 * @param fingerPrint 该消息对应的指纹（如果该消息有QoS保证机制的话），用于在QoS重要机制下服务端离线存储时防止重复存储哦
	 * @return true表示应用层已经处理了离线消息（如果该消息有QoS机制，则服务端将代为发送一条伪应答包
	 * （伪应答仅意味着不是接收方的实时应答，而只是存储到离线DB中，但在发送方看来也算是被对方收到，只是延
	 * 迟收到而已（离线消息嘛））），否则表示应用层没有处理（如果此消息有QoS机制，则发送方在QoS重传机制超时
	 * 后报出消息发送失败的提示）
	 * @see #onTransBuffer_C2C_CallBack(int, int, String)
	 */
	@Override
	public boolean onTransBuffer_C2C_RealTimeSendFaild_CallBack(final String userId,
			final String from_user_id, final String dataContent,  String fingerPrint, final int typeu)
	{
		//消息失败做离线消息处理
		int ctype=0;
		 String fp="";
		 System.out.println("typeu:"+typeu+" 指纹码："+fingerPrint);
		//存储消息
		if(typeu/10==2){
		//来源用户id   from_user_id ,发送给用户id  to_user_id,内容 content,发送类型    utype ,发送时间 send_time,聊天类型：1单聊 ， 2群聊   ctype,   1离线消息，0历史消息：sendStatus
			ctype=2;
			if(!fingerPrint.contains("#")) {
				fp=fingerPrint+"#"+userId;
			}else {
				fp=fingerPrint;
			}
		}else{
			ctype=1;
			fp=fingerPrint;
		}
		final int c=ctype;
		final String fingerPrints=fp;
		executes.submit(new Runnable(){
			@Override
			public void run() {
				//逐一发送各个客户端
				try {
					//保存历史消息
					MessageService.ser.saveMessage(fingerPrints,from_user_id, userId, dataContent, typeu, c, 1);
//					if(typeu!=41&&typeu!=51&&typeu!=61&&(typeu>=70 && typeu<=80)){//当发送消息为通知，会议，任务，审稿
					if(typeu < 40){//当发送消息为通知，会议，任务，审稿
						logger.info("来自：【"+from_user_id+"】,发送给【"+userId+"】,消息指纹码："+fingerPrints+"开始发送推送");
						MessageService.ser.pushMessage(fingerPrints,from_user_id, userId, dataContent, typeu, c, 1);
					}
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		logger.info("来自：【"+from_user_id+"】,发送给【"+userId+"】,消息指纹码："+fingerPrints+"---------接受失败，已经离线处理");
		if(OnlineProcessor.isOnline(userId)) {
			return false;
		}else {
			return true;
		}
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
	
	public static void main(String[] args) {
	Integer integer = 	Integer.parseInt("9999999990");
	System.out.println(integer);
	}
}
