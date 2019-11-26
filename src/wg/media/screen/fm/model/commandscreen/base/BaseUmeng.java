package wg.media.screen.fm.model.commandscreen.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseUmeng<M extends BaseUmeng<M>> extends Model<M> implements IBean {

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

	public void setAndroidAppkey(java.lang.String androidAppkey) {
		set("android_appkey", androidAppkey);
	}

	public java.lang.String getAndroidAppkey() {
		return get("android_appkey");
	}

	public void setAndroidAppmastersecret(java.lang.String androidAppmastersecret) {
		set("android_appMasterSecret", androidAppmastersecret);
	}

	public java.lang.String getAndroidAppmastersecret() {
		return get("android_appMasterSecret");
	}

	public void setIosAppkey(java.lang.String iosAppkey) {
		set("ios_appkey", iosAppkey);
	}

	public java.lang.String getIosAppkey() {
		return get("ios_appkey");
	}

	public void setIosAppmastersecret(java.lang.String iosAppmastersecret) {
		set("ios_appMasterSecret", iosAppmastersecret);
	}

	public java.lang.String getIosAppmastersecret() {
		return get("ios_appMasterSecret");
	}

	public void setStatus(java.lang.Integer status) {
		set("status", status);
	}

	public java.lang.Integer getStatus() {
		return get("status");
	}

	public void setInsertTime(java.util.Date insertTime) {
		set("insert_time", insertTime);
	}

	public java.util.Date getInsertTime() {
		return get("insert_time");
	}

	public void setAppInfoId(java.lang.Integer appInfoId) {
		set("app_info_id", appInfoId);
	}

	public java.lang.Integer getAppInfoId() {
		return get("app_info_id");
	}

	public void setAndroidAlias(java.lang.String androidAlias) {
		set("android_alias", androidAlias);
	}

	public java.lang.String getAndroidAlias() {
		return get("android_alias");
	}

	public void setIosAlias(java.lang.String iosAlias) {
		set("ios_alias", iosAlias);
	}

	public java.lang.String getIosAlias() {
		return get("ios_alias");
	}

	public void setAndroidPath(java.lang.String androidPath) {
		set("android_path", androidPath);
	}

	public java.lang.String getAndroidPath() {
		return get("android_path");
	}

	public void setIosPath(java.lang.String iosPath) {
		set("ios_path", iosPath);
	}

	public java.lang.String getIosPath() {
		return get("ios_path");
	}

	public void setAndroidCustom(java.lang.String androidCustom) {
		set("android_custom", androidCustom);
	}

	public java.lang.String getAndroidCustom() {
		return get("android_custom");
	}

	public void setIosCustom(java.lang.String iosCustom) {
		set("ios_custom", iosCustom);
	}

	public java.lang.String getIosCustom() {
		return get("ios_custom");
	}

}