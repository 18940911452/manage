var daterangepicker_separator = ' 至 ';

var daterangepicker_option = {
	startDate: moment().subtract(1, 'days'),
	endDate: moment(),
	minDate: moment().subtract(6, 'year'),
	timePicker: false,
	timePickerIncrement: 1,
	timePicker12Hour: false,
	ranges: {
//		'不限': [''],
		'今天': [moment(), moment()],
//		'昨天': [moment().subtract(1, 'days'), moment().subtract(1, 'days')],
		'一周': [moment().subtract(7, 'days'), moment()],
		'一月': [moment().subtract(1, 'months'), moment()],
		'三月': [moment().subtract(3, 'months'), moment()],
	},
	opens: 'right',
	buttonClasses: ['btn btn-default'],
	applyClass: 'btn-small btn-primary',
	cancelClass: 'btn-small',
	separator : daterangepicker_separator,
	locale: {
		format: 'YYYY-MM-DD',
		applyLabel: '确定',
		cancelLabel: '关闭',
		fromLabel: '开始时间',
		toLabel: '结束时间',
		customRangeLabel: '自定义',
		daysOfWeek: ['七', '一', '二', '三', '四', '五', '六'],
		monthNames: ['一月', '二月', '三月', '四月', '五月', '六月', '七月', '八月', '九月', '十月', '十一月', '十二月'],
		firstDay: 1
	}
}
var daterangepicker_option_left = function (dom,flag) {
	

	let strDate = moment().subtract(7, 'days').format('YYYY-MM-DD')+' 00:00:00';
	let endDate = moment().format('YYYY-MM-DD HH:mm:ss');
	if (dom) {
		let $dom = $(dom);
		if(flag){
			$dom.prop("readonly","readonly").css({'background':'#fcfcfc',cursor: 'pointer'}).val(init7Days())
		}else {
			$dom.prop("readonly","readonly").css({'background':'#fcfcfc',cursor: 'pointer'}).val(init7DaysH())
		}
		
		if($dom.data("starttime")){
			strDate = $dom.data("starttime");
		}
		if($dom.data("endtime")){
			endDate = $dom.data("endtime");
		}
		if($dom.data("starttime")&&$dom.data("endtime")){
			$dom.prop("readonly","readonly").css({'background':'#fcfcfc',cursor: 'pointer'}).val(strDate+" 至 "+endDate);
		}
	};
	var obj = {
			startDate: strDate,
			endDate: endDate,
			minDate: moment().subtract(6, 'year'),
			// maxDate: moment(),
			maxDate: moment().add(6, 'year'),
			timePicker: flag,
			timePickerIncrement: 1,
			timePicker12Hour: false,
			ranges: {
		//		'不限': [''],
				'今天': [moment(), moment()],
		//		'昨天': [moment().subtract(1, 'days'), moment().subtract(1, 'days')],
				'一周': [moment().subtract(7, 'days'), moment()],
				'一月': [moment().subtract(1, 'months'), moment()],
				'三月': [moment().subtract(3, 'months'), moment()],
			},
			opens: 'left',
			buttonClasses: ['btn btn-default'],
			applyClass: 'btn-small btn-primary',
			cancelClass: 'btn-small',
			separator : daterangepicker_separator,
			format : flag?'YYYY-MM-DD HH:mm:ss':'YYYY-MM-DD',
			locale: {
				format: flag?'YYYY-MM-DD HH:mm:ss':'YYYY-MM-DD',
				applyLabel: '确定',
				cancelLabel: '关闭',
				fromLabel: '开始时间',
				toLabel: '结束时间',
				customRangeLabel: '自定义',
				daysOfWeek: ['七', '一', '二', '三', '四', '五', '六'],
				monthNames: ['一月', '二月', '三月', '四月', '五月', '六月', '七月', '八月', '九月', '十月', '十一月', '十二月'],
				firstDay: 1
			}
		}
	/*var obj = {
		startDate: moment().subtract(7, 'days'),
		endDate: moment(),
		minDate: moment().subtract(6, 'year'),
		maxDate: moment(),
		timePicker: false,
		timePickerIncrement: 1,
		timePicker12Hour: false,
		ranges: {
	//		'不限': [''],
			'今天': [moment(), moment()],
	//		'昨天': [moment().subtract(1, 'days'), moment().subtract(1, 'days')],
			'一周': [moment().subtract(7, 'days'), moment()],
			'一月': [moment().subtract(1, 'months'), moment()],
			'三月': [moment().subtract(3, 'months'), moment()],
		},
		opens: 'left',
		buttonClasses: ['btn btn-default'],
		applyClass: 'btn-small btn-primary',
		cancelClass: 'btn-small',
		separator : daterangepicker_separator,
		locale: {
			format: 'YYYY-MM-DD',
			applyLabel: '确定',
			cancelLabel: '关闭',
			fromLabel: '开始时间',
			toLabel: '结束时间',
			customRangeLabel: '自定义',
			daysOfWeek: ['七', '一', '二', '三', '四', '五', '六'],
			monthNames: ['一月', '二月', '三月', '四月', '五月', '六月', '七月', '八月', '九月', '十月', '十一月', '十二月'],
			firstDay: 1
		}
	}*/
	return obj;
}

