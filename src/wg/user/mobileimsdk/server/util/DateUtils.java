package wg.user.mobileimsdk.server.util;

import java.text.DateFormat;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;
import org.springframework.web.util.HtmlUtils;

/**
 * 
 * @description 日期工具类
 * @author ZSJ
 * @date 2019年4月15日
 */
public class DateUtils {

	public static List<String> MONTH_LIST = Arrays.asList("JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP",
			"OCT", "NOV", "DEC");

	/**
	 * 时间比较.
	 * 
	 * @param deadLine
	 * @return
	 */
	public static boolean isOverTime(Date deadLine) {
		try {
			Date date = new Date();
			String dt = new String(new SimpleDateFormat("yyyy-MM-dd").format(date));
			DateFormat f = new java.text.SimpleDateFormat("yy-MM-dd");
			Date nowDate = f.parse(dt);
			return deadLine.before(nowDate);// 截止日期小于当前时间true,否则false;
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}

	public static long getDateTime(Date date) {
		try {
			String dt = new String(new SimpleDateFormat("yyyy-MM-dd").format(date));
			DateFormat f = new java.text.SimpleDateFormat("yy-MM-dd");
			Date nowDate = f.parse(dt);
			return nowDate.getTime();
		} catch (Exception ex) {
			ex.printStackTrace();
			return 0l;
		}
	}

	/**
	 * 一年后的日期.
	 * 
	 * @param dateLine
	 * @return
	 */
	public static String afterOneYear() {
		Format formatter = new SimpleDateFormat("yyyy/MM/dd");
		Date todayDate = new Date();
		long afterTime = (todayDate.getTime() / 1000) + 60 * 60 * 24 * 365;
		todayDate.setTime(afterTime * 1000);
		String afterOneYear = formatter.format(todayDate);
		return afterOneYear;
	}

	/**
	 * 获取N月的今天。
	 * 
	 * @return
	 */
	public static Date afterOneMonth(int nextMonth) {
		try {
			Date date = new Date();
			int year = Integer.parseInt(new SimpleDateFormat("yyyy").format(date));
			int month = Integer.parseInt(new SimpleDateFormat("MM").format(date)) + nextMonth;
			int day = Integer.parseInt(new SimpleDateFormat("dd").format(date));
			if (month == 0) {
				year -= 1;
				month = 12;
			} else if (day > 28) {
				if (month == 2) {
					if (year % 400 == 0 || (year % 4 == 0 && year % 100 != 0)) {
						day = 29;
					} else
						day = 28;
				} else if ((month == 4 || month == 6 || month == 9 || month == 11) && day == 31) {
					day = 30;
				}
			}
			String y = year + "";
			String m = "";
			String d = "";
			if (month < 10)
				m = "0" + month;
			else
				m = month + "";
			if (day < 10)
				d = "0" + day;
			else
				d = day + "";
			return new SimpleDateFormat("yyyy-MM-dd").parse(y + "-" + m + "-" + d);
		} catch (Exception e) {
			return new Date();
		}
	}

	public static void main(String[] args) {
		Calendar cal = Calendar.getInstance();
		int offset = cal.get(Calendar.ZONE_OFFSET);
		cal.add(Calendar.MILLISECOND, -offset);
		Long timeStampUTC = cal.getTimeInMillis();
		Long timeStamp = System.currentTimeMillis();
		Long timeZone = (timeStamp - timeStampUTC) / (1000 * 3600);
		System.out.println(timeZone.intValue());
		// System.out.println(Jsoup.clean("asdas<iv>&", Whitelist.basicWithImages()));
		System.out.println(StringEscapeUtils.escapeHtml(
				"{\"param\":{\"id\":\"34\",\"name\":\"XSS过滤<div>adav>&\",\"leader\":\"leader\",\"contacter\":\"leader\",\"address\":\"leader\",\"telephone\":\"\",\"fax\":\"\",\"email\":\"\",\"note\":\"\",\"inst_id\":\"1\"}}"));
		System.out.println(HtmlUtils.htmlEscape(
				"{\"param\":{\"id\":\"34\",\"name\":\"XSS过滤<div>adav>&\",\"leader\":\"leader\",\"contacter\":\"leader\",\"address\":\"leader\",\"telephone\":\"\",\"fax\":\"\",\"email\":\"\",\"note\":\"\",\"inst_id\":\"1\"}}"));
		System.out.println(HtmlUtils.htmlUnescape("&quot;"));
		System.out.println(System.getProperty("user.timezone"));
	}

	/**
	 * 将月份数字转为英文
	 * 
	 * @param releaseVersion
	 * @return
	 */
	public static String changeNumberToChar(String releaseVersion) {

		if (null == releaseVersion) {
			throw new IllegalArgumentException("ReleaseVersion cannot be null");
		}
		String strNumber = releaseVersion.substring(0, 2);
		String strMonth = null;
		try {
			strMonth = MONTH_LIST.get(Integer.parseInt(strNumber));
		} catch (NumberFormatException e) {
			e.printStackTrace();
			System.out.println("Parse month error........");
		}
		return strMonth + releaseVersion.substring(2);
	}

	/**
	 * 将月份英文转换为数字
	 * 
	 * @param releaseVersion
	 * @return
	 */
	public static String changeStringToNumber(String releaseVersion) {
		if (null == releaseVersion) {
			throw new IllegalArgumentException("ReleaseVersion cannot be null");
		}
		for (int i = 0; i < MONTH_LIST.size(); i++) {
			if (releaseVersion.toUpperCase().startsWith(MONTH_LIST.get(i))) {
				if (i < 10) {
					return "0" + (i + 1);
				}
				return i + 1 + "";
			}
		}
		return -1 + "";
	}

	public static int getSixMonth(Long toYear, Long toMonth) throws ParseException {
		if (toYear == null) {
			return 1;
		} else {
			if (toMonth == null) {
				toMonth = 1l;
			}
			Calendar cal = Calendar.getInstance();
			int month = cal.get(Calendar.MONTH) + 1;
			int year = cal.get(Calendar.YEAR);
			if (year == toYear.intValue()) {
				if ((month - toMonth.intValue()) <= 6) {
					return 1;
				} else {
					return 0;
				}
			} else {
				int marginMonth = ((year - 1) * 12 + month) - ((toYear.intValue() - 1) * 12 + toMonth.intValue());
				if (marginMonth <= 6) {
					return 1;
				} else {
					return 0;
				}
			}
		}
	}

	// 针对某个功能
	public static String format(Date date, String string) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int HH = calendar.get(Calendar.HOUR_OF_DAY);
		int mm = calendar.get(Calendar.MINUTE);
		int ss = calendar.get(Calendar.SECOND);
		StringBuilder sb = new StringBuilder();
		if (HH < 10) {
			sb.append("0").append(HH);
		} else {
			sb.append(HH);
		}

		if (mm < 10) {
			sb.append("0").append(mm);
		} else {
			sb.append(mm);
		}

		if (ss < 10) {
			sb.append("0").append(ss);
		} else {
			sb.append(ss);
		}

		return sb.toString();
	}

