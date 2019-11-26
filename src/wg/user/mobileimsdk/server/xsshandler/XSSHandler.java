package wg.user.mobileimsdk.server.xsshandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jfinal.handler.Handler;

public class XSSHandler extends Handler {

	// 排除的url，使用的target.startsWith匹配的
	private String excludePattern;

	/**
	 * 忽略列表，使用正则
	 * 
	 * @param exclude
	 */
	public XSSHandler() {
	}

	// public XSSHandler(String excludePattern) {
	// this.excludePattern = excludePattern;
	// }

	@Override
	public void handle(String target, HttpServletRequest request, HttpServletResponse response, boolean[] isHandled) {

		// 带.表示非action请求，忽略（其实不太严谨，如果是伪静态，比如.html会被错误地排除）；匹配excludePattern的，忽略
		// if (target.indexOf(".") == -1 && !(!StringUtils.isBlank(excludePattern) &&
		// pattern.matcher(target).find())) {
		request = new XSSHttpServletRequestWrapper(request);
		// }
		// 别忘了
		next.handle(target, request, response, isHandled);

	}
}
