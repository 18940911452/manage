package wg.media.screen.fm.vo;

import java.io.Serializable;

public class ResultVo <T>  implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private  String status ;
	private  Integer code;
	private String info ="";
	private  T data;
	private ResultVo (String status ,Integer code, T data){
		this.status = status;
		this.code = code ;
		this.data =data;
	}
	
	
	public  void setOK(T data){
		this.status="ok";
		this.data = data;
		this.code=1;
	}
	
	public static <T> ResultVo<T> create(T data){
		return new ResultVo<T>("ok",2,data);
	}
	
	public void setError(){
		this.status="error";
		this.code=2;
	}
	
	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}


	public Integer getCode() {
		return code;
	}


	public void setCode(Integer code) {
		this.code = code;
	}


	public String getInfo() {
		return info;
	}


	public void setInfo(String info) {
		this.info = info;
	}


	public T getData() {
		return data;
	}


	public void setData(T data) {
		this.data = data;
	}


	

}
