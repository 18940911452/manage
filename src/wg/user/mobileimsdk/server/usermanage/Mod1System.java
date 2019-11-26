package wg.user.mobileimsdk.server.usermanage;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Record;
import wg.media.screen.fm.model.commandscreencommon.*;
import wg.user.mobileimsdk.server.controller.IController;
import wg.user.mobileimsdk.server.model.vo.ManageUserVo;
import wg.user.mobileimsdk.server.model.vo.MonitorConfigSearchVo;
import wg.user.mobileimsdk.server.model.vo.ResultMessageVo;
import wg.user.mobileimsdk.server.model.vo.UserRoleVo;
import wg.user.mobileimsdk.server.service.SystemImpl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Mod1System extends System_Controller {
    public static String localHeadAdress = PropKit.get("localHeadAdress");
    private ManageUserInfo user;

    private ManageUserInfo getManageUserInfo() {
        if (user == null) {
            user = (ManageUserInfo) getSession().getAttribute("login_user");
        }
        return user;
    }

    public void index() {
        render("systemconfiguration.html");
    }

    public void load() {
        String path = getPara("path");
        render(path);
    }

    /**
     * 单位配置 单位配置 单位配置 单位配置 单位配置 单位配置 单位配置 单位配置 单位配置 单位配置 展示单位
     *
     * @author krl
     */
    public void loadInstitution() {

        Integer pageNo = getParaToInt("pageNo", 1);
        Integer pageSize = getParaToInt("pageSize", 20);
        String name = getPara("name");
        String path = getPara("path");
        ManageUserInfo user = getManageUserInfo();
        MonitorConfigSearchVo search = new MonitorConfigSearchVo();
        search.setPageNo(pageNo);
        search.setPageSize(pageSize);
        Object[] res = SystemImpl.ser.loadInstitution(name, search, user);
        if (null != res[0]) {
            List<ManageInstitution> list = (List<ManageInstitution>) res[0];
            setAttr("list", list);
        }
        if (null != res[1]) {
            Integer total = (Integer) res[1];
            setAttr("total", total);
        }
        setAttr("pageNo", pageNo);
        setAttr("pageSize", pageSize);
        render(path);

    }

    /**
     * 添加单位
     */

    public void addInstitution() {
        ManageUserInfo userInfo = getManageUserInfo();
        Map<String, String[]> map = getParaMap();
        JSONObject json1 = new JSONObject();
        dealParams(map, json1);
        String result = null;
        result = SystemImpl.ser.addInstitution(json1.toJSONString(), userInfo);
        JSONObject json = new JSONObject();
        if (null == result) {
            json.put("massage", "false");
            renderJson(json);
            return;
        }
        json.put("massage", result);
        renderJson(json);
    }

//    /**
//     * 修改单位
//     */
//    public void editInstitution() {
//        ManageUserInfo userInfo = getManageUserInfo();
//        Map<String, String[]> map = getParaMap();
//        JSONObject json1 = new JSONObject();
//        dealParams(map, json1);
//        String result = null;
//        result = SystemImpl.ser.editInstitution(json1.toString(),userInfo);
//        JSONObject json = new JSONObject();
//        if (null == result) {
//            json.put("massage", result);
//        }
//        json.put("massage", "false");
//        renderJson(json);
//    }

    /**
     * 回显单位
     */
    public void echoInstitution() {
        String id = getPara("id");
        JSONObject institution = null;
        institution = SystemImpl.ser.echoInstitution(id);
        renderJson(institution);
    }

    /**
     * 删除单位
     */
    public void deleteInstitution() {
        String id = getPara("id");
        String result = null;
        ManageUserInfo user = getManageUserInfo();
        result = SystemImpl.ser.deleteInstitution(id, user);
        JSONObject json = new JSONObject();
        if (null == result) {
            json.put("massage", "false");
            renderJson(json);
            return;
        }
        json.put("massage", result);
        renderJson(json);
    }

    /**
     * 展示部门信息
     */
    @SuppressWarnings("unchecked")
    public void loadDepartment() {
        Integer pageNo = getParaToInt("pageNo", 1);
        Integer pageSize = getParaToInt("pageSize", 10);
        String name = getPara("name");
        String path = getPara("path");
        ManageUserInfo user = getUser();
//		ManageUserInfo user = (ManageUserInfo) getSession().getAttribute("login_user");
        MonitorConfigSearchVo search = new MonitorConfigSearchVo();
        search.setPageNo(pageNo);
        search.setPageSize(pageSize);
        Object[] res = SystemImpl.ser.loadDepartment(user, search, name);
        if (null != res[0]) {
            List<ManageUserVo> list = (List<ManageUserVo>) res[0];
            setAttr("list", list);
        }
        if (null != res[1]) {
            Integer total = (Integer) res[1];
            setAttr("total", total);
        }
        setAttr("pageNo", pageNo);
        setAttr("pageSize", pageSize);
        render(path);
    }

    public void findDepartByInsIdPlus() {
        Integer pageNo = getParaToInt("pageNo", 1);
        Integer pageSize = getParaToInt("pageSize", 10);
        Integer insId = getParaToInt("insId");
        String path = getPara("path", "");
        ManageUserInfo user = getManageUserInfo();
        Object[] objects = SystemImpl.ser.findDepartByInsIdPlus(user, insId, pageNo, pageSize);
        if (path != "") {
            sendObjedct(objects, pageNo, pageSize, path);
            return;
        }
        JSONObject object = new JSONObject();
        object.put("data", objects[0]);
        object.put("total", objects[1]);
        renderJson(object);
    }

    public void test() {
        Integer pageNo = getParaToInt("pageNo", 1);
        Integer pageSize = getParaToInt("pageSize", 10);
        Integer insId = getParaToInt("insId");
        String path = getPara("path");
        ManageUserInfo user = getManageUserInfo();
        Object[] objects = SystemImpl.ser.findDepartByInsIdPlus(user, insId, pageNo, pageSize);
        JSONObject json = new JSONObject();
        json.put("data", objects);
        renderJson(json);
    }

    /**
     * 添加部门信息
     */
    public void addDepartment() {
        JSONObject json = new JSONObject();
        json.put("leader", getPara("leader"));
        json.put("contacter", getPara("contacter"));
        json.put("address", getPara("address"));
        json.put("name", getPara("name"));
        json.put("telephone", getPara("telephone"));
        json.put("insId", getPara("insId"));
        json.put("mediaId", getPara("mediaId"));
        json.put("email", getPara("email"));
        ManageUserInfo user = getManageUserInfo();
        String result = SystemImpl.ser.addDepartment(json.toJSONString(), user);
        if (null == result) {
            result = "error";
        }
        JSONObject jsonResult = new JSONObject();
        jsonResult.put("massage", result);
        renderJson(jsonResult);
    }

    /**
     * 修改部门信息
     */
    public void editDepartment() {
        Map<String, String[]> paramMap = getParaMap();
        JSONObject json = new JSONObject();
        dealParams(paramMap, json);
        ManageUserInfo user = getManageUserInfo();
        String result = SystemImpl.ser.updataDepartment(json.toJSONString(), user);
        if (null == result) {
            result = "error";
        }
        JSONObject jsonResult = new JSONObject();
        jsonResult.put("result", result);
        renderJson(jsonResult);
    }

    /**
     * 删除部门信息
     */
    public void deleteDepartment() {
        String depid = getPara("depId");

        ManageUserInfo user = getManageUserInfo();

        String result = SystemImpl.ser.deleteDepartment(depid, user);
        if (null == result) {
            result = "false";
        }
        JSONObject json = new JSONObject();
        json.put("massage", result);
        renderJson(json);
    }

    /**
     * 回显部门信息
     */
    public void echoDepartment() {
        String id = getPara("id");
        JSONObject institution = null;
        institution = SystemImpl.ser.echoDepartment(id);
        renderJson(institution);
    }

    /**
     * 显示用户信息
     */
    @SuppressWarnings("unchecked")
    public void loadUser() {

        Integer pageNo = getParaToInt("pageNo", 1);
        Integer pageSize = getParaToInt("pageSize", 10);
        String name = getPara("username", null);
        String insId = getPara("insId", "");
        String depId = getPara("depId", "");
        String path = getPara("path");
        ManageUserInfo user = getManageUserInfo();
        MonitorConfigSearchVo search = new MonitorConfigSearchVo();
        search.setPageNo(pageNo);
        search.setPageSize(pageSize);
        Object[] res = SystemImpl.ser.loadUser(user, search, name, insId, depId);

        if (null != res[0]) {
            List<ManageUserVo> list = (List<ManageUserVo>) res[0];
            setAttr("list", list);
        }
        if (null != res[1]) {
            Integer total = (Integer) res[1];
            setAttr("total", total);
        }
        setAttr("pageNo", pageNo);
        setAttr("pageSize", pageSize);
        render(path);
    }

    /**
     * @Title 加载用户列表
     * @Description
     * @date 2019年3月1日-下午4:47:06
     * @author Yipeng.zou void 测试接口：http://localhost:8088/system/mod1/loadUnderUser
     */
    public void loadUnderUser() {
        ManageUserInfo user = getManageUserInfo();

        JSONArray result = null;
        result = SystemImpl.ser.loadUnderUser(user);
        renderJson(result);
    }

    /**
     * @Title showUserAction
     * @author Yipeng.zou
     * @date 2019年3月4日 测试接口：http://localhost:8088/system/mod1/showUserAction?uid=625
     */
    public void showUserAction() {
        String suid = getPara("uid");
        JSONObject object = null;
        ManageUserInfo user = null;
        List<ManagePerm> perm = null;
        if (suid != null) {
            try {
                Integer uid = Integer.valueOf(suid);
                user = SystemImpl.ser.getUserById(uid);
                perm = SystemImpl.ser.getUserPermList(user);
                object = SystemImpl.ser.showUserAction(uid, perm);

                renderJson(object);
            } catch (NumberFormatException e) {
                // TODO: handle exception
            }
        } else {
            renderText("发生错误");
        }

    }

    /**
     * @Title loadUnderUserByUid
     * @Description
     * @date 2019年3月20日-下午3:47:06
     * @author Yipeng.zou void
     * 测试接口：http://localhost:8088/system/mod1/loadUnderUserByUid?uid=
     */
    public void loadUnderUserByUid() {
        Integer uid = getParaToInt("uid");
        JSONArray result = null;
        result = SystemImpl.ser.loadUnderUserById(uid);
        renderJson(result);
    }

    /**
     * @Title showUserAction
     * @author Yipeng.zou
     * @date 2019年3月4日
     * 测试接口：http://localhost:8088/system/mod1/showUserActionByTime?uid=625
     */
    public void showUserActionByTime() {
        String suid = getPara("uid");
        LinkedHashMap object = new LinkedHashMap();
        ManageUserInfo user = null;
        List<ManagePerm> perm = null;
        if (suid != null) {
            try {
                Integer uid = Integer.valueOf(suid);
                user = SystemImpl.ser.getUserById(uid);
                perm = SystemImpl.ser.getUserPermList(user);
                object = SystemImpl.ser.showUserActionByTime(uid, perm);
                renderJson(object);
            } catch (NumberFormatException e) {
                // TODO: handle exception
            }
        } else {
            renderText("发生错误");
        }

    }

    /**
     * 添加用户信息
     */
    public void addUser() {
        Map<String, String[]> paramMap = getParaMap();
        JSONObject json = new JSONObject();
        dealParams(paramMap, json);// 处理参数
        ManageUserInfo user = getManageUserInfo();
        String result = null;
        String headPath = localHeadAdress + "chatsource/headicon/";
        String basePath = "chatsource/headicon/";
        result = SystemImpl.ser.addUser(json.toJSONString(), user, headPath, basePath);
        if (null == result) {
            result = "error";
        }
        JSONObject jsonResult = new JSONObject();
        jsonResult.put("result", result);
        renderJson(jsonResult);
    }

    /**
     * 回显用户
     */

    public void echoUser() {
        String id = getPara("id");
        JSONObject institution = null;
        institution = SystemImpl.ser.echoUser(id);
        renderJson(institution);
    }

    /**
     * 修改用户信息
     */
    public void editUser() {
        Map<String, String[]> paramMap = getParaMap();
        JSONObject json = new JSONObject();
        dealParams(paramMap, json);
        ManageUserInfo user = getManageUserInfo();
        String result = null;
        result = SystemImpl.ser.editUser(json.toJSONString(), user);
        if (null == result) {
            result = "false";
        }
        JSONObject jsonResult = new JSONObject();
        jsonResult.put("massage", result);
        renderJson(jsonResult);

    }

    /**
     * 删除用户信息
     */

    public void deleteUser() {
        String id = getPara("id");// 用户id
        ManageUserInfo user = getManageUserInfo();
        String result = null;
        result = SystemImpl.ser.deleteUser(id, user);
        JSONObject json = new JSONObject();
        if ("删除失败".equals(result)||"无权删除该用户".equals(result)) {
            json.put("message", result);
            json.put("status", -1);
            renderJson(json);
        } else {
            json.put("message", result);
            json.put("status", 1);
            renderJson(json);
        }

    }

    /**
     * 查询单位列表
     */
    public void queryInstitution() {
        ManageUserInfo user = getManageUserInfo();
        JSONArray result = null;
        result = SystemImpl.ser.queryInstitution(user);
        renderJson(result);
    }

    /**
     * 部门列表
     */

    public void queryDepartment() {
        String mediaId = getPara("mediaId");
        ManageUserInfo user = getSessionAttr(IController.LOGIN_USER);
        Integer userId = user.getUserId();
        String sqlUser = "select manage_depart.inst_id from manage_user_info "
                + " left join manage_depart on manage_user_info.dep_id=manage_depart.dep_id "
                + " left join manage_institution on manage_depart.inst_id=manage_institution.id "
                + " where manage_user_info.user_id= '" + userId + "'";
        ManageUserInfo userInfos = ManageUserInfo.dao.findFirst(sqlUser);
        Integer inst_id = userInfos.getInt("inst_id");
        JSONArray result = null;
        result = SystemImpl.ser.queryDepartment(mediaId, user == null ? 0 : inst_id);
        renderJson(result);
    }

    /**
     * 查询角色列表
     */
    public void queryRole() {
        ManageUserInfo user = getManageUserInfo();
        Integer page = getParaToInt("page", 1);
        Integer pageSize = getParaToInt("pageSize", 10);
        JSONObject res = SystemImpl.ser.queryRole(user, page, pageSize);
        if (res != null) {
            renderJson(res);
        } else {
            renderJson("massage", "false");
        }
    }

    /**
     * 根据ID查询角色
     */
    public void queryRoleByid() {
        ManageUserInfo user = getManageUserInfo();
        String id = getPara("id");
        JSONObject res = SystemImpl.ser.queryRoleByid(user, id);
        if (res != null) {
            renderJson(res);
        } else {
            renderJson("massage", "false");
        }
    }

    /**
     * 用户模块的单位信息
     */
    public void queryInstitutionUser() {
        String role_id = getPara("role_id");// 角色id
        ManageUserInfo user = getManageUserInfo();
        JSONArray result = null;
        result = SystemImpl.ser.queryInstitutionUser(user, role_id);
        renderJson(result);
    }

    /**
     * 获取ukey信息
     * <p>
     * Title:
     * </p>
     * <p>
     * Description:
     * </p>
     * <p>
     * Company:中科闻歌
     * </p>
     *
     * @param pageNum:页号,pageSize:页面大小,ukey:ukeyid，用于模糊查询，username：账号名，用于模糊查询
     * @return void data中可用字段 "auto_id": ukey id, "ukey_id": ukey值, "personName":
     * "真实姓名", "username": 用户名 "name": "部门名", "status":0启用，1禁用
     * @author lzk
     * @date 2017年10月15日
     */
    public void getUkeyInfoList() {
        Integer pageNum = getParaToInt("pageNo", 1);
        Integer pageSize = getParaToInt("pageSize", 10);
        String ukey = getPara("ukey");
        String username = getPara("username");
        Integer status = getParaToInt("status");
        String rType = getPara("Rtype");

        Object[] ukyInfoList = null;
        try {
            ukyInfoList = (Object[]) SystemImpl.ser.getUkeyInfoList(pageNum, pageSize, username, ukey, status);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // data为null表示暂无数据
        if (ukyInfoList != null && ukyInfoList.length > 0) {
            setAttr("data", ukyInfoList[0]);
            setAttr("total", ukyInfoList[1]);
        }
        if (StrKit.notBlank(rType) && rType.equals("json")) {
            renderJson(ukyInfoList);
        } else {
            render("/dynamic/manager/mod12/ukey_html.html");
        }
    }

    /**
     * 获取系统角色信息 url: system_al/mod4?name=&type=
     *
     * @param name:角色名称 type:角色类型
     */

    public void getSysRoleList() {
        Integer pageNo = getParaToInt("pageNo", 1);
        Integer pageSize = getParaToInt("pageSize", 10);
        String path = getPara("path");
        ManageUserInfo user = getManageUserInfo();

        List<UserRoleVo> listVo = new ArrayList<UserRoleVo>();
        Long total = 0L;
        if (null != user) {
            Object[] result = SystemImpl.ser.getSysRoleList(user, pageNo, pageSize);
            if (null != result && result.length == 2) {
                listVo = (List<UserRoleVo>) result[0];
                total = Long.parseLong(result[1].toString());
            }
        }
        setAttr("list", listVo);
        setAttr("total", total);
        render(path);
    }

    /**
     * 获取系统全部权限
     */
    @SuppressWarnings("unchecked")
    public void getSysAllPerm() {
        /*
         * List<Record> perms = null; ManageUserInfo user = this.getUser(); try{
         * if(null!=user){ perms =
         * System_al_SC.getRoleService().getSysAllPerm(user.getId()); } }catch(Exception
         * e){ e.printStackTrace(); } if(null==perms) perms=new ArrayList<Record>();
         * ManageUserInfo user = this.getUser();
         */
        Object menu = getSessionAttr(IController.__menu);
        List<ManagePerm> perms = (List<ManagePerm>) menu;
        renderJson(perms);
    }

    /**
     * 根据角色id获取的权限
     */
    public void getSysPermByRoleId() {
        Integer id = getParaToInt("rid");
        if (id == null || "".equals(id)) {
            renderJson("error");
        }
        ManageUserInfo user = getManageUserInfo();
        List<ManagePerm> sysAllPerm = SystemImpl.ser.getSysPermByRoleId(id, user);
        JSONObject json = new JSONObject();
        if (sysAllPerm != null) {
            json.put("data", sysAllPerm);
            json.put("massage", "success");
            renderJson(json);
        } else {
            json.put("massage", "false");
            renderJson(json);
        }
    }

    /**
     * 获取角色权限
     */
    public void getSysRolePerm() {
        List<Record> perms = null;
        Integer id = getParaToInt("id");
        try {
            perms = SystemImpl.ser.getSysRolePerm(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (null == perms)
            perms = new ArrayList<Record>();
        renderJson(perms);
    }

    /**
     * 更新角色权限
     */
    public void updateSysRolePerm() {
        ManageUserInfo user = getManageUserInfo();
        Integer id = getParaToInt("id");
        Integer sysType = getParaToInt("sysType");
        String roleName = getPara("roleName");
        Integer level = getParaToInt("level");
        String permids = getPara("permids");
        String[] permArr = null;
        if (null != permids) {
            permArr = permids.split(",");
        }
        boolean result = SystemImpl.ser.updateSysRolePerm(id, permArr, roleName, sysType, level, user);
        JSONObject json = new JSONObject();
        json.put("massage", result);
        renderJson(json);
    }

    /**
     * 添加角色
     *
     * @param name:角色名称 type:角色类型 permids:权限集合
     */
    public void addSysRole() {
        String name = getPara("name");//角色名称
        Integer level = getParaToInt("level", 4);// 角色级别
        Integer sys_type = getParaToInt("sysType");

        String permids = getPara("permids");
        ManageUserInfo user = getManageUserInfo();
        String result = null;
        if (null != user && null != permids) {
            String[] permArr = permids.split(",");
            result = SystemImpl.ser.addSysRole(name, level, permArr, user, sys_type);
        }
        JSONObject jsonResult = new JSONObject();
        jsonResult.put("massage", result);
        renderJson(jsonResult);
    }

    //删除角色
    public void deleteSysRole() {
        ManageUserInfo user = getManageUserInfo();
        Integer id = getParaToInt("id");
        JSONObject json = new JSONObject();
        if (SystemImpl.ser.getsysuser(id)) {
            json.put("massage", "该角色下还有用户不能删除");
            renderJson(json);
            return;
        }
        String result = SystemImpl.ser.deleteSysRole(id, user);
        json.put("massage", result);
        renderJson(json);
    }

    // 回显角色
    public void getsysRole() {
        Integer id = getParaToInt("id");
        ManageRole result = SystemImpl.ser.getsysRole(id);
        renderJson(result);
    }

    /**
     * 获取登录用户所能看到的单位信息（超级管理员-全部 单位管理员-本单位 部门管理员-本单位）
     */
    public void obtainInstatutionByUserLevel() {
        ManageUserInfo user = getManageUserInfo();
        String result = SystemImpl.ser.obtainInstatutionByUserLevel(user);
        renderJson(result);
    }

    /**
     * 根据单位ID获取部门
     */
    public void obtainDepByInsId() {
        String insId = getPara("insId");
        ManageUserInfo user = getManageUserInfo();
        String result = SystemImpl.ser.obtainDepByInsId(user, insId);
        renderJson(result);
    }

    /**
     * 权限 获取元数据下拉框
     */
    // public void queryMarall() {
    // List<MetadataMgr> allperm = SystemImpl.ser.getMrgallList();
    // renderJson(allperm);
    // }

    /**
     * 权限 获取角色的数据权限
     */
    public void getSysRoleMrg() {
        List<Record> perms = null;
        Integer id = getParaToInt("id");
        try {
            perms = SystemImpl.ser.getSysRoleMgr(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (null == perms)
            perms = new ArrayList<Record>();
        renderJson(perms);
    }

    /**
     * 添加权限
     */
    // public void addRoleMgr(){
    // Integer id = getParaToInt("id");
    // String permids = getPara("permids");
    // ManageUserInfo user = (ManageUserInfo)
    // getSession().getAttribute("login_user");
    // String roleName = getPara("roleName");
    // String result = null;
    // if(null!=user && null!=permids){
    // String[] MgrArr = null;
    // MgrArr = permids.split(",");
    // result = SystemImpl.ser.addRoleMgr(id,MgrArr,user);
    // }
    // renderText(result);
    // }

    /**
     * 权限 修改
     */
    public void updatesysRole() {
        Integer id = getParaToInt("id");
        String permids = getPara("permids");
        String roleName = getPara("roleName");
        String[] MgrArr = null;
        if (null != permids) {
            MgrArr = permids.split(",");
        }
        boolean result = SystemImpl.ser.updateSysRoleMrg(id, MgrArr, roleName);
        renderJson(result);
    }

    private void dealParams(Map<String, String[]> paramMap, JSONObject json) {
        for (Map.Entry<String, String[]> entry : paramMap.entrySet()) {
            String[] values = entry.getValue();
            if (null == values) {
                continue;
            }
            json.put(entry.getKey(), values[0]);
        }
    }

    public void queryMediaListByInsId() throws Exception {
        ManageUserInfo user = getSessionAttr(IController.LOGIN_USER);
        Integer userId = user.getUserId();
        String sqlUser = "select manage_depart.inst_id from manage_user_info "
                + " left join manage_depart on manage_user_info.dep_id=manage_depart.dep_id "
                + " left join manage_institution on manage_depart.inst_id=manage_institution.id "
                + " where manage_user_info.user_id= '" + userId + "'";
        ManageUserInfo userInfos = ManageUserInfo.dao.findFirst(sqlUser);
        Integer inst_id = userInfos.getInt("inst_id");
        List<Record> list = SystemImpl.ser.queryMediaListByInsId(user == null ? 0 : inst_id);
        renderJson(new ResultMessageVo(1, "成功", list));
    }


    /*查询用户对应的角色权限
     * 1-超级管理员
     * 2-单位管理员
     * 3-部门管理员
     * 4-普通用户
     * */
    public void suchLevel() {
        ManageUserInfo user = getManageUserInfo();
        Integer integer = SystemImpl.ser.suchLevel(user);
        renderJson(integer);
    }

    //查询机构 返回一个freemarker页面
    public void suchInstitution() {
        Integer page = getParaToInt("pageNo", 1);
        Integer pageSize = getParaToInt("pageSize", 100);
        String name = getPara("name", "");
        String path = getPara("path");
        ManageUserInfo user = getManageUserInfo();
        Object[] objects = SystemImpl.ser.suchInstitutionPage(user, page, pageSize, name);
        sendObjedct(objects, page, pageSize, path);
    }

    //查询机构 返回json
    public void findInstitution() {
        ManageUserInfo user = getManageUserInfo();
        renderJson(SystemImpl.ser.suchInstitution(user));
    }


    public void findDepartByInsId() {
        Long insId = getParaToLong("insId");
        Integer page = getParaToInt("page", 1);
        Integer pageSize = getParaToInt("p" +
                "ageSize", 10);
        ManageUserInfo user = getManageUserInfo();
        List<ManageDepart> res = SystemImpl.ser.findDepartByInsId(insId, page, pageSize);
        renderJson(res);
    }

    //机构Id查询对应媒体列表
    public void findMediaByInsId() {
        ManageUserInfo user = getManageUserInfo();
        Integer insId = getParaToInt("insId");
        List<ManageMedia> mediaByInsId = SystemImpl.ser.findMediaByInsId(user, insId);
        JSONObject json = new JSONObject();
        if (mediaByInsId != null) {
            json.put("data", mediaByInsId);
            json.put("massage", "success");
            renderJson(json);
        } else {
            json.put("massage", "false");
            renderJson(json);
        }

    }

    //媒体Id查询对应部门列表
    public void findDepartByMediaId() {
        ManageUserInfo user = getManageUserInfo();
        Integer mediaId = getParaToInt("mediaId");
        List<ManageDepart> departByMediaId = SystemImpl.ser.findDepartByMediaId(user, mediaId);
        JSONObject json = new JSONObject();
        if (departByMediaId != null) {
            json.put("data", departByMediaId);
            json.put("massage", "success");
            renderJson(json);
        } else {
            json.put("massage", "false");
            renderJson(json);
        }
    }

    //返给前台用户信息
    public void suchUserLevel() {
        Integer level = SystemImpl.ser.suchLevel(getManageUserInfo());
        JSONObject object = new JSONObject();
        object.put("level", level);
        renderJson(object);
    }

    public void echoMedia() {
        String id = getPara("id");
        renderJson(SystemImpl.ser.echoMedia(id));
    }


    private void sendJson(JSONObject json) {
        if (json == null || json.isEmpty()) {
            JSONObject jsonObject = new JSONObject();
            json.put("message", "服务器异常");
            jsonObject.put("status", -1);
            renderJson(jsonObject);
            return;
        } else if ("-2".equals(json.getInteger("status"))) {
            renderJson(json);
            return;
        }
        json.put("message", "成功");
        json.put("status", 1);
        renderJson(json);
    }

    private void sendObjedct(Object[] res, Integer pageNo, Integer pageSize, String path) {
        if (null != res[0]) {
            setAttr("list", res[0]);
        }
        if (null != res[1]) {
            setAttr("total", res[1]);
        }
        setAttr("pageNo", pageNo);
        setAttr("pageSize", pageSize);
        render(path);
    }


}
