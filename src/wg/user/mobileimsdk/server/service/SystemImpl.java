package wg.user.mobileimsdk.server.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.*;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.web.util.HtmlUtils;
import wg.media.screen.fm.model.commandscreencommon.*;
import wg.media.screen.fm.utils.Helper;
import wg.media.screen.fm.utils.LevelUtil;
import wg.user.mobileimsdk.server.model.vo.ManageUserVo;
import wg.user.mobileimsdk.server.model.vo.MonitorConfigSearchVo;
import wg.user.mobileimsdk.server.model.vo.UserRoleVo;
import wg.user.mobileimsdk.server.util.CreateNamePicture;
import wg.user.mobileimsdk.server.util.MD5Util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

public class SystemImpl {
    public static SystemImpl ser = new SystemImpl();
    // **
    // 展示单位信息
    // *

    public Object[] loadInstitution(String name, MonitorConfigSearchVo search, ManageUserInfo user) {
        String sql = "  from manage_institution where 1=1 and status=0    ";
        if (null != name && !"".equals(name.trim())) {
            sql += "	and `name` like '%" + name + "%'	";
        }
        sql += " order by create_time desc";
        Page<ManageInstitution> page = ManageInstitution.dao.paginate(search.getPageNo(), search.getPageSize(),
                " select * ", sql);
        List<ManageInstitution> list = page.getList();
        for (int i = 0; i < list.size(); i++) {
            ManageInstitution manageInstitution = list.get(i);
            String username = user.getUserName();
            manageInstitution.put("username", username);
        }
        Object[] res = new Object[2];
        res[0] = page.getList();
        res[1] = page.getTotalRow();
        return res;
    }

    // **
    // 添加单位信息
    // *
    public String addInstitution(String parameterMap, ManageUserInfo userInfo) {
        try {
            Integer level = suchLevel(userInfo);
            if (level != 1) {
                return "无权限修改";
            }
            JSONObject json = JSONObject.parseObject(parameterMap);
            String name = json.getString("name").trim();
            //验证是否同名，同名则不添加
            String sql = "select name from manage_institution where 1=1 and status=0 and name= ? ";
            List<ManageInstitution> list = ManageInstitution.dao.find(sql, name);
            if (null != list && list.size() > 0) {
                return "有同名机构";
            }
            String sql1 = "SELECT id tId FROM iam.`iam_tenant` WHERE tenant = ?";
            Record record = Db.findFirst(sql1, name);
            if (record == null) {
                return "库中找不到对应的tenent_id";
            }
            JSONObject json2 = JSONObject.parseObject(record.toString());
            Integer tId = json2.getInteger("tId");
            String app_id = "d4070396eba34cb2824cdd0084b32c07";// 声网app_id
            String app_secret = "a906f3f7430c4cca85191df3cee7e8e7";// 声网app_secreat
            String jpush_key = "f4efd981c416b4e182ad3222";// 极光app_key
            String jpush_secret = "c3e621024fa1d2da4f9f6b1f";// 极光secret
            Double longitude = json.getDouble("longitude");//经度
            Double latitude = json.getDouble("latitude");//纬度
//            String leader = json.getString("leader").trim();// 负责人
//            String contacter = json.getString("contacter").trim();// 联系人
//            String telephone = json.getString("telephone").trim();// 电话
//            String fax = json.getString("fax").trim();// 传真
//            String address = json.getString("address").trim();// 地址
//            String zipCode = json.getString("zipCode").trim();// 邮编
//            String email = json.getString("email").trim();// 邮件
//            String web = json.getString("web").trim();// 网址
            ManageInstitution institution = new ManageInstitution();
            institution.setName(name);
            institution.setJpushKey(jpush_key);
            institution.setJpushSecret(jpush_secret);
            institution.setUuid("d6c4f079463d480a92a7de9f5" + name);
            institution.setCaibianHost(name + ".wengetech.com");
            institution.setTenentId(tId);
            institution.setLongitude(longitude);
            institution.setLatitude(latitude);
            institution.setAppId(app_id);
            institution.setAppSecret(app_secret);
            institution.setStatus(0);
            institution.setCreateTime(new Date());
            institution.setSystemCode(-1);
            // mon.setMonitorScope(json.getInteger("scope"));
//            institution.setLeader(leader);
//            institution.setContacter(contacter);
//            institution.setTelephone(telephone);
//            institution.setFax(fax);
//            institution.setAddress(address);
//            institution.setZipCode(zipCode);
//            institution.setEmail(email);
//            institution.setWeb(web);
            boolean save = institution.save();
            if (save) {
                Long id = institution.getId();
                institution.setSystemCode(id.intValue());
                institution.update();
                return "success";
            }
            return "添加失败";
        } catch (
                Exception e) {
            e.printStackTrace();
            return "添加失败";
        }

    }

    // 修改单位
    public String editInstitution(String parameterMap, ManageUserInfo userInfo) {
        try {
            Integer level = suchLevel(userInfo);
            if (level != 1) {
                return "false,无权限修改";
            }
            JSONObject json = JSONObject.parseObject(parameterMap);
            Integer id = json.getInteger("id");
            String name = json.getString("name").trim();
            Double longitude = json.getDouble("longitude");//经度
            Double latitude = json.getDouble("latitude");//纬度
//            String leader = json.getString("leader").trim();// 负责人
//            String contacter = json.getString("contacter").trim();// 联系人
//            String telephone = json.getString("telephone").trim();// 电话
//            String fax = json.getString("fax").trim();// 传真
//            String address = json.getString("address").trim();// 地址
//            String zipCode = json.getString("zipCode").trim();// 邮编
//            String email = json.getString("email").trim();// 邮件
//            String web = json.getString("web").trim();// 网址
            ManageInstitution institution = ManageInstitution.dao.findById(id);
            institution.setName(name);
            institution.setLongitude(longitude);
            institution.setLatitude(latitude);
            // mon.setMonitorScope(json.getInteger("scope"));
//            institution.setLeader(leader);
//            institution.setContacter(contacter);
//            institution.setTelephone(telephone);
//            institution.setFax(fax);
//            institution.setAddress(address);
//            institution.setZipCode(zipCode);
//            institution.setEmail(email);
//            institution.setWeb(web);
            institution.update();
            return "success";
        } catch (Exception e) {
            e.printStackTrace();
            return "false";
        }

    }

    // 回显单位

    public JSONObject echoInstitution(String id) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        // String sql=" select * from manage_institution where status = '0' and
        // id ='"+id+"' ";
        ManageInstitution institution = ManageInstitution.dao.findById(id);
        JSONObject obj = new JSONObject();
        if (institution != null) {
            String name = institution.getName();
            Double latitude = institution.getLatitude();
            Double longitude = institution.getLongitude();
//            String leader = institution.getLeader();
//            String contacter = institution.getContacter();
//            String telephone = institution.getTelephone();
//            String fax = institution.getFax();
//            String address = institution.getAddress();
//            String zipCode = institution.getZipCode();
//            String email = institution.getEmail();
//            String web = institution.getWeb();
            Date createTime = institution.getCreateTime();
            String dateString = formatter.format(createTime);
            obj.put("name", name);
//            obj.put("leader", leader);
//            obj.put("contacter", contacter);
//            obj.put("telephone", telephone);
//            obj.put("fax", fax);
//            obj.put("address", address);
//            obj.put("zipCode", zipCode);
//            obj.put("email", email);
//            obj.put("web", web);
            obj.put("createTime", dateString);
            obj.put("latitude", latitude);
            obj.put("longitude", longitude);
        }

