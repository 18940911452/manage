package wg.user.mobileimsdk.server.spring;

import java.io.IOException;

import wg.openmob.mobileimsdk.server.listener.ServerLauncherImpl;
import wg.openmob.mobileimsdk.server.listener.OffLineMessageListerImpl;
import wg.openmob.mobileimsdk.server.listener.OffLineMessageListerImpl.MyThread;

public class SpringLoader  {
	
		public SpringLoader(){// 实例化后记得startup哦，单独startup()的目的是让调用者可以延迟决定何时真正启动IM服务
	    	try {
	    		final ServerLauncherImpl sli = new ServerLauncherImpl();
		    	// 启动MobileIMSDK服务端的Demo
		    	try {
					sli.startup();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    	// 加一个钩子，确保在JVM退出时释放netty的资源
		    	Runtime.getRuntime().addShutdownHook(new Thread() {
		    		@Override
		    		public void run() {
		    			sli.shutdown();
		    		}
		    	});
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	//定时检测在线人列表，然后将离线消息推送给用户
	    	OffLineMessageListerImpl.MyThread thread = new OffLineMessageListerImpl.MyThread();
		    thread.start();
	    	
		}

	

}
