package wg.openmob.mobileimsdk.server.protocal;

import wg.openmob.mobileimsdk.server.protocal.c.PKeepAlive;
import wg.openmob.mobileimsdk.server.protocal.c.PLoginInfo;
import wg.openmob.mobileimsdk.server.protocal.s.PErrorResponse;
import wg.openmob.mobileimsdk.server.protocal.s.PKeepAliveResponse;
import wg.openmob.mobileimsdk.server.protocal.s.PLoginInfoResponse;

import com.google.gson.Gson;

/**
 * MibileIMSDK框架的协议工厂类。
 * <p>
 * 理论上这些协议都是即时通讯框架内部要用到的，上层应用可以无需理解和理会之。
 * 
 * @version 1.0
 */
public class ProtocalFactory
{
	private static String create(Object c)
	{
		return new Gson().toJson(c);
	}
	
	/**
	 * 将JSON文本反射成Java对象。
	 * <p>
	 * <b>本方法主要由MobileIMSDK框架内部使用。</b>
	 * 
	 * @param fullProtocalJASOnBytes 对象的json（byte数组组织形式）
	 * @param len bye数组有效数据长度
	 * @param clazz 要反射成的对象
	 * @return 反射完成的Java对象
	 * @see #parse(String, Class)
	 */
	public static <T> T parse(byte[] fullProtocalJASOnBytes, int len, Class<T> clazz)
	{
		return parse(CharsetHelper.getString(fullProtocalJASOnBytes, len), clazz);
	}
	
	/**
	 * 将JSON文本反射成Java对象。
	 * <p>
	 * <b>本方法主要由MobileIMSDK框架内部使用。</b>
	 * 
	 * @param dataContentOfProtocal 对象json文本
	 * @param clazz 要反射成的对象
	 * @return 反射完成的Java对象
	 * @see Gson#fromJson(String, Class)
	 */
	public static <T> T parse(String dataContentOfProtocal, Class<T> clazz)
	{
		return new Gson().fromJson(dataContentOfProtocal, clazz);
	}
	
	/**
	 * 将JSON文本反射成Java对象。
	 * 
	 * @param fullProtocalJASOnBytes 对象的json（byte数组组织形式）
	 * @param len bye数组有效数据长度
	 * @return 反射完成的Java对象
	 * @see #parse(byte[], int, Class)
	 */
	public static Protocal parse(byte[] fullProtocalJASOnBytes, int len)
	{
		return parse(fullProtocalJASOnBytes, len, Protocal.class);
	}
	
	/**
	 * 创建响应客户端的心跳消息对象（该对象由服务端发出）.
	 * <p>
	 * <b>本方法主要由MobileIMSDK框架内部使用。</b>
	 * 
	 * @param to_user_id
	 * @return
	 */
	public static Protocal createPKeepAliveResponse(String to_user_id)
	{
		return new Protocal(ProtocalType.S.FROM_SERVER_TYPE_OF_RESPONSE$KEEP$ALIVE
				, create(new PKeepAliveResponse()), "0", to_user_id);
	}
	
	/**
	 * <b>本方法主要由MobileIMSDK框架内部使用。</b>
	 * 
	 * @param dataContentOfProtocal
	 * @return
	 */
	public static PKeepAliveResponse parsePKeepAliveResponse(String dataContentOfProtocal)
	{
		return parse(dataContentOfProtocal, PKeepAliveResponse.class);
	}
	
	/**
	 * 创建用户心跳包对象（该对象由客户端发出）.
	 * <p>
	 * <b>本方法主要由MobileIMSDK框架内部使用。</b>
	 * 
	 * @param from_user_id
	 * @return
	 */
	public static Protocal createPKeepAlive(String from_user_id)
	{
		return new Protocal(ProtocalType.C.FROM_CLIENT_TYPE_OF_KEEP$ALIVE
				, create(new PKeepAlive()), from_user_id, "0");
	}
	
	/**
	 * <b>本方法主要由MobileIMSDK框架内部使用。</b>
	 * 
	 * @param dataContentOfProtocal
	 * @return
	 */
	public static PKeepAlive parsePKeepAlive(String dataContentOfProtocal)
	{
		return parse(dataContentOfProtocal, PKeepAlive.class);
	}
	
