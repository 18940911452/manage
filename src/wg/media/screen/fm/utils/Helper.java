package wg.media.screen.fm.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.TableMapping;

public class Helper {
	private static final String regEx_script = "<script[^>]*?>[\\s\\S]*?<\\/script>"; // 定义script的正则表达式  
    private static final String regEx_style = "<style[^>]*?>[\\s\\S]*?<\\/style>"; // 定义style的正则表达式  
    private static final String regEx_html = "<[^>]+>"; // 定义HTML标签的正则表达式  
    private static final String regEx_space = "\\s*|\t|\r|\n";//定义空格回车换行符  
    
    private static SimpleDateFormat formatYMD = new SimpleDateFormat("yyyy-MM-dd 00:00:00");

	public static String getTableName(Class<? extends Model<?>> cla) {
		return TableMapping.me().getTable(cla).getName();
	}
	
	/**
	 * 过滤表情符
	 */
	public static String Subphiz(String keyWords){
		//String str =keyWords.replaceAll("[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\u2600-\u27ff]", "");
		String str =keyWords.replaceAll("[\ud800\udc00-\udbff\udfff\ud800-\udfff]", "");
		return str;
	}
	
	/**
	 * 把双引号""替换成''
	 */
	public static String Substitua(String keyWords){
		String str = keyWords.replaceAll("\"", "'");
		return str;
		
	}
	/**
	 * 判断中文
	 * @param str
	 * @return
	 */
	public static boolean isChineseChar(String str){
	       boolean temp = false;
	       Pattern p=Pattern.compile("[\u4e00-\u9fa5]"); 
	       Matcher m=p.matcher(str); 
	       if(m.find()){ 
	           temp =  true;
	       }
	       return temp;
	   }
	
	
	/**
	 * 取包含单引号的str中，单引号分隔的部分最长的
	 * @param str
	 * @return
	 */
	public static String findTheLongPart(String str){
		if(str==null) return null;
		String[] tstr = str.split("'");
		String temp = "";
		if(tstr!=null && tstr.length>0){
			for (String s : tstr) {
				if(s.length()>temp.length()){
					temp = s;
				}
			}
		}
	     return temp;
	   }
	
	
	/**
     * 全角字符串转换半角字符串
     * 
     * @param fullWidthStr  非空的全角字符串
     * @return 半角字符串
     */
	public static String fullWidth2halfWidth(String fullWidthStr) {
        if (null == fullWidthStr || fullWidthStr.length() <= 0) {
            return "";
        }
        char[] charArray = fullWidthStr.toCharArray();
        //对全角字符转换的char数组遍历
        for (int i = 0; i < charArray.length; ++i) {
            int charIntValue = (int) charArray[i];
            //如果符合转换关系,将对应下标之间减掉偏移量65248;如果是空格的话,直接做转换
            if (charIntValue >= 65281 && charIntValue <= 65374) {
                charArray[i] = (char) (charIntValue - 65248);
            } else if (charIntValue == 12288) {
                charArray[i] = (char) 32;
            }
        }
        return new String(charArray);
    }
    /**
     * 半角字符串转换全角字符串
     * 
     * @param halfWidthStr  非空的半角字符串
     * @return 全角字符串
     */
    public static String halfWidth2fullWidth(String halfWidthStr) {
    	if (null == halfWidthStr || halfWidthStr.length() <= 0) {
    		return "";
    	}
    	char[] charArray = halfWidthStr.toCharArray();
    	//对全角字符转换的char数组遍历
    	for (int i = 0; i < charArray.length; ++i) {
    		int charIntValue = (int) charArray[i];
    		//如果符合转换关系,将对应下标之间加上偏移量65248;如果是空格的话,直接做转换
    		if (charIntValue >= 33 && charIntValue <= 126) {
    			charArray[i] = (char) (charIntValue + 65248);
    		} else if (charIntValue == 32) {
    			charArray[i] = (char) 12288;
    		}
    	}
    	return new String(charArray);
    }
    
