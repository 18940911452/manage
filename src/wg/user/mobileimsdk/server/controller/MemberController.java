package wg.user.mobileimsdk.server.controller;

import java.io.*;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 成员管理相关接口
 */
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.core.Controller;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.upload.UploadFile;

import Decoder.BASE64Decoder;
import wg.media.screen.fm.model.commandscreen.AppVersion;
import wg.media.screen.fm.model.commandscreencommon.ManageUserInfo;
import wg.user.mobileimsdk.server.model.vo.ResultMessageVo;
import wg.user.mobileimsdk.server.service.MemberService;
import wg.user.mobileimsdk.server.service.UploadFileService;

public class MemberController extends Controller {
	public static String headAdress = PropKit.get("headAdress");
	public static String localHeadAdress = PropKit.get("localHeadAdress");
	Logger logger = LoggerFactory.getLogger(MemberController.class);
	public void index() {
		render("/loadtest.html");
	}

	private ManageUserInfo user;

	private ManageUserInfo getManageUserInfo() {
		if (user == null) {
			user = (ManageUserInfo) getSession().getAttribute("login_user");
		}
		return user;
	}
	/**
	 *
	 * @method:getFoShanUserInfo
	 * @describe:获取登录用户信息，根据userName
	 * @author: gongxiangPang from zhangzhigang
	 * @param :TODO
	 */
	public void getUserInfoByName() {
		int status = -1;
		String message = "查询失败";
		JSONObject result = null;
		String userName = getPara("userName");
		String pth = getRequest().getScheme() + "://" + getRequest().getServerName() + ":"
				+ getRequest().getServerPort() + getRequest().getContextPath() + "/";

		try {
			result = MemberService.ser.getUserInfoByName(userName, pth);
			status = 1;
			message = "success";
		} catch (Exception e) {
			e.printStackTrace();
		}
		renderJson(new ResultMessageVo(status, message, result));
	}

	/**
	 *
	 * @method:getFoShanUserInfo
	 * @describe:获取登录用户信息，根据userName
	 * @author: gongxiangPang from zhangzhigang
	 * @param :TODO
	 */
	public void getUserInfoById() {
		int status = -1;
		String message = "查询失败";
		JSONObject result = null;
		String userId = getPara("userId");
		// String
		// pth=getRequest().getScheme()+"://"+getRequest().getServerName()+":"+getRequest().getServerPort()+getRequest().getContextPath()+"/";
		String pth = headAdress;
		try {
			result = MemberService.ser.getUserInfoById(userId, pth);
			status = 1;
			message = "success";
		} catch (Exception e) {
			e.printStackTrace();
		}
		renderJson(new ResultMessageVo(status, message, result));
	}

	/**
	 *
	 * @method:getAllUserInfoNoSelf
	 * @describe:获取所有用户，除了自己
	 *
	 * @author: gongxiangPang from zhangzhigang
	 * @param :TODO
	 */
	public void getAllUserInfoNoSelf() {
		int status = -1;
		String message = "查询失败";
		JSONArray result = null;
		Integer userId = getParaToInt("userId");
		// String
		// pth=getRequest().getScheme()+"://"+getRequest().getServerName()+":"+getRequest().getServerPort()+getRequest().getContextPath()+"/";
		String pth = headAdress;
		try {
			result = MemberService.ser.getFoShanAllUserInfo(userId, pth);
			status = 1;
			message = "success";
		} catch (Exception e) {

			e.printStackTrace();
		}
		renderJson(new ResultMessageVo(status, message, result));
	}

	/**
	 *
	 * @describe:排除这个userid,场景，记者地图需要排除当前登录的用户
	 * @author: gongxiangPang from zhangzhigang
	 * @param :TODO
	 */
	public void getAllReporter() {
		int status = -1;
		String message = "查询失败";
		JSONArray result = null;
		Integer userId = getParaToInt("userId");
		// String
		// pth=getRequest().getScheme()+"://"+getRequest().getServerName()+":"+getRequest().getServerPort()+getRequest().getContextPath()+"/";
		String pth = headAdress;
		try {
			result = MemberService.ser.getAllReporter(userId, pth);
			status = 1;
			message = "success";
		} catch (Exception e) {

			e.printStackTrace();
		}
		renderJson(new ResultMessageVo(status, message, result));
	}

	/**
	 *
	 * @description 排除参数中的用户，返回其他所有用户
	 * @author ZSJ
	 * @date 2019年5月9日
	 */
	public void queryAllUserInfoByParams() {
		int status = -1;
		String message = "查询失败";
		List<Record> result = null;
		String userIds = getPara("exceptUserId");
		String groupId = getPara("groupId");
		String deptId = getPara("deptId");
		String userName = getPara("userName");
		String pth = headAdress;
		try {
			result = MemberService.ser.queryAllUserInfoByParams(userIds, groupId, deptId, userName);
			status = 1;
			message = "success";
		} catch (Exception e) {
			e.printStackTrace();
		}
		renderJson(new ResultMessageVo(status, message, result));
	}

