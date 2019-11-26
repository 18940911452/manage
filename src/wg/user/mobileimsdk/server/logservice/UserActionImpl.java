package wg.user.mobileimsdk.server.logservice;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.DbKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

import wg.media.screen.fm.model.commandscreencommon.ManagerUserRecord;
import wg.user.mobileimsdk.server.model.vo.MonitorConfigSearchVo;

/**
 * 
 * @description 用户日志操作
 * @author yaomeifa
 * @date 2019年6月26日
 */
public class UserActionImpl implements IUserAction {

	/**
	 *
	 * 名称：addActioInfo 描述：添加用户行为记录信息 日期：2018年12月29日-上午10:27:38
	 *
	 * @author gongxiang.pang
	 * @param record
	 * @return
	 * @see com.wenge.datamanager.system.SystemInf#addActioInfo(com.wenge.datamanager.dao.model.tagmgr.ManagerUserRecord)
	 */
	@Override
	public Integer addActioInfo(ManagerUserRecord record) {
		if (record.save()) {
			return 1;
		} else {
			return 0;
		}
	}

	@Override
	public ManagerUserRecord detailLog(Long id) {
		return ManagerUserRecord.dao.findById(id);
	}

	@Override
	public JSONObject updateLog(ManagerUserRecord record) {
		JSONObject json = new JSONObject();
		if (record.update()) {
			json.put("code", 0);
			json.put("msg", "修改成功！");
		} else {
			json.put("code", -1);
			json.put("msg", "修改失败！");
		}
		return json;
	}

	@Override
	public JSONObject delLog(Long id) {
		ManagerUserRecord record = new ManagerUserRecord();
		record.setId(id.intValue());
		record.setStatus(0);
		JSONObject json = new JSONObject();
		if (record.update()) {
			json.put("code", 0);
			json.put("msg", "删除成功！");
		} else {
			json.put("code", -1);
			json.put("msg", "删除失败！");
		}
		return json;
	}

	@Override
	public JSONObject getInfoAction(String searchKey, MonitorConfigSearchVo search) {
		JSONArray array = new JSONArray();
		String sql = " from manager_user_record where 1=1 ";
		String selectSql = "select uid userId,username userName,action action,cation_content content,insert_time insertTime,ip ip ";
		if (StringUtils.isNotBlank(searchKey)) {
			sql += " and (username like '%" + searchKey + "%' or action like '%" + searchKey
					+ "%' or cation_content like '%" + searchKey + "')";
		}
		sql += " order by insert_time desc";
		Page<Record> list = Db.use(DbKit.getConfig(ManagerUserRecord.class).getName()).paginate(search.getPageNo(),
				search.getPageSize(), selectSql, sql);
		for (Record useraction : list.getList()) {
			JSONObject object = new JSONObject();
			object.put("userId", useraction.getInt("userId"));
			object.put("userName", useraction.getStr("userName"));
			object.put("action", useraction.getStr("action"));
			object.put("ip", useraction.getStr("ip") == null ? "" : useraction.getStr("ip"));
			object.put("content", useraction.getStr("content"));
			object.put("insertTime", useraction.getDate("insertTime"));
			array.add(object);
		}
		JSONObject result = new JSONObject();
		result.put("data", array);
		result.put("total", list.getTotalRow());
		return result;
	}

}
