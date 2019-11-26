package wg.user.mobileimsdk.server.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.kit.PathKit;
import com.jfinal.upload.UploadFile;

import wg.media.screen.fm.model.commandscreen.Task;
import wg.media.screen.fm.model.commandscreen.TaskLog;
import wg.media.screen.fm.model.commandscreen.Topic;
import wg.media.screen.fm.model.commandscreencommon.ManageUserInfo;
import wg.media.screen.fm.vo.ResultMessageVo;
import wg.media.screen.fm.vo.TaskResultVo;
import wg.user.mobileimsdk.server.service.TaskManageService;

public class Mod2Controller extends IController {
	Logger logger = LoggerFactory.getLogger(Mod2Controller.class);
	private TaskManageService ser = new TaskManageService();
	static SimpleDateFormat sdf = new SimpleDateFormat();

	public void index() {
		render("main.html");
	}

	public void getAllUser() {
		ManageUserInfo user = getUser();
		System.out.println(user);
		JSONObject object = new JSONObject();
		object.put("code", 0);
		List<ManageUserInfo> ls = ser.getAllUser(user);
		if (null != ls && ls.size() > 0) {
			object.put("code", 1);
			object.put("data", ls);
		}
		renderJson(object);
	}

	public ManageUserInfo getUser(String userId) {
		ManageUserInfo user = new ManageUserInfo();
		user = ser.findUserById(userId);
		if (null == user) {
			user = new ManageUserInfo();
			user.setUserId(11);
			user.setUserName("张三");
		}
		return user;
	}