	/**
	 * 创建错误响应消息对象（该对象由服务端发出）.
	 * <p>
	 * <b>本方法主要由MobileIMSDK框架内部使用。</b>
	 * 
	 * @param errorCode 错误码
	 * @param errorMsg 错误消息文本内容（本参数非必须的）
	 * @return
	 * @see ErrorCode
	 */
	public static Protocal createPErrorResponse(int errorCode, String errorMsg, String user_id)
	{
		return new Protocal(ProtocalType.S.FROM_SERVER_TYPE_OF_RESPONSE$FOR$ERROR
				, create(new PErrorResponse(errorCode, errorMsg)), "0", user_id);
	}
	
	/**
	 * 解析错误响应消息对象（该对象由客户端接收）.
	 * <p>
	 * <b>本方法主要由MobileIMSDK框架内部使用。</b>
	 * 
	 * @param dataContentOfProtocal
	 * @return
	 */
	public static PErrorResponse parsePErrorResponse(String dataContentOfProtocal)
	{
		return parse(dataContentOfProtocal, PErrorResponse.class);
	}
	
	/**
	 * 创建用户注消登陆消息对象（该对象由客户端发出）.
	 * <p>
	 * <b>本方法主要由MobileIMSDK框架内部使用。</b>
	 * 
	 * @param user_id
	 * @param loginName
	 * @return
	 */
	public static Protocal createPLoginoutInfo(String user_id)
	{
		return new Protocal(ProtocalType.C.FROM_CLIENT_TYPE_OF_LOGOUT
//				, create(new PLogoutInfo(user_id, loginName))
				, null
				, user_id, "0");
	}
	
	/**
	 * 创建用户登陆消息对象（该对象由客户端发出）.
	 * <p>
	 * <b>本方法主要由MobileIMSDK框架内部使用。</b>
	 * 
	 * @param userId 传递过来的准一id，保证唯一就可以通信，可能是登陆用户名、也可能是任意不重复的id等，具体意义由业务层决定
	 * @param token 用于身份鉴别和合法性检查的token，它可能是登陆密码，也可能是通过前置单点登陆接口拿到的token等，具体意义由业务层决定
	 * @param extra 额外信息字符串。本字段目前为保留字段，供上层应用自行放置需要的内容
	 * @return
	 */
	public static Protocal createPLoginInfo(String userId, String token, String extra)
	{
		return new Protocal(ProtocalType.C.FROM_CLIENT_TYPE_OF_LOGIN
				// 因登陆额外处理丢包逻辑，所以此包也无需QoS支持。不能支持QoS的原因
				// 是：登陆时QoS机制都还没启用呢（只在登陆成功后启用），所以此处无需设置且设置了也没有用的哦
				, create(new PLoginInfo(userId, token, extra))
//					, "-1"
					, userId
					, "0");
	}
	
	/**
	 * 解析用户登陆消息对象（该对象由服务端接收）.
	 * <p>
	 * <b>本方法主要由MobileIMSDK框架内部使用。</b>
	 * 
	 * @param dataContentOfProtocal
	 * @return
	 */
	public static PLoginInfo parsePLoginInfo(String dataContentOfProtocal)
	{
		return parse(dataContentOfProtocal, PLoginInfo.class);
	}
	
	/**
	 * 创建用户登陆响应消息对象（该对象由服务端发出）.
	 * <p>
	 * <b>本方法主要由MobileIMSDK框架内部使用。</b>
	 * 
	 * @param code
	 * @param user_id
	 * @return
	 */
	public static Protocal createPLoginInfoResponse(int code
			, String user_id)
	{
		return new Protocal(ProtocalType.S.FROM_SERVER_TYPE_OF_RESPONSE$LOGIN
				, create(new PLoginInfoResponse(code
//						, user_id
						))
				, "0"
				, user_id // changed -1 to user_id: modified by author 20150911 -> 目的是让登陆响应包能正常支持QoS机制
				, true, Protocal.genFingerPrint()// add QoS support by author 20150911
				); // 此包由服务端发出，默认已支持QoS
	}
	
