package wg.user.mobileimsdk.server.config;

import com.jfinal.config.Constants;
import com.jfinal.config.Handlers;
import com.jfinal.config.Interceptors;
import com.jfinal.config.JFinalConfig;
import com.jfinal.config.Plugins;
import com.jfinal.config.Routes;
import com.jfinal.core.JFinal;
import com.jfinal.ext.handler.ContextPathHandler;
import com.jfinal.ext.interceptor.SessionInViewInterceptor;
import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import com.jfinal.render.ViewType;
import com.jfinal.template.Engine;

import wg.media.screen.fm.model.commandscreencommon.ManageInstitution;
import wg.user.mobileimsdk.server.service.UserService;
import wg.user.mobileimsdk.server.controller.ActionPlanController;
import wg.user.mobileimsdk.server.controller.GroupController;
import wg.user.mobileimsdk.server.controller.IController;
import wg.user.mobileimsdk.server.controller.InformController;
import wg.user.mobileimsdk.server.controller.LoginController;
import wg.user.mobileimsdk.server.controller.MemberController;
import wg.user.mobileimsdk.server.controller.MessageController;
import wg.user.mobileimsdk.server.controller.Mod4Controller;
import wg.user.mobileimsdk.server.controller.Mod2Controller;
import wg.user.mobileimsdk.server.controller.SignalingTokenController;
import wg.user.mobileimsdk.server.controller.StatisticalController;
import wg.user.mobileimsdk.server.controller.UmPush;
import wg.user.mobileimsdk.server.controller.UploadFileController;
import wg.user.mobileimsdk.server.controller.WebToAppMessageController;
import wg.user.mobileimsdk.server.interceptor.SSOInterceptor;
import wg.user.mobileimsdk.server.logcontroller.UserAction;
import wg.user.mobileimsdk.server.spring.SpringLoader;
import wg.user.mobileimsdk.server.usermanage.LoginSystem;
import wg.user.mobileimsdk.server.usermanage.Mod1System;
import wg.user.mobileimsdk.server.xsshandler.XSSHandler;

/**
 * API引导式配置
 */
public class WebConfig extends JFinalConfig {
	static Prop prop = PropKit.use("web_config.txt");

	/**
	 * 配置常量
	 */
	public void configConstant(Constants me) {
		// 加载少量必要配置，随后可用PropKit.get(...)获取值

		InitDb.init();
		
		ManageInstitution user = new UserService().findByUUid(PropKit.use("a_little_config_ms.txt").get("userUuid"));
		IController.setUser(user);
		//SystemConfig.buildConfig(user);
		
		System.out.println("########init Dubbo over#######");
		me.setDevMode(prop.getBoolean("devMode", false));
		me.setEncoding("UTF-8");
		me.setViewType(ViewType.FREE_MARKER);
		// 默认10M,此处设置为最大20M
		me.setMaxPostSize(1024 * 1024 * 20);

	}

	/**
	 * 配置路由
	 */
	public void configRoute(Routes me) {
		me.setBaseViewPath("page");
		me.add("/group", GroupController.class); // 群组管理
		me.add("/member", MemberController.class); // 成员管理
		me.add("/message", MessageController.class); // 消息管理
		me.add("/login/mod1", LoginController.class); // 登录
		me.add("/", LoginSystem.class); // 登陆
		me.add("/system" + "/mod1", Mod1System.class); // 部门管理
		me.add("/upload", UploadFileController.class); // 消息管理
		me.add("/signalingToken", SignalingTokenController.class); // 信令
	//	me.add("/push", JiGuangPush.class); // 极光推送
		me.add("/webToApp", WebToAppMessageController.class); // web与手机进行消息互相连通
		me.add("/plan", ActionPlanController.class); // 行动预案
		me.add("/inform", InformController.class); // 行动预案
		me.add("/report/mod4", Mod4Controller.class); // 主题管理
		me.add("/report/mod2", Mod2Controller.class); // 任务管理(可以显示)
		me.add("/web", Mod2Controller.class); // 移动端任务管理
		me.add("/webx", Mod2Controller.class); // 移动端任务管理
		me.add("/webg", Mod2Controller.class); // 移动端任务管理
		me.add("/statistical", StatisticalController.class);//统计分析
		me.add("/action", UserAction.class);// 系统操作日志
		me.add("/push", UmPush.class);// 友盟系统推送
		
	}

	/**
	 * 初始化数据库
	 * 
	 * @return
	 */

	/**
	 * 配置插件
	 */
	public void configPlugin(Plugins me) {

	}

	/**
	 * 配置全局拦截器
	 */
	public void configInterceptor(Interceptors me) {
		// me.addGlobalActionInterceptor(new GlobalActionInterceptor());
//		if (prop.getBoolean("openSSO", true)) {
//			me.add(new SSOInterceptor());
//		}
//		me.add(new SessionInViewInterceptor());
	}

	/**
	 * 配置处理器
	 */
	public void configHandler(Handlers me) {
		me.add(new ContextPathHandler("baseUrl"));
		me.add(new XSSHandler()); // hongqi 无
	}

	@Override
	public void afterJFinalStart() {
		// super.afterJFinalStart();
		//new SpringLoader();

	}

	/**
	 * 建议使用 JFinal 手册推荐的方式启动项目 运行此 main 方法可以启动项目，此main方法可以放置在任意的Class类定义中，不一定要放于此
	 */
	public static void main(String[] args) {
		JFinal.start("WebRoot", 8080, "/");
	}

	@Override
	public void configEngine(Engine arg0) {
		// TODO Auto-generated method stub

	}
}
