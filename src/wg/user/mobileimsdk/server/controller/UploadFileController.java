package wg.user.mobileimsdk.server.controller;

import static java.lang.System.out;

import java.io.*;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.core.Controller;
import com.jfinal.kit.PropKit;
import com.jfinal.upload.UploadFile;

import wg.media.screen.fm.model.commandscreencommon.ImFileMange;
import wg.user.mobileimsdk.server.service.MessageService;
import wg.user.mobileimsdk.server.service.UploadFileService;
/**
 * 

 * @author DELL
 *
 */
public class UploadFileController extends Controller{
	public static String headAdress = PropKit.get("headAdress");
	public static String localHeadAdress = PropKit.get("localHeadAdress");
	public static final int BUFSIZE = 1024 * 1;
	public void index(){
		render("/loadtest.html");
	}
	
	public void uploadFileFromBreak(){
		String result="";
		String status="";
		UploadFile uploadFile=getFile();//这句已经完成上传，一下是从上传的文件进行重新上传到
        String fileName=uploadFile.getOriginalFileName();
        File file=uploadFile.getFile();
        long length = uploadFile.getFile().length();//
        String sizeStr=getPrintSize(length);
        String type=getPara("type","4");//类型  1图片   2语音  3文件    4视频 
        String index=getPara("index","1");//序号
        String maxIndex=getPara("maxIndex","1");//最大分割数
//        String format = new SimpleDateFormat("yyyyMMddHHmmssSSS") .format(new Date() );
//        String filenameOrigin=getPara("filename","");
        String typepath="";//图片路径
        if(type.equals("1")||type=="1"){
        	typepath="image";//图片路径
        }else if(type.equals("2")||type=="2"){
        	typepath="voice";//语音存储
        }else if(type.equals("4")||type=="4"){
        	typepath="video";//语音存储
        	
        }else{
        	typepath="file";//文件存储
        }
        
        String filename="";
        if(Integer.valueOf(maxIndex)>1){
        	filename=index+"#"+fileName;
        	out.println("11filename"+filename);
        }else{
        	filename=fileName;
			out.println("22filename"+filename);
        }
//     白真钰修改   String repath = getRequest().getSession().getServletContext().getRealPath("");
        String repath = localHeadAdress;
        
        if(repath != null && !repath.endsWith(File.separator)){
        	repath += File.separator;
        }

		String filename2=filename.substring(filename.indexOf("."),filename.length());
		out.println("截取后缀filename2"+filename2);
		UUID uuid = UUID.randomUUID();
		String str = uuid.toString();
		String uuidStr=str.replace("-", "");
		String filename3=uuidStr+filename2;
		out.println("filename3=="+filename3);
        String dir=repath+"chatsource"+File.separator+typepath+File.separator+filename3;//上传的路径
		System.out.println("dir===="+dir);

//      String basePath = getRequest().getScheme()+"://"+getRequest().getServerName()+":"+getRequest().getLocalPort()+getRequest().getContextPath()+"/"+"chatsource"+File.separator+typepath+File.separator+filename3;
//		System.out.println("新改basePath-getLocalPort()===="+basePath);
        String basePath =headAdress+"chatsource"+File.separator+typepath+File.separator+filename3;
		System.out.println("之前basePath-getLocalPort()===="+basePath);
        File to=new File(dir);
        try {
            to.createNewFile();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        boolean re = UploadFileService.ser.fileChannelCopy(file, to);
        file.delete();
        if(re){
        	//result=basePath;
        	result=basePath;
        	status="1";
        }else{
        	result="false";
        	status="-1";
        }
        Integer indexLast=Integer.valueOf(index);
        Integer maxIndexLast=Integer.valueOf(maxIndex);
        
        if(indexLast==maxIndexLast&&Integer.valueOf(maxIndex)>1){
        	result=MergedFile( fileName,Integer.valueOf(maxIndex) , type);
        	if(result.equals("false")){
        		status="-1";
        	}
        }
        
        JSONObject jo=new JSONObject();
        if(maxIndexLast>indexLast){
        	jo.put("result", "断点续传上传中，本次上传完毕："+index);
            jo.put("status", 0);//1上传成功 ,0 上传中   -1上传失败
            jo.put("message", "本次上传成功");//1上传成功 ,0 上传中   -1上传失败
            
        }else{
        	if(type.equals("4")||type=="4"){//获取视频预览图
        		String path = dir;
        		String imgName = result .substring(0,filename .lastIndexOf("."));
        		//String outImagePath = getRequest().getSession().getServletContext().getRealPath("/")+"chatsource"+File.separator + "video"+File.separator+"preimage"+File.separator+imgName+".jpg";
        		String videoImage="";
        		try {
        			//videoImage=preImage.randomGrabberFFmpegImage("F:\\1.mp4", 2);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        		jo.put("preImage", imgName+".jpg");
        		
        	}
        	jo.put("message", "success");
        	jo.put("result", result);
            jo.put("status", status);//1上传成功 ,0 上传中   -1上传失败
        }
        
        renderJson(jo);
    }
	/**
	 * 合并上传完的文件
	 * @return
	 */
	public String  MergedFile(String fileName,Integer maxIndex,String type){
		long startTime = System.currentTimeMillis();
        String typepath="";//图片路径
        if(type.equals("1")||type=="1"){
        	typepath="image";//图片路径
        }else if(type.equals("2")||type=="2"){
        	typepath="voice";//语音存储
        }else if(type.equals("4")||type=="4"){
        	typepath="video";//语音存储
        }else{
        	typepath="file";//文件存储
        }
		
		 String[] files=new String[maxIndex];
		 String dir=getRequest().getSession().getServletContext().getRealPath("")+File.separator+"chatsource"+File.separator+typepath+File.separator;//上传的路径
		 String basePath = getRequest().getScheme()+"://"+getRequest().getServerName()+":"+getRequest().getServerPort()+getRequest().getContextPath()+"/"+"chatsource"+File.separator+typepath+File.separator+fileName;;
		 String outFile=dir+fileName;
		 for (int i = 0; i < maxIndex; i++) {
			 String strName=dir+(i+1)+"#"+fileName;
			 files[i]=strName;
		 }
		 
		 FileChannel outChannel = null;
		  out.println("Merge " + Arrays.toString(files) + " into " + outFile);
		  try {
			   outChannel = new FileOutputStream(outFile).getChannel();
			   for(String f : files){
			    FileChannel fc = new FileInputStream(f).getChannel(); 
			    ByteBuffer bb = ByteBuffer.allocate(BUFSIZE);
			    while(fc.read(bb) != -1){
			     bb.flip();
			     outChannel.write(bb);
			     bb.clear();
			    }
			    fc.close();
			   
			   }
			   
			   out.println("Merged Success! ");
		  } catch (IOException ioe) {
			  ioe.printStackTrace();
			  return "false";
		  } finally {
			  try {if (outChannel != null) {outChannel.close();}} catch (IOException ignore) {}
			  	for (int i = 0; i < maxIndex; i++) {
					 String strName=dir+(i+1)+"#"+fileName;
					  File file = new File(strName);
				        if (!file.exists()) {
				            System.out.println("删除文件失败:" + fileName + "不存在！");
				        } else {
				        	file.delete();
				        }
				 }
			  
			  
		  }
		  long endTime = System.currentTimeMillis();
		  System.out.println(fileName+"合并运行时间:" + (endTime - startTime) + "ms");
		 return basePath;
	}
	
	
	/**
	 * 
	 * @method:uploadImg 
	 * @describe:上传文件，图片，文档，语音 带有后缀的
	 * @author:  gongxiangPang 
	 * @param type    1图片  2语音   3文件
	 * @param :TODO
	 */
	
	public void uploadFile(){
		String result="";
		UploadFile uploadFile=getFile();//这句已经完成上传，一下是从上传的文件进行重新上传到
        String fileName=uploadFile.getOriginalFileName();
        File file=uploadFile.getFile();
        String type=getPara("type","");
        
        String typepath="";//图片路径
        if(type.equals("1")||type=="1"){
        	typepath="image";//图片路径
        }else if(type.equals("2")||type=="2"){
        	typepath="voice";//语音存储
        }else if(type.equals("4")||type=="4"){
        	typepath="video";//语音存储
        }else{
        	typepath="file";//文件存储
        }
       
        String filename=UploadFileService.ser.newName(fileName);
//		String filename2=null;
//		try {
//			filename2 = URLEncoder.encode(filename,"UTF-8");
//			System.out.println("UTF-8+++filename2 = " + filename2);
//		} catch (UnsupportedEncodingException e1) {
//			e1.printStackTrace();
//		}
        String dir=getRequest().getSession().getServletContext().getRealPath("/")+"chatsource"+File.separator+typepath+File.separator+filename;//上传的路径
        String basePath = getRequest().getScheme()+"://"+getRequest().getServerName()+":"+getRequest().getServerPort()+getRequest().getContextPath()+"/"+"chatsource"+File.separator+typepath+File.separator+filename;;
        File to=new File(dir);
        try {
            to.createNewFile();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        boolean re = UploadFileService.ser.fileChannelCopy(file, to);
        file.delete();
        if(re){
        	result=basePath;
        }else{
        	result="false";
        }
        JSONObject jo=new JSONObject();
        jo.put("result", result);	
        renderJson(jo);
    }
	
	
	public void uploadFile_jsonp(){
		HttpServletResponse response = getResponse();
		String path = PropKit.get("localHeadAdress");
		response.setHeader("Access-Control-Allow-Origin", "*");//允许跨域访问的域，可以是通配符”*”；
		response.setHeader("Access-Control-Allow-Methods", "POST, GET");
		response.setHeader("Access-Control-Max-Age", "1800");
		response.setHeader("Access-Control-Allow-Headers", "x-requested-with");
		response.setHeader("Access-Control-Allow-Credentials", "true");
		String result="";
		UploadFile uploadFile=getFile();//这句已经完成上传，一下是从上传的文件进行重新上传到
        String fileName=uploadFile.getOriginalFileName();
        File file=uploadFile.getFile();
        long length = uploadFile.getFile().length();//
        //String sizeStr=getPrintSize(length);
        String type=getPara("typeu","");
        String typepath="";//图片路径
        if(type.equals("12")||type=="12"||type.equals("22")||type.equals("22")){
        	typepath="image";//图片路径
        }else if(type.equals("13")||type=="13"||type.equals("23")||type.equals("23")){
        	typepath="voice";//语音存储
        }else if(type.equals("14")||type=="14"||type.equals("24")||type.equals("24")){
        	typepath="video";//语音存储
        }else if(type.equals("25")||type=="25"||type.equals("15")||type.equals("15")){
        	typepath="file";//语音存储
        }else{
        	typepath="file";//文件存储
        }
       
        String filename=UploadFileService.ser.newName(fileName);
        
        String dir=path+File.separator+"chatsource"+"/"+typepath+"/"+filename;//上传的路径
//        String dir=getRequest().getSession().getServletContext().getRealPath("/")+"files/chatsource"+"/"+typepath+"/"+filename;//上传的路径
       System.out.println("上传文件路径----------------"+dir); 
       //String basePath = getRequest().getScheme()+"://"+getRequest().getServerName()+":"+getRequest().getServerPort()+getRequest().getContextPath()+"/"+"chatsource"+File.separator+typepath+File.separator+filename;;
        File to=new File(dir);
        try {
            to.createNewFile();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        boolean re = UploadFileService.ser.fileChannelCopy(file, to);
        file.delete();
        if(re){
        	result="chatsource"+File.separator+typepath+File.separator+filename;
        }else{
        	result="false";
        }
        JSONObject jo=new JSONObject();
        jo.put("path", "chatsource"+"/"+typepath+"/"+filename);	
        jo.put("filename", fileName);	
        jo.put("size", Long.valueOf(length));
        jo.put("typeu", type);
       // renderJson(jo);
        renderJson(jo.toJSONString());
    }
	
	public  String getPrintSize_detail(long size) {  
	    //如果字节数少于1024，则直接以B为单位，否则先除于1024，后3位因太少无意义  
	    if (size < 1024) {  
	        return String.valueOf(size) + "B"; 
	    } else {  
	        size = size / 1024;  
	    }  
	    //如果原字节数除于1024之后，少于1024，则可以直接以KB作为单位  
	    //因为还没有到达要使用另一个单位的时候  
	    //接下去以此类推  
	    if (size < 1024) {  
	        return String.valueOf(size) + "KB";  
	    } else {  
	        size = size / 1024;  
	    }  
	    if (size < 1024) {  
	        //因为如果以MB为单位的话，要保留最后1位小数，  
	        //因此，把此数乘以100之后再取余  
	        size = size * 100;  
	        return String.valueOf((size / 100)) + "."+ String.valueOf((size % 100)) + "MB";  
	    } else {  
	        //否则如果要以GB为单位的，先除于1024再作同样的处理  
	        size = size * 100 / 1024;  
	        return String.valueOf((size / 100)) + "."  
	                + String.valueOf((size % 100)) + "GB";  
	    }  
	}  
	public  String getPrintSize(long size) {  
	    //如果字节数少于1024，则直接以B为单位，否则先除于1024，后3位因太少无意义  
	    if (size < 1024) {  
	        return  "1KB"; 
	    } else { 
	        size = size / 1024;  
	    }  
	    //如果原字节数除于1024之后，少于1024，则可以直接以KB作为单位  
	    //因为还没有到达要使用另一个单位的时候  
	    //接下去以此类推  
	    if (size < 1024) {  
	    	if(size % 1024>0){
	    		 return String.valueOf(size+1) + "KB";  
	    	}else{
	    		 return String.valueOf(size) + "KB";  
	    	}
	       
	    } else {  
	        size = size / 1024;  
	    }  
	    if (size < 1024) {  
	        //因为如果以MB为单位的话，要保留最后1位小数，  
	        //因此，把此数乘以100之后再取余  
	        size = size * 100;  
	        return String.valueOf((size / 100)) + "."+ String.valueOf((size % 100)) + "MB";  
	    } else {  
	        //否则如果要以GB为单位的，先除于1024再作同样的处理  
	        size = size * 100 / 1024;  
	        return String.valueOf((size / 100)) + "."  
	                + String.valueOf((size % 100)) + "GB";  
	    }  
	}  
	
}
