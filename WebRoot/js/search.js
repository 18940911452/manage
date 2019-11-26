var $search_type = iFunc.UrlParse().type
var $search_kw = iFunc.UrlParse().kw
var $searchVo;
var startParentId = '';
var endParentId = '';
var $drag = false;
var sourceTabsObj;
var loadingMark = 0

// 传值 搜索跳过来的 关键词
if ($search_kw != undefined && $search_kw != '') {
	$('#tags_1').val($search_kw);
	$('#tags_1').tagsinput('add',$search_kw);
	loading_show('.tabbable',true)

	setTimeout(function () {
		search_click ()	
		screenInfoShow();
	},100)
}


// 拖动开始
$('body').on('dragstart', '.tags_button_drag', function(e) {
	$drag = true
	let tagName = e.target.innerText
	startParentId = $(e.target).parents('.bootstrap-tagsinput').siblings('input').attr('id');
	e.originalEvent.dataTransfer.setData("text", tagName);
	repetitionFlag=false;
})
// 在目标元素上移动
$('body').on('dragover', '.bootstrap-tagsinput', function(e) {
	e.preventDefault();
})
// 被拖拽元素被放置在目标元素上
$('body').on('drop', '.bootstrap-tagsinput', function(e) {
	let tagName = e.originalEvent.dataTransfer.getData("text");
	let $input = $(e.target).siblings('input');
	if(!$input.hasClass('tags_drag')) {
		$input = $(e.target).parents('.bootstrap-tagsinput').siblings('input')
	}
	$input.tagsinput('add', tagName);
	endParentId = $input.attr('id')
});
// 拖拽结束
$('body').on('dragend', '.tags_button_drag', function(e) {
	let tagName = e.target.innerText;
	if(endParentId != startParentId) {
		$(e.target).parents('.bootstrap-tagsinput').siblings('input').tagsinput('remove', tagName);
	}
	$drag = false
	repetitionFlag=true;
})


$('#search_date').daterangepicker(daterangepicker_option_left('#search_date',true), function(start, end, label) {
	screenInfoShow();
})


screenInfoShow();

// 媒体选择
var fillResetFlag=0;
$.get("search_al/mod1/dataSources", {
	"level": 1,
	'pageSize': 300
}, function(data) {
	var str = "<option value=''>"+
			"</option>";
	$.each(data.data, function(i, val) {
		str += "<option value='" + val.auto_id + "'>" + val.name +
			"</option>";
	});
	$("#info_adopt_media_cross").append("<optgroup label='自定义源'>"+str+'</optgroup>');
	// $("#info_adopt_media_cross").trigger("chosen:updated");
	$('#info_adopt_media_cross').chosen({
		no_results_text: "未找到此选项!",
// max_selected_options: 1,
		allow_single_deselect: true,
		search_contains: true,
		width: '30%'
	})
	// $("#info_adopt_media_cross").chosen("destroy");
	if(urlAutoId&&urlType=='advanced'){
		fillResetFlag++;
		searchInfoFillHosts(JSON.parse(listFillData.search_terms));
		if(fillResetFlag==2){
			setTimeout(function () {
				$('#screenSearchBtn').click();
			}, 500);
		}
	}
})


// $("input[name='region'][value='0']").change(function() {
// if($(this).is(':checked')) {
// loactionCity (loadLoactionCityStr)
// }else{
// $('#loactionCity').multiselect('destroy');
// $('#loactionCity').val('')
// }
// });


$("input[name='region']").change(function() { 
	if($("input[name='region'][value='0']").is(':checked')) {
		loactionCity (loadLoactionCityStr)
	}else{
		$('#loactionCity').multiselect('destroy');
		$('#loactionCity').val('')
	}
});



// $('body').on('keyup', '.checkbox_media .chosen-container
// li.search-field>input', function(event) {
// var $sinput = $(".chosen-container li.search-field>input");
// setTimeout(function() {
// kwsadoptmedia = $sinput.val();
// if(kwsadoptmedia != null && kwsadoptmedia != "") {
// if(event.keyCode == "13") {
// mpageNo = 1;
// manualSearch = "search";
// dynamicLoadMedia(mpageNo, manualSearch, kwsadoptmedia); //默认查100，可满足需求
// }
//
// } else {
// manualSearch = null;
// }
//
// }, 200);
// })

function dynamicLoadMedia(mpageNo, manualSearch, kwsadoptmedia) {
	if(mpageNo == 1 && manualSearch == null) {
		$("#info_adopt_media_cross").empty();
		$("#info_adopt_media_cross").chosen("destroy");
		var option = $("<option value='0'>全部</option><option value='-2'>空</option>");
		$("#info_adopt_media_cross").append(option);
	}
	$.get("search_al/mod2/siteInfos", {
		"name": kwsadoptmedia,
		"pageNo": mpageNo,
		"pageSize": 50
	}, function(data) {
		if(data.data.length > 0) {
			var lenli = $(".adopt_media_divs  li.active-result").length;
			var str = "";
			var appresult = "";
			$.each(data.data, function(i, val) {
				str += "<option value='" + val.host + "'>" + val.siteName +
					"</option>";
				appresult += '<li class="active-result" data-option-array-index="' + lenli + '">' + val.siteName + '</li>';
				lenli++;
			});
			if(manualSearch == null) {
				$("#info_adopt_media_cross").append(str);
				$("#info_adopt_media_cross").trigger("chosen:updated");
				$('#info_adopt_media_cross').chosen({
					no_results_text: "未找到此选项!",
				})
			} else if(manualSearch == "search") { // 手动搜索
				// var choice = $(".adopt_media_divs ul.chosen-choices").html();
				// setTimeout(function(){
				$("#info_adopt_media_cross").prepend(str);
				$("#info_adopt_media_cross").trigger("chosen:updated");
				$('#info_adopt_media_cross').chosen({
					no_results_text: "未找到此选项!",
				});
			}
		} else {
			$('.no-results').html("");
			$('.no-results').append("<span>在当前板块媒体范围内无匹配</span>")
			if(mpageNo == 1 && manualSearch == null) {
				$("#info_adopt_media_cross").trigger("chosen:updated");
				$('#info_adopt_media_cross').chosen({
					no_results_text: "未找到此选项!",
				})
			}
		}

	})

}

