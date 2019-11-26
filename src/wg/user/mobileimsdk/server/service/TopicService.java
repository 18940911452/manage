package wg.user.mobileimsdk.server.service;

import wg.user.mobileimsdk.server.service.BaseOpenAPIInvoke;

public class TopicService extends BaseOpenAPIInvoke {

	private String modelName;

	public TopicService(String sysName) {
		this.modelName = sysName;
				
	}

	@Override
	protected String modelName() {
		return modelName;
	}


}