        return obj;
    }

    // 删除机构

    public String deleteInstitution(String id, ManageUserInfo user) {
        Integer level = suchLevel(user);
        if (level != 1) {
            return "false,无权限";
        }
        ManageInstitution institution = ManageInstitution.dao.findById(id);
        String sql = "select * from manage_depart where inst_id= ? and status=0 ";
        List<ManageDepart> list = ManageDepart.dao.find(sql, id);
        if (list != null && list.size() > 0) {// 单位下有部门不能删除
            return "false,该机构有包含的部门";
        } else {
            // 判断该单位下是否有角色--没有角色才个删除单位
            String roleSql = "select * from manage_role where institution_id = ? ";
            List<Record> list3 = Db.find(roleSql, id);
            if (list3 != null && list3.size() > 0) {// 单位下有角色不能删除
                return "false,该机构包含有角色";
            } else {
                if (institution != null) {
                    institution.setStatus(-1);
                    institution.update();
                    return "success";
                }
            }
        }
        return "false";
    }

    // 显示部门信息

    public Object[] loadDepartment(ManageUserInfo user, MonitorConfigSearchVo search, String name) {
        Object[] res = new Object[2];
        Integer id = user.getUserId();
        String sqlUser = "select manage_depart.inst_id from manage_user_info "
                + " left join manage_depart on manage_user_info.dep_id=manage_depart.dep_id "
                + " left join manage_institution on manage_depart.inst_id=manage_institution.id "
                + " where manage_user_info.user_id= '" + id + "'";
        ManageUserInfo userInfos = ManageUserInfo.dao.findFirst(sqlUser);
        Integer inst_id = userInfos.getInt("inst_id");
        String sql = "select rid from manage_role_user_rel where uid= ?";
        ManageRoleUserRel manageRoleUserRel = ManageRoleUserRel.dao.findFirst(sql, id);
        if (manageRoleUserRel != null) {// 根据用户id查询出角色权限
            Integer rid = manageRoleUserRel.getRid();
            ManageRole manageRole = ManageRole.dao.findById(rid);
            Integer type = manageRole.getLevel();
            if (type.equals(1)) {// 超级管理员权限查看所有的部门数据
                String sql2 = "select dep.create_time createTime,dep.dep_id depId,dep.dep_name as depName,dep.inst_id instId,ins.name as insName,media_id mediaId from manage_depart dep left join manage_institution ins on dep.inst_id = ins.id where dep.status=0 ";
                String countSql = "select dep.create_time createTime,dep.dep_id depId,dep.dep_name as depName,dep.inst_id instId,ins.name as insName,media_id mediaId from manage_depart dep left join manage_institution ins on dep.inst_id = ins.id where dep.status=0 ";
                if (null != name && !"".equals(name.trim())) {
                    sql2 += "	and dep.dep_name like '%" + name + "%' ";
                    countSql += "	and dep.dep_name like '%" + name + "%' ";
                }
                sql2 += " order by dep.create_time desc limit " + (search.getPageNo() - 1) * search.getPageSize() + ","
                        + search.getPageSize();
                countSql += " order by dep.create_time desc ";
                List<ManageDepart> list = ManageDepart.dao.find(sql2);
                List<ManageDepart> countList = ManageDepart.dao.find(countSql);
                // 封装VO
                List<ManageUserVo> voList = new ArrayList<ManageUserVo>();
                for (ManageDepart manageDepartment : list) {
                    ManageUserVo vo = new ManageUserVo();
                    vo.setId(manageDepartment.get("depId"));// 部门id
                    vo.setDepId(manageDepartment.get("depId"));// 部门id
                    vo.setDep(HtmlUtils.htmlUnescape(manageDepartment.get("depName"))); // 部门名称
                    vo.setInsId(manageDepartment.get("instId")); // 所属单位id
                    vo.setIns(manageDepartment.get("insName")); // 所属单位名称
                    try {
                        Record media = queryMediaByMediaId(manageDepartment.get("mediaId"),
                                manageDepartment.get("instId"));
                        vo.setMediaName(media == null ? "" : media.get("mediaName"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (manageDepartment.get("createTime") != null && !"".equals(manageDepartment.get("createTime"))) {
                        String createTime = manageDepartment.get("createTime").toString();
                        vo.setCreatTime(createTime.substring(0, createTime.length() - 2));// 部门创建时间
                    } else {
                        vo.setCreatTime("");// 部门创建时间
                    }
                    voList.add(vo);
                }
                res[0] = voList;
                res[1] = countList.size();
            } else if (type.equals(2)) {// 管理员查看本单位所有的部门数据
                ManageUserInfo manageUserInfo = ManageUserInfo.dao.findById(id);
                if (inst_id != null) {
                    Integer instId = inst_id;
                    String sql2 = "select dep.create_time createTime,dep.dep_id depId,dep.dep_name as depName,dep.inst_id instId,ins.name as insName,media_id mediaId  from manage_depart dep left join manage_institution ins on dep.inst_id = ins.id where dep.inst_id = "
                            + instId + " and dep.status=0 ";
                    String countSql = "select dep.create_time createTime,dep.dep_id depId,dep.dep_name as depName,dep.inst_id instId,ins.name as insName,media_id mediaId  from manage_depart dep left join manage_institution ins on dep.inst_id = ins.id where dep.inst_id = "
                            + instId + " and dep.status=0 ";
                    if (null != name && !"".equals(name.trim())) {
                        sql2 += "	and dep.name like '%" + name + "%' ";
                        countSql += "	and dep.name like '%" + name + "%' ";
                    }
                    sql2 += " order by dep.create_time desc limit " + (search.getPageNo() - 1) * search.getPageSize()
                            + "," + search.getPageSize();
                    countSql += " order by dep.create_time desc ";
                    List<ManageDepart> list = ManageDepart.dao.find(sql2);
                    List<ManageDepart> countList = ManageDepart.dao.find(countSql);
                    // 封装VO
                    List<ManageUserVo> voList = new ArrayList<ManageUserVo>();
                    for (ManageDepart manageDepartment : list) {
                        ManageUserVo vo = new ManageUserVo();
                        vo.setId(Integer.parseInt(manageDepartment.get("depId").toString()));// 部门id
                        vo.setDepId(Integer.parseInt(manageDepartment.get("depId").toString()));// 部门id
                        vo.setDep(HtmlUtils.htmlUnescape(manageDepartment.get("depName"))); // 部门名称
                        vo.setInsId(Integer.parseInt(manageDepartment.get("instId").toString())); // 所属单位id
                        vo.setIns(manageDepartment.get("insName").toString()); // 所属单位名称
                        try {
                            Record media = queryMediaByMediaId(manageDepartment.get("mediaId"),
                                    manageDepartment.get("instId"));
                            vo.setMediaName(media == null ? "" : media.get("mediaName"));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (manageDepartment.get("createTime") != null
                                && !"".equals(manageDepartment.get("createTime"))) {
                            String createTime = manageDepartment.get("createTime").toString();
                            vo.setCreatTime(createTime.substring(0, createTime.length() - 2));// 部门创建时间
                        } else {
                            vo.setCreatTime("");// 部门创建时间
                        }
                        voList.add(vo);
                    }
                    res[0] = voList;
                    res[1] = countList.size();
                }
            }
        }
        return res;
    }


    //根据权限查询对应的用户列表
    public Object[] loadUser(ManageUserInfo user, MonitorConfigSearchVo search, String name, String instId, String depId) {
        Object[] res = new Object[2];
        if (user != null) {// 根据用户id查询出角色权限
            Integer type = suchLevel(user);
            if (type.equals(1)) {// 超级管理员权限查看所有的部门数据
                String sql = "SELECT u.`user_id` id ,u.`user_name` userName , u.`nick_name` realName ,r.`name` roleName ," +
                        "r.`id` roleId,d.`dep_id` depId , d.`dep_name` dep,m.`media_name` mediaName,i.`id` insId ,i.`name` ins,u.`create_time`" +
                        "FROM manage_user_info u" +
                        " LEFT JOIN manage_role_user_rel ur ON ur.`uid` = u.`user_id` " +
                        " LEFT JOIN manage_role r ON r.`id` = ur.`rid` " +
                        " LEFT JOIN manage_depart d ON d.`dep_id` = u.`dep_id` " +
                        " LEFT JOIN manage_institution i ON i.`id` = d.`inst_id` " +
                        " LEFT JOIN manage_media m ON m.`inst_id` =d.`media_id` " +
                        "WHERE u.`status`=1 ";
                String totoalSql = "SELECT COUNT(*) t " +
                        "FROM manage_user_info u" +
                        " LEFT JOIN manage_role_user_rel ur ON ur.`uid` = u.`user_id` " +
                        " LEFT JOIN manage_role r ON r.`id` = ur.`rid` " +
                        " LEFT JOIN manage_depart d ON d.`dep_id` = u.`dep_id` " +
                        " LEFT JOIN manage_institution i ON i.`id` = d.`inst_id` " +
                        " LEFT JOIN manage_media m ON m.`inst_id` =d.`media_id` " +
                        "WHERE u.`status`=1 ";
                if (name != null && !"".equals(name)) {
                    if (!"".equals(name.trim())) {
                        sql += " and u.user_name like '%" + name + "%' ";
                        totoalSql += " and u.user_name like '%" + name + "%' ";
                    }
                }

                if (!"".equals(depId)) {
                    sql += " AND u.`dep_id`=" + depId;
                    totoalSql += " AND u.`dep_id`=" + depId;
                }

                if (!"".equals(instId)) {
                    sql += " AND i.`id`=" + instId;
                    totoalSql += " AND i.`id`=" + instId;
                }

                sql += " order by u.create_time desc limit " + (search.getPageNo() - 1) * search.getPageSize() + ","
                        + search.getPageSize();
                List<Record> records = Db.find(sql);
                Record first = Db.findFirst(totoalSql);
                JSONObject jsonObject = JSONObject.parseObject(first.toString());
                Integer total = jsonObject.getInteger("t");
                res[0] = records;
                res[1] = total;
            } else if (type.equals(2)) {// 单位管理员--查看本机构下的用户
                String sql = "SELECT u.`user_id` id ,u.`user_name` userName , u.`nick_name` realName ,r.`name` roleName ," +
                        "r.`id` roleId,d.`dep_id` depId , d.`dep_name` dep,m.`media_name` mediaName,i.`id`  insId,i.`name` ins  ,u.`create_time`" +
                        "FROM manage_user_info u" +
                        " LEFT JOIN manage_role_user_rel ur ON ur.`uid` = u.`user_id` " +
                        " LEFT JOIN manage_role r ON r.`id` = ur.`rid` " +
                        " LEFT JOIN manage_depart d ON d.`dep_id` = u.`dep_id` " +
                        " LEFT JOIN manage_institution i ON i.`id` = d.`inst_id` " +
                        " LEFT JOIN manage_media m ON m.`inst_id` =d.`media_id` " +
                        "WHERE u.`status`=1 and i.`id`=? ";
                String totoalSql = "SELECT COUNT(*) t " +
                        "FROM manage_user_info u" +
                        " LEFT JOIN manage_role_user_rel ur ON ur.`uid` = u.`user_id` " +
                        " LEFT JOIN manage_role r ON r.`id` = ur.`rid` " +
                        " LEFT JOIN manage_depart d ON d.`dep_id` = u.`dep_id` " +
                        " LEFT JOIN manage_institution i ON i.`id` = d.`inst_id` " +
                        " LEFT JOIN manage_media m ON m.`inst_id` =d.`media_id` " +
                        "WHERE u.`status`=1 and i.`id`=? ";
                ManageInstitution institution = findInstitutionByUserId(user.getUserId());
                Long insId = institution.getId();
                if (name != null && !"".equals(name)) {
                    if (!"".equals(name.trim())) {
                        sql += " and u.user_name like '%" + name + "%' ";
                        totoalSql += " and u.user_name like '%" + name + "%' ";
                    }
                }

                if (!"".equals(depId)) {
                    sql += " AND u.`dep_id`=" + depId;
                    totoalSql += " AND u.`dep_id`=" + depId;
                }

                if (!"".equals(instId)) {
                    sql += " AND i.`id`=" + instId;
                    totoalSql += " AND i.`id`=" + instId;
                }

                sql += " order by u.create_time desc limit " + (search.getPageNo() - 1) * search.getPageSize() + ","
                        + search.getPageSize();
                List<Record> records = Db.find(sql, insId);
                Record first = Db.findFirst(totoalSql, insId);
                JSONObject jsonObject = JSONObject.parseObject(first.toString());
                Integer total = jsonObject.getInteger("t");
                res[0] = records;
                res[1] = total;
            } else if (type.equals(3)) {// 部门管理员
                String sql = "SELECT u.`user_id` id ,u.`user_name` userName , u.`nick_name` realName ,r.`name` roleName ," +
                        "r.`id` roleId,d.`dep_id` depId , d.`dep_name` dep,m.`media_name` mediaName,i.`id` insId,i.`name` ins,u.`create_time`" +
                        "FROM manage_user_info u" +
                        " LEFT JOIN manage_role_user_rel ur ON ur.`uid` = u.`user_id` " +
                        " LEFT JOIN manage_role r ON r.`id` = ur.`rid` " +
                        " LEFT JOIN manage_depart d ON d.`dep_id` = u.`dep_id` " +
                        " LEFT JOIN manage_institution i ON i.`id` = d.`inst_id` " +
                        " LEFT JOIN manage_media m ON m.`inst_id` =d.`media_id` " +
                        "WHERE u.`status`=1 AND d.`dep_id`=?";

                String totoalSql = "SELECT COUNT(*) t " +
                        "FROM manage_user_info u" +
                        " LEFT JOIN manage_role_user_rel ur ON ur.`uid` = u.`user_id` " +
                        " LEFT JOIN manage_role r ON r.`id` = ur.`rid` " +
                        " LEFT JOIN manage_depart d ON d.`dep_id` = u.`dep_id` " +
                        " LEFT JOIN manage_institution i ON i.`id` = d.`inst_id` " +
                        " LEFT JOIN manage_media m ON m.`inst_id` =d.`media_id` " +
                        "WHERE u.`status`=1 AND d.`dep_id`=?";


                if (name != null && !"".equals(name)) {
                    if (!"".equals(name.trim())) {
                        sql += " and u.user_name like '%" + name + "%' ";
                        totoalSql += " and u.user_name like '%" + name + "%' ";
                    }
                }

                if (!"".equals(depId)) {
                    sql += " AND u.`dep_id`=" + depId;
                    totoalSql += " AND u.`dep_id`=" + depId;
                }

                sql += " order by u.create_time desc limit " + (search.getPageNo() - 1) * search.getPageSize() + ","
                        + search.getPageSize();
                List<Record> records = Db.find(sql, user.getDepId());
                Record first = Db.findFirst(totoalSql, user.getDepId());
                JSONObject jsonObject = JSONObject.parseObject(first.toString());
                Integer total = jsonObject.getInteger("t");
                res[0] = records;
                res[1] = total;
            }
        }
        return res;
    }

    public String addUser(String param, ManageUserInfo user, String headPath, String basePath) {
        boolean finalStatus = true;
        JSONObject json = JSONObject.parseObject(param);
        Integer level = suchLevel(user);
        try {
            //判断是否为权限内的操作
            if (level == 4) {
                return "false";
            } else if (level == 2) {
                ManageInstitution institutionByUserId = findInstitutionByUserId(user.getUserId());
                if (institutionByUserId.getId() != json.getInteger("insId").longValue()) {
                    return "false";
                }
            } else if (level == 3) {
                ManageInstitution institutionByUserId = findInstitutionByUserId(user.getUserId());
                if (institutionByUserId.getId() != json.getInteger("insId").longValue()) {
                    return "false";
                } else if (json.getInteger("dep_id") != user.getDepId()) {
                    return "false";
                }
            } else if (level != 1 && level != 2 && level != 3) {
                return "false";
            }

            //判断不可为空的内容是否存在
            String username = json.getString("username"); // 用户名
            if (username != null) {
                username = username.trim();
            } else {
                return "false";
            }
            final String name = username;
            String roleId = json.getString("roleId").trim();// 角色id
            if (roleId != null) {
                roleId = roleId.trim();
            } else {
                roleId = "281";
//                return "false";
            }
            final String rId = roleId;
            String sql = "select * from manage_user_info where status = 1 and user_name = '" + username + "' ";
            List<ManageUserInfo> list = ManageUserInfo.dao.find(sql);// 根据名称查重
            if (list != null && list.size() > 0) {
                return "exist";
            }

            //无重名用户 且关键参数存在  事务添加用户
            else {
                // 事物回滚--只有全部成功才可以 有任意一个失败 则全部插入的数据全部回滚
                finalStatus = Db.tx(new IAtom() {

                    public boolean run() throws SQLException {
                        boolean result = true;
                        ManageUserInfo userInfo = new ManageUserInfo();
                        // 构建头像
                        String timename = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
                        String headAddress = "";
                        String nickName = json.getString("personName");
                        try {
                            boolean generateImg = CreateNamePicture.generateImg(nickName, headPath, timename);
                            if (generateImg) {
                                headAddress = basePath + timename + ".jpg";
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        userInfo.setIcon(headAddress);
                        userInfo.setUserName(name);
                        userInfo.setPassword(MD5Util.string2MD5("123456"));// 加密后再存入密码
                        userInfo.setNickName(nickName);
                        userInfo.setEmail(json.getString("email"));
                        userInfo.setTel(json.getString("tel"));
                        userInfo.setDepId(json.getInteger("dep_id"));
                        userInfo.setCreateTime(new Date());
//						userInfo.setMediaId(json.getInteger("mediaId"));
                        result = userInfo.save();

                        if (result == true) {
                            // 存入角色用户关联表信息
                            Integer userid = userInfo.get("user_id"); // 获取刚添加的用户id
                            ManageRoleUserRel roleUserRel = new ManageRoleUserRel();
                            roleUserRel.setRid(Integer.parseInt(rId));
                            roleUserRel.setUid(userid);
                            result = roleUserRel.save();
                        }
                        return result;
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "fail";
        }
        if (finalStatus == true) {
            return "ok";
        } else {
            return "fail";
        }
    }

    /**
     * @param id
     * @return ManageUserInfo
     * @Title findUserById
     * @Description
     * @date 2019年3月6日-下午5:22:46
     * @author Yipeng.zou
     */

    public ManageUserInfo findUserById(Integer id) {
        ManageUserInfo manageUserInfo = ManageUserInfo.dao.findFirst(" select * from manage_user_info where id=? ", id);
        return manageUserInfo;
    }

    public JSONObject echoUser(String id) {
        String sqlUser = "select manage_depart.inst_id from manage_user_info "
                + " left join manage_depart on manage_user_info.dep_id=manage_depart.dep_id "
                + " left join manage_institution on manage_depart.inst_id=manage_institution.id "
                + " where manage_user_info.user_id= '" + id + "'";
        ManageUserInfo userInfos = ManageUserInfo.dao.findFirst(sqlUser);
        Integer inst_id = userInfos.getInt("inst_id");
        String sqlRole = "select rid from manage_role_user_rel where uid= '" + id + "' and status=1";
        ManageRoleUserRel roleUserRel = ManageRoleUserRel.dao.findFirst(sqlRole);
        Integer role_id = null;
        if (roleUserRel != null) {
            role_id = roleUserRel.getRid();
        }
        ManageUserInfo userinfo = ManageUserInfo.dao.findById(id);
        JSONObject obj = new JSONObject();
        if (userinfo != null) {
            String username = userinfo.getUserName();
            String contacter = userinfo.getNickName();
            String email = userinfo.getEmail();
            String tel = userinfo.getTel();
            Integer depId = userinfo.getDepId();
            obj.put("username", HtmlUtils.htmlUnescape(username));
            obj.put("rid", role_id);
            obj.put("contacter", HtmlUtils.htmlUnescape(contacter));
            obj.put("tel", HtmlUtils.htmlUnescape(tel));
            obj.put("email", HtmlUtils.htmlUnescape(email));
            obj.put("depId", depId);
            obj.put("instId", inst_id);
        }
        return obj;
    }

    public String editUser(String param, ManageUserInfo user) {
        boolean finalStatus = true;
        try {
            JSONObject json = JSONObject.parseObject(param);

            final Integer userid = json.getInteger("id");// 用户Id
            ManageUserInfo manageUserInfo = new ManageUserInfo();
            manageUserInfo.setUserId(userid);
            Integer level = suchLevel(manageUserInfo);
            Integer userLevel = suchLevel(user);
            //同等级不允许修改  level为1时可以
            if (userLevel >= level && userLevel != 1) {
                return "false";
            }
            final Integer depId = json.getInteger("dep_id");// 部门Id
            final Integer roleId = json.getInteger("roleId");// 部门Id
            final String username = json.getString("username").trim();// 账号
            final String personName = json.getString("personName").trim();
            final String email = json.getString("email").trim();
            final String tel = json.getString("tel").trim();
            final Integer mediaId = json.getInteger("mediaId");
            String sqlUser = "select manage_depart.inst_id from manage_user_info "
                    + " left join manage_depart on manage_user_info.dep_id=manage_depart.dep_id "
                    + " left join manage_institution on manage_depart.inst_id=manage_institution.id "
                    + " where manage_user_info.user_id= '" + userid + "'";
            ManageUserInfo userInfos = ManageUserInfo.dao.findFirst(sqlUser);
            Integer inst_id = userInfos.getInt("inst_id");
            finalStatus = Db.tx(new IAtom() {
                public boolean run() throws SQLException {
                    boolean result = true;
                    // 更换用户信息
                    ManageUserInfo userinfo = ManageUserInfo.dao.findById(userid);
                    ManageUserInfo infos = new ManageUserInfo();
                    infos.setUserId(userid);
                    infos.setUserName(username);
                    infos.setNickName(personName);
                    infos.setEmail(email);
                    infos.setTel(tel);
                    infos.setDepId(depId);
                    result = infos.update();
                    if (inst_id != null) {
                        ManageDepart depart = new ManageDepart();
                        depart.setDepId(depId);
                        depart.setInstId(inst_id);
                        depart.setMediaId(mediaId);
                        result = depart.update();
                    }
                    if (userid != null) {
//						ManageRoleUserRel roleUserRel=new ManageRoleUserRel();
                        String sql = "update manage_role_user_rel set rid = '" + roleId + "' where uid = '" + userid + "'";
                        int a = Db.use(DbKit.getConfig(ManageRoleUserRel.class).getName()).update(sql);

                    }

                    //更改用户level后，需要把map中放的level清空
                    LevelUtil.removeLevel(userid);

                    return result;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            return "false";
        }
        if (finalStatus == true) {
            return "success";
        } else {
            return "false";
        }
    }

    public String deleteUser(String id, ManageUserInfo user) {
        ManageUserInfo userInfo = new ManageUserInfo();
        userInfo.setUserId(Integer.parseInt(id));
        Integer level = suchLevel(userInfo);
        //不能删除等级高于账号等级的用户
        if (suchLevel(user) >= level&&suchLevel(user)!=1) {
            return "无权删除该用户";
        }

        if (id != null && !"".equals(id)) {
            final String uid = id;
            boolean result = true;
            result = Db.tx(new IAtom() {

                public boolean run() throws SQLException {
                    boolean res = true;
                    ManageUserInfo manageUserInfo = ManageUserInfo.dao.findById(uid);
                    manageUserInfo.setStatus(-1);
                    res = manageUserInfo.update();
                    // 删除角色关联
                    String rolesql = "select * from manage_role_user_rel where status=0 and uid=?";
                    ManageRoleUserRel roleUserRel = ManageRoleUserRel.dao.findFirst(rolesql, uid);
                    if (roleUserRel != null) {
                        roleUserRel.setStatus(-1);
                        res = roleUserRel.update();
                    }
                    return res;
                }
            });
            if (result == true) {
                return "删除成功";
            } else {
                return "删除失败";
            }
        } else {
            return "删除失败";
        }
    }

    //查询机构，只有level为1可以查看
    public JSONArray queryInstitution(ManageUserInfo user) {
        JSONArray array = new JSONArray();
        Integer type = suchLevel(user);
        if (type.equals(1)) {
            String sql = "select * from manage_institution where status=0  order by create_time desc ";
            List<ManageInstitution> list = ManageInstitution.dao.find(sql);
            if (null != list && list.size() > 0) {
                for (int i = 0; i < list.size(); i++) {
                    JSONObject obj = new JSONObject();
                    ManageInstitution institution = list.get(i);
                    obj.put("id", institution.getId());
                    obj.put("name", institution.getName());
                    obj.put("latitude", institution.getLatitude());
                    obj.put("longitude", institution.getLongitude());
                    obj.put("date", institution.getCreateTime());
                    obj.put("caibian_host", institution.getCaibianHost());
                    array.add(obj);
                }
            }
            return array;
        }
        if (type.equals(2)) {
            String sql = "SELECT * FROM manage_institution i LEFT JOIN manage_depart d ON  i.`id`=d.`inst_id` WHERE i.status = 0 AND d.`dep_id` =" + user.getDepId();
            List<ManageInstitution> list = ManageInstitution.dao.find(sql);
            if (null != list && list.size() > 0) {
                for (int i = 0; i < list.size(); i++) {
                    JSONObject obj = new JSONObject();
                    ManageInstitution institution = list.get(i);
                    obj.put("id", institution.getId());
                    obj.put("name", institution.getName());
                    obj.put("latitude", institution.getLatitude());
                    obj.put("longitude", institution.getLongitude());
                    obj.put("date", institution.getCreateTime());
                    obj.put("caibian_host", institution.getCaibianHost());
                    array.add(obj);
                }
            }
            return array;
        }
        array.add("false");
        return array;
    }

    public JSONArray queryDepartment(String mediaId, Integer insId) {
        JSONArray array = new JSONArray();
        String sql = "select dep_id deptId,dep_name deptName from manage_depart ";
        sql += " where status=0 ";
        if (StringUtils.isNotBlank(mediaId)) {
            sql += " and media_id =  '" + mediaId + "' ";
        }
        if (insId != null) {
            sql += " and inst_id =  '" + insId + "'";
        }
        sql += " order by create_time desc ";
        List<ManageDepart> list = ManageDepart.dao.find(sql);
        if (list != null && list.size() > 0) {
            for (ManageDepart manageDepart : list) {
                JSONObject jo = new JSONObject();
                jo.put("deptId", manageDepart.get("deptId"));
                jo.put("deptName", manageDepart.get("deptName"));
                array.add(jo);
            }
        }
        return array;
    }

    //查询角色列表
    public JSONObject queryRole(ManageUserInfo user, Integer page, Integer pageSize) {
        Integer level = suchLevel(user);
        JSONObject json = new JSONObject();
        //level为1  查询所有角色
        if (level == 1) {
            String sql = "select * from manage_role";
            String s = pageHelper(sql, page, pageSize);
            List<ManageRole> roles = ManageRole.dao.find(s);
            json.put("data", roles);
            String totalSql = "select count(*) t from manage_role";
            Record first = Db.findFirst(totalSql);
            String total = JSON.parseObject(first.toString()).getString("t");
            json.put("total", total);
            json.put("massage", "success");
            return json;
        }
        //level为2或3   查询大于level的角色列表（查询角色等级不大于当前角色的  角色列表）
        if (level == 2 || level == 3) {
            String sql = "select * from manage_role WHERE LEVEL >=" + level;
            String s = pageHelper(sql, page, pageSize);
            List<ManageRole> roles = ManageRole.dao.find(s);
            json.put("data", roles);
            String totalSql = "select count(*) t from manage_role";
            Record first = Db.findFirst(totalSql);
            String total = JSON.parseObject(first.toString()).getString("t");
            json.put("total", total);
            json.put("massage", "success");
            return json;
        }
        return null;
    }

    public JSONArray queryInstitutionUser(ManageUserInfo user, String role_id) {
        JSONArray array = new JSONArray();
        ManageRole role = ManageRole.dao.findById(role_id);
        Integer type = role.getLevel();
        if (type.equals(1)) {
            String sql = "select * from manage_institution where status=0  order by create_time desc ";
            List<ManageInstitution> list = ManageInstitution.dao.find(sql);
            if (null != list && list.size() > 0) {
                for (int i = 0; i < list.size(); i++) {
                    JSONObject obj = new JSONObject();
                    ManageInstitution siteNewsCategory = list.get(i);
                    obj.put("manageInstitution", siteNewsCategory);
                    array.add(obj);
                }
            }
        } else {
            String sql = "select * from manage_depart where status=0  and id= ?";
            // List<ManageDepartment> list = ManageDepartment.dao.find(sql);
            ManageDepart manageDepartment = ManageDepart.dao.findFirst(sql, user.getDepId());
            // if(null!=list && list.size()>0){
            JSONObject obj = new JSONObject();
            // Integer instId = list.get(0).getInstId();
            Integer instId = manageDepartment.getInstId();
            ManageInstitution institution = ManageInstitution.dao.findById(instId);
            obj.put("manageInstitution", institution);
            array.add(obj);
            // }
        }
        return array;
    }

    public Object getUkeyInfoList(Integer pageNum, Integer pageSize, String username, String ukey, Integer status) {
        Object[] rt = new Object[2];
        rt[0] = "";
        rt[1] = "";

        /*
         * String select = "SELECT uk.auto_id,uk.ukey_id,uk.status "; String others =
         * "FROM ukey_info uk ";
         *
         * List<Object> parList = new ArrayList<Object>(); String condition = ""; if
         * (StringUtils.isNotBlank(username)) { condition +=
         * " and mui.username like ? "; parList.add("%" + username + "%"); } if
         * (StringUtils.isNotBlank(ukey)) { condition += " and uk.ukey_id like ? ";
         * parList.add("%" + ukey + "%"); } if (status != null) { condition +=
         * " and uk.status = ? "; parList.add(status); } else { condition +=
         * " and uk.status >-1 "; } Page<UkeyInfo> finds = null; try { finds =
         * UkeyInfo.dao.paginate(pageNum, pageSize, select, others +
         * SmHelper.getWhere(condition) + " order by uk.create_time desc",
         * parList.toArray(new Object[parList.size()])); } catch (Exception e) {
         * e.printStackTrace(); } if (finds != null) { rt = new Object[2]; rt[0] =
         * finds.getList(); rt[1] = finds.getTotalRow(); }
         */
        return rt;
    }

    public String enableOrDisableUkey(Long id) {
        boolean flag = false;
        String sql = "select * from manage_ukey_user_rel where ukid=? and status=0";
        /*
         * List<ManageUkeyUserRel> find2 = ManageUkeyUserRel.dao.find(sql, new Object[]
         * { id }); if (find2 != null && find2.size() > 0) { return "exist"; } else {
         * UkeyInfo find = UkeyInfo.dao.findById(id); if (find != null) { try { Integer
         * status = find.getStatus(); if (status == 0) { find.setStatus(1); } else {
         * find.setStatus(0); } flag = find.update(); } catch (Exception e) {
         * e.printStackTrace(); } } if (flag) { return "ok"; } else { return "error"; }
         * }
         */
        return "";
    }

    public String deleteUkey(Long id) {
        boolean flag = false;
        /*
         * final UkeyInfo find = UkeyInfo.dao.findById(id); if (find != null) { try {
         * List<ManageUkeyUserRel> manageUkeyUserRels = ManageUkeyUserRel.dao
         * .find("select * from manage_ukey_user_rel where status != -1 and ukid=?",
         * id); if (manageUkeyUserRels != null && manageUkeyUserRels.size() > 0) {//
         * 未关联账号可以删除 return "exist"; } else { find.setStatus(-1); flag = find.update();
         * } } catch (Exception e) { e.printStackTrace(); return "error"; } } if (flag)
         * { return "ok"; } else { return "error"; }
         */
        return "";

    }

    public String addUkey(String ukey) {
        boolean flag = false;
        try {
            /*
             * UkeyInfo ukeyInfo = new UkeyInfo(); List<UkeyInfo> find = UkeyInfo.dao.
             * find("select * from ukey_info where status != -1 and ukey_id = ?", ukey); if
             * (find == null || find.size() < 1) { ukeyInfo.setUkeyId(ukey);
             * ukeyInfo.setCreateTime(new Date()); flag = ukeyInfo.save(); } else { return
             * "exist"; }
             */
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
        if (flag) {
            return "ok";
        } else {
            return "error";
        }

    }

    /**
     * @param username
     * @param password
     * @return
     * @description 登录查询用户信息
     * @author ZSJ
     * @date 2019年7月1日
     */

    public ManageUserInfo loginUserNew(String username) {
        String sql = "select * from " + TableMapping.me().getTable(ManageUserInfo.class).getName()
                + " where status=1 and user_name = ? ";
        return (ManageUserInfo) ManageUserInfo.dao.findFirst(sql, username);
    }

    public ManageUserInfo loginUser(String username, String password) {
        String pwd = MD5Util.string2MD5(password);
        String sql = "select * from manage_user_info where status = 0 and user_name = '" + username + "' and password = '" + pwd + "'";
        List<ManageUserInfo> list = ManageUserInfo.dao.find(sql);
        ManageUserInfo user = new ManageUserInfo();
        // 添加机构信息、所属行业信息
        if (list != null && list.size() > 0) {
            for (ManageUserInfo manageUserInfo : list) {
                if (MD5Util.string2MD5(password).equals(manageUserInfo.getPassword().trim())) {
                    user = manageUserInfo;
                    Integer user_id = user.getUserId();
                    String sqlUser = "select manage_depart.inst_id from manage_user_info "
                            + " left join manage_depart on manage_user_info.dep_id=manage_depart.dep_id "
                            + " left join manage_institution on manage_depart.inst_id=manage_institution.id "
                            + " where manage_user_info.user_id= '" + user_id + "'";
                    ManageUserInfo userInfos = ManageUserInfo.dao.findFirst(sqlUser);
                    Integer inst_id = userInfos.getInt("inst_id");
                    String sqlRole = "select rid from manage_role_user_rel where uid= '" + user_id + "' and status=1";
                    ManageRoleUserRel roleUserRel = ManageRoleUserRel.dao.findFirst(sqlRole);
                    Integer role_id = null;
                    if (roleUserRel != null) {
                        role_id = roleUserRel.getRid();
                    }
                    ManageRole role = ManageRole.dao.findById(role_id);
                    user.put("roleName", role.getName());
                    user.put("superView", role.getLevel());
                    user.put("instId", inst_id);
                    ManageInstitution ins = ManageInstitution.dao.findById(inst_id);
                    if (null != ins) {
                        user.put("dbindex", ins.getDbindex());
                    } else {
                        user.put("dbindex", "");
                    }
                }
            }
        }
        return user;
    }

    /**
     * 获取机构信息
     */
    public ManageInstitution getInstitution(ManageUserInfo user) {
        String sql = "";
        sql += "	SELECT manage_institution.* from manage_institution INNER JOIN manage_department ON manage_institution.id = manage_department.inst_id	";
        sql += "	INNER JOIN manage_user_info ON manage_user_info.dep_id = manage_department.id	";
        sql += "	WHERE manage_institution.`status` = 0	";
        sql += "	AND manage_user_info.id = ?	";
        ManageInstitution ins = ManageInstitution.dao.findFirst(sql, new Object[]{user.getUserId()});
        return ins;
    }

    /**
     * 获取用户机构id
     */

    public Integer getUserInstId(ManageUserInfo user) {
        if (null != user) {
            String sql = "";
            sql += "	SELECT manage_institution.* from manage_institution INNER JOIN manage_department ON manage_institution.id = manage_department.inst_id	";
            sql += "	INNER JOIN manage_user_info ON manage_user_info.dep_id = manage_department.id	";
            sql += "	WHERE manage_institution.`status` = 0	";
            sql += "	AND manage_user_info.id = ?	";
            try {
                ManageInstitution dao = ManageInstitution.dao.findFirst(sql, new Object[]{user.getUserId()});
                if (null != dao) {
                    return dao.getId().intValue();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return null;
    }

    /**
     * 获取用户权限列表
     */

    public List<ManagePerm> getUserPermList(ManageUserInfo user) {
        if (null != user) {
            List<ManagePerm> list = new ArrayList<ManagePerm>();
            List<ManageRoleUserRel> rel = ManageRoleUserRel.dao.find(" select * from manage_role_user_rel where uid=?",
                    new Object[]{user.getUserId()});
            if (CollectionUtils.isNotEmpty(rel)) {
                List<ManageRolePermRel> prel = ManageRolePermRel.dao.find(
                        " select * from manage_role_perm_rel where status = 0 and rid=?",
                        new Object[]{rel.get(0).getRid()});
                if (null != prel && prel.size() > 0) {
                    for (ManageRolePermRel r : prel) {
                        ManagePerm p = ManagePerm.dao.findById(r.getPid());
                        if (p != null) {
                            list.add(p);
                        }
                    }
                }
            }
            return list;
        }
        return null;
    }

    /**
     * 获取权限列表
     */

    /*
     * public List<MetadataMgr> getRoleMgrList(ManageUserInfo user) { if (null !=
     * user) { /* String sql = ""; sql+=
     * "	SELECT manage_perm.* from manage_perm INNER JOIN manage_role_perm_rel ON manage_perm.id = manage_role_perm_rel.pid	"
     * ; sql+=
     * "	INNER JOIN manage_role ON manage_role_perm_rel.rid = manage_role.id	" ;
     * sql+=
     * "	INNER JOIN manage_role_user_rel ON manage_role.id = manage_role_user_rel.rid	"
     * ; sql+=
     * "	INNER JOIN manage_user_info ON manage_role_user_rel.uid = manage_user_info.id	"
     * ; sql+="	WHERE manage_perm.`status`=0	"; sql+=
     * "	AND manage_user_info.id = ?	"; List<ManagePerm> list =
     * ManagePerm.dao.find(sql,new Object[]{user.getId()});
     *
     * List<MetadataMgr> list = new ArrayList<MetadataMgr>();
     * List<ManageRoleUserRel> rel =
     * ManageRoleUserRel.dao.find(" select * from manage_role_user_rel where uid=?",
     * new Object[] { user.getId() });if(null!=rel) { List<ManageRoleMgr> prel =
     * ManageRoleMgr.dao.find(" select * from manage_role_mgr where  rid=?", new
     * Object[] { rel.get(0).getRid() }); if (null != prel && prel.size() > 0) { for
     * (ManageRoleMgr r : prel) { MetadataMgr p =
     * MetadataMgr.dao.findById(r.getMid()); list.add(p); } } }return list; }return
     * null;}
     */
    public List<ManagePerm> getAllUserPermList() {
        List<ManagePerm> list = new ArrayList<ManagePerm>();
        list = ManagePerm.dao.find("select * from manage_perm");
        return list;
    }

    @Deprecated
    public ManageUserInfo getUserTransformInfo(Map<String, Object> userMap) {
        /*
         * Object id = userMap.get("id"); String dep_id = (String)
         * userMap.get("departmentNumber"); String depsql =
         * " select * from manage_department  where user_dep=? "; ManageDepart dep =
         * ManageDepart.dao.findFirst(depsql, new Object[] { dep_id }); if (dep == null)
         * { dep = new ManageDepart(); dep.setUserDep(dep_id); dep.setInstId(1);
         * dep.setName((String) userMap.get("department")); dep.save(); } String user_id
         * = id.toString(); String sql =
         * " select * from manage_user_info  where user_id=? "; ManageUserInfo user =
         * ManageUserInfo.dao.findFirst(sql, new Object[] { user_id }); if (null !=
         * user) {// 更新 或者直接获取操作 if (dep.getId() != user.getDepId()) { user.update(); }
         * } else {// 添加操作 user = new ManageUserInfo(); user.setUserId(user_id);
         * user.setUsername((String) userMap.get("uid")); user.setDepId(dep.getId());
         * user.setInstitutionId(1); user.setRoleId(82);// 82 超管 83 部门管理员 84 普通用户
         * user.save(); ManageRoleUserRel rel = new ManageRoleUserRel();
         * rel.setUid(user.getId()); rel.setRid(user.getRoleId()); rel.save(); }
         */
        return null;
    }

    public List<ManagePerm> getUserPrermLists(ManageUserInfo user) {
        List<ManagePerm> list = new ArrayList<>();
        String url = PropKit.get("getPermUrl");// "http://192.168.12.88:9889/mongoService/saveData";
        try {
            Connection conn = Jsoup.connect(url).ignoreContentType(true).timeout(600000);
            Document d = conn.data("permission", "AQZHPGXT").data("userId", user.getUserId().toString()).get();
            System.out.println(d.body().text());
            JSONObject result = JSONObject.parseObject(d.body().text());
            // JSONObject result = JSONObject.parseObject("{ 'code': 200,
            // 'permission': [ 'navi_al/mod1', 'monitor_al', 'monitor_al/mod3' ]
            // }");
            if (result.size() > 0 && result != null) {
                JSONArray ja = result.getJSONArray("permission");
                String per = "";
                if (null != ja && ja.size() > 0) {
                    for (int i = 0; i < ja.size(); i++) {
                        per += "'" + ja.getString(i) + "',";
                    }
                    per = per.substring(0, per.length() - 1);
                    String sql = "select *  from manage_perm where url in( " + per + ")";
                    list = ManagePerm.dao.find(sql);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            String sql = "select *  from manage_perm where id>=25 ";
            list = ManagePerm.dao.find(sql);
        }
        if (list.size() == 0) {
            String sql = "select *  from manage_perm where id>=25 ";
            list = ManagePerm.dao.find(sql);
        }
        return list;
    }

    public List<Integer> getTid(Integer insId) {
        List<Integer> list = new ArrayList<Integer>();
        String sql = "select tid from manage_trade_institution_rel where inst_id=? and status=0";
        return list;
    }

    public Object[] getIndustryInfo(Integer insId) {
        Object[] res = new Object[2];
        List<Integer> list = getTid(insId);
        List<Integer> sourceList = new ArrayList<>();
        if (null != list && list.size() > 0) {
            String sql = "select * from manage_trade_data_rule where tid in (" + Helper.list2str(list, ",")
                    + ") group by data_type ";
            res[0] = list;
            res[1] = sourceList;
        }
        return res;
    }

    public ManageUserInfo getUserById(Integer userId) {// SSO 获取用户信息
        String sqlUser = "select manage_depart.inst_id from manage_user_info "
                + " left join manage_depart on manage_user_info.dep_id=manage_depart.dep_id "
                + " left join manage_institution on manage_depart.inst_id=manage_institution.id "
                + " where manage_user_info.user_id= '" + userId + "'";
        ManageUserInfo userInfos = ManageUserInfo.dao.findFirst(sqlUser);
        Integer inst_id = userInfos.getInt("inst_id");
        String sqlRole = "select rid from manage_role_user_rel where uid= '" + userId + "' and status=1";
        ManageRoleUserRel roleUserRel = ManageRoleUserRel.dao.findFirst(sqlRole);
        Integer role_id = null;
        if (roleUserRel != null) {
            role_id = roleUserRel.getRid();
        }
        ManageUserInfo user = ManageUserInfo.dao.findById(userId);
        // 添加机构信息、所属行业信息
        if (null != user) {
            ManageRole role = ManageRole.dao.findById(role_id);
            user.put("roleName", role.getName());
            user.put("superView", role.getLevel());
            user.put("instId", inst_id);
            user.put("level", role.getLevel());
            ManageInstitution ins = ManageInstitution.dao.findById(inst_id);
            if (null != ins) {
                user.put("dbindex", ins.getDbindex());
            } else {
                user.put("dbindex", "");
            }
        }
        return user;
    }

    public ManageUserInfo loginUser(String username, String password, String industryId) {

        ManageUserInfo user = new ManageUserInfo();
        List<ManageUserInfo> list = new ArrayList<ManageUserInfo>();
        if (industryId != null) {
            String pwd = MD5Util.string2MD5(password);
            String sql = "select * from manage_user_info us,manage_trade_institution_rel inrel where 1=1 and us.status=1 and inrel.status=0 ";
            sql += " and inrel.inst_id=us.institution_id ";
            sql += " and inrel.tid in(" + industryId + ") ";
            sql += " and us.user_name=? and password = '" + pwd + "' ";
            list = ManageUserInfo.dao.find(sql, username);

        } else {
            String pwd = MD5Util.string2MD5(password);
            String sql = "select * from manage_user_info where status = 0 and user_name = '" + username + "' and password = '" + pwd + "'";
            list = ManageUserInfo.dao.find(sql);
        }
        // ManageUserInfo user = new ManageUserInfo();
        // 添加机构信息、所属行业信息
        if (list != null && list.size() > 0) {
            for (ManageUserInfo manageUserInfo : list) {
                if (MD5Util.string2MD5(password).equals(manageUserInfo.getPassword().trim())) {
                    user = manageUserInfo;
                    Integer userId = user.getUserId();
                    String sqlUser = "select manage_depart.inst_id from manage_user_info "
                            + " left join manage_depart on manage_user_info.dep_id=manage_depart.dep_id "
                            + " left join manage_institution on manage_depart.inst_id=manage_institution.id "
                            + " where manage_user_info.user_id= '" + userId + "'";
                    ManageUserInfo userInfos = ManageUserInfo.dao.findFirst(sqlUser);
                    Integer inst_id = userInfos.getInt("inst_id");
                    String sqlRole = "select rid from manage_role_user_rel where uid= '" + userId + "' and status=1";
                    ManageRoleUserRel roleUserRel = ManageRoleUserRel.dao.findFirst(sqlRole);
                    Integer role_id = null;
                    if (roleUserRel != null) {
                        role_id = roleUserRel.getRid();
                    }
                    ManageRole role = ManageRole.dao.findById(role_id);
                    user.put("roleName", role.getName());
                    user.put("superView", role.getLevel());
                    user.put("instId", inst_id);
                    ManageInstitution ins = ManageInstitution.dao.findById(inst_id);
                    if (null != ins) {
                        user.put("dbindex", ins.getDbindex());
                    } else {
                        user.put("dbindex", "");
                    }
                }
            }
        }
        return user;

    }

    /**
     * 获取角色列表
     *
     * @param name            角色名称
     * @param type            角色类型
     * @param superView       查询用户级别
     * @param authorityDepIds 查询用户具备哪些部门的访问权限
     * @return
     */
    public Object[] getSysRoleList(ManageUserInfo userInfo, Integer pageNo,
                                   Integer pageSize) {
        Integer userId = userInfo.getUserId();
        Object[] obj = new Object[2];
        String level = ""; // 用户级别
        String levelSql = "select * from manage_role r left join manage_role_user_rel ru on r.id = ru.rid where ru.uid = "
                + userInfo.getUserId();
        List<ManageRole> find = ManageRole.dao.find(levelSql);
        if (find != null && find.size() > 0) {
            ManageRole role = find.get(0);
            if (role.getLevel() != null) {
                level = role.getLevel().toString();
                String sql = "select r.create_time createTime,r.id roleId,r.name as roleName,r.level level,d.dep_name as depName,d.dep_id as depId,i.name as insName,i.id as insId ,r.`sys_type` sysType from manage_role r  left join manage_depart d on r.dep_id = d.dep_id left join manage_institution i on r.institution_id = i.id where r.level >= "
                        + level;
                String countSql = "select r.create_time createTime,r.id roleId,r.name as roleName,r.level level,d.dep_name as depName,d.dep_id as depId,i.name as insName,i.id as insId ,r.`sys_type` sysType from manage_role r  left join manage_depart d on r.dep_id = d.dep_id left join manage_institution i on r.institution_id = i.id where r.level >= "
                        + level;


                if ("1".equals(level)) { // 超级管理员--不做限制
                    sql += "";
                    countSql += "";

                    sql += " order by r.create_time desc limit " + (pageNo - 1) * pageSize + "," + pageSize + "";
                    List<ManageRole> countList = ManageRole.dao.find(countSql);
                    List<ManageRole> list = ManageRole.dao.find(sql);
                    List<UserRoleVo> listVo = new ArrayList<UserRoleVo>();
                    for (ManageRole manageRole : list) {
                        UserRoleVo vo = new UserRoleVo();
                        vo.setRoleId(manageRole.get("roleId"));
                        vo.setLevel(manageRole.get("level").toString());
                        vo.setName(HtmlUtils.htmlUnescape(manageRole.get("roleName")));
                        Integer sysType = manageRole.get("sysType");
                        vo.setSysType(sysType);
                        if (manageRole.get("createTime") != null && !"".equals(manageRole.get("createTime"))) {// 角色创建时间
                            String createTime = manageRole.get("createTime").toString();
                            vo.setCreateTime(createTime.substring(0, createTime.length() - 2));
                        } else {
                            vo.setCreateTime("");// 用户创建时间
                        }
                        if (manageRole.get("depName") != null) {
                            vo.setDep(HtmlUtils.htmlUnescape(manageRole.get("depName")));
                            vo.setDepId(manageRole.get("depId").toString());
                        } else {
                            vo.setDep("");
                            vo.setDepId("");
                        }
                        if (manageRole.get("insName") != null) {
                            vo.setIns(manageRole.get("insName").toString());
                            vo.setInsId(manageRole.get("insId").toString());
                        } else {
                            vo.setIns("");
                            vo.setInsId("");
                        }
                        listVo.add(vo);
                    }
                    obj[0] = listVo;
                    obj[1] = countList.size();
                    return obj;
                }
            }
        }
        return null;
    }


    //根据角色id 获取权限表
    public List<ManagePerm> getSysPermByRoleId(Integer rid, ManageUserInfo user) {
        String sql = "SELECT * FROM manage_perm p " +
                "LEFT JOIN manage_role_perm_rel rp ON p.id=rp.`pid`" +
                "LEFT JOIN manage_role r  ON r.`id`=rp.`rid`" +
                "WHERE p.`status`= 0  AND r.`id`=?";
        List<ManagePerm> permList = ManagePerm.dao.find(sql, rid);
        return permList;
    }

    public List<Record> getSysRolePerm(Integer id) {
        if (null != id) {
            String sql = "";
            sql += "	SELECT manage_role_perm_rel.pid FROM manage_role_perm_rel	";
            sql += "	WHERE manage_role_perm_rel.status = 0 and manage_role_perm_rel.rid = " + id;
            String name = DbKit.getConfig(ManagePerm.class).getName();
            List<Record> pidlist = Db.use(name).find(sql);
            return pidlist;
        }
        return null;
    }

    //修改角色信息
    public boolean updateSysRolePerm(Integer id, String[] permArr, String roleName, Integer sysType, Integer level, ManageUserInfo user) {
        String sql = "";
        Integer userLevel = suchLevel(user);
        if (null != id && userLevel == 1) {
            // 修改角色的权限配置
            sql = "DELETE FROM manage_role_perm_rel WHERE manage_role_perm_rel.rid = " + id;
            String name = DbKit.getConfig(ManagePerm.class).getName();
            Db.use(name).update(sql);
            if (null != permArr && permArr.length > 0) {
                for (String permid : permArr) {
                    if (null != permid && permid.trim().matches("[0-9]+")) {
                        Integer pid = Integer.valueOf(permid.trim());
                        ManageRolePermRel mrpr = new ManageRolePermRel();
                        mrpr.setRid(id);
                        mrpr.setPid(pid);
                        mrpr.save();
                    }
                }
            }
            // 修改角色信息
            ManageRole manageRole = ManageRole.dao.findById(id);
            if (manageRole != null) {
                if (roleName != null) {
                    manageRole.setName(roleName);
                    manageRole.setLevel(level);
                    manageRole.setSysType(sysType);
                    manageRole.update();
                }
            }
            return true;
        }
        return false;
    }

    //添加角色
    public String addSysRole(String name, Integer level, String[] permArr,
                             ManageUserInfo user, Integer sys_type) {
        String result = "false";// 0,失败；1，成功；
        String sql = "";
        try {
            if (null != user) {
                if (StringUtils.isNoneBlank(name)) {
                    // 1.保存角色信息
                    //判断是否有同名角色
                    name = name.trim();
                    sql = "select * from manage_role where name ='" + name + "' ";

                    ManageRole mr = ManageRole.dao.findFirst(sql);
                    if (null != mr)
                        return "exist";
                    mr = new ManageRole();
                    mr.setName(name);
                    mr.setLevel(level);
                    mr.setSysType(sys_type);
                    mr.setCreateTime(new Date());
                    // 2.保存角色与权限关系信息
                    if (mr.save()) {
                        Integer id = mr.getId();
                        if (null != permArr && permArr.length > 0) {
                            for (String permid : permArr) {
                                if (null != permid && permid.trim().matches("[0-9]+")) {
                                    Integer pid = Integer.valueOf(permid.trim());
                                    ManageRolePermRel mrpr = new ManageRolePermRel();
                                    mrpr.setRid(id);
                                    mrpr.setPid(pid);
                                    mrpr.save();
                                }
                            }
                        }
                    }
                    result = "success";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    //删除角色  只有超管有权限删除
    public String deleteSysRole(Integer id, ManageUserInfo user) {
        String result = "false";
        Integer level = suchLevel(user);

        if (null != id && level == 1) {
            String sql = "DELETE FROM manage_role_perm_rel WHERE manage_role_perm_rel.rid = " + id;
            String name = DbKit.getConfig(ManagePerm.class).getName();
            Db.use(name).update(sql);

            sql = " delete from manage_role where id = " + id;
            Db.use(name).update(sql);
            result = "success";
        }
        return result;
    }

    /**
     * 获取登录用户所能看到的单位信息（超级管理员-全部 单位管理员-本单位 部门管理员-本单位）
     */

    public String obtainInstatutionByUserLevel(ManageUserInfo userInfo) {
        //获取该用户的机构id
        Integer userId = userInfo.getUserId();
        String sqlUser = "select manage_depart.inst_id from manage_user_info "
                + " left join manage_depart on manage_user_info.dep_id=manage_depart.dep_id "
                + " left join manage_institution on manage_depart.inst_id=manage_institution.id "
                + " where manage_user_info.user_id= '" + userId + "'";
        ManageUserInfo userInfos = ManageUserInfo.dao.findFirst(sqlUser);
        Integer inst_id = userInfos.getInt("inst_id");
        // 根据用户获取该用户的级别
        String level = "";
        JSONObject finalJo = new JSONObject();
        JSONArray ja = new JSONArray();
        String levelSql = "select * from manage_role r left join manage_role_user_rel ru on r.id = ru.rid where ru.uid = "
                + userInfo.getUserId();
        List<ManageRole> find = ManageRole.dao.find(levelSql);
        if (find != null && find.size() > 0) {
            ManageRole role = find.get(0);
            if (role.getLevel() != null && !"".equals(role.getLevel())) {
                level = role.getLevel().toString();
            }
            String sql = "";
            if (level != null && !"".equals(level)) {
                if (level.equals("1")) { // 超级管理员
                    sql = "select * from manage_institution where status = 0";
                    List<ManageInstitution> list = ManageInstitution.dao.find(sql);
                    for (ManageInstitution manageInstitution : list) {
                        JSONObject jo = new JSONObject();
                        if (manageInstitution.getName() != null) {
                            jo.put("insName", manageInstitution.getName());
                            jo.put("insId", manageInstitution.getId());
                            ja.add(jo);
                        }
                    }
                    finalJo.put("level", level);
                    finalJo.put("insId", "");
                    finalJo.put("insObj", ja);
                } else { // 单位管理员或者部门管理员 -- 通过所属单位ID获得
                    if (inst_id != null) {
                        ManageInstitution manageInstitution = ManageInstitution.dao.findById(inst_id);
                        if (manageInstitution.getName() != null) {
                            JSONObject jo = new JSONObject();
                            jo.put("insName", manageInstitution.getName());
                            jo.put("insId", manageInstitution.getId());
                            ja.add(jo);
                            finalJo.put("level", level);
                            finalJo.put("insId", manageInstitution.getId().toString());
                            finalJo.put("insObj", ja);
                        }
                    }
                }
            }
        }
        return finalJo.toJSONString();
    }

    /**
     * 根据单位ID获取部门
     */

    public String obtainDepByInsId(ManageUserInfo userInfo, String insId) {
        JSONObject finalJo = new JSONObject();
        if (insId != null && !"".equals(insId)) {
            String level = "";
            JSONArray ja = new JSONArray();
            String levelSql = "select * from manage_role r left join manage_role_user_rel ru on r.id = ru.rid where ru.uid = "
                    + userInfo.getUserId();
            List<ManageRole> find = ManageRole.dao.find(levelSql);
            if (find != null && find.size() > 0) {
                ManageRole role = find.get(0);
                if (role.getLevel() != null && !"".equals(role.getLevel())) {
                    level = role.getLevel().toString();
                }
                String sql = "";
                if (level != null && !"".equals(level)) {
                    if (level.equals("3")) { // 部门管理员
                        Integer userId = userInfo.getUserId();
                        String sqlUser = "select manage_depart.inst_id from manage_user_info "
                                + " left join manage_depart on manage_user_info.dep_id=manage_depart.dep_id "
                                + " left join manage_institution on manage_depart.inst_id=manage_institution.id "
                                + " where manage_user_info.user_id= '" + userId + "'";
                        ManageUserInfo userInfos = ManageUserInfo.dao.findFirst(sqlUser);
                        Integer inst_id = userInfos.getInt("inst_id");
                        if (inst_id != null) {
                            ManageDepart department = ManageDepart.dao.findById(userInfo.getDepId());
                            if (department.getDepName() != null) {
                                JSONObject jo = new JSONObject();
                                jo.put("depName", department.getDepName());
                                jo.put("depId", department.getDepId());
                                ja.add(jo);
                                finalJo.put("level", level);
                                finalJo.put("depId", department.getDepId());
                                finalJo.put("depObj", ja);
                            }
                        }
                    } else { // 单位管理员或者超级管理员 -- 通过单位ID获得
                        sql = "select * from manage_department where status = 0 and inst_id = " + insId;
                        List<ManageDepart> list = ManageDepart.dao.find(sql);
                        for (ManageDepart department : list) {
                            JSONObject jo = new JSONObject();
                            if (department.getDepName() != null) {
                                jo.put("depName", department.getDepName());
                                jo.put("depId", department.getDepId());
                                ja.add(jo);
                            }
                        }
                        finalJo.put("level", level);
                        finalJo.put("depId", "");
                        finalJo.put("depObj", ja);

                    }
                }
            }
        }
        return finalJo.toString();
    }

    public boolean getsysuser(Integer id) {
        List<ManageRolePermRel> list = ManageRolePermRel.dao.find("select * from manage_role_user_rel where rid=? ",
                id);
        boolean user = false;
        for (ManageRolePermRel manageRolePermRel : list) {
            if (manageRolePermRel.getStatus() == 0) {
                user = true;
            }
        }
        return user;
    }

    public ManageRole getsysRole(Integer id) {
        ManageRole manageRole = ManageRole.dao.findFirst(" select * from manage_role where id=? ", id);
        return manageRole;
    }

    /*
     * public List<MetadataMgr> getMrgallList() { List<MetadataMgr> list = new
     * ArrayList<MetadataMgr>(); list =
     * MetadataMgr.dao.find("select * from metadata_mgr "); return list; }
     */

    public List<Record> getSysRoleMgr(Integer id) {
        if (null != id) {
            String sql = "";
            sql += "	SELECT mid FROM manage_role_mgr	";
            sql += "	WHERE  rid =" + id;
            String name = DbKit.getConfig(ManageRole.class).getName();
            List<Record> pidlist = Db.use(name).find(sql);
            return pidlist;
        }
        return null;
    }

    public boolean updateSysRoleMrg(Integer id, String[] marArr, String roleName) {
        String sql = "";
        if (null != id) {
            // 修改角色的权限配置
            sql = "DELETE FROM manage_role_mgr WHERE manage_role_mgr.rid = " + id;
            String name = DbKit.getConfig(ManagePerm.class).getName();
            Db.use(name).update(sql);
            if (null != marArr && marArr.length > 0) {
                for (String marid : marArr) {
                    if (null != marid && marid.trim().matches("[0-9]+")) {
                        Integer pid = Integer.valueOf(marid.trim());
                        // ManageRoleMgr mrpr = new ManageRoleMgr();
                        // mrpr.setRid(id);
                        // mrpr.setMid(pid);
                        // mrpr.save();
                    }
                }
            }
            // 修改角色的角色名称
            ManageRole manageRole = ManageRole.dao.findById(id);
            if (manageRole != null) {
                if (roleName != null) {
                    manageRole.setName(roleName);
                    manageRole.update();
                }
            }
            return true;
        }
        return false;
    }

// 权限管理 --添加

    public String addRoleMgr(Integer id, String[] marArr, ManageUserInfo user) {
        if (null != id) {
            if (null != marArr && marArr.length > 0) {
                for (String permid : marArr) {
                    if (null != permid && permid.trim().matches("[0-9]+")) {
                        Integer pid = Integer.valueOf(permid.trim());
                        ManageRolePermRel mrpr = new ManageRolePermRel();
                        mrpr.setRid(id);
                        mrpr.setPid(pid);
                        mrpr.save();
                    }
                }
            }
        }
        return "success";
    }

    public ManageUserInfo loginName(String username) {
        String decryptOver = null;
        try {
            decryptOver = new String(Base64.decodeBase64(username), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        ManageUserInfo user = new ManageUserInfo();
        List<ManageUserInfo> list = new ArrayList<ManageUserInfo>();
        // byte[] name=MD5Util.decode(username);
        list = ManageUserInfo.dao.find("select * from manage_user_info where status = 0 and username=?", decryptOver);
        // ManageUserInfo user = new ManageUserInfo();
        // 添加机构信息、所属行业信息
        if (list != null && list.size() > 0) {
            for (ManageUserInfo manageUserInfo : list) {
                user = manageUserInfo;
                Integer userId = user.getUserId();
                String sqlUser = "select manage_depart.inst_id from manage_user_info "
                        + " left join manage_depart on manage_user_info.dep_id=manage_depart.dep_id "
                        + " left join manage_institution on manage_depart.inst_id=manage_institution.id "
                        + " where manage_user_info.user_id= '" + userId + "'";
                ManageUserInfo userInfos = ManageUserInfo.dao.findFirst(sqlUser);
                Integer inst_id = userInfos.getInt("inst_id");
                String sqlRole = "select rid from manage_role_user_rel where uid= '" + userId + "' and status=1";
                ManageRoleUserRel roleUserRel = ManageRoleUserRel.dao.findFirst(sqlRole);
                Integer role_id = null;
                if (roleUserRel != null) {
                    role_id = roleUserRel.getRid();
                }
                ManageRole role = ManageRole.dao.findById(role_id);
                user.put("roleName", role.getName());
                user.put("superView", role.getLevel());
                user.put("instId", inst_id);
                ManageInstitution ins = ManageInstitution.dao.findById(inst_id);
                if (null != ins) {
                    user.put("dbindex", ins.getDbindex());
                } else {
                    user.put("dbindex", "");
                }
            }
        }
        return user;
    }

    /**
     * 名称：getInfoAction 描述 获取用户行为记录信息表 日期：2018年12月28日-下午3:30:04
     *
     * @param pageNo
     * @param pageSize
     * @param name
     * @param stime
     * @param etime
     * @param action
     * @param cationcontent
     * @return
     * @author gongxiang.pang
     * @see com.wenge.datamanager.system.SystemInf#getInfoAction#(java.lang.Integer,
     * java.lang.Integer, java.lang.String, java.lang.String, java.lang.String,
     * java.lang.String, java.lang.String)
     */

    public Object[] getInfoAction(Integer pageNo, Integer pageSize, String name, String stime, String etime,
                                  String action, String cationcontent, ManageUserInfo user) {
        Integer userId = user.getUserId();
        String sqlUser = "select manage_depart.inst_id from manage_user_info "
                + " left join manage_depart on manage_user_info.dep_id=manage_depart.dep_id "
                + " left join manage_institution on manage_depart.inst_id=manage_institution.id "
                + " where manage_user_info.user_id= '" + userId + "'";
        ManageUserInfo userInfos = ManageUserInfo.dao.findFirst(sqlUser);
        Integer inst_id = userInfos.getInt("inst_id");
        String sqle = "select rid from manage_role_user_rel where uid=?";
        ManageRoleUserRel manageRoleUserRel = ManageRoleUserRel.dao.findFirst(sqle, user.getUserId());
        String sql = "  from manager_user_record s LEFT JOIN manage_user_info u  on s.username=u.user_name left join manage_department d on u.dep_id = d.dep_id "
                + " left join manage_institution ins on d.institution_id = ins.id  "
                + " left join manage_role_user_rel as mrur on u.user_id=mrur.uid "
                + " left join manage_role as mr  on mrur.rid=mr.id  "
                + " where  u.status =0 and s.status=1  ";
        if (manageRoleUserRel != null) {// 根据用户id查询出角色权限
            Integer rid = manageRoleUserRel.getRid();
            ManageRole manageRole = ManageRole.dao.findById(rid);
            Integer type = manageRole.getLevel();
            if (type.equals(1)) {// 超级管理员权限查看所有的部门数据

            } else if (type.equals(2)) {
                sql += " and r.level>=2 and d.institution_id =" + inst_id;
            } else if (type.equals(3)) {
                sql += " and r.level>=3 and u.dep_id = " + user.getDepId();
            } else {
                sql += " and r.level=4 and u.id = " + inst_id;
            }
        }
        if (null != name && !"".equals(name.trim())) {
            sql += "	and s.username like '%" + name + "%'	";
        }
        if (null != action && !"".equals(action.trim())) {
            sql += "	and s.action like '%" + action + "%'	";
        }
        if (null != cationcontent && !"".equals(cationcontent.trim())) {
            sql += "	and s.cation_content like '%" + cationcontent + "%'	";
        }
        if (null != stime && !"".equals(stime.trim())) {
            sql += "	and s.insert_time >= '" + stime + "'	";
        }
        if (null != etime && !"".equals(etime.trim())) {
            sql += "	and s.insert_time <= '" + etime + "'	";
        }
        sql += " order by s.insert_time desc";
        // Page<ManagerUserRecord> page = ManagerUserRecord.dao.paginate(pageNo,
        // pageSize, "select s.*,r.level", sql);
        // List<ManagerUserRecord> list = page.getList();

        Object[] res = new Object[2];
        // res[0] = page.getList();
        // res[1] = page.getTotalRow();
        return res;

    }

/**
 *
 * 名称：addActioInfo 描述：添加用户行为记录信息 日期：2018年12月29日-上午10:27:38
 *
 * @author gongxiang.pang
 * @param record
 * @return
 * @see com.wenge.datamanager.system.SystemInf#addActioInfo(com.wenge.datamanager.dao.model.tagmgr.ManagerUserRecord)
 */

    /*
     * public Integer addActioInfo(ManagerUserRecord record) { if (record.save()) {
     * return 1; } else { return 0; } }
     */

    /**
     * 名称：loadUnderUserById 描述：用户行为列表用于显示当前用户的下级 日期：2019年3月01日-上午15:47:38
     *
     * @param
     * @return
     * @author Yipeng.zou
     * @see
     */

    public JSONArray loadUnderUserById(Integer id) {
        JSONArray array = new JSONArray();
        String sqlUser = "select manage_depart.inst_id from manage_user_info "
                + " left join manage_depart on manage_user_info.dep_id=manage_depart.dep_id "
                + " left join manage_institution on manage_depart.inst_id=manage_institution.id "
                + " where manage_user_info.user_id= '" + id + "'";
        ManageUserInfo userInfos = ManageUserInfo.dao.findFirst(sqlUser);
        Integer inst_id = userInfos.getInt("inst_id");
        String sqlRole = "select rid from manage_role_user_rel where uid= '" + id + "' and status=1";
        ManageRoleUserRel roleUserRel = ManageRoleUserRel.dao.findFirst(sqlRole);
        Integer role_id = null;
        if (roleUserRel != null) {
            role_id = roleUserRel.getRid();
        }
        if (roleUserRel != null) {// 根据用户id查询出角色权限
            ManageRole manageRole = ManageRole.dao.findById(role_id);
            Integer type = manageRole.getLevel();
            if (type.equals(1)) {// 超级管理员
                String sql2 = "	select u.id,u.user_name from manage_user_info u "
                        + " left join manage_depart d on u.dep_id = d.dep_id "
                        + " left join manage_institution ins on d.institution_id = ins.id "
                        + " left join manage_role_user_rel as mrur on u.user_id=mrur.uid "
                        + " left join manage_role as mr  on mrur.rid=mr.id  "
                        + " where (mr.level >1 and mr.level<=2 and u.status = 0) or  u.id= ?";
                sql2 += " order by mr.level asc ";
                System.out.println(sql2);
                List<ManageUserInfo> list = ManageUserInfo.dao.find(sql2, id);
                if (null != list && list.size() > 0) {
                    for (int i = 0; i < list.size(); i++) {
                        // JSONObject object=new JSONObject();
                        ManageUserInfo userInfo = list.get(i);
                        // object.put("manageUserInfo", userInfo);
                        array.add(userInfo);
                    }
                }
                return array;

            } else if (type.equals(2)) {// 单位管理员
                if (inst_id != null) {
                    String sql2 = "	select u.id,u.user_name from manage_user_info u "
                            + " left join manage_depart d on u.dep_id = d.dep_id "
                            + " left join manage_institution ins on u.institution_id = ins.id "
                            + " left join manage_role_user_rel as mrur on u.user_id=mrur.uid "
                            + " left join manage_role as mr on mrur.rid=mr.id "
                            + " where (mr.level >2 and mr.level<=3 and  u.status = 0) or  u.id= ? ";
                    sql2 += " order by mr.level asc ";
                    System.out.println(sql2);
                    List<ManageUserInfo> list = ManageUserInfo.dao.find(sql2, id);
                    if (null != list && list.size() > 0) {
                        for (int i = 0; i < list.size(); i++) {
                            // JSONObject object=new JSONObject();
                            ManageUserInfo userInfo = list.get(i);
                            // object.put("manageUserInfo", userInfo);
                            array.add(userInfo);
                        }
                    }
                    return array;
                }
            } else if (type.equals(3)) {// 部门管理员
                if (inst_id != null) {
                    String sql2 = "	select u.id,u.user_name from manage_user_info u "
                            + " left join manage_depart d on u.dep_id = d.dep_id "
                            + " left join manage_institution ins on u.institution_id = ins.id "
                            + " left join manage_role_user_rel as mrur on u.user_id=mrur.uid "
                            + " left join manage_role as mr on mrur.rid=mr.id "
                            + " where (mr.level >3 and mr.level <=4 and u.status = 0) or  u.id= ?";
                    sql2 += " order by mr.level asc ";
                    System.out.println(sql2);
                    List<ManageUserInfo> list = ManageUserInfo.dao.find(sql2, id);
                    if (null != list && list.size() > 0) {
                        for (int i = 0; i < list.size(); i++) {
                            // JSONObject object=new JSONObject();
                            ManageUserInfo userInfo = list.get(i);
                            // object.put("manageUserInfo", userInfo);
                            array.add(userInfo);
                        }
                    }
                    return array;
                }
            }
        }
        return array;
    }

    /**
     * 名称：showUserAction 描述：根据id返回该用户访问所有地址的次数 日期：2019年3月04日-上午17:47:38
     *
     * @return
     * @author Yipeng.zou
     * @see
     */

    public JSONObject showUserAction(Integer id, List<ManagePerm> perm) {
        // List<ManagerUserRecord> list = ManagerUserRecord.dao
        // .find("select username,address from manager_user_record where uid=?", id);
        List list = null;
        List<ManageUserInfo> userInfo = ManageUserInfo.dao.find("select * from manage_user_info where id=?", id);
        JSONObject object = new JSONObject();
        String username = null;
        JSONObject object2 = new JSONObject();
        if (perm.size() > 0) {
            for (ManagePerm managePerm : perm) {
                int count = 0;
                for (int i = 0; i < list.size(); i++) {
                    if (null != managePerm) {
                        // if (list.get(i).getAddress().contains(managePerm.getName())) {
                        // count++;
                        // }
                    }

                }
                if (null != managePerm) {
                    object2.put(managePerm.getName(), count);
                }

            }
        }
        if (userInfo.size() > 0) {
            username = userInfo.get(0).getUserName();
        }

        object.put(username, object2);
        return object;
    }

    /**
     * 名称：getBeforeDay 描述：根据日期获取前6天的日期 日期：2019年3月04日-上午18:27:38
     *
     * @param
     * @return
     * @author Yipeng.zou
     * @see
     */
    private String[] getBeforeDay(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        String[] str = new String[7];
        str[6] = sdf.format(date);
        for (int i = 0; i <= 5; i++) {
            cal.set(Calendar.DATE, cal.get(Calendar.DATE) - 1);
            Date time = cal.getTime();
            String s = sdf.format(time);
            str[5 - i] = s;
        }
        return str;
    }

    /**
     * 名称：showUserActionByTime 描述：根据id,时间返回该用户访问所有地址的次数 日期：2019年3月04日-上午17:47:38
     *
     * @param
     * @return
     * @author Yipeng.zou
     * @see
     */

    public LinkedHashMap showUserActionByTime(Integer id, List<ManagePerm> perm) {
        /*
         * List<ManagerUserRecord> list = null; LinkedHashMap object = new
         * LinkedHashMap(); String[] time = getBeforeDay(new Date()); LinkedHashMap
         * object3 = new LinkedHashMap(); String username = null; LinkedHashMap object2
         * = null; for (String string : time) {
         *
         * list = ManagerUserRecord.dao.find(
         * "select username,address,insert_time from manager_user_record where uid=? AND insert_time like?"
         * , id, "%" + string + "%"); if (list.size() > 0) { username =
         * list.get(0).getUsername(); } object2 = new LinkedHashMap(); if (perm.size() >
         * 0) { for (ManagePerm managePerm : perm) { int count = 0;
         *
         * for (int i = 0; i < list.size(); i++) {
         *
         * if (null != managePerm) { if
         * (list.get(i).getAddress().contains(managePerm.getName())) { count++; } } } if
         * (null != managePerm) { object2.put(managePerm.getName(), count); } } }
         * object3.put(string, object2); } object.put(username, object3); return object;
         * }
         */
        return null;
    }

    public JSONArray loadUnderUser(ManageUserInfo user) {
        JSONArray array = new JSONArray();
        Integer id = user.getUserId();
        String sql = "select role_id from manage_user_info where id=?";
        ManageUserInfo manageUser = ManageUserInfo.dao.findFirst(sql, id);
        if (manageUser != null) {// 根据用户id查询出角色权限
            String sqlUser = "select manage_depart.inst_id from manage_user_info "
                    + " left join manage_depart on manage_user_info.dep_id=manage_depart.dep_id "
                    + " left join manage_institution on manage_depart.inst_id=manage_institution.id "
                    + " where manage_user_info.user_id= '" + id + "'";
            ManageUserInfo userInfos = ManageUserInfo.dao.findFirst(sqlUser);
            Integer inst_id = userInfos.getInt("inst_id");
            String sqlRole = "select rid from manage_role_user_rel where uid= '" + id + "' and status=1";
            ManageRoleUserRel roleUserRel = ManageRoleUserRel.dao.findFirst(sqlRole);
            Integer role_id = null;
            if (roleUserRel != null) {
                role_id = roleUserRel.getRid();
            }
            ManageRole manageRole = ManageRole.dao.findById(role_id);
            Integer type = manageRole.getLevel();
            if (type.equals(1)) {// 超级管理员
                String sql2 = "	select u.user_id,u.user_name from manage_user_info u "
                        + " left join manage_depart d on u.dep_id = d.dep_id "
                        + " left join manage_institution ins on u.institution_id = ins.id "
                        + " left join manage_role_user_rel as mrur on u.user_id=mrur.uid "
                        + " left join manage_role as mr on mrur.rid=mr.id "
                        + " where (mr.level >1 and mr.level<=2 and  u.status = 0) or  u.user_id= ?";
                sql2 += " order by mr.level asc ";
                List<ManageUserInfo> list = ManageUserInfo.dao.find(sql2, id);
                if (null != list && list.size() > 0) {
                    for (int i = 0; i < list.size(); i++) {
                        // JSONObject object=new JSONObject();
                        ManageUserInfo userInfo = list.get(i);
                        // object.put("manageUserInfo", userInfo);
                        array.add(userInfo);
                    }
                }
                return array;

            } else if (type.equals(2)) {// 单位管理员
                if (inst_id != null) {
                    String sql2 = "	select u.user_id,u.user_name from manage_user_info u "
                            + " left join manage_depart d on u.dep_id = d.dep_id "
                            + " left join manage_institution ins on u.institution_id = ins.id "
                            + " left join manage_role_user_rel as mrur on u.user_id=mrur.uid "
                            + " left join manage_role as mr on mrur.rid=mr.id "
                            + " where (mr.level >2 and mr.level<=3 and  u.status = 0) or  u.user_id= ?";
                    sql2 += " order by mr.level asc ";
                    List<ManageUserInfo> list = ManageUserInfo.dao.find(sql2, id);
                    if (null != list && list.size() > 0) {
                        for (int i = 0; i < list.size(); i++) {
                            // JSONObject object=new JSONObject();
                            ManageUserInfo userInfo = list.get(i);
                            // object.put("manageUserInfo", userInfo);
                            array.add(userInfo);
                        }
                    }
                    return array;
                }
            } else if (type.equals(3)) {// 部门管理员
                if (inst_id != null) {
                    String sql2 = "	select u.user_id,u.user_name from manage_user_info u "
                            + " left join manage_department d on u.dep_id = d.dep_id "
                            + " left join manage_institution ins on u.institution_id = ins.id "
                            + " left join manage_role_user_rel as mrur on u.user_id=mrur.uid "
                            + " left join manage_role as mr on mrur.rid=mr.id "
                            + " where (mr.level >3 and mr.level <=4 and u.status = 0) or  u.user_id= ?";
                    sql2 += " order by mr.level asc ";
                    List<ManageUserInfo> list = ManageUserInfo.dao.find(sql2, id);
                    if (null != list && list.size() > 0) {
                        for (int i = 0; i < list.size(); i++) {
                            // JSONObject object=new JSONObject();
                            ManageUserInfo userInfo = list.get(i);
                            // object.put("manageUserInfo", userInfo);
                            array.add(userInfo);
                        }
                    }
                    return array;
                }
            }
        }
        return array;
    }

    public String creat() {
        return "";
    }

    /**
     * @param user
     * @param result
     * @return
     * @throws Exception
     * @description 冻结用户或错误次数清零
     * @author ZSJ
     * @date 2019年7月1日
     */
    public boolean lockingUser(ManageUserInfo user, String result) throws Exception {
        if ("error".equals(result)) { // 密码输错
            Integer errorTimes = user.getErrorTimes();
            if (errorTimes == null) {
                errorTimes = 0;
            }
            if (errorTimes >= 5) { // 次数大于5则冻结
                user.setLockTime(new Date()); // 冻结时间
            } else { // 否则次数累加
                user.setErrorTimes(errorTimes + 1);
            }
        } else {// 正确
            user.setErrorTimes(0);
        }
        return user.update();
    }

    /**
     * @param mediaId
     * @param instId
     * @return
     * @throws Exception
     * @description 获取媒体
     * @author ZSJ
     * @date 2019年5月10日
     */
    public Record queryMediaByMediaId(Integer mediaId, Integer instId) throws Exception {
        String sql = "SELECT de.media_id mediaId,de.media_name mediaName FROM manage_media de where de.media_id = "
                + mediaId + " and de.inst_id = " + instId;
        return Db.findFirst(sql);
    }

    /**
     * @param instId
     * @return
     * @throws Exception
     * @description 查询媒体列表
     * @author ZSJ
     * @date 2019年7月3日
     */
    public List<Record> queryMediaListByInsId(Integer instId) throws Exception {
        String sql = "SELECT de.media_id mediaId,de.media_name mediaName FROM manage_media de where de.inst_id = ?";
        return Db.find(sql, instId);
    }


    //	查询用户对应的角色权限等级
    public Integer suchLevel(ManageUserInfo userInfo) {
        //判断是否已有level 再判断是否当前存储的用户(上一个登录用户)和登录的用户是一致的
        if (LevelUtil.getLevel(userInfo.getUserId()) == null) {
            String sql = "SELECT level FROM manage_user_info u " +
                    "LEFT JOIN manage_role_user_rel r ON u.`user_id`=r.`uid`" +
                    "LEFT JOIN manage_role ro ON r.`rid`=ro.`id`" +
                    "WHERE u.`user_id`= ?";
            Record record = Db.findFirst(sql, userInfo.getUserId());
            Integer level = record.getInt("level");
            if (level == null || "".equals(level)) {
                level = 4;
            }
            LevelUtil.setLevel(userInfo.getUserId(), level);
            return level;
        }
        return LevelUtil.getLevel(userInfo.getUserId());
    }


    //分页查询所有机构
    public Object[] suchInstitutionPage(ManageUserInfo user, Integer page, Integer pageSize, String name) {
        Object[] objects = new Object[2];
        if (suchLevel(user) == 1) {
            String sql;
            if ("".equals(name)) {
                sql = "SELECT * FROM manage_institution where status = 0 ORDER BY create_time DESC";

            } else {
                sql = "SELECT * FROM manage_institution where status = 0 AND NAME LIKE '%" + name + "%'";
                sql += " ORDER BY create_time DESC";
            }
            String sql2 = pageHelper(sql, page, pageSize);
            List<ManageInstitution> institutions = ManageInstitution.dao.find(sql2);
            Integer total = getTotal("manage_institution");
            objects[0] = institutions;
            objects[1] = total;
            return objects;
        }
        if (suchLevel(user) == 2 || suchLevel(user) == 3) {
            String sql = "SELECT * FROM manage_institution i LEFT JOIN manage_depart d ON  i.`id`=d.`inst_id` WHERE i.status = 0 AND d.`dep_id` =" + user.getDepId();
            List<ManageInstitution> institutions = ManageInstitution.dao.find(sql);
            objects[0] = institutions;
            objects[1] = 1;
            return objects;
        }
        return null;
    }

    //不带分页查询所有机构
    public List<ManageInstitution> suchInstitution(ManageUserInfo user) {
        if (suchLevel(user) == 1) {
            String sql = "SELECT * FROM manage_institution where status = 0 ORDER BY create_time DESC ";
            return ManageInstitution.dao.find(sql);
        }
        if (suchLevel(user) == 2 || suchLevel(user) == 3) {
            String sql = "SELECT * FROM manage_institution i LEFT JOIN manage_depart d ON  i.`id`=d.`inst_id` WHERE i.status = 0 AND d.`dep_id` =" + user.getDepId();
            return ManageInstitution.dao.find(sql);
        } else
            return null;
    }


    //分页拼接
    public String pageHelper(String sql, Integer page, Integer pageSize) {
        int p = (page - 1) * pageSize;
        sql += " limit " + p + "," + pageSize;
        return sql;
    }

    //获取total
    public Integer getTotal(String tableName) {
        String sql = "SELECT COUNT(*) total FROM " + tableName + " where status = 0";
        Record first = Db.findFirst(sql);
        Integer total = JSON.parseObject(first.toString()).getInteger("total");
        return total;
    }

    // 根据机构ID查询包含的部门
    public List<ManageDepart> findDepartByInsId(Long ins_id, Integer page, Integer pageSize) {
        String sql = "SELECT dep_id depId,dep_name name FROM manage_depart WHERE inst_id = ? and status =0 ";
        String sql2 = pageHelper(sql, page, pageSize);
        return ManageDepart.dao.find(sql2, ins_id);
    }


    public Object[] findDepartByInsIdPlus(ManageUserInfo user, Integer ins_id, Integer page, Integer pageSize) {
        Integer level = suchLevel(user);
        Object[] objects = new Object[2];
        if (level == 4) {
            return null;
        }
        if (level == 2 || level == 3) {
            ManageInstitution institution = findInstitutionByUserId(user.getUserId());
            if (institution.getId() != (long) ins_id) {
                return null;
            }
        }
        String sql = "SELECT i.`id` instId,i.`name` insName,d.`dep_id` depId,d.`dep_name` dep,m.`media_id` mediaId,m.`media_name`,d.`create_time` creatTime  FROM manage_institution i " +
                "LEFT JOIN manage_depart d ON i.`id`=d.`inst_id`" +
                "LEFT JOIN manage_media m ON m.`media_id`=d.`media_id` " +
                "WHERE i.`status`=0 AND d.`status`=0 AND i.`id`=?";
        String sql2 = pageHelper(sql, page, pageSize);
        List<Record> list = Db.find(sql2, ins_id);
        objects[0] = list;
        String sqlTotal = "SELECT count(*) total FROM manage_institution i " +
                "LEFT JOIN manage_depart d ON i.`id`=d.`inst_id`" +
                "LEFT JOIN manage_media m ON m.`media_id`=d.`media_id` " +
                "WHERE i.`status`=0 AND d.`status`=0 AND i.`id`=?";
        Record record = Db.findFirst(sqlTotal, ins_id);
        objects[1] = JSONObject.parseObject(record.toString()).getInteger("total");

        return objects;
    }


    // 根据用户ID 查询所属机构
    public ManageInstitution findInstitutionByUserId(Integer userId) {
        String sql = "SELECT i.`id` FROM manage_institution i LEFT JOIN manage_depart d ON d.`inst_id`=i.`id` " +
                "LEFT JOIN manage_user_info u ON u.`dep_id` = d.`dep_id` " +
                "WHERE u.`user_id` = ? and i.status=0";
        return ManageInstitution.dao.findFirst(sql, userId);
    }

    //  增加一个部门
    public String addDepartment(String param, ManageUserInfo user) {
        try {
            JSONObject json = JSONObject.parseObject(param);
            Integer level = suchLevel(user);
            ManageDepart department = new ManageDepart();

            if (level == 2) {
                ManageInstitution institution = findInstitutionByUserId(user.getUserId());
                Long insId = json.getLong("insId");
                Long id = institution.getId();
                if (!insId.equals(id)) {
                    return "部门管理员只能添加本机构下的内容";
                }
            }

            if (level == 1 || level == 2) {// 超级管理员和单位管理员有添加权限
                if (json.getString("name") == null || json.getInteger("insId") == null) {
                    return "name或insId不能为空";
                }
                if (json.getString("name") != null) {
                    String name = json.getString("name").trim();
                    department.setDepName(name);
                }
                if (json.getString("leader") != null) {
                    String leader = json.getString("leader").trim();// 领导
                    department.setLeader(leader);
                }
                if (json.getString("contacter") != null) {
                    String contacter = json.getString("contacter").trim();//联系人
                    department.setContacter(contacter);
                }
                if (json.getString("address") != null) {
                    String address = json.getString("address").trim();
                    department.setAddress(address);
                }
                if (json.getString("telephone") != null) {
                    String telephone = json.getString("telephone").trim();
                    department.setTel(telephone);
                }
                if (json.getString("email") != null) {
                    String email = json.getString("email").trim();
                    department.setEmail(email);
                }
                Integer mediaId = json.getInteger("mediaId");// 媒体id
                Integer inst_id = json.getInteger("insId");// 机构id
                department.setMediaId(mediaId);
                department.setInstId(inst_id);
                department.setCreateTime(new Date());
                department.setStatus(0);
                department.save();
                return "success";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "false";
        }
        return "false";
    }

    //修改部门信息
    public String updataDepartment(String param, ManageUserInfo user) {
        JSONObject json = JSONObject.parseObject(param);
        Integer level = suchLevel(user);
        if (level == 1 || level == 2) {// 超级管理员、单位管理员
            Integer dep_id = json.getInteger("id");
            Integer mediaId = json.getInteger("mediaId");
            Integer inst_id = json.getInteger("insId");
            String name = json.getString("name").trim();
            String leader = json.getString("leader").trim();
            String contacter = json.getString("contacter").trim();
            String address = json.getString("address").trim();
            String telephone = json.getString("telephone").trim();
            String email = json.getString("email").trim();
            ManageDepart department = ManageDepart.dao.findById(dep_id);
            department.setDepName(name);
            department.setLeader(leader);
            department.setContacter(contacter);
            department.setAddress(address);
            department.setTel(telephone);
            department.setEmail(email);
            department.setMediaId(mediaId);
            department.setInstId(inst_id);
            department.update();
            return "ok";
        }
        return "error";
    }

    //删部门
    public String deleteDepartment(String depid, ManageUserInfo user) {
        Integer level = suchLevel(user);
        if (depid != null && !"".equals(depid) && (level == 1 || level == 2)) {
            ManageDepart department = ManageDepart.dao.findById(depid);
            department.setStatus(-1);
            boolean update = department.update();
            if (update == true) {
                return "success";
            } else {
                return "false";
            }
        } else {
            return "false";
        }
    }

    // 根据部门id查部门详情
    public JSONObject echoDepartment(String id) {
        ManageDepart department = ManageDepart.dao.findById(id);
        JSONObject obj = new JSONObject();
        if (department != null) {
            String name = department.getDepName();
            String leader = department.getLeader();
            String contacter = department.getContacter();
            String telephone = department.getTel();
            String address = department.getAddress();
            String email = department.getEmail();
            Integer instId = department.getInstId();
            obj.put("depName", HtmlUtils.htmlUnescape(name));
            obj.put("leader", HtmlUtils.htmlUnescape(leader));
            obj.put("contacter", HtmlUtils.htmlUnescape(contacter));
            obj.put("telephone", telephone);
            obj.put("address", HtmlUtils.htmlUnescape(address));
            obj.put("email", HtmlUtils.htmlUnescape(email));
            obj.put("instId", instId);
            ManageInstitution institution = ManageInstitution.dao.findById(instId);
            obj.put("insName", institution.getName());
//            obj.put("mediaId", department.getMediaId());
            Integer mediaId = department.getMediaId();
            if (mediaId != null) {
                obj.put("mediaId", mediaId);
                ManageMedia manageMedia = ManageMedia.dao.findById(mediaId);
                obj.put("mediaName", manageMedia.getMediaName());
            } else {
                obj.put("mediaId", -1);
                obj.put("mediaName", "");
            }
        }
        return obj;
    }

    //根据机构id查询机构
    public ManageInstitution findInstitutionByInsId(Integer insId) {
        ManageInstitution institution = ManageInstitution.dao.findById(insId);
        return institution;
    }

    //根据机构id查询对应媒体
    public List<ManageMedia> findMediaByInsId(ManageUserInfo user, Integer insId) {
        Integer level = suchLevel(user);
        if (level == 1 || level == 2 || level == 3) {
            String sql = "SELECT * FROM manage_media WHERE inst_id = ?";
            List<ManageMedia> manageMedia = ManageMedia.dao.find(sql, insId);
            return manageMedia;
        }
        return null;
    }

    //根据媒体id查询对应部门
    public List<ManageDepart> findDepartByMediaId(ManageUserInfo user, Integer mediaId) {
        Integer level = suchLevel(user);
        if (level != 4) {
            String sql = "SELECT * FROM manage_depart WHERE media_id =? and status = 0";
            List<ManageDepart> departs = ManageDepart.dao.find(sql, mediaId);
            return departs;
        }
        return null;
    }


    public ManageMedia echoMedia(String id) {

        return ManageMedia.dao.findById(id);
    }

    public JSONObject queryRoleByid(ManageUserInfo user, String id) {
        JSONObject json = new JSONObject();
        Integer level = suchLevel(user);
        if (level == 1) {
            //获取角色权限列表
            String sql = "";
            sql += "	SELECT manage_role_perm_rel.pid FROM manage_role_perm_rel	";
            sql += "	WHERE manage_role_perm_rel.status = 0 and manage_role_perm_rel.rid = " + id;
            String name = DbKit.getConfig(ManagePerm.class).getName();
            List<Record> pidlist = Db.use(name).find(sql);

            ManageRole role = ManageRole.dao.findById(id);
            json.put("id", role.getId());
            json.put("level", role.getLevel());
            json.put("sysType", role.getSysType());
            json.put("name", role.getName());
            json.put("pidlist", pidlist);
        } else {
            json.put("massage", false);
        }
        return json;
    }

}
