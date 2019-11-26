package wg.user.mobileimsdk.server.interceptor;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;

/**
 * 
 * @description 解决跨域问题
 * @author ZSJ
 * @date 2019年5月7日
 */
public class SolveJsonpInterceptor implements Interceptor {

	@Override
	public void intercept(Invocation inv) {
		Controller controller = inv.getController();
		controller.getResponse().addHeader("Access-Control-Allow-Origin", "*");
		inv.invoke();
	}

	/*
	 * @Override public void intercept(Invocation inv) { CrossOrigin cross =
	 * inv.getController().getClass().getAnnotation(CrossOrigin.class); if (cross !=
	 * null) { handler(inv.getController().getResponse()); inv.invoke(); return; }
	 * cross = inv.getMethod().getAnnotation(CrossOrigin.class); if (cross != null)
	 * { handler(inv.getController().getResponse()); inv.invoke(); return; }
	 * inv.invoke(); }
	 * 
	 * private void handler(HttpServletResponse response) {
	 * response.setHeader("Access-Control-Allow-Origin", "*");
	 * response.setHeader("Access-Control-Allow-Methods", "POST, GET");
	 * response.setHeader("Access-Control-Max-Age", "3600");
	 * response.setHeader("Access-Control-Allow-Headers",
	 * "Content-Type, Access-Control-Allow-Headers, Authorization, X-Requested-With"
	 * ); }
	 */

}
