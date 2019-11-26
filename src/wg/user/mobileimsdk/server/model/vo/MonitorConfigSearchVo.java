package wg.user.mobileimsdk.server.model.vo;

import java.io.Serializable;
import java.util.List;

public class MonitorConfigSearchVo implements Serializable {
	private static final long serialVersionUID = 3584276970227352276L;
	private Integer pageNo;
	private Integer pageSize;
	private List<Integer> list;// 行业id

	public Integer getPageNo() {
		return pageNo;
	}

	public void setPageNo(Integer pageNo) {
		this.pageNo = pageNo;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	public List<Integer> getList() {
		return list;
	}

	public void setList(List<Integer> list) {
		this.list = list;
	}

}
