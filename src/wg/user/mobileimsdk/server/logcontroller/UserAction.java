package wg.user.mobileimsdk.server.logcontroller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.core.Controller;

import wg.media.screen.fm.model.commandscreencommon.ManageUserInfo;
import wg.media.screen.fm.model.commandscreencommon.ManagerUserRecord;
import wg.user.mobileimsdk.server.logservice.IUserAction;
import wg.user.mobileimsdk.server.logservice.UserActionImpl;
import wg.user.mobileimsdk.server.model.vo.MonitorConfigSearchVo;
import wg.user.mobileimsdk.server.util.ServiceUtils;

public class UserAction extends Controller {

	private IUserAction actionService = new UserActionImpl();

	public void load() {
		String path = getPara("path");
		render(path);
	}

	/**
	 * 获取用户行为记录信息 名称：getInfoAction 描述：TODO 日期：2018年12月27日-下午8:19:06
	 * 
	 * @author gongxiang.pang http://localhost:8080/system/action/getInfoAction
	 */
	public void getInfoAction() {
		Integer pageSize = getParaToInt("pageSize");
		if (pageSize == null) {
			pageSize = 10;
		}
		Integer pageNo = getParaToInt("pageNo");
		if (pageNo == null) {
			pageNo = 1;
		}
		String path = getPara("path");
		String searchKey = getPara("searchKey");// 关键词模糊搜索

		MonitorConfigSearchVo search = new MonitorConfigSearchVo();
		search.setPageNo(pageNo);
		search.setPageSize(pageSize);
		ManageUserInfo user = (ManageUserInfo) getSession().getAttribute("login_user");

		JSONObject json = actionService.getInfoAction(searchKey, search);
		setAttr("list", json.get("data"));
		setAttr("total", json.get("total"));
		setAttr("pageNo", pageNo);
		setAttr("pageSize", pageSize);
		render(path);
	}

	/**
	 * 
	 * 名称：addActioInfo 日期：2018年12月28日-下午3:58:52
	 * 
	 * @author gongxiang.pang
	 * @throws Exception
	 */
	public void addActioInfo() throws Exception {
		String action = getPara("action");// 动作
		String cation_content = getPara("cation_content");// 动作内容
		JSONObject jo = new JSONObject();
		Integer result = 0;
		ManageUserInfo user = (ManageUserInfo) getSession().getAttribute("login_user");
		Integer uid = -1;
		String username = "访客身份";
		if (user != null) {
			uid = user.getUserId();
			username = user.getUserName();
		}
		ManagerUserRecord record = new ManagerUserRecord();
		record.setUid(uid);
		record.setUsername(username);
		String ip = ServiceUtils.getIpAddr(getRequest());// 获取客户端ip
		record.setIp(ip);
		record.setAction(action);
		record.setCationContent(cation_content);
		record.setInsertTime(new Date());
		result = actionService.addActioInfo(record);

		jo.put("result", result);
		renderJson(jo);
	}

