var $layout = '{"containerType":"panel","state":{"element":"titleezqwfw","width":1890,"height":293,"id":"title","sid":950,"autoid":1,"name":"网站新闻主要来源"},"children":[]}'
var $layout = '{"containerType":"vertical","state":{"width":1890,"height":293},"children":[{"containerType":"panel","state":{"element":"module1123qwe","autoid":2,"width":1890,"height":80,"name":"aaaa","id":"module1","sid":950},"children":[]},{"containerType":"panel","state":{"element":"titleezqwfw","width":1890,"height":115,"id":"title","sid":950,"autoid":1,"name":"网站新闻主要来源"},"children":[]}]}'
var $sid = ""

var ppp_, ddd_;
var undockInitiatorArr = []

var loadPathObj = {}
var dockManager;
var documentNode;
var childInfo_s, childInfo_material;
var childInfo_material_mark = true;
//var htmlobj = $.ajax({
//	url: "detail/detail.html",
//	async: false
//});
//$("#dialog_box").append(htmlobj.responseText);


if(localStorage.getItem('layout_type') == 1) {
	//		$("#mod4_box").append(htmlobj.responseText);
} else if(localStorage.getItem('layout_type') == 2) {
	//		$("#author_box").append(htmlobj.responseText);
} else if(localStorage.getItem('layout_type') == 3) {
	//		$("#modal_box").append(htmlobj.responseText);
} else {
	window.localStorage.setItem("layout_type", 1)
}
// $('.layout_btn[data-type=' + localStorage.getItem('layout_type') + ']').addClass('btn-success')

// Convert a div to the dock manager.  Panels can then be docked on to it
var divDockManager = document.getElementById("my_dock_manager");
dockManager = new dockspawn.DockManager(divDockManager);
dockManager.initialize();
// Let the dock manager element fill in the entire screen
var onResized = function(e) {
	//		dockManager.resize(window.innerWidth - (divDockManager.clientLeft + divDockManager.offsetLeft), window.innerHeight - (divDockManager.clientTop + divDockManager.offsetTop));
	dockManager.resize(window.innerWidth - (divDockManager.clientLeft + 30), window.innerHeight - (divDockManager.clientTop + divDockManager.offsetTop + 15));
}
window.onresize = onResized;
onResized(null);
var myLayout;

function dockInit ($layout) {
	$layout = JSON.stringify($layout);
	var savedState = $layout;
	if(savedState !== '') {
		if (savedState.indexOf('containerType')>=0) {
			TraversalObject(JSON.parse($layout));
			dockManager.loadState(savedState);
		}else{
			let savedStateAnalysis = aLayoutToLsLayout (JSON.parse(savedState))
			aLayoutTraversalObject(savedStateAnalysis)
			dockManager.loadState(JSON.stringify(savedStateAnalysis));
		}
		$('.tab-handle').click()
	}
}

$('body').on('click', '.tab-handle', function() {
	var str = $(this).text()
	var markP = $('[caption=' + str + ']').text();
	var mark = $('[caption=' + str + ']').find('div').text();
	var id = $('[caption=' + str + ']').find('div').prop('id') ? $('[caption=' + str + ']').find('div').prop('id') : $('[caption=' + str + ']').prop('id');
	if(mark == '' || markP == '') {
		if($('[caption=' + str + ']').find('div').length) {
			$('[caption=' + str + ']').find('div').load(loadPathObj[id])
		} else {
			$('[caption=' + str + ']').load(loadPathObj[id])
		}

	}

})