var night_mode = ''

function drawStatisticChart(container, option) {
	
	// debugger
	$("#" + container).empty().attr("_echarts_instance_", "");
	var domMain = document.getElementById(container);
	var myChart = echarts.init(domMain, night_mode);
// myChart.setTheme("macarons");
	myChart.hideLoading();
	myChart.setOption(option);
	myChart.on('click', function (params) {
		let isDuplicate = getDuplicate();
		let data = searchVo()
		data.isDuplicate= isDuplicate;
		let name = params.name;
		let $Table=$('.data_source_tabs .active');
		let dataSource="";
		let isAll =false;
		if($Table.data('name')=='total'){// 全部
			dataSource = getDataSourceEnName(name).split(',');
			isAll =true;
		}else{
			dataSource = [$Table.data('val')];
		}
		
		if(params.seriesType=='line'){
// debugger ;
			let strat = data.startTime.split('-');
			if(params.name.length>7){
				data.startTime=strat[0]+'-'+params.name+':00:00';
				data.endTime=strat[0]+'-'+params.name+':59:59';
				data.dataSource=dataSource;
			}else{
				data.startTime=strat[0]+'-'+params.name+' 00:00:00';
				data.endTime=strat[0]+'-'+params.name+' 23:59:59';
				data.dataSource=dataSource;
			}
		}else if(params.seriesType=='pie'){
			data.dataSource=dataSource;
			data.emotion=[$('.checkbox_emotion input[data-val="'+params.data.name+'"]').val()]
		}else if(params.seriesType=='bar'){
			data.dataSource=dataSource;
			if(!isAll){
				if(isSocialMedia(dataSource)){// 社交媒体
					data.uids=[name];
				}else{// 站点
					data.site=[name];
				}
			}
		}
		search_list_load(data,true)
	});
}

function getDataSourceEnName(enName){
	switch (enName) {
		case '网站':
			return '4';
			break;
		case '电子报纸':
			return '7';
			break;
		case 'App':
			return '10';
			break;
		case '微信':
			return '8';
			break;
		case '微博':
			return '9';
			break;
		case '论坛':
			return '6';
			break;
		case 'Twitter':
			return '1';
			break;
		case 'Facebook':
			return '2';
			break;
		case 'Youtube':
			return '11';
			break;
		default:
			return $('.data_source_tabs .active').data('val');
			break;
	}
}

/**
 * 判断是否是社交媒体
 * 
 * @param dataSource
 * @returns
 */
function isSocialMedia(dataSource){
	dataSource = dataSource+"";
	switch (dataSource) {
	case '1':
		return true;
		break;
	case '2':
		return true;
		break;
	case '8':
		return true;
		break;
	case '9':
		return true;
		break;
	default:
		return false
		break;
	}
}

// 将对象转成url 参数
var parseParam=function(param, key){
  var paramStr="";
  if(param instanceof String||param instanceof Number||param instanceof Boolean){
    paramStr+="&"+key+"="+encodeURIComponent(param);
  }else{
    $.each(param,function(i){
      var k=key==null?i:key+(param instanceof Array?"["+i+"]":"."+i);
      paramStr+='&'+parseParam(this, k);
    });
  }
  return paramStr.substr(1);
};

$('body').on('click', '#search_tab', function(e) {
	if(initDuplicate){
		initDuplicate();
	}
	search_tab ($('#search_tab'),true)
})

function search_tab ($this,mark) {
	let param = iFunc.UrlParse()
	let tabSearch = function ($this) {
		$this.html($tName);
		$('.advancedTab').text($bName);
		$('.advanced_box,.general_box,.basic_box').hide()
		$('.basic_box').show();
		screenInfoShow();
	}
	
	let simpleTabSearch = function ($this,param) {
		tabSearch($this)
		param.type = "simple"
		history.pushState(null, null, window.location.pathname + "?" + parseParam(param));
		$this.data('type', 1);
		$('.general_box').show();
		$('#search_date_box').css('top','10px');
		$('.checkbox_source_type').css('top','-5px');
		$('.checkbox_source_type').css('border-bottom','none');
		$('#screenBoxTabBar').hide();
// searchFunc('down');
	}
// var $this = $(this)
	var $bName = $this.text();
	var $tName = $('.advancedTab').text();
	if($this.data('type') == '1') {
		if (mark) {
			if (searchVo () != 'false') {  
				var obj = searchVo ()			
				$('#tags_advanced').tagsinput('add', advancedKeywordsFill(obj.keywords[0]));
			}
		}
		tabSearch($this)
		param.type = "advanced"
		history.pushState(null, null, window.location.pathname + "?" + parseParam(param));
		$this.data('type', 0);
		$('.advanced_box').show();
		$('#search_date_box').css('top','20px');
		$('.checkbox_source_type').css('top','0px');
		$('.checkbox_source_type').css('border-bottom','1px dashed #e9ecef');
		$('#screenBoxTabBar').show();
		$("#info_adopt_media_cross").trigger("chosen:updated");
// searchFunc('down');
		if($this.hasClass('firstClick')) {

// searchFunc('down');
		} else {
			$this.addClass('firstClick');
// searchFunc('down',true);
		}
	} else {
		if (searchVo () =='false') {
			simpleTabSearch($this,param)
		}else{
			swal({
				title: "切换到普通搜索？",
				text: "切换到普通搜索将删除您已更改的高级搜索。单击取消键以保存当前搜索，或点击并不保存以进行普通搜索。",
				type: "warning",
				showCancelButton: true,
				confirmButtonColor: "#DD6B55",
				confirmButtonText: "切换并不保存",
				cancelButtonText: "取消",
				closeOnConfirm: false
			},
			function() {
				swal.close();
				simpleTabSearch($this,param)
				screenInfoShow();
				screenInfoClear();
				
			});
		}
	}
	
	$('.basic_box').show();
	$('.advanced_box,.general_box,.basic_box').addClass('animated fadeIn');
	setTimeout(function() {
		$('.advanced_box,.general_box,.basic_box').removeClass('fadeIn');
	}, 1000);
	screenInfoShow();
}



