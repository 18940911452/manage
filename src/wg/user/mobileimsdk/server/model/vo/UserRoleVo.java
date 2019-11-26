package wg.user.mobileimsdk.server.model.vo;

import java.io.Serializable;

public class UserRoleVo implements Serializable {

    private static final long serialVersionUID = -3083439993169372716L;
    private Integer roleId; // 角色id
    private String name; // 角色名称
    private String level; // 角色级别
    private String dep; // 角色所属部门
    private String depId; // 角色所属部门ID
    private String ins; // 角色所属单位
    private String insId; // 角色所属单位ID
    private String createTime;// 角色创建时间
    private Integer sysType; //角色类型

    public Integer getSysType() {
        return sysType;
    }

    public void setSysType(Integer sysType) {
        this.sysType = sysType;
    }

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
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

    public String getDepId() {
        return depId;
    }

    public void setDepId(String depId) {
        this.depId = depId;
    }

    public String getInsId() {
        return insId;
    }

    public void setInsId(String insId) {
        this.insId = insId;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}