//解析分析布局 生成 大屏布局
function aLayoutToLsLayout (arr) {
	var children = [];
	var children2 = [];
	let width = Math.ceil(1920/arr.length);
	var layout = {};
	if (arr.length<=4) {
		$.each(arr,function (i,v) {
			var random = getRandom(6)
			children.push({
				"containerType": "panel",
	            "state": {
	                "element": v.id+random,
	                "width": width,
	                "height": 842,
	                'name':v.name,
	                'id':v.autoid,
	                'random':random,
	                'sid':v.sid,
	                'content':v.content
	            },
	            'children':[]
			})
		})
		layout = {
		    "containerType": "horizontal",
		    "state": {
		        "width": 1445,
		        "height": 842
		    },
		    "children": children
		};
	}else{
		$.each(arr,function (i,v) {
			var random = getRandom(6);
			let obj = {				
				"containerType": "panel",
	            "state": {
	                "element": v.id+random,
	                "width": width,
	                "height": 400,
	                'name':v.name,
	                'id':v.autoid,
	                'sid':v.sid,
	                'random':random,
	                'content':v.content
	            },
	            'children':[]		
			};
			if (i%2 == 0) {
				children.push(obj)
			}else{
				children2.push(obj)
			}
			
		});
		console.log(children)
		console.log(children2)
		layout = {
		    "containerType": "vertical",
		    "state": {
		        "width": 1890,
		        "height": 800
		    },
		    "children": [
		    	{
		    		"containerType": "horizontal",
		            "state": {
		                "width": 1890,
		                "height": 400
		            },
		            "children": children2
		    	},{
		    		"containerType": "horizontal",
		            "state": {
		                "width": 1890,
		                "height": 400
		            },
		            "children": children
		    	}
		    ]
		};
	}
		
	return layout
}

function aLayoutTraversalObject(s) {
	for(var i in s) {
		if(typeof s[i] == "object") {
			aLayoutTraversalObject(s[i])
		} else {
			if(i == 'id') {
//				var id = s[i].replace(/[^0-9]/ig, "");
				aLayoutAddModules(s[i], $layoutObj[s[i]],s.name,s.random,s)
			}　　　　　
		}
	}
}

function aLayoutAddModules(i, obj,name,random,layout) {
	// console.log(obj)
	var content = layout.content!= undefined?layout.content:'';
	var str = '<div data-autoid="'+obj.autoid+'" data-id="'+obj.id+'" data-sid="'+(layout.sid!= undefined?layout.sid:$sid)+'" id="' + obj.id+random + '" class="row module_box '+obj.id+" "+(layout.border!= undefined?layout.border:'')+'" caption="' + name + '"  style="margin: 0;" content="'+content+'"><div  class="'+(layout.style!= undefined?layout.style:'')+'"></div><bg></bg></div>';
	$('#module_box').append(str);
	let $dom = $('#' + obj.id+random);
	let params = $params(obj,layout);
	let sid = layout.sid!= undefined?layout.sid:$sid;
	params.sid = sid
	if(obj.id != '') {
		if(obj.id == 'search_infos') {
			ipageLoad($dom,params,'search_infos_ipage'+random,layout,obj)
//			let searchInfoStr = '<div class="content-box">' +
//				'	<div class="content-title small-box i-block">' +
//				'		<h4 class="zero-m echarts3-title">信息列表</h4>' +
//				'		<div class="content-tools i-block pull-right">' +
//				'			<a class="close-btn" onclick="removeWidget($(this).parents(\'.gs-w\'))">' +
//				'				<i class="fa fa-times"></i>' +
//				'			</a>' +
//				'		</div>' +
//				'	</div>' +
//				'	<div class="clearfix"></div>' +
//				'	<div class="small-box content_">' +
//				'	<div id="search_infos_ipage">' +
//				'	</div>' +
//				'	</div>' +
//				'</div>';
//			$dom.find('div').append(searchInfoStr);
//			var option = {};
//			option.param = params;
//			option.pageSize = 10;
//			option.isExport = false;
//			option.page_foot_html = true;
//			option.callback = function() {
//			}
//			var obj = new iPage.PageLoad('search_infos_ipage', obj.url, option).init();
		}else if(obj.id == 'text_module'||obj.id == 'picture_module'){
			$dom.find('div').load(obj.url,{'data':layout.content},function () {
				if(name!=""){
					$dom.find('.echarts3-title').text(name);
				}
			})
			$dom.resize(function() {
				rightBgResize();
			})
		} else {
			// $dom.find('div').load(obj.url, params,function () {
			$dom.find('div').load(obj.url,function () {
				if(name!=""){
					$dom.find('.echarts3-title').text(name);
				}
				let id = obj.id+random
				listResize();
				rightBgResize();
				module_box_load (id,obj.autoid)
			});
			chartResize($dom); 
		}
	}
}

