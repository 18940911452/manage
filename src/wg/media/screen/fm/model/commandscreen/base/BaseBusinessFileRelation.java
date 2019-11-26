package wg.media.screen.fm.model.commandscreen.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseBusinessFileRelation<M extends BaseBusinessFileRelation<M>> extends Model<M> implements IBean {

	public void setFileId(java.lang.Integer fileId) {
		set("file_id", fileId);
	}

	public java.lang.Integer getFileId() {
		return get("file_id");
	}

	public void setDataId(java.lang.Integer dataId) {
		set("data_id", dataId);
	}

	public java.lang.Integer getDataId() {
		return get("data_id");
	}

	public void setDataType(java.lang.String dataType) {
		set("data_type", dataType);
	}

	public java.lang.String getDataType() {
		return get("data_type");
	}

	public void setId(java.lang.Integer id) {
		set("id", id);
	}

	public java.lang.Integer getId() {
		return get("id");
	}

}