var daterangepicker_option_right = function (dom,flag) {
	

	let strDate = moment().subtract(7, 'days');
	let endDate = moment();
	if (dom) {
		let $dom = $(dom);
		if(flag){
			$dom.prop("readonly","readonly").css({'background':'#fcfcfc',cursor: 'pointer'}).val(init7Days())
		}else{
			$dom.prop("readonly","readonly").css({'background':'#fcfcfc',cursor: 'pointer'}).val(init7DaysH())
		}
		
		if($dom.data("starttime")){
			strDate = $dom.data("starttime");
		}
		if($dom.data("endtime")){
			endDate = $dom.data("endtime");
		}
		if($dom.data("starttime")&&$dom.data("endtime")){
			$dom.prop("readonly","readonly").css({'background':'#fcfcfc',cursor: 'pointer'}).val(strDate+" 至 "+endDate);
		}
	};
	var obj = {
		startDate: strDate,
		endDate: endDate,
		minDate: moment().subtract(6, 'year'),
		maxDate: moment().add(6, 'year'),
		timePicker: flag,
		timePickerIncrement: 1,
		timePicker12Hour: false,
		ranges: {
	//		'不限': [''],
			'今天': [moment(), moment()],
	//		'昨天': [moment().subtract(1, 'days'), moment().subtract(1, 'days')],
			'一周': [moment().subtract(7, 'days'), moment()],
			'一月': [moment().subtract(1, 'months'), moment()],
			'三月': [moment().subtract(3, 'months'), moment()],
		},
		opens: 'right',
		buttonClasses: ['btn btn-default'],
		applyClass: 'btn-small btn-primary',
		cancelClass: 'btn-small',
		separator : daterangepicker_separator,
		format : flag?'YYYY-MM-DD HH:mm:ss':'YYYY-MM-DD',
		locale: {
			format: flag?'YYYY-MM-DD HH:mm:ss':'YYYY-MM-DD',
			applyLabel: '确定',
			cancelLabel: '关闭',
			fromLabel: '开始时间',
			toLabel: '结束时间',
			customRangeLabel: '自定义',
			daysOfWeek: ['七', '一', '二', '三', '四', '五', '六'],
			monthNames: ['一月', '二月', '三月', '四月', '五月', '六月', '七月', '八月', '九月', '十月', '十一月', '十二月'],
			firstDay: 1
		}
	}
	return obj;
}