	/**
	 *
	 * @description 根据参数过滤用户
	 * @author ZSJ
	 * @date 2019年5月9日
	 */
	public void queryUserInfoByParams() {
		int status = -1;
		String message = "查询失败";
		List<Record> result = null;
		String userIds = getPara("insId");
		String exceptUserId = getPara("exceptUserId");
		String groupId = getPara("groupId");
		String deptId = getPara("mediaId");
		String userName = getPara("userName");
		String pth = headAdress;
		try {
			result = MemberService.ser.queryUserInfoByParams(userIds, groupId, deptId, userName, exceptUserId);
			status = 1;
			message = "success";
		} catch (Exception e) {
			e.printStackTrace();
		}
		renderJson(new ResultMessageVo(status, message, result));
	}

	/**
	 *
	 * @method:getAllUserInfoNoSelfByName
	 * @describe:查询所有成员不包括自己 通过用户名
	 * @author: gongxiangPang
	 * @param :TODO
	 */
	public void getAllUserInfoNoSelfByName() {
		int status = -1;
		String message = "查询失败";
		JSONArray result = null;
		String userName = getPara("userName");
		String pth = getRequest().getScheme() + "://" + getRequest().getServerName() + ":"
				+ getRequest().getServerPort() + getRequest().getContextPath() + "/";
		try {
			result = MemberService.ser.getAllUserInfoNoSelfByName(userName, pth);
			status = 1;
			message = "success";
		} catch (Exception e) {
			e.printStackTrace();
		}
		renderJson(new ResultMessageVo(status, message, result));
	}

	/**
	 *
	 * @method:getAllUserInfo
	 * @describe:排除这个userid,场景，记者地图需要排除当前登录的用户
	 * @author: gongxiangPang from zhangzhigang
	 * @param :TODO
	 */
	public void getAllUserInfo() {
		int status = -1;
		String message = "查询失败";
		JSONArray result = null;
		Integer userId = getParaToInt("userId");
		// String
		// pth=getRequest().getScheme()+"://"+getRequest().getServerName()+":"+getRequest().getServerPort()+getRequest().getContextPath()+"/";
		String pth = headAdress;
		try {
			result = MemberService.ser.getAllUserInfo(userId, pth);
			status = 1;
			message = "success";
		} catch (Exception e) {
			e.printStackTrace();
		}
		renderJson(new ResultMessageVo(status, message, result));
	}

	public void queryUserInfoByIds() throws Exception {
		int status = -1;
		String message = "error";
		List<Record> result = null;
		String userIds = getPara("userIds");
		String pth = headAdress;
		try {
			result = MemberService.ser.queryUserInfoByIds(userIds, pth);
			status = 1;
			message = "success";
		} catch (Exception e) {
			e.printStackTrace();
		}
		renderJson(new ResultMessageVo(status, message, result));
	}

	public void queryDeptsByInsId() throws Exception {
		int status = -1;
		String message = "error";
		List<Record> result = null;
		String insId = getPara("insId");
		try {
			result = MemberService.ser.queryDeptsByInsId(insId);
			status = 1;
			message = "success";
		} catch (Exception e) {
			e.printStackTrace();
		}
		renderJson(new ResultMessageVo(status, message, result));
	}

	/**
	 * 更新登录用户经纬度
	 *
	 * @param userId
	 *            用户id, longitude 经度 ,latitude 纬度
	 * @return
	 */
	public void getCoordByUserId() {
		int status = -1;
		String message = "获取失败";
		JSONObject result = null;
		Integer userId = getParaToInt("userId");
		Double longitude = Double.valueOf(getPara("longitude"));
		Double latitude = Double.valueOf(getPara("latitude"));
		try {
			result = MemberService.ser.getCoordByUserId(userId, longitude, latitude);
			status = 1;
			message = "success";
		} catch (Exception e) {
			e.printStackTrace();
		}
		renderJson(new ResultMessageVo(status, message, result));
	}

	/**
	 * 修改群名称
	 *
	 * @param groupId,groupName
	 * @return
	 */
	public void updateGroupName() {
		int status = -1;
		String message = "修改群名称失败";
		JSONObject result = null;
		Integer groupId = getParaToInt("groupId");
		String groupName = getPara("groupName");
		try {
			result = MemberService.ser.updateGroupName(groupId, groupName);
			status = 1;
			message = "success";
		} catch (Exception e) {
			e.printStackTrace();
		}
		renderJson(new ResultMessageVo(status, message, result));
	}

