package wg.user.mobileimsdk.server.controller;

/**
 * 登录相关接口
 */
import com.alibaba.fastjson.JSONObject;
import com.jfinal.aop.Clear;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;

import wg.media.screen.fm.model.commandscreencommon.ManageUserInfo;
import wg.user.mobileimsdk.server.service.LoginService;
import wg.user.mobileimsdk.server.util.SSOUtils;

/**
 * 
 * 类名称：LoginController <br/>
 * 类描述：登录相关接口<br/>
 * 创建时间：2018年1月5日 上午11:24:42 <br/>
 * 
 * @author panggongxiang
 * @version V1.0
 */
public class LoginController extends IController {
	public static String headAdress = PropKit.get("headAdress");

	public void index() {

	}

	/**
	 * 登出<br>
	 * 1.清空session域<br>
	 * 2.判断是否为单点登出<br>
	 */
	public void loginout() {
		getSession().removeAttribute(LOGIN_USER);
		getSession().invalidate();
		String cuntry = SSOUtils.isSsoLogout(getRequest());
		if (StrKit.notBlank(cuntry)) {// sso地址退出，重定向通知sso单点登录系统，
			renderText(SSOUtils.ssoLogoutRedirectUrl(getRequest()));
			return;
		}
		renderText("login.html");
	}

	/**
	 * @method:Loginvalidate
	 * @describe:登录验证
	 * @author: gongxiangPang
	 * @param :TODO
	 * @param name
	 * @param password
	 *            return 0用户存在 1026 用户不存在 1027 密码错误
	 */
	@Clear
	public void Loginvalidate() {
		String name = getPara("name");
		String password = getPara("password");
		// String
		// pth=getRequest().getScheme()+"://"+getRequest().getServerName()+":"+getRequest().getServerPort()+getRequest().getContextPath()+"/";
		JSONObject loginvalidate = LoginService.ser.Loginvalidate(name, password, headAdress);
		Object object = loginvalidate.get("user");
		if (object instanceof ManageUserInfo) {
			// ManageUserInfo user = (ManageUserInfo)object;
			// addUser(user);
			getSession().setAttribute(IController.LOGIN_USER, object);
		}
		loginvalidate.put("JSESSIONID", getSession().getId());
		loginvalidate.remove("user");
		if (getPara("isWeb") != null) {
			renderText("report/mod2");
		} else {
			renderJson(loginvalidate);
		}
	}

	/**
	 * 退出采编账号
	 */
	@Clear
	public void SignOutLogin(){
		String moni=getPara("moni");
		JSONObject jsonObject=LoginService.ser.SignOutLogin(moni);
		renderJson(jsonObject);
	}

	/**
	 * 判断采编session是否失效
	 */
	@Clear
	public void IfSeesionInvalid(){
		String cbSession=getPara("cbSession");
		JSONObject jsonObject=LoginService.ser.IfSeesionInvalid(cbSession);
		renderJson(jsonObject);
	}

	/**
	 * 
	 * 名称：LoginvalidateByname 描述：根据用户名登陆 日期：2019年5月22日-下午6:18:10
	 * 
	 * @author gongxiang.pang
	 */
	@Clear
	public void LoginvalidateByname() {
		String name = getPara("name");
		// String
		// pth=getRequest().getScheme()+"://"+getRequest().getServerName()+":"+getRequest().getServerPort()+getRequest().getContextPath()+"/";
		JSONObject loginvalidate = LoginService.ser.LoginvalidateByname(name, headAdress);
		Object object = loginvalidate.get("user");
		if (object instanceof ManageUserInfo) {
			// ManageUserInfo user = (ManageUserInfo)object;
			// addUser(user);
			getSession().setAttribute(IController.LOGIN_USER, object);
		}
		loginvalidate.put("JSESSIONID", getSession().getId());
		loginvalidate.remove("user");
		renderJson(loginvalidate);
	}

	@Clear
	public void Loginval() {
		String id = getPara("id");
		// String
		// pth=getRequest().getScheme()+"://"+getRequest().getServerName()+":"+getRequest().getServerPort()+getRequest().getContextPath()+"/";
		JSONObject loginvalidate = LoginService.ser.Loginval(id);
		renderJson(loginvalidate);

	}

	@Clear
	public void getServerTime() {
		String callback = getPara("callback");
		String name = getPara("name");
		String password = getPara("password");
		// String
		// pth=getRequest().getScheme()+"://"+getRequest().getServerName()+":"+getRequest().getServerPort()+getRequest().getContextPath()+"/";
		JSONObject object = LoginService.ser.getServerTime();
		renderText(callback + "(" + object.toJSONString() + ")");
	}

	public void ssoapplogin() {

	}

	public void ssoapplogout() {

	}

	@Clear
	public void version() {

	}

}
