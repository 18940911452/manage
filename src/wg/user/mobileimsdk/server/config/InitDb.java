package wg.user.mobileimsdk.server.config;

import java.lang.reflect.Method;
import java.util.List;

import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.druid.DruidPlugin;

import wg.media.screen.fm.model.commandscreen.OpenConfig;

public class InitDb {

	static Prop prop = PropKit.use("a_little_config_ms.txt");

	private static String jdbcUrl = "jdbc:mysql://#IP/#DB?characterEncoding=utf8&serverTimezone=Asia/Shanghai";

	public static void init() {
		initcommand_screen_common();
		initcommand_screen();
		initiam();
	}

	
	/**
	 * 初始化command_screen_common库
	 */
	public static void initcommand_screen_common() {
		initDataSourceTwo(prop.get("jdbc.ip.commandscreencommon"), prop.get("jdbc.db.commandscreencommon"), prop.get("jdbc.username.commandscreencommon"),
				prop.get("jdbc.password.commandscreencommon"), prop.get("jdbc.map.commandscreencommon"));
	}
	public static void initdataSea() {
		System.out.println("数据库信息：" + prop.get("jdbc.ip.datasea") + prop.get("jdbc.db.datasea")
				+ prop.get("jdbc.username.datasea") + prop.get("jdbc.password.datasea") + prop.get("jdbc.map.datasea"));
		initDataSource(prop.get("jdbc.ip.datasea"), prop.get("jdbc.db.datasea"), prop.get("jdbc.username.datasea"),
				prop.get("jdbc.password.datasea"), prop.get("jdbc.map.datasea"));
	}
	
	/**
	 * 初始化command_screen库
	 */
	public static void initcommand_screen() {
		initDataSourceThree(prop.get("jdbc.ip.commandscreen"), prop.get("jdbc.db.commandscreen"), prop.get("jdbc.username.commandscreen"),
				prop.get("jdbc.password.commandscreen"), prop.get("jdbc.map.commandscreen"));
	}
	 
	 /**
	  * 初始化command_screen库
	  */
	  public static void initiam() {
		  initDataSourceFour(prop.get("jdbc.ip.iam"), prop.get("jdbc.db.iam"), prop.get("jdbc.username.iam"),
				  prop.get("jdbc.password.iam"), prop.get("jdbc.map.iam"));
	  }
	
	/**
	 * jfinal初始化数据库
	 * 
	 * @param ip
	 * @param dbname
	 * @param userName
	 * @param password
	 * @param mappingClassName
	 */
	public static void initDataSourceTwo(String ip, String dbname, String userName, String password,
			String mappingClassName) {
		try {
			String str_kb = jdbcUrl.replace("#IP", ip).replace("#DB", dbname);
			DruidPlugin dp_kb = new DruidPlugin(str_kb, userName, password);
			Class<?> obj = Class.forName(mappingClassName);
			Method method = obj.getMethod("mapping", ActiveRecordPlugin.class);
			ActiveRecordPlugin arp_kb = new ActiveRecordPlugin(dbname, dp_kb);
			method.invoke(obj, new Object[] { arp_kb });
			arp_kb.setShowSql(false);
			dp_kb.start();
			arp_kb.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * jfinal初始化数据库
	 * 
	 * @param ip
	 * @param dbname
	 * @param userName
	 * @param password
	 * @param mappingClassName
	 */
	public static void initDataSourceThree(String ip, String dbname, String userName, String password,
			String mappingClassName) {
		try {
			String str_kb = jdbcUrl.replace("#IP", ip).replace("#DB", dbname);
			DruidPlugin dp_kb = new DruidPlugin(str_kb, userName, password);
			Class<?> obj = Class.forName(mappingClassName);
			Method method = obj.getMethod("mapping", ActiveRecordPlugin.class);
			ActiveRecordPlugin arp_kb = new ActiveRecordPlugin(dbname, dp_kb);
			method.invoke(obj, new Object[] { arp_kb });
			arp_kb.setShowSql(false);
			dp_kb.start();
			arp_kb.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * jfinal初始化数据库
	 * 
	 * @param ip
	 * @param dbname
	 * @param userName
	 * @param password
	 * @param mappingClassName
	 */
	public static void initDataSourceFour(String ip, String dbname, String userName, String password,
			String mappingClassName) {
		try {
			String str_kb = jdbcUrl.replace("#IP", ip).replace("#DB", dbname);
			DruidPlugin dp_kb = new DruidPlugin(str_kb, userName, password);
			Class<?> obj = Class.forName(mappingClassName);
			Method method = obj.getMethod("mapping", ActiveRecordPlugin.class);
			ActiveRecordPlugin arp_kb = new ActiveRecordPlugin(dbname, dp_kb);
			method.invoke(obj, new Object[] { arp_kb });
			arp_kb.setShowSql(false);
			dp_kb.start();
			arp_kb.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * jfinal初始化数据库
	 * 
	 * @param ip
	 * @param dbname
	 * @param userName
	 * @param password
	 * @param mappingClassName
	 */
	public static void initDataSource(String ip, String dbname, String userName, String password,
			String mappingClassName) {
		try {
			String str_kb = jdbcUrl.replace("#IP", ip).replace("#DB", dbname);
			DruidPlugin dp_kb = new DruidPlugin(str_kb, userName, password);
			Class<?> obj = Class.forName(mappingClassName);
			Method method = obj.getMethod("mapping", ActiveRecordPlugin.class);
			ActiveRecordPlugin arp_kb = new ActiveRecordPlugin(dbname, dp_kb);
			method.invoke(obj, new Object[] { arp_kb });
			arp_kb.setShowSql(false);
			dp_kb.start();
			arp_kb.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static void main(String[] args) {
		init();
		List<OpenConfig> openConfigs = OpenConfig.dao.find("select * from open_config limit 3");
		System.out.println(openConfigs);
	}

}
