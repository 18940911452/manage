var iPageT = {
	//common
	pageNo : 0,					//页码
	pageSize : 30,				//信息最大条数
	
	//isExport : false,			//导出模式是否打开
	exportTip : "全部导出可能耗时较长，确定导出？",	//全部导出提示
	downloadAction : "export_download.action?data=",		//下载url
	exportUrl : "export_select_info.action",		//导出所选url
	exportAllUrl : "export_all_info.action",		//导出全部url
	exportItem : ".export_info",					//导出选择单元
	
	//scroll
	scrollToLoadRate : 1,		//触发加载时的页面滚动率
	range : 3,					//连续加载页数
	wait : 1000,				//延迟加载时间
	mvItem : ".item",			//单元块的class
	
	//masonry
	colWidth : 226,				//列宽
	colDist : 15,				//列间距
	
	//PageLoad
	pageData : ".page_data",	//分页方式返回的数据存放地址
		
		
		
		
	//内容div
//	content_html : "<div class='content_div'></div>",
	//暂存div
	cache_html : "<div class='cache_div' style='display:none;'></div>",
	//滚动状态tip
	status_tip_html : "<div class='status_tip' style='cursor:pointer;text-align:center;margin-top:10px;'></div>",
	//返回顶部
	gotop_html : "<div style='display:none;' id='gotopbtn' class='to_top'><a title='返回顶部' href='javascript:void(0);'></a></div>",
	
	// 连飞宇修改 - 在头部增加翻页
	page_head_html : '<div class="page_info ipage-fenye ipage-fenye-head">'+
	'<div class="export_div ipage-fenye-export" style="display:none;"><input type="checkbox" class="select_all"/><span class="select_all_text">全选</span>&nbsp;&nbsp;<button class="ipage-fenye-on exoprt_btn">导出所选</button>&nbsp;&nbsp;<button class="ipage-fenye-on exoprt_all_btn">导出全部</button></div>'+
	'<div class="ipage-fenye-btn"><button class="startPage page_btn ipage-fenye-on">首页</button><button class="prePage page_btn ipage-fenye-on">上页</button><button class="nextPage page_btn ipage-fenye-on">下页</button><button class="endPage page_btn ipage-fenye-on" style="display:none;">尾页</button><input class="pageNo_input" type="text" value="1"/><button class="ipage-fenye-on go_btn">GO</button></div>'+
	'<div class="ipage-fenye-info">记录总数：<span class="ipage-number total"></span>&nbsp;&nbsp;总页数：<span class="ipage-number totalPage"></span>&nbsp;&nbsp;当前页：<span class="ipage-number pageNo"></span></div>'+
	'</div>',
	//翻页方式页脚
	page_foot_html : '<div class="page_info ipage-fenye">'+
	'<div class="oper_div ipage-fenye-oper"><div class="select_div" style="float:left;margin-right:10px;"><input type="checkbox" class="select_all"/><span class="select_all_text">全选</span></div><div class="oper_btn_div" style="float:left;"></div></div>'+
	'<div class="ipage-fenye-btn"><button class="startPage page_btn ipage-fenye-on">首页</button><button class="prePage page_btn ipage-fenye-on">上页</button><button class="nextPage page_btn ipage-fenye-on">下页</button><button class="endPage page_btn ipage-fenye-on" style="display:none;">尾页</button><input class="pageNo_input" type="text" value="1"/><button class="ipage-fenye-on go_btn">GO</button></div>'+
	'<div class="ipage-fenye-info">记录总数：<span class="ipage-number total"></span>&nbsp;&nbsp;总页数：<span class="ipage-number totalPage"></span>&nbsp;&nbsp;当前页：<span class="ipage-number pageNo"></span></div>'+
	'</div>',

	operBtnHtml : '<button class="ipage-fenye-on exoprt_btn">导出所选</button>&nbsp;&nbsp;<button class="ipage-fenye-on exoprt_all_btn">导出全部</button>',
	
  	//滚动方式页脚
	//container_foot_html : '<div class="page_info ipage-fenye" style="display:none;"><div class="export_div ipage-fenye-export" style="display:none;"><input type="checkbox" class="select_all"/><span class="select_all_text">全选</span>&nbsp;&nbsp;<button class="ipage-fenye-on exoprt_btn">导出所选</button>&nbsp;&nbsp;<button class="ipage-fenye-on exoprt_all_btn">导出全部</button></div></div>',
	container_foot_html : '<div class="page_info ipage-fenye"><div class="oper_div ipage-fenye-oper"><div class="select_div" style="float:left;margin-right:10px;"><input type="checkbox" class="select_all"/><span class="select_all_text">全选</span></div><div class="oper_btn_div" style="float:left;"></div></div></div>',
  	
	//滚动状态提示
	end_html : "<div class='page_end'>暂无数据</div>",					
	//loading_html : "<div style='line-height:80px;'><span class='page_loading'></span>正在努力加载...</div>",
	 loading_html: "<br><br><br><center><img style='margin:0 auto;' src='/images/ajax-loader.gif' /><br></center><br><br><br>",
	more_html : "<div class='page_more'>加载更多</div>",
	
	
	ScrollLoadContainer : function(eleId, url, option){
		this.container = $("#"+eleId);		//容器ID
		this.url = url;						//加载URL
		//option
		this.param = option && option.param ? option.param:null;
		this.callback = option && option.callback ? option.callback:null;
		this.pageNo = option && option.pageNo ? option.pageNo:iPageT.pageNo;
		this.pageSize = option && option.pageSize ? option.pageSize:iPageT.pageSize;
		this.range = option && option.range ? option.range:iPageT.range;
		this.wait = option && option.wait ? option.wait:iPageT.wait;
		this.scrollToLoadRate = option && option.scrollToLoadRate ? option.scrollToLoadRate:iPageT.scrollToLoadRate;
		this.mvItem = option && option.mvItem ? option.mvItem:iPageT.mvItem;
		
		//this.isExport = option && option.isExport ? option.isExport:iPageT.isExport;
		this.exportHead = option && option.exportHead ? option.exportHead:null;
		this.exportUrl = option && option.exportUrl ? option.exportUrl:iPageT.exportUrl;
		this.exportAllUrl = option && option.exportAllUrl ? option.exportAllUrl:iPageT.exportAllUrl;
		this.exportItem = option && option.exportItem ? option.exportItem:iPageT.exportItem;
		this.operBtnBind = option && option.operBtnBind ? option.operBtnBind:undefined;
		this.operBtnHtml = option && option.operBtnHtml ? option.operBtnHtml:iPageT.operBtnHtml;
		//run param
		this.count;					//剩余加载页数
		this.isRec = 1;				//是否可加载1：可加载 0：正在加载 -1：已到结尾 2:点击加载更多
		this.handle = null;			//计时器
		this.isFirstLoad = true;	//是否第一次加载
		this.$pageInfo;
		this.$contentDiv;
		this.$operDiv;
	},
	
	PageLoad : function(eleId, url, option){
		this.container = $("#"+eleId);		//容器ID
		this.url = url;						//加载URL
		//option
		this.param = option && option.param ? option.param:null;
		this.callback = option && option.callback ? option.callback:null;
		this.pageNo = option && option.pageNo ? option.pageNo:iPageT.pageNo;
		this.pageSize = option && option.pageSize ? option.pageSize:iPageT.pageSize;
		this.pageData = option && option.pageData ? option.pageData:iPageT.pageData;
		//this.isExport = option && option.isExport ? option.isExport:iPageT.isExport;
		this.exportHead = option && option.exportHead ? option.exportHead:null;
		this.exportUrl = option && option.exportUrl ? option.exportUrl:iPageT.exportUrl;
		this.exportAllUrl = option && option.exportAllUrl ? option.exportAllUrl:iPageT.exportAllUrl;
		this.exportItem = option && option.exportItem ? option.exportItem:iPageT.exportItem;
		this.operBtnBind = option && option.operBtnBind ? option.operBtnBind:undefined;
		this.operBtnHtml = option && option.operBtnHtml ? option.operBtnHtml:iPageT.operBtnHtml;
		//run param
		this.count;					//剩余加载页数
		this.isRec = 1;				//是否可加载1：可加载 0：正在加载 
		this.total = -1;			//-1代表第一次
		this.totalPage;
		this.$pageInfo;
		this.$contentDiv;
		this.$operDiv;
	}
		
};




