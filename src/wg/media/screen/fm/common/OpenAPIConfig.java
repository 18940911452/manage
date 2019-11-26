package wg.media.screen.fm.common;

import wg.media.screen.fm.model.commandscreen.OpenInvokeInfo;

public interface OpenAPIConfig {
	public String getToken();
	
	public OpenInvokeInfo getOpenInvokeInfoByMethodName(String methodName);
}
