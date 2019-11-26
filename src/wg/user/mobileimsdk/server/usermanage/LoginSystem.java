package wg.user.mobileimsdk.server.usermanage;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.aop.Clear;
import com.jfinal.kit.PropKit;

import wg.media.screen.fm.model.commandscreencommon.ManagePerm;
import wg.media.screen.fm.model.commandscreencommon.ManageUserInfo;
import wg.user.mobileimsdk.server.controller.IController;
import wg.user.mobileimsdk.server.custom.ModuleController;
import wg.user.mobileimsdk.server.service.SystemImpl;
import wg.user.mobileimsdk.server.util.DateUtils;
import wg.user.mobileimsdk.server.util.MD5Util;

public class LoginSystem extends System_Controller implements ModuleController {
	public void load() {
		String path = getPara("path");
		render(path);
	}

	public void index() {
		render("/login.html");
	}

	@Clear
	public void index1() {
		try {
			if (getSessionAttr(IController.LOGIN_USER) != null) {
				redirect("/");
			} else {
				String ticket = getCookie("ticket");
				if (ticket != null) {
					Connection conn = Jsoup.connect(PropKit.get("ssoCheckStatusUrl")).ignoreContentType(true)
							.timeout(600000);
					Document d = conn.data("appCode", PropKit.get("appCode")).data("ticket", ticket.split("\\$")[0])
							.data("userId", ticket.split("\\$")[1]).post();
					JSONObject msg = JSONObject.parseObject(d.body().text());
					if (msg.getString("status").equals("success")) {
						ManageUserInfo user = SystemImpl.ser.getUserById(msg.getInteger("userId"));
						List<ManagePerm> perm = SystemImpl.ser.getUserPermList(user);
						List<ManagePerm> allperm = SystemImpl.ser.getAllUserPermList();
						setSessionAttr(IController.LOGIN_USER, user);
						setSessionAttr(IController.username, username);
						setSessionAttr(IController.__menu, perm);
						setSessionAttr(IController.__allPerms, allperm);
						setSessionAttr("username", user.getUserName());
						redirect("/");
					} else {
						redirect("/login.html");
					}
				} else {
					redirect("/login.html");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void login() throws Exception {
		String username = getPara("username");
		String password = getPara("password");
		if (StringUtils.isNotBlank(username) && StringUtils.isNotBlank(password)) {
			password = password.trim();
			ManageUserInfo user = SystemImpl.ser.loginUserNew(username);
			JSONObject res = new JSONObject();
			if (null == user) {
				res.put("status", "error");
				renderJson(res);
				return;
			}
			boolean flag = false; // 判断该账号是否还在冻结时间范围内
			if (user.getLockTime() != null) {
				Long lockTime = DateUtils.getDateTime(user.getLockTime()); //
				Long currentTimeMillis = System.currentTimeMillis(); // 当前时间戳
				Long result = currentTimeMillis - lockTime;
				if (result >= 18) {// 冻结时间大于半小时可以登录可以登录
					flag = true;
					SystemImpl.ser.lockingUser(user, "ok");// 错误次数清零
				} else {
					res.put("status", "warn");
					renderJson(res);
					return;
				}
			} else { // 该账号未曾被冻结
				flag = true;
			}
			if (flag && user.getPassword() != null) {
				Integer status = user.getStatus();
				if (status == 1) {
					if (password.equals(user.getPassword().trim())) {
						List<ManagePerm> perm = SystemImpl.ser.getUserPermList(user);
						List<ManagePerm> allperm = SystemImpl.ser.getAllUserPermList();
						JSONArray jsonpb = new JSONArray();
						JSONArray jsonmgrb = new JSONArray();
						for (ManagePerm jsonperm : perm) {
							JSONObject jsonp = new JSONObject();
							jsonp.put("authid", jsonperm.getId());
							jsonp.put("nameLabel", jsonperm.getName());
							jsonp.put("name", jsonperm.getNameEn());
							// jsonp.put("parentName", jsonperm.getParentName());
							jsonp.put("type", jsonperm.getIcon());
							jsonp.put("component", jsonperm.getUrl());
							jsonp.put("path", jsonperm.getPath());
							jsonpb.add(jsonp);
						}
						setSessionAttr(IController.LOGIN_USER, user);
						setSessionAttr(IController.username, username);
						setSessionAttr(IController.__menu, perm);
						setSessionAttr(IController.jsonb, jsonpb);
						// setSessionAttr(IController.USER_MENU, jsonpb);
						setSessionAttr(IController.jsonm, jsonmgrb);
						setSessionAttr(IController.__allPerms, allperm);
						res.put("status", "ok");
						SystemImpl.ser.lockingUser(user, "ok");// 错误次数清零
						if (null != perm && perm.size() > 0) {
							for (ManagePerm p : perm) {
								if (StringUtils.isNotBlank(p.getUrl()) && p.getUrl().contains("/")) {
									res.put("naviUrl", p.getUrl());
									break;
								}
							}
						}
						renderJson(res);
						return;
					} else {
						Integer errorTimes = user.getErrorTimes();
						if (errorTimes != null && errorTimes > 5) {
							res.put("status", "warn"); //
						} else {
							res.put("status", "error");
							SystemImpl.ser.lockingUser(user, "error"); // 密码错误，次数加1
						}
						renderJson(res);
						return;
					}
				} else {
					if ((MD5Util.string2MD5(password).equals(user.getPassword().trim()))) {
						List<ManagePerm> perm = SystemImpl.ser.getUserPermList(user);
						List<ManagePerm> allperm = SystemImpl.ser.getAllUserPermList();
						JSONArray jsonpb = new JSONArray(); // 用户所拥有的菜单
						JSONArray jsonmgrb = new JSONArray();
						for (ManagePerm jsonperm : perm) {
							if (jsonperm != null) {
								JSONObject jsonp = new JSONObject();
								jsonp.put("authid", jsonperm.getId());
								jsonp.put("nameLabel", jsonperm.getName());
								jsonp.put("name", jsonperm.getNameEn());
								// jsonp.put("parentName", jsonperm.getParentName());
								jsonp.put("type", jsonperm.getIcon());
								jsonp.put("component", jsonperm.getUrl());
								jsonp.put("path", jsonperm.getPath());
								jsonpb.add(jsonp);
							}
						}
						getSession().setMaxInactiveInterval(30000);
						setSessionAttr(IController.LOGIN_USER, user);
						setSessionAttr(IController.username, username);
						setSessionAttr(IController.__menu, perm);
						setSessionAttr(IController.jsonb, jsonpb);
						// setSessionAttr(IController.USER_MENU, jsonpb);
						setSessionAttr(IController.jsonm, jsonmgrb);
						setSessionAttr(IController.__allPerms, allperm);
						res.put("status", "ok");
						SystemImpl.ser.lockingUser(user, "ok");// 错误次数清零
						renderJson(res);
						return;
					} else {
						Integer errorTimes = user.getErrorTimes();
						if (errorTimes != null && errorTimes > 5) {
							res.put("status", "warn"); //
						} else {
							res.put("status", "error");
							SystemImpl.ser.lockingUser(user, "error"); // 密码错误，次数加1
						}
						renderJson(res);
						return;
					}
				}
			} else {
				Integer errorTimes = user.getErrorTimes();
				if (errorTimes != null && errorTimes > 5) {
					res.put("status", "warn"); //
				} else {
					res.put("status", "error");
					SystemImpl.ser.lockingUser(user, "error"); // 密码错误，次数加1
				}
				renderJson(res);
				return;
			}
		} else {
			redirect("/login.html");
		}
	}

	@Deprecated
	public void login1() {
		String username = getPara("username");
		String password = getPara("password");
		String IndustryId = PropKit.get("IndustryId");
		if (StringUtils.isNotBlank(username) && StringUtils.isNotBlank(password)) {
			password = password.trim();
			ManageUserInfo user = SystemImpl.ser.loginUser(username, password, IndustryId);
			JSONObject res = new JSONObject();
			if (null == user) {
				res.put("status", "error");
				return;
			}
			if (user.getPassword() != null) {
				Integer status = user.getStatus();
				if (status == 1) {
					if (MD5Util.string2MD5(password).equals(user.getPassword().trim())) {
						List<ManagePerm> perm = SystemImpl.ser.getUserPermList(user);
						List<ManagePerm> allperm = SystemImpl.ser.getAllUserPermList();
						JSONArray jsonpb = new JSONArray();
						JSONArray jsonmgrb = new JSONArray();
						for (ManagePerm jsonperm : perm) {
							JSONObject jsonp = new JSONObject();
							jsonp.put("id", jsonperm.getId());
							jsonp.put("name", jsonperm.getName());
							jsonpb.add(jsonp);
						}
						setSessionAttr(IController.LOGIN_USER, user);
						setSessionAttr(IController.username, username);
						setSessionAttr(IController.__menu, perm);
						setSessionAttr(IController.jsonb, jsonpb);
						setSessionAttr(IController.jsonm, jsonmgrb);
						setSessionAttr(IController.__allPerms, allperm);
						setSessionAttr("username", username);
						res.put("status", "ok");
						if (null != perm && perm.size() > 0) {
							for (ManagePerm p : perm) {
								if (StringUtils.isNotBlank(p.getUrl()) && p.getUrl().contains("/")) {
									res.put("naviUrl", p.getUrl());
									break;
								}
							}
						}
						renderJson(res);
						return;
					} else {
						res.put("status", "error1");
						renderJson(res);
						// renderText("error1");
						return;
					}
				} else {
					if ((MD5Util.string2MD5(password).equals(user.getPassword().trim()))) {
						List<ManagePerm> perm = SystemImpl.ser.getUserPermList(user);
						List<ManagePerm> allperm = SystemImpl.ser.getAllUserPermList();
						// Object[]industryInfo=SystemImpl.ser.getIndustryInfo(user.getInstitutionId());
						JSONArray jsonpb = new JSONArray();
						JSONArray jsonmgrb = new JSONArray();
						for (ManagePerm jsonperm : perm) {
							if (jsonperm != null) {
								JSONObject jsonp = new JSONObject();
								jsonp.put("id", jsonperm.getId());
								jsonp.put("name", jsonperm.getName());
								jsonpb.add(jsonp);
							}
						}
						getSession().setMaxInactiveInterval(30000);
						setSessionAttr(IController.LOGIN_USER, user);
						setSessionAttr(IController.username, username);
						setSessionAttr(IController.__menu, perm);
						setSessionAttr(IController.jsonb, jsonpb);
						setSessionAttr(IController.jsonm, jsonmgrb);
						setSessionAttr(IController.__allPerms, allperm);
						System.out.println(user.toJson());
						setSessionAttr("user", user.toJson());
						setSessionAttr("username", username);
						res.put("status", "ok");
						renderJson(res);
						return;
					} else {
						res.put("status", "error1");
						renderJson(res);
						return;
					}
				}
			} else {
				res.put("status", "error1");
				renderJson(res);
				return;
			}

		} else

		{
			redirect("/login.html");
		}

	}

	// 退出
	public void logout() {
		getSession().removeAttribute(IController.LOGIN_USER);
		renderJson(true);
	}

	/**
	 * 用户名加密直接登录 名称：loginCode 描述：TODO 日期：2018年11月26日-下午5:47:39
	 * 
	 * @author 李健
	 *         测试地址：http://localhost:8080/loginCode?code=21232f297a57a5a743894a0e4a801fc3
	 */
	@Clear
	public void loginCode() {
		String code = getPara("code");
		JSONObject res = new JSONObject();
		ManageUserInfo user = SystemImpl.ser.loginName(code);
		if (user != null) {
			List<ManagePerm> perm = SystemImpl.ser.getUserPermList(user);
			List<ManagePerm> allperm = SystemImpl.ser.getAllUserPermList();
			// Object[]industryInfo=SystemImpl.ser.getIndustryInfo(user.getInstitutionId());
			JSONArray jsonpb = new JSONArray();
			JSONArray jsonmgrb = new JSONArray();
			for (ManagePerm jsonperm : perm) {
				if (jsonperm != null) {
					JSONObject jsonp = new JSONObject();
					jsonp.put("id", jsonperm.getId());
					jsonp.put("name", jsonperm.getName());
					jsonpb.add(jsonp);
				}
			}
			getSession().setMaxInactiveInterval(30);
			setSessionAttr(IController.LOGIN_USER, user);
			setSessionAttr(IController.username, username);
			setSessionAttr(IController.__menu, perm);
			setSessionAttr(IController.jsonb, jsonpb);
			setSessionAttr(IController.jsonm, jsonmgrb);
			setSessionAttr(IController.__allPerms, allperm);
			setSessionAttr("username", username);
			res.put("status", "ok");
			renderJson(res);
			return;
		} else {
			res.put("status", "error1");
			renderJson(res);
			return;
		}
	}

	public void changeLanguage() {
		String language = getPara("language");
		ManageUserInfo user = (ManageUserInfo) getSession().getAttribute("login_user");
		if (null != language) {
			user.set("language", language);
		}
		renderNull();
	}

	/**
	 * 验证码
	 * 
	 * @throws IOException
	 */
	public void getRandomCode() throws IOException {
		// 随机数生成类
		Random random = new Random();

		// 定义验证码的位数
		int size = 5;

		// 定义变量保存生成的验证码
		String vCode = "";
		char c;
		// 产生验证码
		for (int i = 0; i < size; i++) {
			// 产生一个26以内的随机整数
			int number = random.nextInt(26);
			// 如果生成的是偶数，则随机生成一个数字
			if (number % 2 == 0) {
				c = (char) ('0' + (char) ((int) (Math.random() * 10)));
				// 如果生成的是奇数，则随机生成一个字母
			} else {
				c = (char) ((char) ((int) (Math.random() * 26)) + 'A');
			}
			vCode = vCode + c;
		}

		// 保存生成的5位验证码
		getRequest().getSession().setAttribute("vCode", vCode);
		// 验证码图片的生成
		// 定义图片的宽度和高度
		int width = (int) Math.ceil(size * 20);
		int height = 30;
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		// 获取图片的上下文
		Graphics gr = image.getGraphics();
		// 设定图片背景颜色
		gr.setColor(Color.WHITE);
		gr.fillRect(0, 0, width, height);
		// 设定图片边框
		gr.setColor(Color.GRAY);
		gr.drawRect(0, 0, width - 1, height - 1);
		// 画十条干扰线
		for (int i = 0; i < 5; i++) {
			int x1 = random.nextInt(width);
			int y1 = random.nextInt(height);
			int x2 = random.nextInt(width);
			int y2 = random.nextInt(height);
			gr.setColor(randomColor());
			gr.drawLine(x1, y1, x2, y2);
		}
		// 设置字体，画验证码
		gr.setColor(randomColor());
		gr.setFont(randomFont());
		gr.drawString(vCode, 10, 22);
		// 图像生效
		gr.dispose();
		// 输出到页面
		boolean result = ImageIO.write(image, "png", getResponse().getOutputStream());
		if (result) {
			JSONObject json = new JSONObject();
			json.put("vCode", vCode);
			getResponse().getOutputStream().close();
			renderJson(json);
		}

	}

	// 生成随机的字体
	private Font randomFont() {
		Random r = new Random();
		String[] fontNames = { "宋体", "华文楷体", "黑体", "微软雅黑", "楷体_GB2312" };
		int index = r.nextInt(fontNames.length);
		String fontName = fontNames[index];// 生成随机的字体名称
		int style = r.nextInt(4);
		int size = r.nextInt(3) + 24; // 生成随机字号, 24 ~ 28
		return new Font(fontName, style, size);
	}

	// 生成随机的颜色
	private Color randomColor() {
		Random r = new Random();
		int red = r.nextInt(150);
		int green = r.nextInt(150);
		int blue = r.nextInt(150);
		return new Color(red, green, blue);
	}

	public void getSessionValueByKey() {
		String key = getPara("key");
		JSONObject result = new JSONObject();
		if (StringUtils.isNotBlank(key)) {
			Object obj = getRequest().getSession().getAttribute("vCode");
			result.put("data", obj);
		} else {
			Object userInfo = getRequest().getSession().getAttribute("login_user"); // 用户信息
			Object menuInfo = getRequest().getSession().getAttribute("user_menu"); // 菜单列表
			result.put("userInfo", userInfo);
			result.put("menuInfo", menuInfo);
		}
		renderJson(result);
	}
}
