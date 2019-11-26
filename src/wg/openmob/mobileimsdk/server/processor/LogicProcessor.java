package wg.openmob.mobileimsdk.server.processor;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler.ServerHandshakeStateEvent;
import wg.media.screen.fm.model.commandscreencommon.ManageGroupUserTaskRel;
import wg.media.screen.fm.model.commandscreencommon.ManageUserInfo;
import wg.nettime.mobileimsdk.server.bridge.QoS4SendDaemonB2C;
import wg.nettime.mobileimsdk.server.netty.MBObserver;
import wg.openmob.mobileimsdk.server.handler.ServerCoreHandler;
import wg.openmob.mobileimsdk.server.listener.ServerEventListenerImpl;
import wg.openmob.mobileimsdk.server.protocal.Protocal;
import wg.openmob.mobileimsdk.server.protocal.ProtocalFactory;
import wg.openmob.mobileimsdk.server.protocal.c.PLoginInfo;
import wg.openmob.mobileimsdk.server.qos.QoS4ReciveDaemonC2S;
import wg.openmob.mobileimsdk.server.qos.QoS4SendDaemonS2C;
import wg.openmob.mobileimsdk.server.utils.GlobalSendHelper;
import wg.openmob.mobileimsdk.server.utils.LocalSendHelper;
import wg.user.mobileimsdk.server.service.FsUserServices;
import wg.user.mobileimsdk.server.service.MemberService;
import wg.user.mobileimsdk.server.service.MessageService;
import wg.user.mobileimsdk.server.util.DesUtil;
import wg.user.mobileimsdk.server.util.SendPost;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.SocketAddress;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.core.Controller;
import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;

/**
 * MobileIMSDK框架的IM消息逻辑处理器。
 * <p>
 * <font color="red">本类是MobileIMSDK的服务端消息处理逻辑代码集中类，由服务端算法自行
 * 调用，开发者原则上不需要关注本类中的任何方法，请勿随意调用！</font>
 * 
 * @version 1.0
 * @since 3.0
 */
public class LogicProcessor 
{
	
	
	static Prop prop = PropKit.use("web_config.txt");
	private static int cpus = Runtime.getRuntime().availableProcessors();

	static int maxSize = 200;
	  Map<String, Integer> messagetoWeb = new LinkedHashMap<String, Integer>(){
	      private static final long    serialVersionUID    = 1L;
	      @Override
	      protected boolean removeEldestEntry(java.util.Map.Entry<String, Integer> pEldest) {
	          return size() > maxSize;
	      }
	  };
	  
	/**
	 * 任务 短生命周期，并发量大，IO访问频繁。
	 */
	private static ExecutorService executes = Executors.newFixedThreadPool((int) Math.pow(2, cpus));

	private static Logger logger = LoggerFactory.getLogger(LogicProcessor.class);  
	
	public static  ServerCoreHandler serverCoreHandler = null;
	public static String  webServerIpPort=PropKit.get("webServerIpPort");
	
	public LogicProcessor(ServerCoreHandler serverCoreHandler)
	{
		this.serverCoreHandler = serverCoreHandler;
	}	
	
