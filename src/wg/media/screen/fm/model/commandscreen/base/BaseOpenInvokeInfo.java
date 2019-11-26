package wg.media.screen.fm.model.commandscreen.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseOpenInvokeInfo<M extends BaseOpenInvokeInfo<M>> extends Model<M> implements IBean {

	public void setAutoId(java.math.BigInteger autoId) {
		set("auto_id", autoId);
	}

	public java.math.BigInteger getAutoId() {
		return get("auto_id");
	}

	public void setSysName(java.lang.String sysName) {
		set("sys_name", sysName);
	}

	public java.lang.String getSysName() {
		return get("sys_name");
	}

	public void setMethodName(java.lang.String methodName) {
		set("method_name", methodName);
	}

	public java.lang.String getMethodName() {
		return get("method_name");
	}

	public void setInvokeUrl(java.lang.String invokeUrl) {
		set("invoke_url", invokeUrl);
	}

	public java.lang.String getInvokeUrl() {
		return get("invoke_url");
	}

	public void setName(java.lang.String name) {
		set("name", name);
	}

	public java.lang.String getName() {
		return get("name");
	}

	public void setParam(java.lang.String param) {
		set("param", param);
	}

	public java.lang.String getParam() {
		return get("param");
	}

	public void setInserTime(java.util.Date inserTime) {
		set("inser_time", inserTime);
	}

	public java.util.Date getInserTime() {
		return get("inser_time");
	}

	public void setStatus(java.lang.Integer status) {
		set("status", status);
	}

	public java.lang.Integer getStatus() {
		return get("status");
	}

}