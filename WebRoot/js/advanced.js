/**
 * @created :  2018/05/16
 * @author  :  qyc
 * @desc    : 
 */

/* --------------- mod1 --------------- */
// 历史记录
// if (localStorage.getItem('searchHistory') != null) {
// var historyHtml='';
// JSON.parse(localStorage.getItem('searchHistory')).forEach(function (item,i){
// historyHtml+='<a>'+item+'</a>';
// });
// if(historyHtml){
// historyHtml+='<span id="clearLocalStorage"><i class="iconfont icon-lajitong"
// data-toggle="tooltip" data-placement="bottom" title="清空" style=" position:
// relative;top: 2px;font-size:14px;"></i></span>';
// $('.labelContent').html(historyHtml);
// $('.history-search-box').show();
// }
// }
$('.search-input-box')
		.on(
				'focus',
				'.search-input',
				function() {
					var historyHtml = '';
					if (localStorage.getItem('searchHistory') != null) {
						JSON
								.parse(localStorage.getItem('searchHistory'))
								.forEach(
										function(item, i) {
											historyHtml += '<li class="clearfix">'
													+ '<span class="pull-left"><a data-toggle="tooltip" data-placement="bottom" title="'
													+ item
													+ '">'
													+ item
													+ '</a></span>'
													+ '<span class="pull-right"><i class="iconfont icon-lajitong delLocalStorage" data-toggle="tooltip" data-placement="bottom" title="删除"></i></span>'
													+ '</li>';
										});
						if (historyHtml) {
							historyHtml = '<li class="clearfix">'
									+ '<span class="pull-left">历史记录</span>'
									+ '<span class="pull-right" id="clearLocalStorage">清空</span>'
									+ '</li>' + historyHtml;
							$('#history-search-box-ul').html(historyHtml);
							$('#history-search-box-ul [data-toggle="tooltip"]').tooltip();
						}
					}
					if (historyHtml) {
						$('.history-search-box').slideDown(200);
					}
				}).on('click', function(e) {
			e.stopPropagation();
		});
$(document).on('click', function() {
	$('.history-search-box').slideUp(200);
});
// 清空记录
$('.search-input-box').on('click', '#clearLocalStorage', function() {
	swal({
		title : "确定清空吗？",
		type : "warning",
		showCancelButton : true,
		confirmButtonColor : "#DD6B55",
		confirmButtonText : "清空",
		cancelButtonText : "取消",
		closeOnConfirm : false
	}, function() {
		swal.close();

		$('.history-search-box').slideUp(200);
		localStorage.removeItem('searchHistory');
	});
});
// 删除记录
$('.search-input-box').on('click', '.delLocalStorage', function() {
	delHistory($(this).parent().siblings('.pull-left').text());
	$(this).parents('li').remove();
});
// 删除历史检索
function delHistory(val) {
	var oldHistory = JSON.parse(localStorage.getItem('searchHistory'));
	oldHistory.forEach(function(item, i) {
		if (item == val) {
			oldHistory.splice(i, 1);
		}
	});
	if (JSON.stringify(oldHistory) == '[]') {
		localStorage.removeItem('searchHistory');
		$('.history-search-box').slideUp(200);
	} else {
		localStorage.setItem('searchHistory', JSON.stringify(oldHistory));
	}
}

mod1_Table = $('#mod1_Table')
		.DataTable(
				{
					// "destroy": true,
					"processing" : true,
					"serverSide" : true,
					"ajax" : {
						"url" : 'search_al/mod1/searchParams',
						"dataSrc" : 'data',
						"data" : function(data) {
							data.name = $('.tableSearhInput').val().trim();
						}
					},

					"columns" : [
							{
								"data" : null,
								render : function() {
									return ''
								}
							},
							{
								"data" : null,
								render : function(data, type, full, meta) {
									return '<span class="xyClassFon" onclick="ChangeData('
											+ meta.row
											+ ')">'
											+ data.name
											+ '</span>'
								}
							},
							{
								"data" : null,
								render : function(data, type, full, meta) {
									return data.is_analysis == 0 ? ''
											: '<span class="stateBox first xyClassFon" onclick="ChangeData('
													+ meta.row + ')">分析</span>'
								}
							},
							{
								"data" : null,
								render : function(data, type, full, meta) {
									return '<span class="xyClassFon" onclick="ChangeData('
											+ meta.row
											+ ')">'
											+ data.update_time + '</span>'
								}
							},
							{
								"data" : null,
								render : function(data) {
									return "<td class='inbox-icon textCenter tableOptionBox'>"
											+
											// "<div class='btn-group'><a
											// data-toggle='dropdown' class=''
											// aria-expanded='false'><svg
											// t='1526958579337' class='icon'
											// style='fill:#01c0c8' viewBox='0 0
											// 5120 1024' version='1.1'
											// xmlns='http://www.w3.org/2000/svg'
											// p-id='2393'
											// xmlns:xlink='http://www.w3.org/1999/xlink'
											// width='24'
											// height='24'><defs><style
											// type='text/css'></style></defs><path
											// d='M3963.904 512c0 283.136
											// 221.696 512 495.616 512 273.408 0
											// 495.104-228.864 495.104-512
											// 0-282.624-221.696-512-495.104-512-273.92
											// 0-495.616 229.376-495.616
											// 512zM1981.44 512c0 283.136
											// 221.696 512 495.616 512 273.408 0
											// 495.104-228.864 495.104-512
											// 0-282.624-221.696-512-495.104-512-273.92
											// 0-495.616 229.376-495.616 512z'
											// fill='' p-id='2394'></path><path
											// d='M1981.952 512c0 283.136
											// 221.696 512 495.616 512 273.408 0
											// 495.104-228.864 495.104-512
											// 0-282.624-221.696-512-495.104-512-273.92
											// 0-495.616 229.376-495.616 512zM0
											// 512c0 283.136 221.696 512 495.616
											// 512 273.408 0 495.104-228.864
											// 495.104-512
											// 0-282.624-221.696-512-495.104-512C221.696
											// 0 0 229.888 0 512z' fill=''
											// p-id='2395'></path></svg></a><ul
											// class='dropdown-menu
											// dropdown-menu-right'>"+(item.is_analysis==0?"<li><a
											// data-autoId='"+item.auto_id+"'
											// class='tableAddBtn'>添加分析任务</a></li>":"<li><a
											// data-analysis-autoId='"+item.analysisAutoId+"'
											// class='toSearchBtn'>查看分析任务</a></li>")+"<li><a
											// data-autoId='"+item.auto_id+"'
											// class='tableCopyBtn'>复制</a></li><li><a
											// data-autoId='"+item.auto_id+"'
											// data-is-analysis='"+item.is_analysis+"'
											// class='tableDelBtn'>删除</a></li></ul></div>"+

											"<span class=\"tdOptionBtn tableAddBtn add1\" data-autoId='"
											+ data.auto_id
											+ "' data-toggle=\"tooltip\" data-placement=\"bottom\" title=\"添加分析\"><i class=\"iconfont icon-tianjia\"></i></span>"
											+ "<span class=\"tdOptionBtn add2\" data-autoId='"
											+ data.auto_id
											+ "' data-toggle=\"tooltip\" data-placement=\"bottom\" title=\"添加头条\"><i class=\"iconfont icon-toutiao\"></i></span>"
											+ "<span class=\"tdOptionBtn tableCopyBtn copy\" data-autoId='"
											+ data.auto_id
											+ "' data-toggle=\"tooltip\" data-placement=\"bottom\" title=\"复制\"><i class=\"iconfont icon-fuzhi\"></i></span>"
											+ "<span class=\"tdOptionBtn tableDelBtn del\" data-autoId='"
											+ data.auto_id
											+ "' data-is-analysis='"
											+ data.is_analysis
											+ "' data-toggle=\"tooltip\" data-placement=\"bottom\" title=\"删除\"><i class=\"iconfont icon-chuyidong\"></i></span>"
											+ "</td>"
								}
							} ],
							"fnDrawCallback": function (Settings) {
								// console.log(Settings);
								$('#mod1_tbodyId [data-toggle="tooltip"]').tooltip();
							},
					"language" : {
						"sProcessing" : "处理中...",
						"sLengthMenu" : "显示 _MENU_ 项结果",
						"sZeroRecords" : "没有匹配结果",
						"sInfo" : "显示第 _START_ 至 _END_ 项结果，共 _TOTAL_ 项",
						"sInfoEmpty" : "显示第 0 至 0 项结果，共 0 项",
						"sInfoFiltered" : "(由 _MAX_ 项结果过滤)",
						"sInfoPostFix" : "",
						"sSearch" : "搜索:",
						"sUrl" : "",
						"sEmptyTable" : "暂无数据",
						"sLoadingRecords" : "载入中...",
						"sInfoThousands" : ",",
						"oPaginate" : {
							"sFirst" : "首页",
							"sPrevious" : "上页",
							"sNext" : "下页",
							"sLast" : "末页"
						},
						"oAria" : {
							"sSortAscending" : ": 以升序排列此列",
							"sSortDescending" : ": 以降序排列此列"
						}
					},
					"ordering" : false,
					"autoWidth" : false,
					"aaSorting" : [ // 清除默认第一列小箭头
					[ 0 ] ],
					// "order": [[ 4, "desc" ]],
					"columnDefs" : [ {
						"orderable" : false,
						"targets" : [ 0, 2, 4 ]
					}, {
						"width" : "3%",
						"targets" : 0
					}, {
						"width" : "20%",
						"targets" : 1
					} ],
					"lengthMenu" : [ 8 ], // 默认分页选择第一个
					"lengthChange" : false, // 分页可调
					"searching" : true,
					"dom" : 'lBrtip', // 不显示搜索框

				});
