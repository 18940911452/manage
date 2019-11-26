package wg.media.screen.fm.model.commandscreen.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseManageMediaMatrix<M extends BaseManageMediaMatrix<M>> extends Model<M> implements IBean {

	public void setAutoId(java.lang.Long autoId) {
		set("auto_id", autoId);
	}

	public java.lang.Long getAutoId() {
		return get("auto_id");
	}

	public void setName(java.lang.String name) {
		set("name", name);
	}

	public java.lang.String getName() {
		return get("name");
	}

	public void setUid(java.lang.String uid) {
		set("uid", uid);
	}

	public java.lang.String getUid() {
		return get("uid");
	}

	public void setIcon(java.lang.String icon) {
		set("icon", icon);
	}

	public java.lang.String getIcon() {
		return get("icon");
	}

	public void setDescription(java.lang.String description) {
		set("description", description);
	}

	public java.lang.String getDescription() {
		return get("description");
	}

	public void setType(java.lang.Integer type) {
		set("type", type);
	}

	public java.lang.Integer getType() {
		return get("type");
	}

	public void setStatus(java.lang.Boolean status) {
		set("status", status);
	}

	public java.lang.Boolean getStatus() {
		return get("status");
	}

	public void setSort(java.lang.Integer sort) {
		set("sort", sort);
	}

	public java.lang.Integer getSort() {
		return get("sort");
	}

	public void setInstId(java.lang.Long instId) {
		set("inst_id", instId);
	}

	public java.lang.Long getInstId() {
		return get("inst_id");
	}

}