	/**
	 * 接收用户登陆响应消息对象（该对象由客户端接收）.
	 * <p>
	 * <b>本方法主要由MobileIMSDK框架内部使用。</b>
	 * 
	 * @param dataContentOfProtocal
	 * @return
	 */
	public static PLoginInfoResponse parsePLoginInfoResponse(String dataContentOfProtocal)
	{
		return parse(dataContentOfProtocal, PLoginInfoResponse.class);
	}
	
	/**
	 * 通用消息的Protocal对象新建方法（typeu字段默认-1）。
	 * <p>
	 * <font color="#0000ff"><b>友情提示：</b></font>为了您能定义更优雅的IM协议，
	 * 建议优先使用typeu定义您的协议类型，而不是使用默认的-1。
	 * 
	 * @param dataContent 要发送的消息内容
	 * @param from_user_id 发送人的user_id
	 * @param to_user_id 接收人的user_id
	 * @param QoS 是否需要QoS支持，true表示需要，否则不需要
	 * @param fingerPrint 消息指纹特征码，为null则表示由系统自动生成指纹码，否则使用本参数指明的指纹码
	 * @return 新建的Protocal对象
	 */
	public static Protocal createCommonData(String dataContent, String from_user_id, String to_user_id
			, boolean QoS, String fingerPrint)
	{
		return createCommonData(dataContent, from_user_id, to_user_id, QoS, fingerPrint, -1);
	}
	
	/**
	 * 通用消息的Protocal对象新建方法。
	 * 
	 * @param dataContent 要发送的消息内容
	 * @param from_user_id 发送人的user_id
	 * @param to_user_id 接收人的user_id
	 * @param QoS 是否需要QoS支持，true表示需要，否则不需要
	 * @param fingerPrint 消息指纹特征码，为null则表示由系统自动生成指纹码，否则使用本参数指明的指纹码
	 * @param typeu 应用层专用字段——用于应用层存放聊天、推送等场景下的消息类型，不需要设置时请填-1即可
	 * @return 新建的Protocal对象
	 */
	public static Protocal createCommonData(String dataContent, String from_user_id, String to_user_id
			, boolean QoS, String fingerPrint, int typeu)
	{
		return new Protocal(ProtocalType.C.FROM_CLIENT_TYPE_OF_COMMON$DATA
				, dataContent, from_user_id, to_user_id, QoS, fingerPrint, typeu);
	}
	
	/**
	 * 客户端from_user_id向to_user_id发送一个QoS机制中需要的“收到消息应答包”(默认bridge标认为false).
	 * <p>
	 * <b>本方法主要由MobileIMSDK框架内部使用。</b>
	 * 
	 * @param from_user_id 发起方
	 * @param to_user_id 接收方
	 * @param recievedMessageFingerPrint 已收到的消息包指纹码
	 * @return
	 */
	public static Protocal createRecivedBack(String from_user_id, String to_user_id
			, String recievedMessageFingerPrint)
	{
		return createRecivedBack(from_user_id, to_user_id, recievedMessageFingerPrint, false);
	}
	
	/**
	 * 客户端from_user_id向to_user_id发送一个QoS机制中需要的“收到消息应答包”.
	 * <p>
	 * <b>本方法主要由MobileIMSDK框架内部使用。</b>
	 * 
	 * @param from_user_id 发起方
	 * @param to_user_id 接收方
	 * @param recievedMessageFingerPrint 已收到的消息包指纹码
	 * @param bridge true表示是跨机器的桥接消息ACK，否则是本机消息ACK，默认请填false
	 * @return
	 */
	public static Protocal createRecivedBack(String from_user_id, String to_user_id
			, String recievedMessageFingerPrint, boolean bridge)
	{
		Protocal p = new Protocal(ProtocalType.C.FROM_CLIENT_TYPE_OF_RECIVED
				, recievedMessageFingerPrint, from_user_id, to_user_id);// 该包当然不需要QoS支持！
		p.setBridge(bridge);
		return p;
	}
}
