package wg.media.screen.fm.model.commandscreen.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseImFeedback<M extends BaseImFeedback<M>> extends Model<M> implements IBean {

	public void setId(java.lang.Integer id) {
		set("id", id);
	}

	public java.lang.Integer getId() {
		return get("id");
	}

	public void setContent(java.lang.String content) {
		set("content", content);
	}

	public java.lang.String getContent() {
		return get("content");
	}

	public void setUserId(java.lang.Integer userId) {
		set("user_id", userId);
	}

	public java.lang.Integer getUserId() {
		return get("user_id");
	}

	public void setTell(java.lang.String tell) {
		set("tell", tell);
	}

	public java.lang.String getTell() {
		return get("tell");
	}

	public void setInsterTime(java.util.Date insterTime) {
		set("inster_time", insterTime);
	}

	public java.util.Date getInsterTime() {
		return get("inster_time");
	}

}
