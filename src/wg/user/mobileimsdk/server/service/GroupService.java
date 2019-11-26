package wg.user.mobileimsdk.server.service;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.DbKit;
import com.jfinal.plugin.activerecord.Record;

import io.netty.channel.Channel;
import org.springframework.web.util.HtmlUtils;
import wg.media.screen.fm.model.commandscreencommon.HistoryDialogue;
import wg.media.screen.fm.model.commandscreencommon.ImHistoryMessage;
import wg.media.screen.fm.model.commandscreencommon.ManageGroup;
import wg.media.screen.fm.model.commandscreencommon.ManageGroupUserTaskRel;
import wg.media.screen.fm.model.commandscreencommon.ManageUserInfo;
import wg.openmob.mobileimsdk.server.listener.ServerEventListenerImpl;
import wg.openmob.mobileimsdk.server.processor.LogicProcessor;
import wg.openmob.mobileimsdk.server.protocal.Protocal;
import wg.openmob.mobileimsdk.server.utils.GlobalSendHelper;
import wg.user.mobileimsdk.server.controller.ActionPlanController;
import wg.user.mobileimsdk.server.image.ImageUtil;
import wg.user.mobileimsdk.server.image.ImageUtilHead;
import wg.user.mobileimsdk.server.util.DesUtil;
import wg.user.mobileimsdk.server.util.SendPost;


public class GroupService {
	public static GroupService ser = new GroupService();
	private static int cpus = Runtime.getRuntime().availableProcessors();
	private static ExecutorService executes = Executors.newFixedThreadPool((int) Math.pow(2, cpus));
	public static String webServerIpPort = PropKit.get("webServerIpPort");

