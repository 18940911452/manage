package wg.user.mobileimsdk.server.service;

import java.util.Date;
import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.plugin.activerecord.Db;

import wg.media.screen.fm.model.commandscreencommon.ImActionPlan;
import wg.media.screen.fm.model.commandscreencommon.ImInform;

/**
 * 
 * 类名称：ActionPlanService 
 * 类描述：行到预案 
 * 创建时间：2018年4月26日 上午11:04:20 
 * @author gongxiang.pang
 * @version V1.0
 */
public class ActionPlanService {
	public static ActionPlanService ser=new ActionPlanService();

	public boolean addActionPlan(String uid, String toUid, String title, String content, String url, String type) {
		boolean result=false;
		if(toUid!=null && !toUid.equals("")){
			
			String[] touid=	toUid.split(",");
			for (int i = 0; i < touid.length; i++) {
				String tuid=touid[i].trim().toString();
				ImActionPlan action=new ImActionPlan();
				action.setUid(uid);
				action.setToUser(tuid);
				action.setTitle(title);
				action.setContent(content);
				action.setUrl(url);
				action.setType(Integer.valueOf(type));
				action.setType(Integer.valueOf(type));
				action.setTime(new Date());
				result= action.save();
			}
			ImActionPlan action=new ImActionPlan();
			action.setUid(uid);
			action.setToUser(uid);
			action.setTitle(title);
			action.setContent(content);
			action.setUrl(url);
			action.setType(Integer.valueOf(type));
			action.setTime(new Date());
			result= action.save();
		}
		return result;
	}
	
	public boolean setReadAtionPlan(String id){
		int update = Db.update("update im_action_plan set readstatus = '1' where id = '" + id + "'");
		if(update>0){
			return true;
		}else{
			return false;
		}
	}
	
	public  JSONObject getActionPlan(String uid,Integer pageNo,Integer pageSize){
		JSONObject json = new JSONObject();
		String sql="SELECT  plan.*,user.nick_name as author  from im_action_plan plan left join manage_user_info user on user.user_id = plan.uid where plan.toUser="+uid+" ";
		String sql1="SELECT  count(*) as count from im_action_plan where toUser="+uid+" ";
		sql+=" ORDER BY time DESC ";
		if(pageNo!=null && pageSize!=null){
			pageNo = (pageNo < 0) ? 1 : pageNo;
			pageSize = (pageSize < 0) ? 20 : pageSize;
			sql+="  LIMIT "+(pageNo-1)*pageSize+","+pageSize;
		}
		
		List<ImActionPlan> find = ImActionPlan.dao.find(sql);
		ImActionPlan plan= ImActionPlan.dao.findFirst(sql1);
		json.put("list", find);
		json.put("total", plan.get("count"));
		return json;
		
	}
   /**
    * 获取未读信息个数
    * @param uid
    * @return
    */
	public JSONObject getNotReadTotal(String uid) {
		// TODO Auto-generated method stub
		JSONObject json = new JSONObject();
		String sql = "SELECT  count(*) as count from im_action_plan where toUser="+uid+" and readstatus = 0 ";
		String sql1 = "SELECT  count(*) as count from im_inform where toUser="+uid+" and readstatus = 0 ";
		ImActionPlan plan= ImActionPlan.dao.findFirst(sql);
		ImInform form= ImInform.dao.findFirst(sql1);
		json.put("planTotal", plan.get("count"));
		json.put("formTotal", form.get("count"));
		return json;
	}
	

}
