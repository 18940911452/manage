package wg.user.mobileimsdk.server.modelKit;

import javax.sql.DataSource;

import com.jfinal.kit.PathKit;
import com.jfinal.plugin.activerecord.generator.Generator;
import com.jfinal.plugin.c3p0.C3p0Plugin;
import com.jfinal.plugin.druid.DruidPlugin;

/**
 * 在数据库表有任何变动时，运行一下 main 方法，极速响应变化进行代码重构
 */
public class _DBModelGenerator_fs {
	private static DruidPlugin createDruidPlugin() {
//		return new C3p0Plugin("jdbc:mysql://101.200.125.86/_newMedia_al?characterEncoding=utf8", "dba", "db*#2016");
		return new DruidPlugin("jdbc:mysql://192.168.14.10/tjim?characterEncoding=utf8", "tjuser", "tj%2018");
	}

	public static DataSource getDataSource() {
		DruidPlugin druidPlugin = createDruidPlugin();
		druidPlugin.start();
		return druidPlugin.getDataSource();
	}

	static String package_ = "wg.user.mobileimsdk.server.model.fs";

	public static void main(String[] args) {
		// base model 所使用的包名
		String baseModelPackageName =package_+".base";
		// base model 文件保存路径
		System.out.println(package_.replaceAll("\\.", "/"));
		String baseModelOutputDir = PathKit.getRootClassPath() + "/../../../src/"+package_.replaceAll("\\.", "/")+"/base";
		System.out.println(baseModelOutputDir);
		// model 所使用的包名 (MappingKit 默认使用的包名)
		String modelPackageName =package_;
		// model 文件保存路径 (MappingKit 与 DataDictionary 文件默认保存路径)
		String modelOutputDir = baseModelOutputDir + "/..";
		Generator gernerator = new Generator(getDataSource(), baseModelPackageName, baseModelOutputDir, modelPackageName, modelOutputDir);
		gernerator.setGenerateDaoInModel(true);
		gernerator.setGenerateDataDictionary(true);
		gernerator.setRemovedTableNamePrefixes("t_");
		gernerator.generate();
	}
}
