package wg.openmob.mobileimsdk.server.utils;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import wg.nettime.mobileimsdk.server.bridge.QoS4SendDaemonB2C;
import wg.nettime.mobileimsdk.server.netty.MBObserver;
import wg.openmob.mobileimsdk.server.handler.ServerCoreHandler;
import wg.openmob.mobileimsdk.server.handler.ServerLauncher;
import wg.openmob.mobileimsdk.server.processor.OnlineProcessor;
import wg.openmob.mobileimsdk.server.protocal.ErrorCode;
import wg.openmob.mobileimsdk.server.protocal.Protocal;
import wg.openmob.mobileimsdk.server.protocal.ProtocalFactory;
import wg.openmob.mobileimsdk.server.qos.QoS4SendDaemonS2C;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 本类提供的是公开的数据发送方法（数据接收者仅限于本机在线用户）。
 * <p>
 * <b>注意：</b>如果未开通与Web版的互通（参见： {@link net.openmob.mobileimsdk.server.ServerLauncher#bridgeEnabled} ）
 * ，则服务端消息发送建议使用本类，否则请使用 {@link GlobalSendHelper} 。
 * 
 * @version 1.0
 * @since 3.1
 * @see net.openmob.mobileimsdk.server.ServerLauncher#bridgeEnabled
 */
public class LocalSendHelper
{
	private static Logger logger = LoggerFactory.getLogger(ServerCoreHandler.class);  
	
	/**
     * 向目标发送一条数据（默认QoS=true，typeu=-1）。
     * 
     * @param from_user_id 发送方user_id
     * @param to_user_id 接收方的user_id
     * @param dataContent 发送的文本内容
     * @param resultObserver 因netty的异步化特征，发送数据在API层也是异步的，本参数用于获得数据发送的结果通
     * 知（这是与MINA的区别之一）。服务端为了获得高并发、高性能，失去传统网络编程同步调用时编码的便利也是在所
     * 难免(再也不是直接的函数返回值了)，开发者需适应之
     * @throws Exception 发送过程中出现错误则抛出本异常
     * @see #sendData(String, String, String, boolean, String, int, MBObserver)
     * @since 2.1.4
     */
	public static void sendData(String from_user_id, String to_user_id
			, String dataContent, MBObserver resultObserver) throws Exception 
    {
    	sendData(from_user_id, to_user_id, dataContent, true, null, -1, resultObserver);
    }
	
	/**
     * 向目标发送一条数据（默认QoS=true）。
     * 
     * @param from_user_id 发送方user_id
     * @param to_user_id 接收方的user_id
     * @param dataContent 发送的文本内容
     * @param typeu 应用层专用字段——用于应用层存放聊天、推送等场景下的消息类型。注意：此值为-1时表示未定义。
	 *              MobileIMSDK_X框架中，本字段为保留字段，不参与框架的核心算法，专留作应用层自行定义和使用。
     * @param resultObserver 因netty的异步化特征，发送数据在API层也是异步的，本参数用于获得数据发送的结果通
     * 知（这是与MINA的区别之一）。服务端为了获得高并发、高性能，失去传统网络编程同步调用时编码的便利也是在所
     * 难免(再也不是直接的函数返回值了)，开发者需适应之
     * @throws Exception 发送过程中出现错误则抛出本异常
     * @see #sendData(String, String, String, boolean, String, int, MBObserver)
     * @since 2.1.4
     */
	public static void sendData(String from_user_id, String to_user_id, String dataContent
			, int typeu, MBObserver resultObserver) throws Exception 
    {
    	sendData(from_user_id, to_user_id, dataContent, true, null, typeu, resultObserver);
    }
	
	/**
     * 向目标发送一条数据。
     * 
     * @param from_user_id 发送方user_id
     * @param to_user_id 接收方的user_id
     * @param dataContent 发送的文本内容
     * @param QoS 是否需要QoS支持，true表示需要，否则不需要。当为true时系统将自动生成指纹码
     * @param typeu 应用层专用字段——用于应用层存放聊天、推送等场景下的消息类型。注意：此值为-1时表示未定义。
	 *              MobileIMSDK_X框架中，本字段为保留字段，不参与框架的核心算法，专留作应用层自行定义和使用。
     * @param resultObserver 因netty的异步化特征，发送数据在API层也是异步的，本参数用于获得数据发送的结果通
     * 知（这是与MINA的区别之一）。服务端为了获得高并发、高性能，失去传统网络编程同步调用时编码的便利也是在所
     * 难免(再也不是直接的函数返回值了)，开发者需适应之
     * @throws Exception 发送过程中出现错误则抛出本异常
     * @see #sendData(String, String, String, boolean, String, int, MBObserver)
     * @since 2.1.4
     */
	public static void sendData(String from_user_id, String to_user_id, String dataContent
			, boolean QoS, int typeu, MBObserver resultObserver) throws Exception 
    {
    	sendData(from_user_id, to_user_id, dataContent, QoS, null, typeu, resultObserver);
    }
	
	/**
     * 向目标发送一条数据（typeu=-1）。
     * 
     * @param from_user_id 发送方user_id
     * @param to_user_id 接收方的user_id
     * @param dataContent 发送的文本内容
     * @param QoS 是否需要QoS支持，true表示需要，否则不需要。当为false时fingerPrint字段值将无意义
	 * @param fingerPrint 消息指纹特征码，当QoS=true且本参数为null则表示由系统自动生成指纹码，否则使用本参数指明的指纹码
     * @param resultObserver 因netty的异步化特征，发送数据在API层也是异步的，本参数用于获得数据发送的结果通
     * 知（这是与MINA的区别之一）。服务端为了获得高并发、高性能，失去传统网络编程同步调用时编码的便利也是在所
     * 难免(再也不是直接的函数返回值了)，开发者需适应之
     * @throws Exception 发送过程中出现错误则抛出本异常
     * @see #sendData(String, String, String, boolean, String, int, MBObserver)
     * @since 2.1.4
     */
	public static void sendData(String from_user_id, String to_user_id, String dataContent
			, boolean QoS, String fingerPrint, MBObserver resultObserver) throws Exception 
    {
    	sendData(from_user_id, to_user_id, dataContent, QoS, fingerPrint, -1, resultObserver);
    }
	
	/**
     * 向目标发送一条数据。
     * 
     * @param from_user_id 发送方user_id
     * @param to_user_id 接收方的user_id
     * @param dataContent 发送的文本内容
     * @param QoS 是否需要QoS支持，true表示需要，否则不需要。当为false时fingerPrint字段值将无意义
	 * @param fingerPrint 消息指纹特征码，当QoS=true且本参数为null则表示由系统自动生成指纹码，否则使用本参数指明的指纹码
     * @param typeu 应用层专用字段——用于应用层存放聊天、推送等场景下的消息类型。注意：此值为-1时表示未定义。
	 *              MobileIMSDK_X框架中，本字段为保留字段，不参与框架的核心算法，专留作应用层自行定义和使用。
     * @param resultObserver 因netty的异步化特征，发送数据在API层也是异步的，本参数用于获得数据发送的结果通
     * 知（这是与MINA的区别之一）。服务端为了获得高并发、高性能，失去传统网络编程同步调用时编码的便利也是在所
     * 难免(再也不是直接的函数返回值了)，开发者需适应之
     * @throws Exception 发送过程中出现错误则抛出本异常
     * @see #sendData(Protocal, MBObserver)
     * @since 3.0
     */
	public static void sendData(String from_user_id, String to_user_id, String dataContent
			, boolean QoS, String fingerPrint, int typeu, MBObserver resultObserver) throws Exception 
    {
    	sendData(
    		ProtocalFactory.createCommonData(dataContent, from_user_id, to_user_id, QoS, fingerPrint, typeu)
    		, resultObserver);
    }
    
	/**
	 * 向目标发送一条数据。
	 * 
	 * @param p 要发送的内容（此对象封装了发送方user_id、接收方user_id、消息内容等）
     * @param resultObserver 因netty的异步化特征，发送数据在API层也是异步的，本参数
     * 用于获得数据发送的结果通知（这是与MINA的区别之一）。服务端为了获得高并发、高性
     * 能，失去传统网络编程同步调用时编码的便利也是在所难免(再也不是直接的函数返回值了
     * )，开发者需适应之
	 * @throws Exception 发送过程中出现错误则抛出本异常
	 * @see #sendData(io.netty.channel.Channel, Protocal, MBObserver)
	 */
    public static void sendData(Protocal p, MBObserver resultObserver) throws Exception 
    {
    	if(p != null)
    	{
    		if(!"0".equals(p.getTo()))
    			sendData(OnlineProcessor.getInstance().getOnlineSession(p.getTo()), p, resultObserver);
    		else
    		{
    			logger.warn("[IMCORE-netty]【注意】此Protocal对象中的接收方是服务器(user_id==0)（而此方法本来就是由Server调用，自已发自已不可能！），数据发送没有继续！"+p.toGsonString());

//    			return false;
    			// 通知观察者，数据发送失败
    			if(resultObserver != null)
    				resultObserver.update(false, null);
    		}
    	}
    	else
    	{
    		// 通知观察者，数据发送失败
    		if(resultObserver != null)
    			resultObserver.update(false, null);
//    		return false;
    	}
    }
    
    /**
     * 向目标发送一条数据。
     * 
     * @param session 接收者的会话对象引用
     * @param p 要发送的内容（此对象封装了发送方user_id、接收方user_id、消息内容等）
     * @param resultObserver 因netty的异步化特征，发送数据在API层也是异步的，本参数
     * 用于获得数据发送的结果通知（这是与MINA的区别之一）。服务端为了获得高并发、高性
     * 能，失去传统网络编程同步调用时编码的便利也是在所难免(再也不是直接的函数返回值了
     * )，开发者需适应之
     * @throws Exception 发送过程中出现错误则抛出本异常
     * @see io.netty.channel.ChannelFuture#writeAndFlush(Object)
     * @see MBObserver
     */
    public static void sendData(final Channel session, final Protocal p, final MBObserver resultObserver) throws Exception 
    {
    	// 要发送的目标用户的session已经不存在了
		if(session == null)
		{
			logger.info("[IMCORE-netty]toSession==null >> id="+p.getFrom()+"的用户尝试发给客户端"+p.getTo()
					+"的消息：str="+p.getDataContent()+"因接收方的id已不在线，此次实时发送没有继续(此消息应考虑作离线处理哦).");
			
			if(resultObserver != null)
				resultObserver.update(false, null);
			
		}
		else
		{
			// 要发送到的对方会话是正常状态
			if(session.isActive())
			{
		    	if(p != null)
		    	{
		    		final byte[] res = p.toBytes();
		    		
//		    		IoBuffer buf = IoBuffer.wrap(res);  
//		    		// 向客户端写数据
//		    		WriteFuture future = session.write(buf);  
//		    		// 在100毫秒超时间内等待写完成
//		    		future.awaitUninterruptibly(100);
		    		
		    		ByteBuf to = Unpooled.copiedBuffer(res);
		    		ChannelFuture cf = session.writeAndFlush(to);//.sync();
		    		
		    		// 通过异步监听来实现结果的判定：使用ChannelFutureListener是
		    		// netty的最优化方法，因为await()虽简单但它是一个阻塞的操作而且可能会发生死锁，
		    		// 而ChannelFutureListener会利于最佳的性能和资源的利用，因为它一点阻塞都没有
		    	//	 logger.warn("11111异步监听是否进来");
		    		cf.addListener(new ChannelFutureListener() {
		    			 // Perform post-closure operation
		    	         public void operationComplete(ChannelFuture future) 
		    	         {
		    	        	 
		    	        	// logger.warn("2222异步监听是否进来："+future.isSuccess());
		    	        	// The message has been written successfully
		 		    		if( future.isSuccess())
		 		    			
		 		    		{
		 		    			// logger.warn("3333进来："+future.isSuccess());
		 		    			//暂时
		 		    			/*logger.info("[IMCORE-netty] >> 给客户端："+ServerToolKits.clientInfoToString(session)
		 		    					+"的数据->"+p.toGsonString()+",已成功发出["+res.length+"].");*/
		 		    			
		 		    			// ** 如果是服务端主动发起的消息才需要加入S2C模式的发消息QoS机制
		 		    			if("0".equals(p.getFrom()))
		 		    			{
		 		    				// 【【S2C模式下的QoS机制1/4步：将包加入到发送QoS队列中】】
		 		    				// 如果需要进行QoS质量保证，则把它放入质量保证队列中供处理(已在存在于列
		 		    				// 表中就不用再加了，已经存在则意味当前发送的这个是重传包哦)
		 		    				if(p.isQoS() && !QoS4SendDaemonS2C.getInstance().exist(p.getFp()))
		 		    					QoS4SendDaemonS2C.getInstance().put(p);
		 		    			}
		 		    			// ** 通过桥接被服务端代理发出的消息也同样有它的QoS机制 (since 3.0)
		 		    			// 通过服务端发出的桥接消息from不是”0“而仍然是被代发者的username哦
		 		    			else if(p.isBridge())
		 		    			{
		 		    				// 【【S2C[桥接]模式下的QoS机制1/4步：将包加入到桥接发送QoS队列中】】
		 		    				// 如果需要进行QoS质量保证，则把它放入质量保证队列中供处理(已在存在于列
		 		    				// 表中就不用再加了，已经存在则意味当前发送的这个是重传包哦)
		 		    				if(p.isQoS() && !QoS4SendDaemonB2C.getInstance().exist(p.getFp()))
		 		    					QoS4SendDaemonB2C.getInstance().put(p);
		 		    			}
		 		    			
//		 		    			return true;
		 		    		}
		 		    		// The messsage couldn't be written out completely for some reason.
		 	    			// (e.g. Connection is closed)
		 		    		else
		 		    		{
		 		    			logger.warn("[IMCORE-netty]给客户端："+ServerToolKits.clientInfoToString(session)+"的数据->"+p.toGsonString()+",发送失败！["+res.length+"](此消息应考虑作离线处理哦).");
		 		    		}
		 		    		
		 		    		// 通知观察者，数据发送结果
		 		    		if(resultObserver != null)
	 		    				resultObserver.update(future.isSuccess(), null);
		    	         }
		    	    });
		    	}
		    	else
		    	{
		    		logger.warn("[IMCORE-netty]客户端id="+p.getFrom()+"要发给客户端"+p.getTo()+"的实时消息：str="+p.getDataContent()+"没有继续(此消息应考虑作离线处理哦).");
		    	}
			}
		}
		
		// 通知观察者，数据发送失败
//		if(resultObserver != null)
//			resultObserver.update(false, null);
//		return false;
    }
    
	/**
	 * 当服务端检测到用户尚未登陆（或登陆会话已失效时）由服务端回复给
	 * 客户端的消息。
	 * <p>
	 * <font color="red">本方法将由MobileIMSDK框架内部算法按需调用，目前不建议也不需要开发者调用。</font>
	 * 
	 * @param session 被回复的会话
	 * @param p 回复的数据包
     * @param resultObserver 因netty的异步化特征，发送数据在API层也是异步的，本参数
     * 用于获得数据发送的结果通知（这是与MINA的区别之一）。服务端为了获得高并发、高性
     * 能，失去传统网络编程同步调用时编码的便利也是在所难免(再也不是直接的函数返回值了
     * )，开发者需适应之
	 * @throws Exception 发生时出错将抛出本异常
	 * @see #sendData(io.netty.channel.Channel, Protocal, MBObserver)
	 */
	public static void replyDataForUnlogined(Channel session, Protocal p
			, MBObserver resultObserver) throws Exception
	{
		logger.warn("[IMCORE-netty]>> 客户端"+ServerToolKits.clientInfoToString(session)+"尚未登陆，"+p.getDataContent()+"处理未继续.");
		
		// 把“未登陆”错误信息反馈给客户端，并把本次发送的数据内容回给客户端！
		Protocal perror = ProtocalFactory.createPErrorResponse(
				// 将原Protocal包的JSON作为错误内容原样返回（给客户端）
				ErrorCode.ForS.RESPONSE_FOR_UNLOGIN, p.toGsonString(), "-1"); // 尚未登陆则user_id就不存在了,用-1表示吧，目前此情形下该参数无意义
		
		// 发送数据
		sendData(session, perror, resultObserver);
	}

	/**
	 * 服务端回复伪应答包（给客户端发送方）。前提是客户发送的消息包中的字段QoS=true
	 * ，否则本方法什么也不做。
	 * <p>
	 * <b>何为伪应答包？</b><br>
	 * 当客户端A发给接收方（目标接收方可能是服务器也可能是客户端B）的消息在意外情况下
	 * （B不在线，或者其它情况下没有发送成功：此时服务端会通知上层启动离线存储机制等
	 * ，总之当上层成功处理了这个消息，也算是意味着该消息成功送达（虽成了离线消息但总
	 * 归是没有丢）），且该消息有QoS保证机制，则服务端将代(如果接收方恰好就是服务器，
	 * 当然就不是"代"了)为发送一个应答包，以便告之发送方消息已经收到了（离线消息当然
	 * 也算是，只是非及时收到而已^_^）.
	 * <p>
	 * <font color="red">本方法将由MobileIMSDK框架内部算法按需调用，目前不建议也不需要开发者调用。</font>
	 * 
	 * @param session 被回复的会话
	 * @param pFromClient 客户端发过来的数据包，本方法将据此包中的from、to、fp属性进行回复
     * @param resultObserver 因netty的异步化特征，发送数据在API层也是异步的，本参数
     * 用于获得数据发送的结果通知（这是与MINA的区别之一）。服务端为了获得高并发、高性
     * 能，失去传统网络编程同步调用时编码的便利也是在所难免(再也不是直接的函数返回值了
     * )，开发者需适应之
	 * @throws Exception 当发送数据出错时将抛出本异常
	 * @see #sendData(io.netty.channel.Channel, Protocal, MBObserver)
	 */
	public static void replyDelegateRecievedBack(Channel session, Protocal pFromClient
			, MBObserver resultObserver) throws Exception
	{
		if(pFromClient.isQoS() && pFromClient.getFp() != null)
		{
			Protocal receivedBackP = ProtocalFactory.createRecivedBack(
					pFromClient.getTo()
					, pFromClient.getFrom()
					, pFromClient.getFp());

			sendData(session, receivedBackP, resultObserver);
		}
		else
		{
			logger.warn("[IMCORE-netty]收到"+pFromClient.getFrom()
					+"发过来需要QoS的包，但它的指纹码却为null！无法发伪应答包哦！");
//			return false;
			
			// 将失败的结果通知观察者
			if(resultObserver != null)
				resultObserver.update(false, null);
		}
	}
}
