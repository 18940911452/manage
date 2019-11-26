package wg.media.screen.fm.model.commandscreencommon.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseImFileMange<M extends BaseImFileMange<M>> extends Model<M> implements IBean {

	public void setId(java.lang.Integer id) {
		set("id", id);
	}

	public java.lang.Integer getId() {
		return get("id");
	}

	public void setName(java.lang.String name) {
		set("name", name);
	}

	public java.lang.String getName() {
		return get("name");
	}

	public void setUrl(java.lang.String url) {
		set("url", url);
	}

	public java.lang.String getUrl() {
		return get("url");
	}

	public void setType(java.lang.Integer type) {
		set("type", type);
	}

	public java.lang.Integer getType() {
		return get("type");
	}

	public void setSize(java.lang.Double size) {
		set("size", size);
	}

	public java.lang.Double getSize() {
		return get("size");
	}

	public void setFromUserId(java.lang.String fromUserId) {
		set("from_user_id", fromUserId);
	}

	public java.lang.String getFromUserId() {
		return get("from_user_id");
	}

	public void setToUserId(java.lang.String toUserId) {
		set("to_user_id", toUserId);
	}

	public java.lang.String getToUserId() {
		return get("to_user_id");
	}

	public void setTime(java.util.Date time) {
		set("time", time);
	}

	public java.util.Date getTime() {
		return get("time");
	}

}
