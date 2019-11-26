package wg.media.screen.fm.model.commandscreencommon.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseManageRole<M extends BaseManageRole<M>> extends Model<M> implements IBean {

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

	public void setLevel(java.lang.Integer level) {
		set("level", level);
	}

	public java.lang.Integer getLevel() {
		return get("level");
	}

	public void setUserId(java.lang.Integer userId) {
		set("user_id", userId);
	}

	public java.lang.Integer getUserId() {
		return get("user_id");
	}

	public void setDepId(java.lang.Integer depId) {
		set("dep_id", depId);
	}

	public java.lang.Integer getDepId() {
		return get("dep_id");
	}

	public void setInstitutionId(java.lang.Integer institutionId) {
		set("institution_id", institutionId);
	}

	public java.lang.Integer getInstitutionId() {
		return get("institution_id");
	}

	public void setCreateTime(java.util.Date createTime) {
		set("create_time", createTime);
	}

	public java.util.Date getCreateTime() {
		return get("create_time");
	}

	public void setSysType(java.lang.Integer sysType) {
		set("sys_type", sysType);
	}

	public java.lang.Integer getSysType() {
		return get("sys_type");
	}

}