function ChangeData(Row) {

	var data = $('#mod1_Table').DataTable().rows(Row).data()[0]
	var sessionData = {};
	var sessionKey = 'autoId_' + data.auto_id;
	sessionData.name = data.name;
	sessionData.search_terms = data.search_terms;
	sessionStorage.setItem(sessionKey, JSON.stringify(sessionData));
	window.location.href = 'search_al/mod2?type='
			+ (data.search_level == 0 ? 'simple' : 'advanced') + '&autoId='
			+ data.auto_id;
};

// 添加分析
$('#mod1_Table').on('click', '.tableAddBtn', function() {
	event.stopPropagation()
	var that = this;
	$('#analysis_select').find('option').each(function() {
		if ($(that).attr('data-autoId') == $(this).val()) {
			$(this).prop("selected", true);
			$("#analysis_select").prop('disabled', true).trigger("chosen:updated");
			$('#analysisName').val($(this).text()).focus();
		}
	});
	$('#analysisModal .targetBar').find('.modulNum').html('').hide();
	$('#analysisModal .targetBar').find('.modulListBox').html('').hide();
	$('#analysisModal .modalSelectList .file-item').removeClass('active');
	$("#analysisType").val("").trigger("chosen:updated");
	$('#analysisModal').modal('show');
});
// 跳转分析
$('#mod1_Table').on(
		'click',
		'.toSearchBtn',
		function() {
			// console.log($(this).attr('data-autoId'));
			window.location.href = 'analysis_al/mod2?autoId='
					+ $(this).attr('data-analysis-autoId');
		});
// 复制
$('#mod1_Table').on('click', '.tableCopyBtn', function() {
	// console.log($(this).attr('data-autoId'));
	// $(this).css('option','0.8');
	$('.refresh-preloader').show();
	$.ajax({
		type : "get",
		url : "search_al/mod1/copySearchParamById",
		data : {
			id : $(this).attr('data-autoId')
		},
		success : function(data) {
			$('.refresh-preloader').hide();
			if (data.status == 'ok') {
				$('.refresh-preloader').hide();
				toastr["success"]("复制成功");
				mod1_Table.draw(false);
			} else {
				$('.refresh-preloader').hide();
				toastr["error"]("复制失败,请刷新重试");
			}
		},
		error : function(res) {
			$('.refresh-preloader').hide();
			$('.refresh-preloader').hide();
			toastr["error"]("复制失败,请刷新重试");
		}
	});
});
// 删除
$('#mod1_Table').on('click', '.tableDelBtn', function() {
	// console.log($(this).attr('data-autoId'));
	var aoid = $(this).attr('data-autoId');
	if ($(this).attr('data-is-analysis') == 0) {
		swal({
			title : "确定删除吗？",
			type : "warning",
			showCancelButton : true,
			confirmButtonColor : "#DD6B55",
			confirmButtonText : "删除",
			cancelButtonText : "取消",
			closeOnConfirm : false
		}, function() {
			swal.close();

			$.ajax({
				type : "get",
				url : "search_al/mod1/deleteSearchParam",
				data : {
					autoId : aoid
				},
				success : function(data) {
					if (data.status == 'ok') {
						$('.refresh-preloader').hide();
						toastr["success"]("删除成功");
						mod1_Table.draw(false);
					} else {
						$('.refresh-preloader').hide();
						toastr["error"]("删除失败,请刷新重试");
					}
				},
				error : function(res) {
					$('.refresh-preloader').hide();
					toastr["error"]("删除失败,请刷新重试");
				}
			});
		});
	} else {
		toastr["info"]("该搜索存在分析任务，不能直接删除");
	}
});
// 跳转
$('#mod1_Table')
		.on(
				'click',
				'.tableInfoTd',
				function() {
					var sessionData = {};
					var sessionKey = 'autoId_'
							+ $(this).parent().attr('data-autoid');
					sessionData.name = $(this).parent().attr('data-name');
					sessionData.search_terms = $(this).parent().attr(
							'data-search-terms')
					sessionStorage.setItem(sessionKey, JSON
							.stringify(sessionData));
					window.location.href = 'search_al/mod2?type='
							+ ($(this).parent().attr('data-search-level') == 0 ? 'simple'
									: 'advanced') + '&autoId='
							+ $(this).parent().attr('data-autoid');

				});

