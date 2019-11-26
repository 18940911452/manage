package wg.user.mobileimsdk.server.service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.DbKit;
import com.jfinal.plugin.activerecord.Record;

import wg.media.screen.fm.model.commandscreen.Topic;
import wg.media.screen.fm.model.commandscreencommon.ManageDepart;
import wg.media.screen.fm.model.commandscreencommon.ManageUserInfo;
import wg.media.screen.fm.report.ITaskAndTopicalService;
import wg.user.mobileimsdk.server.util.MyTimeUtil;

public class TopicManageService implements ITaskAndTopicalService {

	public JSONObject getTopicInfo(Integer pageNo, Integer pageSize, String keyword, ManageUserInfo user) {
		String sql3 ="select  inst_id from manage_depart where dep_id= '"+user.getDepId()+"'";
		ManageDepart depart=ManageDepart.dao.findFirst(sql3);
		Integer sys_user_id = depart.getInt("inst_id");
		JSONObject result = new JSONObject();
		String sql = "select topic.update_time,topic.t_id as id,topic.topic_name as top_name,topic.keyword,topic.createtime as create_time from topic where topic.inst_id=?";
		String count_sql = "select count(*) count from topic where inst_id=?";
		String sql1 = " and topic_name like '%" + keyword + "%' ";
		String sql2 = " order by createtime desc limit " + (pageNo - 1) * pageSize + " , " + pageSize;
		if (keyword != null && !("").equals(keyword)) {
			sql += sql1;
			count_sql += sql1;
		}
		sql += sql2;
		List<Topic> list = Topic.dao.find(sql, sys_user_id);
		JSONArray jsonArray=new JSONArray();
		for (Topic top : list) {
			JSONObject json=new JSONObject();
			json.put("topName", top.get("top_name"));
			json.put("keyword", top.get("keyword"));
			json.put("createTime", top.get("create_time"));
			json.put("updateTime", top.get("update_time"));
			json.put("id", top.get("id"));
			jsonArray.add(json);
		}
		Topic first = Topic.dao.findFirst(count_sql, sys_user_id);
		if (list != null && list.size() > 0) {
			result.put("list", jsonArray);
		}
		if (first != null) {
			result.put("total", first.getLong("count"));
		}
		return result;

	}

	public JSONObject addTopicInfo(String name, ManageUserInfo user, String des, String create_time) {
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
		try {
			manageTopic.setCreatetime(MyTimeUtil.sdf.parse(create_time));
			manageTopic.setUpdateTime(MyTimeUtil.sdf.parse(create_time));
		} catch (ParseException e1) {
			e1.printStackTrace();
		}

		try {
			manageTopic.setCreatetime(MyTimeUtil.sdf.parse(create_time));
		} catch (ParseException e) {
			e.printStackTrace();
		}
//		Db.use(DbKit.getConfig(Topic.class).getName()).save("topic", record);
		manageTopic.save();
		result.put("message", "添加成功");
		return result;
	}

	public JSONObject editTopicInfo(Integer tId, String name, String des) {
		JSONObject result = new JSONObject();
		String sql = "select * from topic where t_id=?";
		Topic manageTopic = Topic.dao.findFirst(sql, tId);
		if (manageTopic != null && !("").equals(manageTopic)) {
			manageTopic.setTopicName(name);
			manageTopic.setUpdateTime(new Date());

			manageTopic.update();
			result.put("message", "编辑成功");
			return result;
		}
		return result;
	}

	public JSONObject deleTopicInfo(Integer tId) {
		JSONObject result = new JSONObject();
		boolean manageTopic = Topic.dao.deleteById(tId);
		if (manageTopic == true) {
			result.put("message", "success");
			return result;
		}
		return result;
	}

}
