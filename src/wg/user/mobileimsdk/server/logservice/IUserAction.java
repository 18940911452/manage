package wg.user.mobileimsdk.server.logservice;

import com.alibaba.fastjson.JSONObject;

import wg.media.screen.fm.model.commandscreencommon.ManagerUserRecord;
import wg.user.mobileimsdk.server.model.vo.MonitorConfigSearchVo;

/**
 * @description: 用户日志
 * @author: yaomeifa
 * @date: 2019-06-26 20:23
 **/
public interface IUserAction {
	public Integer addActioInfo(ManagerUserRecord record);

	public ManagerUserRecord detailLog(Long id);

	public JSONObject updateLog(ManagerUserRecord record);

	public JSONObject delLog(Long id);

	public JSONObject getInfoAction(String searchKey, MonitorConfigSearchVo search);

}
