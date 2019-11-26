//選題列表
load_topic_list();
function load_topic_list(to_item) {
	let param = {
		name : ""
	}
	$.post("topicSelPlan/findName",param,function (data) {
		$("#topic_list").empty();
		if(data.status == 1) {
			data.data.forEach(function (item) {
				let status_html = "";
				if(item.status == 1) {
					status_html = '<span class="icon_content"></span>';
				}
				$("#topic_list").append('<div keywords="'+item.keywords+'" status="'+item.status+'" topic_des="'+item.des+'" topic_time="'+item.create_time+'" topic_id="'+item.id+'" class="media topicSelItem">'+status_html+
						'<div class="col-md-12">'+
							'<div class="content">'+item.name+'</div>'+
						'</div>'+
				'</div>');
			});
			//	选择一个选择
			$(".topicSelItem").unbind("click").click(function () {
				let id = $(this).attr("topic_id");
				$(this).addClass("active").siblings().removeClass("active");
				$("#top_topic_des").html($(this).attr("topic_des"));
				$("#top_topic_time").html($(this).attr("topic_time"));
				// $("#top_topic_name").html($(this).find(".content").text());
				$("#top_topic_name").html($(this).find(".content").text().substr(0,70) +'');

					Date.prototype.format = function(fmt) {  // 时间格式转换(当前时间)
						var o = { 
						"M+" : this.getMonth()+1,                 //月份 
						"d+" : this.getDate(),                    //日 
						"h+" : this.getHours(),                   //小时 
						"m+" : this.getMinutes(),                 //分 
						"s+" : this.getSeconds(),                 //秒 
						"q+" : Math.floor((this.getMonth()+3)/3), //季度 
						"S"  : this.getMilliseconds()             //毫秒 
					}; 
					if(/(y+)/.test(fmt)) {
							fmt=fmt.replace(RegExp.$1, (this.getFullYear()+"").substr(4 - RegExp.$1.length)); 
					}
						for(var k in o) {
						if(new RegExp("("+ k +")").test(fmt)){
								fmt = fmt.replace(RegExp.$1, (RegExp.$1.length==1) ? (o[k]) : (("00"+ o[k]).substr((""+ o[k]).length)));
							}
						}
					return fmt; 
					}     
				var items = new Date().format("yyyy-MM-dd hh:mm:ss");
				// console.log(items)
				var key = ''
				key = $(this).attr("keywords").split(',')
				if( key[0] == 'null' || '') {
					key = ['云南']
				}
				var p  = [{"must":key,"any":[],"not":[]}]
				var params = {};
				params.keywords = p;
				// params.startTime = $(this).attr("topic_time");
				params.endTime = items;

				//	审核 & 任务概览
				if($(this).attr("status") == "1") {
					$(".topic_look_btn").hide();
					$(".content-boxs-task").show();
					$(".tableList>li:last-child").show();
					task_overview(); // 任务概况
					topic_rank(params); // 报道量排行
					Volume_trend(params); // 报道量趋势
					dispatch_text(params); //发文量
					getNewslist(params);	// 网络素材
					videoListBox(params); // 自有稿件
				} else {
					$(".topic_look_btn").show();
					$(".content-boxs-task").hide();
					$(".tableList>li:last-child").hide()
					topic_rank(params); // 报道量排行
					Volume_trend(params); // 报道量趋势
					dispatch_text(params); //发文量
					getNewslist(params);  // 网络素材
					videoListBox(params); // 自有稿件
				}
				
			});
			if(to_item == undefined) {
				$("#topic_list").find(".topicSelItem").eq(0).click();
			} else {
				
			}
		} else {
			$("#topic_list").html("<img style='display:block;margin:60px auto 50px auto;width:auto;' src='img/bt-nodata-now.png' />")
		}
	});
}