// table 搜索
$('#searchTopBox').on('click', '.tableSearhIcon', function() {
	$(this).css({
		'left' : '3px'
	}).siblings('.tableSearhInput').css({
		'width' : '150px',
		'padding-left' : '25px',
		'padding-right' : '20px'
	});
});
$('#searchTopBox').on('transitionend webkitTransitionEnd', '.tableSearhInput',function() {
	$(this).focus();
});

$('#searchTopBox').on('keyup', '.tableSearhInput', function() {
	if ($(this).val()) {
		$(this).siblings('.tableSearhDelIcon').show();
	} else {
		$(this).siblings('.tableSearhDelIcon').hide();
	}

	mod1_Table.search($(this).val()).draw();
});
$('#searchTopBox').on('blur', '.tableSearhInput', function() {
	if (!$(this).val()) {
		$(this).siblings('.tableSearhDelIcon').hide();
		$(this).css({
			'width' : '0',
			'padding-left' : '0',
			'padding-right' : '0'
		}).siblings('i.tableSearhIcon').css({
			'left' : '-20px'
		});
	}
	mod1_Table.search($(this).val()).draw();
});
// 删除
$('#searchTopBox').on(
		'click',
		'.tableSearhDelIcon',
		function() {
			$(this).hide().siblings('.tableSearhInput').val('');
			mod1_Table.search($(this).siblings('.tableSearhInput').val())
					.draw();
			$(this).siblings('i.tableSearhIcon').css('left', '-20px').end()
					.siblings('.tableSearhInput').css({
						'width' : '0',
						'padding-left' : '0',
						'padding-right' : '0'
					});
		});

/* --------------- end --------------- */

/* --------------- mod2 --------------- */
var urlAutoId = iFunc.UrlParse().autoId;
$('#mainInfoBigBoxBox').load(
		'dynamic/ipageListBox.html',
		function() {
			// input
			animateInputInit('.sortSearhInput', '.sortSearhIcon',
					'.sortSearhDelIcon', '', function() {
						lishKeywordSearch($.trim($('.sortSearhInput').val()));
					}, function() {
						search_list_load(sourceTabsObj, true);
					});

			// 名称回填
			if (urlAutoId) {
				let data = JSON.parse(sessionStorage.getItem('autoId_'
						+ urlAutoId));
				$('#nameBox').text(data.name);
			}
		});

// 二次媒体
// function multiselectCloseBtnFunc(multiselectArr) {
// 	var newObj = jQuery.extend(true, {}, sourceTabsObj);
// 	newObj.mediaInfo = multiselectArr;
// 	search_list_load(newObj, true);
// }

var listFillData = '';
var urlType = iFunc.UrlParse().type;
if (urlAutoId) {
	listFillData = JSON.parse(sessionStorage.getItem('autoId_' + urlAutoId));
	// 为保存检索条件添加时间
	listFillData.search_terms = JSON.parse(listFillData.search_terms);
//	debugger;
	let $SearchDate = $("#search_date");
	if (listFillData.search_terms.startTime) {
		$SearchDate.attr("data-starttime", listFillData.search_terms.startTime.substring(0,10));
	}
	if (listFillData.search_terms.endTime) {
		$SearchDate.attr("data-endtime", listFillData.search_terms.endTime.substring(0,10));
	}
	listFillData.search_terms = JSON.stringify(listFillData.search_terms);
}


// 普通检索
var repetitionFlag=true;
var tags_option = {
	tagClass: 'label btn btn-primary tags_button tags_button_drag bgc-blue',
	delimiterFlag : false,
	confirmKeys : [13],
	trimValue: true
}
$('.tags_drag').tagsinput(tags_option);
//排除重复关键词
$('.tags_drag').on('beforeItemAdd', function(event) {
	let key =event.item;
	if(repetitionFlag){
		$.each($('.tags_button_drag'), function(i, v) {
			if($(this).text() == key) {
				event.cancel=true;
				toastr["info"]("输入关键词重复");
			}
		});
	}
	setTimeout(() => {
		screenInfoShow();
		ordinaryEditableInit();
	}, 30);
});
$('.tags_drag').on('itemRemoved', function(event) {
  screenInfoShow();
});
// 高级检索
var $drag = false;
var advanced_tags_option = {
	tagClass : 'label btn btn-primary tags_button advanced_tags_button waves bgc-blue',
	trimValue : true,
	delimiterFlag : false,
	confirmKeys : [13]
};
$('#tags_advanced').tagsinput(advanced_tags_option);
$('#tags_advanced').on('itemAdded', function(event) {
	let key = event.item;
	let mark = 0;
	$.each($('.advanced_tags_button'), function(i, v) {
		if ($(this).text() == key) {
			mark++
		}
	});
	if (mark > 1) {
		if (!$drag) {
			$(event.target).tagsinput('remove', key);
		}
	}

	editableInit();
	screenInfoShow();
});
$('#tags_advanced').on('itemRemoved', function(event) {
	screenInfoShow();
});
editableInit();
ordinaryEditableInit();

// 获取城市
var loadLoactionCityStr = '';
loadLoactionCity()
function loadLoactionCity() {
	$.ajax({
		type : "get",
		url : "search_al/mod1/getCitys",
		async : false,
		success : function(data) {
			let cityArray = [ '直辖市', '省份', '自治区', '特别行政区' ];
			loadLoactionCityStr = '';
			$.each(cityArray, function(i, v) {
				loadLoactionCityStr += '<optgroup label="' + v + '">';
				$.each(data[v], function(i, v) {
					loadLoactionCityStr += '<option value="' + v + '">' + v
							+ '</option>';
				});
				loadLoactionCityStr += '</optgroup>'
			});

		}
	})
}

