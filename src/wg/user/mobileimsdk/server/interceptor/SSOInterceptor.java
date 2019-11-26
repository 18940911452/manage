package wg.user.mobileimsdk.server.interceptor;

import static wg.user.mobileimsdk.server.util.SSOUtils.*;

import java.io.IOException;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.TableMapping;

import wg.media.screen.fm.model.commandscreencommon.ManageUserInfo;
import wg.user.mobileimsdk.server.controller.IController;
import wg.user.mobileimsdk.server.listener.SSOListener;
import wg.user.mobileimsdk.server.util.SSOUtils;


/**
 * sso登录拦截
 * @author 18170
 *
 */
public class SSOInterceptor implements Interceptor {
	
	/**
	 * token 与session的对应关系，用于单点退出
	 */
	private static Map<String, String> tokenSession = new HashMap<>();
	
	private static JSONObject validateTicketFail = JSON.parseObject("{\"code\":\"IAM-001\",\"description\":\"请求失败\"}");

	

	
	/**
	 * 应用名
	 */
	@Override
	public void intercept(Invocation inv) {
		Controller con = inv.getController();
		String actionKey = inv.getActionKey();
		System.out.println("actionKey:" + actionKey);

		// 接受sso登录回调
		if (isSsoLogin(actionKey)) {
			System.out.println("into login:");
			login(inv);
			return;
		}

		// 接受sso退出回调
		if (isSsoLogout(actionKey)) {
			System.out.println("into loginout:");
			ssologinOut(inv);
			return;
		}

		boolean isLogin = isLogin(con);

		if (isLogin) {
			inv.invoke();
			return;
		} else {
			// 移动端登录|视频连线
			if (isAppLogin(actionKey) || isVideoLogin(actionKey)) {
				inv.invoke();
				return;
			} else {
				con.redirect("/login.html");
			}
		}

	}

	private String getHeader(String header, Controller con) {
		return con.getHeader(header);
	}

	// 打印头信息
	private void printAllHearders(HttpServletRequest request) {
		Enumeration<String> headerNames = request.getHeaderNames();
		while (headerNames.hasMoreElements()) {
			String head = headerNames.nextElement();
			System.out.println(head + " : " + request.getHeader(head));
		}
	}

	private void saveLastAccessUrl(Invocation inv) {
		String queryString = inv.getController().getRequest().getQueryString() == null ? ""
				: "?" + inv.getController().getRequest().getQueryString();
		inv.getController().setSessionAttr(getLastAccessInfo(), inv.getControllerKey() + queryString);
	}

	private boolean isLogin(Controller controller) {
		return controller.getSessionAttr(IController.LOGIN_USER) != null;
	}

		/**
		 * 单点登录操作
		 * @date
		 * @param inv
		 */
	private void login(Invocation inv) {
		Controller con = inv.getController();
		String ticket = con.getPara("ticket");
		String token = con.getPara("token");
		if (StringUtils.isAnyBlank(ticket, token)) {
			con.renderText("参数不全");
			return;
		}
		JSONObject result = validateTicket(inv);
		if (result.containsKey("user")) {
			String tent = result.getJSONObject("user").getString("username");
			System.out.println("result :" + result);
			System.out.println("tenant :" + tent);
			String sql = "select * from " + TableMapping.me().getTable(ManageUserInfo.class).getName()
					+ " where user_name = ? ";
			ManageUserInfo dbUser = ManageUserInfo.dao.findFirst(sql, tent);

			if (dbUser == null) {
				con.renderText("用户失效");
				return;
			}
			con.setSessionAttr(IController.LOGIN_USER, dbUser);

			tokenSession.put(token, con.getSession().getId());
			/**
			 * 重定向到原先访问页面。
			 */
			con.redirect(
					SSOUtils.prefix(con.getRequest()) + (String) con.getSession().getAttribute(getLastAccessInfo()));
			return;
		}
		con.renderText(result.getString("description"));
		return;
	}

	private JSONObject validateTicket(Invocation inv) {
		try {
			String ticket = inv.getController().getPara("ticket");
			String response = Jsoup.connect(validateTicketUrl(inv.getController().getRequest()))
					.data("service", registLoginUrl(inv.getController().getRequest())).data("ticket", ticket)
					.ignoreContentType(true).timeout(300000).execute().body();
			if (StringUtils.isBlank(response)) {
				return validateTicketFail;
			}
			JSONObject res = JSON.parseObject(response);
			JSONObject info = res.getJSONObject("serviceResponse");
			if (info.containsKey("authenticationSuccess")) {
				return info.getJSONObject("authenticationSuccess");
			}
			return info.getJSONObject("authenticationFailure");
		} catch (IOException e) {
			e.printStackTrace();
			return validateTicketFail;
		}

	}

	/**
	 * 退出操作
	 * 
	 * @param con
	 */

	private void ssologinOut(Invocation inv) {
		Controller con = inv.getController();
		String token = con.getPara("token");
		HttpSession session = inv.getController().getSession();
		// sso退出
		if (StringUtils.isNotEmpty(token) && tokenSession.containsKey(token.trim())) {
			String sessionId = tokenSession.get(token);
			session = SSOListener.getSessionById(sessionId);
		}
		// 子系统退出
		if (session != null) {
			session.invalidate();
		}
	}

}
