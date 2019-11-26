package wg.user.mobileimsdk.server.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Map.Entry;

public class SendPost {
	
	
	public static  void sendPost(String urlParam, Map<String, Object> params) {  
        // 构建请求参数  
        StringBuffer sbParams = new StringBuffer();  
        if (params != null && params.size() > 0) {  
            for (Entry<String, Object> e : params.entrySet()) {  
                sbParams.append(e.getKey());  
                sbParams.append("=");  
                sbParams.append(e.getValue());  
                sbParams.append("&");
            }
        }
        String parma=sbParams.toString();
        HttpURLConnection conn=null;
        URL url=null;
        OutputStream os=null;
        BufferedReader br=null;
        //1, 得到URL对象 
        try {
		         url = new URL(urlParam); 
		        //2, 打开连接 
		         conn = (HttpURLConnection) url.openConnection(); 
		        //3, 设置提交类型 
					conn.setRequestMethod("POST");
		        //4, 设置允许写出数据,默认是不允许 false 
		        conn.setDoOutput(true); 
		        conn.setDoInput(true);//当前的连接可以从服务器读取内容, 默认是true 
		        //5, 获取向服务器写出数据的流 
		         os = conn.getOutputStream(); 
		        //参数是键值队  , 不以"?"开始 
		        os.write(parma.getBytes()); 
		        //os.write("googleTokenKey=&username=admin&password=5df5c29ae86331e1b5b526ad90d767e4".getBytes()); 
		        os.flush();
		        //6, 获取响应的数据 
		       //得到服务器写回的响应数据 
		         br=new BufferedReader(new InputStreamReader(conn.getInputStream(),"UTF-8"));
		        String str = br.readLine();
		        System.out.println("响应内容为:  " + str);
        } catch (Exception e) {
			e.printStackTrace();
			conn.disconnect();
			url=null;
			try {
				os.close();
				br.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} finally{
			conn.disconnect();
			url=null;
			try {
				os.close();
				br.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
    }   
	

}
