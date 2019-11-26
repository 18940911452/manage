package wg.media.screen.fm.model.commandscreen.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseNoticeCommentsReply<M extends BaseNoticeCommentsReply<M>> extends Model<M> implements IBean {

	public void setId(java.lang.Integer id) {
		set("id", id);
	}

	public java.lang.Integer getId() {
		return get("id");
	}

	public void setCommentsId(java.lang.Integer commentsId) {
		set("comments_id", commentsId);
	}

	public java.lang.Integer getCommentsId() {
		return get("comments_id");
	}

	public void setReplyPsnId(java.lang.Integer replyPsnId) {
		set("reply_psn_id", replyPsnId);
	}

	public java.lang.Integer getReplyPsnId() {
		return get("reply_psn_id");
	}

	public void setContent(java.lang.String content) {
		set("content", content);
	}

	public java.lang.String getContent() {
		return get("content");
	}

	public void setCreateTime(java.util.Date createTime) {
		set("create_time", createTime);
	}

	public java.util.Date getCreateTime() {
		return get("create_time");
	}

}