// mod1 列表回填
function searchInfoFillHosts(data) {
	// console.log(data);
	$('#info_adopt_media_cross').find('option').each(function() {
		var that = this;
		if (data.datasourceId) {
			// data.hosts.forEach(function (item,i) {
			// if(item==$(that).val()){
			// $(that).attr("selected","selected");
			// }
			// });
			if (data.datasourceId == $(that).val()) {
				$(that).attr("selected", "selected");
			}
		}

	});
	$('#info_adopt_media_cross').trigger('chosen:updated');// 更新选项

	screenInfoShow();
}
function searchInfoFillCategory(data) {
	$('.checkbox_category').find('input[name="category"]').each(
			function() {
				// var that=this;
				if ($(this).val().split(';')[0]) {
					if (data.category) {
						if (data.category.join(';').indexOf(
							$(this).val().split(';')[0]) != (-1)) {
							$(this).prop("checked", true);
						}
					}
				}
			});
	screenInfoShow();
}
function searchInfoFillFirst(data) {
	if (urlType == 'simple') {
		data.keywords[0].must.forEach(function (item,i) {
			$('#tags_1').tagsinput('add',item);
		});
		data.keywords[0].any.forEach(function (item,i) {
			$('#tags_2').tagsinput('add',item);
		});
		data.keywords[0].not.forEach(function (item,i) {
			$('#tags_3').tagsinput('add',item);
		});
		if (data.dataSource) {
			data.dataSource.forEach(function(item, i) {
				$('.screenBox').find('.checkbox input[name="dataSource"]')
						.each(function() {
							if (item == $(this).val()) {
								$(this).prop("checked", true);
							}
						});
			});
		}
	} else {
		var advancedVal = [];
		// console.log(data);
		data.keywords.forEach(function(item, i) {
			advancedVal.push(advancedKeywordsFill(item));
		});
		// console.log(advancedVal);
		advancedVal.forEach(function (item,i) {
			$('#tags_advanced').tagsinput('add',item);
		});
		$.map(data, function(val, key) {
			if(key=='keywordsPosion'){
				key='place'
			}
			$('.screenBox').find('.checkbox input[name="' + key + '"]').each(function() {
				var that = this;
				val.forEach(function(item, i) {
					if (item == $(that).val()) {
						$(that).prop("checked", true);
					}
				});
			});
		})
		if (data.citys != undefined) {
			loactionCity(loadLoactionCityStr, data.citys)
		}

	}
	screenInfoShow();
}
function loactionCity(str, array) {
	$('#loactionCity').multiselect('destroy');
	let dom = $('#loactionCity');
	dom.empty();
	dom.append(str);
	dom.find('option').attr("selected", false);

	if (array) {
		$.each(array, function(i, v) {
			dom.find('option[value="' + v + '"]').attr("selected", true);
		});
	}

	$('#loactionCity').multiselect({
		buttonClass : 'btn btn-link multiselect-btn',
		enableClickableOptGroups : true,
		enableCollapsibleOptGroups : true,
		enableFiltering : true,
		maxHeight : 200,
		buttonText : function(options, select) {
			if (options.length === 0) {
				return '请选择省市';
			} else if (options.length > 6) {
				return '已选择 ' + options.length + ' 个省市';
			} else {
				var labels = [];
				options.each(function() {
					if ($(this).attr('label') !== undefined) {
						labels.push($(this).attr('label'));
					} else {
						labels.push($(this).html());
					}
				});
				return labels.join(', ') + '';
			}
		}
	});
}

function advancedKeywordsFill(data) {
	var mustVal = data.must.join('&'), anyVal = data.any.join('|'), notVal = data.not
			.join('&'), keywordsHtml = '';
	mustVal = mustVal == '' ? mustVal : '(' + mustVal + ')';
	anyVal = anyVal == '' ? anyVal : '(' + anyVal + ')';
	notVal = notVal == '' ? notVal : ' ! (' + notVal + ')';

	if (mustVal != '' && anyVal != '') {
		anyVal = ' & ' + anyVal;
	}
	keywordsHtml = mustVal + anyVal + notVal;
//	debugger;
	return keywordsHtml;
}

var testData = {
	"language" : [ "1", "6", "2" ],
	"region" : [ "2" ],
	"emotion" : [],
	"keywordsPosion" : [],
	"category" : [ "社会", "society", "общество", "Sociedad", "société",
			"المجتمع", "Sociedade", "教育", "时政" ],
	"hosts" : [ "beijingreview.com.cn", "news.cn,xinhuanet.com" ],
	"keywords" : [ {
		"must" : [ "西安", "无人机" ],
		"any" : [ "乱码", "尬舞", "1374", "失误", "演砸", "亿航", "出岔" ],
		"not" : [ "据新华社西安" ]
	} ],
	"dataSource" : [ "4", "7", "10" ],
	"startTime" : "2018-05-21 00:00:00",
	"endTime" : "2018-05-28 23:59:59",
};
if (urlAutoId) {
	searchInfoFillFirst(JSON.parse(listFillData.search_terms));
	if (urlType == 'simple') {
		setTimeout(function() {
			$('#screenSearchBtn').click();
			// search_click()
		}, 700);
	}
}

$('.fileinput').fileinput();

// editable
function ordinaryEditableInit() {
	$('.tag.tags_button_drag').editable(
			{
				type : 'textarea',
				pk : 1,
				// title: '修改',
				rows : 4,
				placeholder : '不能修改为空',
				placement : 'autoPlace',
				showbuttons : 'bottom',
				mode : 'popup',
				savenochange : true,
				display : function(value, sourceData) {
					// console.log(value);

				},
				success : function(response, newValue) {
					// console.log(this);
					if (newValue) {
						newValue = newValue.replace(/\ +/g, ' ').replace(
								/[\r\n]/g, '');
						$(this).html('');
						$(this).html(newValue).append('<span data-role="remove"></span>');
					}
					// var that = this;
					// setTimeout(function() {
					// $(that).append('<span data-role="remove"></span>');
					// }, 50);
					screenInfoShow();
				},
				selfPositionFlag : true,

			});
}
function editableInit() {
	$('.tag.advanced_tags_button').editable(
			{
				type : 'textarea',
				pk : 1,
				// title: '修改',
				rows : 4,
				placeholder : '不能修改为空',
				placement : 'autoPlace',
				showbuttons : 'right',
				mode : 'popup',
				savenochange : true,
				display : function(value, sourceData) {
					// console.log(value);

				},
				success : function(response, newValue) {
					// console.log(this);
					if (newValue) {
						newValue = newValue.replace(/\ +/g, ' ').replace(
								/[\r\n]/g, '');
						$(this).html('');
						$(this).html(newValue).append(
								'<span data-role="remove"></span>');
					}
					// var that = this;
					// setTimeout(function() {
					// $(that).append('<span data-role="remove"></span>');
					// }, 50);
					screenInfoShow();
				},
				selfPositionFlag : true,

			});
}

