package wg.media.screen.fm.model.commandscreencommon.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseManagerUserRecord<M extends BaseManagerUserRecord<M>> extends Model<M> implements IBean {

	public void setId(java.lang.Integer id) {
		set("id", id);
	}

	public java.lang.Integer getId() {
		return get("id");
	}

	public void setUid(java.lang.Integer uid) {
		set("uid", uid);
	}

	public java.lang.Integer getUid() {
		return get("uid");
	}

	public void setUsername(java.lang.String username) {
		set("username", username);
	}

	public java.lang.String getUsername() {
		return get("username");
	}

	public void setAction(java.lang.String action) {
		set("action", action);
	}

	public java.lang.String getAction() {
		return get("action");
	}

	public void setAddress(java.lang.String address) {
		set("address", address);
	}

	public java.lang.String getAddress() {
		return get("address");
	}

	public void setIp(java.lang.String ip) {
		set("ip", ip);
	}

	public java.lang.String getIp() {
		return get("ip");
	}

	public void setBrowser(java.lang.String browser) {
		set("browser", browser);
	}

	public java.lang.String getBrowser() {
		return get("browser");
	}

	public void setMac(java.lang.String mac) {
		set("mac", mac);
	}

	public java.lang.String getMac() {
		return get("mac");
	}

	public void setDuration(java.lang.String duration) {
		set("duration", duration);
	}

	public java.lang.String getDuration() {
		return get("duration");
	}

	public void setStatus(java.lang.Integer status) {
		set("status", status);
	}

	public java.lang.Integer getStatus() {
		return get("status");
	}

	public void setCationContent(java.lang.String cationContent) {
		set("cation_content", cationContent);
	}

	public java.lang.String getCationContent() {
		return get("cation_content");
	}

	public void setInsertTime(java.util.Date insertTime) {
		set("insert_time", insertTime);
	}

	public java.util.Date getInsertTime() {
		return get("insert_time");
	}

	public void setRemarks(java.lang.String remarks) {
		set("remarks", remarks);
	}

	public java.lang.String getRemarks() {
		return get("remarks");
	}

	public void setUpdateUserid(java.lang.Integer updateUserid) {
		set("updateUserid", updateUserid);
	}

	public java.lang.Integer getUpdateUserid() {
		return get("updateUserid");
	}

	public void setUpdateUserName(java.lang.String updateUserName) {
		set("updateUserName", updateUserName);
	}

	public java.lang.String getUpdateUserName() {
		return get("updateUserName");
	}

	public void setUpdateTime(java.util.Date updateTime) {
		set("updateTime", updateTime);
	}

	public java.util.Date getUpdateTime() {
		return get("updateTime");
	}

}
