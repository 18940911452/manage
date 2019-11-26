package wg.user.mobileimsdk.server.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.FileChannel;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.plugin.activerecord.Db;

import io.netty.channel.Channel;
import wg.media.screen.fm.model.commandscreen.Task;
import wg.media.screen.fm.model.commandscreen.TaskLog;
import wg.media.screen.fm.model.commandscreen.Topic;
import wg.media.screen.fm.model.commandscreencommon.ManageDepart;
import wg.media.screen.fm.model.commandscreencommon.ManageUserInfo;
import wg.media.screen.fm.report.ITaskAndTopicalService;
import wg.media.screen.fm.vo.TaskResultVo;
import wg.openmob.mobileimsdk.server.listener.ServerEventListenerImpl;
import wg.openmob.mobileimsdk.server.processor.LogicProcessor;
import wg.openmob.mobileimsdk.server.protocal.Protocal;
import wg.openmob.mobileimsdk.server.utils.GlobalSendHelper;
import wg.user.mobileimsdk.server.util.MyTimeUtil;

public class TaskManageService implements ITaskAndTopicalService {
	private MessageService messageService = new MessageService();
	ReportService reportService = new ReportService();
	JiGuangPushService Jpush = new JiGuangPushService();

	// 删除任务管理列表
	public JSONObject delTaskManage(Integer tId) {
		// TODO Auto-generated method stub
		JSONObject result = new JSONObject();
		boolean delManage =Task.dao.deleteById(tId);
		if (delManage == true) {
			result.put("message", "success");
			return result;
		}
		return result;
	}

	// 根据tId查询任务详情
	public JSONArray findTaskDetails(Integer tId) {
		// TODO Auto-generated method stub
		JSONArray result = new JSONArray();
		String sql = "select * from task left join task_log on "
				+ "task.t_id=task_log.task_id where task.t_id=" + tId
				+ "order by task_log.update_time desc";
		List<Task> manageTask = Task.dao.find(sql);
		if (manageTask.size() > 0 && manageTask != null) {
			for (Task manageTask2 : manageTask) {
				JSONObject temp = new JSONObject();
				temp.put("task_id", manageTask2.getTId());
				temp.put("title", manageTask2.getTaskTitle());
				temp.put("updatetime", manageTask2.getUpdatetime());
				temp.put("task_desc", manageTask2.getTaskDescription());
				temp.put("author_name", getUserNameById(Integer.valueOf(manageTask2.getTaskCreator())));
				temp.put("principal", getUserNameById(Integer.parseInt(manageTask2.getTaskPrincipal())));
				temp.put("plan", manageTask2.getTaskProgress());
				temp.put("start_time", manageTask2.getTaskStarttime());
				temp.put("creat_time", manageTask2.getCreateTime());
				result.add(temp);
			}
			return result;
		}
		return null;
	}

	public JSONObject getUserNameById(Integer id) {
		JSONObject result = new JSONObject();
		ManageUserInfo manageUserInfo = ManageUserInfo.dao.findById(id);
		if (manageUserInfo != null) {
			result.put("userId", manageUserInfo.getUserId());
			result.put("userName", manageUserInfo.getUserName());
			return result;
		}
		return null;
	}

	public JSONArray findTaskDetailsAll() {
		// TODO Auto-generated method stub
		return null;
	}

