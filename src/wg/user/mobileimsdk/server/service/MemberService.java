package wg.user.mobileimsdk.server.service;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import freemarker.ext.beans.BeansWrapper;
import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

import wg.media.screen.fm.model.commandscreen.AppVersion;
import wg.media.screen.fm.model.commandscreen.ImFeedback;
import wg.media.screen.fm.model.commandscreencommon.ManageGroup;
import wg.media.screen.fm.model.commandscreencommon.ManageGroupUserTaskRel;
import wg.media.screen.fm.model.commandscreencommon.ManageRoleUserRel;
import wg.media.screen.fm.model.commandscreencommon.ManageUserInfo;
import wg.user.mobileimsdk.server.util.CreateNamePicture;
import wg.user.mobileimsdk.server.util.DateUtils;
import wg.user.mobileimsdk.server.util.MD5Util;

/**
 * 
 * 类名称：MemberService <br/>
 * 类描述：TODO <br/>
 * 创建时间：2017年12月28日 下午7:31:15 <br/>
 * 
 * @author panggongxiang
 * @version V1.0
 */
public class MemberService {
	public static MemberService ser = new MemberService();

	/**
	 * getAllUserInfogetAllUserInfo
	 *
	 * @param :TODO
	 * @param userId
	 * @param longitude
	 * @param latitude
	 * @return
	 * @method:getCoordByUserId
	 * @describe:添加经纬度
	 * @author: gongxiangPang
	 */
	public JSONObject getCoordByUserId(Integer userId, Double longitude, Double latitude) {
		int re = Db.update("update manage_user_info set longitude = '" + longitude + "',latitude= '" + latitude
				+ "' where user_id = '" + userId + "'");
		JSONObject temp = new JSONObject();
		if (re == 1) {
			temp.put("result", "success");
			return temp;
		}
		return null;
	}

	/**
	 * 获取所有用户信息，排除这个userid,场景，记者地图需要排除当前登录的用户
	 *
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public JSONArray getFoShanAllUserInfo(Integer userId, String pth) throws Exception {
		String sqlUser = "select manage_depart.inst_id from manage_user_info "
				+ " left join manage_depart on manage_user_info.dep_id=manage_depart.dep_id "
				+ " left join manage_institution on manage_depart.inst_id=manage_institution.id "
				+ " where manage_user_info.user_id= '" + userId + "'";
		ManageUserInfo userInfos = ManageUserInfo.dao.findFirst(sqlUser);
		Integer inst_id = userInfos.getInt("inst_id");
		String sql = "select mu.user_id userId,mu.user_name userName,mu.icon  userIcon,mu.nick_name nickName,mu.tel userTel,mu.sex sex,mu.email email,mu.nation nation,mu.birthdate birthdate,"
				+ "mu.longitude longitude,mu.latitude latitude,md.dep_id depId,md.dep_name depName "
				+ " from manage_user_info mu ,manage_depart md "
				+ " where  mu.dep_id=md.dep_id and mu.status=1 and md.inst_id = '" + inst_id + "'";
		List<ManageUserInfo> mUserInfos = ManageUserInfo.dao.find(sql);
		JSONArray result = new JSONArray();
		if (mUserInfos != null && mUserInfos.size() > 0) {
			for (ManageUserInfo manageUserInfo : mUserInfos) {
				JSONObject temp = new JSONObject();
				if (manageUserInfo.getInt("userId") == userId || manageUserInfo.getInt("userId").equals(userId)) {
					continue;
				}
				temp.put("userId", manageUserInfo.getInt("userId"));
				temp.put("userName", manageUserInfo.getStr("userName"));
				temp.put("nickName", manageUserInfo.getStr("nickName"));
				temp.put("userIcon", pth + manageUserInfo.getStr("userIcon"));
				temp.put("tel", manageUserInfo.getStr("userTel"));
				temp.put("sex", manageUserInfo.getInt("sex"));
				temp.put("email", manageUserInfo.getStr("email"));
				temp.put("nation", manageUserInfo.getStr("nation"));
				temp.put("birthdate", manageUserInfo.getStr("birthdate"));
				temp.put("depId", manageUserInfo.getInt("depId"));
				temp.put("depName", manageUserInfo.getStr("depName"));
				temp.put("longitude", manageUserInfo.getLongitude());
				temp.put("latitude", manageUserInfo.getLatitude());
				result.add(temp);
			}
			return result;
		}

		return null;
	}

	/**
	 * 名称：getAllReporter 描述：TODO 获取所有用户记者 日期：2019年6月24日-下午5:16:32
	 *
	 * @param userId
	 * @param pth
	 * @return
	 * @throws Exception
	 * @author gongxiang.pang
	 */
	public JSONArray getAllReporter(Integer userId, String pth) throws Exception {

		String sql = "select mu.user_id userId,mu.user_name userName,mu.icon userIcon,mu.nick_name nickName,mu.tel userTel,mu.inst_id instId, "
				+ "mu.longitude longitude,mu.latitude latitude,mm.media_id mediaId,md.dep_id depId,mm.media_name mediaName,mm.icon mediaIcon,md.dep_name depName "
				+ " from manage_user_info mu,manage_media mm,manage_depart md "
				+ " where mm.media_id=mu.media_id and mu.dep_id=md.dep_id and mu.dep_id=3";
		List<ManageUserInfo> mUserInfos = ManageUserInfo.dao.find(sql);
		JSONArray result = new JSONArray();
		if (mUserInfos != null && mUserInfos.size() > 0) {
			for (ManageUserInfo manageUserInfo : mUserInfos) {
				JSONObject temp = new JSONObject();
				temp.put("userId", manageUserInfo.getInt("userId"));
				temp.put("userName", manageUserInfo.getStr("userName"));
				temp.put("nickName", manageUserInfo.getStr("nickName"));
				temp.put("userIcon", pth + manageUserInfo.getStr("userIcon"));
				temp.put("tel", manageUserInfo.getStr("userTel"));
				temp.put("mediaId", manageUserInfo.getInt("mediaId"));
				temp.put("mediaName", manageUserInfo.getStr("mediaName"));
				temp.put("mediaIcon", pth + manageUserInfo.getStr("mediaIcon"));
				temp.put("depId", manageUserInfo.getInt("depId"));
				temp.put("depName", manageUserInfo.getStr("depName"));
				temp.put("icon", manageUserInfo.getStr("icon"));
				temp.put("longitude", manageUserInfo.getLongitude());
				temp.put("latitude", manageUserInfo.getLatitude());
				result.add(temp);
			}
			return result;
		}

		return null;
	}

