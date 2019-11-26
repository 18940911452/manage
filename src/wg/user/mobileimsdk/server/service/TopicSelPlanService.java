package wg.user.mobileimsdk.server.service;

import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.plugin.activerecord.Db;


//public class TopicSelPlanService {
//
//	public List<ManageSelect> getSelectTopic(String pageNum, String pageSize) {
//		String sql = "select id,name,user_id,create_time,des,keywords,status from manage_select order by create_time desc";
//		List<ManageSelect> manageSelect = ManageSelect.dao.find(sql);
//		return manageSelect;
//	}
//
//	public JSONObject getCompleteCount() {
//		JSONObject json = new JSONObject();
//		String sql = "select count(*) from manage_task where status=2";
//		List<ManageSelect> hircount = ManageSelect.dao.find(sql);
//		json.put("yiwancheng", hircount);
//		return json;
//	}
//
//	public JSONObject getConductCount() {
//		JSONObject json = new JSONObject();
//		String sql = "select count(*) from manage_task where status=1";
//		List<ManageSelect> hircount = ManageSelect.dao.find(sql);
//		json.put("jinxingshi", hircount);
//		return json;
//	}
//
//	public JSONObject getHirCount() {
//		JSONObject json = new JSONObject();
//		String sql = "select count(*) from manage_task where status=2 or status=1";
//		List<ManageSelect> hircount = ManageSelect.dao.find(sql);
//		json.put("zongji", hircount);
//		return json;
//	}
//
//	public boolean getStatus(String id) {
//		String sql = "update manage_select set status=1 where id='" + id + "'";
//		int sel = Db.use().update(sql);
//		return sel == 1 ? true : false;
//	}
//
//	public JSONObject getSelectTopicById(String id, String name, String des, String keywords) {
//		JSONObject result = new JSONObject();
//		String sql = "select id,name,user_id,create_time,des,keywords,status from manage_select where id='" + id + "'";
//		ManageSelect manageSelect = ManageSelect.dao.findFirst(sql);
//		if (manageSelect != null && !("").equals(manageSelect)) {
//			manageSelect.setName(name);
//			manageSelect.setDes(des);
//			manageSelect.setKeywords(keywords);
//			manageSelect.update();
//			result.put("message", "编辑成功");
//			return result;
//		}
//		return result;
//	}
//
//	public JSONObject addSelectTopic(String name, String des, String keywords, Integer user_id) {
//		JSONObject result = new JSONObject();
//		ManageSelect manageSelect2 = new ManageSelect();
//		manageSelect2.setUserId(user_id);
//		manageSelect2.setName(name);
//		manageSelect2.setDes(des);
//		manageSelect2.setKeywords(keywords);
//		manageSelect2.save();
//		result.put("message", "添加成功");
//		return result;
//	}
//
//	public List<ManageSelect> findName(String name) {
//		String sql = "select id,name,user_id,create_time,des,keywords,status from manage_select where name like '%"
//				+ name + "%' or des like '%" + name + "%' order by create_time desc";
//		List<ManageSelect> manageSelect = ManageSelect.dao.find(sql);
//		return manageSelect;
//	}
//
//	public JSONObject delSelectTopic(Integer id) {
//		JSONObject result = new JSONObject();
//		boolean manageSelect = ManageSelect.dao.deleteById(id);
//		if (manageSelect == true) {
//			result.put("message", "success");
//			return result;
//		}
//		return result;
//	}
//}
