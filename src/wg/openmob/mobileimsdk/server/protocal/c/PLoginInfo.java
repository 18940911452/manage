package wg.openmob.mobileimsdk.server.protocal.c;

/**
 * 登陆信息DTO类.
 * 
 * @version 1.0
 * @since 3.0
 */
public class PLoginInfo
{
	/** 
	 * 登陆时提交的用户名：此用户名对框架来说可以随意，具体意义由上层逻辑决即可。
	 *  */
	private String loginUserId = null;
	/** 登陆时提交的密码：此密码对框架来说可以随意，具体意义由上层逻辑决即可 */
	private String loginToken = null;
	
	/**
	 * 额外信息字符串。本字段目前为保留字段，供上层应用自行放置需要的内容。
	 * @since 2.1.6 */
	private String extra = null;
	
	/**
	 * 构造方法。
	 * 
	 * @param userId 传递过来的准一id，保证唯一就可以通信，可能是登陆用户名、也可能是任意不重复的id等，具体意义由业务层决定
	 * @param token 用于身份鉴别和合法性检查的token，它可能是登陆密码，也可能是通过前置单点登陆接口拿到的token等，具体意义由业务层决定
	 */
	public PLoginInfo(String loginUserId, String loginToken)
	{
		this(loginUserId, loginToken, null);
	}
	
	/**
	 * 构造方法。
	 * 
	 * @param userId 传递过来的准一id，保证唯一就可以通信，可能是登陆用户名、也可能是任意不重复的id等，具体意义由业务层决定
	 * @param token 用于身份鉴别和合法性检查的token，它可能是登陆密码，也可能是通过前置单点登陆接口拿到的token等，具体意义由业务层决定
	 * @param extra 额外信息字符串。本字段目前为保留字段，供上层应用自行放置需要的内容
	 */
	public PLoginInfo(String loginUserId, String loginToken, String extra)
	{
		this.loginUserId = loginUserId;
		this.loginToken = loginToken;
		this.extra = extra;
	}
	
	/**
	 * 返回登陆时提交的准一id，保证唯一就可以通信，可能是登陆用户名、也可
	 * 能是任意不重复的id等，具体意义由业务层决定。
	 * 
	 * @return
	 */
	public String getLoginUserId()
	{
		return loginUserId;
	}

	/**
	 * 设置登陆时提交的准一id，保证唯一就可以通信，可能是登陆用户名、也可
	 * 能是任意不重复的id等，具体意义由业务层决定。
	 * 
	 * @param loginUserId
	 */
	public void setLoginUserId(String loginUserId)
	{
		this.loginUserId = loginUserId;
	}

	/**
	 * 返回登陆时提交的用于身份鉴别和合法性检查的token，它可能是登陆密码，也可能是
	 * 通过前置单点登陆接口拿到的token等，具体意义由业务层决定。
	 * 
	 * @return
	 */
	public String getLoginToken()
	{
		return loginToken;
	}

	/**
	 * 设置登陆时提交的用于身份鉴别和合法性检查的token，它可能是登陆密码，也可能是
	 * 通过前置单点登陆接口拿到的token等，具体意义由业务层决定。
	 * 
	 * @param loginPsw
	 */
	public void setLoginToken(String loginToken)
	{
		this.loginToken = loginToken;
	}

	/**
	 * 返回额外信息字符串。本字段目前为保留字段，供上层应用自行放置需要的内容。
	 * 
	 * @return
	 * @since 2.1.6
	 */
	public String getExtra()
	{
		return extra;
	}
	
	/**
	 * 设置额外信息字符串。本字段目前为保留字段，供上层应用自行放置需要的内容。
	 * 
	 * @param extra
	 * @since 2.1.6
	 */
	public void setExtra(String extra)
	{
		this.extra = extra;
	}
}