	/**
	 * 时间格式化
	 * 
	 * @param str
	 * @return
	 * @throws Exception
	 */
	public static Date Str2Date(String str) throws Exception {
		Date ret = null;
		if (StringUtils.isNotBlank(str)) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			ret = sdf.parse(str);
		}
		return ret;
	}

	public static String Date2Str(Date date) throws Exception {
		String ret = null;
		if (date != null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			ret = sdf.format(date);
		}
		return ret;
	}
/**
 * 处理date为  date+23:59:59
 * @param date
 * @return
 */
	public static Date processDate(String date){
		Date d = null;
		try {
			if(date!=null && !date.trim().equals("")){
				d = Helper.Str2Date(Helper.Date2Str(Helper.Str2Date(date, "yyyyMMdd"))+" 23:59:59","yyyy-MM-dd HH:mm:ss");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return d;
	}
	public static Date Str2Date(String str, String formate) throws Exception {
		Date ret = null;
		if (StringUtils.isNotBlank(str)) {
			SimpleDateFormat sdf = new SimpleDateFormat(formate);
			ret = sdf.parse(str);
		}
		return ret;
	}

	public static String Date2Str(Date date, String formate) throws Exception {
		String ret = null;
		if (date != null) {
			SimpleDateFormat sdf = new SimpleDateFormat(formate);
			ret = sdf.format(date);
		}
		return ret;
	}
	/**
	 * 时间是今日显示时分秒，否则显示日期
	 * @param xd
	 * @return
	 */
	public static String findProcesstime(Date xd) {
		String ptime = "";
		try {
			String now = Helper.Date2Str(new Date()).trim();
			String time1 = Helper.Date2Str(xd).trim();
			if (time1.equals(now)) {
				ptime = Helper.Date2Str(xd, "HH:mm:ss");
			} else {
				ptime = Helper.Date2Str(xd, "yyyy-MM-dd");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ptime;
	}
	
	/**
	 * 拼接字符串
	 * 
	 * @param checks
	 * @return
	 */
	public static String conStrSql(Object[] checks) {
		return conStr(checks, ",", "'", "'");
	}
	
	public static<T> String conStr(T[] checks, String split,String prefix,String suffex) {
		StringBuilder sb = new StringBuilder();
		for (Object check : checks) {
			String item = check.toString();
			if (StringUtils.isNotBlank(item)) {
				sb.append(prefix).append(item.trim()).append(suffex).append(split);
			}
		}
		if(sb.length()>split.length()) {
			return sb.subSequence(0, sb.length()-split.length()).toString();
		}
		return sb.toString();
	}
	public static<T> String conStr(T[] checks) {
		return conStr(checks,",","'","'");
	}
//	public static void main(String[] args) {
//		String conStr = conStr("aaa,bbb,ccc".split(","));
//		System.out.println(conStr);
//	}
	

	/**
	 * 列表转字符串
	 * 
	 * 拼接为普通形式   比如  a,b
	 * 
	 * @param os
	 * @return
	 */
	public static String list2str(List os, String split) {
		return collection2str(os, split);
	}
	/**
	 * 列表转字符串
	 * 
	 * 拼接为普通形式   比如  a,b
	 * 
	 * @param os
	 * @return
	 */
	public static String collection2str(Collection os, String split) {
		
		return collection2str(os,split,"","");
	}
	
	/**
	 * 列表转字符串
	 * 
	 * 拼接为普通形式   比如  a,b
	 * 
	 * @param os
	 * @return
	 */
	public static String collection2str(Collection os, String split,String prefix,String suffix) {
		StringBuilder str = new StringBuilder();
		for (Object o : os) {
			if (str.length() == 0) {
				str.append(prefix).append(o.toString().trim()).append(suffix);
			} else {
				str.append(split).append(prefix).append(o.toString()).append(suffix);
			}
		}
		return str.toString();
	}

	/**
	 * 列表转字符串
	 * 
	 * 拼接为普通形式   比如  a,b
	 * 
	 * @param os
	 * @return
	 */
	public static String list2str(String[] os, String split) {
		StringBuilder str = new StringBuilder();
		for (Object o : os) {
			if (str.length() == 0) {
				str.append(o.toString());
			} else {
				str.append(split + o.toString());
			}
		}
		return str.toString();
	}
	
	/**
	 * 将List拼接为字符串
	 * 
	 * 拼接为带单引号的形式，    比如  'a','b'
	 * 
	 * @param os
	 * @return
	 */
	public static String list2strWithQuotes(List os, String split) {
		StringBuilder str = new StringBuilder();
		for (Object o : os) {
			if (str.length() == 0) {
				str.append("'"+o.toString()+"'");
			} else {
				str.append(split + "'"+o.toString()+"'");
			}
		}
		return str.toString();
	}
	
	/**
	 * 将数组Array拼接为字符串
	 * 
	 * 将拼接为带单引号的形式 ，  比如  'a','b'
	 * 
	 * @param os
	 * @return
	 */
	public static String list2strWithQuotes(String[] os, String split) {
		StringBuilder str = new StringBuilder();
		for (Object o : os) {
			if (str.length() == 0) {
				str.append("'"+o.toString()+"'");
			} else {
				str.append(split + "'"+o.toString()+"'");
			}
		}
		return str.toString();
	}

	/**
	 * 标红
	 * 
	 * @param keywords
	 * @param string
	 * @return
	 */
	public static String redMark(List<String> keywords, String string) {
		if (keywords != null && keywords.size() > 0) {
			for (String word : keywords) {
				try {
					if (!word.isEmpty())
						string = string.replace(word, "<font color='red' >" + word + "</font>");
				} catch (Exception e) {
					continue;
				}
			}
		}
		return string;
	}
	/**
	 * 标红
	 * 
	 * @param keywords
	 * @param string
	 * @return
	 */
	public static String redMark(String[] keywords, String string,String colod) {
		if (keywords != null && keywords.length > 0) {
			for (String word : keywords) {
				try {
					if (!word.isEmpty())
						//style='font-weight:600'
						string = string.replace(word, "<font color='"+colod+"' >" + word + "</font>");
				} catch (Exception e) {
					continue;
				}
			}
		}
		return string;
	}

	/**
	 * 十万级以上的数字，忽视万以下数据
	 * 
	 * @param count
	 * @return
	 */
	public static String getCountForUser(String count) {
		String result = "0";
		if (StringUtils.isNotBlank(count) && !("null").equals(count)) {
			if (count.length() > 5)
				result = count.substring(0, count.length() - 4) + "万";
			else
				result = count;
		}
		return result;
	}

	/**
	 * 字符串的压缩
	 * 
	 * @param str
	 *            待压缩的字符串
	 * @return 返回压缩后的字符串
	 * @throws IOException
	 */
	public static String enCodeByGZIP(String str) throws IOException {
		if (null == str || str.length() <= 0) {
			return str;
		}
		// 创建一个新的 byte 数组输出流
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		// 使用默认缓冲区大小创建新的输出流
		GZIPOutputStream gzip = new GZIPOutputStream(out);
		// 将 b.length 个字节写入此输出流
		gzip.write(str.getBytes());
		gzip.close();
		// 使用指定的 charsetName，通过解码字节将缓冲区内容转换为字符串
		return out.toString("ISO-8859-1");
	}

	/**
	 * 字符串的解压
	 * 
	 * @param str
	 *            对字符串解压
	 * @return 返回解压缩后的字符串
	 * @throws IOException
	 */
	public static String deCodeByGZIP(String str) throws IOException {
		if (null == str || str.length() <= 0) {
			return str;
		}
		// 创建一个新的 byte 数组输出流
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		// 创建一个 ByteArrayInputStream，使用 buf 作为其缓冲区数组
		ByteArrayInputStream in = new ByteArrayInputStream(str.getBytes("ISO-8859-1"));
		//
		GZIPInputStream gzip = new GZIPInputStream(in);
		byte[] buffer = new byte[256];
		int n = 0;
		while ((n = gzip.read(buffer)) >= 0) {// 将未压缩数据读入字节数组
			// 将指定 byte 数组中从偏移量 off 开始的 len 个字节写入此 byte数组输出流
			out.write(buffer, 0, n);
		}
		// 使用指定的 charsetName，通过解码字节将缓冲区内容转换为字符串
		return out.toString("utf-8");
	}
	 /**
     * 获取前几个小时的时间
     * @param d
     * @param hour
     * @return
     */
    public static String getDateTimeBefor(Date d,Integer hour){
    	Calendar calendar = Calendar.getInstance(); 
    	calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) - hour);
    	SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//    	System.out.println("12个小时前的时间：" + df.format(calendar.getTime()));
//    	System.out.println("当前的时间：" + df.format(new Date()));
    	return df.format(calendar.getTime());
    }

    /** 
     * @param htmlStr 
     * @return 
     *  删除Html标签 
     */  
    public static String delHTMLTag(String htmlStr) {  
    	if(StringUtils.isBlank(htmlStr))  return  "";
        Pattern p_script = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE);  
        Matcher m_script = p_script.matcher(htmlStr);  
        htmlStr = m_script.replaceAll(""); // 过滤script标签  
  
        Pattern p_style = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE);  
        Matcher m_style = p_style.matcher(htmlStr);  
        htmlStr = m_style.replaceAll(""); // 过滤style标签  
  
        Pattern p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);  
        Matcher m_html = p_html.matcher(htmlStr);  
        htmlStr = m_html.replaceAll(""); // 过滤html标签  
  
