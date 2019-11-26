package wg.media.screen.fm.model.commandscreencommon.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseManagePerm<M extends BaseManagePerm<M>> extends Model<M> implements IBean {

	public void setId(java.lang.Integer id) {
		set("id", id);
	}

	public java.lang.Integer getId() {
		return get("id");
	}

	public void setUrl(java.lang.String url) {
		set("url", url);
	}

	public java.lang.String getUrl() {
		return get("url");
	}

	public void setName(java.lang.String name) {
		set("name", name);
	}

	public java.lang.String getName() {
		return get("name");
	}

	public void setNameEn(java.lang.String nameEn) {
		set("name_en", nameEn);
	}

	public java.lang.String getNameEn() {
		return get("name_en");
	}

	public void setNamePt(java.lang.String namePt) {
		set("name_pt", namePt);
	}

	public java.lang.String getNamePt() {
		return get("name_pt");
	}

	public void setIcon(java.lang.String icon) {
		set("icon", icon);
	}

	public java.lang.String getIcon() {
		return get("icon");
	}

	public void setPid(java.lang.Integer pid) {
		set("pid", pid);
	}

	public java.lang.Integer getPid() {
		return get("pid");
	}

	public void setStatus(java.lang.Integer status) {
		set("status", status);
	}

	public java.lang.Integer getStatus() {
		return get("status");
	}

	public void setPath(java.lang.String path) {
		set("path", path);
	}

	public java.lang.String getPath() {
		return get("path");
	}

	public void setParentName(java.lang.String parentName) {
		set("parent_name", parentName);
	}

	public java.lang.String getParentName() {
		return get("parent_name");
	}

}