	public Object[] getTaskList(Integer task_id, String task_name, ManageUserInfo user, Integer pageNo,
			Integer pageSize, Integer relation_type) {
		String sql3 ="select  inst_id from manage_depart where dep_id= '"+user.getDepId()+"'";
		ManageDepart depart=ManageDepart.dao.findFirst(sql3);
		Integer sys_user_id = depart.getInt("inst_id");
		Integer user_id = user.getInt("user_id");

		List<String> paramsList = new ArrayList<String>();
		Object[] obj = new Object[2];
		List<TaskResultVo> resultList = new ArrayList<>();
		Boolean isOne = false;
		String select = "ta.*,top.t_id as mtid,top.topic_name as mtname,ui.nick_name as muiname";
		String sql = "select " + select
				+ " from command_screen.task ta "
				+ "left join command_screen.topic top on ta.belong_to_topic=top.t_id "
				+ "left join command_screen_common.manage_user_info ui on ta.task_creator=ui.user_id "
				+ "left join command_screen_common.manage_depart on ui.dep_id=manage_depart.dep_id "
				+ "where manage_depart.inst_id= '"+sys_user_id+"'  and ta.task_status <= 2";
		paramsList.add(sys_user_id.toString());

		if (null != relation_type) {
			if(relation_type==1){
				sql +=" and FIND_IN_SET ('"+user_id+"',ta.task_creator) ";
			}
			if(relation_type==2){
				sql +=" and FIND_IN_SET ('"+user_id+"',ta.task_principal) ";
			}
			if(relation_type==3){
				sql +=" and FIND_IN_SET ('"+user_id+"',ta.task_principal) ";
			}
			paramsList.add(user_id.toString());
		}

		if (StringUtils.isNotBlank(task_name)) {
			sql += " and ta.task_title like '%"+task_name+"%' ";
			paramsList.add("%" + task_name + "%");
		}
		if (null != task_id) {
			isOne = true;
			sql += " and ta.t_id = '"+task_id+"' ";
			paramsList.add(task_id.toString());
		}

		sql += " order by ta.create_time desc ";
		Long total=Db.queryLong(sql.replace(select, "count(1)"));
		if (null != pageNo && null != pageSize) {
			sql += " limit  " + (pageNo - 1) * pageSize + "," + pageSize;
		}
		List<Task> list = Task.dao.find(sql);
		if (null != list && list.size() > 0) {
			for (Task manageTask : list) {
				TaskResultVo vo = manageTask2ResultVo(manageTask);
				if (isOne) { // 详情页
					// 获取 任务操作历史记录 时间倒序
					String sqlPl="select task_log.t_id id,task_log.operational_desc task_con_pro,"
							+ " task_log.update_time,task_log.task_id,task_log.personal_id user_id,task_log.personal_type user_type,"
							+ " manage_user_info.nick_name from command_screen.task_log,command_screen_common.manage_user_info"
							+ " where task_log.task_id='"+task_id+"' and task_log.personal_id=manage_user_info.user_id order by task_log.update_time desc";
					List<TaskLog> planList = TaskLog.dao.find(sqlPl);
					vo.setPlanList(planList);
					// 附件列表
					String sqlFile="select t_id as id,task_id as task_id,enclosure_name as file_name,enclosure_path as file_path,update_time from task_log where task_id= '"+task_id+"'";
					List<TaskLog> fileList=TaskLog.dao.find(sqlFile);
					vo.setFileList(fileList);
					
				}
				resultList.add(vo);
			}
		}
		obj[0] = total;
		obj[1] = resultList;
		return obj;
	}

	private TaskResultVo manageTask2ResultVo(Task manageTask) {
		TaskResultVo vo = new TaskResultVo();
		vo.setTask_id(manageTask.getTId().longValue());
		vo.setTitle(manageTask.getTaskTitle());
		String a=manageTask.getTaskStatus();
		vo.setStatus(Integer.valueOf(a));
		if (org.apache.commons.lang3.StringUtils.isNoneBlank(manageTask.getTaskPrincipal())) {
			vo.setPrincipal(ManageUserInfo.dao.find("select user_id,nick_name from manage_user_info "
					+ " where user_id in (" + manageTask.getTaskPrincipal() + " )"));
		} else {
			vo.setPrincipal(null);
		}
		if (org.apache.commons.lang3.StringUtils.isNoneBlank(manageTask.getTaskParticipant())) {
			vo.setParticipant(ManageUserInfo.dao.find("select user_id,nick_name from manage_user_info "
					+ " where user_id in (" + manageTask.getTaskParticipant() + " )"));
		} else {
			vo.setParticipant(null);
		}
		vo.setCreat_per(manageTask.getStr("muiname"));
		vo.setTop_id(manageTask.getInt("mtid"));
		vo.setTop_name(manageTask.getStr("mtname"));
		vo.setStart_time(manageTask.getTaskStarttime());
		vo.setUpdate_time(manageTask.getUpdatetime());
		vo.setTask_desc(manageTask.getTaskDescription());
		vo.setPlan(manageTask.getTaskProgress());
		vo.setStatus(Integer.valueOf(manageTask.getTaskStatus()));
		return vo;
	}

	public Boolean deleteFileById(Integer id) {
		Boolean f = false;
		TaskLog file = TaskLog.dao.findById(id);
		f = file.delete();
		return f;
	}

	public List<Topic> topicalList(ManageUserInfo user) {
		String sql3 ="select  inst_id from manage_depart where dep_id= '"+user.getDepId()+"'";
		ManageDepart depart=ManageDepart.dao.findFirst(sql3);
		Integer sys_user_id = depart.getInt("inst_id");
		String sql="select topic.update_time,topic.t_id as id,topic.topic_name as top_name,topic.keyword,topic.createtime as create_time from topic where topic.inst_id=?";
		List<Topic> list=Topic.dao.find(sql, sys_user_id);
		return list;
	}

