package wg.openmob.mobileimsdk.server.listener;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mysql.cj.util.TimeUtil;

import wg.openmob.mobileimsdk.server.processor.LogicProcessor;
import wg.openmob.mobileimsdk.server.processor.OnlineProcessor;

public class OffLineMessageListerImpl  {
	private static Logger logger = LoggerFactory.getLogger(LogicProcessor.class);  
	
	 public static void main(String[] args){    
	      
	        //使用线程的sleep方法，让线程休眠来达到按时执行的效果
	        MyThread thread = new MyThread();
	        thread.start();
	    }   
	    // 使用内部类，来实现线程执行后，每隔两秒继续执行一次
	    public static class MyThread extends Thread{
	        public void run(){
	        	int usersNum=OnlineProcessor.onlineSessions.size();
	    		//logger.info("【@】当前在线用户共("+usersNum+")人------------------->");
	    		if(usersNum>0){
	    			for(String key : OnlineProcessor.onlineSessions.keySet()){
	    				//logger.info(" 【##########】   当前在线用户 > user_id="+key+",session="+OnlineProcessor.onlineSessions.get(key).remoteAddress());
	    				ServerEventListenerImpl.sendOffLineMessage(key);//检测用户消息是否有离线消息，如果有则推送给用户
	    			}
	    		}
	            MyThread thread = new MyThread();
	            try {
	                Thread.sleep(1000);//定时请求的时间
	                thread.start();
	            } catch (InterruptedException e) {
	                e.printStackTrace();
	            }
	        }
	    }
	 
}