	public void testIp() {
		/*
		 * ServletRequestAttributes attr = (ServletRequestAttributes)                 
		 * RequestContextHolder.currentRequestAttributes();          HttpServletRequest
		 * request = attr.getRequest();
		 */
		System.out.println("******************************");
		String remoteHost = getRequest().getRemoteHost();
		System.out.println("remoteHost:" + remoteHost);
		System.out.println("******************************");
		String remoteAddr = getRequest().getRemoteAddr();
		System.out.println("remoteAddr:" + remoteAddr);
		System.out.println("******************************");
		HttpServletRequest request = getRequest();
		String ipAddr = getIpAddr(request);
		System.out.println("ipAddr:" + ipAddr + "***********dfsdfsf***************");
		String macAddress;
		try {
			macAddress = getMACAddress(ipAddr);
			System.out.println("macAddress:" + ipAddr + "***********************");
			System.out.println("macAddress:" + macAddress);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * 通过HttpServletRequest返回IP地址  
	 * 
	 * @param request
	 *             HttpServletRequest
	 * @return ip String
	 * @throws Exception
	 */
	public static String getIpAddr(HttpServletRequest request) {
		String ip = request.getHeader("X-Forwarded-For");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_CLIENT_IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_X_FORWARDED_FOR");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		return ip;
	}

	/**
	 * 通过IP地址获取MAC地址  
	 * 
	 * @param ip
	 *                        String,127.0.0.1格式
	 * @return mac String
	 * @throws Exception
	 */
	public static String getMACAddress(String ip) throws Exception {
		String line = "";
		String macAddress = "";
		final String MAC_ADDRESS_PREFIX = "MAC Address = ";
		final String LOOPBACK_ADDRESS = "127.0.0.1";
		// 如果为127.0.0.1,则获取本地MAC地址。
		if (LOOPBACK_ADDRESS.equals(ip)) {
			InetAddress inetAddress = InetAddress.getLocalHost();
			// 貌似此方法需要JDK1.6。
			byte[] mac = NetworkInterface.getByInetAddress(inetAddress).getHardwareAddress();
			// 下面代码是把mac地址拼装成String
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < mac.length; i++) {
				if (i != 0) {
					sb.append("-");
				}
				// mac[i] & 0xFF 是为了把byte转化为正整数
				String s = Integer.toHexString(mac[i] & 0xFF);
				sb.append(s.length() == 1 ? 0 + s : s);
			}
			// 把字符串所有小写字母改为大写成为正规的mac地址并返回
			macAddress = sb.toString().trim().toUpperCase();
			return macAddress;
		}
		// 获取非本地IP的MAC地址
		try {
			Process p = Runtime.getRuntime().exec("nbtstat -A " + ip);
			InputStreamReader isr = new InputStreamReader(p.getInputStream());
			BufferedReader br = new BufferedReader(isr);
			while ((line = br.readLine()) != null) {
				if (line != null) {
					int index = line.indexOf(MAC_ADDRESS_PREFIX);
					if (index != -1) {
						macAddress = line.substring(index + MAC_ADDRESS_PREFIX.length()).trim().toUpperCase();
					}
				}
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace(System.out);
		}
		return macAddress;
	}

	/**
	 * 名称：http://localhost:8080/system/action/detailLog?id=1 描述：详情
	 * 日期：2019年4月23日-下午4:19:55
	 * 
	 * @author jian.li
	 */
	public void detailLog() {
		Long id = getParaToLong("id");
		ManagerUserRecord record = actionService.detailLog(id);
		renderJson(record);
	}

	/**
	 * 名称：http://localhost:8080/system/action/updateLog?id=1&action=&address=&cation_content=动作内容&browser=浏览器名称版本&mac=mac地址
	 * 描述：修改 日期：2019年4月23日-下午4:19:49
	 * 
	 * @author jian.li
	 */
	public void updateLog() {
		int id = getParaToInt("id");
		String action = getPara("action");
		String address = getPara("address");
		String cation_content = getPara("cation_content");// 动作内容
		String browser = getPara("browser");// 浏览器名称 版本
		String mac = getPara("mac");// mac地址
		JSONObject object = new JSONObject();
		ManageUserInfo user = (ManageUserInfo) getSession().getAttribute("login_user");
		ManagerUserRecord record = new ManagerUserRecord();
		record.setId(id);
		// record.setUid(user.getId());
		record.setAddress(address);
		record.setUsername(user.getUserName());
		record.setAction(action);
		record.setCationContent(cation_content);
		// String ip= getIpAddr(getRequest());//获取ip地址
		record.setBrowser(browser);
		record.setMac(mac);
		record.setUpdateTime(new Date());
		record.setUpdateUserid(user.getUserId());
		record.setUpdateUserName(user.getUserName());
		object = actionService.updateLog(record);
		renderJson(object);
	}

	/**
	 * 名称：http://localhost:8080/system/action/delLog?id=1 描述：删除
	 * 日期：2019年4月23日-下午4:19:53
	 * 
	 * @author jian.li
	 */
	public void delLog() {
		Long id = getParaToLong("id");
		renderJson(actionService.delLog(id));
	}
}
