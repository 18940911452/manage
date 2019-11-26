package wg.media.screen.fm.model.commandscreen.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseMeeting<M extends BaseMeeting<M>> extends Model<M> implements IBean {

	public void setMId(java.lang.Integer mId) {
		set("m_id", mId);
	}

	public java.lang.Integer getMId() {
		return get("m_id");
	}

	public void setMeetingName(java.lang.String meetingName) {
		set("meeting_name", meetingName);
	}

	public java.lang.String getMeetingName() {
		return get("meeting_name");
	}

	public void setMeetingContent(java.lang.String meetingContent) {
		set("meeting_content", meetingContent);
	}

	public java.lang.String getMeetingContent() {
		return get("meeting_content");
	}

	public void setMeetingPlace(java.lang.String meetingPlace) {
		set("meeting_place", meetingPlace);
	}

	public java.lang.String getMeetingPlace() {
		return get("meeting_place");
	}

	public void setMeetingParticipant(java.lang.String meetingParticipant) {
		set("meeting_participant", meetingParticipant);
	}

	public java.lang.String getMeetingParticipant() {
		return get("meeting_participant");
	}

	public void setStartDate(java.util.Date startDate) {
		set("start_date", startDate);
	}

	public java.util.Date getStartDate() {
		return get("start_date");
	}

	public void setEndDate(java.util.Date endDate) {
		set("end_date", endDate);
	}

	public java.util.Date getEndDate() {
		return get("end_date");
	}

	public void setMeetingYear(java.lang.Integer meetingYear) {
		set("meeting_year", meetingYear);
	}

	public java.lang.Integer getMeetingYear() {
		return get("meeting_year");
	}

	public void setMeetingMonth(java.lang.String meetingMonth) {
		set("meeting_month", meetingMonth);
	}

	public java.lang.String getMeetingMonth() {
		return get("meeting_month");
	}

	public void setMeetingDay(java.lang.String meetingDay) {
		set("meeting_day", meetingDay);
	}

	public java.lang.String getMeetingDay() {
		return get("meeting_day");
	}

	public void setStartTime(java.lang.String startTime) {
		set("start_time", startTime);
	}

	public java.lang.String getStartTime() {
		return get("start_time");
	}

	public void setEndTime(java.lang.String endTime) {
		set("end_time", endTime);
	}

	public java.lang.String getEndTime() {
		return get("end_time");
	}

	public void setMeetingCreator(java.lang.String meetingCreator) {
		set("meeting_creator", meetingCreator);
	}

	public java.lang.String getMeetingCreator() {
		return get("meeting_creator");
	}

	public void setBelongToTopic(java.lang.String belongToTopic) {
		set("belong_to_topic", belongToTopic);
	}

	public java.lang.String getBelongToTopic() {
		return get("belong_to_topic");
	}

	public void setSourceId(java.lang.Integer sourceId) {
		set("source_id", sourceId);
	}

	public java.lang.Integer getSourceId() {
		return get("source_id");
	}

}
