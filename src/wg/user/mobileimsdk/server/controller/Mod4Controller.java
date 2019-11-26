package wg.user.mobileimsdk.server.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;

import wg.media.screen.fm.model.commandscreencommon.ManageUserInfo;
import wg.media.screen.fm.vo.ResultMessageVo;
import wg.user.mobileimsdk.server.service.TopicManageService;

public class Mod4Controller extends IController {
	Logger logger = LoggerFactory.getLogger(Mod4Controller.class);

	public void index() {
		render("main.html");
	}

	private TopicManageService topicManageService = new TopicManageService();

	/**
	 * @version 获取当前用户所属系统的主题
	 */
	public void getTopicInfo() {
		ManageUserInfo user = getUser();
		String path = getPara("path");
		String keyword = getPara("keyword");
		Integer pageNo = getParaToInt("pageNo", 1);// 页码
		Integer pageSize = getParaToInt("pageSize", 10);// 每页条数
		JSONObject result= topicManageService.getTopicInfo(pageNo, pageSize, keyword, user);
		setAttr("list", result.get("list"));
		setAttr("total", result.get("total"));
		render(path);
	}

	/**
	 * @version 添加主题
	 */
	public void addTopicInfo() {
		ManageUserInfo user = getUser();
		int status = -1;
		String message = "查询失败";
		JSONObject result = null;
		String name = getPara("name");
		String des = getPara("des");
		String create_time = getPara("create_time");
		try {
			result = topicManageService.addTopicInfo(name, user, des, create_time);
			status = 1;
			message = "success";
		} catch (Exception e) {
			e.printStackTrace();
		}
		renderJson(new ResultMessageVo(status, message, result));
	}

	/**
	 * @version 编辑主题
	 */
	public void editTopicInfo() {
		int status = -1;
		String message = "查询失败";
		JSONObject result = null;
		Integer tId = getParaToInt("tId");
		String name = getPara("name");
		String des = getPara("des");
		try {
			result = topicManageService.editTopicInfo(tId, name, des);
			status = 1;
			message = "success";
		} catch (Exception e) {
			e.printStackTrace();
		}
		renderJson(new ResultMessageVo(status, message, result));
	}

	/**
	 * @version 删除主题
	 */
	public void deleTopicInfo() {
		int status = -1;
		String message = "查询失败";
		JSONObject result = null;
		Integer tId = getParaToInt("tId");
		try {
			result = topicManageService.deleTopicInfo(tId);
			status = 1;
			message = "success";
		} catch (Exception e) {
			e.printStackTrace();
		}
		renderJson(new ResultMessageVo(status, message, result));
	}
}
