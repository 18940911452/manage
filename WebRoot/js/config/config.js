$('#iframeBigScreen').load(function() {
	var deptObjs = document.getElementById("iframeBigScreen");
	//判断此元素是否存在
	if (deptObjs != null) {
		//设置该元素的样式或其他属性
		$(deptObjs).contents().find('#my_dock_manager').addClass("noZIndex"); //!important用来提升指定样式条目的应用优先权
	}
	configInit(1);


	window.onresize = function() {
		resizeCenter()
	}
	resizeCenter()

	function resizeCenter() {
		var ratio = $('.iframe-box').width() / 3840;
// 		if ($(window).height() < 1920 / 1.7) {
// 			$('html').addClass('html_')
// 		}
		$(deptObjs).contents().find('body').css({
			transform: "scale(" + ratio + ")",
			width: "3840px ",
    height: "1080px ",
    "transform-origin": "left top 0px"
		});
	}
})


// 初始化
configPageInit()

function configPageInit() {

	// 生成模块页面
	var str = "";
	$.each($layoutObj, function(i, v) {
		str += "<button type='button' class='btn btn-default addModuleBtn' value=" + v.autoid + ">" + v.name + "</button>";
	});
	$('#moduleBox').html(str);

	// 生成样式条件
	$.each($borderStyle, function(i, v) {
		$('#selectStyle').append('<option value="' + v.style + '">' + v.name + '</option>')
		$('#selectBorder').append('<option value="' + v.style + '">' + v.name + '</option>')
	})

};

// 选择大屏功能
$('#selectBigScreen').change(function(data) {
	changeIframeUrl(this.value,$(this).find("option:selected").data('type'))
});

// 更改iframe src
function changeIframeUrl(value,type) {
	// $('#iframeBigScreen').prop('src', 'index.html?view=' + type);
	$('#iframeBigScreen').prop('src', value);
	configInit(type)
}

// 配置参数初始化
function configInit(type) {
	$.getJSON('../json/view' + type + '.json', function(data) {
		var global = data.global
		console.log(global)

		$("#selectStyle option[value='" + global.style + "']").attr("selected", "selected");
		$("#selectBg option[value='" + global.backgroundImage + "']").attr("selected", "selected");
		$("#selectBgModule option[value='" + global.backgroundModule + "']").attr("selected", "selected");
	})
}


// 判断添加背景还是模块
var markBackground = true;
$('.addBackgroundModule').click(function() {
	markBackground = true;
})
$('.addDockModule').click(function() {
	markBackground = false;
})


// 添加模块
$('body').on('click', '.addModuleBtn', function() {
	let param = {}
	param.mark = markBackground
	param.data = this.value
	param.type = 1
	send(param)
})

// 更改背景
$('#selectBg').change(function() {
	let param = {}
	param.value = this.value
	param.type = 3
	send(param)
});
$('#selectBgModule').change(function() {
	let param = {}
	param.value = this.value
	param.type = 5
	send(param)
});


// 更改边框
$('#selectStyle').change(function() {
	let param = {}
	param.style = this.value
	param.type = 2
	send(param)
});


// 更改模块边框
$('#selectBorder').change(function() {
	let param = {}
	param.id = $('#oneModuleStyleId').text();
	param.style = this.value
	param.type = 4
	send(param)
});

// 更改模块标题
$('#selectName').blur(function() {
	let param = {}
	param.id = $('#oneModuleStyleId').text();
	param.name = $('#selectName').val();
	param.type = 6
	send(param)
});


// 生成数据
$('#generateDataBtn').click(function() {
	var data = {};
	var global = {};
	global.header = "header1"
	global.style = window.frames["iframeBigScreen"].contentWindow.document.body.className.split(" edit-screen")[0];
	global.backgroundImage = $(window.frames["iframeBigScreen"].contentWindow.document.body).find('#topModule').attr(
		'bg');
	global.backgroundModule = $(window.frames["iframeBigScreen"].contentWindow.document.body).find('#topModule').attr(
		'module');
	var layout = window.frames["iframeBigScreen"].contentWindow.dockManager.saveState();
	data.global = global;
	data.module = JSON.parse(layout);
	console.log(data)
	$('textarea').val(JSON.stringify(data))
})

function send(data) {
	// var data = document.querySelector('#data').value;

	// 注意: 只会触发当前window对象的message事件
	// 也可以访问子页面的window对象，触发子页面的message事件 (window.frames[0].postMessage(...))
	// window.postMessage(data, '/'); 
	// data = {name: 'sandy', age: 20, fav: {sing: true, shop: false}}; // 也可以传普通对象
	window.frames[0].postMessage(data, '/'); // 触发同域子页面的message事件
	//window.frames[0].postMessage(data, 'http://localhost:9022/'); // 触发跨域子页面的messag事件
}

// 当前页面执行 window.postMessage(..)
// 或
// 子页面执行 parent.postMessage(...) 都会触发下面的回调, messageEvent.source不同而已
window.addEventListener('message', function(messageEvent) {
	var data = messageEvent.data; // messageEvent: {source, currentTarget, data}
	console.info('message from child:', data);
	if (data.type == 1) {
		$('#oneModuleStyleId').html(data.id);
		$('#selectName').val(data.name)
		var border = $('#iframeBigScreen').contents().find('#my_dock_manager').find('#' + data.id).children('div').prop(
			'class');
		console.log(border)
		$("#selectBorder").val(border)

	}

}, false);