// 高级 普通初始化
if($search_type == 'advanced') {
	search_tab ($('#search_tab'),false)
}

// 获取分类
$.get('search_al/mod2/infoCategorys',function (data) {
	var str = ''
	for (v in data) {
		// str += ' <label><input type="checkbox" name="category"
		// value="'+data[v]+'" data-val="'+v+'"><i></i>'+v+'</label>'
		str += ' <label><input type="checkbox" name="category" value="'+data[v]+'" data-val="'+v+'"><span>'+v+'</span></label>'
	}
	$('.checkbox_category').append(str);
	iFunc.checkbox_all_btn('.checkbox','category');
	
	if(urlAutoId&&urlType=='advanced'){
		fillResetFlag++;
		searchInfoFillCategory(JSON.parse(listFillData.search_terms));
		if(fillResetFlag==2){
			setTimeout(function () {
				$('#screenSearchBtn').click();
			}, 100);
		}
	}
})

// 普通关键词 解析成 布尔关键词
// function simpleToComplex (data) {
// data.must
// }


// 布尔搜索解析
function complexKwsTocommonKws(str) {
	
	var str = str.replace(/\s+([\u4e00-\u9fa5|&|(|)])/ig,'$1')
	var arr = [];
	var kws = str.split('!');
	var not = '';
	var contain = '';
	var must = '';
	var any = '';
	// 不包含
	if (kws.length>1) {
		var not = kws[1].replace('(' , "").replace(')' , "");
		str = kws[0]
	}
	
	// 包含
	if (kws[0].indexOf(')&(')>0) {
		contain = kws[0].split(')&(');
		must = contain[0].replace('(' , "").replace(')' , "");
		any = contain[1].replace('(' , "").replace(')' , "");
	}else{
		if (str.indexOf('|')>0) {
			any = str.replace('(' , "").replace(')' , "");
		}else{
			must = str.replace('(' , "").replace(')' , "");
		}
	}
	return {must:splitAndOr(must),any:splitAndOr(any),not:splitAndOr(not)};
}

function splitAndOr(str) {
	var arr = [];
	if (str.indexOf('|')>0) {
		arr = str.split('|')
	}else if(str.indexOf('&')>0){
		arr = str.split('&')
	}else if(str != ''){
		arr.push(str)
	}else{
		arr
	};
	$.each(arr, function(i,v) {
		arr[i] = arr[i].replace('(' , "").replace(')' , "");
	});
	return arr;
}


function searchVo () {
	var obj = {};
	// 关键词
	var keywords = [];
	if(iFunc.UrlParse().type == 'advanced') { // 高级搜索
		$.each($('.advanced_tags_button'), function(i,v) {
			keywords.push(complexKwsTocommonKws($(this).text()))
		});
		
		// 语种
		var language = checkboxValStr('.screenBox','language','value',true);
		obj.language = language;
		
		// 境内外
		var region = checkboxValStr('.screenBox','region','value',true);
		obj.region = region;
		
		// 情感
		var emotion = checkboxValStr('.screenBox','emotion','value',true);
		obj.emotion = emotion;
		
		// 关键词位置
		var keywordsPosion = checkboxValStr('.screenBox','place','value',true);
		obj.keywordsPosion = keywordsPosion;
		
		// 分类
		var category = checkboxValStr('.screenBox','category','value',true);
		var categoryArr = []
		$.each(category, function(i,v) {
			categoryArr = categoryArr.concat(v.split(';'))
		});
		obj.category = categoryArr;
		
		// 媒体
		var hosts = $('#info_adopt_media_cross').val();
		obj.datasourceId = hosts;
		
		// 省市
		var citys = $('#loactionCity').val();
		obj.citys = citys;

		// 内容审核
		// var sensitive = checkboxValStr('.screenBox','sensitive','value',true);
		// obj.sensitive = sensitive;
		
		if ($('#tags_advanced').tagsinput('items')== '' && hosts == '') {
//			return 'false';
		}
	}else{
		if ($('#tags_1').tagsinput('items')!= '' || $('#tags_2').tagsinput('items')!= '') {
			
		}else{
//			return 'false';
		}
		var tags_1Arr=[],tags_2Arr=[],tags_3Arr=[];
		$('#tags_1').parent().find('.tags_button_drag').each(function () {
			tags_1Arr.push($(this).text());
		});
		$('#tags_2').parent().find('.tags_button_drag').each(function () {
			tags_2Arr.push($(this).text());
		});
		$('#tags_3').parent().find('.tags_button_drag').each(function () {
			tags_3Arr.push($(this).text());
		});
		// console.log(tags_1Arr,tags_2Arr,tags_3Arr);
		// var kws = {must:$('#tags_1').tagsinput('items'),any:$('#tags_2').tagsinput('items'),not:$('#tags_3').tagsinput('items')};
		var kws = {must:tags_1Arr,any:tags_2Arr,not:tags_3Arr};
		keywords.push(kws)
	}
	obj.keywords = keywords;
	
	// 数据源
	var dataSource = checkboxValStr('.screenBox','dataSource','value',true);
	obj.dataSource = dataSource;
	let sid = iFunc.UrlParse().autoId;
	obj.sid=sid?sid:"";
	
	
// data.isDuplicate=1;
	// 时间
	if($('#search_date').val().indexOf(':')>=0){
		obj.startTime = $('#search_date').val().split(' 至 ')[0];
		obj.endTime = $('#search_date').val().split(' 至 ')[1]
	}else {
		obj.startTime = $('#search_date').val().split(' 至 ')[0]+' 00:00:00';
		if ($('#search_date').val().split(' 至 ')[1] == moment().format('YYYY-MM-DD')) {
			obj.endTime = moment().format('YYYY-MM-DD HH:mm:ss')
		}else{
			obj.endTime = $('#search_date').val().split(' 至 ')[1]+' 23:59:59';
		}
	}
	obj.now  = moment().format('YYYY-MM-DD HH:mm:ss');
	return obj;
}

