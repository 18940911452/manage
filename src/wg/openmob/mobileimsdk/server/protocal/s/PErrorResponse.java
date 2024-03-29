package wg.openmob.mobileimsdk.server.protocal.s;

/**
 * 错误信息DTO类。
 * 
 * @version 1.0
 */
public class PErrorResponse
{
	private int errorCode = -1;
	private String errorMsg = null;
	
	public PErrorResponse(int errorCode, String errorMsg)
	{
		this.errorCode = errorCode;
		this.errorMsg = errorMsg;
	}

	public int getErrorCode()
	{
		return errorCode;
	}

	public void setErrorCode(int errorCode)
	{
		this.errorCode = errorCode;
	}

	public String getErrorMsg()
	{
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg)
	{
		this.errorMsg = errorMsg;
	}
}
