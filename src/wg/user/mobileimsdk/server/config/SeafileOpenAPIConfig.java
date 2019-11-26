package wg.user.mobileimsdk.server.config;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.Connection.Method;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.kit.StrKit;

import wg.user.mobileimsdk.server.config.CommonOpenAPIConfig;

public class SeafileOpenAPIConfig extends CommonOpenAPIConfig {

	private String sessionid;
	
	
	private String sfcsrftoken;
	

	public String getSessionid() {
		return sessionid;
	}

	public void setSessionid(String sessionid) {
		this.sessionid = sessionid;
	}

	public String getSfcsrftoken() {
		return sfcsrftoken;
	}

	public void setSfcsrftoken(String sfcsrftoken) {
		this.sfcsrftoken = sfcsrftoken;
	}

	public SeafileOpenAPIConfig(String systemName, String tokenUrl, String appId, String appSecret) {
		super(systemName, tokenUrl, appId, appSecret);
	}
	//   Authorization: Token 24fd3c026886e3121b2ca630805ed425c272cb96
	//	curl -d "username=username@example.com&password=123456" https://cloud.seafile.com/api2/auth-token/

	@Override
	protected String requestToken() {
		Map<String,String> data = new HashMap<>();
		data.put("username", getAppId());
		data.put("password", getAppSecret());
		String result = "";
		try {
			result =Jsoup.connect(getTokenUrl()).method(Method.POST).ignoreContentType(true).data(data).timeout(300000).execute().body();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}


	@Override
	protected String parseToken(String result) {
		if(StrKit.isBlank(result))
			return "";
		try {
			JSONObject data = JSON.parseObject(result);
			return data.getString("token");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e);
		}
		return "";
	}
	
	public String sfcsrfTokenKey() {
		return "sfcsrftoken";
	}

	
}
