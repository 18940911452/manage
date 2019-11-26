package wg.media.screen.fm.common;

import java.util.Map;


/**
 * 调用openApi接口
 * @author 18170
 *
 */
public interface OpenAPIInvoker {
	String invokeOpenAPI(Map<String, String> requestParams,Map<String,String > headers,String method,OpenAPIConfig openAPIConfigByStstemName);
}