	/**
	 * 获取所有用户信息，排除这个userid,场景，记者地图需要排除当前登录的用户
	 *
	 * @param userId
	 * @return
	 */
	public JSONArray getAllUserInfoNoSelfByName(String userName, String pth) {
		String sql = "select mu.user_id userId,mu.user_name userName,mu.icon userIcon,mu.nick_name nickName,mu.tel userTel,"
				+ "mu.longitude longitude,mu.latitude latitude,md.dep_id depId,md.dep_name depName "
				+ " from manage_user_info mu,manage_depart md " + " where  mu.dep_id=md.dep_id";
		List<ManageUserInfo> mUserInfos = ManageUserInfo.dao.find(sql);
		JSONArray result = new JSONArray();
		if (mUserInfos != null && mUserInfos.size() > 0) {
			for (ManageUserInfo manageUserInfo : mUserInfos) {
				JSONObject temp = new JSONObject();
				if (manageUserInfo.getUserName() == userName) {
					continue;
				}
				temp.put("userId", manageUserInfo.getInt("userId"));
				temp.put("userName", manageUserInfo.getStr("userName"));
				temp.put("nickName", manageUserInfo.getStr("nickName"));
				temp.put("userIcon", pth + manageUserInfo.getStr("userIcon"));
				temp.put("tel", manageUserInfo.getStr("userTel"));
				temp.put("depId", manageUserInfo.getInt("depId"));
				temp.put("depName", manageUserInfo.getStr("depName"));
				temp.put("longitude", manageUserInfo.getLongitude());
				temp.put("latitude", manageUserInfo.getLatitude());
				result.add(temp);
			}
			return result;
		}
		return null;
	}

