package wg.user.mobileimsdk.server.listener;

import java.util.HashMap;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

public class SSOListener implements HttpSessionListener{

    private static HashMap<String,HttpSession> idSession =new HashMap<>();
	@Override
	public void sessionCreated(HttpSessionEvent se) {
		HttpSession session = se.getSession();
		idSession.put(session.getId(), session);
		
	}

	@Override
	public void sessionDestroyed(HttpSessionEvent se) {
		String id = se.getSession().getId();
		idSession.remove(id);
	}
	
	
	public static HttpSession getSessionById(String sessionId) {
		return idSession.get(sessionId);
	}

}
