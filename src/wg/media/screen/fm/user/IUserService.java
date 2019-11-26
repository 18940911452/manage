package wg.media.screen.fm.user;

import wg.media.screen.fm.model.commandscreencommon.ManageInstitution;

public interface IUserService {

	/**
	 * 根据uuid 获取用户
	 * @param uuid
	 * @return
	 */
	ManageInstitution findByUUid(String uuid);
	/**
	 * 获取layout
	 * @param long1 
	 * @return
	 */
	String layout(Long autoId, String modName);

}