// 搜索
function search_click () {
	if(initDuplicate){
		initDuplicate();
	}
	$searchVo = searchVo();
//	if ($searchVo == 'false') {
//		toastr["info"]("请输入包含关键词或者媒体站点");
//		return
//	}
	$('.data_source_tabs').find('li[data-name="total"]').addClass('active').siblings().removeClass('active');
	loading_show('.tabbable',true)
	sourceTabsObj = jQuery.extend(true, {}, $searchVo);
	dataSourceCount(sourceTabsObj)
	search_list_load(sourceTabsObj)
	tabToggleSortBtn();
}

// 情感分布
function emotionCount (data,isDuplicate) {
	$('#echarts4').prev('.refresh-preloader').fadeIn(200);
	$.post('search_al/mod2/emotionCount',{params:JSON.stringify(data),"sid":data.sid,"isDuplicate":isDuplicate},function (data) {
		$('#echarts4').prev('.refresh-preloader').fadeOut(200);
		if(data!=[]&&data.length!=0){
			echarts_pie("echarts4",data);
		}else {
			$('#echarts4').html('<img style="display:block;margin:0 auto;" src="img/nodata.png">');
		}
	})
}

// 站点排名
function siteCount (data,isDuplicate) {
	var legend = $.trim($('.data_source_tabs li.active').text().split("（")[0]);
	$('#echarts3').prev('.refresh-preloader').fadeIn(200);
	if (legend != '全部'){
		$.post('search_al/mod2/siteCount',{params:JSON.stringify(data),"sid":data.sid,"isDuplicate":isDuplicate},function (data) {
			$('#echarts3').prev('.refresh-preloader').fadeOut(200);
			var dataFlag=false;
			data.forEach(function (item,i) {
				if(item.value!=0){
					dataFlag=true;
				}
			});
			if(dataFlag){
				data.sort(sortValue)
				echarts_barH ("echarts3",data)
			}else {
				$('#echarts3').html('<img style="display:block;margin:0 auto;" src="img/nodata.png">');
			}
		})
	}else{
		//全部数据
		$.post("search_al/mod2/dataSourceCount",{params:JSON.stringify(data),"sid":data.sid,"isDuplicate":isDuplicate},function (data) {
			let dataArr = [];
			$.each(data, function(i,v) {
				dataArr.push(v);
			});
			dataArr.sort(sortValue)
			dataSourceCountEcharts (dataArr);
			$('#echarts3').prev('.refresh-preloader').fadeOut(200);
		});
	}
		
}

// 信息量排名
var infoCountF;
function infoCount (data,isDuplicate) {
	$('#echarts2').prev('.refresh-preloader').fadeIn(200);
	infoCountF = $.post('search_al/mod2/infoCount',{params:JSON.stringify(data),"sid":data.sid,"isDuplicate":isDuplicate},function (data) {
		$('#echarts2').prev('.refresh-preloader').fadeOut(200);
		var legend = $.trim($('.data_source_tabs li.active').text().split("（")[0]);
		var dataFlag=false;
		// lzk 判断为空
		if(data&&data.count&&data.count instanceof Array){
			data.count.forEach(function (item,i) {
				if(item!=0){
					dataFlag=true;
				}
			});
		}
		if(dataFlag) {
			echartsLine("echarts2",data,legend);
		}else {
			$('#echarts2').html('<img style="display:block;margin:0 auto;" src="img/nodata.png">');
		}
	})
}