	/**
	 * 名称：addUser 描述：添加用户
	 *
	 * @author gongxiang.pang
	 * @parameter
	 * @return http://localhost:8080/WG_HC_IM/member/addUser?user_name=ceshi&password=123456&nick_name=%E9%92%A2%E8%9B%8B&tel=18840511232&email=123@qq.com
	 */
	public void addUser() {
		String user_name = getPara("user_name");// 登录用户名，建议使用手机号
		String password = getPara("password");// 登录密码
		String nick_name = getPara("nick_name");// 昵称或姓名
		String tel = getPara("tel");// 手机号登录
		String email = getPara("email");//
		Integer dep_id = getParaToInt("dep_id", 1);
		Integer mediaId = getParaToInt("mediaId", 1);
		// String headPath =
		// getRequest().getSession().getServletContext().getRealPath("/") +
		// File.separator + "chatsource"+ File.separator + "headicon";// 上传的路径
		String headPath = localHeadAdress + "chatsource/headicon/";
		String basePath = "chatsource/headicon/";
		JSONObject addUser = MemberService.ser.addUser(user_name, password, nick_name, tel, dep_id, email, headPath,
				basePath, mediaId);
		renderJson(addUser);
	}


	/**
	 *
	 * 名称：uploadIcon 描述：1.上传头像到头像文件夹， 2.然后返回地址url 3.url更新用户信息 3.返回保存之后的地址，json类型
	 *
	 * @author gongxiang.pang
	 * @parameter
	 * @return
	 */
	public void uploadIcon() {
		String result = "";
		UploadFile uploadFile = getFile();// 这句已经完成上传，一下是从上传的文件进行重新上传到
		String fileName = uploadFile.getOriginalFileName();
		File file = uploadFile.getFile();
		Integer uid = getParaToInt("uid", 146);
		String filename = UploadFileService.ser.newName(fileName);

		// String
		// dir=getRequest().getSession().getServletContext().getRealPath("/")+File.separator+"images"+File.separator+"headicon"+File.separator+filename;//上传的路径
		// String basePath =
		// getRequest().getScheme()+"://"+getRequest().getServerName()+":"+getRequest().getServerPort()+getRequest().getContextPath()+"/";
		String basePath = headAdress;
		String lastPath = "chatsource/headicon/" + filename;
		String dir = localHeadAdress + "chatsource" + File.separator + "headicon" + File.separator + filename;// 上传的路径;
		File to = new File(dir);

		try {
			to.createNewFile();
			MemberService.ser.updateByString("icon", lastPath, uid);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		boolean re = UploadFileService.ser.fileChannelCopy(file, to);
		file.delete();
		if (re) {
			result = basePath + lastPath;
		} else {
			result = "false";
		}
		JSONObject jo = new JSONObject();
		jo.put("result", result);
		jo.put("status", 1);// 1上传成功 ,0 上传中 -1上传失败
		renderJson(jo);

	}

	/**
	 * 名称：uploadIcon 描述：接收base64图片
	 *
	 * @return
	 * @author gongxiang.pang
	 * @parameter
	 */
	public void uploadIcon2() {
		String result = "";
		String picStr = getPara("picStr");
		BASE64Decoder decoder = new BASE64Decoder();
		Integer uid = getParaToInt("uid", 146);
		String dir = localHeadAdress + "chatsource" + File.separator + "headicon" + File.separator ;// 上传的路径;
		File newFile = new File(dir);
		if (!newFile.exists()){
			newFile.mkdirs();
		}
		String picName=UUID.randomUUID().toString()+  ".jpg";
		File file = new File(dir, picName );
		try (OutputStream out = new FileOutputStream(file)) {
			// Base64解码
			byte[] b = decoder.decodeBuffer(picStr);
			for (int i = 0; i < b.length; ++i) {
				if (b[i] < 0) {// 调整异常数据
					b[i] += 256;
				}
			}
			out.write(b);
			String basePath = headAdress;
			String lastPath = "chatsource/headicon/" + picName;
			boolean re=MemberService.ser.updateByString("icon", lastPath, uid);
			if (re) {
				result = basePath + lastPath;
			} else {
				result = "false";
			}
			JSONObject jo = new JSONObject();
			jo.put("result", result);
			jo.put("status", 1);// 1上传成功 ,0 上传中 -1上传失败
			renderJson(jo);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	/**
	 *
	 * 名称：updateUserInfo 描述：更新用户信息
	 *
	 * @author gongxiang.pang
	 * @parameter
	 * @return
	 */
	public void updateUserInfo() {
		Integer uid = getParaToInt("uid");
		String field = getPara("field");
		String content = getPara("content");
		JSONObject jo = new JSONObject();
		if (uid != null) {
			boolean re = MemberService.ser.updateByString(field, content, uid);
			if (re) {
				jo.put("result", "success");
				jo.put("status", "1");// 1上传成功 ,0 上传中 -1上传失败
				jo.put("message", "成功");
				renderJson(jo);
			}
		} else {
			jo.put("result", "失败");
			jo.put("status", "-1");// 1上传成功 ,0 上传中 -1上传失败
			jo.put("message", "用户uid不能为空");

		}
		renderJson(jo);
	}

	/**
	 *
	 * @description 更新用户信息
	 * @author ZSJ
	 * @throws Exception
	 * @date 2019年5月10日
	 */
	public void ajaxUpdateUserInfo() throws Exception {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("userId", getPara("userId"));
		jsonObject.put("userTel", getPara("userTel"));
		jsonObject.put("email", getPara("email"));
		jsonObject.put("nation", getPara("nation"));
		jsonObject.put("sex", getPara("sex"));
		jsonObject.put("birthdate", getPara("birthdate"));
		jsonObject.put("deptId", getPara("deptId"));
		jsonObject.put("newPassword", getPara("newPassword"));
		jsonObject.put("headPortrait", getPara("headPortrait"));
		JSONObject jo = new JSONObject();
		if (jsonObject != null) {
			boolean re = MemberService.ser.ajaxUpdateUserInfo(jsonObject);
			if (re) {
				jo.put("result", "success");
				jo.put("status", "1");
				jo.put("message", "成功");
				renderJson(jo);
			} else {
				jo.put("result", "失败");
				jo.put("status", "-1");
				jo.put("message", "用户uid不能为空");
				renderJson(jo);
			}
		}
	}

	/**
	 * 修改密码 名称：updatePassword
	 *
	 * @author gongxiang.pang
	 * @parameter
	 * @return
	 */
	public void updatePassword() {
		String uid = getPara("uid");
		String oldpassword = getPara("oldpassword");
		String newpassword = getPara("newpassword");
		JSONObject jo = new JSONObject();
		if (uid != null && oldpassword != null && newpassword != null) {
			jo = MemberService.ser.updatePassword(uid, oldpassword, newpassword);
		} else {
			jo.put("result", "false");
			jo.put("status", "-1");
			jo.put("message", "用户id或者密码不能为空");
		}
		renderJson(jo);
	}

	/**
	 *
	 * @description 更新密码前，务必校验原来密码
	 * @author ZSJ
	 * @throws Exception
	 * @date 2019年5月28日
	 */
	public void ajaxValidatePassword() throws Exception {
		String oldPassword = getPara("oldPassword");
		String userId = getPara("userId");
		JSONObject jo = new JSONObject();
		if (StringUtils.isNotBlank(oldPassword) && StringUtils.isNotBlank(userId)) {
			jo = MemberService.ser.ajaxValidatePassword(oldPassword, userId);
		} else {
			jo.put("result", "false");
			jo.put("message", "密码校验不通过");
		}
		renderJson(jo);
	}

	/**
	 * 最新版本号
	 */
	public void getVersionInfo() {

		JSONObject json = new JSONObject();
		String androidVersion = PropKit.get("androidVersion");
		String iosVersion = PropKit.get("iosVersion");
		String androidPath = PropKit.get("androidPath");

		json.put("code", 1);
		json.put("androidVersion", androidVersion);
		json.put("iosVersion", iosVersion);
		json.put("androidPath", androidPath);

		renderJson(json);

	}

	/**
	 * 新增意见反馈
	 */

	public void getFeedback(){
		String content=getPara("content");
		String tell=getPara("tell");
		ManageUserInfo user = getManageUserInfo();
		JSONObject a=MemberService.ser.getFeedback(content,tell,user);
		renderJson(a);
	}

	/**
	 * 获取掌上融媒最新版本号
	 */
	public void getNewVersion(){
		String type=getPara("type");
		AppVersion json=MemberService.ser.getNewVersion(type);
		JSONObject jsonObject = new JSONObject();
		if(null!=json){
			jsonObject.put("data",json);
			jsonObject.put("status",1);
		}
		if(null == json){
			jsonObject.put("data",json);
			jsonObject.put("status",-1);
		}
		renderJson(jsonObject);
	}
	
	/**
	 * 判断当前用户是否打开应用
	 */
	public void getUserApp(){
		Integer status=getParaToInt("status");
		String userId=getPara("userId");
		JSONObject a=MemberService.ser.getUserApp(status,userId);
		renderJson(a);
	}
}
