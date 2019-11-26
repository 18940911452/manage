package wg.user.mobileimsdk.server.util;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;

public class SSOUtils {

	private static Prop prop = PropKit.use("sso_config.txt");

	private final static String lastAccessAddress = "lastAccessAddress";

	/**
	 * 统一登录地址
	 */
	private static String ssoLonginUrl = prop.get("ssoLoginUrl", "http://{county}.wengetech.com:8081/cas/login");

	private static String county = prop.get("county", "qtx");

	/**
	 * 统一退出地址
	 * 
	 * nginx 配置多级路径oca
	 */
	private static String ssoLoginoutUrl = prop.get("ssoLoginOutUrl", "http://{county}.wengetech.com:8081/cas/logout");

	private static String validateTicketUrl = prop.get("ssoValidateUrl",
			"http://{county}.wengetech.com:8081/cas/serviceValidate");

	private static String domain = prop.get("domain", "http://{county}.wengetech.com:9006/WG_IM");

	private static String appLoginUrl = "/login/mod1/ssoapplogin";
	private static String appLoginoutUrl = "/login/mod1/ssoapplogout";
	private static String appLoginvalidate = "/login/mod1/Loginvalidate";
	private static String videoLoginvalidate = "/signalingToken/getKey";

	/**
	 * 视频连线
	 * @param actionKey
	 * @return
	 */
	public static boolean isVideoLogin(String actionKey) {
		return videoLoginvalidate.equals(actionKey);
	}

	/**
	 * 判断是否是单点登录退出
	 * 
	 * @param request
	 * @return
	 */
	public static String isSsoLogout(HttpServletRequest request) {
		String county = request.getParameter("county");
		if (StringUtils.isNotBlank(county)) {
			return county;
		}
		return request.getHeader(SSOUtils.county);
	}

	/**
	 * 是否是登录接口
	 * 
	 * @param actionKey
	 * @return
	 */
	public static boolean isAppLogin(String actionKey) {
		return appLoginvalidate.equals(actionKey);
	}

	public static boolean isSsoLogin(String actionKey) {
		return appLoginUrl.equals(actionKey);
	}

	public static boolean isSsoLogout(String actionKey) {
		return appLoginoutUrl.equals(actionKey);
	}

	public static String ssoLonginUrl(HttpServletRequest request) {
		return ssoLonginUrl.replace("{county}", county(request));
	}

	public static String ssoLogoutUrl(HttpServletRequest request) {
		return ssoLoginoutUrl.replace("{county}", county(request));
	}

	public static String validateTicketUrl(HttpServletRequest request) {
		return validateTicketUrl.replace("{county}", county(request));
	}

	public static String county(HttpServletRequest request) {
		return county;
	}

	public static String prefix(HttpServletRequest request) {
		return domain.replace("{county}", county(request));
	}

	public static String registLogoutUrl(HttpServletRequest request) {
		return prefix(request) + appLoginUrl;
	}

	public static String registLoginUrl(HttpServletRequest request) {
		return prefix(request) + appLoginUrl;
	}

	public static String ssoLogoutRedirectUrl(HttpServletRequest request) {
		return ssoLogoutUrl(request) + "?from=" + registLogoutUrl(request);
	}

	public static String ssoLoginRedirectUrl(HttpServletRequest request) {
		return ssoLonginUrl(request) + "?service=" + registLoginUrl(request);
	}

	public static String getLastAccessInfo() {
		return lastAccessAddress;
	}

}