// 收藏
// function enshrineFunc(that) {
// $.ajax({
// type: "get",
// url: "search_al/mod2/addOrDelCollection",
// data: {
// uuid: $(that).attr('data-uuid')
// },
// beforeSend: function () {
// $(that).addClass('disabled');
// },
// success: function (data) {
// $(that).removeClass('disabled');
// if(data.status=='ok'){
// if($(that).hasClass('active')){
// $(that).removeClass('active').find('i.fa').removeClass('fa-star').addClass('fa-star-o');
// }else {
// $(that).addClass('active').find('i.fa').removeClass('fa-star-o').addClass('fa-star');
// }
// toastr["success"]("操作成功");
// }else{
// toastr["error"]("删除失败,请刷新重试");
// }
// },
// error: function (res) {
// $(that).removeClass('disabled');
// toastr["error"]("删除失败,请刷新重试");
// }
// });
// }

// 防止关闭
$('.listInfoModul, .textBox, #multiselect').on('click', function(e) {
	e.stopPropagation();
});
// 点击空白关闭的
$(document).on('click', 'body', function() {
	$('.textBox').stop().slideUp(200); // 问号

	$('html').removeClass("overhid");
});

// 父元素和name获取 valStr
function checkboxValStr(container, name, attribute, arr) {
	var isallchecked = true;
	var valArr = [];
	$(container).find("input[name='" + name + "']:gt(0)").each(function() {
		if (!$(this).is(":checked")) {
			isallchecked = false;
		} else {
			// valArr.push($(this).val());
			valArr.push($(this).attr(attribute));
		}
	});
	if (arr) {
		if (isallchecked) {
			return [];
		} else {
			return valArr;
		}
	} else {
		if (isallchecked) {
			return '';
		} else {
			return valArr.join(',');
		}
	}
}
// 所有name的input父元素获取 valData
function checkboxAllValData(container, attribute) {
	var valData = {};
	$(container).find('.checkbox').each(function() {
		if(iFunc.UrlParse().type=='advanced'){
			valData[$(this).find('.labelTitle').text()] = checkboxValStr($(this),$(this).find('input').attr('name'),attribute);
		}else if ($(this).is(':visible')) {
			valData[$(this).find('.labelTitle').text()] = checkboxValStr($(this),$(this).find('input').attr('name'),attribute);
		}
	});
	return valData;
}
// 检索展示条件获取
function screenInfoListBoxData(container) {
	var valData = {};
	$(container).find('.screenInfoList').each(function() {
						var screenInfoTitleVal = $(this).find(
								'.screenInfoTitle').text().replace('：', '');
						var screenInfoTextVal = $(this).find('.screenInfoText')
								.text();
						if (screenInfoTitleVal == '日期') {
							valData.sTime = screenInfoTextVal.split(' 至 ')[0];
							valData.eTime = screenInfoTextVal.split(' 至 ')[1];
						} else {
							valData[screenInfoTitleVal] = screenInfoTextVal == '全部' ? ''
									: screenInfoTextVal;
						}
					});
	return valData;
}
// 获取 input#tags_advanced 的数据
function advancedTagsVal(container) {
	var tagsVal = '';
	$(container).find('span.advanced_tags_button').each(function() {
		tagsVal += $(this).text() + ',';
	});
	return tagsVal.slice(0, -1);
}
// // 获取 input#tags_1,2,3 的数据
function generalTagsArr(container) {
	var tagsVal = '';
	$(container).find('span.tags_button_drag').each(function() {
		tagsVal += $(this).text() + ',';
	});
	return tagsVal.slice(0, -1);
}
// 获取 媒体站点 的数据
function mediaSelectVal(container) {
	var tagsArr = [];
	$(container).find('option:selected').each(function() {
		tagsArr.push($(this).text());
	});
	return tagsArr.join(',');
}

// 高级检索清空
function screenInfoClear(param) {
	$('#tags_advanced').tagsinput('removeAll');
	$('.screenBox').find('.checkbox:gt(0) input').each(function() {
		$(this).prop("checked", false);
	});
	$("#info_adopt_media_cross").val("");
	$("#info_adopt_media_cross").trigger("chosen:updated");
	screenInfoShow();
}