	public JSONObject getCreatGroup(Integer clusterId, String groupName, String userIds, Integer taskId, String dir,
			String pth, String group_type) {
		JSONObject json = new JSONObject();
		ManageGroup manageGroup = new ManageGroup();
		if (groupName == null || groupName.equals("") || userIds == null || userIds.equals("")) {// 群组名称和群组成员不能为空
			json.put("result", "fail");
			json.put("status", "-1");
			json.put("msg", "群成员或群组不能为空");
			return json;
		}
		groupName=HtmlUtils.htmlUnescape(groupName);
		if (userIds == null || userIds.equals("")) {// 判断如果 群成员里面没有群主就添加群主
			String[] userIdss = userIds.split(",");
			ArrayList<String> picUrls = new ArrayList<String>();
			boolean userInclude = false;
			for (String ud : userIdss) {
				Integer userId = Integer.valueOf(ud);
				if (userId.equals(clusterId) || clusterId == userId) {
					userInclude = true;
				}
			}
			if (!userInclude) {
				userIds = clusterId.toString() + "," + userIds;
			}
		}

		manageGroup.setGroupName(groupName);
		manageGroup.setGroupType(1);
		manageGroup.save();// 创建群组

		Integer groupId = manageGroup.getGroupId();
		try {

			String[] userIdss = userIds.split(",");
			ArrayList<String> picUrls = new ArrayList<String>();
			int iconNum=0;
			for (String ud : userIdss) {
				Integer userId = Integer.valueOf(ud);
				String sql = "select user_id,user_name,icon,token from manage_user_info where user_id=?";
				ManageUserInfo manageUserInfo = ManageUserInfo.dao.findFirst(sql, new Object[] { userId });
				if (manageUserInfo != null) {
					 System.out.println(dir + manageUserInfo.getIcon());
					if(iconNum>9) break;
					picUrls.add(dir + manageUserInfo.getIcon());
				}
				iconNum++;
			}
			String pic = String.valueOf(groupId);
			// 九宫格头像
			System.out.println("群组头像地址：" + dir);
			ImageUtilHead.getCombinationOfhead(picUrls, dir, pic);
			String path = "chatsource/groupPicture/" + groupId + ".jpg";
			Db.update("update manage_group set icon = '" + path + "' where group_id = '" + groupId + "'");
			String status = "200";
			// 保存到数据库
			Db.update("update manage_group set status = '" + status + "' where group_id = '" + groupId + "'");
			for (String ud : userIdss) {
				ManageGroupUserTaskRel manageGroupUserTaskRel = new ManageGroupUserTaskRel();
				Integer userId = Integer.valueOf(ud);
				manageGroupUserTaskRel.setGroupId(groupId);
				manageGroupUserTaskRel.setUserId(userId);
				if (userId == clusterId || userId.equals(clusterId)) {
					manageGroupUserTaskRel.setUserType(1);
				} else {
					manageGroupUserTaskRel.setUserType(2);
				}
				if (taskId != null) {
					manageGroupUserTaskRel.setTaskId(taskId);
				}

				manageGroupUserTaskRel.save();
			}

			String[] userIdg = userIds.split(",");
			for (String ud : userIdg) {
				Integer userId = Integer.valueOf(ud);
				// 创建网页端对话框
				HistoryDialogue dialogue = new HistoryDialogue();
				dialogue.setUserId(userId);
				dialogue.setDtype(2);
				dialogue.setStatus(1);
				dialogue.setGroupId(groupId);
				dialogue.setStatus(1);
				dialogue.save();

			}

			// 返回到前端
			json.put("message", "建群成功");
			json.put("status", 1);
			json.put("groupId", groupId);
			json.put("groupName", groupName);
			String sql = "select icon from manage_group where group_id=?";
			ManageGroup manageGroup2 = ManageGroup.dao.findFirst(sql, groupId);
			json.put("groupIcon", pth + manageGroup2.getIcon());
			return json;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
	public JSONObject auditGroup(Integer clusterId, String groupName, String userIds, Integer taskId, String dir,
			String pth, String group_type) {
		JSONObject json = new JSONObject();
		List<ManageGroupUserTaskRel> manageGroupUserTaskRelList=ManageGroupUserTaskRel.dao.
				find("select * from manage_group_user_task_rel where task_id="+taskId);
		Integer groupId = manageGroupUserTaskRelList.get(0).getGroupId();
		ManageGroup manageGroup=ManageGroup.dao.findById(groupId);
		
		if (groupName == null || groupName.equals("") || userIds == null || userIds.equals("")) {// 群组名称和群组成员不能为空
			json.put("result", "fail");
			json.put("status", "-1");
			json.put("msg", "群成员或群组不能为空");
			return json;
		}
		groupName= HtmlUtils.htmlUnescape(groupName);
		if (userIds == null || userIds.equals("")) {// 判断如果 群成员里面没有群主就添加群主
			String[] userIdss = userIds.split(",");
			ArrayList<String> picUrls = new ArrayList<String>();
			boolean userInclude = false;
			for (String ud : userIdss) {
				Integer userId = Integer.valueOf(ud);
				if (userId.equals(clusterId) || clusterId == userId) {
					userInclude = true;
				}
			}
			if (!userInclude) {
				userIds = clusterId.toString() + "," + userIds;
			}
		}

		manageGroup.setGroupName(groupName);
		manageGroup.update();// 修改群组
		
		try {
			String[] userIdss = userIds.split(",");
			ArrayList<String> picUrls = new ArrayList<String>();
			int iconNum=0;
			for (String ud : userIdss) {
				Integer userId = Integer.valueOf(ud);
				String sql = "select user_id,user_name,icon,token from manage_user_info where user_id=?";
				ManageUserInfo manageUserInfo = ManageUserInfo.dao.findFirst(sql, new Object[] { userId });
				if (manageUserInfo != null) {
					 System.out.println(dir + manageUserInfo.getIcon());
					if(iconNum>9) break;
					picUrls.add(dir + manageUserInfo.getIcon());
				}
				iconNum++;
			}
			String pic = String.valueOf(groupId);
			// 九宫格头像
			System.out.println("群组头像地址：" + dir);
			ImageUtilHead.getCombinationOfhead(picUrls, dir, pic);
			String path = "chatsource/groupPicture/" + groupId + ".jpg";
			Db.update("update manage_group set icon = '" + path + "' where group_id = '" + groupId + "'");
			
			String status = "200";
			// 保存到数据库
			Db.update("update manage_group set status = '" + status + "' where group_id = '" + groupId + "'");
			for(ManageGroupUserTaskRel manageGroupUserTaskRel :manageGroupUserTaskRelList){
				boolean flag=true;
				for (String ud : userIdss) {
					Integer userId = Integer.valueOf(ud);
					if(userId==manageGroupUserTaskRel.getUserId()){
						flag=false;
						break;
					}
				}
				if(flag){
					//deleteGroupUser(groupId, manageGroupUserTaskRel.getUserId().toString(), clusterId);
					manageGroupUserTaskRel.delete();
					String sql2 = "select * from history_dialogue where group_id = " + groupId + " and user_id = " + manageGroupUserTaskRel.getUserId()
							+ "  and status = 1";
					HistoryDialogue dis = HistoryDialogue.dao.findFirst(sql2);
					dis.delete();
				}
			}
			for (String ud : userIdss) {
				boolean flag=true;
				Integer userId = Integer.valueOf(ud);
				for(ManageGroupUserTaskRel m :manageGroupUserTaskRelList){
					if(userId==m.getUserId()){
						flag=false;
						break;
					}
				}
				if(flag){
					ManageGroupUserTaskRel manageGroupUserTaskRel = new ManageGroupUserTaskRel();
					manageGroupUserTaskRel.setGroupId(groupId);
					manageGroupUserTaskRel.setUserId(userId);
					if (userId == clusterId || userId.equals(clusterId)) {
						manageGroupUserTaskRel.setUserType(1);
					} else {
						manageGroupUserTaskRel.setUserType(2);
					}
					if (taskId != null) {
						manageGroupUserTaskRel.setTaskId(taskId);
					}
					manageGroupUserTaskRel.save();
					// 创建网页端对话框
					HistoryDialogue dialogue = new HistoryDialogue();
					dialogue.setUserId(userId);
					dialogue.setDtype(2);
					dialogue.setStatus(1);
					dialogue.setGroupId(groupId);
					dialogue.setStatus(1);
					dialogue.save();
				}
			}
			// 返回到前端
			json.put("message", "修改群成功");
			json.put("status", 1);
			json.put("groupId", groupId);
			json.put("groupName", groupName);
			String sql = "select icon from manage_group where group_id=?";
			ManageGroup manageGroup2 = ManageGroup.dao.findFirst(sql, groupId);
			json.put("groupIcon", pth + manageGroup2.getIcon());
			return json;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
	/**
	 * 
	 * @method:getGroupUserInfo
	 * @describe:添加群成员
	 * @author: gongxiangPang
	 * @param :TODO
	 * @param groupId
	 * @param groupName
	 * @param userIds
	 * @param taskId
	 * @param pth
	 * @param dir
	 * @return
	 */
	public JSONArray getGroupUserInfo(Integer groupId, String userIds, Integer taskId, String pth, String dir) {
		JSONArray reArray = new JSONArray();
		JSONObject json = new JSONObject();
		if (userIds == null || groupId == null) {
			json.put("result", "fail");
			json.put("msg", "the data is null");
			reArray.add(json);
			return reArray;
		}
		try {
			if (taskId == null) {
				String groupSql="select * from manage_group_user_task_rel where task_id is not null and group_id="+groupId;
				Record groupRecord=Db.use(DbKit.getConfig(ManageGroupUserTaskRel.class).getName()).findFirst(groupSql);
				if(null!=groupRecord){
					taskId= groupRecord.getInt("task_id");
				}
			}
			
			
			String[] userIdss = userIds.split(",");
			String sql1 = "select mu.user_id userId,mu.icon userIcon "
					+ "from manage_group_user_task_rel mgr,manage_user_info mu "
					+ "where mgr.user_id=mu.user_id  and mgr.group_id=? ";
			List<ManageUserInfo> manageUserInfos = ManageUserInfo.dao.find(sql1, new Object[] { groupId });
			ArrayList<String> picUrls = new ArrayList<String>();
			if (manageUserInfos != null && manageUserInfos.size() > 0) {
				for (ManageUserInfo manageUserInfo : manageUserInfos) {
					picUrls.add(dir + manageUserInfo.getStr("userIcon"));
				}
			}
			for (String ud : userIdss) {
				Integer userId = Integer.valueOf(ud);
			}
			for (String ud : userIdss) {
				Integer userId = Integer.valueOf(ud);
				JSONObject temp = new JSONObject();
				String sql = "select mu.user_name userName,mg.user_id uId,mg.group_id groupId,mu.icon userIcon  "
						+ "from manage_group_user_task_rel mg,manage_user_info mu "
						+ "where mg.user_id=mu.user_id and mg.user_id=? and mg.group_id =?";
				String sql2 = "select * from history_dialogue where group_id = " + groupId + " and user_id = " + userId
						+ "  and status = 1";
				HistoryDialogue dis = HistoryDialogue.dao.findFirst(sql2);
				if (dis == null) {
					dis = new HistoryDialogue();
					dis.setDtype(2);
					dis.setGroupId(groupId);
					dis.setUserId(userId);
					dis.setStatus(1);
					dis.save();
				}
				ManageGroupUserTaskRel manageGroupUserTaskRel = ManageGroupUserTaskRel.dao.findFirst(sql,
						new Object[] { userId, groupId });
				if (manageGroupUserTaskRel != null) {
					continue;
				}
				ManageGroupUserTaskRel manageGroupUserTaskRel2 = new ManageGroupUserTaskRel();
				manageGroupUserTaskRel2.setGroupId(groupId);
				manageGroupUserTaskRel2.setUserId(userId);
				manageGroupUserTaskRel2.setUserType(2);
				if (taskId != null) {
					manageGroupUserTaskRel2.setTaskId(taskId);
				}
				manageGroupUserTaskRel2.save();
				ManageUserInfo manageUserInfo = ManageUserInfo.dao.findFirst(sql, new Object[] { userId, groupId });
				picUrls.add(dir + manageUserInfo.getStr("userIcon"));
				temp.put("userName", manageUserInfo.getStr("userName"));
				temp.put("userId", userId);
				temp.put("userIcon", pth + manageUserInfo.getStr("userIcon"));
				reArray.add(temp);
			}
		
				String pic = String.valueOf(groupId);
				// 九宫格头像
				//ImageUtil.getCombinationOfhead(picUrls, dir, pic);
				ImageUtilHead.getCombinationOfhead(picUrls, dir, pic);
				//String path = "images/groupPicture/" + groupId + ".jpg";
				String path = "chatsource/groupPicture/" + groupId + ".jpg";
				Db.update("update manage_group set icon = '" + path + "' where group_id = '" + groupId + "'");
			
			// 返回到前端
			return reArray;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public JSONObject getGroupUserByGroupId(Integer groupId, String path) {
		JSONObject json = new JSONObject();
		if (groupId == null) {
			json.put("result", "fail");
			json.put("status", "-1");
			json.put("msg", "the data is null");
			return json;
		}
		try {
			String sql = "SELECT mui.user_id userId,mui.icon userIcon,mui.user_name userName,mg.group_id groupId,mgutr.user_type userType,mui.nick_name nickName "
					+ "FROM manage_user_info mui,manage_group mg,manage_group_user_task_rel mgutr "
					+ "WHERE mgutr.group_id=mg.group_id and mui.user_id=mgutr.user_id and mg.group_id=? and mui.status=1";
			List<ManageUserInfo> manageUserInfos = ManageUserInfo.dao.find(sql, new Object[] { groupId });
			JSONArray result = new JSONArray();
			if (manageUserInfos != null && manageUserInfos.size() > 0) {
				for (ManageUserInfo manageUserInfo : manageUserInfos) {
					JSONObject temp = new JSONObject();
					temp.put("groupId", manageUserInfo.getInt("groupId"));
					temp.put("userId", manageUserInfo.getInt("userId"));
					temp.put("userName", manageUserInfo.getStr("userName"));
					temp.put("nickName", manageUserInfo.getStr("nickName"));
					temp.put("userType", manageUserInfo.getInt("userType"));
					temp.put("userIcon", path + manageUserInfo.getStr("userIcon"));
					result.add(temp);

				}
			}
			// 返回到前端
			json.put("result", "success");
			json.put("status", "1");
			json.put("users", result);
			json.put("groupId", groupId);
			return json;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * 
	 * 名称：planToAppMessage 描述：向移动端发送解散群组的通知
	 * 
	 * @author gongxiang.pang
	 * @parameter
	 * @return
	 * @param message
	 * @param fromUserId
	 * @param toUserId
	 * @param typeu
	 */
	public void planToAppMessage(String message, String fromUserId, String toUserId, Integer typeu) {
		System.out.println("删除群成员或者解散群组参数:  " + "message:" + message + "  fromUserId:" + fromUserId + "  toUserId:"
				+ toUserId + "  typeu:" + typeu);
		// int type, String dataContent, String from, String to, boolean QoS, String
		// fingerPrint, int typeu
		String fingerPrint = fromUserId.split("#")[0] + new Date().getTime() + "000#" + toUserId;
		Protocal Protocal = new Protocal(2, message, fromUserId, toUserId, true, fingerPrint, typeu);
		try {
			Set<Channel> set = ServerEventListenerImpl.ser.rooms.get(toUserId);
			if (set != null) {
				for (Channel s : set) {
					String remoteAddress = ActionPlanController.clientInfoToString(s, fromUserId);
					GlobalSendHelper.ser.sendDataC2C(null, s, Protocal, remoteAddress,
							LogicProcessor.serverCoreHandler);
				}
			} else {
				MessageService.ser.saveHisMessage(fingerPrint, fromUserId, toUserId, message, 43, 2, 1, 1);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// 向网页端发送消息
		/*
		 * executes.submit(new Runnable(){
		 * 
		 * @Override public void run() { //逐一发送各个客户端 try { //向网页端发送信息 toWebMassage(
		 * message, fromUserId,toUserId, typeu,new Date().getTime()); } catch (Exception
		 * e) { e.printStackTrace(); } } });
		 */

		// renderText("ok");
	}

	public JSONObject deleteGroupUser(Integer groupId, String userIds, Integer groupUserId) {
		JSONObject json = new JSONObject();
		if (groupId == null) {
			json.put("result", "fail");
			json.put("status", "-1");
			json.put("msg", "the data is null");
			return json;
		}
		String sql = "select * from manage_group where group_id = " + groupId;
		ManageGroup group = ManageGroup.dao.findFirst(sql);
		try {
			String[] userIdss = userIds.split(",");
			// 给删除的群成员发送一条消息
			String fromUserId = groupUserId + "#" + groupId;
			for (int i = 0; i < userIdss.length; i++) {
				String content = "您已被请出[" + group.getGroupName() + "]群";
				boolean res = InformService.ser.addInformPlan(groupUserId.toString(), userIdss[i], "", content, "",
						"1");
				planToAppMessage(String.valueOf(groupId) + "," + group.getGroupName(), fromUserId, userIdss[i], 43);
			}
			// 更新数据库
			Db.update("delete  from manage_group_user_task_rel where group_id = ? and user_id in (" + userIds + ")",
					groupId);
			Db.update("delete  from history_dialogue where group_id = ? and user_id in (" + userIds + ")", groupId);

			json.put("result", "删除成功");
			return json;

		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}

	public JSONObject breakGroup(Integer groupId, Integer userId, String dir) {
		JSONObject json = new JSONObject();
		if (userId == null || groupId == null || groupId == 87) {
			json.put("result", "fail");
			json.put("msg", "the data is null");
			return json;
		}
		String sql1 = "select * from manage_group where group_id = " + groupId;
		ManageGroup group = ManageGroup.dao.findFirst(sql1);
		try {

			// 数据库查询群成员，给每个群成员发送一条消息
			String sql = "SELECT mui.user_id userId "
					+ "FROM manage_user_info mui,manage_group mg,manage_group_user_task_rel mgutr "
					+ "WHERE mgutr.group_id=mg.group_id and mui.user_id=mgutr.user_id and mg.group_id=?";
			List<ManageUserInfo> manageUserInfos = ManageUserInfo.dao.find(sql, new Object[] { groupId });
			String fromUserId = userId + "#" + groupId;
			for (int i = 0; i < manageUserInfos.size(); i++) {
				String content = "[" + group.getGroupName() + "]群已解散";
				String groupUserId = manageUserInfos.get(i).get("userId").toString();
				boolean res = InformService.ser.addInformPlan(userId.toString(), groupUserId, "", content, "", "1");
				planToAppMessage(String.valueOf(groupId) + "," + group.getGroupName(), fromUserId,
						String.valueOf(manageUserInfos.get(i).getInt("userId")), 42);
			}
			// String status="1";
			// 更新数据库
			Db.update("delete  from manage_group where group_id = ?", groupId);
			Db.update("delete  from manage_group_user_task_rel where group_id = ?", groupId);
			Db.update("delete  from history_dialogue where group_id = ?", groupId);
			String URL = dir + "images" + File.separator + "groupPicture";
			File file = new File(URL);
			if (file.isDirectory()) {// 判断file是否是文件目录 若是返回TRUE
				String name[] = file.list();// name存储file文件夹中的文件名
				for (int i = 0; i < name.length; i++) {
					String[] urlname = name[i].split("[.]");
					if (Integer.parseInt(urlname[0].trim()) == groupId) {
						File f = new File(URL, name[i]);// 此时就可得到文件夹中的文件
						f.delete();// 删除文件
						break;
					}
				}
			}

			// 返回到前端
			json.put("result", "解散成功");
			json.put("userId", userId);
			json.put("groupId", groupId);
			return json;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * 获取群组列表
	 * 
	 * @param userId
	 * @return
	 */
	public JSONArray getGroupInfoByUserId(Integer userId, String pth, Integer group_type) {
		String sql = "SELECT mr.user_id,mr.group_id groupId,mg.group_name groupName,mg.icon groupIcon,mr.task_id topicId "
				+ "FROM manage_group_user_task_rel mr,manage_group mg "
				+ "WHERE mr.group_id=mg.group_id and mr.user_id=? and mg.group_type=? and mg.status=200 ";
		List<ManageGroup> list = ManageGroup.dao.find(sql, new Object[] { userId, group_type });
		JSONArray result = new JSONArray();
		if (list != null && list.size() > 0) {
			for (ManageGroup manageGroup : list) {
				JSONObject temp = new JSONObject();
				temp.put("groupId", manageGroup.getInt("groupId"));
				temp.put("groupName", manageGroup.getStr("groupName"));
				temp.put("topicId", manageGroup.getInt("topicId"));
				temp.put("groupIcon", pth + manageGroup.getStr("groupIcon"));
				sql = "select count(*) num from manage_group_user_task_rel where group_id = ?";
				ManageGroupUserTaskRel manageGroupUserTaskRel = ManageGroupUserTaskRel.dao.findFirst(sql,
						manageGroup.getInt("groupId"));
				temp.put("userNum", manageGroupUserTaskRel.getLong("num"));
				result.add(temp);
			}
			return result;
		}
		return null;
	}

	/**
	 * 
	 * @description 根据id获取群组信息
	 * @author ZSJ
	 * @date 2019年5月24日
	 * @param groupId
	 * @param pth
	 * @return
	 */
	public JSONArray getGroupInfoById(String groupId, String pth) {
		String sql = "SELECT mg.group_id groupId,mg.group_name groupName,mg.icon groupIcon,mgr.task_id topicId"
				+ " FROM manage_group mg,manage_group_user_task_rel mgr WHERE mg.group_id=? and mg.status=200 and mg.group_id=mgr.group_id and mgr.user_type=1";
		List<ManageGroup> list = ManageGroup.dao.find(sql, new Object[] { groupId });
		JSONArray result = new JSONArray();
		if (list != null && list.size() > 0) {
			for (ManageGroup manageGroup : list) {
				JSONObject temp = new JSONObject();
				temp.put("groupId", manageGroup.getInt("groupId"));
				temp.put("groupName", manageGroup.getStr("groupName"));
				temp.put("topicId", manageGroup.getInt("topicId"));
				temp.put("groupIcon", pth + manageGroup.getStr("groupIcon"));
				sql = "select count(*) num from manage_group_user_task_rel where group_id = ?";
				ManageGroupUserTaskRel manageGroupUserTaskRel = ManageGroupUserTaskRel.dao.findFirst(sql, groupId);
				temp.put("userNum", manageGroupUserTaskRel.getLong("num"));
				result.add(temp);
			}
		}
		return result;
	}

	// 退群
	public JSONObject quitGroup(Integer groupId, Integer userId) {
		JSONObject json = new JSONObject();
		try {
			// 更新数据库
			Db.update("delete  from manage_group_user_task_rel where user_id = ? and group_id=?",
					new Object[] { userId, groupId });
			// 返回到前端
			json.put("result", "退群成功");
			json.put("userId", userId);
			json.put("groupId", groupId);
			return json;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	// 获取pushToken
	public JSONObject getPushToken(Integer userId, String token) {
		JSONObject result = new JSONObject();

		String sql = "select user_id userId,user_name userName,pushToken from manage_user_info where user_id=? ";
		ManageUserInfo manageUserInfo = ManageUserInfo.dao.findFirst(sql, new Object[] { userId });
		if (manageUserInfo != null) {
			Db.update("update manage_user_info set pushToken = '" + token + "' where user_id = '" + userId + "'");
			result.put("msg", "OK");
			result.put("userId", manageUserInfo.getInt("userId"));
			result.put("userName", manageUserInfo.getStr("userName"));
			return result;
		}
		return null;
	}

	/**
	 * 获取登录用户经纬度
	 * 
	 * @param userId，longitude，latitude
	 * @return
	 */
	public JSONObject getCoordByUserId(Integer userId, Double longitude, Double latitude, String cooType) {

		String gaode_map_url_str = "http://restapi.amap.com/v3/assistant/coordinate/convert?locations=@1,@2&coordsys=gps&output=json&key=0b2d36755acc80d03345c01329308658";
		gaode_map_url_str = gaode_map_url_str.replace("@1", longitude + "");// 经度
		gaode_map_url_str = gaode_map_url_str.replace("@2", latitude + "");// 纬度
		try {
			if (cooType != null && cooType.equals("gps")) {
				Document document = Jsoup.connect(gaode_map_url_str).ignoreContentType(true).get();
				String text = document.text();

				// {"status":"1","info":"ok","infocode":"10000","locations":"116.298957248264,39.886054416233"}
				JSONObject json = JSONObject.parseObject(text);

				if (json.getString("info").equals("ok")) {
					String locations = json.getString("locations");
					String[] split = locations.split(",");
					// 经度
					longitude = Double.parseDouble(split[0]);
					// 纬度
					latitude = Double.parseDouble(split[1]);
				}
			}
			String select_sql = "select * from manage_user_info where user_id =?";
			ManageUserInfo mui = ManageUserInfo.dao.findFirst(select_sql, userId);
			if (mui != null) {
				mui.setLongitude(longitude);
				mui.setLatitude(latitude);
				mui.setUpdateTime(new Date());

				JSONObject temp = new JSONObject();
				if (mui.update()) {
					temp.put("result", "success");
					return temp;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 添加对话框
	 * 
	 * @param dialogue
	 * @return
	 */
	public JSONObject addDialogue(HistoryDialogue dialogue) {
		// TODO Auto-generated method stub
		DateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // HH表示24小时制；
		boolean res = false;
		int re = 0;
		JSONObject result = new JSONObject();
		String sql = "select * from history_dialogue where 1=1";
		if (dialogue.getDtype() == 1) {
			sql += " and (user_id=" + dialogue.getUserId() + " and to_user_id=" + dialogue.getToUserId()
					+ ") or (user_id=" + dialogue.getToUserId() + " and to_user_id=" + dialogue.getUserId() + ")";
		} else if (dialogue.getDtype() == 2) {
			sql += " and user_id=" + dialogue.getUserId() + " and group_id = " + dialogue.getGroupId();
		}
		HistoryDialogue historyDialogue = HistoryDialogue.dao.findFirst(sql);
		if (historyDialogue == null) { // 如果之前没有产生对话框 则创建对话框
			res = dialogue.save();
		} else { // 如果之前有对话框则修改对话框信息
			re = Db.update("delete  from history_dialogue where id = ?", historyDialogue.getId());
			if (re > 0) {
				res = dialogue.save();
			}
		}
		if (res) {
			result.put("msg", "OK");
		}
		return result;
	}

	/**
	 * 获取对话框
	 * 
	 * @param user_id
	 * @return
	 */
	public JSONArray getDialogue(Integer user_id, String pth) {
		// TODO Auto-generated method stub
		String sql = "select * from  history_dialogue where (user_id=? or to_user_id=?) and status = 1  order by create_time desc ";

		List<HistoryDialogue> imList = HistoryDialogue.dao.find(sql, new Object[] { user_id, user_id });
		JSONArray list = new JSONArray();
		ManageUserInfo toUser = null;
		String sql1 = null;
		ImHistoryMessage mes = null;
		for (HistoryDialogue object : imList) {
			JSONObject temp = new JSONObject();
			if (object.getGroupId() == null) {
				sql1 = "select count(*) as count from im_history_message where from_user_id  in ( " + object.getUserId()
						+ "," + object.getToUserId() + ") and to_user_id = " + user_id
						+ " and c_type =1 and send_status = 1 and utype in (11,15)";
				mes = ImHistoryMessage.dao.findFirst(sql1);
				temp.put("num", mes.get("count"));
			} else {
				sql1 = "select count(*) as count from im_history_message where from_user_id - to_user_id != 0 and from_user_id like '%#"
						+ object.getGroupId() + "%' and send_status = 1 and utype in (21,25) and to_user_id = "
						+ object.getUserId();
				mes = ImHistoryMessage.dao.findFirst(sql1);
				temp.put("num", mes.get("count"));
			}
			temp.put("dialogueId", object.getId());
			temp.put("userType", object.getDtype());
			temp.put("create_time", object.get("create_time"));
			if (object.getDtype() == 1) { // 单聊
				if (user_id.equals(object.getUserId())) { // 若为本人发起，则获取对方信息
					toUser = ManageUserInfo.dao.findFirst(
							"select * from manage_user_info where status = 1 and user_id = " + object.getToUserId());
				} else {
					toUser = ManageUserInfo.dao.findFirst(
							"select * from manage_user_info where status = 1 and user_id =" + object.getUserId()); // 若为對方发起，则获取发送方信息
				}
				if (toUser != null) {
					temp.put("userId", toUser.getUserId());
					temp.put("nickName", toUser.getNickName());
					temp.put("userIcon", pth + toUser.getIcon().toString());
					int re = 1;
					for (int i = 0; i < list.size(); i++) {
						JSONObject JSON = (JSONObject) list.get(i);
						if (JSON.get("userId") == toUser.getUserId()) {
							re = 0;
						}
					}
					if (re == 1) {
						list.add(temp);
					}
				}
			} else if (object.getDtype() == 2) { // 群聊
				ManageGroup group = ManageGroup.dao.findFirst(
						"select * from manage_group where status = 200 and group_id =" + object.getGroupId());
				if (group != null) {
					temp.put("userId", group.getGroupId());
					temp.put("nickName", group.getGroupName());
					temp.put("userIcon", pth + group.getIcon().toString());
					int re = 1;
					for (int i = 0; i < list.size(); i++) {
						JSONObject JSON = (JSONObject) list.get(i);
						if (JSON.get("userId") == group.getGroupId()) {
							re = 0;
						}
					}
					if (re == 1) {
						list.add(temp);
					}
				}
			}
		}
		return list;
	}

	/**
	 * 单聊情况下 通过对话框的id 获取历史交流信息 dialogueId 对话框id userId 本人Id
	 * 
	 * @param pageSize
	 * @param pageNo
	 */
	@SuppressWarnings("unchecked")
	public JSONObject getHistoryMessage(Integer selfId, Integer otherId, String pth, Integer pageNo, Integer pageSize) {
		JSONObject json = new JSONObject();
		JSONArray list = new JSONArray();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		ManageUserInfo toUser = null;
		String sql = "select * from im_history_message where from_user_id in (?,?) and to_user_id in (?,?) and c_type =1 and utype in (11,12,13,14,15) order by send_time desc ";
		String sql1 = "select count(*) as count from im_history_message where from_user_id in (?,?) and to_user_id in (?,?) and utype in (11,12,13,14,15) and c_type =1 ";
		String sql2 = "select * from im_history_message  where from_user_id in (?,?) and to_user_id in (?,?) and c_type =1 and send_status = 1 and utype in (11,15)";
		List<ImHistoryMessage> Immes = ImHistoryMessage.dao.find(sql2,
				new Object[] { selfId, otherId, selfId, otherId });
		for (int i = 0; i < Immes.size(); i++) {
			ImHistoryMessage hisIm = Immes.get(i);
			hisIm.setSendStatus(0);
			boolean res = hisIm.update();
		}
		ImHistoryMessage mes = ImHistoryMessage.dao.findFirst(sql1, new Object[] { selfId, otherId, selfId, otherId });
		sql += " limit " + (pageNo - 1) * pageSize + "," + (pageSize);
		List<ImHistoryMessage> ims = ImHistoryMessage.dao.find(sql, new Object[] { selfId, otherId, selfId, otherId });

		for (int i = 0; i < ims.size(); i++) {
			JSONObject temp = new JSONObject();
			ImHistoryMessage his = ims.get(i);

			toUser = ManageUserInfo.dao
					.findFirst("select * from manage_user_info where user_id = " + ims.get(i).getFromUserId());
			if (selfId.equals(Integer.parseInt(ims.get(i).getFromUserId()))) { // 代表该条信息为发送信息
				temp.put("sendStatus", 1); // 1 为发送信息
				temp.put("sendName", toUser.getNickName());
				temp.put("sendIcon", pth + toUser.getIcon().toString());
			} else {
				temp.put("sendStatus", 2); // 2 为接收信息
				temp.put("sendName", toUser.getNickName());
				temp.put("sendIcon", pth + toUser.getIcon().toString());
			}
			temp.put("createTime", format.format(ims.get(i).getCreateTime())); // 文件上传时间
			temp.put("ctype", ims.get(i).getCType());
			temp.put("typeu", ims.get(i).getUtype());
			temp.put("sendTime", ims.get(i).getSendTime());
			temp.put("fingerPrint", ims.get(i).getFingerPrint().substring(ims.get(i).getFingerPrint().length() - 16));
			if (ims.get(i).getUtype() == 11) {
				String content = ims.get(i).getContent();
				try {
					temp.put("content", DesUtil.decrypt(content, "zhongkewenge2018"));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				list.add(temp);
			} else {
				JSONObject content;
				try {
					content = JSONObject.parseObject(DesUtil.decrypt(ims.get(i).getContent(), "zhongkewenge2018"));
					temp.put("content", content);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				list.add(temp);
			}
		}
		json.put("list", list);
		json.put("total", mes.get("count"));
		return json;

	}

	/**
	 * 群聊情况下 通过对话框的id 获取历史交流信息 dialogueId 对话框id userId 本人Id
	 */
	@SuppressWarnings("unchecked")
	public JSONObject getHistoryMessageForGroup(Integer selfId, Integer groupId, String pth, Integer pageNo,
			Integer pageSize) {
		JSONObject json = new JSONObject();
		JSONArray list = new JSONArray();
		ManageUserInfo toUser = null;
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String sql = "select * from im_history_message where ( to_user_id = " + selfId + ") and (from_user_id like '%#"
				+ groupId + "' ) and c_type =2 and utype in (21,22,23,24,25)  order by send_time desc ";
		String sql2 = "select * from im_history_message where ( to_user_id = " + selfId + ") and (from_user_id like '%#"
				+ groupId + "' ) and c_type =2 and send_status=1 and utype in (21,25) order by send_time desc ";
		String sql1 = "select count(*) as count from im_history_message where ( to_user_id = " + selfId
				+ ") and (from_user_id like '%#" + groupId + "' ) and c_type =2 and utype in (21,25)";
		ImHistoryMessage mes = ImHistoryMessage.dao.findFirst(sql1);
		List<ImHistoryMessage> Immes = ImHistoryMessage.dao.find(sql2);
		for (int i = 0; i < Immes.size(); i++) {
			ImHistoryMessage hisIm = Immes.get(i);
			hisIm.setSendStatus(0);
			hisIm.update();
		}
		sql += " limit " + (pageNo - 1) * pageSize + "," + (pageSize);
		List<ImHistoryMessage> ims = ImHistoryMessage.dao.find(sql);
		String sendUser = "";
		String sendId;
		for (int i = 0; i < ims.size(); i++) {
			JSONObject temp = new JSONObject();
			if (ims.get(i).getSendStatus() != 0 && Integer.parseInt(ims.get(i).getToUserId()) == selfId) {
				ims.get(i).setSendStatus(0);
				ims.get(i).update();
			}
			if (selfId.equals(ims.get(i).getFromUserId())) { // 代表该条信息为发送信息
				temp.put("sendStatus", 1); // 1 为发送信息
				toUser = ManageUserInfo.dao
						.findFirst("select * from manage_user_info where user_id = " + ims.get(i).getFromUserId());
				temp.put("sendName", toUser.getNickName());
				temp.put("sendIcon", pth + toUser.getIcon().toString());
			} else {
				sendUser = ims.get(i).getFromUserId();
				sendId = sendUser.substring(0, sendUser.lastIndexOf("#"));
				toUser = ManageUserInfo.dao.findFirst("select * from manage_user_info where user_id = " + sendId);
				temp.put("sendName", toUser.getNickName()); // 获取发送人的昵称和userId
				temp.put("sendIcon", pth + toUser.getIcon().toString());
				if (selfId.equals(Integer.valueOf(sendId))) {
					temp.put("sendStatus", 1); // 1 为发送信息
				} else {
					temp.put("sendStatus", 2); // 2 为接收信息
				}
			}
			temp.put("createTime", format.format(ims.get(i).getCreateTime())); // 文件上传时间
			temp.put("ctype", ims.get(i).getCType());
			temp.put("typeu", ims.get(i).getUtype());
			temp.put("sendTime", ims.get(i).getSendTime());
			String fingerPrint = ims.get(i).getFingerPrint().substring(0, ims.get(i).getFingerPrint().lastIndexOf("#"));
			temp.put("fingerPrint", fingerPrint.substring(fingerPrint.length() - 16));
			if (ims.get(i).getUtype() == 21) {
				String content = ims.get(i).getContent();
				try {
					temp.put("content", DesUtil.decrypt(content, "zhongkewenge2018"));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				list.add(temp);
			} else if (ims.get(i).getUtype() == 22 || ims.get(i).getUtype() == 25) {
				JSONObject content;
				try {
					content = JSONObject.parseObject(DesUtil.decrypt(ims.get(i).getContent(), "zhongkewenge2018"));
					temp.put("content", content);
				} catch (IOException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
				list.add(temp);
			}
		}

		Collections.sort(list, new Comparator() {
			@Override
			public int compare(Object o1, Object o2) {
				JSONObject stu1 = (JSONObject) o1;
				JSONObject stu2 = (JSONObject) o2;
				if (stu1.get("fingerPrint").toString().compareTo(stu2.get("fingerPrint").toString()) > 0) {
					return -1;
				} else if (stu1.get("fingerPrint").toString().compareTo(stu2.get("fingerPrint").toString()) == 0) {
					return 0;
				} else {
					return 1;
				}
			}
		});

		json.put("list", list);
		json.put("total", mes.get("count"));
		return json;

	}

	/**
	 * 文件下载列表
	 * 
	 * @param user_id
	 * @return
	 */
	public JSONArray getFileList(Integer user_id) {
		// TODO Auto-generated method stub
		JSONArray list = new JSONArray();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		ManageUserInfo toUser = null;
		ManageGroup group = null;
		String sql = "select * from im_history_message where (from_user_id like " + user_id + " or to_user_id="
				+ user_id + " )  and utype in (25,15) and status=1  order by create_time desc";
		List<ImHistoryMessage> ims = ImHistoryMessage.dao.find(sql);
		for (ImHistoryMessage imHistoryMessage : ims) {
			JSONObject temp = new JSONObject();
			temp.put("dtype", imHistoryMessage.getCType());// 群聊方式
			temp.put("createTime", format.format(imHistoryMessage.getCreateTime())); // 文件上传时间
			if (imHistoryMessage.getCType() == 1) { // 1单聊 查询发送人
				toUser = ManageUserInfo.dao.findFirst("select * from manage_user_info where status= 1 and user_id = "
						+ imHistoryMessage.getFromUserId());
				if (toUser == null) {
					continue;
				} else {
					temp.put("sourceName", toUser.getUserName());
				}
			} else if (imHistoryMessage.getCType() == 2) { // 1群聊 查询发送人和群 来源拼接为 ‘ 群名-发送人名’
				String[] fromUser = imHistoryMessage.getFromUserId().split("#");
				toUser = ManageUserInfo.dao
						.findFirst("select * from manage_user_info where status= 1 and user_id = " + fromUser[0]);
				group = ManageGroup.dao
						.findFirst("select * from manage_group where status = 200 and group_id =" + fromUser[1]);
				if (toUser == null || group == null) {
					continue;
				}
				temp.put("sourceName", group.getGroupName() + "-" + toUser.getUserName());
			}
			JSONObject content;
			try {
				if (imHistoryMessage.getContent() != null) {
					content = JSONObject
							.parseObject(DesUtil.decrypt(imHistoryMessage.getContent(), "zhongkewenge2018"));
					if (content.get("url") != null) {
						temp.put("content", content);
						list.add(temp);
					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return list;
	}

	/**
	 * 根据群id获取群信息
	 * 
	 * @param groupId
	 * @return
	 */
	public JSONObject getGroup(String groupId) {
		// TODO Auto-generated method stub
		String sql = "select * from manage_group where group_id = " + groupId;
		ManageGroup group = ManageGroup.dao.findFirst(sql);
		JSONObject json = new JSONObject();
		json.put("result", "success");
		json.put("status", "1");
		json.put("group", group);
		return json;
	}

	public void toWebMassage(String message, String fromUserId, String toUserId, Integer typeu, long fingerPrints) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("groupId", message.split(",")[0]);
		map.put("toUserId", toUserId);
		try {
			if (typeu / 10 != 4) {
				map.put("message", DesUtil.decrypt(message, "zhongkewenge2018"));
			} else {
				map.put("message", message);

			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String url = webServerIpPort + "/apptoweb/AppToWebMessage";
		map.put("typeu", typeu);
		map.put("fromUserId", fromUserId);
		map.put("fingerPrints", fingerPrints);

		SendPost.sendPost(url, map);
	}

	public JSONObject getGroupUserByGroupId2(Integer groupId, String path, ManageUserInfo user) {
		Integer my_id=user.getUserId();
		JSONObject json = new JSONObject();
		if (groupId == null) {
			json.put("result", "fail");
			json.put("status", "-1");
			json.put("msg", "the data is null");
			return json;
		}
		try {
			String sql = "SELECT mui.user_id userId,mui.icon userIcon,mui.user_name userName,mg.group_id groupId,mgutr.user_type userType,mui.nick_name nickName "
					+ "FROM manage_user_info mui,manage_group mg,manage_group_user_task_rel mgutr "
					+ "WHERE mgutr.group_id=mg.group_id and mui.user_id=mgutr.user_id and mg.group_id=? and mui.status=1";
			List<ManageUserInfo> manageUserInfos = ManageUserInfo.dao.find(sql, new Object[] { groupId });
			JSONArray result = new JSONArray();
			if (manageUserInfos != null && manageUserInfos.size() > 0) {
				for (ManageUserInfo manageUserInfo : manageUserInfos) {
					if(manageUserInfo.getInt("userId").equals(my_id)){
						continue;
					}
					JSONObject temp = new JSONObject();
					temp.put("groupId", manageUserInfo.getInt("groupId"));
					temp.put("userId", manageUserInfo.getInt("userId"));
					temp.put("userName", manageUserInfo.getStr("userName"));
					temp.put("nickName", manageUserInfo.getStr("nickName"));
					temp.put("userType", manageUserInfo.getInt("userType"));
					temp.put("userIcon", path + manageUserInfo.getStr("userIcon"));
					result.add(temp);

				}
			}
			// 返回到前端
			json.put("result", "success");
			json.put("status", "1");
			json.put("users", result);
			json.put("groupId", groupId);
			return json;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public JSONObject getGroupUserByGroupId3(Integer groupId, String path, Integer userId) {
		JSONObject json = new JSONObject();
		if (groupId == null) {
			json.put("result", "fail");
			json.put("status", "-1");
			json.put("msg", "the data is null");
			return json;
		}
		try {
			String sql = "SELECT mui.user_id userId,mui.icon userIcon,mui.user_name userName,mg.group_id groupId,mgutr.user_type userType,mui.nick_name nickName "
					+ "FROM manage_user_info mui,manage_group mg,manage_group_user_task_rel mgutr "
					+ "WHERE mgutr.group_id=mg.group_id and mui.user_id=mgutr.user_id and mg.group_id=? and mui.status=1";
			List<ManageUserInfo> manageUserInfos = ManageUserInfo.dao.find(sql, new Object[] { groupId });
			JSONArray result = new JSONArray();
			if (manageUserInfos != null && manageUserInfos.size() > 0) {
				for (ManageUserInfo manageUserInfo : manageUserInfos) {
					if(manageUserInfo.getInt("userId").equals(userId)){
						continue;
					}
					JSONObject temp = new JSONObject();
					temp.put("groupId", manageUserInfo.getInt("groupId"));
					temp.put("userId", manageUserInfo.getInt("userId"));
					temp.put("userName", manageUserInfo.getStr("userName"));
					temp.put("nickName", manageUserInfo.getStr("nickName"));
					temp.put("userType", manageUserInfo.getInt("userType"));
					temp.put("userIcon", path + manageUserInfo.getStr("userIcon"));
					result.add(temp);

				}
			}
			// 返回到前端
			json.put("result", "success");
			json.put("status", "1");
			json.put("users", result);
			json.put("groupId", groupId);
			return json;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
}