// 获取列表
var searchOption='';
var isDuplicate =0;
function search_list_load(data,chart,secondKeywords,mediaInfo,isHidden){
	$('#totalNum').text('');
	let isDuplicate=getDuplicate();
	if (!chart) {
		emotionCount (data,isDuplicate);
		siteCount (data,isDuplicate);
		infoCount (data,isDuplicate);
	}
	var sid = data.sid;
	
	var param = {
		params:JSON.stringify(data),
		path:'../../../dynamic/search-list.html',
		secondKeywords: secondKeywords,
		mediaInfo: mediaInfo,
		isHidden: isHidden,
		sid: data.sid,
		isDuplicate:isDuplicate,
		sensitive : data.sensitive,
		// now: moment().format('YYYY-MM-DD HH:mm:ss'),
		// Rtype: 'json'
	}
	
	var option = {};
	option.param = param;
	option.pageSize =20;
	option.isExport = false;
	option.page_foot_html = true;
	option.callback = function () {
		// $(".hideInfos_div").fadeOut(200);
		$('#mainInfoListBox').attr('data-sid',sid);

		loadingMark++;
		if (loadingMark>1) {
			loading_show('.tabbable',false)
		}
		$('#totalNum').text('（'+$('#mainInfoListBox .item >.page_data').attr('data-total')+'）');

		// 提示
		$('[data-toggle="tooltip"]').tooltip();
		// 无sid ，去掉隐藏按钮
		if(sid==null || sid==undefined)  $(".hide_single_art").hide();
		$.post("search_al/mod2/invokeMblogKeyword",{"params":option.param.params},function(data){
			console.log(data);
		});
		// //初始化多选
		// $('.mainInfoBigBox').find("input[name='infoInput']:eq(0)").prop("checked","");
		// var isCheck = false;
		// var checkboxs =
		// $('.mainInfoBigBox').find("input[name='infoInput']:gt(0)");//获取除第一个的所有checkbox元素
		// if(checkboxs.length>0){
		// for(var i=0 ; i<checkboxs.length ; i++){
		// if($(checkboxs[i]).is(':checked')){
		// isCheck = true;
		// }
		// }
		// }
		// if(isCheck){
		// $('.bottomFunction2').stop().hide();
		// $('.bottomFunction').stop().show();
		// $('.hideInfos_div').stop().show();
		// }else {
		// $('.bottomFunction').stop().hide();
		// $('.bottomFunction2').stop().show();
		// $('.hideInfos_div').stop().hide();
		// }
		
		// 点击隐藏按钮
		// $(".hide_single_art").unbind("click").click(function(){
		// if(confirm("确定隐藏吗？")){
		// var infoInputStr=[];
		// var uuid = $(this).data("uuid");
		// var source = $(this).data("source");
		// var dataarr = {"uuid":uuid,"dataSource":source};
		// infoInputStr.push(dataarr);
		// var $i = $(this);
		// $.post("search_al/mod2/hideArtsByUuids",{uuids_source:JSON.stringify(infoInputStr),sid:sid},function(msg){
		// if(msg!=null && msg=="ok"){
		// $i.closest(".mainInfoListHoverBox").slideUp(500);
		// }else{
		// alert("隐藏失败")
		// }
		// });
		// }
		// });
		// $(".hide_single_art").hover(function(){
		// $(this).attr("style","color:#03a9f3;margin-right: 16px;cursor:
		// pointer;")
		// },function(){
		// $(this).attr("style","color:#aeaeae;margin-right: 16px;cursor:
		// pointer;")
		// })
		
		// similarityListNum($('#mainInfoListBox'),20);
		
	}
	searchOption = jQuery.extend(true, {}, param);
	var obj = new iPage.ScrollLoadContainer("mainInfoListBox", "search_al/mod2/searchInfos", option).init();
	listCheckOptionInit();
}


// 排重功能，暂时注释
$('body').on('click','span.duplicate',function(){
	let $this= $(this);
	toggleDuplicate();
	let mediaInfo = [];
	$("#multiselect input[name='selectInput']").each(function(){
		if($(this).prop("checked")==true){
			mediaInfo.push($(this).val());
		}
	})
	
	var type = $('.mainInfoBigBox').find('.sortBtn.active').data('type');
	var sort = $('.mainInfoBigBox').find('.sortBtn.active i').attr('data-sort');
	var newObj = jQuery.extend(true, {}, sourceTabsObj);
	newObj.sortField = type;
	newObj.sortWay = sort;
	newObj.mediaInfo = mediaInfo;
	search_list_load(newObj,false,$("input.sortSearhInput").val());
});

// 剔除列表
$('body').on('click','span.hideList',function(){
	var newObj = jQuery.extend(true, {}, sourceTabsObj);
	if($(this).hasClass('choose')){
		search_list_load(newObj,false,'','',false);
		$('#hideInfos').attr('title','剔除选中').find('i.iconfont').removeClass('icon-eye1').addClass('icon-eye');
	}else {
		search_list_load(newObj,false,'','',true);
		$('#hideInfos').attr('data-original-title','取消剔除选中').find('i.iconfont').removeClass('icon-eye').addClass('icon-eye1');
	}
	$("span.duplicate").removeClass("choose");
	$("span.hideList").toggleClass("choose");
});



// 数据源数量
var sourceCountData;
function dataSourceCount(param) {
	var val = $searchVo.dataSource.join(',');
	$('.data_source_tabs').find('li[data-name="total"]').data('val',val);
	$.post("search_al/mod2/dataSourceCount",{params:JSON.stringify(param),"sid":param.sid},function (data) {
		$('.data_source_tabs').find('li').hide();
		var dataArr = [];
		let total = 0
		$.each(data, function(i,v) {
			$('.data_source_tabs').find('li[data-name="'+v.enname+'"]').show().find('span').text(v.value);	
			if (v != 'total') {
				total += v.value;
				dataArr.push({
					name:$.trim($('.data_source_tabs li[data-name="'+v.enname+'"]').text().split("（")[0]),
					value:v.value
				});
			}
		});
		if (total>0) {
			$('.data_source_tabs').find('li[data-name="total"]').show().find('span').text(total);	
		}
		// author lzk 线上部署开放
		mblogCountOuter();
		dataArr.sort(sortValue)
		sourceCountData = dataArr
		var legend = $.trim($('.data_source_tabs li.active').text().split("（")[0]);
		if (legend == '全部') {
			dataSourceCountEcharts (sourceCountData)
		}
		loadingMark++;
		if (loadingMark>1) {
			loading_show('.tabbable',false)
		}
	})
}

