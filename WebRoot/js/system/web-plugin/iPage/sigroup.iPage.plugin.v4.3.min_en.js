var iPage = {
	pageNo: 0,
	pageSize: 30,
	//exportTip: "全部导出可能耗时较长，确定导出？",
	exportTip: "Exporting all may take a long time, are you sure to export?",
	downloadAction: "export_download.action?data=",
	exportUrl: "export_select_info.action",
	exportAllUrl: "export_all_info.action",
	exportItem: ".export_info",
	scrollToLoadRate: 1,
	range: 3,
	wait: 1000,
	mvItem: ".item",
	colWidth: 226,
	colDist: 15,
	pageData: ".page_data",
	content_html: "<div class='content_div'></div>",
	cache_html: "<div class='cache_div' style=''></div>",
	status_tip_html: "<div class='status_tip' style='cursor:pointer;text-align:center;margin-top:0px;'></div>",
	/*
	gotop_html: "<div style='display:none;' id='gotopbtn' class='to_top'><a title='返回顶部' href='javascript:void(0);'></a></div>",
	page_foot_html: '<div class="page_info ipage-fenye">' + '<div class="oper_div ipage-fenye-oper"><div class="select_div" style="float:left;margin-right:10px;"><input type="checkbox" class="select_all"/><span class="select_all_text">全选</span></div><div class="oper_btn_div" style="float:left;"></div></div>' + '<div class="ipage-fenye-btn"><button class="startPage page_btn ipage-fenye-on">O primeiro</button><button class="prePage page_btn ipage-fenye-on">Antes de UMA</button><button class="nextPage page_btn ipage-fenye-on">O próximo</button><button class="endPage page_btn ipage-fenye-on" style="display:none;">尾页</button><input class="pageNo_input" type="text" value="1"/><button class="ipage-fenye-on go_btn">GO</button></div>' + '<div class="ipage-fenye-info">记录总数：<span class="ipage-number total"></span>&nbsp;&nbsp;总页数：<span class="ipage-number totalPage"></span>&nbsp;&nbsp;当前页：<span class="ipage-number pageNo"></span></div>' + "</div>",
	operBtnHtml: '<button class="ipage-fenye-on exoprt_btn">导出所选</button>&nbsp;&nbsp;<button class="ipage-fenye-on exoprt_all_btn">导出全部</button>',
	container_foot_html: '<div class="page_info ipage-fenye"><div class="oper_div ipage-fenye-oper"><div class="select_div" style="float:left;margin-right:10px;"><input type="checkbox" class="select_all"/><span class="select_all_text">全选</span></div><div class="oper_btn_div" style="float:left;"></div></div></div>',
	 */
	gotop_html: "<div style='display:none;' id='gotopbtn' class='to_top'><a title='Back to top' href='javascript:void(0);'></a></div>",
	page_foot_html: '<div class="page_info ipage-fenye">' + '<div class="oper_div ipage-fenye-oper"><div class="select_div" style="float:left;margin-right:10px;"><input type="checkbox" class="select_all"/><span class="select_all_text">Select all</span></div><div class="oper_btn_div" style="float:left;"></div></div>' + '<div class="ipage-fenye-btn"><button class="startPage page_btn ipage-fenye-on">First</button><button class="prePage page_btn ipage-fenye-on">Previous</button><button class="nextPage page_btn ipage-fenye-on">Next</button><button class="endPage page_btn ipage-fenye-on" style="display:none;">Last</button><input class="pageNo_input" type="text" value="1"/><button class="ipage-fenye-on go_btn">GO</button></div>' + '<div class="ipage-fenye-info">Total：<span class="ipage-number total"></span>&nbsp;&nbsp;Total Pages：<span class="ipage-number totalPage"></span>&nbsp;&nbsp;Current Page：<span class="ipage-number pageNo"></span></div>' + "</div>",
	operBtnHtml: '<button class="ipage-fenye-on exoprt_btn">Export selected</button>&nbsp;&nbsp;<button class="ipage-fenye-on exoprt_all_btn">Export all</button>',
	container_foot_html: '<div class="page_info ipage-fenye"><div class="oper_div ipage-fenye-oper"><div class="select_div" style="float:left;margin-right:10px;"><input type="checkbox" class="select_all"/><span class="select_all_text">Select all</span></div><div class="oper_btn_div" style="float:left;"></div></div></div>',
	end_html: "<div class='page_end'></div>",
	loading_html: "<div style='padding-top:105px;'><img style='display:block;margin:0 auto;' src='img/ajax-loader.gif' /></div>",
	//more_html: "<div class='page_more'>加载更多</div>",
	more_html: "<div class='page_more'>Load more</div>",
	ScrollLoadContainer: function(a, b, c) {
		this.container = $("#" + a);
		this.url = b;
		this.param = c && c.param ? c.param : null;
		this.callback = c && c.callback ? c.callback : null;
		this.pageNo = c && c.pageNo ? c.pageNo : iPage.pageNo;
		this.pageSize = c && c.pageSize ? c.pageSize : iPage.pageSize;
		this.range = c && c.range ? c.range : iPage.range;
		this.wait = c && c.wait ? c.wait : iPage.wait;
		this.scrollToLoadRate = c && c.scrollToLoadRate ? c.scrollToLoadRate : iPage.scrollToLoadRate;
		this.mvItem = c && c.mvItem ? c.mvItem : iPage.mvItem;
		this.exportHead = c && c.exportHead ? c.exportHead : null;
		this.exportUrl = c && c.exportUrl ? c.exportUrl : iPage.exportUrl;
		this.exportAllUrl = c && c.exportAllUrl ? c.exportAllUrl : iPage.exportAllUrl;
		this.exportItem = c && c.exportItem ? c.exportItem : iPage.exportItem;
		this.operBtnBind = c && c.operBtnBind ? c.operBtnBind : undefined;
		this.operBtnHtml = c && c.operBtnHtml ? c.operBtnHtml : iPage.operBtnHtml;
		this.count;
		this.isRec = 1;
		this.handle = null;
		this.isFirstLoad = true;
		this.$pageInfo;
		this.$contentDiv;
		this.$operDiv
	},
	PageLoad: function(a, b, c) {
		this.container = $("#" + a);
		this.url = b;
		this.param = c && c.param ? c.param : null;
		this.callback = c && c.callback ? c.callback : null;
		this.pageNo = c && c.pageNo ? c.pageNo : 1;
		this.pageSize = c && c.pageSize ? c.pageSize : iPage.pageSize;
		this.pageData = c && c.pageData ? c.pageData : iPage.pageData;
		this.exportHead = c && c.exportHead ? c.exportHead : null;
		this.exportUrl = c && c.exportUrl ? c.exportUrl : iPage.exportUrl;
		this.exportAllUrl = c && c.exportAllUrl ? c.exportAllUrl : iPage.exportAllUrl;
		this.exportItem = c && c.exportItem ? c.exportItem : iPage.exportItem;
		this.operBtnBind = c && c.operBtnBind ? c.operBtnBind : undefined;
		this.operBtnHtml = c && c.operBtnHtml ? c.operBtnHtml : iPage.operBtnHtml;
		this.page_head_html = c && c.page_head_html ? c.page_head_html : null;
		this.count;
		this.isRec = 1;
		this.total = -1;
		this.totalPage;
		this.$pageInfo;
		this.$contentDiv;
		this.$operDiv
	}
};
iPage.ScrollLoadContainer.prototype.init = function() {
	var a = this;
	a.container.empty();
	a.container.next(".page_info").remove();
	a.container.append(iPage.content_html);
	a.container.append(iPage.cache_html);
	a.container.append(iPage.status_tip_html);
	a.container.after(iPage.container_foot_html);
	a.$pageInfo = a.container.next(".page_info");
	a.$contentDiv = a.container.find(".content_div");
	a.$operDiv = a.$pageInfo.find(".oper_div");
	if(a.operBtnHtml) {
		a.$operDiv.find(".oper_btn_div").append(a.operBtnHtml);
		a.$pageInfo.find(".select_all").click(function() {
			if($(this).is(":checked")) {
				a.$contentDiv.find(a.exportItem).prop("checked", true)
			} else {
				a.$contentDiv.find(a.exportItem).prop("checked", false)
			}
		});
		if(a.$operDiv.find(".exoprt_btn").length) {
			a.$operDiv.find(".exoprt_btn").unbind("click").click(function() {
				if(a.exportHead && a.exportUrl) {
					var d = a.$contentDiv.find(a.exportItem + ":checked");
					if(d.length) {
						var g = {};
						var c = [];
						var f = [];
						for(var b = 0; b < a.exportHead.length; b++) {
							c.push(a.exportHead[b].n)
						}
						d.each(function(j, k) {
							var h = [];
							for(var j = 0; j < a.exportHead.length; j++) {
								h.push($(k).data(a.exportHead[j].d))
							}
							f.push(h)
						});
						g.head = c;
						g.data = f;
						var e = JSON.stringify(g);
						$.post(a.exportUrl, {
							"data": e
						}, function(h) {
							if(h == "error") {
								//alert("暂无数据!")
								alert("No data available!")
							} else {
								window.location.href = iPage.downloadAction + h
							}
						})
					} else {
						//alert("请选择记录!");
						alert("Please select one record!");
						return
					}
				}
			})
		}
		if(a.operBtnBind) {
			a.operBtnBind($(".content_div", a.container), a)
		}
	} else {
		a.$pageInfo.hide()
	}
	a.count = a.range;
	$(".status_tip", a.container).unbind("click").click(function() {
		if(a.isRec == 2) {
			a.count = a.range;
			a.loadMore()
		}
	});
	a.container.unbind("scroll").scroll(function() {
		clearTimeout(a.handle);
		if(a.container.scrollTop() + a.container.height() >= a.container.find(".content_div").height() * a.scrollToLoadRate) {
			if(a.isRec == 1) {
				a.handle = setTimeout(function() {
					a.loadMore()
				}, a.wait)
			}
		}
	});
	a.loadMore();
	return a
};
iPage.ScrollLoadContainer.prototype.loadMore = function() {
	var a = this;
	a.isRec = 0;
	a.count--;
	a.pageNo++;
	a.updateTip();
	a.loadPage()
};
iPage.ScrollLoadContainer.prototype.updateTip = function() {
	var a = this;
	switch(a.isRec) {
		case -1:
			$(".status_tip", a.container).html(iPage.end_html);
			break;
		case 0:
			$(".status_tip", a.container).html(iPage.loading_html);
			break;
		case 1:
			$(".status_tip", a.container).html("");
			break;
		case 2:
			//$(".status_tip", a.container).html(iPage.more_html);
			//$(".status_tip", a.container).unbind("click").click();	去掉加载更多
			a.count = a.range;
			a.loadMore();
			break
	}
};
iPage.ScrollLoadContainer.prototype.loadPage = function() {
	var b = this;
	console.log("loadPage");
	var a = "";
	if(b.url.indexOf("?") > -1) {
		a += "&"
	} else {
		a += "?"
	}
	a += "pageNo=" + b.pageNo + "&pageSize=" + b.pageSize;
	if(b.param) {
		$(".cache_div", b.container).load(b.url + a, b.param, function() {
			b.loadPageAfter()
		})
	} else {
		$(".cache_div", b.container).load(b.url + a, function() {
			b.loadPageAfter()
		})
	}
};
iPage.ScrollLoadContainer.prototype.loadPageAfter = function() {
	var b = this;
	if(b.callback) {
		b.callback($(".cache_div", b.container), b)
	}
	var a = $(".cache_div", b.container).find(b.mvItem);
	if(a != null && a.length > 0) {
		if(b.isFirstLoad) {
			$(".content_div", b.container).append(a);
			b.isFirstLoad = false
		} else {
			a.hide();
			$(".content_div", b.container).append(a);
			a.fadeIn()
		}
		if(b.count == 0) {
			b.isRec = 2
		} else {
			b.isRec = 1
		}
		$(".cache_div", b.container).empty()
	} else {
		b.isRec = -1
	}
	b.updateTip()
};
iPage.PageLoad.prototype.init = function() {
	var a = this;
	a.container.empty();
	a.container.next(".page_info").remove();
	a.container.append(iPage.page_head_html);
	a.container.append(iPage.content_html);
	a.container.append(iPage.page_foot_html);
	a.$pageInfo = a.container.find(".page_info");
	a.$contentDiv = a.container.find(".content_div");
	a.$operDiv = a.$pageInfo.find(".oper_div");
	if(a.operBtnHtml) {
		a.$operDiv.find(".oper_btn_div").append(a.operBtnHtml);
		a.$pageInfo.find(".select_all").click(function() {
			if($(this).is(":checked")) {
				a.$contentDiv.find(a.exportItem).prop("checked", true)
			} else {
				a.$contentDiv.find(a.exportItem).prop("checked", false)
			}
		});
		if(a.$operDiv.find(".exoprt_btn").length) {
			a.$operDiv.find(".exoprt_btn").unbind("click").click(function() {
				if(a.exportHead && a.exportUrl) {
					var d = a.$contentDiv.find(a.exportItem + ":checked");
					if(d.length) {
						var g = {};
						var c = [];
						var f = [];
						for(var b = 0; b < a.exportHead.length; b++) {
							c.push(a.exportHead[b].n)
						}
						d.each(function(j, k) {
							var h = [];
							for(var j = 0; j < a.exportHead.length; j++) {
								h.push($(k).data(a.exportHead[j].d))
							}
							f.push(h)
						});
						g.head = c;
						g.data = f;
						var e = JSON.stringify(g);
						$.post(a.exportUrl, {
							"data": e
						}, function(h) {
							if(h == "error") {
								//alert("暂无数据!")
								alert("No data available!")
							} else {
								window.location.href = iPage.downloadAction + h
							}
						})
					} else {
						//alert("请选择记录!");
						alert("Please select one record!");
						return
					}
				}
			})
		}
		if(a.operBtnBind) {
			a.operBtnBind($(".content_div", a.container), a)
		}
	} else {
		a.$operDiv.hide()
	}
	a.$pageInfo.find(".go_btn").click(function() {
		if(a.isRec == 1) {
			var b = a.$pageInfo.find(".pageNo_input").val();
			if(b) {
				b = parseInt(b);
				if(b > 0 && b <= a.totalPage) {
					a.pageNo = b;
					a.loadMore()
				} else {
					//alert("请输入正确的页码")
					alert("Please enter the correct page number.")
				}
			} else {
				//alert("请输入正确的页码")
				alert("Please enter the correct page number,")
			}
		}
	});
	a.$pageInfo.find(".page_btn").unbind().click(function() {
		if(a.isRec == 1) {
			var b = $(this).attr("pageNo");
			if(b) {
				b = parseInt(b);
				a.pageNo = b;
				a.loadMore()
			}
		}
	});
	a.loadMore();
	return a
};
iPage.PageLoad.prototype.loadMore = function() {
	var a = this;
	a.isRec = 0;
	a.updateTip();
	a.loadPage()
};
iPage.PageLoad.prototype.updateTip = function() {
	var a = this;
	switch(a.isRec) {
		case 0:
			$(".content_div", a.container).html(iPage.status_tip_html).find(".status_tip").html(iPage.loading_html);
			break;
		case 1:
			break
	}
};
iPage.PageLoad.prototype.loadPage = function() {
	var b = this;
	console.log("loadPage");
	var a = "";
	if(b.url.indexOf("?") > -1) {
		a += "&"
	} else {
		a += "?"
	}
	if(b.total == -1) {
		a += "total=" + b.total + "&";
//		b.pageNo = 1
	}
	a += "pageNo=" + b.pageNo + "&pageSize=" + b.pageSize;
	if(b.param) {
		$(".content_div", b.container).load(b.url + a, b.param, function() {
			b.loadPageAfter()
		})
	} else {
		$(".content_div", b.container).load(b.url + a, function() {
			b.loadPageAfter()
		})
	}
};
iPage.PageLoad.prototype.loadPageAfter = function() {
	var a = this;
	if(a.callback) {
		a.callback($(".content_div", a.container), a)
	}
	if(a.total == -1) {
		a.total = a.container.find(a.pageData).data("total")
	}
	if(!a.totalPage) {
		a.totalPage = parseInt(a.total / a.pageSize) + (a.total % a.pageSize > 0 ? 1 : 0)
	}
	a.$pageInfo.find(".total").html(a.total);
	a.$pageInfo.find(".totalPage").html(a.totalPage);
	a.$pageInfo.find(".pageNo").html(a.pageNo);
	a.$pageInfo.find(".startPage").attr("pageNo", 1);
	a.$pageInfo.find(".endPage").attr("pageNo", a.totalPage);
	a.$pageInfo.find(".pageNo_input").val(a.pageNo);
	if(a.pageNo > 1) {
		a.$pageInfo.find(".prePage").removeAttr("disabled").removeClass("ipage-fenye-off").addClass("ipage-fenye-on").attr("pageNo", a.pageNo - 1);
		a.$pageInfo.find(".startPage").removeAttr("disabled").removeClass("ipage-fenye-off").addClass("ipage-fenye-on")
	} else {
		a.$pageInfo.find(".prePage").attr("disabled", true).removeClass("ipage-fenye-on").addClass("ipage-fenye-off").attr("pageNo", 1);
		a.$pageInfo.find(".startPage").attr("disabled", true).removeClass("ipage-fenye-on").addClass("ipage-fenye-off")
	}
	if(a.pageNo < a.totalPage) {
		a.$pageInfo.find(".nextPage").removeAttr("disabled").removeClass("ipage-fenye-off").addClass("ipage-fenye-on").attr("pageNo", a.pageNo + 1);
		a.$pageInfo.find(".endPage").removeAttr("disabled").removeClass("ipage-fenye-off").addClass("ipage-fenye-on")
	} else {
		a.$pageInfo.find(".nextPage").attr("disabled", true).removeClass("ipage-fenye-on").addClass("ipage-fenye-off").attr("pageNo", a.totalPage);
		a.$pageInfo.find(".endPage").attr("disabled", true).removeClass("ipage-fenye-on").addClass("ipage-fenye-off")
	}
	a.isRec = 1;
	a.updateTip()
};