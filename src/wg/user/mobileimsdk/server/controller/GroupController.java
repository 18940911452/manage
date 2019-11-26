package wg.user.mobileimsdk.server.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.core.Controller;
import com.jfinal.kit.PropKit;

import wg.media.screen.fm.model.commandscreencommon.HistoryDialogue;
import wg.media.screen.fm.model.commandscreencommon.ManageGroup;
import wg.media.screen.fm.model.commandscreencommon.ManageUserInfo;
import wg.user.mobileimsdk.server.model.vo.ResultMessageVo;
import wg.user.mobileimsdk.server.service.GroupService;

/**
 * 
 * 类名称：GroupController <br/>
 * 类描述：群组相关配置操作 <br/>
 * 创建时间：2017年12月28日 下午6:28:20 <br/>
 * 
 * @author panggongxiang
 * @version V1.0
 */
public class GroupController extends Controller {
	public static String headAdress = PropKit.get("headAdress");
	public static String localHeadAdress = PropKit.get("localHeadAdress");

	private ManageUserInfo user;

	private ManageUserInfo getManageUserInfo() {
		if (user == null) {
			user =  getSessionAttr(IController.LOGIN_USER);
		}
		return user;
	}

	/**
	 * @method:getCreatGroup
	 * @describe:创建群组
	 * @author: gongxiangPang from zhangzhigang
	 * @param :groupName
	 * @param :taskId
	 * @param :clusterId
	 * @param :userIds
	 * http://localhost/group/getCreatGroup?groupName=测试群组3&taskId=1&clusterId=12&userIds=12,13,14,15
	 */
	public void getCreatGroup() {
		int status = -1;
		String message = "查询失败";
		JSONObject result = null;
		String groupName = getPara("groupName");
		Integer taskId = getParaToInt("taskId");
		Integer clusterId = getParaToInt("clusterId");// 创建用户id
		String userIds = getPara("userIds");// 群组成员
		String group_type = getPara("group_type");
		// String
		// dir=getRequest().getSession().getServletContext().getRealPath("/")+File.separator;//获取本地根目录图片路径
		// String
		// pth=getRequest().getScheme()+"://"+getRequest().getServerName()+":"+getRequest().getServerPort()+getRequest().getContextPath()+"/";//浏览器地址
		String pth = headAdress;
		String dir = localHeadAdress;

		try {
			result = GroupService.ser.getCreatGroup(clusterId, groupName, userIds, taskId, dir, pth, group_type);
			String re = result.get("status").toString();
			if (re.equals("1")) {
				status = 1;
				message = "success";
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		renderJson(new ResultMessageVo(status, message, result));
	}
	/**
	 * 修改群组
	 */
	public void auditGroup() {
		int status = -1;
		String message = "查询失败";
		JSONObject result = null;
		String groupName = getPara("groupName");
		Integer taskId = getParaToInt("taskId");
		Integer clusterId = getParaToInt("clusterId");// 创建用户id
		String userIds = getPara("userIds");// 群组成员
		String group_type = getPara("group_type");
		String pth = headAdress;
		String dir = localHeadAdress;

		try {
			result = GroupService.ser.auditGroup(clusterId, groupName, userIds, taskId, dir, pth, group_type);
			String re = result.get("status").toString();
			if (re.equals("1")) {
				status = 1;
				message = "success";
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		renderJson(new ResultMessageVo(status, message, result));
	}
	public void getGroup() {
		int status = -1;
		String message = "查询失败";
		JSONObject result = null;
		String groupId = getPara("groupId");
		// String
		// dir=getRequest().getSession().getServletContext().getRealPath("/")+File.separator;//获取本地根目录图片路径
		// String
		// pth=getRequest().getScheme()+"://"+getRequest().getServerName()+":"+getRequest().getServerPort()+getRequest().getContextPath()+"/";//浏览器地址
		String pth = headAdress;
		String dir = localHeadAdress;

		try {
			result = GroupService.ser.getGroup(groupId);
			String re = result.get("status").toString();
			if (re.equals("1")) {
				status = 1;
				message = "success";
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		renderJson(new ResultMessageVo(status, message, result));
	}

	public void getGroupInfoById() {
		int status = -1;
		String message = "查询失败";
		JSONArray result = null;
		String groupId = getPara("groupId");
		String pth = headAdress;
		try {
			result = GroupService.ser.getGroupInfoById(groupId, pth);
			status = 1;
			message = "success";

		} catch (Exception e) {
			e.printStackTrace();
		}
		renderJson(new ResultMessageVo(status, message, result));
	}

	/**
	 * @method:getGroupUserInfo
	 * @describe: 添加群成员
	 * @author: gongxiangPang from zhangzhigang
	 * @param :TODO
	 */
	public void addGroupUser() {
		int status = -1;
		String message = "查询失败";
		JSONArray result = null;
		Integer groupId = getParaToInt("groupId");
		String userIds = getPara("userIds");
		Integer taskId = getParaToInt("taskId");
		// String
		// dir=getRequest().getSession().getServletContext().getRealPath("/")+File.separator;
		// String
		// pth=getRequest().getScheme()+"://"+getRequest().getServerName()+":"+getRequest().getServerPort()+getRequest().getContextPath()+"/";
		String pth = headAdress;
		String dir = localHeadAdress;
		try {
			result = GroupService.ser.getGroupUserInfo(groupId, userIds, taskId, pth, dir);
			status = 1;
			message = "success";
		} catch (Exception e) {
			e.printStackTrace();
		}
		renderJson(new ResultMessageVo(status, message, result));
	}

	/**
	 * 
	 * @method:getGroupUserByGroupId
	 * @describe:获取群成员信息
	 * @author: gongxiangPang
	 * @param :TODO
	 */
	public void getGroupUserByGroupId() {
		int status = -1;
		String message = "查询失败";
		JSONObject result = null;
		Integer groupId = getParaToInt("groupId");
		// String
		// pth=getRequest().getScheme()+"://"+getRequest().getServerName()+":"+getRequest().getServerPort()+getRequest().getContextPath()+"/";
		String pth = headAdress;
		String dir = localHeadAdress;
		try {
			result = GroupService.ser.getGroupUserByGroupId(groupId, pth);
			status = 1;
			message = "success";
		} catch (Exception e) {
			e.printStackTrace();
		}

		renderJson(new ResultMessageVo(status, message, result));

	}

	/**
	 * 
	 * @method:getGroupUserByGroupId
	 * @describe:获取群成员信息
	 * @author: gongxiangPang
	 * @param :TODO
	 */
	public void getGroupUserByGroupId_jsonp() {
		String callback = getPara("callback");
		int status = -1;
		String message = "查询失败";
		JSONObject result = null;
		Integer groupId = getParaToInt("groupId");
		// String
		// pth=getRequest().getScheme()+"://"+getRequest().getServerName()+":"+getRequest().getServerPort()+getRequest().getContextPath()+"/";
		String pth = headAdress;
		String dir = localHeadAdress;
		try {
			result = GroupService.ser.getGroupUserByGroupId(groupId, pth);
			status = 1;
			message = "success";
		} catch (Exception e) {
			e.printStackTrace();
		}
		renderText(callback + "(" + result.toJSONString() + ")");

	}

	/**
	 *
	 * @method:getGroupUserByGroupId
	 * @describe:获取群成员信息(不包括自己)
	 * @author: baizhenyu
	 * @param :TODO
	 */
	public void getGroupUserByGroupId2() {
		int status = -1;
		String message = "查询失败";
		JSONObject result = null;
		Integer groupId = getParaToInt("groupId");
		System.out.println("groupId = " + groupId);
		Integer userId = getParaToInt("userId");
		// String
		// pth=getRequest().getScheme()+"://"+getRequest().getServerName()+":"+getRequest().getServerPort()+getRequest().getContextPath()+"/";
		String pth = headAdress;
		String dir = localHeadAdress;
		ManageUserInfo user = getManageUserInfo();
		System.out.println("user = " + user);
		if(user == null) {
			user = ManageUserInfo.dao.findById(userId);
		}
		try {
			result = GroupService.ser.getGroupUserByGroupId2(groupId, pth,user);
			status = 1;
			message = "success";
		} catch (Exception e) {
			e.printStackTrace();
		}

		renderJson(new ResultMessageVo(status, message, result));

	}

	/**
	 *
	 * @method:getGroupUserByGroupId
	 * @describe:获取群成员信息(不包括自己)
	 * @author: baizhenyu
	 * @param :TODO
	 * 支持云南app发布会议调用通讯录新增接口
	 */
	public void getGroupUserByGroupId3() {
		int status = -1;
		String message = "查询失败";
		JSONObject result = null;
		Integer groupId = getParaToInt("groupId");
		System.out.println("groupId = " + groupId);
		Integer userId = getParaToInt("userId");
		System.out.println("userId = " + userId);
		// String
		// pth=getRequest().getScheme()+"://"+getRequest().getServerName()+":"+getRequest().getServerPort()+getRequest().getContextPath()+"/";
		String pth = headAdress;
		String dir = localHeadAdress;
//		ManageUserInfo user = getManageUserInfo();
//		System.out.println("user = " + user);
//		if(user == null) {
//			user = ManageUserInfo.dao.findById(userId);
//		}
		try {
			result = GroupService.ser.getGroupUserByGroupId3(groupId, pth,userId);
			status = 1;
			message = "success";
		} catch (Exception e) {
			e.printStackTrace();
		}

		renderJson(new ResultMessageVo(status, message, result));

	}

	/**
	 * 
	 * @method:breakGroup
	 * @describe:解散群
	 * @author: gongxiangPang from zhangzhigang
	 * @param :groupId
	 *            群组id http://localhost/group/breakGroup?groupId=256&userId=12
	 */
	public void breakGroup() {
		int status = -1;
		String message = "查询失败";
		JSONObject result = null;
		Integer groupId = getParaToInt("groupId");
		Integer userId = getParaToInt("userId");
		// String
		// dir=getRequest().getSession().getServletContext().getRealPath("/")+File.separator;
		String pth = headAdress;
		String dir = localHeadAdress;
		try {
			result = GroupService.ser.breakGroup(groupId, userId, dir);
			status = 1;
			message = "success";
		} catch (Exception e) {
			e.printStackTrace();
		}
		renderJson(new ResultMessageVo(status, message, result));
	}

	/**
	 * @method:deleteGroupUser
	 * @describe:删除群成员
	 * @author: haoyueyu
	 * @param :groupId:群组id
	 *            userIds:删除的群成员的id groupUserId:群主id
	 */
	public void deleteGroupUser() {
		int status = -1;
		String message = "查询失败";
		JSONObject result = null;
		Integer groupId = getParaToInt("groupId");
		Integer groupUserId = getParaToInt("groupUserId");
		String userIds = getPara("userIds");
		String pth = headAdress;
		String dir = localHeadAdress;
		try {
			result = GroupService.ser.deleteGroupUser(groupId, userIds, groupUserId);
			status = 1;
			message = "success";
		} catch (Exception e) {
			e.printStackTrace();
		}
		renderJson(new ResultMessageVo(status, message, result));
	}

	/**
	 * 
	 * @method:getGroupInfoByUserId
	 * @describe: 根据用户id获取该用户所在的群组
	 * @author: gongxiangPang from zhangzhigang
	 * @param :TODO
	 */
	public void getGroupInfoByUserId() {
		int status = -1;
		String message = "查询失败";
		JSONArray result = null;
		Integer userId = getParaToInt("userId");
		Integer group_type = getParaToInt("group_type", 1);

		// String
		// pth=getRequest().getScheme()+"://"+getRequest().getServerName()+":"+getRequest().getServerPort()+getRequest().getContextPath()+"/";
		String pth = headAdress;
		String dir = localHeadAdress;
		try {
			result = GroupService.ser.getGroupInfoByUserId(userId, pth, group_type);
			status = 1;
			message = "success";
		} catch (Exception e) {
			e.printStackTrace();
		}
		renderJson(new ResultMessageVo(status, message, result));
	}

	public String returnJosnResult(int status, String message, JSONArray result) {
		JSONObject jo = new JSONObject();
		jo.put("status", status);
		jo.put("message", message);
		jo.put("result", result.toJSONString());
		return jo.toJSONString();
	}

	public String returnJosnResult(int status, String message, JSONObject result) {
		JSONObject jo = new JSONObject();
		jo.put("status", status);
		jo.put("message", message);
		jo.put("result", result.toJSONString());
		return jo.toJSONString();
	}

	/**
	 * 
	 * @method:breakGroup
	 * @describe:退群
	 * @author: gongxiangPang from zhangzhigang
	 * @param :groupId
	 *            群组id http://localhost/group/breakGroup?groupId=256&userId=12
	 */
	public void quitGroup() {
		int status = -1;
		String message = "查询失败";
		JSONObject result = null;
		Integer groupId = getParaToInt("groupId");
		Integer userId = getParaToInt("userId");
		try {
			result = GroupService.ser.quitGroup(groupId, userId);
			status = 1;
			message = "success";
		} catch (Exception e) {
			e.printStackTrace();
		}
		renderJson(new ResultMessageVo(status, message, result));
	}

	/**
	 * 
	 * @method:
	 * @describe:获取pushToken
	 * @author: zhangzhigang
	 * @param :userId
	 *            用户id token
	 */
	public void getPushToken() {
		int status = -1;
		String message = "查询失败";
		JSONObject result = null;
		String token = getPara("pushToken");
		Integer userId = getParaToInt("userId");
		try {
			result = GroupService.ser.getPushToken(userId, token);
			status = 1;
			message = "success";
		} catch (Exception e) {
			e.printStackTrace();
		}
		renderJson(new ResultMessageVo(status, message, result));
	}

	/**
	 * 获取登录用户经纬度
	 * 
	 * @param userId,longitude,latitude
	 * @return
	 */
	public void getCoordByUserId() {
		int status = -1;
		String message = "此接口暂停服务";
		JSONObject result = null;
		Integer userId = getParaToInt("userId");
		Double longitude = Double.valueOf(getPara("longitude"));
		Double latitude = Double.valueOf(getPara("latitude"));
		String cooType = getPara("type");
		try {
			result = GroupService.ser.getCoordByUserId(userId, longitude, latitude, cooType);
			status = 1;
			message = "success";
		} catch (Exception e) {
			e.printStackTrace();
		}
		renderJson(new ResultMessageVo(status, message, result));
	}

	/**
	 * 名称：addDialogue 描述：添加对话框
	 * 
	 * @author gongxiang.pang
	 * @parameter
	 * @return
	 */
	public void addDialogue() {
		String callback = getPara("callback");
		int status = -1;
		String message = "添加失败";
		JSONObject result = null;
		Integer user_id = getParaToInt("user_id");// 本人id
		HistoryDialogue dialogue = new HistoryDialogue();
		dialogue.setUserId(user_id);
		Integer dtype = getParaToInt("dtype");// 1单聊 2 群聊
		dialogue.setDtype(dtype);
		dialogue.setStatus(1);
		if (dtype == 1) {
			Integer to_user_id = getParaToInt("to_user_id");// 对方id
			dialogue.setToUserId(to_user_id);
		} else if (dtype == 2) {
			Integer group_id = getParaToInt("group_id");// 1单聊 2 群聊
			dialogue.setGroupId(group_id);
		}
		try {
			result = GroupService.ser.addDialogue(dialogue);
			status = 1;
			message = "success";
		} catch (Exception e) {
			e.printStackTrace();
		}
		renderText(callback + "(" + result.toJSONString() + ")");
	}

	/**
	 * 名称：getDialogue 描述：获取对话框
	 * 
	 * @author gongxiang.pang
	 * @parameter
	 * @return
	 */
	public void getDialogue() {
		String callback = getPara("callback");
		JSONArray result = null;
		JSONObject json = new JSONObject();
		Integer user_id = getParaToInt("user_id");// 本人id
		String pth = headAdress;
		try {
			result = GroupService.ser.getDialogue(user_id, pth);
			json.put("result", result);
			json.put("status", 1);
			json.put("message", "success");

		} catch (Exception e) {
			e.printStackTrace();
		}
		renderText(callback + "(" + json.toJSONString() + ")");

	}

	/**
	 * 名称：getDialogue 描述：获取对话框
	 * 
	 * @author gongxiang.pang
	 * @parameter
	 * @return
	 */
	public void getHistoryMsg() {
		String callback = getPara("callback");
		JSONObject json = new JSONObject();
		JSONObject result = null;
		Integer pageNo = getParaToInt("pageNo", 1);
		Integer pageSize = getParaToInt("pageSize", 10);
		Integer dtype = getParaToInt("dtype");// 1单聊 2 群聊
		Integer selfId = getParaToInt("user_id");// 本人id
		String pth = headAdress;
		long pageNumber = 0;
		Long res = 0l;
		try {
			if (dtype == 1) {
				Integer otherId = getParaToInt("targetUserId");// 单聊下对方id
				result = GroupService.ser.getHistoryMessage(selfId, otherId, pth, pageNo, pageSize);
				json.put("result", result.get("list"));
				res = (Long) result.get("total");
			} else if (dtype == 2) {
				Integer groupId = getParaToInt("targetGroupId");// 群聊下 群id
				result = GroupService.ser.getHistoryMessageForGroup(selfId, groupId, pth, pageNo, pageSize);
				json.put("result", result.get("list"));
				res = (Long) result.get("total");
			}
			if (res % pageSize == 0) {
				pageNumber = res / pageSize;
			} else {
				pageNumber = res / pageSize + 1;
			}
			json.put("status", 1);
			json.put("total", pageNumber);
			json.put("message", "success");
		} catch (Exception e) {
			e.printStackTrace();
		}
		renderText(callback + "(" + json.toJSONString() + ")");
	}

	/**
	 * 
	 * 名称：getFileList 描述：
	 * 
	 * @author gongxiang.pang
	 * @parameter
	 * @return
	 */
	public void getFileList() {
		String callback = getPara("callback");
		JSONArray result = null;
		Integer user_id = getParaToInt("user_id");// 本人id
		try {
			result = GroupService.ser.getFileList(user_id);
		} catch (Exception e) {
			e.printStackTrace();
		}
		renderText(callback + "(" + result.toJSONString() + ")");
	}

	/**
	 * 
	 * 名称：getFileList 描述：
	 * 
	 * @author gongxiang.pang
	 * @parameter
	 * @return
	 */
	public void getGroupById() {
		String callback = getPara("callback");
		JSONObject result = null;
		String pth = headAdress;
		JSONObject result1 = new JSONObject();
		String groupId = getPara("groupId");// 本人id
		try {
			result = GroupService.ser.getGroup(groupId);
			ManageGroup group = (ManageGroup) result.get("group");
			if (group != null) {
				result1.put("groupIcon", pth + group.get("icon"));
				result1.put("groupName", group.get("group_name"));
				result1.put("groupId", groupId);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		renderText(callback + "(" + result1.toJSONString() + ")");
	}
}
