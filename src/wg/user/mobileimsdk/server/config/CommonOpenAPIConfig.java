package wg.user.mobileimsdk.server.config;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.kit.StrKit;

import wg.media.screen.fm.common.OpenAPIConfig;
import wg.media.screen.fm.model.commandscreen.OpenInvokeInfo;
import wg.user.mobileimsdk.server.util.HttpClientUtils;


public abstract class CommonOpenAPIConfig implements OpenAPIConfig {
	private String systemName;
	private String token;
	private String tokenUrl;
	private String appId;
	private String appSecret;

	private Map<String, OpenInvokeInfo> nameInvokeInfos;

	/**
	 * 获取请求token
	 * 
	 * @return
	 */
	abstract protected String requestToken();

	public void setNameInvokeInfos(Map<String, OpenInvokeInfo> nameInvokeInfos) {
		this.nameInvokeInfos = nameInvokeInfos;
	}

	/**
	 * 解析结果
	 * 
	 * @param getResult
	 * @return
	 */
	abstract protected String parseToken(String getResult);

	public OpenInvokeInfo getOpenInvokeInfoByMethodName(String methodName) {
		if (nameInvokeInfos == null)
			return null;
		return nameInvokeInfos.get(methodName);
	}

	public CommonOpenAPIConfig(String systemName, String tokenUrl, String appId, String appSecret) {
		this.systemName = systemName;
		this.tokenUrl = tokenUrl;
		this.appId = appId;
		this.appSecret = appSecret;
	}

	public boolean initToken() {
		try {
			for (int i = 0; i < failRetryTime(); i++) {
				String getResult = requestToken();
				System.out.println("token url :" + getTokenUrl());
				if (StrKit.notBlank(getResult)) {
					String parseData = parseToken(getResult);
					if (StrKit.notBlank(parseData)) {
						token = parseData.trim();
						return true;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e);
		}
		return false;
	}

	protected int failRetryTime() {
		return 3;
	}

	public static CommonOpenAPIConfig createOpenApiConfig(String systemName, String tokenUrl, String appId,
			String appSecret, String pubKey, String privateKey) {

		if ("resource".equals(systemName)) {
			return new SeafileOpenAPIConfig(systemName, tokenUrl, appId, appSecret);
		} else {
			CommonOpenAPIConfig commonOpenAPIConfig = new CommonOpenAPIConfig(systemName, tokenUrl, appId, appSecret) {
				private String pubKey;
				@SuppressWarnings("unused")
				private String privateKey;

				protected String requestToken() {
					String getResult = HttpClientUtils.httpGet(getTokenUrl(), getParam());
					return getResult;
				}

				protected String parseToken(String getResult) {
					JSONObject result = JSON.parseObject(getResult);
					if (result != null && result.getInteger("code") == 1) {
						String token = result.getString("token");
//						privateKey
//						RSACryptography.decrypt(content, privateKey);
						return token;
					}
					return "";
				}

				private Map<String, String> getParam() {
					Map<String, String> param = new HashMap<>();
					param.put(OpenAPIConstants.APPID, getAppId());
					param.put(OpenAPIConstants.APPSECRET, getAppSecret());
					param.put("pubKey", pubKey);
					return param;
				}

				public CommonOpenAPIConfig saveKey(String pubKey, String privateKey) {
					this.pubKey = pubKey;
					this.privateKey = privateKey;
					return this;
				}

			}.saveKey(pubKey, privateKey);
			return commonOpenAPIConfig;
		}
	}

	/**
	 * 获取对应子系统token
	 * 
	 * @param systemName
	 * @return
	 */
	public String getToken(String systemName) {
		return token;
	}

	public String getSystemName() {
		return systemName;
	}

	public String getToken() {
		return token;
	}

	public String getTokenUrl() {
		return tokenUrl;
	}

	public String getAppId() {
		return appId;
	}

	public String getAppSecret() {
		return appSecret;
	}

}