	public JSONArray getAllUserInfo(Integer userId, String pth) {
		String sqlUser = "select manage_depart.inst_id from manage_user_info "
				+ " left join manage_depart on manage_user_info.dep_id=manage_depart.dep_id "
				+ " left join manage_institution on manage_depart.inst_id=manage_institution.id "
				+ " where manage_user_info.user_id= '" + userId + "'";
		ManageUserInfo userInfo = ManageUserInfo.dao.findFirst(sqlUser);
		Integer inst_id = userInfo.getInt("inst_id");

		String sql = "select mu.user_id userId,mu.user_name userName,mu.icon userIcon,mu.nick_name nickName,mu.tel userTel,"
				+ " mu.longitude longitude,mu.latitude latitude,md.dep_id depId,md.dep_name depName,mrur.rid roleId "
				+ " from manage_user_info mu "
				+ " left join manage_depart md on mu.dep_id = md.dep_id "
				+ " left join manage_role_user_rel as mrur on mu.user_id=mrur.uid "
				+ " left join manage_role as mr  on mrur.rid=mr.id  "
				+ " where mu.dep_id=md.dep_id and mu.status=1 and md.inst_id = " + inst_id ;
		List<ManageUserInfo> mUserInfos = ManageUserInfo.dao.find(sql);
		JSONArray result = new JSONArray();
		if (mUserInfos != null && mUserInfos.size() > 0) {
			for (ManageUserInfo manageUserInfo : mUserInfos) {
				JSONObject temp = new JSONObject();
				/*
				 * if (manageUserInfo.getInt("userId")==userId) { continue; }
				 */
				temp.put("userId", manageUserInfo.getInt("userId"));
				temp.put("userName", manageUserInfo.getStr("userName"));
				temp.put("nickName", manageUserInfo.getStr("nickName"));
				temp.put("userIcon", pth + manageUserInfo.getStr("userIcon"));
				temp.put("tel", manageUserInfo.getStr("userTel"));
				temp.put("depId", manageUserInfo.getInt("depId"));
				temp.put("depName", manageUserInfo.getStr("depName"));
				temp.put("longitude", manageUserInfo.getLongitude());
				temp.put("latitude", manageUserInfo.getLatitude());
				result.add(temp);

			}
			return result;
		}

		return null;
	}

	/**
	 * 修改群名称
	 *
	 * @param userId
	 * @return
	 */
	public JSONObject updateGroupName(Integer groupId, String groupName) {
		int re = Db.update("update manage_group set group_name = '" + groupName + "' where group_id = ?", groupId);
		JSONObject temp = new JSONObject();
		if (re == 1) {
			temp.put("result", "success");
			temp.put("status", "1");
			temp.put("message", "执行成功");
			return temp;
		}
		return null;
	}

	/**
	 * @param jsonObject
	 * @return
	 * @throws Exception
	 * @description
	 * @author ZSJ
	 * @date 2019年5月10日
	 */
	public boolean ajaxUpdateUserInfo(JSONObject jsonObject) throws Exception {
		ManageUserInfo userInfo = ManageUserInfo.dao.findById(jsonObject.getInteger("userId"));
		if (userInfo != null) {
			userInfo.setEmail(jsonObject.getString("email"));
			userInfo.setBirthdate(jsonObject.getString("birthdate"));
			userInfo.setNation(jsonObject.getString("nation"));
			userInfo.setDepId(jsonObject.getInteger("deptId"));
			userInfo.setTel(jsonObject.getString("userTel"));
			userInfo.setSex(jsonObject.getInteger("sex"));
			if (StringUtils.isNotBlank(jsonObject.getString("newPassword"))) {
				userInfo.setPassword(MD5Util.string2MD5(jsonObject.getString("newPassword")));
			}
			if (StringUtils.isNotBlank(jsonObject.getString("headPortrait"))) {
				userInfo.setIcon(jsonObject.getString("headPortrait"));
			}
		}
		return userInfo.update();
	}

	/**
	 * 名称：updateByString 描述：根据字符串更新信息
	 *
	 * @return
	 * @author gongxiang.pang
	 * @parameter
	 */
	public boolean updateByString(String field, String content, Integer uid) {

		int re = Db.update("update manage_user_info set " + field + " = '" + content + "' where user_id = ?", uid);
		if (re == 1) {
			return true;
		}
		return false;
	}