// 搜索
$(".form-control").keyup(function(event){
	if(event.keyCode == 13){
		findName()
	}
})
$(".glyphicon").unbind("click").click(function(){
	findName()
})
function findName(){
		let str = $(".form-control").val()
		$.post("topicSelPlan/findName",{name:str},function(data){
			if(data.status == 1) {
			$("#topic_list").empty();
				data.data.forEach(function (item) {
					let status_html = "";
					if(item.status == 1) {
						status_html = '<span class="icon_content"></span>';
					}
					$("#topic_list").append('<div keywords="'+item.keywords+'" status="'+item.status+'" topic_des="'+item.des+'" topic_time="'+item.create_time+'" topic_id="'+item.id+'" class="media topicSelItem">'+status_html+
							'<div class="col-md-12">'+
								'<div class="content">'+item.name+'</div>'+
							'</div>'+
					'</div>');
				});
				//	选择一个选择
				$(".topicSelItem").unbind("click").click(function () {
					let id = $(this).attr("topic_id");
					$(this).addClass("active").siblings().removeClass("active");
					$("#top_topic_des").html($(this).attr("topic_des"));
					$("#top_topic_time").html($(this).attr("topic_time"));
					// $("#top_topic_name").html($(this).find(".content").text());
					$("#top_topic_name").html($(this).find(".content").text().substr(0,100) +'');
						
					Date.prototype.format = function(fmt) {// 时间格式转换(当前时间)
							var o = { 
								"M+" : this.getMonth()+1,                 //月份 
								"d+" : this.getDate(),                   //日 
								"h+" : this.getHours(),                   //小时 
								"m+" : this.getMinutes(),                 //分 
								"s+" : this.getSeconds(),                 //秒 
								"q+" : Math.floor((this.getMonth()+3)/3), //季度 
								"S"  : this.getMilliseconds()             //毫秒 
							}; 
							if(/(y+)/.test(fmt)) {
									fmt=fmt.replace(RegExp.$1, (this.getFullYear()+"").substr(4 - RegExp.$1.length)); 
							}
							for(var k in o) {
							if(new RegExp("("+ k +")").test(fmt)){
									fmt = fmt.replace(RegExp.$1, (RegExp.$1.length==1) ? (o[k]) : (("00"+ o[k]).substr((""+ o[k]).length)));
								}
							}
							return fmt; 
						}     
						
					var items = new Date().format("yyyy-MM-dd hh:mm:ss");

					var key = ''
					key = $(this).attr("keywords").split(',')
					if( key[0] == 'null' || '') {
						key = ['云南']
					}
					var p  = [{"must":key,"any":[],"not":[]}]
					var params = {};
					params.keywords = p;
					params.endTime = $(this).attr("topic_time");
					//	审核 & 任务概览
					if($(this).attr("status") == "1") {
						$(".topic_look_btn").hide();
						$(".content-boxs-task").show();
						$(".tableList>li:last-child").show();
						task_overview(); // 任务概况
						topic_rank(params); // 报道量排行
						Volume_trend(params); // 报道量趋势
						dispatch_text(params); //发文量
						getNewslist(params);	// 网络素材
						videoListBox(params); // 自有稿件
					} else {
						$(".topic_look_btn").show();
						$(".content-boxs-task").hide();
						$(".tableList>li:last-child").hide()
						topic_rank(params); // 报道量排行
						Volume_trend(params); // 报道量趋势
						dispatch_text(params); //发文量
						getNewslist(params);  // 网络素材
						videoListBox(params); // 自有稿件
					}
					
				});
			} else {
				$("#topic_list").html("<img style='display:block;margin:60px auto 50px auto;width:auto;' src='img/bt-nodata-now.png' />")
			}
		})
	}

	function	dateFormatter(str) {
		//默认返回yyyy-MM-dd HH-mm-ss
		// var hasTime = arguments[1] != false ? true : false;//可传第二个参数false，返回yyyy-MM-dd
		var d = new Date(str);
		var year = d.getFullYear();
		var month = d.getMonth() + 1 < 10 ? "0" + (d.getMonth() + 1) : d.getMonth() + 1;
		var day = d.getDate() < 10 ? "0" + d.getDate() : d.getDate();
		var hour = d.getHours() < 10 ? "0" + d.getHours() : d.getHours();
		var minute = d.getMinutes() < 10 ? "0" + d.getMinutes() : d.getMinutes();
		var second = d.getSeconds() < 10 ? "0" + d.getSeconds() : d.getSeconds();
		// if(hasTime){
		//     return [year, month, day].join('-') + " " + [hour, minute, second].join(':');
		// }else{
		return [year, month, day].join('-') + " " + [hour, minute, second].join(':');  //[year, month, day,].join("-");
		// }
	}