//        Pattern p_space = Pattern.compile(regEx_space, Pattern.CASE_INSENSITIVE);  
//        Matcher m_space = p_space.matcher(htmlStr);  
//        htmlStr = m_space.replaceAll(""); // 过滤空格回车标签  
        return htmlStr.trim(); // 返回文本字符串  
    }  
    
	public static int getCurrentHour(){
		Calendar calender = Calendar.getInstance();
		calender.setTime(new Date());
		return calender.get(Calendar.HOUR_OF_DAY);
	}
	
	public static String getDate(int day){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.add(Calendar.DAY_OF_YEAR, day);
		String dateTime = formatYMD.format(calendar.getTime());
		return dateTime;
	}
	/**
	 * 
	 * @param date  日期
	 * @param day   增加的天数
	 * @return		增加后的日期字符串 格式为"yyyy-MM-dd"
	 */
	public static String getDate(Date date,int day){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DAY_OF_YEAR, day);
		String dateTime = formatYMD.format(calendar.getTime());
		return dateTime;
	}
	
	public static int getHour(int hour){
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, (calendar.get(Calendar.HOUR_OF_DAY)) + hour);
		return calendar.get(Calendar.HOUR_OF_DAY);
	}
	
	/**
	 * 将文本中的关键词高亮
	 * @param source
	 * @param keywords
	 * @return
	 * @author mayucong
	 * @date 2017年1月11日 下午4:08:50
	 */
	public static String getKeywordMarkedContent(String source,String keywords){
		if(StringUtils.isBlank(source) || StringUtils.isBlank(keywords)){
			return source;
		}
		//加上(?i)，不区分大小写
		return source.replaceAll("(?i)"+keywords.trim(), "<mark>"+keywords+"</mark>");
	}