var undockInitiatorContentTime;
//退出编辑模式
function disableWidget() {
	$('body').removeClass('edit-screen');
	$('.index_top>h2').prop('contenteditable',false)
	onResized(null)
//	if ($layout != dockManager.saveState()) {
		window.location.reload();
//	}
}
//编辑模式
function enableWidget() {
	$('body').addClass('edit-screen');
	$('.index_top>h2').prop('contenteditable',true)
	//		undockInitiatorContent (true)
	onResized(null)
}

//内容可拖动
function undockInitiatorContent(mark) {
	$.each(undockInitiatorArr, function(i, v) {
		v.enabled = mark;
	});
}

$('.showHideTitle').click(function() {
	if($(this).data('type') == 'hide') {
		$(this).data('type', 'show');
		$('.panel-titlebar,.tab-handle-list-container,.tab-handle-content-seperator').hide();
		$('.splitbar-vertical,.splitbar-horizontal').addClass('splitbar-transparent');
	} else {
		$(this).data('type', 'hide');
		$('.panel-titlebar,.tab-handle-list-container,.tab-handle-content-seperator').show();
		$('.splitbar-vertical,.splitbar-horizontal').removeClass('splitbar-transparent');
	}
	onResized(null)
}).click();
$('.layout_save_btn').click(function() {
	localStorage.setItem('savedState_' + localStorage.getItem('layout_type'), dockManager.saveState());
})


// 添加及回填
$('body').on('click', '#addNewAnalysisModal', function() {
	//	  var tempData=dockManager.saveState();
	//	   console.log(tempData);
	//	   childInfoEach(JSON.parse(tempData))
});

//添加模块
function addWidget(array, text) {
	childInfo_material_mark = true
	console.log(dockManager)
	childInfoEach(dockManager.context.model.rootNode, true)
	$.each(array, function(i, v) {
// 		if (v.autoid == 0) {
// 			var id = 'mod' + (v.autoid+getRandom(6)) + '_box';
// 		}else{
// 			var id = 'mod' + v.autoid + '_box';
// 		}
		var id = v.id + (getRandom(6));
		var path = v.url;
		if($("#my_dock_manager #" + id).length == 0) {
			if(path && id) {
				if($("#module_box #" + id).length == 0) {
					var style = v.autoid==0?"moduleHidden":""
					var str = '<div  data-autoid="'+v.autoid+'" id="' + id + '" caption="' + text + '" class="row module_box" style="margin: 0;"><div class='+style+'></div><bg></bg></div>';
					$('#module_box').append(str);
					// $("#" + id).find('div').load(path, $params(v), function() {
					$("#" + id).find('div').load(path,  function() {
						dockManager.dockUp(childInfo_material, new dockspawn.PanelContainer(document.getElementById(id), dockManager), 0.4);
						module_box_load (id)
					});
				} else {
					dockManager.dockUp(childInfo_material, new dockspawn.PanelContainer(document.getElementById(id), dockManager), 0.4);
					module_box_load (id)
				}
				//				$(this).data('type', 'hide');
				$('.panel-titlebar,.tab-handle-list-container,.tab-handle-content-seperator').show();
				$('.splitbar-vertical,.splitbar-horizontal').removeClass('splitbar-transparent');
				//				onResized(null)
				
			}
		}
	});
}

//模块加载回调
function module_box_load (id,autoid) {
	if ('8' == autoid || '9' == autoid || '10' == autoid || '11' == autoid) {
// 		let parent = $("#" + id).find('.topTenListBox').prop('id')
// 		let child1 = $("#" + id).find('.topTenListBox_1').prop('id')
// 		let child2 = $("#" + id).find('.topTenListBox_2').prop('id')
// 		zbdp.moveHorizental($('#'+parent), 'ranking-title', '')
	}
}


//移除模块
function removeWidget(a, array) {
	$.each(array, function(i, v) {
		var id = 'mod' + v.autoid + '_box';
		$('#' + id).parents('.panel-base').find('.panel-titlebar-button-close').click();
	});
}

