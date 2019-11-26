package wg.user.mobileimsdk.server.model.vo;

import java.io.Serializable;

public class ManageUserVo extends MonitorConfigSearchVo implements Serializable {

	private static final long serialVersionUID = -8595845355892439079L;
	private Integer id; // 用户id
	private String userName; // 用户名称
	private String realName; // 用户真实名称
	private String creatTime; // 用户创建时间
	private Integer roleLevel; // 用户角色级别
	private String roleName; // 用户所属的角色名称
	private Integer roleId; // 用户所属的角色ID
	private String dep; // 用户所属的部门名称
	private Integer depId; // 用户所属的部门名称ID
	private String ins; // 用户所属的单位名称
	private Integer insId; // 用户所属的单位名称ID
	private String mediaName; // 媒体名称

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public String getDep() {
		return dep;
	}

	public void setDep(String dep) {
		this.dep = dep;
	}

	public String getIns() {
		return ins;
	}

	public void setIns(String ins) {
		this.ins = ins;
	}

	public Integer getDepId() {
		return depId;
	}

	public void setDepId(Integer depId) {
		this.depId = depId;
	}

	public Integer getInsId() {
		return insId;
	}

	public void setInsId(Integer insId) {
		this.insId = insId;
	}

	public Integer getRoleId() {
		return roleId;
	}

	public void setRoleId(Integer roleId) {
		this.roleId = roleId;
	}

	public String getRealName() {
		return realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}

	public String getCreatTime() {
		return creatTime;
	}

	public void setCreatTime(String creatTime) {
		this.creatTime = creatTime;
	}

	public Integer getRoleLevel() {
		return roleLevel;
	}

	public void setRoleLevel(Integer roleLevel) {
		this.roleLevel = roleLevel;
	}

	public String getMediaName() {
		return mediaName;
	}

	public void setMediaName(String mediaName) {
		this.mediaName = mediaName;
	}

}