function mblogCountOuter(){
	let vo = searchVo();
	if(vo.dataSource.length==0 || vo.dataSource.filter(function(data){return data == 9;}).length>0){
		$.post("search_al/mod2/getMblogCount",{'vo':JSON.stringify(vo)},function(result){
			// debugger;
			if(result&&result.data>0){
				let rtCount = result.data
				let $Tabls = $(".data_source_tabs");
				let $Total =$Tabls.find("li[data-name='total']");
				let $Mblog= $Tabls.find("li[data-name='mblog_info']");
				let mblogCount= $Mblog.find("span").text();
				if(parseInt(rtCount)>parseInt(mblogCount)){
					$Mblog.find("span").text(result.data);
					$Total.find("span").text(parseInt(result.data)+parseInt($Total.find("span").text())-parseInt(mblogCount));
				}
			}
		});	
	}
}


function dataSourceCountEcharts (sourceCountData) {
	if (sourceCountData) {
		var dataFlag=false;
		sourceCountData.forEach(function (item,i) {
			if(item.value!=0){
				dataFlag=true;
			}
		});
		if(dataFlag){
			sourceCountData.sort(sortValue)
			echarts_barH ("echarts3",sourceCountData)
		}else {
			$('#echarts3').html('<img style="display:block;margin:0 auto;" src="img/nodata.png">');
		}
	}
}


// 根据value排序
function sortValue(a,b){  
   return b.value-a.value  
}


// 数据源切换
$('.data_source_tabs').on('click','li a',function () {
	if(initDuplicate){
		initDuplicate();
	}
	let val = $(this).parent().data("val");
	if(needDuplicate(val)){
		if(showDuplicate){
			showDuplicate();
		}
	
	}else{
		if(hideDuplicate){
			hideDuplicate();
		}
	}
	
	if ($(this).find('span').text() == 0) {
		nodata_show ('.search_content',true,'.nodata_box','.big-box');
		return
	}
	nodata_show ('.search_content',false,'.nodata_box','.big-box');
	$(this).parent().addClass('active').siblings().removeClass('active');
	loading_show('.tabbable',true)
	sourceTabsObj = jQuery.extend(true, {}, $searchVo);
	var sourceType = $(this).parent().data('val')+'';
	if (sourceType == '') {
		sourceTabsObj.dataSource = [];
	}else{
		sourceTabsObj.dataSource = sourceType.split(',');
	}
	search_list_load(sourceTabsObj);
	tabToggleSortBtn();
})

/**
 * 判断是否需要排重标志
 * 
 * @param dataSource
 * @returns
 */
function needDuplicate(dataSource){
	dataSource = dataSource+"";
	switch (dataSource) {
	case '4':
		return true;
		break;
	case '7':
		return true;
		break;
	case '8':
		return true;
		break;
	case '10':
		return true;
		break;
	case '':
		return true;
		break;
	default:
		return false
		break;
	}
}

// 排序
function search_list_sort() {
	if(initDuplicate){
		initDuplicate();
	}
	var multiselectArr=[];
  $('#multiselect').find('input[name="selectInput"]').each(function () {
    if($(this).is(":checked")){
      multiselectArr.push($(this).val());
    }
  });
	var type = $('.mainInfoBigBox').find('.sortBtn.active').data('type');
	var sort = $('.mainInfoBigBox').find('.sortBtn.active i').attr('data-sort');
	var newObj = jQuery.extend(true, {}, sourceTabsObj);
	newObj.sortField = type;
	newObj.sortWay = sort;
	newObj.mediaInfo = multiselectArr;
	newObj.sensitive = $('#sensitiveselect').find('input[name="selectInput"]:checked').val();
	search_list_load(newObj,true);
}
// loading 显隐
function loading_show(dom,mark) {
	var $dom = $(dom);
	var $loading = $dom.find('.loading_box')
	var $content = $dom.find('.content_box')
	$content.show();
	if(mark == true){
		$loading.show();
// $content.hide();
	}else{
		$loading.hide();
		
	}
}


function nodata_show (dom,mark,nodata,content) {
	var $dom = $(dom);
	var $nodata = $dom.children(nodata)
	var $content = $dom.children(content)
	if(mark == true){
		$nodata.show();
		$content.hide();
	}else{
		$nodata.hide();
		$content.show();
		
	}
}

// //相似文章
// $('body').on('click','.similarityListBtn',function () {
// var uuid = $(this).data('uuid');
// // similarityList ($(this),sourceTabsObj,uuid)
	
// if($(this).hasClass('firstClick')){
// $(this).parents('.mainInfoListHoverBox').next('.similarity-list').stop().slideToggle(300);
// }else{
// $(this).addClass('firstClick');
// search_similarity_list_load($(this),sourceTabsObj,uuid);
// }

// })

// //详细信息列表
// function search_similarity_list_load($this,data,uuid,pageNo,pageSize){
// if(null==pageNo){
// pageNo=1;
// }
// if(null==pageSize){
// pageSize=10;
// }
// var $dom = $this.parents('.mainInfoListHoverBox').next('.similarity-list');
// //debugger;
// var refreshBox = $dom;
// $("<div class='refresh-preloader'><div class='la-timer
// la-dark'><div></div></div></div>").appendTo(refreshBox).fadeIn(300);
// var param = {
// //params:JSON.stringify(data),
// path:'../../../dynamic/search-list.html',
// uuid:uuid,
// pageNo:pageNo,
// pageSize:pageSize,
// sid: data.sid
// }
	
