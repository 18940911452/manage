package wg.media.screen.fm.model.commandscreen.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseSchemeRelation<M extends BaseSchemeRelation<M>> extends Model<M> implements IBean {

	public void setSId(java.lang.Integer sId) {
		set("s_id", sId);
	}

	public java.lang.Integer getSId() {
		return get("s_id");
	}

	public void setSchemeId(java.lang.Integer schemeId) {
		set("scheme_id", schemeId);
	}

	public java.lang.Integer getSchemeId() {
		return get("scheme_id");
	}

	public void setAuditor(java.lang.String auditor) {
		set("auditor", auditor);
	}

	public java.lang.String getAuditor() {
		return get("auditor");
	}

	public void setAuditTime(java.util.Date auditTime) {
		set("audit_time", auditTime);
	}

	public java.util.Date getAuditTime() {
		return get("audit_time");
	}

	public void setAuditStatus(java.lang.String auditStatus) {
		set("audit_status", auditStatus);
	}

	public java.lang.String getAuditStatus() {
		return get("audit_status");
	}

	public void setAuditOpinion(java.lang.String auditOpinion) {
		set("audit_opinion", auditOpinion);
	}

	public java.lang.String getAuditOpinion() {
		return get("audit_opinion");
	}

	public void setCommitPsnId(java.lang.Integer commitPsnId) {
		set("commit_psn_id", commitPsnId);
	}

	public java.lang.Integer getCommitPsnId() {
		return get("commit_psn_id");
	}

	public void setCommitTime(java.util.Date commitTime) {
		set("commit_time", commitTime);
	}

	public java.util.Date getCommitTime() {
		return get("commit_time");
	}

}
