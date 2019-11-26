package wg.user.mobileimsdk.server.service;

import java.util.Date;
import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.plugin.activerecord.Db;

import wg.media.screen.fm.model.commandscreencommon.ImInform;


public class InformService {
	public static InformService ser=new InformService();

	/**
	 * 名称：addInformPlan 
	 * 描述：添加通知
	 * @author gongxiang.pang
	 * @parameter
	 * @return 
	 * @param uid
	 * @param toUid
	 * @param title
	 * @param content
	 * @param url
	 * @param type
	 * @return
	 */
	public boolean addInformPlan(String uid, String toUid, String title, String content, String url, String type) {
		boolean result=false;
		if(toUid!=null && !toUid.equals("")){
			String[] touid=	toUid.split(",");
			for (int i = 0; i < touid.length; i++) {
				String tuid=touid[i].trim().toString();
				ImInform action=new ImInform();
				action.setUid(uid);
				action.setToUser(tuid);
				if("".equals(title)) {
					action.setTitle("群通知");
				}else {
					action.setTitle(title);
				}
				action.setContent(content);
				action.setUrl(url);
				action.setType(Integer.valueOf(type));
				action.setTime(new Date());
				result= action.save();
			}
			if(!"".equals(title)) {
				ImInform action=new ImInform();
				action.setUid(uid);
				action.setToUser(uid);
				action.setTitle(title);
				action.setContent(content);
				action.setUrl(url);
				action.setType(Integer.valueOf(type));
				action.setTime(new Date());
				action.setReadstatus(1);
				result= action.save();
			}
		}
		return result;
	}
	
	public boolean setReadInform(String id){
		int update = Db.update("update im_inform set readstatus = '1' where id = '" + id + "'");
		if(update>0){
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * 
	 * 名称：getActionPlan 
	 * 描述：获取通知列表
	 * @author gongxiang.pang
	 * @parameter
	 * @return 
	 * @return
	 */
	public  JSONObject getInformPlan(String uid,Integer pageNo,Integer pageSize){
		JSONObject json = new JSONObject();
		String sql="SELECT  plan.*,user.nick_name as author  from im_inform plan left join manage_user_info user on user.user_id = plan.uid  where plan.toUser="+uid+" ";
		String sql1="SELECT  count(*) as count from im_inform where toUser="+uid+" ";
		sql+=" ORDER BY time DESC";
		if(pageNo!=null && pageSize!=null){
			pageNo = (pageNo < 0) ? 1 : pageNo;
			pageSize = (pageSize < 0) ? 20 : pageSize;
			sql+="  LIMIT "+(pageNo-1)*pageSize+","+pageSize;
		}
		List<ImInform> find = ImInform.dao.find(sql);
		ImInform form = ImInform.dao.findFirst(sql1);
		json.put("result", find);
		json.put("total", form.get("count"));
		return json;
	}
	

}
