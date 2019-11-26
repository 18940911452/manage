package wg.openmob.mobileimsdk.server.processor;

import io.netty.channel.Channel;
import io.netty.util.AttributeKey;
import wg.openmob.mobileimsdk.server.listener.ServerEventListenerImpl;
import wg.openmob.mobileimsdk.server.protocal.Protocal;
import wg.openmob.mobileimsdk.server.utils.GlobalSendHelper;
import wg.openmob.mobileimsdk.server.utils.LocalSendHelper;

import java.net.SocketAddress;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * MibileIMSDK的服务端用户列表管理类。
 * <p>
 * 根据全局算法约定，当user_id=0时即表示是服务器。
 * <p>
 * <b>关于用户的非正常退出问题的检查和处理机制：</b><br>
 * 利用MINA自已的recycle会话回收机制({@link ExpiringSessionRecycler)，
 * 无需单独编写SessionChecker这样的类来检查用户的非正常退出了。
 * 
 * @version 3.1
 */
public class OnlineProcessor
{
	/** 用于用户session中存放user_id的属性key */
	public final static String USER_ID_IN_SESSION_ATTRIBUTE = "__user_id__";
	
	/** 用于Netty的Channel中存取属性用的 {@link USER_ID_IN_SESSION_ATTRIBUTE} 的AttributeKey */
	public static final AttributeKey<String> USER_ID_IN_SESSION_ATTRIBUTE_ATTR = 
			AttributeKey.newInstance(USER_ID_IN_SESSION_ATTRIBUTE);
	
	public static boolean DEBUG = false;

	private static Logger logger = LoggerFactory.getLogger(OnlineProcessor.class); 
	private static OnlineProcessor instance = null;
	
	/** 用户在线列表：key=user_id、value=会话实例引用 */
	public static ConcurrentHashMap<String, Channel> onlineSessions = new ConcurrentHashMap<String, Channel>();
	
	
	static int maxSize = 200;
	public static Map<String, Long> messageMap = new LinkedHashMap<String, Long>(){
	      private static final long    serialVersionUID    = 1L;
	      @Override
	      protected boolean removeEldestEntry(java.util.Map.Entry<String, Long> pEldest) {
	          return size() > maxSize;
	      }
	  };
	/**
	 * 为了简化API调用，本方法将以单例的形式存活。
	 * 
	 * @return 本类的全局单例
	 */
	public static OnlineProcessor getInstance()
	{
		if(instance == null)
			instance = new OnlineProcessor();
		return instance;
	}
	
	private OnlineProcessor()
	{
	}
	
	/**
	 * 将用户放入在线列表。
	 * <p>
     * <b><font color="#ff0000">本方法由MobileIMSDK内部决定如
     * 何调用，不建议开发者调用此方法！</font></b>
	 * 
	 * @param user_id 用户的user_id
	 * @param extra 
	 * @param session 该用户对应的 Netty UDP Channel 引用
	 * @param loginName 用户登陆账号
	 */
	public void putUser(String user_id, Channel session)
	{
		if(onlineSessions.containsKey(user_id))
		{
			logger.debug("[IMCORE]【注意】用户id="+user_id+"已经在在线列表中了，session也是同一个吗？"
					+(onlineSessions.get(user_id).hashCode() == session.hashCode()));
			
			// TODO 同一账号的重复登陆情况可在此展开处理逻辑
		}
		
		// 将用户加入到在线列表中
		onlineSessions.put(user_id, session);
		
		__printOnline();// just for debug
	}
	public int isOkLogin(String user_id, String extra, Channel session)
	{
		Long extra1 = null;
		int code = 0;
		if(extra != null) {
			 extra1 = Long.valueOf(extra);
		}
		System.err.println(session);
		if(onlineSessions.containsKey(user_id))
		{//登录成功后放入MAP session集合
			String wsid = user_id;
			Set<Channel> set = ServerEventListenerImpl.rooms.get(wsid);
			if(set!=null){
				if(set.size()>0) {
						 if((extra1-messageMap.get(user_id)) == 0) {
						   code= 0;
						 }else if(extra1  - messageMap.get(user_id) > 0){
/*							 Protocal Protocal=new Protocal(2, "0", null, user_id, true, "0"+extra+user_id, 44);
								System.err.println("set不为null----------------------------set个数：" + set.size());
								     Channel s = onlineSessions.get(user_id);
									System.err.println(s );
									System.err.println(session );
									System.err.println(s == session);
									System.err.println("isOpen--------------" + s);
									     if(s.hashCode() != session.hashCode() ) {
									    	 String remoteAddress = clientInfoToString(s,"0");
									    		 try {
									    			 System.err.println(s);
									    			// LocalSendHelper.sendData("0", user_id, "44", true, "0"+extra+user_id, 44, null);
									    			 LocalSendHelper.sendData(session, Protocal, null);
									    			 GlobalSendHelper.ser.sendDataC2C(null, s, Protocal, remoteAddress, LogicProcessor.serverCoreHandler);
									    		 } catch (Exception e) {
									    			 // TODO Auto-generated catch block
									    			 e.printStackTrace();
									    		 }
									    	
									     }
*/								messageMap.put(user_id.toString(),extra1);
							    code= 0;
						 }else {
							 code= -1;
						 }
				}
			}else {
				messageMap.put(user_id.toString(),extra1);
				 code= 0;
			}
		}
		
		// 将用户加入到在线列表中
		if(code != -1) {
			messageMap.put(user_id, extra1);
		}
		return code;
	}
	
	/**
	 * 打印在线用户列。
	 * <p>
	 * 本方法仅应用于DEBUG时，当在线用户数量众多时，本方法会影响性能。
	 */
	public void __printOnline()
	{
		logger.debug("【@】当前在线用户共("+onlineSessions.size()+")人------------------->");
		if(true)
		{
			for(String key : onlineSessions.keySet())
				logger.debug(" 【#】   当前在线用户 > user_id="+key+",session="+onlineSessions.get(key).remoteAddress());
		}
	}
	
	/**
	 * 将用户从在线列表中移除.
	 * <p>
     * <b><font color="#ff0000">本方法由MobileIMSDK内部决定如
     * 何调用，不建议开发者调用此方法！</font></b>
     * 
	 * @param user_id 用户的user_id
	 * @return true表示已成功remove，否则表示没有此user_id对应的在线信息
	 */
	public boolean removeUser(String user_id)
	{
		synchronized(onlineSessions)
		{
			if(!onlineSessions.containsKey(user_id))
			{
				logger.warn("[IMCORE]！用户id="+user_id+"不存在在线列表中，本次removeUser没有继续.");
				__printOnline();// just for debug
				return false;
			}
			else
				return (onlineSessions.remove(user_id) != null);
		}
	}
	
	/**
	 * 根据user_id获得该在线用户对应的 Netty UDP Channel 会话实例句柄。
	 * 
	 * @param user_id 用户的user_id
	 * @return 存在该在线用户则成功返回，否则返回null
	 */
	public Channel getOnlineSession(String user_id)
	{
//		logger.debug("======>user_id="+user_id+"在列表中吗？"+usersBySession.containsKey(user_id));
//		__printOnline();
		return onlineSessions.get(user_id);
	}
	
	/**
	 * 返回用户在线列表：key=user_id、value=会话实例引用。
	 * 
	 * @return 在线列表实例引用
	 */
	public ConcurrentHashMap<String, Channel> getOnlineSessions()
	{
		return onlineSessions;
	}

	//------------------------------------------------------------------ 实用方法
	/**
	 * 该用户会话是否是合法的（已登陆认证）。
	 * <p>
	 * 根据MINA的原理，任何请求都会建立会话，但会话是否是合法的，则需根据
	 * 存放于其会话中的登陆属性来校验（MobileIMSDK中，已经过登陆认证的会话
	 * ，会在其session中存放user_id，判断是否已设置user_id即可认定是否是
	 * 合法的会话）。
	 * 
	 * @param session 用户会话引用
	 * @return true表示已经成功登陆认证过，否则表示未登陆过（非法请求）
	 */
	public static boolean isLogined(Channel session)
	{
		return session != null && getUserIdFromSession(session) != null;
	}
	
	/**
	 * 尝试取出存放于用户会话中的user_id.通常只有已成功登陆验证后的用户
	 * 会话中才会存放它对应的user_id.
	 * 
	 * @param session 用户会话引用
	 * @return 如果找到该属性则返回指定session的user_id，否则返回null
	 */
	public static String getUserIdFromSession(Channel session)
	{
		Object attr = null;
		if(session != null)
		{
			attr = session.attr(USER_ID_IN_SESSION_ATTRIBUTE_ATTR).get();
			if(attr != null)
				return (String)attr;
		}
		return null;
	}
	
	/**
	 * 指定用户ID的用户是否在线。
	 * 
	 * @param userId 用户名ID（就是用户登陆名）
	 * @return true表示该用户在线，否则不在线
	 * @since 3.0
	 */
	public static boolean isOnline(String userId)
	{
		return OnlineProcessor.getInstance().getOnlineSession(userId) != null;
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