/**
 * ScrollLoadContainer
 */
iPageT.ScrollLoadContainer.prototype.init = function(){
	var obj = this;
	//清空容器
	obj.container.empty();
	//分页方式切换，清楚滚动模式下的按钮组-20160727
	obj.container.next(".page_info").remove();
	//添加内部DOM
	
//	obj.container.append(iPageT.content_html);
	obj.container.append(iPageT.cache_html);
	obj.container.append(iPageT.status_tip_html);
	
	
	obj.container.after(iPageT.container_foot_html);		//导出html
	obj.$pageInfo = obj.container.next(".page_info");
	
	//obj.container.append(iPageT.container_foot_html);		//导出html
	//obj.$pageInfo = obj.container.find(".page_info");
	
	
	obj.$contentDiv = obj.container.find(".content_div");
	
	obj.$operDiv = obj.$pageInfo.find(".oper_div");

	if(obj.operBtnHtml){	//存在批量操作
		obj.$operDiv.find('.oper_btn_div').append(obj.operBtnHtml);
		//全选
		obj.$pageInfo.find(".select_all").click(function(){
			if($(this).is(":checked")){
				obj.$contentDiv.find(obj.exportItem).prop("checked",true);
			} else {
				 obj.$contentDiv.find(obj.exportItem).prop("checked",false);
			}
			/*
			 if($(this).next("span").text().indexOf("全")>-1){
			      $(this).next("span").text("取消");
			      obj.$contentDiv.find(obj.exportItem).attr("checked",true);
			 }else if($(this).next("span").text().indexOf("取")>-1){
			       $(this).next("span").text("全选");
			       obj.$contentDiv.find(obj.exportItem).attr("checked",false);
			 }
			 */
		});
		//导出所选
		if(obj.$operDiv.find(".exoprt_btn").length){
			obj.$operDiv.find(".exoprt_btn").unbind('click').click(function(){
				if(obj.exportHead && obj.exportUrl){
					var $info = obj.$contentDiv.find(obj.exportItem+":checked");
					if($info.length){
						var exportDataVo = {};
						var head = [];
						var data = [];
						/*
						obj.exportHead.each(function(m){
							head.push(m.n);
						});
						$info.each(function(i,n){
							//console.log(n);
							var temp = [];
							obj.exportHead.each(function(m){
								temp.push($(n).data(m.d));
							});
							data.push(temp);
						});
						*/
						for(var i=0;i<obj.exportHead.length;i++){
							head.push(obj.exportHead[i].n);
						}
						$info.each(function(i,n){
							//console.log(n);
							var temp = [];
							for(var i=0;i<obj.exportHead.length;i++){
								temp.push($(n).data(obj.exportHead[i].d));
							}
							data.push(temp);
						});


						exportDataVo.head = head;
						exportDataVo.data = data;
						var exportDataVoStr = JSON.stringify(exportDataVo);
						//console.log(exportDataVoStr);
						$.post(obj.exportUrl, {"data":exportDataVoStr},function(d){
							if(d=='error'){alert("暂无数据!")}
							else{window.location.href=iPageT.downloadAction+d;}
						});
					} else {
						alert("请选择记录!");return;
					}
				}
			});
		}
		//绑定其他操作
		if(obj.operBtnBind){
			obj.operBtnBind($(".content_div",obj.container),obj);
		}
	} else {
		//obj.$operDiv.hide();
		obj.$pageInfo.hide();
	}
	
	//剩余页数初始化
	obj.count = obj.range;
	//点击继续加载重新打开阀值
	$(".status_tip",obj.container).unbind('click').click(function(){
		if(obj.isRec == 2){
			obj.count = obj.range;
			obj.loadMore();
		}
	});
	obj.container.unbind("scroll").scroll(function(){
        clearTimeout(obj.handle);
		if(obj.container.scrollTop()+obj.container.height() >= obj.container.find(".content_div").height()*obj.scrollToLoadRate){
			if(obj.isRec == 1){
				obj.handle = setTimeout(function() {
					obj.loadMore();
				}, obj.wait);
			}	
        }
    });
	//初次加载
	obj.loadMore();
	return obj;
}
iPageT.ScrollLoadContainer.prototype.loadMore = function(){
	var obj = this;
	obj.isRec = 0;		//正在加载
	obj.count--;		//剩余页-1
	obj.pageNo++;		//页码+1
	obj.updateTip();	//更新tip
	obj.loadPage();		//加载页面
}
iPageT.ScrollLoadContainer.prototype.updateTip = function(){
	var obj = this;
	switch(obj.isRec){
	case -1:	//已到结尾
		$(".status_tip",obj.container).html(iPageT.end_html);
		break;
	case 0:		//正在加载
		$(".status_tip",obj.container).html(iPageT.loading_html);
		break;
	case 1:		//可自动加载
		$(".status_tip",obj.container).html("");
		break;
	case 2:		//点击继续加载
		$(".status_tip",obj.container).html(iPageT.more_html);
		break;
	}
}
iPageT.ScrollLoadContainer.prototype.loadPage = function(){
	var obj = this;
	console.log("loadPage");
	var urlPlus = "";
	
	if(obj.url.indexOf("?")>-1){
		urlPlus += "&";
	} else {
		urlPlus += "?";
	}
	
	urlPlus += "pageNo="+obj.pageNo+"&pageSize="+obj.pageSize;
	//console.log(obj.param);
	if(obj.param){
		$(".cache_div",obj.container).load(obj.url+urlPlus, obj.param, function(){obj.loadPageAfter();});
	} else {
		$(".cache_div",obj.container).load(obj.url+urlPlus, function(){obj.loadPageAfter();});
	}
}
iPageT.ScrollLoadContainer.prototype.loadPageAfter = function(){
	var obj = this;
	if (obj.callback){
		obj.callback($(".cache_div",obj.container),obj);
	}
	var content = $(".cache_div",obj.container).find(obj.mvItem);
	//console.log(content);
	if (content != null && content.length > 0){
		if(obj.isFirstLoad){
			$(".content_div",obj.container).append(content);
			obj.isFirstLoad = false;
		} else {
			content.hide();
			$(".content_div",obj.container).append(content);
			content.fadeIn();
		}
		if (obj.count == 0){
			obj.isRec = 2;
		} else {
			obj.isRec = 1;
		}
		$(".cache_div",obj.container).empty();
	} else {
		obj.isRec = -1;
	}
	obj.updateTip();
}

