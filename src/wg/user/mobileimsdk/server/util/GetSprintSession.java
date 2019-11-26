package wg.user.mobileimsdk.server.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpStatus;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

//采编系统模拟登录util
public class GetSprintSession {

    private static RequestConfig requestConfig = null;
    static {
        // 设置请求和传输超时时间
        requestConfig = RequestConfig.custom().setSocketTimeout(2000).setConnectTimeout(2000).build();
    }
    public static JSONObject httpPost(CloseableHttpClient httpClient,String url, String strParam) {
        // post请求返回结果
        JSONObject jsonResult = new JSONObject();
        HttpPost httpPost = new HttpPost(url);
        httpPost.setConfig(requestConfig);
		httpPost.setHeader("User-Agent","mobile");
        try {
            if (StringUtils.isNotBlank(strParam)) {
                // 解决中文乱码问题
                StringEntity entity = new StringEntity(strParam, "utf-8");
                entity.setContentEncoding("UTF-8");
                entity.setContentType("application/x-www-form-urlencoded");
                httpPost.setEntity(entity);
            }
            CloseableHttpResponse response = httpClient.execute(httpPost);
            // 请求发送成功，并得到响应
            jsonResult.put("statusCode",response.getStatusLine().getStatusCode());
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                // 读取服务器返回过来的字符串数据
                jsonResult.put("result",EntityUtils.toString(response.getEntity(), "utf-8"));
            }else if(response.getStatusLine().getStatusCode() == HttpStatus.SC_MOVED_TEMPORARILY){
                Header[] locations = response.getHeaders("Location");
                jsonResult.put("location",locations[0].getValue());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            httpPost.releaseConnection();
        }
        return jsonResult;
    }