//	添加选题
$("#topic_list_add").unbind("click").click(function () {
	$("#add_edit_topicModal_box").empty();
	$("#add_edit_topicModal_box").load("page/report/mod1/dynamic/add_edit_topic.html",function () {
		$("#add_edit_topicModal").modal("show");
		$("#topicModal_name").html("添加选题");
		// 点击添加关键字
		$("#topicModal_add").unbind('click').click(function() {
			let datalist = $("#topicModal_input_name").val()
			$.post("topicSelPlan/addSelectTopic",{name: datalist},function(res){
				var str = ''
				for (data in res) {
					var item = res[data];
					var num = item.length;
					// for (data in res) {
					// 	var ber = 1;
					// 	$("#topicModal_keyword").tagsinput('add',item.word)
					// }
					item.forEach(items=>{
						$("#topicModal_keyword").tagsinput('add',items.word)
						str+=items.word+','
					})
					// var reg=/,$/gi;
					// str=str.replace(reg,"");
				}
				var keyword = str
				// / $("#topicModal_keyword").tagsinput('add',keyword)
				// $("#topicModal_keyword").val(keyword)
				// $(".bootstrap-tagsinput>input").val(str)

			})	
		})
		//	添加
		$(".topicModal_sure").unbind("click").click(function () {
			let param = get_topic_param();
			if(!param) {
				return;
			}
			$.post("topicSelPlan/addSelectTopic",param,function (data) {
				if(data.status == 1) {
					$("#add_edit_topicModal").modal("hide");
					load_topic_list();
				}
			});
		});
	});
});

//	编辑选题
$(".topic_edit_btn").unbind("click").click(function () {
	$("#add_edit_topicModal_box").empty();
	$("#add_edit_topicModal_box").load("page/report/mod1/dynamic/add_edit_topic.html",function () {
		$("#add_edit_topicModal").modal("show");
		$("#topicModal_name").html("编辑选题");
		
		$("#topicModal_input_name").val($("#topic_list").find(".active").find(".content").text());
		$("#topicModal_des").val($("#topic_list").find(".active").attr("topic_des"));
		let keywords = $("#topic_list").find(".active").attr("keywords");
		if(keywords == null || keywords == "null") {
			keywords = "";
		}
		$('#topicModal_keyword').tagsinput('add',keywords);
		// $('.bootstrap-tagsinput>input').val(keywords);
		//	编辑
		$(".topicModal_sure").unbind("click").click(function () {
			let param = get_topic_param();
			param.id = $("#topic_list").find(".active").attr("topic_id");
			if(!param) {
				return;
			}
			$.post("topicSelPlan/updateSelectTopic",param,function (data) {
				if(data.status == 1) {
					$("#add_edit_topicModal").modal("hide");
					load_topic_list();
				}
			});
		});
	});
});

//	审核
$(".topic_look_btn").unbind("click").click(function () {
	$.post("topicSelPlan/getStatus",{id:$("#topic_list").find(".active").attr("topic_id")},function (data) {
		if(data.status == 1) {
			load_topic_list();
		}
	});
});

