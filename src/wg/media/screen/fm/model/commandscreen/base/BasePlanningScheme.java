package wg.media.screen.fm.model.commandscreen.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BasePlanningScheme<M extends BasePlanningScheme<M>> extends Model<M> implements IBean {

	public void setPId(java.lang.Integer pId) {
		set("p_id", pId);
	}

	public java.lang.Integer getPId() {
		return get("p_id");
	}

	public void setSchemeName(java.lang.String schemeName) {
		set("scheme_name", schemeName);
	}

	public java.lang.String getSchemeName() {
		return get("scheme_name");
	}

	public void setSchemeContent(java.lang.String schemeContent) {
		set("scheme_content", schemeContent);
	}

	public java.lang.String getSchemeContent() {
		return get("scheme_content");
	}

	public void setBelongToMedia(java.lang.String belongToMedia) {
		set("belong_to_media", belongToMedia);
	}

	public java.lang.String getBelongToMedia() {
		return get("belong_to_media");
	}

	public void setUploadTime(java.util.Date uploadTime) {
		set("upload_time", uploadTime);
	}

	public java.util.Date getUploadTime() {
		return get("upload_time");
	}

	public void setUploadPerson(java.lang.String uploadPerson) {
		set("upload_person", uploadPerson);
	}

	public java.lang.String getUploadPerson() {
		return get("upload_person");
	}

	public void setSchemeType(java.lang.String schemeType) {
		set("scheme_type", schemeType);
	}

	public java.lang.String getSchemeType() {
		return get("scheme_type");
	}

	public void setSchemeStatus(java.lang.String schemeStatus) {
		set("scheme_status", schemeStatus);
	}

	public java.lang.String getSchemeStatus() {
		return get("scheme_status");
	}

	public void setUpdatetime(java.util.Date updatetime) {
		set("updatetime", updatetime);
	}

	public java.util.Date getUpdatetime() {
		return get("updatetime");
	}

	public void setSchemePath(java.lang.String schemePath) {
		set("scheme_path", schemePath);
	}

	public java.lang.String getSchemePath() {
		return get("scheme_path");
	}

	public void setAuditor(java.lang.String auditor) {
		set("auditor", auditor);
	}

	public java.lang.String getAuditor() {
		return get("auditor");
	}

	public void setBelongToTopic(java.lang.String belongToTopic) {
		set("belong_to_topic", belongToTopic);
	}

	public java.lang.String getBelongToTopic() {
		return get("belong_to_topic");
	}

	public void setDownloadCount(java.lang.Integer downloadCount) {
		set("download_count", downloadCount);
	}

	public java.lang.Integer getDownloadCount() {
		return get("download_count");
	}

	public void setUrl(java.lang.String url) {
		set("url", url);
	}

	public java.lang.String getUrl() {
		return get("url");
	}

}
