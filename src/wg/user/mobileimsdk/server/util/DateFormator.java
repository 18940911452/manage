package wg.user.mobileimsdk.server.util;

/**
 * 
 * @description 日期格式化枚举类
 * @author ZSJ
 * @date 2019年4月26日
 */

public enum DateFormator {
	YEAR_MONTH_DAY_HH_MM_SS("yyyy-MM-dd HH:mm:ss"), YEAR_MONTH("yyyy-MM"), YEAR("yyyy"), SPLIT_CHAR(
			"-"), YEAR_MONTH_DAY("yyyy-MM-dd"), MONTH_DAY("MM-dd"), YEAR_MONTH_EU("yyyy/MM"), MONTH_DAY_YEAR_EU(
					"MM/dd/yyyy"), YEAR_MONTH_DAY_EU("yyyy/MM/dd"), YEAR_MONTH_DAY_H_M_S_EU("yyyy/MM/dd HH:mm:ss");
	private String str;

	private DateFormator(String s) {
		this.str = s;
	}

	@Override
	public String toString() {
		return str;
	}

	public static final String YEAR_MONTH_DAY_HH_MM = "yyyy-MM-dd HH:mm";

	public static final String[] pattern = new String[] { "yyyy-MM-dd", "yyyy/MM/dd", "yyyy-MM", "yyyy/MM",
			"MM/dd/yyyy", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy/MM/dd HH:mm:ss" };

}
