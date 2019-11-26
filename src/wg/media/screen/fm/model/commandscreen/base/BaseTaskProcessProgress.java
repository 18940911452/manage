package wg.media.screen.fm.model.commandscreen.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseTaskProcessProgress<M extends BaseTaskProcessProgress<M>> extends Model<M> implements IBean {

	public void setId(java.lang.Integer id) {
		set("id", id);
	}

	public java.lang.Integer getId() {
		return get("id");
	}

	public void setTaskId(java.lang.Integer taskId) {
		set("task_id", taskId);
	}

	public java.lang.Integer getTaskId() {
		return get("task_id");
	}

	public void setProCode(java.lang.String proCode) {
		set("pro_code", proCode);
	}

	public java.lang.String getProCode() {
		return get("pro_code");
	}

	public void setProName(java.lang.String proName) {
		set("pro_name", proName);
	}

	public java.lang.String getProName() {
		return get("pro_name");
	}

	public void setProProgress(java.lang.String proProgress) {
		set("pro_progress", proProgress);
	}

	public java.lang.String getProProgress() {
		return get("pro_progress");
	}

	public void setTotalProgress(java.lang.String totalProgress) {
		set("total_progress", totalProgress);
	}

	public java.lang.String getTotalProgress() {
		return get("total_progress");
	}

}