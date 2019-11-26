package wg.media.screen.fm.report;


public interface IJiGuangPushService {

	String JpushSendMessage(String fingerPrint, String fromUserId, String toUserId, String fromUserName,
			String toUserName, String typeu, String message);
	
	String sendMessage(String fromUserId,String toUserId,String fromUserName,String toUserName,
			String type,String roomId,String message);
	
}
