package wg.user.mobileimsdk.server.controller;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.jfinal.core.Controller;
import com.jfinal.kit.PropKit;

import wg.media.screen.fm.model.commandscreencommon.ManageInstitution;
import wg.media.screen.fm.model.commandscreencommon.ManageUserInfo;
import wg.media.screen.fm.utils.Helper;


public abstract class IController extends Controller {
//public static Logger log = Logger.getLogger(IController.class);
	private static ManageInstitution user ;
	public final static String  LOGIN_USER ="login_user";
	public final static String  API_USER ="api_user";
	public final static String __menu = "__menu";
	public final static String __button = "__button";
	public final static String username = "username";
	public final static String __allPerms = "__allPerms";
	public final static String __mgr = "__mgr";
	public final static String jsonb = "jsonb";
	public final static String jsonm = "jsonm";
	public final static String file_upload_path = PropKit.use("a_little_config_ms.txt").get("glusterfs_baseDir")
			+ "/szybjjcmk";
	public final static Map<String,HttpSession>usernameSession = new ConcurrentHashMap<>();
	public  void addUser(ManageUserInfo user) {
		String userName =user.getUserName();
		String type = getHeader("mobile-type");
		if(type==null) {
			type ="";
		}
		userName+=type;
		if(usernameSession.containsKey(userName)) {
			usernameSession.get(userName).removeAttribute(LOGIN_USER);
		}
		HttpSession session =getSession();
		usernameSession.put(userName, session);

		session.setAttribute(LOGIN_USER, user);
	}
	
//	public static String TEMPVIEW_PATH = "/dynamic/";
	public static String TEMPVIEW_PATH = "/";
	// web缓存，当redis缓存不可用时，启用
	@SuppressWarnings("unused")
	private static Map<String, String> webCache = new HashMap<String, String>();
	

	@Override
	public void render(String view) {
		try {
			String para = getPara("embed");
			if(para!=null){
				setAttr("embed", para);
			}
		} catch (Exception e) {
		}
		
		if ("json".equals(getPara("Rtype"))) {
			renderJson();
		} else {
			if(view==null) {
				renderText("path不能为null");
			}else {
				super.render(view);
			}
		}
	}
	
	public void renderT(String view) {
		try {
			String para = getPara("embed");
			if(para!=null){
				setAttr("embed", para);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if ("json".equals(getPara("Rtype"))) {
			renderJson();
		} else
			super.render(TEMPVIEW_PATH+view);
	}
	
	protected Map<String,String> getRequestMap(){
		HttpServletRequest request = getRequest();
		Map<String, String[]> parameterMap = request.getParameterMap();
		Map<String,String >result = new HashMap<>();
		for(Map.Entry<String, String []> entry:parameterMap.entrySet()) {
			result.put(entry.getKey(), Helper.conStr(entry.getValue(),",","",""));
		}
		return result;
	}
	
	protected Map<String,String> getHeaders(){
		HttpServletRequest request = getRequest();
		Enumeration<String> headerNames = request.getHeaderNames();
		Map<String,String >result = new HashMap<>();
		while(headerNames.hasMoreElements()) {
			String head = headerNames.nextElement();
			String value = request.getHeader(head);
			result.put(head, value);
		}
		return result;
	}
	/****
	 * 获取当前网络根Url
	 * @return
	 */
	public String getWebRootAddress(){
		HttpServletRequest request = getRequest();
		String baseWebAddress = request.getScheme() // 当前链接使用的协议
				+ "://" + request.getServerName()// 服务器地址
				+ ":" + request.getServerPort() // 端口号
				+ request.getContextPath();

		return baseWebAddress;
	}
	
	public ManageUserInfo getUser() {
		return getSessionAttr(LOGIN_USER);
	}
	
	public static ManageInstitution getSystemUser() {
		return user;
	}

	public static void setUser(ManageInstitution user) {
		IController.user = user;
	}
	
	
	
	/*public Integer findTargetDB(){
		return 1;
		//return this.getSessionAttr(DBHelper.SESSION_DB_FLAG)==null?null:this.getSessionAttr(DBHelper.SESSION_DB_FLAG).toString();
	}*/
	/**
	 * 返回当前登录用户的
	 * @return
	 */
//	public ManageUserInfo getUser(){
//		
//		
//		//小程序分享,返回null对象
////		Boolean share = getSessionAttr(ConstantWEB.ISOPEN);
////		if(share!=null && share) {//分享返回null对象
////			return null;
////		}
//		
//		//登录用户
//		Object sesssionUser = getSessionAttr(IController.LOGIN_USER);
//		//return user;
//		if(sesssionUser!=null) {
//			return (ManageUserInfo)sesssionUser;
//		}
//		
//		//api用户
//		Object apiUser = getSessionAttr(IController.API_USER);
//		if(apiUser!=null) {
//			return (ManageUserInfo)apiUser;
//		}
//		
//		return null;
//		
//	}
}