	public static String clientInfoToString(Channel session,String from)
	{
		SocketAddress remoteAddress = session.remoteAddress();
		String s1 = remoteAddress.toString();
		StringBuilder sb = new StringBuilder()
		.append("{uid:")
		.append(from)
		.append("}")
		.append(s1);
		return sb.toString();
	}
	/**
	 * 处理C2C（client to client）类消息（即客户端发给客户端的普通聊天消息）。
	 * 
	 * @param bridgeProcessor
	 * @param session
	 * @param pFromClient
	 * @param remoteAddress
	 * @throws Exception
	 */
	public void processC2CMessage(final BridgeProcessor bridgeProcessor,
			final Channel session,  Protocal pFromClient, final String remoteAddress) throws Exception
	{
		final int typeu = pFromClient.getTypeu();
		final String toUserId = pFromClient.getTo();
		final String fromUserId=pFromClient.getFrom();
		final String message=pFromClient.getDataContent();
		final String fingerPrints=pFromClient.getFp()+"#"+fromUserId;
		final ServerCoreHandler servleHandler= this.serverCoreHandler;
		
		
		if(typeu/10==2){
			System.out.println("------------群聊--------------------------------------------");
			List<ManageGroupUserTaskRel> result=MemberService.ser.getGroupUserIds(Integer.valueOf(toUserId),Integer.valueOf(fromUserId));
			String newFromid=fromUserId+"#"+toUserId;
			pFromClient.setFrom(newFromid);
			pFromClient.setQoS(true);
		//	MessageService.ser.saveMessage(fingerPrints,FromId, fromUserId, message, typeu, 2, 0);
//			for (int i = 0; i < result.size(); i++) {
//				String user_id = result.get(i).get("user_id").toString();
//				final Protocal tFromClient=(Protocal) pFromClient.clone();
//				tFromClient.setTo(user_id);
//				MessageService.ser.saveMessage(fingerPrints,FromId, fromUserId, message, typeu, 2, 0);
				executes.submit(new Runnable(){
					@Override
					public void run() {
						//逐一发送各个客户端
						try {
							for (int i = 0; i < result.size(); i++) {
								String user_id = result.get(i).get("user_id").toString();
								final Protocal tFromClient=(Protocal) pFromClient.clone();
								tFromClient.setTo(user_id);
							GlobalSendHelper.sendDataC2C(bridgeProcessor, session, tFromClient, remoteAddress,servleHandler );
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
				
			
				//向web端同步发送消息
				if(!messagetoWeb.containsKey(fingerPrints) ){
					if(prop.getBoolean("sendMesageToWeb", false)){
						executes.submit(new Runnable(){
							@Override
							public void run() {
								//逐一发送各个客户端
								try {
									//向网页端发送信息
								for (int i = 0; i < result.size(); i++) {
										String user_id = result.get(i).get("user_id").toString();
										String fingerPrint=pFromClient.getFp()+"#"+user_id;
									toWebMassage( message, toUserId, fromUserId,  typeu,pFromClient.getFp());
									}
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						});
					}
				}
				
			
		}else{
			System.out.println("---------------------单聊消息-----------------------------");
			String towebFp=pFromClient.getFp();
//			MessageService.ser.saveMessage(towebFp,fromUserId, toUserId, message, typeu, 1, 0);
			final Protocal tpFromClient=pFromClient;
			//单个发送到客户端
			executes.submit(new Runnable(){
				@Override
				public void run() {
					//逐一发送各个客户端
					try {
						System.out.println("---------------------APP的消息发送给客户端---------------------------");
						GlobalSendHelper.sendDataC2C(bridgeProcessor, session, tpFromClient, remoteAddress,servleHandler );
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			//向网页端发送消息
			if(prop.getBoolean("sendMesageToWeb", false)){
				 if(!messagetoWeb.containsKey(towebFp)){//
					executes.submit(new Runnable(){
						@Override
						public void run() {
							//逐一发送各个客户端
							try {
								//向网页端发送信息
								System.out.println("------------APP的消息同步发送给网页端---------------------------");
								toWebMassage( message, toUserId, fromUserId,  typeu,fingerPrints);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					});
				}
			}

		}
	}

	
	/**
	 * 把消息推送到网页 端
	 * @throws Exception 
	 */
	public void toWebMassage(String message,String toUserId,String fromUserId,Integer  typeu,String fingerPrints) {
		Map<String, Object> map=new HashMap<String, Object>();
		String toUserIds = "";
		if (typeu/10 == 2) {
			String sql="SELECT mui.user_id userId "
					+ "FROM manage_user_info mui,manage_group mg,manage_group_user_task_rel mgutr "
					+ "WHERE mgutr.group_id=mg.group_id and mui.user_id=mgutr.user_id and mg.group_id=?";
			List<ManageUserInfo>manageUserInfos=ManageUserInfo.dao.find(sql, new Object[]{toUserId});
			for (int i = 0; i < manageUserInfos.size(); i++) {
				toUserIds += manageUserInfos.get(i).getInt("userId") + ",";
			}
			if(toUserIds != "") {
				toUserIds = toUserIds.substring(0,toUserIds.length() - 1);
			}
			map.put("groupId", toUserId);
			map.put("toUserId",toUserIds);
		}else {
			map.put("toUserId",toUserId);
		}
		try {
			map.put("message", DesUtil.decrypt(message, "zhongkewenge2018"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String url=webServerIpPort+"/apptoweb/AppToWebMessage";
		map.put("typeu", typeu);
		map.put("fromUserId",fromUserId);
		map.put("fingerPrints",fingerPrints);
		
		SendPost.sendPost(url,map);
	}
	/**
	 * 处理C2S（client to server）类消息（即客户端发给服务端的指点令类消息）。
	 * 
	 * @param session
	 * @param pFromClient
	 * @param remoteAddress
	 * @throws Exception
	 */
	public void processC2SMessage(Channel session, final Protocal pFromClient
			, String remoteAddress) throws Exception
	{
		// 客户端直发服务端（而不是发给另一客户端）的正常数据包则
		// 回一个C2S模式的质量保证模式下的应答包
		if(pFromClient.isQoS())// && processedOK)
		{
			// 已经存在于已接收列表中（及意味着可能是之前发给对方的应
			// 答包因网络或其它情况丢了，对方又因QoS机制重新发过来了）
			if(QoS4ReciveDaemonC2S.getInstance().hasRecieved(pFromClient.getFp()))
			{
				if(QoS4ReciveDaemonC2S.getInstance().isDebugable())
					logger.debug("[IMCORE-本机QoS！]【QoS机制】"+pFromClient.getFp()
							+"已经存在于发送列表中，这是重复包，通知业务处理层收到该包罗！");
				
				// ## Bug FIX: 20170215 by jackjiang - 解决了不能触发回调onTransBuffer_CallBack的问题
				//------------------------------------------------------------------------------ [1]代码与[2]处相同的哦 S
				// 【【QoS机制2/4步：将收到的包存入QoS接收方暂存队列中（用于防QoS消息重复）】】
				// @see 客户端LocalUDPDataReciever中的第1/4和第4/4步相关处理
				QoS4ReciveDaemonC2S.getInstance().addRecieved(pFromClient);
				// 【【QoS机制3/4步：回应答包】】
				// @see 客户端LocalUDPDataReciever中的第1/4和第4/4步相关处理
				// 给发送者回一个“收到”应答包(发送C2S模式的应答包)
				LocalSendHelper.replyDelegateRecievedBack(session
						, pFromClient
						// Netty的数据发送结果观察者：netty的数据发送结果是通过异步通知来实现的（这就
						// 是异步编程模型，跟Nodejs的promise、Androi里的RxJava、iOS的block道理一样）
						, new MBObserver(){
							@Override
							public void update(boolean receivedBackSendSucess, Object extraObj)
							{
								if(receivedBackSendSucess)
									logger.debug("[IMCORE-本机QoS！]【QoS_应答_C2S】向"+pFromClient.getFrom()+"发送"+pFromClient.getFp()
											+"的应答包成功了,from="+pFromClient.getTo()+".");
							}
						}
				);
				//------------------------------------------------------------------------------ [1]代码与[2]处相同的哦 E

				// 此包重复，不需要通知应用层收到该包了，直接返回
				return;
			}
			
			// ## Bug FIX: 20170215 by jackjiang - 解决了不能触发回调onTransBuffer_CallBack的问题
			//------------------------------------------------------------------------------ [2]代码与[1]处相同的哦 S
			// 【【QoS机制2/4步：将收到的包存入QoS接收方暂存队列中（用于防QoS消息重复）】】
			// @see 客户端LocalUDPDataReciever中的第1/4和第4/4步相关处理
			QoS4ReciveDaemonC2S.getInstance().addRecieved(pFromClient);
			// 【【QoS机制3/4步：回应答包】】
			// @see 客户端LocalUDPDataReciever中的第1/4和第4/4步相关处理
			// 给发送者回一个“收到”应答包(发送C2S模式的应答包)
			LocalSendHelper.replyDelegateRecievedBack(session
					, pFromClient
					// Netty的数据发送结果观察者：netty的数据发送结果是通过异步通知来实现的（这就
					// 是异步编程模型，跟Nodejs的promise、Androi里的RxJava、iOS的block道理一样）
					, new MBObserver(){
						@Override
						public void update(boolean receivedBackSendSucess, Object extraObj)
						{
							if(receivedBackSendSucess)
								logger.debug("[IMCORE-本机QoS！]【QoS_应答_C2S】向"+pFromClient.getFrom()+"发送"+pFromClient.getFp()
										+"的应答包成功了,from="+pFromClient.getTo()+".");
						}
					}
			);
			//------------------------------------------------------------------------------ [2]代码与[1]处相同的哦 E
		}

		// 进入业务处理回调（processedOK返回值目前尚未有用到，目前作为保留参数以后试情况再行定义和使用）
		boolean processedOK = this.serverCoreHandler.getServerEventListener().onTransBuffer_CallBack(
				pFromClient.getTo(), pFromClient.getFrom(), pFromClient.getDataContent()
				, pFromClient.getFp(), pFromClient.getTypeu(), session);
	}
	
	/**
	 * 处理来自客户端的各类ACK消息应答包。
	 * 
	 * @param pFromClient
	 * @param remoteAddress
	 * @throws Exception
	 */
	public void processACK(final Protocal pFromClient, final String remoteAddress) throws Exception
	{
		// 【C2S数据】客户端发给服务端的ACK应答包（即S2C模式下的应答）
		if("0".equals(pFromClient.getTo()))
		{
			// 应答包的消息内容即为之前收到包的指纹id
			String theFingerPrint = pFromClient.getDataContent();
			logger.warn("[IMCORE-本机QoS！]【QoS机制_S2C】收到接收者"+pFromClient.getFrom()+"回过来的指纹为"+theFingerPrint+"的应答包.");

			// 将收到的应答事件通知事件处理者
			if(this.serverCoreHandler.getServerMessageQoSEventListener() != null)
				this.serverCoreHandler.getServerMessageQoSEventListener().messagesBeReceived(theFingerPrint);

			// 【【S2C模式下的QoS机制4/4步：收到应答包时将包从发送QoS队列中删除】】
			QoS4SendDaemonS2C.getInstance().remove(theFingerPrint);
		}
		// 【C2C数据】客户端发给客户端的ACK应答
		else
		{
			// TODO just for DEBUG
			OnlineProcessor.getInstance().__printOnline();
			
			// 应答包的消息内容即为之前收到包的指纹id
			final String theFingerPrint = pFromClient.getDataContent();

			// true表示此包是之前由服务端桥接代发消息的应答包
			boolean isBridge = pFromClient.isBridge();
			
			// ** 这是一个 桥接模式的应答包（单机时，C2C应答不需以下代码，以下代码专为【按照第一阶段的异构通信算法】而加）
			// 【注意】桥接模式下代发包的应答包存在一个例外：那就是当web端发过来的消息，代发后
			//        本应答包到达服务端时，app端的该发起用户名已在线（可能是web端和app端重复登陆情况）
			//        ，那就会导致服务端代码的包还会发生重发（因为应答被app端的在线用户收到而非代发服务端本身）
			//        ，所以更好的处理方式是，在C2C模式下，收到应答时也应考虑尝试从send队列中删除一次（虽然可能
			//        确实不是代码模式），删除时的性能消耗总比每次要等包重传完成要好吧(如果桥接qos队列不大的话
			//        理论上不会有明显性能下降)！
			if(isBridge)
			{
				logger.debug("[IMCORE-桥接QoS！]【QoS机制_S2C】收到接收者"+pFromClient.getFrom()+"回过来的指纹为"+theFingerPrint+"的应答包.");

				// 如果有必要，可以将收到的应答事件通知事件处理者哦

				// 【【S2C[桥接]模式下的QoS机制4/4步：收到应答包时将包从发送QoS队列中删除】】
				QoS4SendDaemonB2C.getInstance().remove(theFingerPrint);
			}
			// ** 这是一个本机用户的ACK直接转发给被接收者就行了
			else
			{
				// Netty的数据发送结果观察者：netty的数据发送结果是通过异步通知来实现的（这就
				// 是异步编程模型，跟Nodejs的promise、Androi里的RxJava、iOS的block道理一样）
				MBObserver sendResultObserver = new MBObserver(){
					@Override
					public void update(boolean _sendOK, Object extraObj)
					{
						logger.debug("[IMCORE-本机QoS！]【QoS机制_C2C】"+pFromClient.getFrom()+"发给"+pFromClient.getTo()
								+"的指纹为"+theFingerPrint+"的应答包已成功转发？"+_sendOK);
					}
				};
				//修改属性给发送者回执
				// 发送应答包（给消息接收者）
				LocalSendHelper.sendData(pFromClient, sendResultObserver);
				System.err.println("发送应答包（给消息接收者）");
				/**
				 * 将对方收到的消息回执，将离线专题改为正常
				 */
				logger.info("[IMCORE-netty] << 收到客户端"+remoteAddress+"，消息类型："+pFromClient.getType()+"，的消息:"+pFromClient.toGsonString());

				String fingerPrintStr=pFromClient.getDataContent();
				MessageService.ser.callBackOfflineMessage(fingerPrintStr);
				
			}
		}
	}
	
	
	
	
	
	
	/**
	 * 处理来自客户端的登陆请求。
	 * 
	 * @param session
	 * @param pFromClient
	 * @param remoteAddress
	 * @throws Exception
	 */
	public void processLogin(final Channel session, final Protocal pFromClient, final String remoteAddress) throws Exception
	{
		final PLoginInfo loginInfo = ProtocalFactory.parsePLoginInfo(pFromClient.getDataContent());
		logger.info("[IMCORE]>> 客户端"+remoteAddress+"发过来的登陆信息内容是：loginInfo="
				+loginInfo.getLoginUserId()+"|getToken="+loginInfo.getLoginToken());
		
		//##          解决在某些极端情况下由于Java PC客户端程序的不合法数据提交而导致登陆数据处理流程发生异常。
		
		if(loginInfo == null || loginInfo.getLoginUserId() == null)
		{
			logger.warn("[IMCORE]>> 收到客户端"+remoteAddress
					+"登陆信息，但loginInfo或loginInfo.getLoginUserId()是null，登陆无法继续[loginInfo="+loginInfo
					+",loginInfo.getLoginUserId()="+loginInfo.getLoginUserId()+"]！");
			return;
		}
		// 开始回调
		if(serverCoreHandler.getServerEventListener() != null)
		{
			int code1 = OnlineProcessor.getInstance().isOkLogin(loginInfo.getLoginUserId(), loginInfo.getExtra(), session);
			// ** 先检查看看该会话的用户是否已经登陆
			// 是否已经登陆（目前只要会话中存放有user_id就被认为该用户已经登陆：会话
			// 还在在线列表中即意味着与客户端的session是处活性状态，所以借user_id来
			// 判定在线更严谨也确实是合理的）
			boolean alreadyLogined = OnlineProcessor.isLogined(session);//(_try_user_id != -1);
			// 该会话对应的用户已经登陆：此种情况目前还是让它再次走登陆流程吧，测试期观察它会不会导致bug即可
			// 【理论上出现这种情况的可能是】：当用户在会话有效期内程序非正常退出（如崩溃等））后，
			//								又在很短的时间内再次登陆！
			if(code1 != -1) {
				if(alreadyLogined)
				{
					logger.debug("[IMCORE]>> 【注意】客户端"+remoteAddress+"的会话正常且已经登陆过，而此时又重新登陆：getLoginName="
							+loginInfo.getLoginUserId()+"|getLoginPsw="+loginInfo.getLoginToken());
					
					// Netty的数据发送结果观察者：netty的数据发送结果是通过异步通知来实现的（这就
					// 是异步编程模型，跟Nodejs的promise、Androi里的RxJava、iOS的block道理一样）
					MBObserver retObserver = new MBObserver(){
						@Override
						public void update(boolean _sendOK, Object extraObj)
						{
							if(_sendOK)
							{
								//----------------------------------------------------------------------- [1] 代码同[2] START
								// 将用户登陆成功后的id暂存到会话对象中备用
//							session.setAttribute(OnlineProcessor.USER_ID_IN_SESSION_ATTRIBUTE, loginInfo.getLoginUserId());
								session.attr(OnlineProcessor.USER_ID_IN_SESSION_ATTRIBUTE_ATTR).set(loginInfo.getLoginUserId());
								
								// 将用户信息放入到在线列表中（理论上：每一个存放在在线列表中的session都对应了user_id）
								OnlineProcessor.getInstance().putUser(loginInfo.getLoginUserId(), session);
							
								// 重复登陆则至少回调：成功登陆了（保证通知给在线好友我的在线状态，之前基于性能考虑，想
								// 让重复登陆就不用再通知好友了，但实际情况是因丢包等因素的存在，极端情况下好友可能永远
								// 也收不到上线通知了，目前在没有质量保证的前提下，还是损失点性能至少保证正确性吧！）
								serverCoreHandler.getServerEventListener().onUserLoginAction_CallBack(
										loginInfo.getLoginUserId(), loginInfo.getExtra(), session);
								//----------------------------------------------------------------------- [1] 代码同[2] END
							}
							else
							{
								logger.warn("[IMCORE]>> 发给客户端"+remoteAddress+"的登陆成功信息发送失败了！");
							}
						}
					};
					
					// 【1】直接将登陆反馈信息回馈给客户端而不用再走完整的登陆流程（包括通知好友上线等），
					// 之所以将登陆反馈信息返回的目的是让客户端即时更新上线状态，因为重复登陆的原因
					// 可能是在于客户端之前确实是因某种原因短时断线了（而服务端的会话在如此短的时间内还没在
					// 关闭），那么此登陆反馈消息的返回有助于纠正此时间段内可能的好友状态的更新（上、下线等）
					// 因为此时间虽短，但理论上可以发生任何事情哦！
					// 【2】为何不干脆再走一遍登陆流程呢？这样启不是在保证该用户登陆数据一致性
					//      上更保险，而不是像现在这样直接利用上次登陆的数据（理论上如果客户端
					//      在此时间段内改了loginName的话则就真的不一致了，理论上可能发生，现
					//      现实不太可能，即使出现也无太大问题）。总的一句话，就是为了避免完整
					//      登陆过程中需要产生的一些数据查询、网络交互等，从而在大并发的情况下
					//      能尽可能地提升性能
					LocalSendHelper.sendData(session
							, ProtocalFactory.createPLoginInfoResponse(code1, loginInfo.getLoginUserId()), retObserver);
				}
				// 新登陆的用户
				else
				{
					int code = serverCoreHandler.getServerEventListener().onVerifyUserCallBack(
							loginInfo.getLoginUserId(), loginInfo.getLoginToken(), loginInfo.getExtra(), session);
					// 登陆验证成功
					if(code == 0 )
					{
						// Netty的数据发送结果观察者：netty的数据发送结果是通过异步通知来实现的（这就
						// 是异步编程模型，跟Nodejs的promise、Androi里的RxJava、iOS的block道理一样）
						MBObserver sendResultObserver = new MBObserver(){
							@Override
							public void update(boolean __sendOK, Object extraObj)
							{
								if(__sendOK)
								{
									//----------------------------------------------------------------------- [2] 代码同[1] START
									// 将用户登陆成功后的id暂存到会话对象中备用
//								session.setAttribute(OnlineProcessor.USER_ID_IN_SESSION_ATTRIBUTE, loginInfo.getLoginUserId());
									session.attr(OnlineProcessor.USER_ID_IN_SESSION_ATTRIBUTE_ATTR).set(loginInfo.getLoginUserId());
									
									// 将用户信息放入到在线列表中（理论上：每一个存放在在线列表中的session都对应了user_id）
									OnlineProcessor.getInstance().putUser(loginInfo.getLoginUserId(),  session);
									
									// 回调：成功登陆了
									serverCoreHandler.getServerEventListener()
									.onUserLoginAction_CallBack(loginInfo.getLoginUserId(), loginInfo.getExtra(), session);
									//----------------------------------------------------------------------- [2] 代码同[1] START
								}
								else
									logger.warn("[IMCORE]>> 发给客户端"+remoteAddress+"的登陆成功信息发送失败了！");
								
							}
						};
						
						// 将登陆反馈信息回馈给客户端
						LocalSendHelper.sendData(session
								, ProtocalFactory.createPLoginInfoResponse(code1, loginInfo.getLoginUserId())
								, sendResultObserver);
					}
					// 登陆验证失败！
					else
					{
						// 将登陆错误信息回馈给客户端
						LocalSendHelper.sendData(session, ProtocalFactory.createPLoginInfoResponse(code1, "-1"), null);
					}
				}
			}else {
				LocalSendHelper.sendData(session, ProtocalFactory.createPLoginInfoResponse(code1, "-1"), null);
			}
		}
		else
		{
			logger.warn("[IMCORE]>> 收到客户端"+remoteAddress+"登陆信息，但回调对象是null，没有进行回调.");
		}
	}

	/**
	 * 处理来自客户端的心跳包。
	 * 
	 * @param session
	 * @param pFromClient
	 * @param remoteAddress
	 * @throws Exception
	 */
	public void processKeepAlive(Channel session, Protocal pFromClient
			, String remoteAddress) throws Exception
	{
		String userId = OnlineProcessor.getUserIdFromSession(session);
		if(userId != null)
		{
			// 给用户发回心跳响应包
			LocalSendHelper.sendData(ProtocalFactory.createPKeepAliveResponse(userId), null);
		}
		else
		{
			logger.warn("[IMCORE]>> Server在回客户端"+remoteAddress+"的响应包时，调用getUserIdFromSession返回null，用户在这一瞬间掉线了？！");
		}
	}
}