	/**
	 * 名称：updatePassword 描述：更新密码
	 *
	 * @return
	 * @author gongxiang.pang
	 * @parameter
	 */
	public JSONObject updatePassword(String uid, String oldpassowd, String newpassword) {
		JSONObject result = new JSONObject();
		String sql = "select * from manage_user_info where user_id=?";
		ManageUserInfo manageUserInfo = ManageUserInfo.dao.findFirst(sql, new Object[]{uid});
		if (manageUserInfo != null) {
			String password2 = manageUserInfo.getPassword();
			if (password2 != null) {
				String convertMD5 = MD5Util.string2MD5(oldpassowd);
				String newconvertMD5 = MD5Util.string2MD5(newpassword);
				if (password2.equals(convertMD5)) {
					int re = Db.update(
							"update manage_user_info set  password= '" + newconvertMD5 + "' where user_id = ?", uid);
					if (re > 0) {
						result.put("result", "true");
						result.put("status", "1");
						result.put("message", "执行成功");
					}
				} else {
					result.put("result", "false");
					result.put("status", "-1");
					result.put("message", "旧密码错误");
				}
			}
		} else {
			result.put("result", "false");
			result.put("status", "-1");
			result.put("message", "用户不存在");
		}
		return result;
	}

	public JSONObject ajaxValidatePassword(String oldPassword, String userId) throws Exception {
		JSONObject obj = new JSONObject();
		ManageUserInfo userInfo = ManageUserInfo.dao.findById(userId);
		if (userInfo != null) {
			String password = userInfo.getPassword();
			if (password.equals(MD5Util.string2MD5(oldPassword))) {
				obj.put("result", "true");
				obj.put("message", "密码校验通过");
			} else {
				obj.put("result", "false");
				obj.put("message", "密码校验不通过");
			}
		} else {
			obj.put("result", "false");
			obj.put("message", "密码校验不通过");
		}
		return obj;
	}

