package wg.user.mobileimsdk.server.usermanage;

import com.jfinal.config.Handlers;
import com.jfinal.config.Interceptors;
import com.jfinal.config.Plugins;
import com.jfinal.config.Routes;

import wg.user.mobileimsdk.server.custom.InitWebJfinal;

public class System_InitWebJfinal extends InitWebJfinal {

	public static String sysName = "system";

	@Override
	public String getSysName() {
		return sysName;
	}

	@Override
	public void configRoute(Routes me) {
		me.add("/" + getSysName() + "/mod1", Mod1System.class); // 部门管理
	}

	@Override
	public void configPlugin(Plugins me) {

	}

	@Override
	public void configInterceptor(Interceptors me) {

	}

	@Override
	public void configHandler(Handlers me) {

	}

	@Override
	public void afterJFinalStart() {

	}

}
