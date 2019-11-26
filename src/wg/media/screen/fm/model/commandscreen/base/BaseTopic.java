package wg.media.screen.fm.model.commandscreen.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseTopic<M extends BaseTopic<M>> extends Model<M> implements IBean {

	public void setTId(java.lang.Integer tId) {
		set("t_id", tId);
	}

	public java.lang.Integer getTId() {
		return get("t_id");
	}

	public void setTopicName(java.lang.String topicName) {
		set("topic_name", topicName);
	}

	public java.lang.String getTopicName() {
		return get("topic_name");
	}

	public void setTopicCreator(java.lang.String topicCreator) {
		set("topic_creator", topicCreator);
	}

	public java.lang.String getTopicCreator() {
		return get("topic_creator");
	}

	public void setTopicPrincipal(java.lang.String topicPrincipal) {
		set("topic_principal", topicPrincipal);
	}

	public java.lang.String getTopicPrincipal() {
		return get("topic_principal");
	}

	public void setTopicParticipant(java.lang.String topicParticipant) {
		set("topic_participant", topicParticipant);
	}

	public java.lang.String getTopicParticipant() {
		return get("topic_participant");
	}

	public void setBelongToMedia(java.lang.String belongToMedia) {
		set("belong_to_media", belongToMedia);
	}

	public java.lang.String getBelongToMedia() {
		return get("belong_to_media");
	}

	public void setKeyword(java.lang.String keyword) {
		set("keyword", keyword);
	}

	public java.lang.String getKeyword() {
		return get("keyword");
	}

	public void setCreatetime(java.util.Date createtime) {
		set("createtime", createtime);
	}

	public java.util.Date getCreatetime() {
		return get("createtime");
	}

	public void setTopicLabel(java.lang.String topicLabel) {
		set("topic_label", topicLabel);
	}

	public java.lang.String getTopicLabel() {
		return get("topic_label");
	}

	public void setTopicDesc(java.lang.String topicDesc) {
		set("topic_desc", topicDesc);
	}

	public java.lang.String getTopicDesc() {
		return get("topic_desc");
	}

	public void setSceneId(java.lang.String sceneId) {
		set("scene_id", sceneId);
	}

	public java.lang.String getSceneId() {
		return get("scene_id");
	}

	public void setTopicEnclosure(java.lang.String topicEnclosure) {
		set("topic_enclosure", topicEnclosure);
	}

	public java.lang.String getTopicEnclosure() {
		return get("topic_enclosure");
	}

	public void setStartTime(java.util.Date startTime) {
		set("start_time", startTime);
	}

	public java.util.Date getStartTime() {
		return get("start_time");
	}

	public void setEndTime(java.util.Date endTime) {
		set("end_time", endTime);
	}

	public java.util.Date getEndTime() {
		return get("end_time");
	}

	public void setStatus(java.lang.Integer status) {
		set("status", status);
	}

	public java.lang.Integer getStatus() {
		return get("status");
	}

	public void setUpdateTime(java.util.Date updateTime) {
		set("update_time", updateTime);
	}

	public java.util.Date getUpdateTime() {
		return get("update_time");
	}

	public void setInstId(java.lang.Integer instId) {
		set("inst_id", instId);
	}

	public java.lang.Integer getInstId() {
		return get("inst_id");
	}

}