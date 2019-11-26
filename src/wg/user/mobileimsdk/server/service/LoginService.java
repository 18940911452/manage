package wg.user.mobileimsdk.server.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.kit.HttpKit;
import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wg.media.screen.fm.model.commandscreencommon.ManageDepart;
import wg.media.screen.fm.model.commandscreencommon.ManageInstitution;
import wg.media.screen.fm.model.commandscreencommon.ManageUserInfo;
import wg.media.screen.fm.model.commandscreencommon.MediaStoreInfo;
import wg.media.screen.fm.model.iam.IamCaibianUser;
import wg.media.screen.fm.model.iam.IamTenant;
import wg.user.mobileimsdk.server.util.GetSprintSession;
import wg.user.mobileimsdk.server.util.HttpClientUtils;
import wg.user.mobileimsdk.server.util.MD5Util;

public class LoginService {
	private static Logger logger = LoggerFactory.getLogger(LoginService.class);
	public static LoginService ser;
	static Prop pro;
	public static String masterSecret;
	public static String appKey;

	static {
		LoginService.ser = new LoginService();
		LoginService.pro = PropKit.use("a_little_config_ms.txt");
		LoginService.masterSecret = LoginService.pro.get("appCertificate", "946242e3b76ce11379794ab7");
		LoginService.appKey = LoginService.pro.get("appID", "942d333e49c94d01c34ad78e");
	}