//	tab
$(".tableList").find("li").unbind("click").click(function () {
	$(this).addClass("active").siblings().removeClass("active");
		var dom = $("#tableList_box > .news-list")
		var val = $(this).attr('link')
		$.each(dom,function(i,Val){
		if( $(Val).attr('link') == val){
			$(Val).attr('link',$(this).attr('link')).css('display','block').siblings().css('display','none')
		}
	})
});


//	删除
$(".topic_delete_btn").unbind("click").click(function () {
	swal({
        title:"确定删除该选题吗？",
        text:"",
        type:"warning",
        showCancelButton:true,//是否显示取消按钮
        cancelButtonText:'取 消',//按钮内容
        cancelButtonColor:'#b9b9b9',
        showConfirmButton:true,
        confirmButtonText:'确 认',
        confirmButtonColor:"#dd6b55",
        closeOnConfirm:false,
        closeOnCancel:true
    },function(isConfirm){
        if(isConfirm) {
         	$.post("topicSelPlan/delSelectTopic",{id:$("#topic_list").find(".active").attr("topic_id")},function (data) {
				if(data[0].message == "success") {
					swal({
					   title: "删除成功!",
					   type: "success",
					   timer: 1500, 
					   showConfirmButton: false 
					});
					load_topic_list();
				} else {
					swal({
					   title: "删除失败!",
					   type: "error",
					   timer: 1500, 
					   showConfirmButton: false 
					});
				}
			})
        } else {
        	$(".sweet-alert").fadeOut(100);
			$(".sweet-overlay").fadeOut(100);
        }
    });
});

function get_topic_param() {
	let param = {};
	param.name = $.trim($("#topicModal_input_name").val());
	param.des = $.trim($("#topicModal_des").val());
	param.keywords = $("#topicModal_keyword").val();
	param.user_id = 1;
	if(param.name == "") {
		swal({
		   title: "请输入选题名称!",
		   type: "waring",
		   timer: 1500, 
		   showConfirmButton: false 
		});
		return false;
	}
	return param;
}

// 发文量

function dispatch_text (params) {
	var startTime = [new Date().getTime() - 3600 * 1000 * 24 * 90, new Date()]
	var Data = params;
	Data.pageNo = 1;
	Data.pageSize = 1;
	Data.startTime = dateFormatter(startTime[0])
	$.post("topicSelPlan/infoCount_self",{params:JSON.stringify(Data)},function (data) {
		if (data.status == 'ok') {
			var data = JSON.parse(data.data);
			$(".file-t").empty();
			var str = '<p>'+'发文量'+'</p>'+
					'<p>'+'<span style="font-size: 50px;font-weight: 700;">'+data.total +'</span>'+ '篇'+'</p>'	
			$(".file-t").append(str)
		}
	})

}

//	任务概览
function task_overview() {
	$.post("topicSelPlan/getCountTask",function (data) {
		var data_arr = [{
		    "name": "已完成的任务",
		    "value": data[0].yiwancheng[0]['count(*)']
		}, {
		    "name": "进行中的任务",
		    "value": data[1].jinxingshi[0]['count(*)']
		}]
		let myChart = echarts.init(document.getElementById('topic_chart_task'));
		
		let option = {
			 color: ['#A0CE3A', '#c93931'],
		    title : {
		        text: "共发布"+data[2].zongji[0]['count(*)']+"个任务",
		        x:'right'
		    },
		    legend: {
		        orient: 'vertical',
		        x: 'left',
		        top: 10,
		        data:['已完成的任务','进行中的任务']
		    },
		    series: [
		        {
		            name:'任务',
		            type:'pie',
		            radius: ['40%', '55%'],
		            label: {
		               	normal: {
		               		formatter : function (data) {
		               			return data.name+" : "+data.value
		               		},
		               		textStyle: {
		                        fontSize: '16',
		                        fontWeight: 'bold'
		                    }
		               	},
		                emphasis: {
		                    show: true,
		                    textStyle: {
		                        fontSize: '20',
		                        fontWeight: 'bold'
		                    }
		                }
		            },
		            data:data_arr
		        }
		    ]
		};
		myChart.clear();
		myChart.setOption(option);
	});
}

