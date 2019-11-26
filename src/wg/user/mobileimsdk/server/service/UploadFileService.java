package wg.user.mobileimsdk.server.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.UUID;

public class UploadFileService {
	public static UploadFileService ser=new UploadFileService();
		
	 public boolean fileChannelCopy(File s, File t) {

	        FileInputStream fi = null;
	        FileOutputStream fo = null;
	        FileChannel in = null;
	        FileChannel out = null;
	        try {
	            fi = new FileInputStream(s);
	            fo = new FileOutputStream(t);
	            in = fi.getChannel();// 得到对应的文件通道
	            out = fo.getChannel();// 得到对应的文件通道
	            in.transferTo(0, in.size(), out);// 连接两个通道，并且从in通道读取，然后写入out通道
	            return true;
	        } catch (IOException e) {
	            e.printStackTrace();
	            return false;
	        } finally {
	            try {
	                fi.close();
	                in.close();
	                fo.close();
	                out.close();
	            } catch (IOException e) {
	                e.printStackTrace();
	                return false;
	            }
	            
	        }
	    }
	
	 
	 /**
	  * 
	  * @method:newName 
	  * @describe:生成新名字
	  * @author:  gongxiangPang 
	  * @param :TODO
	  * @param filename
	  * @return
	  */
	 public String newName(String filename){
		 
		 String uuid = UUID.randomUUID().toString();
		 String suffix = filename.substring(filename.lastIndexOf(".") + 1);
		 return uuid+"."+suffix;
	 }
	
}