// 检索条件展示
function screenInfoShow() {
	var screenInfoHtml = '';
	var Dom_advancedTags = $('#tags_advanced').prev('.bootstrap-tagsinput')[0];
	var Dom_tags_1 = $('#tags_1').prev('.bootstrap-tagsinput')[0];
	var Dom_tags_2 = $('#tags_2').prev('.bootstrap-tagsinput')[0];
	var Dom_tags_3 = $('#tags_3').prev('.bootstrap-tagsinput')[0];
	// 搜索关键词
	// 普通
	if ($('.general_box').is(':visible')) {
		if (generalTagsArr(Dom_tags_1)) {
			screenInfoHtml += '<span class="screenInfoList keywordBox" data-toggle="tooltip" data-placement="bottom" title="包含全部:'
					+ generalTagsArr(Dom_tags_1)
					+ '"><span class="screenInfoTitle">包含全部：</span><span class="screenInfoText col-blue">'
					+ generalTagsArr(Dom_tags_1) + '</span></span>';
		}
		if (generalTagsArr(Dom_tags_2)) {
			screenInfoHtml += '<span class="screenInfoList keywordBox" data-toggle="tooltip" data-placement="bottom" title="包含任意:'
					+ generalTagsArr(Dom_tags_2)
					+ '"><span class="screenInfoTitle">包含任意：</span><span class="screenInfoText col-blue">'
					+ generalTagsArr(Dom_tags_2) + '</span></span>';
		}
		if (generalTagsArr(Dom_tags_3)) {
			screenInfoHtml += '<span class="screenInfoList keywordBox" data-toggle="tooltip" data-placement="bottom" title="不包含:'
					+ generalTagsArr(Dom_tags_3)
					+ '"><span class="screenInfoTitle">不包含：</span><span class="screenInfoText col-blue">'
					+ generalTagsArr(Dom_tags_3) + '</span></span>';
		}
	} else if ($('.advanced_box').is(':visible') && advancedTagsVal(Dom_advancedTags)) {
		screenInfoHtml += '<span class="screenInfoList keywordBox" data-toggle="tooltip" data-placement="bottom" title="关键词:'
				+ advancedTagsVal(Dom_advancedTags)
				+ '"><span class="screenInfoTitle">关键词：</span><span class="screenInfoText col-blue">'
				+ advancedTagsVal(Dom_advancedTags) + '</span></span>';
	}
	// 搜索条件
	$.map(checkboxAllValData('.screenBox', 'data-val'),function(val, key) {
		// console.log(key+'==='+val);
		if (val) {
			screenInfoHtml += '<span class="screenInfoList" data-toggle="tooltip" data-placement="bottom" title="'
					+ key
					+ '：'
					+ val
					+ '"><span class="screenInfoTitle">'
					+ key
					+ '：</span><span class="screenInfoText col-blue">'
					+ val
					+ '</span><i class="fa fa-times screenInfoListDelBtn"></i></span>';
		}/*
				* else { screenInfoHtml+='<span
				* class="screenInfoList" title="'+key+'：全部"><span
				* class="screenInfoTitle">'+key+'：</span><span
				* class="screenInfoText">全部</span></span>'; }
				*/
	});

	if ($("#info_adopt_media_cross option:selected").text() && $('.advanced_box').is(':visible')) {
		screenInfoHtml += '<span class="screenInfoList mediaBox" data-toggle="tooltip" data-placement="bottom" title="媒体站点：'
				+ mediaSelectVal('#info_adopt_media_cross')
				+ '" style=""><span class="screenInfoTitle">媒体站点：</span><span class="screenInfoText col-blue">'
				+ mediaSelectVal('#info_adopt_media_cross')
				+ '</span><i class="fa fa-times screenInfoListDelBtn"></i></span>';

	}

	if ($('#search_date').val()) {
		screenInfoHtml += '<span class="screenInfoList dateBox" data-toggle="tooltip" data-placement="bottom" title="日期：'
				+ $('#search_date').val()
				+ '" style=""><span class="screenInfoTitle">日期：</span><span class="screenInfoText col-blue">'
				+ $('#search_date').val() + '</span></span>';
	}

	// 媒体站点筛选联动
	if ($("#info_adopt_media_cross option:selected").text() && $('.advanced_box').is(':visible')) {
		$('.checkbox_source_type').find('input[name="dataSource"]').prop('disabled',true);
	}else if ($("#info_adopt_media_cross option:selected").text()=='') {
		$('.checkbox_source_type').find('input[name="dataSource"]').prop('disabled',false);
	}

	if (screenInfoHtml) {
		$('#screenInfoListBox').html(screenInfoHtml);
		$('#screenInfoListBox2').html(screenInfoHtml);
		$('.screenInfoBox [data-toggle="tooltip"]').tooltip();
	}
}
screenInfoShow();
// ajax 对象拼接
/*
 * var screenData={}; function searchAjaxData() { var
 * Dom_advancedTags=$('#tags_advanced').prev('.bootstrap-tagsinput')[0];
 * screenData.keywards=advancedTagsVal(Dom_advancedTags); $.extend(screenData,
 * checkboxAllValData('.screenBox','value'));
 * screenData.sTime=$('#search_date').val().split(' 至 ')[0];
 * screenData.eTime=$('#search_date').val().split(' 至 ')[1];
 * console.log(screenData); }
 */

// 搜索
function searchFunc(slide, search) {
	// searchAjaxData();
	if (search) {
//		if (searchVo() == 'false') {
//			toastr["info"]("请输入包含关键词或者媒体站点");
//			return;
//		}
	}

	$('html').animate({
		scrollTop : document.body.scrollHeight - $("html").height() - 80
	}, 500);
	// if(slide=='up'){
	// $('.hiddenToggleBox').stop().slideUp();
	// $('#screenSearchBtn').hide();
	// $('#screenBtn').show();
	// screenInfoShow();
	// }else if(slide=='down'){
	// $('.hiddenToggleBox').stop().slideDown();
	// $('#screenSearchBtn').show();
	// $('#screenBtn').hide();
	// }else if(slide=='toggle') {
	// screenInfoShow();
	// $('.hiddenToggleBox').stop().slideToggle();
	// $('#screenSearchBtn').toggle();
	// $('#screenBtn').toggle();
	// }
	if (search) {
		search_click();
	}
}
// 检索btn
$(document).on('click', '#screenSearchBtn', function() {
	searchFunc('up', true);
	nodata_show ('.search_content',false,'.nodata_box','.big-box');
	//处理排重
	/*let $dumplicate = $("span.duplicate");
	if($dumplicate){
		$dumplicate.attr('data-status',"1");
	}*/
});
// 检索切换
$(document).on('click', '#screenBtn', function() {
	searchFunc('down');
});
// 保存暂时
$(document).on('click', '#saveBtn', function() {
	// console.log(screenInfoListBoxData('#screenInfoListBox'));
	$('#saveSearch .analysisName').val('');
	$("#saveSearch .analysisType").val("");
	$("#saveSearch .analysisType").trigger("chosen:updated");
	if ($('#saveSearchCheckbox').is(':checked')) {
		$('#saveSearchCheckbox').click();
	}
});

// 问号
$(document).on('click', '.fakeywordIcon', function(e) {
	$('.textBox').stop().slideToggle(200);
	e.stopPropagation();
});

// 删除标签
$(document).on('click','i.screenInfoListDelBtn',function() {
	var that = this;
	$(this).parents('.screenInfoList').css('pointer-events', 'none');
	$('.tooltip').remove();

	$(this).parent().fadeOut(300, function() {
		$(that).parent().remove();
	});

	// console.log($(this).siblings('.screenInfoTitle').text().replace('：',''));
	$('.screenBox').find('.checkbox .labelTitle').each(
			function() {
				if ($(this).text() == $(that).siblings('.screenInfoTitle').text().replace('：', '')) {
					$(this).parents('.checkbox').find('input[type="checkbox"]').prop("checked",false);
				}
				if ($(this).text() == '媒体站点') {
					$("#info_adopt_media_cross").val("");
					$("#info_adopt_media_cross").trigger("chosen:updated");
					$('.checkbox_source_type').find('input[name="dataSource"]').prop("checked","").prop("disabled","")
				}
			});

	if (!$('#screenSearchBtn').is(':visible')) {
		// var newObj = searchVo();
		// console.log(newObj);
		// search_list_load(newObj,true);
		search_click();
	}
});

// 相似文章
// $('#mainInfoListBox').on('click', '.similarityListBtn', function () {
// console.log('111');
//
// });

// 二次搜索
// $('#advancedTopBox').on('focus', '.sortSearhInput', function () {
// $(this).width('150px');
// if($(this).val()){
// $(this).siblings('.sortSearhDelIcon').show();
// }
// });

// 列表二次检索
function lishKeywordSearch(val) {
	// var showNum=0;
	// $('#mainInfoListBox').find('.mainInfoList
	// .textContantBoxContent').each(function () {
	// if($(this).text().indexOf(val)<0){
	// $(this).parents('.mainInfoList').hide();
	// }else{
	// $(this).parents('.mainInfoList').show();
	// showNum++;
	// }
	// });
	// if(showNum<=5){
	search_list_load(sourceTabsObj, true, val);
	// }
}

