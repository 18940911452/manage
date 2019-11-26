package wg.openmob.mobileimsdk.server.protocal.s;

/**
 * 登陆结果响应信息DTO类。
 * 
 * @version 1.0
 */
public class PLoginInfoResponse
{
	/** 错误码：0表示认证成功，否则是用户自定的错误码（该码应该是>1024的整数） */
	private int code = 0;
	
	public PLoginInfoResponse(int code)
	{
		this.code = code;
	}

	public int getCode()
	{
		return code;
	}
	public void setCode(int code)
	{
		this.code = code;
	}
}
