package wg.user.mobileimsdk.server.util;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Connection.Method;

public class HttpClientUtils {

	/**
	 * get请求
	 * @param url
	 * @param param
	 * @return
	 */
	public static String httpGet(String url ,Map<String,String> param) {
		return httpGet(url,param,Collections.<String,String>emptyMap());
	}
	public static String httpGet(String url ,Map<String,String> param, Map<String,String> headers) {
		HttpClient client = HttpClients.createDefault();
		HttpUriRequest request = buildHttpUriRequest(url,Method.GET,param,headers);
		try {
			HttpResponse response = client.execute(request);
			HttpEntity entity = response.getEntity();
			return EntityUtils.toString(entity);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}
	
	
	/**
	 * post请求
	 * @param url
	 * @param param
	 * @return
	 */
	public static String httpPost(String url ,Map<String,String> param,Map<String,String> headers) {
		HttpClient client = HttpClients.createDefault();
		HttpUriRequest request = buildHttpUriRequest(url,Method.POST,param,headers);
		try {
			HttpResponse response = client.execute(request);
			HttpEntity entity = response.getEntity();
			return EntityUtils.toString(entity);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}
	/**
	 * post请求
	 * @param url
	 * @param param
	 * @return
	 */
	public static String httpPost(String url ,Map<String,String> param) {
		return httpPost(url,param,Collections.<String,String>emptyMap());
	}


	private static HttpUriRequest buildHttpUriRequest(String url, Method method, Map<String, String> param,Map<String,String> headers) {
		RequestBuilder setConfig = RequestBuilder.create(method.toString()).setUri(url).setConfig(getConfig());
		if(param!=null && param.size()>0) {
			for(Map.Entry<String, String > entry:param.entrySet()) {
				setConfig.addParameter(entry.getKey(), entry.getValue());
			}
		}
		if(headers!=null && headers.size()>0) {
			for(Map.Entry<String, String > entry:headers.entrySet()) {
				setConfig.addHeader(entry.getKey(),entry.getValue());
			}
		}
		
		HttpUriRequest request	= setConfig.build();
		return request;
	}
	
	

	private static RequestConfig getConfig() {
		RequestConfig requestConfig = RequestConfig.custom().setConnectionRequestTimeout(300000) // 从连接池中获取连接的超时时间
				.setConnectTimeout(300000).setSocketTimeout(300000) // socket读数据超时时间：从服务器获取响应数据的超时时间
				.build();
		return requestConfig;
	}

}