//保存布局
function saveWidget() {
	let layout = dockManager.saveState();
	let name = $('.index_top>h2').text();
	$.post('display_al/mod1/updateDisplayConfs',{autoId:$autoId,layout:layout,name:name},function (data) {
		if(data.status=='ok'){
	        toastr["success"]("保存成功");
	        $('body').removeClass('edit-screen');
	        $('#saveNewAnalysisModal').parents('.optionBtn').siblings().show(100);
    		$('#saveNewAnalysisModal').parent().hide(100).siblings('i.iconfont').show(100);
//  		$('.dock-content>div').each(function (i,v) {
//  			chartResize($(v))
//  		})
    		
	    }else {
	        toastr["error"]("保存失败,请重试");
	    }
	})
}

function destroyViewBoxDropdown(t) {
	var div_id = $(t.elementContent).prop('id');
	$('.view_box_dropdown a[data-id="' + div_id + '"]').removeClass('active');
}
// var $yunyong_style = localStorage.getItem("yunyong_<#if userName??>${userName}</#if>_style");
// if(!$yunyong_style) {
// 	// $('body').addClass('ribao')
// } else {
// 	$('body').addClass($yunyong_style)
// }
// 
// var $yunyong_layout = localStorage.getItem("yunyong_<#if userName??>${userName}</#if>_layout")
// if($yunyong_style != null) {
// 	$('.setting_style_box[data-style="' + $yunyong_style + '"]').addClass('active')
// } else {
// 	$('.setting_style_box').eq(2).addClass('active')
// }
// 
// var $user_mapName = "<#if mapName??>${mapName}</#if>";
// var $user_mapLogo = "<#if mapLogo??>${mapLogo}</#if>";
// var $instId = "<#if instId??>${instId}</#if>";
// $('#LOGO_').prop('src', '<#if screenLogo??>${screenLogo}</#if>')
// 
function getUrlParam(name) {
	var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)"); //构造一个含有目标参数的正则表达式对象
	var r = window.location.search.substr(1).match(reg); //匹配目标参数
	if(r != null) return unescape(r[2]);
	return null; //返回参数值
}

// 顶部数字表
//	$("#clock2").MyDigitClock({
//		fontSize: 36,
//		fontFamily: "Century gothic",
//		fontColor: "#fff",
//		fontWeight: "bold",
//		bAmPm: false,
//		background: 'transparent',
//		bShowHeartBeat: true
//	});

var myChartObj = {};

//layoutLoad()


function layoutLoad() {
	for(let i in $layoutObj) {
		addModules(i, $layoutObj[i])
	}
}

function TraversalObject(s) {
	for(var i in s) {
		if(typeof s[i] == "object") {
			TraversalObject(s[i])
		} else {
			if(i == 'element') {
				var id = s[i].replace(/[^0-9]/ig, "")
//				addModules(s, $layoutObj[id])
				var random = getRandom(6)
				var random = s.element.substr(-6);
				aLayoutAddModules(i, $layoutObj[s.autoid],s.name,random,s)
			}　　　　　
		}
	}
}

function addModules(i, obj) {
	var text = $('.modalSelectList .file-item[data-id="' + i+ '"] .file-date').text();
	var str = '<div id="mod' + i + '_box" caption="' + text + '" class="row module_box" style="margin: 0;"><div></div></div>';
	$('#module_box').append(str);
	let $dom = $('#mod' + i + '_box');
	if(obj.id != '') {
		if(obj.id == 'search_infos') {
			let searchInfoStr = '<div class="content-box">' +
				'	<div class="content-title small-box i-block">' +
				'		<h4 class="zero-m echarts3-title">信息列表</h4>' +
				'		<div class="content-tools i-block pull-right">' +
				'			<a class="close-btn" onclick="removeWidget($(this).parents(\'.gs-w\'))">' +
				'				<i class="fa fa-times"></i>' +
				'			</a>' +
				'		</div>' +
				'	</div>' +
				'	<div class="clearfix"></div>' +
				'	<div class="small-box content_">' +
				'	<div id="search_infos_ipage">' +
				'	</div>' +
				'	</div>' +
				'</div>';
			$dom.find('div').append(searchInfoStr);
			obj.now = moment().format('YYYY-MM-DD HH:mm:ss');
			var option = {};
			option.param = $params(obj);
			option.pageSize = 10;
			option.isExport = false;
			option.page_foot_html = true;
			option.callback = function() {
				
				//				let refreshPreloader = refreshBox.find('.refresh-preloader'),
				//			        deletedRefreshBox = refreshPreloader.fadeOut(300, function(){
				//			        refreshPreloader.remove();
				//			    });
				//			    $.each($('.mainInfoList'),function(i,v){
				//			    	if(i>4){
				//			    		$(v).remove();
				//			    	}
				//			    })
			}
			var obj = new iPage.PageLoad('search_infos_ipage', obj.url, option).init();
			$dom.resize(function() {
				rightBgResize();
			})
		} else {
			$dom.find('div').load(obj.url, $params(obj),function () {
				let id = 'mod' + i + '_box'
				listResize();
				rightBgResize();
				module_box_load (id)
			});
			chartResize($dom); 
		}
	}
}

