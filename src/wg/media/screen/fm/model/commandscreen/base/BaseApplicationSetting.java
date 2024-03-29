package wg.media.screen.fm.model.commandscreen.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseApplicationSetting<M extends BaseApplicationSetting<M>> extends Model<M> implements IBean {

	public void setId(java.lang.Integer id) {
		set("id", id);
	}

	public java.lang.Integer getId() {
		return get("id");
	}

	public void setAppKey(java.lang.String appKey) {
		set("app_key", appKey);
	}

	public java.lang.String getAppKey() {
		return get("app_key");
	}

	public void setAppValue(java.lang.String appValue) {
		set("app_value", appValue);
	}

	public java.lang.String getAppValue() {
		return get("app_value");
	}

	public void setStatus(java.lang.Integer status) {
		set("status", status);
	}

	public java.lang.Integer getStatus() {
		return get("status");
	}

	public void setAppDesc(java.lang.String appDesc) {
		set("app_desc", appDesc);
	}

	public java.lang.String getAppDesc() {
		return get("app_desc");
	}

}
