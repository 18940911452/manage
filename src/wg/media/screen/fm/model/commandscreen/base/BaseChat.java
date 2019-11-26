package wg.media.screen.fm.model.commandscreen.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseChat<M extends BaseChat<M>> extends Model<M> implements IBean {

	public void setCId(java.lang.Integer cId) {
		set("c_id", cId);
	}

	public java.lang.Integer getCId() {
		return get("c_id");
	}

	public void setTId(java.lang.Integer tId) {
		set("t_id", tId);
	}

	public java.lang.Integer getTId() {
		return get("t_id");
	}

	public void setChatTopic(java.lang.String chatTopic) {
		set("chat_topic", chatTopic);
	}

	public java.lang.String getChatTopic() {
		return get("chat_topic");
	}

	public void setCreatetime(java.util.Date createtime) {
		set("createtime", createtime);
	}

	public java.util.Date getCreatetime() {
		return get("createtime");
	}

	public void setChatCreator(java.lang.String chatCreator) {
		set("chat_creator", chatCreator);
	}

	public java.lang.String getChatCreator() {
		return get("chat_creator");
	}

	public void setViewNum(java.lang.Integer viewNum) {
		set("view_num", viewNum);
	}

	public java.lang.Integer getViewNum() {
		return get("view_num");
	}

	public void setCommentNum(java.lang.Integer commentNum) {
		set("comment_num", commentNum);
	}

	public java.lang.Integer getCommentNum() {
		return get("comment_num");
	}

	public void setUpdatetime(java.util.Date updatetime) {
		set("updatetime", updatetime);
	}

	public java.util.Date getUpdatetime() {
		return get("updatetime");
	}

	public void setChatContent(java.lang.String chatContent) {
		set("chat_content", chatContent);
	}

	public java.lang.String getChatContent() {
		return get("chat_content");
	}

}