//自适应
function chartResize(dom) {
	dom.resize(function() {
		if(!$.isEmptyObject(myChartObj)) {
			for(chart in myChartObj) {
				myChartObj[chart].resize();
			}
		}
		listResize();
		rightBgResize();
	});
}
function listResize() {
	$('body').find('.topTenListBox').each(function (i,item) {
		if($(this).width()<650){
			$(this).find('li').addClass('minWidthStyle');
		}else {
			$(this).find('li').removeClass('minWidthStyle');
		}
	});
}
function rightBgResize() {
	// console.log('380')
	$('body').find('.content-title.small-box.i-block').each(function (i,item) {
		if($(this).width()<380){
			$(this).addClass('minWidth');
		}else {
			$(this).removeClass('minWidth');
		}
	});
}


//版面 上下翻页
$('.font_up').click(function() {
	var _aH = 0
	var index_ = $('.banmian_left').find('li.ban_active').index()
	if(0 != index_) {
		$('.banmian_left').find('li').eq(index_ - 1).click()
		_aH = $('.banmian_left li.ban_active').offset().top
		if(_aH < 200) {
			$('#content-1').animate({
				"scrollTop": $('#content-1').scrollTop() - 120 + "px"
			}, 200);
		}
	}
})
$('.font_down').click(function() {
	var _aH = 0
	var index_ = $('.banmian_left').find('li.ban_active').index()
	if($('.banmian_left').find('li').length > index_) {
		$('.banmian_left').find('li').eq(index_ + 1).click()
		_aH = $('.banmian_left li.ban_active').offset().top
		if(_aH > 500) {
			$('#content-1').animate({
				"scrollTop": $('#content-1').scrollTop() + 120 + "px"
			}, 200);
		}
	}
})

function scroll_(parent, child1, child2) {
	var demo = document.getElementById(parent)
	var demo1 = document.getElementById(child1)
	var demo2 = document.getElementById(child2)
	var speed = 100
	var num = $('#' + child1).find('li').length
	if(num > 5) {
		demo2.innerHTML = demo1.innerHTML

		function Marquee() {
			if(demo2.offsetHeight - demo.scrollTop <= 0) {
				demo.scrollTop -= demo1.offsetHeight
			} else {
				demo.scrollTop = demo.scrollTop + 1
			}
		}
		var MyMar = setInterval(Marquee, speed)
		demo.onmouseover = function() {
			clearInterval(MyMar)
		}
		demo.onmouseout = function() {
			MyMar = setInterval(Marquee, speed)
		}
	}
}

//切换背景
var bgs = ['indexBg_1.jpg', 'indexBg_2.jpg', 'indexBg_3.jpg']
var bgs_num = 0
$('#switch_bg').click(function() {
	var url_ = bgs[bgs_num]
	if(bgs_num < bgs.length) {
		$('.index_bg').css('background-image', 'url(../images/' + url_ + ')')
	} else {
		$('.index_bg').css('background-image', 'url(../images/index_bg1.jpg)')
		bgs_num = 0
		return;
	}
	bgs_num++
})

//自适应宽度
//window.onresize = function(){
//	resizeCenter()
//}
//resizeCenter()
function resizeCenter() {
	var ratio = $(window).width() / 1920;
	if($(window).height() < 1920 / 1.7) {
		$('html').addClass('html_')
	}
	$('body').css({
		transform: "scale(" + ratio + ")",
	});
}
//	$(".container1").load("art/get_art_info_list");