//	报道量排行
function topic_rank(params) {
	var startTime = [new Date().getTime() - 3600 * 1000 * 24 * 90, new Date()]
	var Data = params
	Data.startTime = dateFormatter(startTime[0])
	$.post("topicSelPlan/siteCount_topic",{params:JSON.stringify(Data)},function (data) {
		$("#topic_chart_rank").empty();
		var data = JSON.parse(data.data)
		var num = 0;
		if(data.length == 0){
			$("#topic_chart_rank").append('<div class="NoData">'+'暂无数据'+'</div>')
		} else {
			$(".NoData").css({"display":"none"})
			data.forEach(function(item) {
				num++;
				var str = '<div class="media-list">'+
						'<div class="icons '+'icon'+num+'">'+'<span>'+num+'</span>'+'</div>'+
						'<div class="title">'+item.name+'</div>'+
						'<div class="num">'+ item.value +'篇'+'</div>'+
					'</div>'
				$("#topic_chart_rank").append(str)
			})
		}
		
	})
}


// 报道量趋势
function Volume_trend(params) {
	var startTime = [new Date().getTime() - 3600 * 1000 * 24 * 7, new Date()]
	var Data = params
	Data.startTime = dateFormatter(startTime[0])
	$("#topic_chart_trend").css({"display":"none"})
	$(".loading1").show()
	$(".NoData1").css({"display":"none"})
	$.post("topicSelPlan/infoCount_topic",{params:JSON.stringify(Data)},function (data) {
		if(data.data == '{}'){	
			$(".loading1").css({"display":"none"})
			$(".NoData1").show()
			$("#topic_chart_trend").css({"display":"none"})
		} else {
			$(".loading1").css({"display":"none"})
			$(".NoData1").css({"display":"none"})
			$("#topic_chart_trend").show()
			var data = JSON.parse(data.data)
			let myChart = echarts.init(document.getElementById('topic_chart_trend'));
			let option = {
				// color: ['#A0CE3A', '#31C5C0'],
				// title: {
				// 	text: '报道量趋势',
				// },
				tooltip: {
					trigger: 'axis'
				},
				legend: {
					orient: 'horizontal', // 'vertical'
					x: 'right', // 'center' | 'left' | {number},
					y: 'top', // 'center' | 'bottom' | {number}

					data:['报道量']
				},
				grid: {
					left: '4%',
					right: '4%',
					bottom: '10%',
					containLabel: true
				},
				toolbox: {
					
				},
				xAxis: {
					type: 'category',
					boundaryGap: false,
					data: data.delTime
				},
				yAxis: {
					type: 'value'
				},
				series: [
				{
					name:'报道量',
					type:'line',
					stack: '总量',
					data:data.count,
				}
				]
			};
			myChart.clear();
			myChart.setOption(option);
		}
	})
}

  // 网络素材列表
