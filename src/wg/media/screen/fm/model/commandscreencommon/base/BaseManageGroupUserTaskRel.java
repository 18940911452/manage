package wg.media.screen.fm.model.commandscreencommon.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseManageGroupUserTaskRel<M extends BaseManageGroupUserTaskRel<M>> extends Model<M> implements IBean {

	public void setId(java.lang.Integer id) {
		set("id", id);
	}

	public java.lang.Integer getId() {
		return get("id");
	}

	public void setGroupId(java.lang.Integer groupId) {
		set("group_id", groupId);
	}

	public java.lang.Integer getGroupId() {
		return get("group_id");
	}

	public void setUserId(java.lang.Integer userId) {
		set("user_id", userId);
	}

	public java.lang.Integer getUserId() {
		return get("user_id");
	}

	public void setUserType(java.lang.Integer userType) {
		set("user_type", userType);
	}

	public java.lang.Integer getUserType() {
		return get("user_type");
	}

	public void setTaskId(java.lang.Integer taskId) {
		set("task_id", taskId);
	}

	public java.lang.Integer getTaskId() {
		return get("task_id");
	}

	public void setContent(java.lang.String content) {
		set("content", content);
	}

	public java.lang.String getContent() {
		return get("content");
	}

	public void setPubtime(java.util.Date pubtime) {
		set("pubtime", pubtime);
	}

	public java.util.Date getPubtime() {
		return get("pubtime");
	}

}
