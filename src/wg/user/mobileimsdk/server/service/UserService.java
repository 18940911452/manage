package wg.user.mobileimsdk.server.service;

import org.apache.commons.lang3.StringUtils;

import com.jfinal.plugin.activerecord.TableMapping;

import wg.media.screen.fm.model.commandscreen.OpenLayout;
import wg.media.screen.fm.model.commandscreencommon.ManageInstitution;
import wg.media.screen.fm.user.IUserService;

public class UserService  implements IUserService{

	@Override
	public ManageInstitution findByUUid(String uuid) {
		String userSql = "select * from "+TableMapping.me().getTable(ManageInstitution.class).getName()+" where uuid= ? limit 1";
		ManageInstitution manageSystem = ManageInstitution.dao.findFirst(userSql,uuid);
		return manageSystem;
	}
	@Override
	public String layout(Long autoId,String modName) {
		if(StringUtils.isBlank(modName))
			return "";
		if(autoId == null) 
			return "";
		String result ="";
		try {
			String layoutSql = "select layout from "+TableMapping.me().getTable(OpenLayout.class).getName()+" where user_id= ?  and name = ? and status =1 limit 1 ";
			OpenLayout find = OpenLayout.dao.findFirst(layoutSql,autoId,modName);
			if(find==null || StringUtils.isBlank(find.getLayout())) {
				return "";
			}
			result = find.getLayout();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	
	
}