	/**
	 * 解析时间
	 * 
	 * @param dateString
	 * @return
	 */
	public static Date parseStringToDate(String dateString) {

		Date date = null;
		if (StringUtils.isNoneBlank(dateString)) {
			dateString = dateString.replaceAll("/", "-");
			String pattem = "";
			switch (dateString.length()) {
			case 4:
				pattem = "yyyy";
				break;
			case 7:
				pattem = "yyyy-MM";
				break;
			case 10:
				pattem = "yyyy-MM-dd";
				break;
			case 16:
				pattem = "yyyy-MM-dd HH:mm";
				break;
			default:
				pattem = "yyyy-MM-dd HH:mm:ss";
				break;
			}
			DateFormat df = new SimpleDateFormat(pattem);

			try {
				date = df.parse(dateString);
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(date);
				if (calendar.get(Calendar.YEAR) > 9999) { // 超过最大年份
					calendar.set(Calendar.YEAR, 9999);
					return calendar.getTime();
				}
			} catch (ParseException e) {

			}
		}
		return date;
	}

	public static String dateFormatToString(Date d) {
		SimpleDateFormat sdf = new SimpleDateFormat(DateFormator.YEAR_MONTH_DAY.toString());
		if (d == null) {
			return "";
		}
		return sdf.format(d);
	}