/*
	public static File findDfsFile(String dfsPath, String fileName) {
		try {
			if(dfsPath==null) return null;
			File downloadFile  = new File(fileName);
			ClientGlobal.init(PathKit.getRootClassPath()+File.separator+"fdfs_client.conf");
			
			Prop prob = PropKit.use("fdfs_client.conf");
	 		String serverIpPort = prob.get("tracker_server");
	 		String IP = serverIpPort.split(":")[0];
	 		Integer port = Integer.valueOf(serverIpPort.split(":")[1]);
			TrackerGroup tg = new TrackerGroup(new InetSocketAddress[] { new InetSocketAddress(IP, port) });
			TrackerClient tc = new TrackerClient(tg);
			TrackerServer ts = tc.getConnection();
			if (ts == null) {
				System.out.println("getConnection return null");
				return null;
			}
			StorageServer ss = tc.getStoreStorage(ts);
			if (ss == null) {
				System.out.println("getStoreStorage return null");
			}
			StorageClient1 sc1 = new StorageClient1(ts, ss);
			//文件压缩
			byte[] downloadRawData = sc1.download_file1(dfsPath);
			byte[] compressData = Snappy.uncompress(downloadRawData); // fastDFS上的数据都是经过snappy压缩的， 
			FileOutputStream fos = new FileOutputStream(downloadFile);
			fos.write(compressData);
			fos.flush();
			fos.close();
			
			return downloadFile;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	
	}*/

	//list 去重
	public static void removeDuplicate(List arlList) {
		  if(null==arlList) return;
		  HashSet h = new HashSet(arlList);      
		  arlList.clear();      
		  arlList.addAll(h);      
	}
	
	/** 
     * 判断输入的字符串是否满足时间格式 ： yyyy-MM-dd HH:mm:ss 
     * @param patternString 需要验证的字符串 
     * @return 合法返回 true ; 不合法返回false 
     */  
    public static boolean isTimeLegal(String patternString) {  
         if(null==patternString || patternString.trim().equals("")) return false;
         Pattern a=Pattern.compile("^((\\d{2}(([02468][048])|([13579][26]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))(\\s((([0-1][0-9])|(2?[0-3]))\\:([0-5]?[0-9])((\\s)|(\\:([0-5]?[0-9])))))?$");   
         Matcher b=a.matcher(patternString);   
         if(b.matches()) {  
               return true;  
         } else {  
               return false;
         }  
    }
    
    
    public static String getWeek(String sdate) {  
	    // 再转换为时间  
	     Date date = strToDate(sdate);  
	     Calendar c = Calendar.getInstance();  
	     c.setTime(date);  
	     // int hour=c.get(Calendar.DAY_OF_WEEK);  
	     // hour中存的就是星期几了，其范围 1~7  
	     // 1=星期日 7=星期六，其他类推  
	     return new SimpleDateFormat("EEEE",Locale.CHINA).format(c.getTime());  
	 }  
	public static Date strToDate(String strDate) {  
	     SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
	     ParsePosition pos = new ParsePosition(0);  
	     Date strtodate =  formatter.parse(strDate, pos);  
	     return strtodate;  
	 }
	/**
	 * 
	 * <p>Title:递归将data json串转换未JSONArray对象 j</p>
	 * <p>Description:</p>
	 * <p>Company:中科闻歌</p>
	 * @param 
	 * @author lzk
	 * @date 2017年9月13日
	 * @return void
	 */
	public static void trans(JSONArray  ja, JSONArray j) {
		if(ja==null||ja.size()==0)
			return;
		for(int i =0;i<ja.size(); i++){
    		JSONObject o = ja.getJSONObject(i);
    	    JSONObject r=new JSONObject();
    		r.put("name", o.get("name"));
    		r.put("interCount", o.get("interCount"));
    		r.put("withZDRcount", o.get("withZDRcount"));
    		r.put("userid", o.get("userid"));
    		r.put("level", o.get("level"));
    		r.put("site", o.get("site"));
    		r.put("isZDR", o.get("isZDR"));
    		r.put("count", o.get("count"));
    		r.put("id", o.get("id"));
    		r.put("isFriend",o.get("isFriend"));
    		if(o.containsKey("data_id")){
    			r.put("data_id",o.get("data_id"));
    		}
    		j.add(r);
    		trans(o.getJSONArray("child"),j);
    	}
		
	}

	/**
	 * 
	 * <p>Title:对JSONArray中的对象按照field字段的值进行排序</p>
	 * <p>Description:</p>
	 * <p>Company:中科闻歌</p>
	 * @param ja 带排序的JSONArray对象，field排序的字段，isAsc是否翻转ja中的顺序
	 * @author lzk
	 * @date 2017年9月13日
	 * @return void
	 */
	public static void Jsonsort2(JSONArray ja, final String field, final boolean isAsc) {
		Collections.sort(ja, new Comparator<Object>() {
			@Override
			public int compare(Object o1, Object o2) {
				JSONObject js1 = (JSONObject)o1;
				JSONObject js2 = (JSONObject)o2;
				String f1 = String.valueOf(js1.get(field));
 		    	String f2 = String.valueOf(js2.get(field));
 		    	if(f1.matches("[0-9]*") && f2.matches("[0-9]*")){
     	           return Integer.valueOf(f1) - Integer.valueOf(f2);
     	        }else{
     	           return String.valueOf(f1).compareTo(String.valueOf(f2));
     	        }
		}});
		
     	if(!isAsc){
     	    Collections.reverse(ja);
     	}
     	
     	
	}

	
	public static String getDayBeforNowOf(int days, String formate ){
		String dayBeforNow = "";
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DATE, days);
		dayBeforNow = formatDate(c.getTime(), formate);
		return dayBeforNow;
	} 

    public static synchronized String formatDate(Date date, String pattern) {
		try {
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
			return simpleDateFormat.format(date);
		} catch (RuntimeException e) {
			return null;
		}
	}
    
    
    
	private static NumberFormat numberFormat = NumberFormat.getInstance();
	static {// 设置精确到小数点后2位
		numberFormat.setMaximumFractionDigits(2);
	}
    /**
     * 获取百分比
     * @param num
     * @param sum
     * @return
     */
    public static String getPecent(double num,double sum) {
    	if(num==0.0||sum==0.0) {
    		return "0.00";
    	}
    	return numberFormat.format(num / sum * 100);
    }
    public static void main1(String[] args) {
    	System.out.println(getPecent(45.0,100.0));
//		System.out.println(Calendar.getInstance().get(Calendar.HOUR_OF_DAY));
//		System.out.println(Calendar.getInstance().get(Calendar.HOUR));
//    	String simple2Complex = simple2Complex("新华网");
//    	System.out.println(simple2Complex);
//    	System.out.println(complex2Simple("新华网"));
    	
	}    
    
    /**
     * 判断list中是否有某个过滤词
     * @param name
     * @param filtword
     * @return
     */
    public static boolean filterWord(String name, List<String> filtword) {
		boolean flag = false;
		for (String str : filtword) {
			if(str!=null && !("").equals(str) && name.contains(str)){
				flag = true;
				break;
			}
		}
		return flag;
	}
	/**
	 * 每隔 num位插入空格
	 * @param name
	 * @return
	 */
	public static String processInsertBlankName(String name,int num) {
		char[] bankArray = name.toCharArray();
        String bankString = "";
        for(int i=0;i<bankArray.length;i++){
            if(i%num==0 && i>0){
                bankString +=" ";
            }
            bankString += bankArray[i];
        }
		return bankString;
	}
    
	/**
	 * 过滤正文中的url
	 * 
	 */
	public static boolean filterUrl(String url){
		Pattern pattern1 = Pattern.compile("[^//]*?\\.(com|cn|net|org|biz|info|cc|tv)");
		Pattern pattern2 = Pattern.compile("^([hH][tT]{2}[pP]://|[hH][tT]{2}[pP][sS]://)(([A-Za-z0-9-~]+).)+([A-Za-z0-9-~\\/])+$");
		return pattern1.matcher(url).matches() ||pattern2.matcher(url).matches();
	}
	public static void main(String[] args) {
		String url1 = "ddhttp://www.xx.comddd";
		String url2 = "www.xx.com";
//		String url2 = "w.xx.com";
		String url3 = "http://w.xx.com";
		String url4 = "ssss";
//		((http:\\)?w{1,3}\.\w+\.)(com|cn|net|org|biz|info|cc|tv)\s*
		Pattern pattern1 = Pattern.compile("[^//]*?\\.(com|cn|net|org|biz|info|cc|tv)");
		Pattern pattern2 = Pattern.compile("^([hH][tT]{2}[pP]://|[hH][tT]{2}[pP][sS]://)(([A-Za-z0-9-~]+).)+([A-Za-z0-9-~\\/])+$");
		System.out.println(pattern1.matcher(url1).matches() || pattern2.matcher(url1).matches());
		System.out.println(pattern1.matcher(url2).matches() || pattern2.matcher(url2).matches());
		System.out.println(pattern1.matcher(url3).matches() ||pattern2.matcher(url3).matches());
		System.out.println(pattern1.matcher(url4).matches() ||pattern2.matcher(url4).matches());
		
		url1 = url1.replaceAll("[//]*?\\.(com|cn|net|org|biz|info|cc|tv)", "");
		url1 = url1.replaceAll("([hH][tT]{2}[pP]://|[hH][tT]{2}[pP][sS]://)(([A-Za-z0-9-~]+).)+([A-Za-z0-9-~\\/])+$", "");
		System.out.println(url1);
		
	}

}