	/**
	 * 获取群组列表
	 *
	 * @param userId
	 * @return
	 */
	public JSONArray getGroupInfoByUserId(Integer userId, String pth) {
		String sql = "SELECT mr.user_id,mr.group_id groupId,mg.group_name groupName,mg.icon groupIcon "
				+ "FROM manage_group_user_task_rel mr,manage_group mg "
				+ "WHERE mr.group_id=mg.group_id and mr.user_id=? and mg.status=200";
		List<ManageGroup> list = ManageGroup.dao.find(sql, new Object[]{userId});
		JSONArray result = new JSONArray();
		if (list != null && list.size() > 0) {
			for (ManageGroup manageGroup : list) {
				JSONObject temp = new JSONObject();
				temp.put("groupId", manageGroup.getInt("groupId"));
				temp.put("groupName", manageGroup.getStr("groupName"));
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
	 * 查看群成员列表
	 *
	 * @param userId,groupId
	 * @return
	 */
	public JSONArray getGroupUserInfo(Integer groupId, String pth) {
		String sql = "SELECT mui.user_id userId,mui.icon userIcon,mui.user_name userName,mg.group_id groupId,mgutr.user_type userType,mui.nick_name nickName "
				+ "FROM manage_user_info mui,manage_group mg,manage_group_user_task_rel mgutr "
				+ "WHERE mgutr.group_id=mg.group_id and mui.user_id=mgutr.user_id and mg.group_id=? and mui.status=1";
		List<ManageUserInfo> manageUserInfos = ManageUserInfo.dao.find(sql, new Object[]{groupId});
		JSONArray result = new JSONArray();
		if (manageUserInfos != null && manageUserInfos.size() > 0) {
			for (ManageUserInfo manageUserInfo : manageUserInfos) {
				JSONObject temp = new JSONObject();
				temp.put("groupId", manageUserInfo.getInt("groupId"));
				temp.put("userId", manageUserInfo.getInt("userId"));
				temp.put("userName", manageUserInfo.getStr("userName"));
				temp.put("nickName", manageUserInfo.getStr("nickName"));
				temp.put("userType", manageUserInfo.getInt("userType"));
				temp.put("userIcon", manageUserInfo.getStr("userIcon"));
				result.add(temp);

			}
			return result;
		}
		return null;
	}

	/**
	 * 获取APP登录用户的信息
	 *
	 * @param userName
	 * @return
	 */
	public JSONObject getUserInfoByName(String userName, String pth) {
		String sql = "select user_id,user_name,icon,token from manage_user_info where user_name=?";
		ManageUserInfo manageUserInfo = ManageUserInfo.dao.findFirst(sql, new Object[]{userName});
		JSONObject result = new JSONObject();
		if (manageUserInfo != null) {
			result.put("userId", manageUserInfo.getUserId());
			result.put("userName", manageUserInfo.getUserName());
			result.put("userIcon", pth + manageUserInfo.getIcon());
			result.put("access_token", manageUserInfo.getToken());
			return result;
		}

		return null;
	}

	public JSONObject getUserInfoById(String userId, String pth) {
		String sqlUser = "select manage_depart.inst_id,manage_depart.dep_name from manage_user_info "
				+ " left join manage_depart on manage_user_info.dep_id=manage_depart.dep_id "
				+ " left join manage_institution on manage_depart.inst_id=manage_institution.id "
				+ " where manage_user_info.user_id= '" + userId + "'";
		ManageUserInfo userInfos = ManageUserInfo.dao.findFirst(sqlUser);
		Integer inst_id = userInfos.getInt("inst_id");
		String depName=userInfos.get("dep_name");
		String sqlRole = "select rid from manage_role_user_rel where uid= '" + userId + "' and status=1";
		ManageRoleUserRel roleUserRel = ManageRoleUserRel.dao.findFirst(sqlRole);
		Integer role_id = null;
		if (roleUserRel != null) {
			role_id = roleUserRel.getRid();
		}
		String sql = "select * from manage_user_info where user_id=?";
		ManageUserInfo manageUserInfo = ManageUserInfo.dao.findFirst(sql, new Object[]{userId});
		JSONObject result = new JSONObject();
		if (manageUserInfo != null) {
			result.put("userId", manageUserInfo.getUserId());
			result.put("userName", manageUserInfo.getUserName());
			result.put("userIcon", pth + manageUserInfo.getIcon());
			result.put("access_token", manageUserInfo.getToken());
			result.put("nick_name", manageUserInfo.getNickName());
			result.put("sex", manageUserInfo.getSex());
			result.put("tel", manageUserInfo.getTel());
			result.put("email", manageUserInfo.getEmail());
			result.put("nation", manageUserInfo.getNation());
			result.put("birthdate", manageUserInfo.getBirthdate());
			result.put("instId", inst_id);
			result.put("roleId", role_id);
			result.put("depId", manageUserInfo.getDepId());
			result.put("depName",depName);
			return result;
		}

		return null;
	}

	/**
	 * 查看群成员id
	 *
	 * @param userId,groupId
	 * @return
	 */
	public List<ManageGroupUserTaskRel> getGroupUserIds(Integer groupId, Integer user_id) {
		String sql = "SELECT user_id from manage_group_user_task_rel where group_id=?  and user_id<>?";
		List<ManageGroupUserTaskRel> manageUserInfos = ManageGroupUserTaskRel.dao.find(sql,
				new Object[]{groupId, user_id});
		return manageUserInfos;
	}

	/**
	 * 名称：addUser 描述：
	 *
	 * @param user_name
	 * @param password
	 * @param nick_name
	 * @param tel
	 * @param email
	 * @return
	 * @author gongxiang.pang
	 * @parameter
	 */
	public JSONObject addUser(String userName, String password, String nickName, String tel, Integer depId,
							  String email, String headPath, String basePath, Integer mediaId) {
		JSONObject temp = new JSONObject();
		String timename = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
		String headAddress = "";

		try {
			boolean generateImg = CreateNamePicture.generateImg(nickName, headPath, timename);// 创建头像

			if (generateImg) {
				headAddress = basePath + timename + ".jpg";
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		ManageUserInfo user = new ManageUserInfo();
		user.setUserName(userName);
		user.setNickName(nickName);
//		user.setMediaId(mediaId);
		user.setPassword(MD5Util.string2MD5(password));
		user.setDepId(depId);
		if (tel != null) {
			user.setTel(tel);
		}
		user.setIcon(headAddress);
		boolean re = user.save();

		if (re) {
			temp.put("result", "success");
			temp.put("status", "1");
			temp.put("message", "创建成功");
		} else {
			temp.put("result", "false");
			temp.put("status", "-1");
			temp.put("message", "创建失败");
		}

		return temp;
	}

	public List<Record> queryUserInfoByIds(String ids, String headPath) throws Exception {
		String sql = "select mu.user_id userId,mu.user_name userName,mu.nick_name nickName,mu.tel userTel,mu.sex sex,mu.email email,mu.media_id mediaId,mu.birthdate birthdate,"
				+ " mu.create_time createTime,mu.inst_id instId,mu.dep_id deptId,mu.nation nation,mu.icon userIcon,mu.role roleId from manage_user_info mu where mu.status=1 and mu.user_id in ("
				+ ids + ")";
		List<Record> list = Db.find(sql);
		for (Record record : list) {
			record.set("birthdate", DateUtils.dateFormat(record.getStr("birthdate"))); // 这个受数据库字段类型影响，返回前端有问题，不能直接显示
			record.set("birthday", record.getStr("birthdate"));
			record.set("createTime", DateUtils.dateFormatToString(record.get("createTime")));
			Integer deptId = record.get("deptId"); // 部门Id
			Integer instId = record.get("instId"); // 单位Id
			Record dept = queryDeptByDeptId(deptId, instId);
			record.set("deptName", dept == null ? "" : dept.get("deptName"));
			Record ins = queryInsByInsId(instId);
			record.set("instName", ins == null ? "" : ins.get("instName"));
			if (StringUtils.isNotBlank(record.get("userIcon"))) {
				record.set("userIcon", record.get("userIcon"));
			} else {
				record.set("userIcon", "");
			}
			Record media = queryMediaByMediaId(record.getInt("mediaId"), instId);
			record.set("mediaName", media == null ? "" : media.get("mediaName"));
		}
		return list;
	}

	/**
	 * @param ids
	 * @return
	 * @throws Exception
	 * @description 根据单位id获取所有部门
	 * @author ZSJ
	 * @date 2019年5月9日
	 */
	public List<Record> queryDeptsByInsId(String insId) throws Exception {
		String sql = "SELECT de.dep_id deptId,de.dep_name deptName FROM manage_depart de where de.status = 0 and de.inst_id = "
				+ insId;
		return Db.find(sql);
	}

	/**
	 * @param deptId
	 * @param instId
	 * @return
	 * @throws Exception
	 * @description 根据单位id和部门id获取部门
	 * @author ZSJ
	 * @date 2019年5月10日
	 */
	public Record queryDeptByDeptId(Integer deptId, Integer instId) throws Exception {
		String sql = "SELECT de.dep_id deptId,de.dep_name deptName FROM manage_depart de where de.status = 0 and de.inst_id = "
				+ instId + " and de.dep_id = " + deptId;
		return Db.findFirst(sql);
	}

	/**
	 * @param instId
	 * @return
	 * @throws Exception
	 * @description 根据Id获取单位信息
	 * @author ZSJ
	 * @date 2019年5月10日
	 */
	public Record queryInsByInsId(Integer instId) throws Exception {
		String sql = "SELECT de.id instId,de.name instName FROM manage_institution de where de.status = 0 and de.id = "
				+ instId;
		return Db.findFirst(sql);
	}

	/**
	 * @param mediaId
	 * @param instId
	 * @return
	 * @throws Exception
	 * @description 获取媒体
	 * @author ZSJ
	 * @date 2019年5月10日
	 */
	public Record queryMediaByMediaId(Integer mediaId, Integer instId) throws Exception {
		String sql = "SELECT de.media_id mediaId,de.media_name mediaName FROM manage_media de where de.media_id = "
				+ mediaId + " and de.inst_id = " + instId;
		return Db.findFirst(sql);
	}

	/**
	 * @param ids
	 * @param groupId
	 * @param deptId
	 * @param userName
	 * @return
	 * @throws Exception
	 * @description 排除人员id，群组，部门，人名
	 * @author ZSJ
	 * @date 2019年5月9日
	 */
	public List<Record> queryAllUserInfoByParams(String ids, String groupId, String deptId, String userName)
			throws Exception {
		String sql = "select mu.user_id userId,mu.user_name userName,mu.nick_name nickName,mu.tel "
				+ "userTel,mu.sex sex,mu.email email,mu.media_id mediaId,mu.birthdate birthdate,mu.role roleId "
				+ " from manage_user_info mu where mu.status=1 ";
		StringBuffer sb = new StringBuffer(sql);
		if (StringUtils.isNotBlank(ids)) {
			sb.append("and mu.user_id not in (" + ids + ")");
		}
		if (StringUtils.isNotBlank(groupId)) {
			String groupUser = " and mu.user_id in (select mr.user_id FROM manage_group_user_task_rel mr,manage_group mg "
					+ "WHERE mr.group_id=mg.group_id and mg.status=200 and mg.group_id=" + groupId + ")";
			sb.append(groupUser);
		}
		if (StringUtils.isNotBlank(deptId)) {
			sb.append(" and mu.dep_id=" + deptId);
		}
		if (StringUtils.isNotBlank(userName)) {
			sb.append(" and (mu.user_name like '%" + userName + "%' or mu.nick_name like '%" + userName + "%')");
		}
		List<Record> list = Db.find(sb.toString());
		return list;
	}

	/**
	 * @param ids
	 * @param groupId
	 * @param deptId
	 * @param userName
	 * @return
	 * @throws Exception
	 * @description 人员id，群组，部门，人名
	 * @author ZSJ
	 * @date 2019年5月27日
	 */
	public List<Record> queryUserInfoByParams(String insId, String groupId, String mediaId, String userName,
											  String exceptUserId) throws Exception {
		String sql = "select mu.user_id userId,mu.user_name userName,mu.nick_name nickName,mu.tel "
				+ "userTel,mu.sex sex,mu.email email,mu.media_id mediaId,mu.birthdate birthdate,mu.role roleId "
				+ " from manage_user_info mu where mu.status=1 ";
		StringBuffer sb = new StringBuffer(sql);
		if (StringUtils.isNotBlank(insId)) {
			sb.append("and mu.inst_id = " + insId);
		}
		if (StringUtils.isNotBlank(exceptUserId)) {
			sb.append(" and mu.user_id not in (" + exceptUserId + ")");
		}
		if (StringUtils.isNotBlank(groupId)) {
			String groupUser = " and mu.user_id in (select mr.user_id FROM manage_group_user_task_rel mr,manage_group mg "
					+ "WHERE mr.group_id=mg.group_id and mg.status=200 and mg.group_id=" + groupId + ")";
			sb.append(groupUser);
		}
		if (StringUtils.isNotBlank(mediaId)) {
			sb.append(" and mu.media_id=" + mediaId);
		}
		if (StringUtils.isNotBlank(userName)) {
			sb.append(" and (mu.user_name like '%" + userName + "%' or mu.nick_name like '%" + userName + "%')");
		}
		System.out.println("####:" + sb.toString());
		List<Record> list = Db.find(sb.toString());
		return list;
	}

	public JSONObject getFeedback(String content, String tell, ManageUserInfo user) {
		Integer user_id = user.getUserId();
		JSONObject jsonObject = new JSONObject();
		if(content==null){
			jsonObject.put("status","-1");
			jsonObject.put("message","false");
			return jsonObject;
		}
		if (content!=null) {
			ImFeedback imFeedback = new ImFeedback();
			imFeedback.setContent(content);
			imFeedback.setTell(tell);
			imFeedback.setUserId(user_id);
			try {
				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String da = df.format(new Date());
				Date date = null;
				DateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				date = format1.parse(da);
				imFeedback.setInsterTime(date);
			} catch (Exception e) {

			}
			imFeedback.save();
		}
		jsonObject.put("status","1");
		jsonObject.put("message","true");
		return jsonObject;
	}

	public AppVersion getNewVersion(String type) {
		String sql = "select * from app_version where type = '"+type+"' ";
		sql +=" order by insert_time desc limit 1 ";
		AppVersion  versionsList=null;
		try {
			versionsList=AppVersion.dao.findFirst(sql);
		}catch (Exception e){
			e.printStackTrace();
		}
		return versionsList;
	}

	public JSONObject getUserApp(Integer status, String userId) {
		ManageUserInfo info=new ManageUserInfo();
		info.setUserId(Integer.parseInt(userId));
		info.setAppStatus(status);
		info.update();
		String sql ="select app_status from manage_user_info where user_id = ? ";
		ManageUserInfo userInfo=ManageUserInfo.dao.findFirst(sql, new Object[]{userId});
		Integer appStatus = userInfo.getAppStatus();
		JSONObject json=new JSONObject();
		json.put("status", appStatus);
		return json;
	}

}