	public Task saveTask(JSONObject json, ManageUserInfo user, Boolean isApp) {
		String title = json.getString("title");// 任务名称
		String task_desc = json.getString("task_desc");// 任务描述
		String principal = json.getString("principal");// 负责人
		String participant = json.getString("participant");// 参与人
		Integer top_id = json.getInteger("top_id");// 主题id
		Date starttime = new Date();
		try {
			starttime = MyTimeUtil.sdf.parse(json.getString(("start_time")));
		} catch (ParseException e) {
			e.printStackTrace();
		} // 开始时间
		Integer task_id = json.getInteger("task_id");// 任务id
		String taskPlan = json.getString("taskPlan");// 编辑时 写的任务追踪描述
		String planNum = json.getString("plan") == null ? "0%" : json.getString("plan");// 任务进度
		String fileIds = json.getString("fileIds");// app 上传编辑时，上传的 附件ids

		Task task = new Task();
		if(planNum.equals("100%")){
			task.setTaskStatus("1");
		}
		else{
			task.setTaskStatus("0");
		}
		task.setTaskTitle(title);
		task.setTaskDescription(task_desc);
		task.setTaskStarttime(starttime);
		task.setTaskParticipant(participant);
		task.setTaskPrincipal(principal);
		task.setBelongToTopic(top_id.toString());
		task.setTaskCreator(user.getUserId().toString());
		task.setTaskProgress(planNum);
		task.setUpdatetime(new Date());
//		task.setTaskStatus("1");
		TaskLog taskLog=new TaskLog();
		taskLog.setOperationalDesc(taskPlan);
		taskLog.setPersonalId(user.getUserId());
		taskLog.setUpdateTime(new Date());
//		ManageTaskPlanRel plan = new ManageTaskPlanRel();
//		plan.setTaskConPro(taskPlan);
//		plan.setUserId(user.getUserId());
//		plan.setUpdateTime(new Date());
		if (null == task_id) { // 创建任务
			task.setTaskStarttime(new Date());
			task.save();
			taskLog.setTaskId(Integer.parseInt(task.getTId().toString()));
			taskLog.save();
		} else {
			Task exsitTask = Task.dao.findById(task_id);
			if (null != exsitTask) {
				task.setTId(exsitTask.getTId());
//				Integer a=planNum.equals("100%") ? 2 : 1;
//				task.setTaskStatus(a.toString());
				task.update();
				taskLog.setTaskId(task_id);
				taskLog.save();
			}
		}

		if (isApp && org.apache.commons.lang3.StringUtils.isNoneBlank(fileIds)) { // app
																					// 的请求，需要再次关联
																					// 附件表
			String[] split = fileIds.split(",");
			for (String fileId : split) {
				TaskLog file=TaskLog.dao.findById(fileId.trim());
//				ManageTaskFile file = ManageTaskFile.dao.findById(fileId.trim());
				if (null != file) {
					file.setTId(task.getTId());
					file.update();
				}
			}
		}

		return task;
	}

	public Task updataTaskProgress(JSONObject json, ManageUserInfo user, Boolean isApp) {
		Integer task_id = json.getInteger("task_id");// 任务id
		String taskPlan = json.getString("taskPlan");// 编辑时 写的任务追踪描述
		String planNum = json.getString("plan");// 编辑时 写的任务进度

		Task task = Task.dao.findById(task_id);
		if (null != task) {
			TaskLog plan = new TaskLog();
			plan.setTaskId(task_id);
			plan.setOperationalDesc(taskPlan);
			plan.setPersonalId(user.getUserId());
			plan.setUpdateTime(new Date());
			plan.save();

			task.setTaskProgress(planNum);
			task.update();

		}

//		 if(isApp &&
//		 org.apache.commons.lang3.StringUtils.isNoneBlank(fileIds)){ //app
//		 的请求，需要再次关联 附件表
//		 String[] split = fileIds.split(",");
//		 for (String fileId : split) {
//		 ManageTaskFile file = ManageTaskFile.dao.findById(fileId.trim());
//		 if(null != file){
//		 file.setTaskId(task.getTaskId());
//		 file.update();
//		 }
//		 }
//		 }

		return task;
	}

	public void ImInform2App(String message, Task t, ManageUserInfo user, Integer typeu) {
		HashSet<String> set = new HashSet<>(); // 发送im的用户id列表
		set.add(user.getUserId().toString());
		if (StringUtils.isNotBlank(t.getTaskParticipant())) {
			String[] split = t.getTaskParticipant().split(",");
			for (String pid : split) {
				if (StringUtils.isNotBlank(pid)) {
					set.add(pid);
				}
			}
		}
		if (StringUtils.isNotBlank(t.getTaskPrincipal())) {
			String[] split = t.getTaskPrincipal().split(",");
			for (String pid : split) {
				if (StringUtils.isNotBlank(pid)) {
					set.add(pid);
				}
			}
		}
		for (String toUserId : set) {
			String fingerPrints = user.getUserId().toString() + new Date().getTime() + "000#" + toUserId;
			planToAppMessage(message, user.getUserId().toString(), toUserId, typeu, fingerPrints);// 通知APP
		}

	}