var daterangepicker_minute = function (s_times,e_times) {
	$('#time_2').prop("readonly","readonly").css({'background':'#fcfcfc',cursor: 'pointer'});
	var obj = {
        startDate: s_times,
        endDate: moment(),
		minDate: s_times,
        maxDate: e_times,
		timePickerIncrement: 1,
		"timePicker": true,
	    "timePicker12Hour": false,
		timePickerSeconds:true,
		ranges: {
			'今天': [moment().format("YYYY-MM-DD 00:00:00"), moment().format("YYYY-MM-DD 23:59:59")],
			'一周': [moment().subtract(7, 'days'), moment()],
			'一月': [moment().subtract(1, 'months'), moment()],
			'三月': [moment().subtract(3, 'months'), moment()],
		},
		"alwaysShowCalendars": true,
		opens:'right',
		buttonClasses: ['btn btn-default'],
		applyClass: 'btn-small btn-primary',
		cancelClass: 'btn-small',	
		separator : daterangepicker_separator,
		"format": "YYYY-MM-DD HH:mm:ss",
		locale: {
			"format": "YYYY-MM-DD HH:mm:ss",
			applyLabel: '确定',
			cancelLabel: '关闭',
			fromLabel: '开始时间',
			toLabel: '结束时间',
			customRangeLabel: '自定义',
			daysOfWeek: ['七', '一', '二', '三', '四', '五', '六'],
			monthNames: ['一月', '二月', '三月', '四月', '五月', '六月', '七月', '八月', '九月', '十月', '十一月', '十二月'],
			firstDay: 1
		}
	}
	return obj;
}

var daterangepicker_minute_max = function (s_times) {
	$('#time_2').prop("readonly","readonly").css({'background':'#fcfcfc',cursor: 'pointer'})
	var obj = {
		startDate: moment().format("YYYY-MM-DD 00:00:00"),
		endDate: moment().format("YYYY-MM-DD 23:59:59"),
		minDate: s_times,
//      maxDate: moment(),
		timePickerIncrement: 1,
		"timePicker": true,
	    "timePicker12Hour": false,
		timePickerSeconds:true,
		ranges: {
			'今天': [moment().format("YYYY-MM-DD 00:00:00"), moment().format("YYYY-MM-DD 23:59:59")],
			'一周': [moment().subtract(7, 'days').format("YYYY-MM-DD 00:00:00"), moment().format("YYYY-MM-DD 23:59:59")],
			'一月': [moment().subtract(1, 'months').format("YYYY-MM-DD 00:00:00"), moment().format("YYYY-MM-DD 23:59:59")],
			'三月': [moment().subtract(3, 'months').format("YYYY-MM-DD 00:00:00"), moment().format("YYYY-MM-DD 23:59:59")],
		},
		"alwaysShowCalendars": true,
		opens:'right',
		buttonClasses: ['btn btn-default'],
		applyClass: 'btn-small btn-primary',
		cancelClass: 'btn-small',	
		separator : daterangepicker_separator,
		"format": "YYYY-MM-DD HH:mm:ss",
		locale: {
			"format": "YYYY-MM-DD HH:mm:ss",
			applyLabel: '确定',
			cancelLabel: '关闭',
			fromLabel: '开始时间',
			toLabel: '结束时间',
			customRangeLabel: '自定义',
			daysOfWeek: ['七', '一', '二', '三', '四', '五', '六'],
			monthNames: ['一月', '二月', '三月', '四月', '五月', '六月', '七月', '八月', '九月', '十月', '十一月', '十二月'],
			firstDay: 1
		}
	}
	return obj;
}


