package wg.user.mobileimsdk.server.service;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import wg.media.screen.fm.model.commandscreen.Task;
import wg.media.screen.fm.model.commandscreen.TaskLog;
import wg.media.screen.fm.model.commandscreen.Topic;
import wg.media.screen.fm.model.commandscreencommon.ManageUserInfo;
import wg.media.screen.fm.report.IReportService;
public class ReportService implements IReportService{
	/**
	 * 获取所有用户信息
	 * 
	 * @param 
	 * @return
	 */
	public JSONArray getFoShanAllUserInfo(String pth) {
		String sql="select mu.user_id userId,mu.user_name userName,mu.icon userIcon,mu.nick_name nickName,mu.tel userTel,"
				+ "mu.longitude longitude,mu.latitude latitude,mm.media_id mediaId,md.dep_id depId,mm.media_name mediaName,mm.icon mediaIcon,md.dep_name depName "
				+ " from manage_user_info mu,manage_media mm,manage_depart md "
				+ " where mm.media_id=mu.media_id and mu.dep_id=md.dep_id";
    	List<ManageUserInfo>mUserInfos=ManageUserInfo.dao.find(sql);
		JSONArray result=new JSONArray();
		if (mUserInfos!=null&&mUserInfos.size()>0) {
			for (ManageUserInfo manageUserInfo : mUserInfos) {
				JSONObject temp=new JSONObject();
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
	 * 根据主题和任务状态获取任务信息列表
	 * @param statue 
	 * @param tId 
	 * @param tId 主题id，任务状态id
	 * @return
	 */
	public JSONArray getTaskInfoByTopicAndType( Integer pageIndex,Integer pageSize) {
		JSONArray result=new JSONArray();
		String sql="select * from task where status=1 order by creat_time desc";
		String sql1=" limit "+pageIndex * pageSize + " , "+ pageSize;
		List<Task>manageTasks=Task.dao.find(sql+sql1);
		if (manageTasks!=null&&manageTasks.size()>0) {
			for (Task manageTask : manageTasks) {
				JSONObject temp=new JSONObject();
				temp.put("task_id", manageTask.getTId());
				temp.put("title", manageTask.getTaskTitle());
				temp.put("updatetime", manageTask.getUpdatetime());
				temp.put("task_desc", manageTask.getTaskDescription());
				temp.put("author_name", getUserNameById(Integer.valueOf(manageTask.getTaskCreator())+""));
				temp.put("principal", getUserNameById(manageTask.getTaskPrincipal().toString()));
				temp.put("plan", manageTask.getTaskProgress());
				temp.put("start_time", manageTask.getTaskStarttime());
				temp.put("creat_time", manageTask.getCreateTime());
				result.add(temp);
			}
			return result;
		}
		return null;
	}
	public JSONObject getUserNameById(String id) {
		JSONObject result=new JSONObject();
		ManageUserInfo manageUserInfo=ManageUserInfo.dao.findById(id);
		if (manageUserInfo!=null) {
			result.put("userId", manageUserInfo.getUserId());
			result.put("userName", manageUserInfo.getUserName());
			return result;
		}
		return null;
	}
	/**
	 * 根据任务Id获取任务流程信息
	 * @param 任务id
	 * @return
	 */
	public JSONArray getPlanByTadkId(String id) {
		JSONArray result=new JSONArray();
		String sql="select * from task_log where task_id="+id+" order by update_time desc";
		List<TaskLog>manageTaskPlanRels=TaskLog.dao.find(sql);
		if (manageTaskPlanRels.size()>0&&manageTaskPlanRels!=null) {
			for (TaskLog manageTaskPlanRel : manageTaskPlanRels) {
				JSONObject temp=new JSONObject();
				temp.put("id", manageTaskPlanRel.getTaskId());
				Integer uid=manageTaskPlanRel.getPersonalId();
				temp.put("user_name", getUserNameById(uid+""));
				temp.put("task_con_pro", manageTaskPlanRel.getOperationalDesc());
				temp.put("update_time", manageTaskPlanRel.getUpdateTime());
				result.add(temp);
			}
			return result;
		}
		return null;
	}
	/**
	 *获取主题
	 * @param 
	 * @return
	 */
	public JSONArray getTopical(Integer pageIndex,Integer pageSize) {
		JSONArray result=new JSONArray();
		String sql="select * from topic ORDER BY create_time ";
		String sql1=" limit "+pageIndex * pageSize + " , "+ pageSize;
		List<Topic>manageTopicals=Topic.dao.find(sql+sql1);
		if (manageTopicals.size()>0&&manageTopicals!=null) {
			for (Topic manageTopical : manageTopicals) {
				JSONObject temp=new JSONObject();
				temp.put("id", manageTopical.getTId());
				temp.put("top_name", manageTopical.getTopicName());
				temp.put("create_time", manageTopical.getCreatetime());
				result.add(temp);
			}
			return result;
		}
		return null;
	}
	/**
	 *获取任务状态
	 * @param tId 
	 * @param 
	 * @return
	 */
	public JSONArray getTaskStatue() {
		JSONArray result=new JSONArray();
		String sql="select status,count(status) total from task   GROUP BY status ";
		List<Task>manageTasks=Task.dao.find(sql);
		if (manageTasks.size()>0&&manageTasks!=null) {
			for (Task manageTask : manageTasks) {
				JSONObject temp=new JSONObject();
				temp.put("status", manageTask.getTaskStatus());
				temp.put("total", manageTask.getLong("total"));
				result.add(temp);
			}
			return result;
		}
		return null;
	}
}
