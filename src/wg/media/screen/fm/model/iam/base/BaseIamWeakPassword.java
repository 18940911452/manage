package wg.media.screen.fm.model.iam.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseIamWeakPassword<M extends BaseIamWeakPassword<M>> extends Model<M> implements IBean {

	public void setId(java.lang.Integer id) {
		set("id", id);
	}

	public java.lang.Integer getId() {
		return get("id");
	}

	public void setWeakPassword(java.lang.String weakPassword) {
		set("weak_password", weakPassword);
	}

	public java.lang.String getWeakPassword() {
		return get("weak_password");
	}

}
