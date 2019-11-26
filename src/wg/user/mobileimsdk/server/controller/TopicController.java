package wg.user.mobileimsdk.server.controller;

import wg.user.mobileimsdk.server.config.ApiController;
import wg.user.mobileimsdk.server.config.ModuleController;
import wg.user.mobileimsdk.server.service.BaseOpenAPIInvoke;
import wg.user.mobileimsdk.server.service.TopicService;

public class TopicController extends ApiController implements ModuleController {
	BaseOpenAPIInvoke baseOpenAPIInvoke = new TopicService(getSysName());
	
	@Override
	public String getSysName() {
		return "monitoring";
	}

	@Override
	protected BaseOpenAPIInvoke getOpenAPIInvoke() {
		return baseOpenAPIInvoke;
	}
	
}