	/**
	 * 
	 * @method:Loginvalidate
	 * @describe:验证登录是否争取
	 * @author: gongxiangPang
	 * @param :TODO
	 * @param name
	 * @param password
	 * 白真钰修改模拟登陆采编接口
	 * @return 0用户存在 1026 用户不存在 1027 密码错误
	 */
	public JSONObject Loginvalidate(String name, String password, String pth) {
		logger.info("入参:{},{}",name,password);
		JSONObject result = new JSONObject();
		int re = 1026;
		String pwd = MD5Util.string2MD5(password);
		String sql = "select mui.*,minst.caibian_host from manage_user_info as mui left join manage_depart as mde on mui.dep_id=mde.dep_id left join manage_institution as minst on mde.inst_id=minst.id where mui.user_name='"+name+"' and mui.password= '"+pwd+"'  and mui.status = 1";
		ManageUserInfo manageUserInfo = ManageUserInfo.dao.findFirst(sql);
		if (manageUserInfo != null) {
			String password2 = manageUserInfo.getPassword();
			if (password2 != null) {
				String convertMD5 = MD5Util.string2MD5(password);
				if (password2.equals(convertMD5)) {
					re = 0;
					JSONObject temp = new JSONObject();
					temp.put("userId", manageUserInfo.get("user_id"));
					temp.put("userIcon", pth + manageUserInfo.get("icon"));
					temp.put("depId", manageUserInfo.get("dep_id"));
					temp.put("tel", manageUserInfo.get("tel"));
					temp.put("nickName", manageUserInfo.get("nick_name"));
					temp.put("userName", manageUserInfo.get("user_name"));
					temp.put("longitude", manageUserInfo.getLongitude());
					temp.put("latitude", manageUserInfo.getLatitude());
					temp.put("sex", manageUserInfo.get("sex"));
					temp.put("nation", manageUserInfo.get("nation"));
					temp.put("email", manageUserInfo.get("email"));
					temp.put("birthdate", manageUserInfo.get("birthdate"));
					temp.put("time", System.currentTimeMillis());
					temp.put("caibianHost", manageUserInfo.get("caibian_host"));
					temp.put("parent_id", "");
					if(name!=null){
						//根据租户id查出机构id
						Integer dep_id=manageUserInfo.get("dep_id");
						String sql3="select inst_id  from manage_depart where dep_id ='"+dep_id+"' ";
						ManageDepart depart=ManageDepart.dao.findFirst(sql3);
						Integer inst_id=depart.getInstId();
						temp.put("insId", inst_id); // 单位id
						//根据机构id查询媒资库url，如果没有此机构，默认查红旗机构对应的媒资库url
						String sqlMzk=" select url from media_store_info where inst_id = '"+inst_id+"' ";
						MediaStoreInfo mediaStoreInfo=MediaStoreInfo.dao.findFirst(sqlMzk);
						if(mediaStoreInfo==null){
							String sqlMzHongQi=" select url from media_store_info where inst_id = 1 ";
							MediaStoreInfo mediaStoreInfo1=MediaStoreInfo.dao.findFirst(sqlMzHongQi);
							String mzUrlHongQi=mediaStoreInfo1.getUrl();
							String mzPicUrl=mediaStoreInfo1.getResourceUrl();
							temp.put("mzUrl", mzUrlHongQi);
							temp.put("mzPicUrl", mzPicUrl);
						}
						if(mediaStoreInfo!=null){
							String mzUrl=mediaStoreInfo.getUrl();
							String mzPicUrl=mediaStoreInfo.getResourceUrl();
							temp.put("mzUrl", mzUrl);
							temp.put("mzPicUrl", mzPicUrl);
						}
						//根据机构用户名查询tenantid
						String sql4="select name,tenent_id from manage_institution where id = '"+inst_id+"'";
						ManageInstitution institution=ManageInstitution.dao.findFirst(sql4);
						Integer tenant_id=institution.getTenentId();
						//根据tenantid和用户名去iam_caibian_user 获取采编用户  如果有 就模拟登陆 没有就二次登陆
						String sql6="select * from iam_caibian_user where tenent_id ='"+tenant_id+"' and user_name ='"+name+"'";
						IamCaibianUser caibianUser=IamCaibianUser.dao.findFirst(sql6);
						if(caibianUser!=null){
							String cai_name=caibianUser.getCaibianUsername();
							String cai_pwd =caibianUser.getCaibianPwd();
							String caibian_pwd=null;
							try {
								caibian_pwd = new String(java.util.Base64.getDecoder().decode(cai_pwd),"UTF-8");
								String url="http://hongqi.wengetech.com:9080";
								String mobileJSESSIONID = null;
								try {
									mobileJSESSIONID = GetSprintSession.getMobileSession(url, cai_name,
											caibian_pwd, null);
								}catch (Exception e){
									e.printStackTrace();
								}
								if(StringUtils.isNotBlank(mobileJSESSIONID)){
									temp.put("moni", mobileJSESSIONID);
									String caibian="JSESSIONID="+mobileJSESSIONID;
									String url2="http://hongqi.wengetech.com:9080/mobile/userinfo/rest/getInfo";
									HttpGet httpget = new HttpGet(url2);
									httpget.setHeader("Cookie", caibian);
									CloseableHttpClient httpclient = HttpClients.createDefault();
									CloseableHttpResponse response = null;
									try {
										response = httpclient.execute(httpget);
										HttpEntity entity = response.getEntity();
										String a=EntityUtils.toString(entity);
										JSONObject jsonObject=JSONObject.parseObject(a);
										temp.put("cbUserId",jsonObject.get("uuid"));
//										Object object = jsonObject.get("role");
//										String b=object.toString();
//										JSONObject jsonObject1=JSONObject.parseObject(b);
										Object o=jsonObject.get("group");
										String c=o.toString();
										JSONObject jsonObject2=JSONObject.parseObject(c);
										temp.put("cbGroupId",jsonObject2.get("uuid"));
									} catch (IOException e) {
										e.printStackTrace();
									}
									finally {
										try {
											response.close();
										} catch (IOException e) {
											e.printStackTrace();
										}finally {
											try {
												httpclient.close();
											} catch (IOException e) {
												e.printStackTrace();
											}
										}
									}
								}
								else{
									temp.put("moni", null);
									temp.put("cbGroupId","");
									temp.put("cbUserId","");
								}

							}
							catch (UnsupportedEncodingException e) {
									// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						if(caibianUser==null){
							temp.put("moni", null);
							temp.put("cbGroupId","");
							temp.put("cbUserId","");
						}
						
					}
					
					try {
						String token = SignalingTokenService.ser.getToken(LoginService.appKey,
								LoginService.masterSecret, manageUserInfo.get("user_id").toString(), 1641898899L);
						temp.put("token", token);
						String t = manageUserInfo.getToken();
						if (t == null || ("").equals(t)) {
							Db.update("update manage_user_info set token = '" + token + "' where user_id = '"
									+ manageUserInfo.get("user_id") + "'");
							temp.put("token", token);
						}
					} catch (NoSuchAlgorithmException e) {
						e.printStackTrace();
					}
					manageUserInfo.remove("password");
					result.put("user", manageUserInfo);
					result.put("result", temp);
				} else {
					re = 1027;
					result.put("result", "密码错误");
				}
			}
		} else {
			re = 1026;
			result.put("result", "用户名或密码错误");
		}
		result.put("code", re);
		result.put("user", manageUserInfo);
		return result;
	}

	
	public JSONObject LoginvalidateByname(String name,String pth) {
		JSONObject result = new JSONObject();
		int re = 1026;
		String sql = "select * from manage_user_info where user_name=? and status = 1";
		ManageUserInfo manageUserInfo = ManageUserInfo.dao.findFirst(sql, new Object[] { name });
		Integer dep_id=manageUserInfo.get("dep_id");
		String sql2 ="select  inst_id from manage_depart where dep_id= '"+dep_id+"'";
		ManageDepart depart=ManageDepart.dao.findFirst(sql2);
		Integer ins_id=depart.get("inst_id");
		System.out.println("ins_id:"+ins_id);
		if (manageUserInfo != null) {
					re = 0;
					JSONObject temp = new JSONObject();
					temp.put("userId", manageUserInfo.get("user_id"));
					temp.put("userIcon", pth + manageUserInfo.get("icon"));
					temp.put("depId", manageUserInfo.get("dep_id"));
					temp.put("tel", manageUserInfo.get("tel"));
					temp.put("nickName", manageUserInfo.get("nick_name"));
					temp.put("userName", manageUserInfo.get("user_name"));
					temp.put("longitude", manageUserInfo.getLongitude());
					temp.put("latitude", manageUserInfo.getLatitude());
					temp.put("sex", manageUserInfo.get("sex"));
					temp.put("insId",ins_id); // 单位id
					temp.put("nation", manageUserInfo.get("nation"));
					temp.put("email", manageUserInfo.get("email"));
					temp.put("birthdate", manageUserInfo.get("birthdate"));
					temp.put("time", new Date().getTime());

					try {
						String token = SignalingTokenService.ser.getToken("e54476871b6b4496b7ce21b2b56bbfae","be87882ae3aa43c1968ff0dc6e192e80", manageUserInfo.get("user_id").toString(),1641898899);
						temp.put("token", token);
						String t = manageUserInfo.getToken();
						if (t == null || ("").equals(t)) {
							Db.update("update manage_user_info set token = '" + token + "' where user_id = '"
									+ manageUserInfo.get("user_id") + "'");
							temp.put("token", token);
						}
					} catch (NoSuchAlgorithmException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					manageUserInfo.remove("password");
					result.put("result", temp);
		} else {
			re = 1026;
			result.put("result", "用户名或密码错误");
		}
		result.put("code", re);
		result.put("user", manageUserInfo);
		return result;
	}
	
	public JSONObject Loginval(String name) {
		// TODO Auto-generated method stub
		JSONObject jsonObject = new JSONObject();
		String sql = "select * from manage_user_info where user_id=? and status = 1";
		ManageUserInfo manageUserInfo = ManageUserInfo.dao.findFirst(sql, new Object[] { name });
		if (manageUserInfo == null) {
			jsonObject.put("code", 1026);
		} else {
			jsonObject.put("code", 0);

		}
		return jsonObject;
	}

	public JSONObject getServerTime() {
		// TODO Auto-generated method stub
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("time", new Date().getTime());
		return jsonObject;
	}

	public JSONObject version() {

		JSONObject rt = new JSONObject();

		return rt;
	}

	public JSONObject SignOutLogin(String moni) {
		JSONObject jsonObject=new JSONObject();
		if(StrKit.notBlank(moni)){
			String a=GetSprintSession.logout(moni);
			System.out.println("moni = " + a);
			if("success".equals(a)){
				jsonObject.put("status",1);
				return jsonObject;
			}else{
				jsonObject.put("status",-1);
				return jsonObject;
			}
		}
		jsonObject.put("status",0);
		return jsonObject;
	}

	public JSONObject IfSeesionInvalid(String cbSession) {
		JSONObject jsonObject=new JSONObject();
		for(int i=0;i<2;i++){
			jsonObject=GetSprintSession.checkSession(cbSession);
			if(i==1){
				break;
			}
		}
		return jsonObject;

	}
}