// // $.post('search_al/mod2/searchSimilarityInfos',param,function (data) {
// $.post('search_al/mod2/searchSimilarityInfos',param,function (data) {
// if($dom.find(".checkMoreSimilarAtricles").length>0){
// $dom.find(".checkMoreSimilarAtricles").remove();
// }
// $dom.append(data);
// var refreshPreloader = refreshBox.find('.refresh-preloader'),
// deletedRefreshBox = refreshPreloader.fadeOut(300, function(){
// refreshPreloader.remove();
// });
// })
// }

// 加载更多
// $('body').on('click','.checkMoreSimilarAtricles',function () {
// var $this =
// $(this).parents('.similarity-list').prev('.mainInfoListHoverBox').find('.similarityListBtn');
// var uuid = $this.data('uuid');
// var pageNo = Number($(this).attr("pageno"));
// var pageSize = $(this).attr("pageSize");
// search_similarity_list_load($this,sourceTabsObj,uuid,pageNo+1,pageSize)
	
// })

// function similarityList ($this,sourceTabsObj,uuid) {
// search_similarity_list_load($this,sourceTabsObj,uuid)
// }

// 添加站点集合
$('#siteGroupLoadBox').load('dynamic/search/site-group.html',function () {
	$('#addSiteGroup').click(function () {
		siteGroupBackfill ('','{}','')
	})
})


$('.checkbox_media').on('mouseenter','.active-result',function (e) {
	let str = '<span class="site-group-tools"><font class="fa fa-edit site-group-edit"></font><font class="fa fa-trash-o site-group-delete"></font></span>'
	$(this).append(str);
})
$('.checkbox_media').on('mouseleave','.active-result',function (e) {
	$(this).find('span').remove();
})

// $('body').on('mousedown','.site-group-edit',function (event) {
// console.log($(this).parents('.active-result').text())
// event.stopPropagation();
// return false;
// })

$('body').on('click','.site-group-edit',function (event) {
	let autoId = $('#info_adopt_media_cross').val();
// $("#info_adopt_media_cross").val("");
// $("#info_adopt_media_cross").trigger("chosen:updated");
	$.get('search_al/mod1/getDataSource',{autoId:autoId},function (data) {
		siteGroupBackfill (data.auto_id,data.search_data,data.name)
	})
})

$('body').on('click','.site-group-delete',function (event) {
	let autoId = $('#info_adopt_media_cross').val();
	$("#info_adopt_media_cross").val("");
	$("#info_adopt_media_cross").trigger("chosen:updated");
	swal({ 
	  title: "确定删除吗？", 
	  text: "你将无法恢复该数据！", 
	  type: "warning",
	  showCancelButton: true, 
	  confirmButtonColor: "#DD6B55",
	  confirmButtonText: "确定删除", 
	  cancelButtonText: "取消删除",
	  closeOnConfirm: false
	},
	function(){
	  $.get('search_al/mod1/deleteDataSource',{autoId:autoId},function (result) {
				if (result.status == 'ok') {
					swal.close();
					toastr["success"]("删除成功");
					$("#info_adopt_media_cross option[value='"+autoId+"']").remove();
					$("#info_adopt_media_cross").trigger("chosen:updated");
		
				}else{
					toastr["error"]("删除失败");
				}
				console.log(result)
			}) 
	});	
})

// 给源集合回填
function siteGroupBackfill (id,search_data,name) {
	$('#siteGroupModal').modal('show');
	console.log(search_data)
	$('#saveSiteBtn').data('id',id);
	$('.data-source-name').val(name);
	let data = JSON.parse(search_data);
	$(".site-select").val("");
// $(".site-select").find('option').attr("selected",false);
	$(".site-select").data('type','1');
	$(".site-select").siblings('div').find('.btn-dropdown').html('包含 <span class="caret"></span>');
	
	for (i in data) {
		let obj = data[i];
		let arr = []
		for (x in obj) {
			let siteGroup = obj[x];
			if (x == 'contains') {
				$(".site-select[data-source='"+i+"']").data('type','1');
				$(".site-select[data-source='"+i+"']").siblings('div').find('.btn-dropdown').html('包含 <span class="caret"></span>')
			}else{
				$(".site-select[data-source='"+i+"']").data('type','2')
				$(".site-select[data-source='"+i+"']").siblings('div').find('.btn-dropdown').html('不包含 <span class="caret"></span>')
			}
			for (z in siteGroup) {
				siteBackfill (i,siteGroup[z],arr)
			}
		};
		$(".site-select[data-source='"+i+"']").val(arr);
 	};

	$(".site-select").trigger("chosen:updated");
}
// 媒体站点筛选联动
$('#info_adopt_media_cross').on('change', function(e, params) {
	if(params!=undefined){
		$.get('search_al/mod1/getDataSource',{autoId:params.selected},function (data) {
			var num=0;
			$('.checkbox_source_type').find('input[name="dataSource"]').prop("checked","");
			for (i in JSON.parse(data.search_data)) {
				$('.checkbox_source_type').find('input[value="'+i+'"]').prop("checked","checked");
				num++;
			};
			if(num==8){
				$('.checkbox_source_type').find('input[name="dataSource"]').prop("checked","checked");
			}
			setTimeout(function() {
				screenInfoShow();
			}, 100);
		})
	}else {
		$('.checkbox_source_type').find('input[name="dataSource"]').prop("checked","").prop("disabled","");
		screenInfoShow();
	}
});


