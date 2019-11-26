package wg.user.mobileimsdk.server.config;

import java.util.Map;

import com.jfinal.kit.StrKit;

import wg.media.screen.fm.common.OpenAPIConfig;
import wg.media.screen.fm.vo.ResultVo;

import wg.user.mobileimsdk.server.controller.IController;
import wg.user.mobileimsdk.server.service.BaseOpenAPIInvoke;

public abstract class ApiController extends IController{
	
	
	
	protected abstract BaseOpenAPIInvoke getOpenAPIInvoke();
	
	public String getModelKey() {
		ModuleController controller =  (ModuleController)this;
		return controller.getSysName();
	}
	
	public void invokeOpenApi() {
		OpenAPIConfig config = getSystemUser().get(getModelKey());
		Map<String, String> requestMap = getRequestMap();
		
		//传递方法信息
		if(needtransMethod()) {
			requestMap.put(OpenAPIConstants.REQESTMETHOD,String.valueOf(getRequest().getAttribute(OpenAPIConstants.REQESTMETHOD)));
		}
		String data = getOpenAPIInvoke().invokeOpenAPI(requestMap, getHeaders(), getRequest().getMethod(),config);
		ResultVo<String> result = ResultVo.create("");
		if(StrKit.notBlank(data)) {
			result.setOK(data);
			renderJson(result);
			return ;
		}
		
		result.setError();
		renderJson(result);
	}
	private boolean  needtransMethod() {
		return getRequest().getAttribute(OpenAPIConstants.REQESTMETHOD)!=null;
	}
}
