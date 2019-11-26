package wg.user.mobileimsdk.server.service;

import java.io.File;
import java.util.List;

import javax.swing.Icon;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.plugin.activerecord.Db;

import wg.media.screen.fm.model.commandscreencommon.ManageGroup;
import wg.media.screen.fm.model.commandscreencommon.ManageGroupUserTaskRel;
import wg.media.screen.fm.model.commandscreencommon.ManageUserInfo;




/**
 * 
 * 类名称：FsUserServices <br/>
 * 类描述：关于群组，成员的业务操作 <br/>
 * 创建时间：2017年12月28日 下午5:45:17 <br/>
 * @author panggongxiang from zhangzhigang
 * @version V1.0
 */
public class FsUserServices {
public static FsUserServices ser = new FsUserServices();

	
	/**
	 * 获取所有用户信息
	 * 
	 * @param userId
	 * @return
	 */
	public JSONArray getFoShanAllUserInfo(Integer userId,String pth) {
		String sql="select mu.user_id userId,mu.user_name userName,mu.icon userIcon,mu.nick_name nickName,mu.tel userTel,"
				+ "mu.longitude longitude,mu.latitude latitude,md.dep_id depId,md.dep_name depName "
				+ " from manage_user_info mu,manage_depart md "
				+ " where  mu.dep_id=md.dep_id";
    	List<ManageUserInfo>mUserInfos=ManageUserInfo.dao.find(sql);
		JSONArray result=new JSONArray();
		if (mUserInfos!=null&&mUserInfos.size()>0) {
			for (ManageUserInfo manageUserInfo : mUserInfos) {
				JSONObject temp=new JSONObject();
				if (manageUserInfo.getInt("userId")==userId) {
					continue;
				}
				temp.put("userId", manageUserInfo.getInt("userId"));
				temp.put("userName", manageUserInfo.getStr("userName"));
				temp.put("nickName", manageUserInfo.getStr("nickName"));
				temp.put("userIcon", pth+manageUserInfo.getStr("userIcon"));
				temp.put("tel", manageUserInfo.getStr("userTel"));
				temp.put("mediaId", manageUserInfo.getInt("mediaId"));
				temp.put("mediaName", manageUserInfo.getStr("mediaName"));
				temp.put("mediaIcon", pth+manageUserInfo.getStr("mediaIcon"));
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
	 * 获取APP登录用户的信息
	 * 
	 * @param userName
	 * @return
	 */
	public JSONObject getFoShanUserInfo(String userName,String pth) {
		String sql="select user_id,user_name,icon,token from manage_user_info where user_name=?";
		ManageUserInfo manageUserInfo=ManageUserInfo.dao.findFirst(sql,new Object[]{userName});
		JSONObject result=new JSONObject();
		if (manageUserInfo!=null) {
			result.put("userId", manageUserInfo.getUserId());
			result.put("userName", manageUserInfo.getUserName());
			result.put("userIcon", pth+manageUserInfo.getIcon());
			result.put("access_token", manageUserInfo.getToken());
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
	public JSONArray getGroupUserInfo(Integer groupId,String pth) {
		String sql="SELECT mui.user_id userId,mui.icon userIcon,mui.user_name userName,mg.group_id groupId,mgutr.user_type userType,mui.nick_name nickName "
				+ "FROM manage_user_info mui,manage_group mg,manage_group_user_task_rel mgutr "
				+ "WHERE mgutr.group_id=mg.group_id and mui.user_id=mgutr.user_id and mg.group_id=?";
		List<ManageUserInfo>manageUserInfos=ManageUserInfo.dao.find(sql, new Object[]{groupId});
		JSONArray result=new JSONArray();
		if (manageUserInfos!=null&&manageUserInfos.size()>0) {
			for (ManageUserInfo manageUserInfo : manageUserInfos) {
				JSONObject temp=new JSONObject();
				temp.put("groupId", manageUserInfo.getInt("groupId"));
				temp.put("userId", manageUserInfo.getInt("userId"));
				temp.put("userName", manageUserInfo.getStr("userName"));
				temp.put("nickName", manageUserInfo.getStr("nickName"));
				temp.put("userType", manageUserInfo.getInt("userType"));
				temp.put("userIcon", pth+manageUserInfo.getStr("userIcon"));
				result.add(temp);
				
			}
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
	public List<ManageGroupUserTaskRel> getGroupUserIds(Integer groupId) {
		String sql="SELECT user_id from manage_group_user_task_rel where group_id=? ";
		List<ManageGroupUserTaskRel> manageUserInfos=ManageGroupUserTaskRel.dao.find(sql, new Object[]{groupId});
		return manageUserInfos;
	}
	/**
	 * 获取群组列表
	 * 
	 * @param userId
	 * @return
	 */
	public JSONArray getGroupInfoByUserId(Integer userId,String pth) {
		String sql="SELECT mr.user_id,mr.group_id groupId,mg.group_name groupName,mg.icon groupIcon "
				+ "FROM manage_group_user_task_rel mr,manage_group mg "
				+ "WHERE mr.group_id=mg.group_id and mr.user_id=? and mg.status=200";
		List<ManageGroup>list=ManageGroup.dao.find(sql, new Object[]{userId});
		JSONArray result=new JSONArray();
		if (list!=null&&list.size()>0) {
			for (ManageGroup manageGroup : list) {
				JSONObject temp=new JSONObject();
				temp.put("groupId", manageGroup.getInt("groupId"));
				temp.put("groupName", manageGroup.getStr("groupName"));
				temp.put("groupIcon", pth+manageGroup.getStr("groupIcon"));
				sql="select count(*) num from manage_group_user_task_rel where group_id = ?";
				ManageGroupUserTaskRel manageGroupUserTaskRel=ManageGroupUserTaskRel.dao.findFirst(sql, manageGroup.getInt("groupId"));
				temp.put("userNum", manageGroupUserTaskRel.getLong("num"));
				result.add(temp);
			}
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
	public JSONObject getCoordByUserId(Integer userId,Double longitude,Double latitude) {
		

			int re=Db.update("update manage_user_info set longitude = '" + longitude + "',latitude= '" + latitude + "' where user_id = '" + userId + "'");
			JSONObject temp=new JSONObject();
			if (re==1) {
				temp.put("result", "success");
				return temp;
			}
			return null;
		
	}
	/**
	 * 修改群名称
	 * 
	 * @param userId
	 * @return
	 */
	public JSONObject updateGroupName(Integer groupId,String groupName) {
		int re=Db.update("update manage_group set group_name = '" + groupName + "' where group_id = ?",groupId);
		JSONObject temp=new JSONObject();
		if (re==1) {
			temp.put("result", "success");
			return temp;
		}
		return null;
	}
	
	/**
	 * 通讯录群图标
	 * 
	 * @param userId
	 * @return
	 */
	public JSONObject getQunZuIcon(String pth) {
		JSONObject result=new JSONObject();
		result.put("icon", pth+"images/icon/qunzu.jpg");
		return result;
	}
	
	
//	/* 
//	 * 创建user并加入指挥中心：groupName：fscc ，id：87
//	 */
//	RcloudService rcloudService=null;
//	public JSONArray creatUserInfo(String userName,String passWord,String nickName,String icon,String pth) {
//		
//		if (userName!=null&&passWord!=null&&nickName!=null) {
//			String sql="select user_name userName from manage_user_info where user_name=?";
//			ManageUserInfo mUserInfo=ManageUserInfo.dao.findFirst(sql, userName);
//			if (mUserInfo==null) {
//				ManageUserInfo manageUserInfo=new ManageUserInfo();
//				manageUserInfo.setUserName(userName);
//				manageUserInfo.setPassword(LoginUtils.string2MD5(passWord));
//				manageUserInfo.setNickName(nickName);
//				manageUserInfo.setIcon(icon);
//				manageUserInfo.save();
//				sql="select user_id userId from manage_user_info where user_name= "+userName;
//				ManageUserInfo mInfo=ManageUserInfo.dao.findFirst(sql);
//				Integer groupId=12;
//				String groupName="FSCC";
//				String userIds =String.valueOf(mInfo.getInt(userName));
//				Integer taskId=null;
//				JSONArray result=rcloudService.getGroupUserInfo(groupId,groupName,userIds,taskId,pth);
//				return result;
//			}
//		}
//		
//		return null;
//	}
	
	
	

}