    public static JSONObject httpGet(CloseableHttpClient httpClient,String url,String JSESSIONID) {
        // get请求返回结果
        JSONObject jsonResult = new JSONObject();
        // 发送get请求
        HttpGet request = new HttpGet(url);
        request.setConfig(requestConfig);
		request.setHeader("User-Agent","mobile");
        try {
            if(JSESSIONID!=null){
                request.addHeader("Cookie","JSESSIONID="+JSESSIONID);
            }
            CloseableHttpResponse response = httpClient.execute(request);
            jsonResult.put("statusCode",response.getStatusLine().getStatusCode());
            // 请求发送成功，并得到响应
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                jsonResult.put("result",EntityUtils.toString(response.getEntity(), "utf-8"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            request.releaseConnection();
        }
        return jsonResult;
    }

    public static boolean   checkSession(CloseableHttpClient httpClient,String checkUrl,String jSESSIONID){
        boolean flag=false;
        if(StringUtils.isNotBlank(jSESSIONID)){
            JSONObject jsonObject = httpGet(httpClient, checkUrl,jSESSIONID);
            flag=jsonObject.getInteger("statusCode")==HttpStatus.SC_OK;
        }
        return flag;
    }

    public static JSONObject   getSession(String url,String username,String password,String mobileJSESSIONID,String sprintJSESSIONID){
        CookieStore cookieStore = new BasicCookieStore();
        CloseableHttpClient httpClient = HttpClients.custom().setDefaultCookieStore(cookieStore).build();
        JSONObject jsonObject =new JSONObject();
        try {
			username= URLEncoder.encode(username,"UTF-8");
			password= URLEncoder.encode(password,"UTF-8");
            String  loginUrl=url+"/uum/login/process?username="+username+"&password="+password+"&vcode=zkwg";
            String  mobileUrl=url+"/mobile/";
            String  sprintUrl=url+"/sprint/";
            String  checkUrl="security/roles/list";
            if(!checkSession(httpClient,mobileUrl+checkUrl,mobileJSESSIONID)&&!checkSession(httpClient,sprintUrl+checkUrl,sprintJSESSIONID)){
                //      login接口
                JSONObject jsonObject2 = httpPost(httpClient,loginUrl, "");
                String location = jsonObject2.getString("location");
                if(!location.contains("failure")){
                    httpGet(httpClient, location,null);
                    //      mobile接口
                    if(mobileJSESSIONID!=null)httpGet(httpClient, mobileUrl,null);
                    //      sprint接口
                    if(sprintJSESSIONID!=null)httpGet(httpClient, sprintUrl,null);
                    cookieStore.getCookies().forEach(c->jsonObject.put(c.getPath(),c.getValue()));
                }
            }else{
                jsonObject.put("/mobile",mobileJSESSIONID);
                jsonObject.put("/sprint",sprintJSESSIONID);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return jsonObject;
    }
    //获取/mobile的JSESSIONID
    public static String   getMobileSession(String url,String username,String password,String mobileJSESSIONID){
        return getSession(url,username,password,mobileJSESSIONID==null?" ":mobileJSESSIONID,null).getString("/mobile");
    }
    //获取/sprint的JSESSIONID
    public static String   getSprintSession(String url,String username,String password,String sprintJSESSIONID){
        return getSession(url,username,password,null,sprintJSESSIONID==null?" ":sprintJSESSIONID).getString("/sprint");
    }

	/**
	 * 退出采编
	 * @param mobileJSESSIONID
	 * @return
	 */
	public static String logout( String mobileJSESSIONID) {
		String url = "http://hongqi.wengetech.com:9080/mobile/sso/logout";
		URL serverUrl =  null;
		HttpURLConnection conn = null;
		try {
			serverUrl = new URL(url);
			conn = (HttpURLConnection) serverUrl.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("User-Agent","mobile");
			conn.setRequestProperty("Cookie", "JSESSIONID="+mobileJSESSIONID);
			conn.setInstanceFollowRedirects(false);
			conn.connect();
			//获取Location地址
			String location = conn.getHeaderField("Location");
			System.out.println(location);
			if(location.contains("oauth/logout")){
				serverUrl = new URL(location);
				conn = (HttpURLConnection) serverUrl.openConnection();
				conn.setRequestMethod("GET");
				conn.setRequestProperty("User-Agent","mobile");
				conn.setInstanceFollowRedirects(false);
				conn.connect();
				System.out.println(conn.getHeaderField("Location"));
			}else if(location.contains("/login")){
				System.out.println("Cookie已失效！");
			}else {
				System.out.println("退出失败！");
				return "fail";
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return "success";
	}

	/**
	 * 判断登录采编账号session是否过期
	 * @param session
	 * @return
	 */
	public static JSONObject checkSession(String session) {
		String url =  "http://hongqi.wengetech.com:9080/mobile/rest/stories/sensitiveword/check?content=1";
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("check", true);
		URL serverUrl =  null;
		HttpURLConnection conn = null;
		try {
			serverUrl = new URL(url);
			conn = (HttpURLConnection) serverUrl.openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty("User-Agent","mobile");
			conn.setRequestProperty("Cookie", "JSESSIONID="+session);
			conn.setInstanceFollowRedirects(false);
			conn.connect();
			//获取Location地址
			String location = conn.getHeaderField("Location");
			if(location != null && location.contains("/login")){
				System.out.println("Cookie已失效！");
				jsonObject.put("check", false);
			}
		} catch (Exception e) {
			e.printStackTrace();
			jsonObject.put("check", false);
		}
		return jsonObject;
	}

    public static void main(String[] args) {
//        String url="http://hongqi.wengetech.com:9080";
        String url1="http://hongqi.wengetech.com:9080";
//        String url2="http://127.0.0.1:9080";
        String username="panggongxiang2";
        String password="123";
//        JSONObject jsonObject = getSession(url, username,
//                password, "90F34D8FAB5DFF1F3E9B436299A9F119","90F34D8FAB5DFF1F3E9B436299A9F119");
//        System.out.println(jsonObject);

        String mobileJSESSIONID = getMobileSession(url1, username,
                password, null);
//        String sprintJSESSIONID = getSprintSession(url, username,
//                password, "20164400D1FE6234BC606D8B5AF18794");
//        System.out.println(sprintJSESSIONID);

        if(StringUtils.isBlank(mobileJSESSIONID)){
            System.out.println("用户名或密码错误");
        }else{
            System.out.println(mobileJSESSIONID);
        }

    }
}