// 站点回填
function siteBackfill (sourceType,obj,arr) {
	let $select = $(".site-select[data-source='"+sourceType+"']")
	let $dom = $select.find('option[value="'+obj.uid+'"]')
	if ($dom.length>0) {
// $dom.attr("selected",true);
	}else{
		$select.append('<option selected="selected" value="'+obj.uid+'">'+(obj.name!= undefined?obj.name:obj.uid)+'</option>');
	}
	arr.push(obj.uid)

}

// //////////-----------------mod1 代码---------------////////////

// 点击历史搜索
$('.history-search-box').on('click','a',function () {
	var val = $(this).text();
	$('.search-input').val($.trim(val));
	search_info()
})

// 首页跳转
function search_info() {
	var val = $('.search-input').val();
	val = $.trim(val);
	if(val){
		saveHistory(val);
		window.location.href = 'search_al/mod2?kw='+val;
	}else {
		toastr["info"]("内容不能为空");
	}
}

$('#search_info_btn').on('click',function () {
	search_info()
})
$('.search-input').on('keydown',function (event) {
	var e = event || window.event
	if (e && e.keyCode==13) {
		search_info()
	}
})

// 保存历史检索
function saveHistory(val) {
	var oldHistory=JSON.parse(localStorage.getItem('searchHistory'));
	if(oldHistory){
		oldHistory.forEach(function (item,i) {
			if(i<4){
				if(item==val){
					oldHistory.splice(i, 1);
				}
			}else{
				oldHistory.splice(i, 1);
			}
		});
		oldHistory.unshift(val);
		localStorage.setItem('searchHistory', JSON.stringify(oldHistory));
	}else{
		var historyArr=[];
		historyArr.unshift(val)
		localStorage.setItem('searchHistory', JSON.stringify(historyArr));
	}
}


// 保存检索条件
$('body').on('click','#saveSearchBtn',function () {
	saveSearchParam($searchVo)
})
$('#saveSearch').on('hidden.bs.modal', function () {
  $('#saveSearch input').val('');
})

$('#saveSearch').on('show.bs.modal', function () {
	if(urlAutoId){
		var data = JSON.parse(sessionStorage.getItem('autoId_'+urlAutoId));
		$('#saveSearch input#saveSearchName').val(data.name);
		
	}
	if (saveSearchName) {
		$('#saveSearch input#saveSearchName').val(saveSearchName);
	}
})
var saveSearchAutoId = 0;
var saveSearchName = '';
function saveSearchParam(data) {
	if (searchVo() == 'false') {
		return
	}
	var $val = $('#saveSearch input').val();
	if($.trim($val)==''){
    toastr["info"]("搜索名称不能为空");
    return ;
  }
	var $type = 0
	var autoId = iFunc.UrlParse().autoId;
	if (saveSearchAutoId) {
		autoId = saveSearchAutoId
	}
	if (iFunc.UrlParse().type == 'advanced') {
		$type = 1
	}

	$.post('search_al/mod1/saveSearchParam',{params:JSON.stringify(searchVo()),name:$val,searchLevel:$type,autoId:autoId},function (data) {
		if (data.status == 'ok') {
			saveSearchAutoId = data.autoId;
			saveSearchName = $val;
			if(saveSearchAutoId==undefined){
				saveSearchAutoId=iFunc.UrlParse().autoId;
			}
			if($('#saveSearch .hiddenBox').is(':visible')){
				addAnalysisFunc(saveSearchAutoId);
			}else {
				toastr["success"]("保存成功");
				$('#saveSearch').modal('hide');
			}
			// 页面保存sid，供剔除按钮使用
			$(".mainInfoBigBox").find("#mainInfoListBox").attr("data-sid",saveSearchAutoId);
			// 更新sessionStorage
			let autoId = iFunc.UrlParse().autoId;
			if(autoId){
				let searchItem = JSON.parse(sessionStorage["autoId_"+autoId]);
				searchItem.name =$val;
				searchItem.search_terms=JSON.stringify(searchVo());
				sessionStorage["autoId_"+autoId]=JSON.stringify(searchItem);
			}
			
		}else if(data.status == 'exist'){
			toastr["info"]("名称已存在");
		}else{
			toastr["error"]("保存失败,请刷新重试");
		}
	})
}

$('body').on('change','#saveSearchCheckbox',function () {
	$('#saveSearch .hiddenBox').slideToggle(300);
	if ($('#saveSearchCheckbox').is(':checked')) {
		$('input.analysisName').val($('#saveSearchName').val());
	}
})

function addAnalysisFunc(autoId) {
	if($.trim($('#saveSearch input.analysisName').val())){
    $.ajax({
      type: "post",
      url: "analysis_al/mod1/addAnalysisSearchConfig",
      data: {
        sid: autoId,
        analysisCategory: $('#saveSearch .analysisType').val(),
        name: $('#saveSearch .analysisName').val(),
        modules: '',
        layout: ''
      },
      success: function (data) {
        if(data.status=='ok'){
					toastr["success"]("保存并添加成功");
					$('#saveSearch').modal('hide');
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
}

// 分析类型选择
function analysisTypeSaveInit() {
  var optionHtml='';
  $analysisCategory.forEach(function (item,i) {
    optionHtml+='<option value="'+item.code+'" >'+item.name+'</option>';
  });
  $('#saveSearch .analysisType').html(optionHtml);

  $('#saveSearch .analysisType').chosen({
		disable_search: true,
		max_selected_options: 1,
		search_contains: true,
		width: '100%'
	});
}
