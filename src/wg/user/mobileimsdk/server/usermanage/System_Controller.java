package wg.user.mobileimsdk.server.usermanage;

import wg.user.mobileimsdk.server.controller.IController;
import wg.user.mobileimsdk.server.custom.ModuleController;

public class System_Controller extends IController implements ModuleController {
	@Override
	public String getSysName() {
		return System_InitWebJfinal.sysName;
	}
}
