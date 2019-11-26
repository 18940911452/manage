package wg.openmob.mobileimsdk.server.utils;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;

import java.net.SocketAddress;

import wg.openmob.mobileimsdk.server.handler.ServerCoreHandler;
import wg.openmob.mobileimsdk.server.handler.ServerLauncher;
import wg.openmob.mobileimsdk.server.processor.OnlineProcessor;
import wg.openmob.mobileimsdk.server.protocal.CharsetHelper;
import wg.openmob.mobileimsdk.server.protocal.Protocal;
import wg.openmob.mobileimsdk.server.protocal.ProtocalFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 服务端实用方法工具箱。
 * 
 * @version 1.0
 * @since 3.1
 */
public class ServerToolKits
{
	private static Logger logger = LoggerFactory.getLogger(ServerCoreHandler.class);  
	
    /**
     * 设置MobileIMSDK即时通讯核心框架预设的敏感度模式。
     * <p>
     * 请在 {@link #startup()}方法被调用前被设置，否则将不起效.
     * <p>
     * <b>重要说明：</b><u>服务端本模式的设定必须要与客户端的模式设制保持一致</u>，否则
     * 可能因参数的不一致而导至IM算法的不匹配，进而出现不可预知的问题。
     * 
     * @param mode 预设的模感度类型
	 * @see SenseMode
     */
    public static void setSenseMode(SenseMode mode)
    {
    	int expire = 0;
    	
    	switch(mode)
    	{
    		case MODE_3S:
    			// 误叛容忍度为丢3个包
    			expire = 3 * 3 + 1;
    			break;
    		case MODE_10S:
    			// 误叛容忍度为丢2个包
    			expire = 10 * 2 + 1;
        		break;
    		case MODE_30S:
    			// 误叛容忍度为丢2个包
    			expire = 30 * 2 + 2;
        		break;
    		case MODE_60S:
    			// 误叛容忍度为丢2个包
    			expire = 60 * 2 + 2;
        		break;
    		case MODE_120S:
    			// 误叛容忍度为丢2个包
    			expire = 120 * 2 + 2;
        		break;
    	}
    	
    	if(expire > 0)
    		ServerLauncher.SESION_RECYCLER_EXPIRE = expire;
    }
    
	// 注意：InetSocketAddress.getHostName()是通过Ip获得主机名，非常耗时，首次执行时通常耗时10秒左右
	// 而且也没有必要，一定要注意避免使用本方法。
    /**
     * 获取对应Session的信息（如session中存放的user_id、登陆名、ip地址和端口等）。
     * <p>
     * 本方法目前仅用于Debug时。
     * 
     * @param session 目标会话对象引用
     * @return session信息内容的文本
     */
	public static String clientInfoToString(Channel session)
	{
		SocketAddress remoteAddress = session.remoteAddress();
		String s1 = remoteAddress.toString();
		StringBuilder sb = new StringBuilder()
		.append("{uid:")
		.append(OnlineProcessor.getUserIdFromSession(session))
		.append("}")
		.append(s1);
		return sb.toString();
	}
	
	/**
	 * 从Netty的数据缓冲对象中解析出MobileIMSDK的完整协议内容（Protocal对象的JSON表示形式）。
	 * <p>
	 * <font color="red">本方法将由MobileIMSDK框架内部算法按需调用，目前不建议也不需要开发者调用。</font>
	 * 
	 * @param buffer Netty的数据缓冲对象
	 * @return 解析后的MobileIMSDK的完整协议内容（Protocal对象的JSON表示形式）
	 * @throws Exception 解析出错则抛出本异常
	 * @see {@link IoBuffer#getString(java.nio.charset.CharsetDecoder)}
	 */
	public static String fromIOBuffer_JSON(ByteBuf buffer) throws Exception 
	{
//		String jsonStr = buffer.getString(CharsetHelper.decoder);
		// 读取收到的数据
		byte[] req = new byte[buffer.readableBytes()];
		// 把数据读取到byte[]中  
		buffer.readBytes(req);
		String jsonStr = new String(req, CharsetHelper.DECODE_CHARSET);
		
//		logger.debug("[IMCORE]>> 【收到数据长度】"+jsonStr.length()+", 内容："+jsonStr+".");
		return jsonStr;
	}
	
	/**
	 * 从MINA的原始数据对象中解析出MobileIMSDK的完整协议内容（Protocal对象）。
	 * <p>
	 * <font color="red">本方法将由MobileIMSDK框架内部算法按需调用，目前不建议也不需要开发者调用。</font>
	 * 
	 * @param buffer Netty的数据缓冲对象
	 * @return 解析后的MobileIMSDK的完整协议内容（Protocal对象）
	 * @throws Exception 解析出错则抛出本异常
	 * @see #fromIOBuffer_JSON(IoBuffer)
	 */
	public static Protocal fromIOBuffer(ByteBuf buffer) throws Exception 
	{
//		return new Gson().fromJson(fromIOBuffer_JSON(buffer), Protocal.class);
		return ProtocalFactory.parse(fromIOBuffer_JSON(buffer), Protocal.class);
	}
    
    /**
     * MobileIMSDK即时通讯核心框架预设的敏感度模式.
     * <p>
     * 对于服务端而言，此模式决定了用户在非正常退出、心跳丢包、网络故障等情况下
     * 被判定为已下线的超时时长，原则上超敏感客户端的体验越好。
     * <p>
     * <b>重要说明：</b><u>服务端本模式的设定必须要与客户端的模式设制保持一致</u>，否则
     * 可能因参数的不一致而导至IM算法的不匹配，进而出现不可预知的问题。
     * 
     * @version 2.1
     */
    public enum SenseMode
    {
    	/** 
    	 * 对应于客户端的3秒心跳模式：此模式的用户非正常掉线超时时长为“3 * 3 + 1”秒。
    	 * <p>
    	 * 客户端心跳丢包容忍度为3个包。此模式为当前所有预设模式中体验最好，但
    	 * 客户端可能会大幅提升耗电量和心跳包的总流量。 
    	 */
    	MODE_3S,
    	
    	/** 
    	 * 对应于客户端的10秒心跳模式：此模式的用户非正常掉线超时时长为“10 * 2 + 1”秒。 
    	 * <p>
    	 * 客户端心跳丢包容忍度为2个包。
    	 */
    	MODE_10S,
    	
    	/** 
    	 * 对应于客户端的30秒心跳模式：此模式的用户非正常掉线超时时长为“30 * 2 + 2”秒。
    	 * <p>
    	 * 客户端心跳丢包容忍度为2个包。
    	 */
    	MODE_30S,
    	
    	/** 
    	 * 对应于客户端的60秒心跳模式：此模式的用户非正常掉线超时时长为“60 * 2 + 2”秒。
    	 * <p>
    	 * 客户端心跳丢包容忍度为2个包。
    	 */
    	MODE_60S,
    	
    	/** 
    	 * 对应于客户端的120秒心跳模式：此模式的用户非正常掉线超时时长为“120 * 2 + 2”秒。 
    	 * <p>
    	 * 客户端心跳丢包容忍度为2个包。
    	 */
    	MODE_120S
    }
}
