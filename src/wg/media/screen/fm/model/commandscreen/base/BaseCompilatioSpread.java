package wg.media.screen.fm.model.commandscreen.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseCompilatioSpread<M extends BaseCompilatioSpread<M>> extends Model<M> implements IBean {

	public void setId(java.lang.Integer id) {
		set("id", id);
	}

	public java.lang.Integer getId() {
		return get("id");
	}

	public void setName(java.lang.String name) {
		set("name", name);
	}

	public java.lang.String getName() {
		return get("name");
	}

	public void setNum(java.lang.String num) {
		set("num", num);
	}

	public java.lang.String getNum() {
		return get("num");
	}

	public void setInstId(java.lang.Integer instId) {
		set("inst_id", instId);
	}

	public java.lang.Integer getInstId() {
		return get("inst_id");
	}

}