/**
 * PageLoad
 */
iPageT.PageLoad.prototype.init = function(){
	var obj = this;
	//清空容器
	obj.container.find(".page_info").empty();
	obj.container.find(".content_div").empty();
	obj.container.find(".oper_div").empty();
	//分页方式切换，清楚滚动模式下的按钮组-20160727  edit by lld
	obj.container.find(".page_info").remove();
	//添加内部DOM
//	obj.container.append(iPageT.content_html);

	obj.container.append(iPageT.page_foot_html);
	obj.container.prepend(iPageT.page_head_html);
	obj.$pageInfo = obj.container.find(".page_info");
	obj.$contentDiv = obj.container.find(".content_div");
	//obj.container.find(".content_div").css({height:obj.height+"px", overflow:"auto"});
	obj.$operDiv = obj.$pageInfo.find(".oper_div");

	if(obj.operBtnHtml){	//存在批量操作
		obj.$operDiv.find('.oper_btn_div').append(obj.operBtnHtml);
		//全选
		obj.$pageInfo.find(".select_all").click(function(){
			if($(this).is(":checked")){
				obj.$contentDiv.find(obj.exportItem).prop("checked",true);
			} else {
				 obj.$contentDiv.find(obj.exportItem).prop("checked",false);
			}
			/*
			 if($(this).next("span").text().indexOf("全")>-1){
			      $(this).next("span").text("取消");
			      obj.$contentDiv.find(obj.exportItem).attr("checked",true);
			 }else if($(this).next("span").text().indexOf("取")>-1){
			       $(this).next("span").text("全选");
			       obj.$contentDiv.find(obj.exportItem).attr("checked",false);
			 }
			 */
		});
		//导出所选
		if(obj.$operDiv.find(".exoprt_btn").length){
			obj.$operDiv.find(".exoprt_btn").unbind('click').click(function(){
				if(obj.exportHead && obj.exportUrl){
					var $info = obj.$contentDiv.find(obj.exportItem+":checked");
					if($info.length){
						var exportDataVo = {};
						var head = [];
						var data = [];
						/*
						obj.exportHead.each(function(m){
							head.push(m.n);
						});
						$info.each(function(i,n){
							//console.log(n);
							var temp = [];
							obj.exportHead.each(function(m){
								temp.push($(n).data(m.d));
							});
							data.push(temp);
						});
						*/
						for(var i=0;i<obj.exportHead.length;i++){
							head.push(obj.exportHead[i].n);
						}
						$info.each(function(i,n){
							//console.log(n);
							var temp = [];
							for(var i=0;i<obj.exportHead.length;i++){
								temp.push($(n).data(obj.exportHead[i].d));
							}
							data.push(temp);
						});


						exportDataVo.head = head;
						exportDataVo.data = data;
						var exportDataVoStr = JSON.stringify(exportDataVo);
						//console.log(exportDataVoStr);
						$.post(obj.exportUrl, {"data":exportDataVoStr},function(d){
							if(d=='error'){alert("暂无数据!")}
							else{window.location.href=iPageT.downloadAction+d;}
						});
					} else {
						alert("请选择记录!");return;
					}
				}
			});
		}
		//绑定其他操作
		if(obj.operBtnBind){
			obj.operBtnBind($(".content_div",obj.container),obj);
		}
	} else {
		obj.$operDiv.hide();
	}
	
	//跳页
	/**
	 * 连飞宇修改
	 * 因为要求在头部再加一个翻页，所以此跳页操作只找到第一个obj.$pageInfo下的pageNo_input的value值
	 * 页尾的跳转会出现BUG
	 **/
	obj.$pageInfo.find(".go_btn").click(function(){
		if(obj.isRec == 1){
			// obj.$pageInfo.find(".pageNo_input").val();
			var temp = $(this).prev().val();
			if (temp){
				temp = parseInt(temp);
				if (temp > 0 && temp <= obj.totalPage){
					obj.pageNo = temp;
					obj.loadMore();
				} else {
					alert("请输入正确的页码");
				}
			} else {
				alert("请输入正确的页码");
			}
		}
	});
	//翻页
	obj.$pageInfo.find(".page_btn").unbind().click(function(){
		if(obj.isRec == 1){
			var temp = $(this).attr("pageNo");
			if (temp){
				temp = parseInt(temp);
				obj.pageNo = temp;
				obj.loadMore();
			}
		}
	});
	
	obj.loadMore();
	return obj;
}
iPageT.PageLoad.prototype.loadMore = function(){
	var obj = this;
	obj.isRec = 0;		//正在加载
	//obj.count--;		//剩余页-1
	//obj.pageNo++;		//页码+1
	obj.updateTip();	//更新tip
	obj.loadPage();		//加载页面
}
iPageT.PageLoad.prototype.updateTip = function(){
	var obj = this;
	switch(obj.isRec){
	case 0:		//正在加载
		$(".content_div",obj.container).html(iPageT.status_tip_html).find(".status_tip").html(iPageT.loading_html);
		break;
	case 1:		//可加载
		break;
	}
}
iPageT.PageLoad.prototype.loadPage = function(){
	var obj = this;
	console.log("loadPage");
	var urlPlus = "";
	if(obj.url.indexOf("?")>-1){
		urlPlus += "&";
	} else {
		urlPlus += "?";
	}

	if(obj.total == -1){
		urlPlus += "total="+obj.total+"&";
		obj.pageNo = 1;
	}
	
	urlPlus += "pageNo="+obj.pageNo+"&pageSize="+obj.pageSize;
	 /************使loading  居中**************/
	var length=$(obj.container).find("th").size();
	$(".status_tip").wrap("<tr><td colspan="+length+"></td></tr>");
	/*******************/
	if(obj.param){
		$.ajax({
			   type: "POST",
			   url: obj.url+urlPlus,
			   data:obj.param,
			   success: function(msg){
			     $(".content_div",obj.container).html("");
				 $("table tr",$(msg)).each(function(){$(".content_div",obj.container).append($(this))})
				 /************将page_data  添加到div 中  by lld**************/
				 var data=$(obj.container).find(".page_data");
				 if(data.length==1){
					 data.remove();
				 }
				 var pageData=$(msg).find(".page_data");				 
				 if(pageData.length==1){
					 $(obj.container).prepend(pageData);
				 }	
				 
				 var data1=$(obj.container).find(".art_res_type");
				 if(data1.length==1){
					 data1.remove();
				 }
				 var pageData1=$(msg).find(".art_res_type");				 
				 if(pageData1.length==1){
					 $(obj.container).prepend(pageData1);
				 }	
				 /*******************/
				 obj.loadPageAfter();
			   }
			});
		//$(".content_div",obj.container).load(obj.url+urlPlus, obj.param, function(html){console.log(html);obj.loadPageAfter();});
	} else {
		$.ajax({
			   type: "POST",
			   url: obj.url+urlPlus,
			   success: function(msg){
				     $(".content_div",obj.container).html("");
					 $("table tr",$(msg)).each(function(){$(".content_div",obj.container).append($(this))})
			     obj.loadPageAfter();
			   }
			});
		//$(".content_div",obj.container).load(obj.url+urlPlus, function(html){console.log(html);obj.loadPageAfter();});
	}
}
iPageT.PageLoad.prototype.loadPageAfter = function(){
	var obj = this;
	if (obj.callback){
		obj.callback($(".content_div",obj.container),obj);
	}
	
	//填参数
	if(obj.total == -1){
		obj.total = obj.container.find(obj.pageData).data("total");
	}
	if(!obj.totalPage){
		obj.totalPage = parseInt(obj.total/obj.pageSize)+(obj.total%obj.pageSize>0?1:0);
	}
	obj.$pageInfo.find(".total").html(obj.total);
	obj.$pageInfo.find(".totalPage").html(obj.totalPage);
	obj.$pageInfo.find(".pageNo").html(obj.pageNo);
	obj.$pageInfo.find(".startPage").attr("pageNo", 1);
	obj.$pageInfo.find(".endPage").attr("pageNo", obj.totalPage);
	obj.$pageInfo.find(".pageNo_input").val(obj.pageNo);
	if(obj.pageNo>1){
		//obj.$pageInfo.find(".prePage").removeClass("ipage-fenye-off").addClass("ipage-fenye-on").attr("pageNo", obj.pageNo-1);
		//obj.$pageInfo.find(".startPage").removeClass("ipage-fenye-off").addClass("ipage-fenye-on");
		obj.$pageInfo.find(".prePage").removeAttr("disabled").removeClass("ipage-fenye-off").addClass("ipage-fenye-on").attr("pageNo", obj.pageNo-1);
		obj.$pageInfo.find(".startPage").removeAttr("disabled").removeClass("ipage-fenye-off").addClass("ipage-fenye-on");
	} else {
		//obj.$pageInfo.find(".prePage").removeClass("ipage-fenye-on").addClass("ipage-fenye-off").attr("pageNo", 1);
		//obj.$pageInfo.find(".startPage").removeClass("ipage-fenye-on").addClass("ipage-fenye-off")
		obj.$pageInfo.find(".prePage").attr("disabled", true).removeClass("ipage-fenye-on").addClass("ipage-fenye-off").attr("pageNo", 1);
		obj.$pageInfo.find(".startPage").attr("disabled", true).removeClass("ipage-fenye-on").addClass("ipage-fenye-off");
	}
	if(obj.pageNo<obj.totalPage){
		//obj.$pageInfo.find(".nextPage").removeClass("ipage-fenye-off").addClass("ipage-fenye-on").attr("pageNo", obj.pageNo+1);
		//obj.$pageInfo.find(".endPage").removeClass("ipage-fenye-off").addClass("ipage-fenye-on");
		obj.$pageInfo.find(".nextPage").removeAttr("disabled").removeClass("ipage-fenye-off").addClass("ipage-fenye-on").attr("pageNo", obj.pageNo+1);
		obj.$pageInfo.find(".endPage").removeAttr("disabled").removeClass("ipage-fenye-off").addClass("ipage-fenye-on");
	} else {
		//obj.$pageInfo.find(".nextPage").removeClass("ipage-fenye-on").addClass("ipage-fenye-off").attr("pageNo", obj.totalPage);
		//obj.$pageInfo.find(".endPage").removeClass("ipage-fenye-on").addClass("ipage-fenye-off");
		obj.$pageInfo.find(".nextPage").attr("disabled", true).removeClass("ipage-fenye-on").addClass("ipage-fenye-off").attr("pageNo", obj.totalPage);
		obj.$pageInfo.find(".endPage").attr("disabled", true).removeClass("ipage-fenye-on").addClass("ipage-fenye-off");
	}
	
	//可加载
	obj.isRec = 1;
	obj.updateTip();
}
