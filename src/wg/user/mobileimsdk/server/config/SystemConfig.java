package wg.user.mobileimsdk.server.config;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.TableMapping;

import wg.media.screen.fm.model.commandscreen.OpenConfig;
import wg.media.screen.fm.model.commandscreen.OpenInvokeInfo;
import wg.media.screen.fm.model.commandscreencommon.ManageInstitution;
import wg.media.screen.fm.utils.Helper;
import wg.user.mobileimsdk.server.util.HttpClientUtils;


/**
 * 
 * 系统配置类
 * 
 * @author 18170
 * 
 * 
 */
public class SystemConfig {
	public Map<String, CommonOpenAPIConfig> NAMEOPENCONFIG = new ConcurrentHashMap<>();

	public void addOpenAPIConfig(CommonOpenAPIConfig config) {
		NAMEOPENCONFIG.put(config.getSystemName(), config);
	}

	public CommonOpenAPIConfig getOpenAPIConfigByStstemName(String systemName) {
		return NAMEOPENCONFIG.get(systemName);
	}

	private SystemConfig() {

	}

	public static List<OpenConfig> getConfigFromDb(Integer systemCode) {
		List<OpenConfig> openConfigs = OpenConfig.dao.find("select * from "
				+ TableMapping.me().getTable(OpenConfig.class).getName() + " where system_code=? and status=1",
				systemCode);
		if (openConfigs == null || openConfigs.size() == 0) {
			openConfigs = Collections.emptyList();
		}

		return openConfigs;
	}

	public static SystemConfig buildConfig(ManageInstitution user) {
		List<OpenConfig> openConfigs = getConfigFromDb(user.getSystemCode());

		Map<String, Map<String, OpenInvokeInfo>> infos = buildSysNameMethodOpenInvokeInfo(openConfigs,
				user.getSystemCode());

		SystemConfig confing = new SystemConfig();
		for (OpenConfig openConfig : openConfigs) {
			if (invalidateOpenConfig(openConfig))
				continue;
			CommonOpenAPIConfig createOpenApiConfig = CommonOpenAPIConfig.createOpenApiConfig(
					openConfig.getSubSystemName(), openConfig.getTokenUrl(), openConfig.getAppId().toString(),
					openConfig.getAppSecret(), user.getPublicKey(), user.getPrivateKey());
			if (createOpenApiConfig.initToken()) {
				confing.addOpenAPIConfig(createOpenApiConfig);
				user.put(createOpenApiConfig.getSystemName(), createOpenApiConfig);
				createOpenApiConfig.setNameInvokeInfos(infos.get(createOpenApiConfig.getSystemName()));
			}
		}
		user.put("SYSTEMCONFIG", confing);
		confing.reflashToken();
		return confing;
	}

	private static Map<String, Map<String, OpenInvokeInfo>> buildSysNameMethodOpenInvokeInfo(
			List<OpenConfig> openConfigs, Integer systemCode) {
		Map<String, OpenConfig> nameOpenConfing = new HashMap<>();
		for (OpenConfig config : openConfigs) {
			nameOpenConfing.put(config.getSubSystemName(), config);
		}
		String systemNames = "";
		Map<String, Map<String, OpenInvokeInfo>> nameOpenInfo = new HashMap<>();
		systemNames = Helper.collection2str(nameOpenConfing.keySet(), ",", "'", "'");
		String invokeInfoSql = "SELECT * FROM `open_invoke_info` , open_ref_user_invoke where open_invoke_info.auto_id = open_ref_user_invoke.open_invoke_id and open_invoke_info.`status` = 1 and sys_name in("
				+ systemNames + ") and inst_id = ? ";

		List<OpenInvokeInfo> openInvokeInfos = OpenInvokeInfo.dao.find(invokeInfoSql, systemCode);
		for (OpenInvokeInfo openInvokeInfo : openInvokeInfos) {
			Map<String, OpenInvokeInfo> map = nameOpenInfo.get(openInvokeInfo.getSysName());
			if (map == null) {
				map = new HashMap<>();
				nameOpenInfo.put(openInvokeInfo.getSysName(), map);
			}
			map.put(openInvokeInfo.getMethodName(), openInvokeInfo);
		}

		return nameOpenInfo;
	}

	private static boolean invalidateOpenConfig(OpenConfig openConfig) {
		if (StrKit.isBlank(openConfig.getTokenUrl()))
			return true;
		if (openConfig.getAppId() == null)
			return true;
		if (StrKit.isBlank(openConfig.getAppSecret()))
			return true;
		return false;
	}

	private long reflushTokenTime() {
		return 1000 * 60 * 60 * 2;
	}

	/**
	 * 刷新token方法
	 */

	public void reflashToken() {
		Thread configThread = new Thread() {

			@Override
			public void run() {
				while (true) {
					try {
						Thread.sleep(reflushTokenTime());
						reflushToken();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}

			private void reflushToken() {
//				List<String> dealName = new ArrayList<String>(NAMEOPENCONFIG.size());
				for (CommonOpenAPIConfig config : NAMEOPENCONFIG.values()) {
					String tokenUrl = config.getTokenUrl();
					
					tokenUrl = tokenUrl.replaceAll("getToken", "reflashToken");
					System.out.println("tokenUrl :"+tokenUrl);
					Map<String, String> headers = new HashMap<String, String>();
					headers.put("Cookie", config.getToken());
					try {
						String httpGet = HttpClientUtils.httpGet(tokenUrl, headers);
						JSONObject data = JSON.parseObject(httpGet);
						String code = data.getString("code");
						if ("2".equals(code)) {
							continue;
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					
					//刷新token
					config.initToken();
				}
			}
		};
		configThread.setName("screen reflush token Demon Thread");
		configThread.setDaemon(true);
		configThread.start();
	}

}