//设置 *暂时只支持更改样式
//点击空白出 收回
$(document).click(function(e) {
	var _con = $('#setting'); // 设置目标区域
	if(!_con.is(e.target) && _con.has(e.target).length === 0) {
		$('#setting').animate({
			'right': "-330px"
		})
	}

})
$('#Set').click(function() {
	//	if ($('body').hasClass("classic")) {
	//		$('body').removeClass('classic')
	//	}else{
	//		$('body').addClass('classic')
	//	}
	if($('#setting').css('right') == '-330px') {
		$('#setting').animate({
			'right': "0px"
		})
	} else {
		$('#setting').animate({
			'right': "-330px"
		})
	}

})

//设置样式
$('.setting_box .nav a').click(function() {
	$(this).addClass('active').parent().siblings().find('a').removeClass('active');
	if($(this).data('type') == 0) {
		$('.setting_style').show()
		$('.setting_scene_box').hide()
		$('#layout_save').hide()
	} else {
		$('.setting_style').hide()
		$('.setting_scene_box').show()
		$('#layout_save').show()
		$.each($yunyong_layout, function(i, v) {
			$('.layout_move').eq(i).prop('id', v)
		});
	}

})
$('body').on('click', '.setting_style_box', function() {
	var $style = $(this).data('style')
	$('.setting_style_box').removeClass('active');
	$(this).addClass('active');
	$('body').removeClass($('body').prop('class')).addClass($style)
	window.localStorage.setItem("yunyong_<#if userName??>${userName}</#if>_style", $style)
});

//更改布局
var $layout_mod = ''
$('.layout_move').click(function() {
	if($layout_mod == '') {
		$layout_mod = $(this).prop('id');
		$(this).addClass('active')
	} else {
		$('.layout_move').removeClass('active')
		var $this = $(this).prop('id')
		$('#' + $layout_mod).prop('id', $this)
		$(this).prop('id', $layout_mod);
		if($('#adoptInfoPage').parent().find('#banmianPage').length > 0) {
			$('#' + $this).prop('id', $layout_mod)
			$(this).prop('id', $this)
			$layout_mod = ''
			return;
		}
		$layout_mod = '';
	}

})
$('#layout_save').click(function() {
	var arr = []
	$('.setting_layout_box').find('.layout_move').each(function(i, v) {
		arr.push($(this).prop('id'))
	})
	arr = arr.join(",")
	$('#setting').animate({
		'right': "-330px"
	})
	window.localStorage.setItem("yunyong_<#if userName??>${userName}</#if>_layout", arr)
	window.location.reload();
})
$('.layout_btn').click(function() {
	var type = $(this).data('type');
	window.localStorage.setItem("layout_type", type)
	window.location.reload();
})

//弹框  dropdown-menu
//$("#dialog_box").dialog({
//	autoOpen: false,
//	minHeight: 500,
//	minWidth: 400,
//	height: 800,
//	width: 1000,
//	show: {
//		effect: "blind",
//		duration: 300
//	},
//	hide: {
//		effect: "blind",
//		duration: 300
//	}
//});

//自适应

function childInfoEach(data, mark) {
	if(data) {
		var childrenInfo = data.children;
		;
		childrenInfo.forEach(function(childInfo) {
			//获取第一个元素
			if(childInfo.container.containerType == 'panel' && childInfo_material_mark) {
				childInfo_material_mark = false;
				childInfo_material = childInfo;
				console.log(childInfo_material)
			}
			//递归存在元素
			if(!mark) {
				var div_id = $(childInfo.container.elementContent).prop('id');
				if(div_id != undefined) {
					$('.modalSelectList .file-item[data-id="' + div_id.replace(/[^0-9]/ig, "") + '"]').find('.file-preview').addClass('active');
				}
			}

			//递归中间元素
			if(childInfo.container.containerType == 'fill') {
				childInfo_s = childInfo
				//					return childInfo
			}
			var  t  =  childInfoEach(childInfo, mark);                
			if(t != null) return t;
		});
		return  null;
	}
}
