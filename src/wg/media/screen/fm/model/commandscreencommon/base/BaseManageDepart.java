package wg.media.screen.fm.model.commandscreencommon.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseManageDepart<M extends BaseManageDepart<M>> extends Model<M> implements IBean {

	public void setDepId(java.lang.Integer depId) {
		set("dep_id", depId);
	}

	public java.lang.Integer getDepId() {
		return get("dep_id");
	}

	public void setDepName(java.lang.String depName) {
		set("dep_name", depName);
	}

	public java.lang.String getDepName() {
		return get("dep_name");
	}

	public void setInstId(java.lang.Integer instId) {
		set("inst_id", instId);
	}

	public java.lang.Integer getInstId() {
		return get("inst_id");
	}

	public void setStatus(java.lang.Integer status) {
		set("status", status);
	}

	public java.lang.Integer getStatus() {
		return get("status");
	}

	public void setCreateTime(java.util.Date createTime) {
		set("create_time", createTime);
	}

	public java.util.Date getCreateTime() {
		return get("create_time");
	}

	public void setContacter(java.lang.String contacter) {
		set("contacter", contacter);
	}

	public java.lang.String getContacter() {
		return get("contacter");
	}

	public void setAddress(java.lang.String address) {
		set("address", address);
	}

	public java.lang.String getAddress() {
		return get("address");
	}

	public void setTel(java.lang.String tel) {
		set("tel", tel);
	}

	public java.lang.String getTel() {
		return get("tel");
	}

	public void setLeader(java.lang.String leader) {
		set("leader", leader);
	}

	public java.lang.String getLeader() {
		return get("leader");
	}

	public void setEmail(java.lang.String email) {
		set("email", email);
	}

	public java.lang.String getEmail() {
		return get("email");
	}

	public void setMediaId(java.lang.Integer mediaId) {
		set("media_id", mediaId);
	}

	public java.lang.Integer getMediaId() {
		return get("media_id");
	}

}