	/**
	 * @version 请求任务列表和单个任务详情接口
	 * @param relation_type
	 *            1：我发起的，2：我负责的，3：我参与的
	 * @param task_id
	 *            单个任务详情
	 * @param task_name
	 *            相关任务
	 */
	public void taskList() {
		ManageUserInfo user = getUser();
		if (null == user) {
			user = getUser(getPara("userId"));
		}
		JSONObject object = new JSONObject();
		object.put("code", 0);
		object.put("data", null);
		Object[] obj = null;
		String path = getPara("path");
		Integer task_id = getParaToInt("task_id");
		String task_name = getPara("task_name");
		Integer pageNo = getParaToInt("pageNo");
		Integer pageSize = getParaToInt("pageSize");
		Integer relation_type = getParaToInt("relation_type");

		try {
			obj = ser.getTaskList(task_id, task_name, user, pageNo, pageSize, relation_type);
			if (StringUtils.isNoneBlank(path)) {
				setAttr("total", obj[0]);
				setAttr("list", (List<TaskResultVo>) obj[1]);
				renderT(path);
			} else {
				object.put("code", 1);
				object.put("data", (List<TaskResultVo>) obj[1]);
				renderJson(object);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 删除附件
	 */
	public void deleteFileById() {
		Integer id = getParaToInt("id");
		JSONObject obj = new JSONObject();
		obj.put("code", 0);
		if (null != id) {
			Boolean flag = ser.deleteFileById(id);
			if (flag) {
				obj.put("code", 1);
			}
		}
		renderJson(obj);
	}

	/**
	 * 任务主题下拉框
	 */
	public void topicalList() {
		ManageUserInfo user = getUser();
		List<Topic> ls = ser.topicalList(user);
		renderJson(ls);
	}

	/**
	 * 任务添加/编辑
	 */
	public void updateTask() {
		ManageUserInfo user = getUser();
		if (null == user) {
			user = getUser(getPara("userId"));
		}
		HttpServletRequest request = getRequest();
		JSONObject obj = new JSONObject();
		obj.put("code", 0);
		String jsonData = null;
		Task task = null;
		try {
			request.setCharacterEncoding("UTF-8");
			DiskFileItemFactory factory = new DiskFileItemFactory();
			ServletFileUpload upload = new ServletFileUpload(factory);
			List items = upload.parseRequest(request);// 上传文件解析
			String path = PathKit.getWebRootPath();
			String rootpath = getRequest().getSession().getServletContext().getRealPath("");

			Iterator itr = items.iterator();
			while (itr.hasNext()) {
				FileItem item = (FileItem) itr.next();
				if (item.isFormField()) {
					// 普通表单域
					jsonData = item.getString("UTF-8");
					if (StringUtils.isNotBlank(jsonData)) {
						JSONObject json = JSONObject.parseObject(jsonData);
						task = ser.saveTask(json, user, false);
					}
				}
			}
			if (null != task) {
				itr = items.iterator();
				while (itr.hasNext()) {
					FileItem item = (FileItem) itr.next();
					if (!item.isFormField()) {
						// 文件域
						String fileName = item.getName();
						// fileName = FilenameUtils.getBaseName(fileName);
						InputStream fileContent = item.getInputStream();
						// String text = IOUtils.toString(filecontent,"UTF-8");
						try {
							String[] fileNameArr = null;
							if (fileName.contains(".")) {
								fileNameArr = fileName.split("\\.");
							}
							fileName = fileNameArr[0] + "_" + System.currentTimeMillis() + "." + fileNameArr[1];
						} catch (Exception e) {
							e.printStackTrace();
						}

						// 附件路徑
						try {
							String dirs = path + File.separator + "taskFile" + "/" + user.getUserId();
							String dir = dirs + "/" + fileName;// 上传的路径
							File fileDir = new File(dirs);
							if (!fileDir.exists())
								fileDir.mkdirs();
							// 文件IO寫出操作
							if (fileContent != null) {
								FileOutputStream os = new FileOutputStream(dir);
								byte[] buffer = new byte[1024];
								int len = fileContent.read(buffer, 0, buffer.length);
								while (len != -1) {
									os.write(buffer, 0, len);
									len = fileContent.read(buffer, 0, buffer.length);
								}
								fileContent.close();
								os.close();
							}
							// 保存 附件信息 入库
							String fileUrlPath = getWebRootAddress() + File.separator + "taskFile" + "/"
									+ user.getUserId() + "/" + fileName;
							ser.saveTaskFileMessage(task, fileName, fileUrlPath, true);
							obj.put("code", 1);
						} catch (Exception e) {
							e.printStackTrace();
							obj.put("code", 0);
						}
					}
					obj.put("code", 1);
				}

				if (StringUtils.isNotBlank(obj.getString("code")) && obj.getString("code").equals("1")) {
					final Task t = task;
					final ManageUserInfo us = user;
					String message = "任务通知：" + us.getUserName() + " 新建了任务 ，任务名称为：" + t.getTaskTitle();
					new Thread(new Runnable() {
						@Override
						public void run() {
							ser.ImInform2App(message, t, us, 31);// 下发APP
						}
					}).start();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		renderJson(obj);
	}

	/**
	 * 删除任务 type 32
	 */
	public void deleteTask() {
		ManageUserInfo user = getUser();
		if (null == user) {
			user = getUser(getPara("userId"));
		}
		JSONObject obj = new JSONObject();
		obj.put("code", 0);
		Integer task_id = getParaToInt("task_id");
		Task task = ser.findTaskById(task_id);
		if (null != task) {
			Boolean flag = ser.deleteTask(task_id);
			if (flag) {
				obj.put("code", 1);

				final Task t = task;
				final ManageUserInfo us = user;
				String message = "任务通知：" + us.getUserName() + " 删除了任务 ，任务名称为：" + t.getTaskTitle();
				new Thread(new Runnable() {
					@Override
					public void run() {
						ser.ImInform2App(message, t, us, 31);// 下发APP
					}
				}).start();
			}

		}
		renderJson(obj);
	}

	/**
	 * APP添加任务 附件
	 */
	public void uploadFile() {
		ManageUserInfo user = getUser();
		if (null == user) {
			user = getUser(getPara("userId"));
		}
		UploadFile uploadFile = getFile();// 这句已经完成上传，一下是从上传的文件进行重新上传到
		String fileName = uploadFile.getOriginalFileName();
		File file = uploadFile.getFile();

		String path = PathKit.getWebRootPath();
		String[] fileNameArr = null;

		try {
			if (fileName.contains(".")) {
				fileNameArr = fileName.split("\\.");
			}
			fileName = fileNameArr[0] + "_" + System.currentTimeMillis() + "." + fileNameArr[1];
		} catch (Exception e) {
			e.printStackTrace();
		}

		String dirs = path + File.separator + "taskFile" + "/" + user.getUserId();
		String dir = dirs + "/" + fileName;// 上传的路径
		File fileDir = new File(dirs);
		if (!fileDir.exists())
			fileDir.mkdirs();
		String fileUrlPath = getWebRootAddress() + File.separator + "taskFile" + "/" + user.getUserId() + "/"
				+ fileName;

		File to = new File(dir);
		try {
			to.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		boolean re = ser.fileChannelCopy(file, to);
		file.delete();
		JSONObject jo = new JSONObject();
		TaskLog taskFile = null;
		if (re) {
			taskFile = ser.saveTaskFileMessage(null, fileName, fileUrlPath, false);// 保存附件信息到
																					// 附件表里
																					// ，等到保存任务的时候，再更新此条对应关系
			jo.put("code", 1);
		} else {
			jo.put("code", 0);
		}
		jo.put("data", taskFile);
		renderJson(jo);
	}

	/**
	 * 新增任务 app 编辑任务进度（目前APP只能编辑进度 ，使用此接口发送IM）
	 */
	public void appEditTask() {
		ManageUserInfo user = getUser();
		JSONObject jo = new JSONObject();
		jo.put("code", 0);
		if (null == user) {
			user = getUser(getPara("userId"));
		}
		String jsonString = getPara("data");
		JSONObject jsonObject = JSONObject.parseObject(jsonString);
		Task task = ser.saveTask(jsonObject, user, true);
		if (null != task) {
			jo.put("code", 1);

			final Task t = task;
			final ManageUserInfo us = user;
			String message = "任务通知：" + us.getUserName() + " 编辑了任务 ，任务名称为：" + t.getTaskTitle();
			new Thread(new Runnable() {
				@Override
				public void run() {
					ser.ImInform2App(message, t, us, 31);// 下发APP
				}
			}).start();
		}
		renderJson(jo);

	}

	/**
	 * 更新任务进度
	 * 
	 */
	public void appUpdataTaskProgress() {
		ManageUserInfo user = getUser();
		JSONObject jo = new JSONObject();
		jo.put("code", 0);
		if (null == user) {
			user = getUser(getPara("userId"));
		}
		String jsonString = getPara("data");
		JSONObject jsonObject = JSONObject.parseObject(jsonString);
		Task task = ser.updataTaskProgress(jsonObject, user, true);
		if (null != task) {
			jo.put("code", 1);

			final Task t = task;
			final ManageUserInfo us = user;
			String message = "任务通知：" + us.getUserName() + " 编辑了任务 ，任务名称为：" + t.getTaskTitle();
			new Thread(new Runnable() {
				@Override
				public void run() {
					ser.ImInform2App(message, t, us, 31);// 下发APP
				}
			}).start();
		}
		renderJson(jo);

	}
	
	public void addTopicalInfo() {
		ManageUserInfo user = getUser();
		int status = -1;
		String message = "查询失败";
		JSONObject result = null;
		String name = getPara("top_name");
		try {
			result = ser.addTopicInfo(name, user);
			status = 1;
			message = "success";
		} catch (Exception e) {
			e.printStackTrace();
		}
		renderJson(new ResultMessageVo(status, message, result));
	}

}