	private void planToAppMessage(String message, String fromUserId, String toUserId, Integer typeu,
			String fingerPrints) {
		Protocal Protocal = new Protocal(2, message, fromUserId, toUserId, true, fingerPrints, typeu);
		try {
			Set<Channel> set = ServerEventListenerImpl.rooms.get(toUserId);
			if (set != null) {
				for (Channel s : set) {
					String remoteAddress = clientInfoToString(s, fromUserId);
					GlobalSendHelper.ser.sendDataC2C(null, s, Protocal, remoteAddress,
							LogicProcessor.serverCoreHandler);
				}
			} else {
				messageService.saveHisMessage(fingerPrints, fromUserId, toUserId, message, typeu, 2, 1, 1);
				try {
					String sql = "select * from manage_user_info where user_id=?";

					ManageUserInfo manageUserInfo = ManageUserInfo.dao.findFirst(sql, new Object[] { fromUserId });
					String fromName = "";
					if (manageUserInfo != null) {
						fromName = manageUserInfo.get("user_name") == null ? manageUserInfo.get("user_name_en")
								: manageUserInfo.get("user_name");
					}
					Jpush.JpushSendMessage(fingerPrints, fromUserId, toUserId, fromName, "", typeu.toString(), message);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String clientInfoToString(Channel session, String from) {
		SocketAddress remoteAddress = session.remoteAddress();
		String s1 = remoteAddress.toString();
		StringBuilder sb = new StringBuilder().append("{uid:").append(from).append("}").append(s1);
		return sb.toString();
	}

	/**
	 * 
	 * @param task
	 * @param fileName
	 * @param dir
	 */
	public TaskLog saveTaskFileMessage(Task task, String fileName, String dir, Boolean flag) {
		TaskLog ta = new TaskLog();
		try {
			if (flag) {
				ta.setTaskId(task.getTId());
			} else {
				ta.setTaskId(null);
			}
			ta.setEnclosureName(fileName);
			ta.setEnclosurePath(dir);
			ta.setUpdateTime(new Date());
			ta.save();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return ta;
	}

	public ManageUserInfo findUserById(String userId) {
		return ManageUserInfo.dao.findById(userId);
	}

	public List<ManageUserInfo> getAllUser(ManageUserInfo user) {
		String sql ="select  inst_id from manage_depart where dep_id= '"+user.getDepId()+"'";
		ManageDepart depart=ManageDepart.dao.findFirst(sql);
		Integer ins_id=depart.get("inst_id");
		System.out.println("ins_id:"+ins_id);
		String sql2="select manage_user_info.*,manage_depart.inst_id as sys_user_id from manage_depart left join manage_user_info "
				+ "on manage_depart.dep_id=manage_user_info.dep_id where manage_depart.inst_id='"+ins_id+"'";
		
		return ManageUserInfo.dao.find(sql2);
	}

	public Boolean deleteTask(Integer task_id) {
		Boolean f = false;
		Task task = Task.dao.findById(task_id);
		if (null != task) {
			Integer a=4;
			task.setTaskStatus(a.toString());
			f = task.update();
		}
		return f;
	}

	public boolean fileChannelCopy(File s, File t) {
		FileInputStream fi = null;
		FileOutputStream fo = null;
		FileChannel in = null;
		FileChannel out = null;
		try {
			fi = new FileInputStream(s);
			fo = new FileOutputStream(t);
			in = fi.getChannel();// 得到对应的文件通道
			out = fo.getChannel();// 得到对应的文件通道
			in.transferTo(0, in.size(), out);// 连接两个通道，并且从in通道读取，然后写入out通道
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				fi.close();
				in.close();
				fo.close();
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}

		}
	}

	public Task findTaskById(Integer task_id) {
		return Task.dao.findById(task_id);
	}

	public JSONObject addTopicInfo(String name, ManageUserInfo user) {
		JSONObject result = new JSONObject();
		String sql3 ="select  inst_id from manage_depart where dep_id= '"+user.getDepId()+"'";
		ManageDepart depart=ManageDepart.dao.findFirst(sql3);
		Integer sys_user_id = depart.getInt("inst_id");
		String sql = "select * from topic where topic_name=?";
		Topic first = Topic.dao.findFirst(sql, name);
		if (first != null && !("").equals(first)) {
			result.put("message", "已存在");
			return result;
		}
		Topic manageTopic = new Topic();
		manageTopic.setTopicName(name);
		manageTopic.set("inst_id", sys_user_id);
		manageTopic.save();
		result.put("message", "添加成功");
		return result;
	}

}