// 按tab隐藏筛选
function tabToggleSortBtn() {
	$('.data_source_tabs').find('li').each(
			function() {
				if ($(this).attr('class') == 'active') {
					var activeText = $(this).find('a').text().split('（')[0];
					if (activeText == '全部') {
						$('.sortBtnCount').hide();
						$('.sortBtnSelect').hide();
						$('.sortBtnHeat').hide();
					} else if (activeText == '电子报纸' ) {
						//|| activeText == 'APP'
						$('.sortBtnCount').hide();
						$('.sortBtnSelect').hide();
						$('.sortBtnHeat').show();
					} else if (activeText == '网站') {
						$('.sortBtnCount').hide();
						$('.sortBtnSelect').show();
						$('.sortBtnHeat').show();
					} else if ( activeText == '微博'
							|| activeText == 'Twitter'
							|| activeText == 'Facebook'
							|| activeText == 'Youtube') {
						//activeText == '微信' ||
						$('.sortBtnCount').show();
						$('.sortBtnSelect').hide();
						$('.sortBtnHeat').hide();
					}else if(activeText=='APP'||activeText == '微信'){
						$('.sortBtnSelect').hide();
						$('.sortBtnCount').show();
						$('.sortBtnHeat').show();
					}
					
				}
			});
	$('.sortBtnFunc').each(
			function(i, item) {
				if ($(this).hasClass('activeInit')) {
					$(this).addClass('active');
				} else {
					$(this).removeClass('active');
				}
				if ($(this).find('i.fa').hasClass('fa-sort-amount-asc')) {
					$(this).find('i.fa').removeClass('fa-sort-amount-asc')
							.addClass('fa-sort-amount-desc').data('sort',
									'desc');
				}
			});
	$('.mainTopBox').find('.checkbox input[name="infoInput"]').prop("checked",
			false);
	$('.sortBtnSelect').removeClass('activeStyle');
	$('.sortBtnSensitive').removeClass('activeStyle');
	$('.multiselect').find('.checkbox input[name="selectInput"]').prop(
			"checked", false);
	$('.multiselect').prev('i').removeClass('fa-caret-up').addClass(
			'fa-caret-down').data('sort', 'desc');
	$('.multiselect').hide();
	$('.sortSearhDelIcon').hide().siblings('.sortSearhInput').val('');
	$('.sortSearhDelIcon').siblings('.sortSearhIcon').css('left', '-20px')
			.end().siblings('.sortSearhInput').css({
				'width' : '0',
				'padding-left' : '0',
				'padding-right' : '0'
			});
}

// 条件多选
iFunc.checkbox_all_btn('.checkbox', 'dataSource', 'name');
iFunc.checkbox_all_btn('.checkbox', 'language', 'name');
// iFunc.checkbox_all_btn('.checkbox','category','name');
iFunc.checkbox_all_btn('.checkbox', 'region', 'name');
iFunc.checkbox_all_btn('.checkbox', 'emotion', 'name');
iFunc.checkbox_all_btn('.checkbox', 'place', 'name');
// iFunc.checkbox_all_btn('.checkbox', 'sensitive', 'name');

// 条件多选操作
$('.screenBox .checkbox').on('change', 'input[type="checkbox"]', function() {
	// if ($(this).is(':checked')) {

	// }
	screenInfoShow();
});

// 列表多选
iFunc.checkbox_all_btn('.mainInfoBigBox', 'infoInput', 'name');
$('.mainInfoBigBox').on('change','.infoInputBox input[type="checkbox"]',function() {
	if ($(this).is(':checked')) {
		$('.bottomFunction2').stop().fadeOut(200, function() {
			$('.bottomFunction').stop().fadeIn(200);
			$('.hideInfos_div').stop().fadeIn(200);
		});
	} else {
		var notallchecked = false;
		$('.mainInfoBigBox .infoInputBox').find(
				'input[type="checkbox"]').each(function() {
			if ($(this).is(":checked")) {
				notallchecked = true;
			}
		});
		if (!notallchecked) {
			$('.bottomFunction').stop().fadeOut(200, function() {
				$('.bottomFunction2').stop().fadeIn(200);
			});
			$('.hideInfos_div').stop().fadeOut(200);
		}
	}
});

$('#screenBoxTabBar').off('click', '.tabTextBox').on('click','.tabTextBox',function() {
	if ($(this).find('i').hasClass('fa-caret-down')) {
		$(this).find('i').removeClass('fa-caret-down').addClass('fa-caret-up');
		$('.screenBox').find('.shrinkBox').slideDown().end().find('.checkbox_region').css('border-bottom','1px dashed #e9ecef');
	} else {
		$(this).find('i').removeClass('fa-caret-up').addClass('fa-caret-down');
		$('.screenBox').find('.shrinkBox').slideUp().end().find('.checkbox_region').css('border-bottom', 'none');
	}
});

$(document).on('click', '#hehehe', function() {
	if ($(this).hasClass('active')) {
		$(this).removeClass('active');
		$('#screenInfoListBox2').fadeToggle(function() {
			$('#screenInfoListBox').fadeToggle();
		});
	} else {
		$(this).addClass('active');
		$('#screenInfoListBox').fadeToggle(function() {
			$('#screenInfoListBox2').fadeToggle();
		});
	}
});