function getNewslist(params) {
	var startTime = [new Date().getTime() - 3600 * 1000 * 24 * 90, new Date()]
	var Data = params
	Data.pageNo = 1;
	Data.pageSize = 10;
	Data.startTime = dateFormatter(startTime[0])
	$("#videoListBox1").css({"display":"none"})
	$(".loading2").show();
	$.post("topicSelPlan/searchInfos_all",{params:JSON.stringify(Data)},function (data) {
		$(".loading2").css({"display":"none"});
		$("#videoListBox1").show();
		$("#videoListBox1").empty();
		var data = JSON.parse(data.data).data
		if (data.length == 0) {
			$("#videoListBox1").append('<div class="NotAvailable">'+'暂无数据'+'</div>')
		} else {
			$(".NotAvailable").css({"display":"none"})
			data.forEach(function (item) {
				if (item.author == null || '') {
					item.author = '其他'
				}
				if (item.host == null || '') {
					item.host = '未知'
				}
				if ( item.tags == null || '') {
					item.tags = '其他媒体'
				}
				if (item.mediaLevel == '1') {
					item.mediaLevel = '中央媒体'
				} else if (item.mediaLevel == '2') {
					item.mediaLevel = '省级媒体'
				} else if (item.mediaLevel == '3') {
					item.mediaLevel = '地市媒体'
				} else if (item.mediaLevel == '4') {
					item.mediaLevel = '商业门户'
				} else if (item.mediaLevel == '5') {
					item.mediaLevel = '综合网站'
				} else if (item.mediaLevel == '6') {
					item.mediaLevel = '行业媒体'
				} else if (item.mediaLevel == '7') {
					item.mediaLevel = '港澳台'
				} else if (item.mediaLevel == '8') {
					item.mediaLevel = '境外主流'
				} else if (item.mediaLevel == '9') {
					item.mediaLevel = '境外重要'
				} else if (item.mediaLevel == '10') {
					item.mediaLevel = '境外异见'
				} else {
					item.mediaLevel = '其他媒体'
				};
		
				if (item.polarity=='1') {
					item.polarity = '正面'
				} else if (item.polarity=='2') {
					item.polarity = '负面'
				} else {
					item.polarity = '中立'
				};
				if(item.commentNum = 'null'|| ''){
					item.commentNum = 0
				}
				// else {
				// 	item.commentNum = item.commentNum
				// }
				var str = '<div style="display:flex; align-items: center;">'+
						'<a target="_blank" href="'+item.sourceUrl+'">'+
							'<div class="infoTitle textContantBoxFunc" title="'+item.title+'">'+item.title +'</div>'+
						'</a>'+
						'</div>'+
						'<div style="cursor: pointer;">'+
							'<a target="_blank" href="'+item.sourceUrl+'">'+
								'<div class="textContantBox textContantBoxTop textContantBoxFunc">'+item.summaries+'</div>'+
								'<div class="textContantBox textContantBoxBottom textContantBoxFunc">'+item.hitStence+'</div>'+
								'<div class="textContantBox textContantBoxContent textContantBoxFunc" style="display: none;">'+item.content+'</div>'+
							'</a>'+
						'</div>';
				var str_html = '<div class="listTagBox">' + 
								'<span class="tagBox">' + 
								'<span class="tagBar">' + item.tags + '</span>' +
								'</span>' +
							'</div>';
				var bottomBar = '<div class="bottomBar">' + 
								'<span class="pull-left dataSource">'+
								'<i class="fa fa-globe" style="color: #1D4FD4;" data-toggle="tooltip" data-placement="bottom" title="" data-original-title="original">' + '</i>' +
								'<span style="padding:0;border:none;" data-toggle="toggle" data-placement="placement" title="" data-original-title="">'+item.author+'</span>'+
								'</span>'+
								'<span class="pull-left dataDate" data-toggle="tooltip" data-placement="bottom" title="" data-original-title="时间">'+item.pubtime+'</span>'+
								'<span class="pull-left interactData" data-toggle="tooltip" data-placement="bottom" title="" data-original-title="互动量">'+'互动量：'+item.commentNum+'</span>'+
								'<span class="pull-left MediaLevelData" data-toggle="tooltip" data-placement="bottom" title="" data-original-title="媒体级别">' + item.mediaLevel+'</span>'+
								'<span class="pull-left emotion">'+
								'<span class="pull-left emotionTypeSelect dropdown-toggle front" data-toggle="dropdown">'+
									'<span data-toggle="tooltip" class="xySelect" data-placement="bottom" title="" data-type="news_info" data-index="major_info_201904" data-uid="a054b8ace09a229cd3d6ee0789455161" data-polarity="1" data-original-title="情感">'+item.polarity+'</span>' +
								'</span>' +
								'</span>' + 
								'<a target="_blank" href="'+item.sourceUrl+'" class="pull-right linkUrl" data-toggle="tooltip" data-placement="bottom" title="" data-original-title="源链接">'+
								'<i class="iconfont icon-lianjie1" style="font-size: 17px;">'+'</i>'+
								'</a>'+
							'</div>';
				$("#videoListBox1").append('<div class="mainInfoListHoverBox">'+ '<div class="mainInfoList clearfix data-uuid="'+item.uuid+'">'+ '<div class="mainInfo">' + str +str_html + bottomBar +'</div>'+ '</div>'+ '</div>')
			})
		}
	  // DetailsPage()
	})
  };
  
  //自有稿件
  function videoListBox(params) {	
		var startTime = [new Date().getTime() - 3600 * 1000 * 24 * 90, new Date()]
		var Data = params
		Data.pageNo = 1;
		Data.pageSize = 10;
		Data.startTime = dateFormatter(startTime[0])
		$("#videoListBox2").css({"display":"none"})
		$(".loading3").show()
		$.post("topicSelPlan/searchInfos_self",{params:JSON.stringify(Data)},function (data) {
			$(".loading3").css({"display":"none"})
			$("#videoListBox2").show()
			$("#videoListBox2").empty();
			var data = JSON.parse(data.data).data
			if(data.length == 0){
				$("#videoListBox2").append('<div class="NotAvailable1">'+'暂无数据'+'</div>')
			} else {
				$(".NotAvailable").css({"display":"none"})
				data.forEach(function (item) {
					if (item.author == null || '') {
						item.author = '其他'
					} 
					if (item.host == null || '') {
						item.host = '未知'
					}
					if ( item.tags == null || '') {
						item.tags = '其他媒体'
					}
					if (item.mediaLevel == '1') {
						item.mediaLevel = '中央媒体'
					} else if (item.mediaLevel == '2') {
						item.mediaLevel = '省级媒体'
					} else if (item.mediaLevel == '3') {
						item.mediaLevel = '地市媒体'
					} else if (item.mediaLevel == '4') {
						item.mediaLevel = '商业门户'
					} else if (item.mediaLevel == '5') {
						item.mediaLevel = '综合网站'
					} else if (item.mediaLevel == '6') {
						item.mediaLevel = '行业媒体'
					} else if (item.mediaLevel == '7') {
						item.mediaLevel = '港澳台'
					} else if (item.mediaLevel == '8') {
						item.mediaLevel = '境外主流'
					} else if (item.mediaLevel == '9') {
						item.mediaLevel = '境外重要'
					} else if (item.mediaLevel == '10') {
						item.mediaLevel = '境外异见'
					} else {
						item.mediaLevel = '其他媒体'
					};
			
					if (item.polarity=='1') {
						item.polarity = '正面'
					} else if (item.polarity=='2') {
						item.polarity = '负面'
					} else {
						item.polarity = '中立'
					};
					if(item.commentNum = 'null'|| ''){
						item.commentNum = 0
					}
					var str = '<div style="display:flex; align-items: center;">'+
								'<a target="_blank" href="'+item.sourceUrl+'">'+
									'<div class="infoTitle textContantBoxFunc" title="'+item.title+'">'+item.title +'</div>'+
								'</a>'+
							'</div>'+
							'<div style="cursor: pointer;">'+
								'<a target="_blank" href="'+item.sourceUrl+'">'+
									'<div class="textContantBox textContantBoxTop textContantBoxFunc">'+item.summaries+'</div>'+
									'<div class="textContantBox textContantBoxBottom textContantBoxFunc">'+item.hitStence+'</div>'+
									'<div class="textContantBox textContantBoxContent textContantBoxFunc" style="display: none;">'+item.content+'</div>'+
								'</a>'
							'</div>';
					var str_html = '<div class="listTagBox">' + 
									'<span class="tagBox">' + 
										'<span class="tagBar">' + item.tags + '</span>' +
									'</span>' +
									'</div>';
					var bottomBar = '<div class="bottomBar">' + 
									'<span class="pull-left dataSource">'+
										'<i class="fa fa-globe" style="color: #1D4FD4;" data-toggle="tooltip" data-placement="bottom" title="" data-original-title="网站">' + '</i>' +
										'<span style="padding:0;border:none;" data-toggle="toggle" data-placement="placement" title="" data-original-title="'+item.author+'">'+item.author+'</span>'+
									'</span>'+
									'<span class="pull-left dataDate" data-toggle="tooltip" data-placement="bottom" title="" data-original-title="时间">'+item.insertTime+'</span>'+
									'<span class="pull-left interactData" data-toggle="tooltip" data-placement="bottom" title="" data-original-title="互动量">'+'互动量：'+(item.commentNum ? item.ttransmit:item.commentNum)+'</span>'+
									'<span class="pull-left MediaLevelData" data-toggle="tooltip" data-placement="bottom" title="" data-original-title="媒体级别">' + item.mediaLevel+'</span>'+
									'<span class="pull-left emotion">'+
										'<span class="pull-left emotionTypeSelect dropdown-toggle front" data-toggle="dropdown">'+
										'<span data-toggle="tooltip" class="xySelect" data-placement="bottom" title="" data-type="news_info" data-index="major_info_201904" data-uid="a054b8ace09a229cd3d6ee0789455161" data-polarity="1" data-original-title="情感">'+item.polarity+'</span>' +
										'</span>' +
									'</span>' + 
									'<a target="_blank" href="'+item.sourceUrl+'" class="pull-right linkUrl" data-toggle="tooltip" data-placement="bottom" title="" data-original-title="源链接">'+
										'<i class="iconfont icon-lianjie1" style="font-size: 17px;">'+'</i>'+
									'</a>'+
									'</div>';
					$("#videoListBox2").append('<div class="mainInfoListHoverBox">'+ '<div class="mainInfoList clearfix data-uuid="'+item.uuid+'">'+ '<div class="mainInfo">' + str +str_html + bottomBar +'</div>'+ '</div>'+ '</div>')
				})
			}
				// DetailsPage()
		})
  };
  
  // 弹出详情
  function DetailsPage(){
		$('.mainInfo').click(function (e) {
			$(this).addClass("active").siblings().removeClass("active")
			$("#listInfoModulTitle").html($(this).attr("title"))
			$("#listInfoModulContent").html($(this).attr("content"))
			// $(".email-body").html($(this).attr("innerHTML"))
			// $("#listInfoModulTop").html($(this).find(".bottomBar").text())
			// $("#listInfoModulTop").append(bottomBar)
			// $('.Popup').show(2000).animate({"width":"770px"}).css({"position":"absolute:","right":"770px"})
			$('.Popup').css({"position":"absolute:","right":"0px","display":"block"}).show(2000)
		})
  }
	//关闭详情 
	closeButton()
	function closeButton(){
		$(".listInfoModulClose").click(function(e){
			$('.Popup').css({"position":"absolute:","right":"-772px","display":"none"}).hide(2000)
		})
	}
  
  // 任务管理
  function Task_management() {
	var param = {
  
	}
	$.post("topicSelPlan/findName",param,function (data) {
  
	})
  }
  
  // 空白关闭
  $(document).click(function (e) {
		e.stopPropagation();
	// $(".mainInfo").removeClass("active"); // 详情关闭
	// $('.mainInfoBigBox .multiselect').slideUp(200);
	// $('.mainInfoBigBox .multiselect').parents('.sortBtn').find('i.fa').removeClass('fa-caret-up').addClass('fa-caret-down').data('sort','desc');
  });
  
  //创建任务
  $('.addList').on('click',function (e) {
	e.stopPropagation();
	// $('#myModal3').show()
  })
  
  
