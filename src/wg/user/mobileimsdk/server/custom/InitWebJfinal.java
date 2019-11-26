package wg.user.mobileimsdk.server.custom;

import com.jfinal.config.Handlers;
import com.jfinal.config.Interceptors;
import com.jfinal.config.Plugins;
import com.jfinal.config.Routes;
import com.jfinal.log.Log;

public abstract class InitWebJfinal {
	public Log log = Log.getLog(InitWebJfinal.class);

	public abstract String getSysName();

	/**
	 * 配置路由
	 */
	public abstract void configRoute(Routes me);

	/**
	 * 配置插件
	 */
	public abstract void configPlugin(Plugins me);

	/**
	 * 配置全局拦截器
	 */
	public abstract void configInterceptor(Interceptors me);

	/**
	 * 配置处理器
	 */
	public abstract void configHandler(Handlers me);

	public abstract void afterJFinalStart();
}