	public static String dateFormat(String date) {
		SimpleDateFormat sdf = new SimpleDateFormat(DateFormator.YEAR_MONTH_DAY.toString());
		if (StringUtils.isBlank(date) || "".equals(date)) {
			return "";
		}
		return sdf.format(parseStringToDate(date));
	}

	/**
	 * 根据日期返回日期中的年. wuwm
	 */
	public static int getYear(Date d) {
		Assert.notNull(d);
		String dateStr = toString(d, DateFormator.YEAR_MONTH); // yyyy-MM
		return Integer.parseInt(dateStr.split(DateFormator.SPLIT_CHAR.toString())[0]);
	}

	/**
	 * 根据日期返回日期中的年.
	 */
	public static int getMonth(Date d) {
		Assert.notNull(d);
		String dateStr = toString(d, DateFormator.YEAR_MONTH); // yyyy-MM
		return Integer.parseInt(dateStr.split(DateFormator.SPLIT_CHAR.toString())[1]);
	}

	/**
	 * 根据日期返回日期中的日.
	 */
	public static int getDay(Date d) {
		Assert.notNull(d);
		String dateStr = toString(d, DateFormator.YEAR_MONTH_DAY); // yyyy-MM-dd
		return Integer.parseInt(dateStr.split(DateFormator.SPLIT_CHAR.toString())[2]);
	}

	public static String toString(Date date, DateFormator pattern) {
		Assert.notNull(date);
		Assert.notNull(pattern);
		SimpleDateFormat sdf = new SimpleDateFormat(pattern.toString());
		return sdf.format(date);
	}

	/**
	 * 
	 * @description
	 * @author ZSJ
	 * @date 2019年4月26日
	 * @param currentDay
	 * @return
	 */
	public static String getTomorrow(String currentDay) {
		Calendar c = Calendar.getInstance();
		Date date = null;
		try {
			date = new SimpleDateFormat("yy-MM-dd").parse(currentDay);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		c.setTime(date);
		int day = c.get(Calendar.DATE);
		c.set(Calendar.DATE, day + 1);

		String dayAfter = new SimpleDateFormat("yyyy-MM-dd").format(c.getTime());
		return dayAfter;
	}

	/**
	 * 获取过去或者未来 任意天内的日期数组
	 * 
	 * @param intervals
	 *            intervals天内
	 * @return 日期数组
	 */
	public static List<String> get(int intervals) {
		List<String> pastDaysList = new ArrayList<>();
		// List<String> fetureDaysList = new ArrayList<>();
		for (int i = 0; i < intervals; i++) {
			pastDaysList.add(getPastDate(i));
			// fetureDaysList.add(getFutureDate(i));
		}
		return pastDaysList;
	}

	/**
	 * 获取过去第几天的日期
	 * 
	 * @param past
	 * @return
	 */
	public static String getPastDate(int past) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) - past);
		Date today = calendar.getTime();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String result = format.format(today);
		// Log.e(null, result);
		return result;
	}

	/**
	 * 获取未来 第 past 天的日期
	 * 
	 * @param past
	 * @return
	 */
	public static String getFutureDate(int past) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) + past);
		Date today = calendar.getTime();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String result = format.format(today);
		// Log.e(null, result);
		return result;
	}

	/**
	 * 
	 * @description 获取当前这个星期的日期集合
	 * @author ZSJ
	 * @date 2019年5月6日
	 * @return
	 */
	public static List<String> getCurrentWeekDate() {
		List<String> list = new ArrayList<String>();
		LocalDate today = LocalDate.now();
		list.add(today.with(DayOfWeek.MONDAY).toString());
		list.add(today.with(DayOfWeek.TUESDAY).toString());
		list.add(today.with(DayOfWeek.WEDNESDAY).toString());
		list.add(today.with(DayOfWeek.THURSDAY).toString());
		list.add(today.with(DayOfWeek.FRIDAY).toString());
		list.add(today.with(DayOfWeek.SATURDAY).toString());
		list.add(today.with(DayOfWeek.SUNDAY).toString());
		return list;
	}
}
