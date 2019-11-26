package wg.user.mobileimsdk.server.service;

import java.io.IOException;
import java.util.Map;
import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.Jsoup;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.jfinal.kit.StrKit;
import wg.media.screen.fm.common.OpenAPIConfig;
import wg.media.screen.fm.common.OpenAPIInvoker;
import wg.media.screen.fm.model.commandscreen.OpenInvokeInfo;
import wg.user.mobileimsdk.server.config.OpenAPIConstants;


public abstract class BaseOpenAPIInvoke implements OpenAPIInvoker {

	@Override
	public String invokeOpenAPI(Map<String, String> param, Map<String, String> headers, String method,
			OpenAPIConfig openAPIConfigByStstemName) {
		String result = "";
		OpenInvokeInfo info =getInvokeInfo(getMethodName(param),openAPIConfigByStstemName);
		if(info ==null) {
			return "";
		}
		String url = info.getInvokeUrl();
		System.out.println("url:"+url);
		if (StrKit.isBlank(url)) {
			return result;
		}
		addDbParam(info,param);
		changeParam(param);
		try {
			if (StrKit.notBlank(openAPIConfigByStstemName.getToken())) {
				result = requestAPI(param, headers, method, url, openAPIConfigByStstemName);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	protected String getMethodName(Map<String, String> param) {
		return param.get(OpenAPIConstants.REQESTMETHOD);
	}

	private void addDbParam(OpenInvokeInfo info, Map<String, String> param) {
		addParam(info.getParam(), param);
		addParam(info.<String>get("requestParam"), param);
	}

	private void addParam(String dbParam,Map<String, String> param) {
		if(StrKit.isBlank(dbParam))
			return;
		Map<String,String> dbParamMap = JSON.parseObject(dbParam,new TypeReference<Map<String,String>>() {});
		for(Map.Entry<String, String> entry:dbParamMap.entrySet()) {
			param.put(entry.getKey(),entry.getValue());
		}
		
	}

	private OpenInvokeInfo getInvokeInfo( String methodName,OpenAPIConfig openAPIConfigByStstemName) {
		return openAPIConfigByStstemName.getOpenInvokeInfoByMethodName(methodName);
	}


	protected abstract String modelName();

	protected void changeParam(Map<String, String> param) {
		param.put("Rtype", "json");
	}

	
	
	
	protected String requestAPI(Map<String, String> param, Map<String, String> headers, String method, String url,
			OpenAPIConfig openAPIConfigByStstemName) throws IOException {
		Connection con = Jsoup.connect(url).method(Method.valueOf(method)).ignoreContentType(true)
				.cookie(sessionKey(), openAPIConfigByStstemName.getToken()).timeout(300000).data(param).maxBodySize(0);
		setHeaders(con, headers);
		return con.execute().body();
	}
	
	
	
	
	

	protected String sessionKey() {
		return "JSESSIONID";
	}

	protected void setHeaders(Connection con, Map<String, String> headers) {
		if (headers != null) {
			for (Map.Entry<String, String> entry : headers.entrySet()) {
				con.header(entry.getKey(), entry.getValue());
			}
		}
	}
	
	private void printInfo() {
		
	}
	
}