// 分析类型选择
function analysisTypeInit() {
	var optionHtml = '<option value="">全部</option>';
	$analysisCategory.forEach(function(item, i) {
		optionHtml += '<option value="' + item.code + '" >' + item.name
				+ '</option>';
	});
	$('#analysisType').html(optionHtml);

	$('#analysisType').chosen();
}
// 主题选择
function initAnalysisChosen() {
	$.ajax({
		type : "get",
		url : "analysis_al/mod1/analysisSearchConfigs",
		beforeSend : function() {

		},
		success : function(data) {
			// console.log(data);
			var optionHtml = '';
			data.forEach(function(item, i) {
				// if(item.is_analysis==0){
				optionHtml += '<option value="' + item.auto_id + '" >'
						+ item.name + '</option>';
				// }else {
				// optionHtml+='<option disabled
				// value="'+item.auto_id+'">'+item.name+'</option>';
				// }

			});
			$('#analysis_select').html(optionHtml);

			$('#analysis_select').chosen();

		}
	});
}
initAnalysisChosen();
$('#analysisBox').on('click', '#mod1_analysisModalBtn', function() {
	initAnalysisChosen();
});
// modules 添加
var modulesArr=[];
var modulesDataArr=[];
var modulesAllData={};
$('#mod1_analysisModal').off('click','.file-item').on('click','.file-item',function () {
  $(this).addClass('active');
  var id=$(this).data('id');
  var classify=$(this).data('classify');
  var name=$.trim($(this).find('.madalName').text());
  modulesAllData[classify]=modulesAllData[classify]?modulesAllData[classify]:[];
  modulesAllData[classify].push({
    name: name,
    id: id,
  });
  modulNumInit(modulesAllData);
  // console.log(modulesAllData);
});
// 数量详情事件
$('#mod1_analysisModal').off('click','.targetBar .modulNum').on('click','.targetBar .modulNum',function (e) {
  e.stopPropagation();
  $(this).parents('.targetBar').find('.modulListBox').slideToggle(200);
});
// 数量删除
$('#mod1_analysisModal').off('click','.targetBar .modulList .delBtn').on('click','.targetBar .modulList .delBtn',function (e) {
  e.stopPropagation();
  var that=this;
  $.map(modulesAllData,function (val,key) {
    if(key==$(that).attr('data-classify')){
      for (var i = val.length-1; i >= 0 ; i--) {
        if(val[i].id==$(that).attr('data-id')){
          val.splice(i,1);
          break ;
        }
      }
    }
  });
  $(that).parents('.modulList').remove();
  modulListInit(modulesAllData);
  modulNumInit(modulesAllData);
});

// 添加分析
$('#mod1_analysisModal').on('click','#addAnalysisBtn',function () {
  $.map(modulesAllData,function (val,key) {
    val.forEach(function (item,i) {
      modulesArr.push(item.id);
    });
  });
  modulesArr.forEach(function (item,i) {
    modulesDataArr[i]=$layoutObj[item];
  });
  if($.trim($('#analysisName').val())){
    $.ajax({
      type: "post",
      url: "analysis_al/mod1/addAnalysisSearchConfig",
      data: {
        sid: $('#analysis_select').val(),
        analysisCategory: $('#analysisType').val(),
        name: $('#analysisName').val(),
        modules: modulesArr.join(','),
        layout: JSON.stringify(modulesDataArr)
      },
      success: function (data) {
        console.log(data);
        if(data.status=='ok'){
          $('#analysisModal').modal('hide');
          swal({
						title: "是否跳转到分析",
						type: "warning",
						showCancelButton: true,
						confirmButtonColor: "#DD6B55",
						confirmButtonText: "跳转",
						cancelButtonText: "取消",
						closeOnConfirm: false
					},
					function() {
						swal.close();
						window.location.href = 'analysis_al/mod2?autoId='+data.autoId;
					});
        }else {
          toastr["error"]("添加失败,请刷新重试");
        }
      },
      error: function (res) {
        toastr["error"]("添加失败,请刷新重试");
      }
    });
  }else {
    toastr["info"]("分析名称不能为空");
  }
});

// 添加案例库
$('#searchTopBox').on('click', '.add2', function() {
	$('#addDisplayBtn').attr('data-sid', $(this).attr('data-autoId'));
	// var that=this;
	// $('#display_selectInput').find('option').each(function () {
	// if($(that).attr('data-name')==$(this).text()){
	// $(this).prop("selected",true);
	// $("#display_selectInput").trigger("chosen:updated");
	// }
	// });
	$('#displayName').val($(this).parents('tr').find('td').eq(1).text());
	$('#headlineTagsInput').tagsinput('removeAll');
	if ($('#headlineTypeSelect_chosen').length > 0) {
		$('#headlineTypeSelect').chosen('destroy');
	}
	$('#headlineTypeSelect').chosen({
		disable_search : true,
		max_selected_options : 1,
		search_contains : true,
		width : '100%'
	});
	$("#headlineTypeSelect").val("");
	$("#headlineTypeSelect").trigger("chosen:updated");
	$('#addDisplayModal').modal('show');
	return false;
});

// 展示添加 头条
$('#addDisplayModalBox').on(
		'click',
		'#addDisplayBtn',
		function() {
			if ($.trim($('#displayName').val()) == '') {
				toastr["info"]("头条名称不能为空");
				return;
			}
			// var displayType='';
			// $('#addDisplayModal').find('.modalSelectList
			// .file-preview').each(function (item,i) {
			// console.log($(this).attr('data-id'));
			// if($(this).hasClass('active')){
			// displayType=$(this).attr('data-id');
			// }
			// });
			var sid = $(this).attr('data-sid')
			var time = $('input[name="addDispalyDate"]:checked').val();
			var sTime = '';
			var eTime = '';
			if (!time) {
				var times = $('#display_date').val().split(' 至 ');
				sTime = times[0] + ' 00:00:00';
				eTime = times[1] + ' 23:59:59';
			}
//			var type = $("#addDisplayModalBox").find("#headlineTypeSelect")
//					.val();
			var tags = $("#addDisplayModalBox").find("#headlineTagsInput")
					.val();
			var analysisType = $('#headlineTypeSelect').val();
			$.ajax({
				type : "post",
				url : "search_al/mod1/addSearchTopInfo",
				data : {
					autoId : sid,
					name : $.trim($('#displayName').val()),
					//    displayType: 4,
					realTime : time,
					startTime : sTime,
					endTime : eTime,
//					type : type,
					tags : tags,
					analysisType:analysisType
				},
				beforeSend : function() {
					$('.refresh-preloader').show();
				},
				success : function(data) {
					if (data.status == 'ok') {
						$('.refresh-preloader').hide();
						$('#addDisplayModal').modal('hide');
						toastr["success"]("添加成功");
						//initTable();
						mod1_Table.draw(false);
						swal({
							title : "是否跳转到头条",
							type : "warning",
							showCancelButton : true,
							confirmButtonColor : "#DD6B55",
							confirmButtonText : "跳转",
							cancelButtonText : "取消",
							closeOnConfirm : false
						}, function() {
							swal.close();
							window.location.href = 'display_al/mod2?autoId='
									+ data.autoId + '&sid='
									+ $('#addDisplayBtn').attr('data-sid')
									+ '&path=designedFor.html';
						});
					} else if (data.status == 'exist') {
						$('.refresh-preloader').hide();
						$('#addDisplayModal').modal('hide');
						toastr["error"]("添加失败,该头条名称已存在");
					} else {
						$('.refresh-preloader').hide();
						$('#addDisplayModal').modal('hide');
						toastr["error"]("添加失败,请刷新重试");
					}
				},
				error : function(res) {
					$('.refresh-preloader').hide();
					$('#addDisplayModal').modal('hide');
					toastr["error"]("添加失败,请刷新重试");
				}
			});
		});