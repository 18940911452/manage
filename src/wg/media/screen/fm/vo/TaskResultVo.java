package wg.media.screen.fm.vo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import wg.media.screen.fm.model.commandscreen.TaskLog;
import wg.media.screen.fm.model.commandscreencommon.ManageUserInfo;

public class TaskResultVo implements Serializable{
	private static final long serialVersionUID = 84325903165089829L;
	
	private Long task_id;//任务id
	private String title;//任务名称
	private Integer status;//任务状态
	private List<ManageUserInfo> principal;//负责人
	private List<ManageUserInfo>  participant;//参与人
	private String creat_per;//创建人
	private Integer top_id;//专题id
	private String top_name;//专题名称
	private Date start_time;//开始时间
	private Date update_time;//更新时间
	private String task_desc;//任务描述
	private List<TaskLog> planList;//任务操作记录列表
	private List<TaskLog> fileList;//附件列表
	private String plan;//任务进度
	
	
	public String getPlan() {
		return plan;
	}
	public void setPlan(String plan) {
		this.plan = plan;
	}
	public List<TaskLog> getFileList() {
		return fileList;
	}
	public void setFileList(List<TaskLog> fileList) {
		this.fileList = fileList;
	}
	public Long getTask_id() {
		return task_id;
	}
	public void setTask_id(Long task_id) {
		this.task_id = task_id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	
	public List<ManageUserInfo> getPrincipal() {
		return principal;
	}
	public void setPrincipal(List<ManageUserInfo> principal) {
		this.principal = principal;
	}
	public List<ManageUserInfo> getParticipant() {
		return participant;
	}
	public void setParticipant(List<ManageUserInfo> participant) {
		this.participant = participant;
	}
	public String getCreat_per() {
		return creat_per;
	}
	public void setCreat_per(String creat_per) {
		this.creat_per = creat_per;
	}
	public Integer getTop_id() {
		return top_id;
	}
	public void setTop_id(Integer top_id) {
		this.top_id = top_id;
	}
	public String getTop_name() {
		return top_name;
	}
	public void setTop_name(String top_name) {
		this.top_name = top_name;
	}
	public Date getStart_time() {
		return start_time;
	}
	public void setStart_time(Date start_time) {
		this.start_time = start_time;
	}
	public Date getUpdate_time() {
		return update_time;
	}
	public void setUpdate_time(Date update_time) {
		this.update_time = update_time;
	}
	public String getTask_desc() {
		return task_desc;
	}
	public void setTask_desc(String task_desc) {
		this.task_desc = task_desc;
	}
	public List<TaskLog> getPlanList() {
		return planList;
	}
	public void setPlanList(List<TaskLog> planList) {
		this.planList = planList;
	}
	
	
	
	
	
}
