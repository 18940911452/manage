package wg.user.mobileimsdk.server.model.vo;

import java.io.Serializable;

public class ResultMessageVo implements Serializable {
	
	private int status;
	private String message;
	private Object data;
	public ResultMessageVo() {

	}
	
	public ResultMessageVo(int status, String message, Object data) {
		super();
		this.status = status;
		this.message = message;
		this.data = data;
	}

	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Object getData() {
		return data;
	}
	public void setData(Object data) {
		this.data = data;
	}
	
	

	

}
