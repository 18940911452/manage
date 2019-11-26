package wg.media.screen.fm.model.commandscreen.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseChatComment<M extends BaseChatComment<M>> extends Model<M> implements IBean {

	public void setId(java.lang.Integer id) {
		set("id", id);
	}

	public java.lang.Integer getId() {
		return get("id");
	}

	public void setCId(java.lang.Integer cId) {
		set("c_id", cId);
	}

	public java.lang.Integer getCId() {
		return get("c_id");
	}

	public void setFatherId(java.lang.Integer fatherId) {
		set("father_id", fatherId);
	}

	public java.lang.Integer getFatherId() {
		return get("father_id");
	}

	public void setCommentContent(java.lang.String commentContent) {
		set("comment_content", commentContent);
	}

	public java.lang.String getCommentContent() {
		return get("comment_content");
	}

	public void setCreatetime(java.util.Date createtime) {
		set("createtime", createtime);
	}

	public java.util.Date getCreatetime() {
		return get("createtime");
	}

	public void setCommentPerson(java.lang.String commentPerson) {
		set("comment_person", commentPerson);
	}

	public java.lang.String getCommentPerson() {
		return get("comment_person");
	}

	public void setUpdatetime(java.util.Date updatetime) {
		set("updatetime", updatetime);
	}

	public java.util.Date getUpdatetime() {
		return get("updatetime");
	}

	public void setFileId(java.lang.Integer fileId) {
		set("file_id", fileId);
	}

	public java.lang.Integer getFileId() {
		return get("file_id");
	}

}