var daterangepicker_minute_minDate = function () {
	$('#time_2').prop("readonly","readonly").css({'background':'#fcfcfc',cursor: 'pointer'})
	var obj = {
		startDate: moment(),
		endDate: moment(),
		minDate: moment(),
		timePickerIncrement: 1,
		 "timePicker": true,
	    "timePicker12Hour": false,
		timePickerSeconds:true,
		ranges: {
			'今天': [moment().format("YYYY-MM-DD 00:00:00"), moment().format("YYYY-MM-DD 23:59:59")],
			'一周': [moment(),moment().subtract(-7, 'days')],
			'一月': [moment(), moment().subtract(-1, 'months')],
			'三月': [moment(),moment().subtract(-3, 'months')],
		},
		"alwaysShowCalendars": true,
		opens:'right',
		buttonClasses: ['btn btn-default'],
		applyClass: 'btn-small btn-primary',
		cancelClass: 'btn-small',	
		separator : daterangepicker_separator,
		"format": "YYYY-MM-DD HH:mm:ss",
		locale: {
			"format": "YYYY-MM-DD HH:mm:ss",
			applyLabel: '确定',
			cancelLabel: '关闭',
			fromLabel: '开始时间',
			toLabel: '结束时间',
			customRangeLabel: '自定义',
			daysOfWeek: ['七', '一', '二', '三', '四', '五', '六'],
			monthNames: ['一月', '二月', '三月', '四月', '五月', '六月', '七月', '八月', '九月', '十月', '十一月', '十二月'],
			firstDay: 1
		}
	}
	return obj;
}

var daterangepicker_minute_left = {
	startDate: moment().subtract(1, 'days'),
	endDate: moment(),
	minDate: moment().subtract(6, 'year'),
	timePickerIncrement: 1,
	 "timePicker": true,
    "timePicker12Hour": false,
	timePickerSeconds:true,
	ranges: {
//		'不限': [''],
		'今天': [moment().format("YYYY-MM-DD 00:00:00"), moment().format("YYYY-MM-DD 23:59:59")],
//		'昨天': [moment().subtract(1, 'days'), moment().subtract(1, 'days')],
		'一周': [moment().subtract(7, 'days'), moment()],
		'一月': [moment().subtract(1, 'months'), moment()],
		'三月': [moment().subtract(3, 'months'), moment()],
	},
	"alwaysShowCalendars": true,
	opens:'left',
	buttonClasses: ['btn btn-default'],
	applyClass: 'btn-small btn-primary',
	cancelClass: 'btn-small',	
	separator : daterangepicker_separator,
	"format": "YYYY-MM-DD HH:mm:ss",
	locale: {
		"format": "YYYY-MM-DD HH:mm:ss",
		applyLabel: '确定',
		cancelLabel: '关闭',
		fromLabel: '开始时间',
		toLabel: '结束时间',
		customRangeLabel: '自定义',
		daysOfWeek: ['七', '一', '二', '三', '四', '五', '六'],
		monthNames: ['一月', '二月', '三月', '四月', '五月', '六月', '七月', '八月', '九月', '十月', '十一月', '十二月'],
		firstDay: 1
	}
}

function init30Days(){
	var start_ = moment().subtract(1, 'months');
	var end_ = moment();
	return start_.format('YYYY-MM-DD HH:mm:ss')+daterangepicker_separator+end_.format('YYYY-MM-DD HH:mm:ss')
}

function init7Days(){
	var start_ = moment().subtract(7, 'days');
	var end_ = moment();
	return start_.format('YYYY-MM-DD HH:mm:ss')+daterangepicker_separator+end_.format('YYYY-MM-DD HH:mm:ss')
}

function init7DaysH(){
	var start_ = moment().subtract(7, 'days');
	var end_ = moment();
	return start_.format('YYYY-MM-DD')+daterangepicker_separator+end_.format('YYYY-MM-DD')
}


var daterangepicker_callback = function(start, end, label) {
					if (label == '不限') {
						$('#adopt_time_adopt_8 span').html('');
					} else {
						if (label == '自定义' && stime != etime) {
							$(".one_day_search").hide();
						} else {
							$(".one_day_search").show();
						}
						$('#adopt_time_adopt_8 span').html(start.format('YYYY-MM-DD') + '至' + end.format('YYYY-MM-DD'));
					}
				}