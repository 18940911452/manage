var iFunc = {
	// 正在加载
	loading_html : "<br><br><br><center><img style='margin:0 auto;width:32px;' src='img/loading_01.gif' /><br></center><br><br><br>",

	loading_html_line : "<center><img style='margin:0 auto;width:32px;' src='img/loading_01.gif' /><br></center>",	
	reg : /^\s*$/,		//用于判断是否是一个或者多个空格
	split_1 : "#@#",
	split_2 : "~@~",
	searchVo:function(){
		var vo=new Object();
		vo.condition="";
		vo.conditionText="";
		vo.startTime="";
		vo.startTimeText="";
		vo.endTime="";
		vo.endTimeText="";
		vo.infoType="";
		vo.infoTypeText="";
		vo.emotion="";
		vo.emotionText="";
		vo.siteCategory="";
		vo.siteCategoryText="";
		vo.siteAttr="";
		vo.siteAttrText="";
		vo.focusSite="";
		vo.focusSiteText="";
		vo.author="";
		vo.authorText="";
		vo.site="";
		vo.siteText="";
		vo.order="";
		vo.orderText="";
		vo.datanum="";
		vo.datanumText="";
		return vo;
	},
	infoType:"news;blog;threads;Twitter;googleplus;facebook",
	emotion:"0 1 2 3",
	//字符串为空判断
	isEmpty : function(str){
		if(str != null && str != undefined && this.reg.test(str) == false){
			return false;
		}
		return true;
	},
	//判断字符串是否符合yyyy-MM-dd HH:mm:ss
	isTime:function(str){
		var reyx = /^\d{4}-(0\d|1[0-2])-([0-2]\d|3[01])( ([01]\d|2[0-3])\:[0-5]\d\:[0-5]\d)$/;
		return(reyx.test(str));
	},
	//邮箱监测
	CheckEmail : function (email){
		var reyx= /^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+(.[a-zA-Z0-9_-])+/;
		return(reyx.test(email));
	},
	isChinaPhone:function(phoneC){
		var reyx=/(\+86|0086)?\s*1[34578]\d{9}/;
		return(reyx.test(phoneC));
	},
	/*isInternalPhone:function(phoneI){//
		var reyx=/^((00){1}[1-9]{1}[0-9]{1,3}|86|\+86)?1[3458]\d{9}$/;
		return(reyx.test(phoneI));
	},*/
	//URL监测
	CheckURL : function (url){
		/*var strRegex = '^((https|http|ftp|rtsp|mms)?://)'
			+ '?(([0-9a-z_!~*\'().&=+$%-]+: )?[0-9a-z_!~*\'().&=+$%-]+@)?' //ftp的user@ 
			+ '(([0-9]{1,3}.){3}[0-9]{1,3}' // IP形式的URL- 199.194.52.184 
			+ '|' // 允许IP和DOMAIN（域名） 
			+ '([0-9a-z_!~*\'()-]+.)*' // 域名- www. 
			+ '([0-9a-z][0-9a-z-]{0,61})?[0-9a-z].' // 二级域名 
			+ '[a-z]{2,6})' // first level domain- .com or .museum 
			+ '(:[0-9]{1,4})?' // 端口- :80 
			+ '((/?)|' // a slash isn't required if there is no file name 
			+ '(/[0-9a-z_!~*\'().;?:@&=+$,%#-]+)+/?)$'; 
			var re=new RegExp(strRegex); 
		return re.test(url);*/
		var re = new RegExp(/^[hH][tT][tT][pP]([sS]?):\/\/(\S+\.)+\S{2,}$/);
		 if (re.test(url)){
	         return (true); 
	     }else{ 
	         return (false); 
	     }
	},
	//数字缩写
	sum : function(t){
		if(t!=null && t!=undefined && t!=""){
			var t = parseInt(t);
			if(t > 1000000){
				t = (t/1000000).toFixed(1)+"M";
			} else if(t > 1000){
				t = (t/1000).toFixed(1)+"K";
			}
		}
		return t;
	},
	//URL解析
	UrlParse : function(){
		var search = window.location.search;
		search=decodeURIComponent(search);
		var paramObj = {};
		if (search != null && !this.isEmpty(search) && search.indexOf("?") == 0){
	  		search = search.substring(1);
	  		var params = search.split("&");
	  		//console.log("size:"+params.length + "; params:" + params);
	  		for(var i=0; i<params.length; i++){
	  			var n = params[i];
	  			if(n != null && !this.isEmpty(n)){
		  			var param = n.split("=");
		  			if(param != null && param.length > 1){
			  			var key = param[0];
			  			var val = param[1];
			  			//console.log(key+":"+val);
			  			if(key != null && !this.isEmpty(key)){
			  				paramObj[key] = val;
				  			//console.log(paramObj);
			  			}
		  			}
	  			}
	  		}
	  	}
	  	return paramObj;
	  },
	  
	  //cookie操作
	  getValue:function(k){var v=window.sessionStorage.getItem(k);return v;},
	  saveValue:function(k,v){if(k==null){return null;}var o=window.sessionStorage.setItem(k,v);return o;},
	  removeHashKey:function(k){window.sessionStorage.removeItem(k);},
	  getValueWithinROOT:function(k){var v=$.cookie(k);return v;},
	  saveValueWithinROOT:function(k,v){if(k==null){return null;}var o=$.cookie(k,v,{expires:30,path:"/"});return o;},
	  removeHashKeyWithinROOT:function(key){$.cookie(key, null,{path:"/"});},
	  
	  //获取用户权限
	  getUserFrame:function(username,func){
		  if(this.isEmpty(username)){
			  return null;
		  }
		  var frame;
		  var un = this.getValue("username");
		  if(!this.isEmpty(un) && un==username){
			  frame = this.getValue("sysFrame");
		  }
		  if(this.isEmpty(frame)){
			  $.post("sys_find_user_frame.action", function(vframe){
				  console.log("Frame From Post.");
				  iFunc.saveValue("sysFrame",vframe);
				  iFunc.saveValue("username",username);
				  if(func){
					  func(vframe);
				  }
			  });
		  } else {
			  console.log("Frame From Storage.");
			  if(func){
				  func(frame);
			  }
		  }
	  },
	   //HighCharts option
	 // typeList : ['新闻','论坛','博客','微博','视频','QQ','YY'],
	  typeList : ['Twitter','Facebook','Instagram','新闻'],
	  typeListEng : ['news','threads','blog','Twitter','facebook','googleplus','YY'],
	  stagelList : ['一','二','三','四','五','六','七','八','九','十', "十一", "十二", "十三", "十四", "十五", "十六", "十七", "十八", "十九", "二十"],
	  labelList : ['发生','发展','高峰','回落','消亡'],
	  colorList : ['#e4d354','#f7a35c','#f15c80','#90ed7d','#8085e9'],
	  opacityValue : 0.4,
	  dateTimeLabelFormats:{
			millisecond: '%H:%M:%S.%L',
			second: '%H:%M:%S',
			minute: '%H:%M',
			hour: '%H:%M',
			day: '%Y-%m-%d',
			week: '%Y-%m-%d',
			month: '%Y-%m',
			year: '%Y'
	  },
	  timezoneOffset: 28800000,		//-8*60*60*1000
	  cutLen : 10,						//截取字符长度
	  legendCutLen : 40,
	  //站点类型
	  siteScope:['全部','境外主流媒体','敌对媒体','Twitter'],
	  //iPage按钮
	  exportBtn : '<button class="ipage-fenye-on exoprt_btn">导出所选</button>',
	  exportAllBtn : '<button class="ipage-fenye-on exoprt_all_btn">导出全部</button>',
	  infoCompareBtn : '<button class="ipage-fenye-on info_compare_btn">信息对比</button>',
	  infoAnalysisBtn : '<button class="ipage-fenye-on info_analysis_btn">信息分析</button>',
	  //境外代理
	  abroadAgentPrefix : "https://isionlineproxy.com/cgi-bin/nph-proxy.cgi/en/E0/",
	  abroadAgent : function(){
		  $(document).off("hover", "[title=查看源网页]").on("hover", "[title=查看源网页]", function(){
			  	var scate = $(this).data("scate");
			  	if(scate && (scate == 0 || scate == 2)){	//0-未知，1-境内，2-境外
			  		var url = $(this).attr("href");
			  		if(url){
			  			if(url.indexOf(iFunc.abroadAgentPrefix) == -1){
			  				url = url.replace("://", "/");
			  				url = iFunc.abroadAgentPrefix+url;
			  				$(this).attr("href",url);
			  			}
			  		}
			  	}
		  });
	  },
	  //用户行为跟踪
	  preFix : "进入 ",
	  recordOper : function (operation){
			//$.post("json_manageConfig_logoperate.action",{data:operation},function(){});
			$.get("sys_save_user_oper.action?data="+encodeURIComponent(encodeURIComponent(iFunc.preFix+operation)));
	  },
	  
	  
	  //信息列表页已阅功能
	  changeReadFlag	:	function(container, parentClass, tflag){	//tflag=0则时间格式为yyyy-MM-dd 否则就是yyyy-MM-dd HH:mm:ss
		  parentClass = parentClass === undefined ? ".informationlist" : parentClass 
		  $("[data-status=1]",$(container)).css("color","#88888");
			$("font[data-status=1].readFlag",$(container)).css("color","rgb(247, 156, 156)").show();
			$(".changecolor",$(container)).click(function(){
				var $tr = $(this).closest(parentClass);
				$tr.find("a:not(.yiyueClass),b,span,p").css("color","#888888");
				var $readFlog = $tr.find(".readFlag");
				$readFlog.show();
				var status = $readFlog.data("status");
				var mydate = new Date();
				var readtime;
				if (tflag == 0) {
					readtime = mydate.getFullYear()+"-"+(mydate.getMonth()+1)+"-"+mydate.getDate(); 
				} else {
					readtime = mydate.getFullYear()+"-"+(mydate.getMonth()+1)+"-"+mydate.getDate()+" "+mydate.getHours()+":"+mydate.getMinutes()+":"+mydate.getSeconds();
				}
				
				console.info("readtime=="+readtime);
				if(status != "1"){
					$readFlog.data("status", 1).find("a").attr("title", "阅读者:"+username);
					$readFlog.data("status", 1).find("a").html("已阅 "+username+" "+readtime);
				}
				//处置按钮
				$(".dispose_info div .info_btn a").css({"color":"#fff"});
			});
	  }, 
	  
	  saveEchartOption : function (container, option) {
		  $("#" + container).find("[name=" + container + "_code]").remove();
		  $("#" + container).append("<textarea name='"+ container +"_code' style='display:none;'></textarea>");
		  $("#" + container).find("[name=" + container + "_code]").val(JSON.stringify(option));
	  },
	  
	  genUserRole : function(role, sysGrade){
		  var str = "";
		  switch(sysGrade){
		  case "1":
			  str += "系统";
			  break;
		  case "2":
			  str += "单位";
			  break;
		  case "3":
			  str += "部门";
			  break;
		  }
		  switch(role){
		  case "1":
			  str += "管理员";
			  break;
		  case "2":
			  str += "审计员";
			  break;
		  case "3":
			  str += "业务用户";
			  break;
		  }
		  return str;
	  },
	  checkKeywords : function(keyword) {
		  var kws = $.trim(keyword);
		  if (kws != '') {
			  kws = kws.replace("；",";");
			  if (kws == ';') {
				  return false;
			  }
			  var re1 = new RegExp("^([\u4E00-\uFA29]|[\uE7C7-\uE7F3]|[a-zA-Z0-9_-]|\\s)*$"); //汉字，字母，空格
			  var kwsArray = kws.split(";");			  
			  for (var i = 0; i < kwsArray.length; i ++) {
				  if (kwsArray[i] == '' || !re1.test(kwsArray[i])) {
					  return false;
				  }
			  }
			  return true;
		  } else {
			  return false;
		  }
	  },
	  pageColumn:function(options){
          var defaults = {
				colBoxId :"",	//分列的元素id
				//colEle : 'div',			//分列的元素
				colNum : '3'			//分成多少列
			}
			var _oSelf$ = this;
			_oSelf$.config = $.extend({
				container$: _oSelf$, 
			}, defaults, options);
			var colWidth = 0;
			var $containerwidth = 0;
			var $containerpleft = 0;
			var $containerpright = 0;
			if(_oSelf$.config.colBoxId!=""){
				$containerwidth = $("#"+_oSelf$.config.colBoxId).width();
				$containerpleft = parseInt($("#"+_oSelf$.config.colBoxId).css("padding-left"))
				$containerpright = parseInt($("#"+_oSelf$.config.colBoxId).css("padding-right"))
			}
			colWidth = parseInt((($containerwidth+$containerpleft)/_oSelf$.config.colNum))
			console.log(colWidth+"1")
			return colWidth;/**/
			/*$(window).resize(function(){
				if(_oSelf$.config.colBoxId!=""){
					$containerwidth = $("#"+_oSelf$.config.colBoxId).width();
					$containerpleft = parseInt($("#"+_oSelf$.config.colBoxId).css("padding-left"))
					$containerpright = parseInt($("#"+_oSelf$.config.colBoxId).css("padding-right"))
				}
				colWidth = parseInt((($containerwidth+$containerpleft)/_oSelf$.config.colNum))
				console.log(colWidth+"2")
				return colWidth;
			})*/
		},
		showLoading:function(obj){
			$(obj).html(iPage.loading_html);
		},
		hideLoading:function(obj){
			$(obj).html("");
		},
		//obj是所有checkbox的父亲对象
		//current为当前点击的checkbox
		//name 为所有checkbox的name属性
		clickall:function(obj,current,name){
			if(!$(current).is(':checked')){
				$.each($(obj).find("input[name='"+name+"']"),function(i,item){
					$(item).prop("checked","");
				});
			}else{
				$.each($(obj).find("input[name='"+name+"']"),function(i,item){
					$(item).prop("checked","checked");
				});
			}
		},
		//时间格式验证 YYYY-MM-dd HH:mm:ss
		//时间格式有效返回true 无效返回false
		validateDateTime:function(testdate){
			var date_regex = /((19|20)[0-9]{2})-(0?[1-9]|1[012])-(0?[1-9]|[12][0-9]|3[01]) ([01]?[0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]/;
		    var res = date_regex.test(testdate);
		    if(res) {
		    	var ymd = testdate.match(/(\d{4})-(\d+)-(\d+).*/);
		    	var year = parseInt(ymd[1]);
		    	var month = parseInt(ymd[2]);
		    	var day = parseInt(ymd[3]);
				if(day > 28) {
					//获取当月的最后一天
					var lastDay = new Date(year, month, 0).getDate();
					return (lastDay >= day);
				}
				return true;
		    }
		    return false;
		},
		
		//计算字符串长度(英文占1个字符，中文汉字占2个字符)		
		strlen: function(str){
		    var len = 0;
		    for (var i=0; i<str.length; i++) { 
		     var c = str.charCodeAt(i); 
		    //单字节加1 
		     if ((c >= 0x0001 && c <= 0x007e) || (0xff60<=c && c<=0xff9f)) { 
		       len++; 
		     } 
		     else { 
		      len+=2; 
		     } 
		    } 
		    return len;
		},
		//用正则表达式 过滤特殊字符
		stripscript : function(s){ 
//			var pattern = new RegExp("[`~!@#$^&*()=|{}':;',\\[\\].<>/?~！@#￥……&*（）——|{}【】‘；：”“'。，、？，、～＄％＠＆＃＊?；︰…‥﹐˙?‘’“”〝〞‵′〃↑↓←→↖↗ΓΔΘ∧Ξэюㄅㄉㄓㄚㄞㄢㄦㄆㄊㄍㄐㄔㄗㄧㄛㄟㄣㄇㄋㄎㄑㄕㄘㄨㄜㄠㄤㄈㄏㄒㄖㄙㄩㄝㄡㄥāáǎà、ōóǒò、êēéěè、īíǐì、ūúǔù、ǖǘǚǜüぁぃぅぇぉかきくけこんさしすせそたちつってとゐなにぬねのはひふへほゑまみむめもゃゅょゎをァィゥヴェォカヵキクケヶコサシスセソタチツッテトヰンナニヌネノハヒフヘホヱマミムメモャュョヮヲˉˇ¨‘’々～‖∶”’‘|〃〔〕《》「」『』〖〗【【】()〔〕{｝ⅠⅡⅢⅣⅤⅥⅦⅧⅨⅩⅪⅫ①②③④⑤⑥⑦⑧⑨⑩≈≡≠=≤≥<>≮≯∷±+-×÷∫∮∝∞∧∨∑∏∪∩∈∵∴⊥‖∠⌒⊙≌∽√°′〃$￡￥‰%℃¤￠┌┍┎┏┐┑┒┓—┄┈├┝┞┟┠┡┢┣|┆┊┬┭┮┯┰┱┲┳┼┽┾┿╀╂╁╃§№☆★○●◎◇◆□■△▲※→←↑↓〓#&@\^_▁▂▃▄▅▆▇█▉▊▋▌▍▎▏▓▔▕◢◣◤◥☉♀♂⊙●○①⊕◎Θ⊙¤㊣▂ ▃ ▄ ▅ ▆ ▇ █ █ ■ ▓ 回 □ 〓≡ ╝╚╔ ╗╬ ═ ╓ ╩ ┠ ┨┯ ┷┏ ┓┗ ]");
			var pattern = new RegExp("[`~!@#$^&*()=|{}':;',\\[\\].<>/?~！@#￥……&*（）——|{}【】‘；：”“'。，、？]")
			var rs = ""; 
			for (var i = 0; i < s.length; i++) { 
			rs = rs+s.substr(i, 1).replace(pattern, ''); 
			} 
			return rs; 
		},
		//用正则表达式 替换特殊字符为空格
		stripscript_blank : function(s){ 
//			var pattern = new RegExp("[`~!@#$^&*()=|{}':;',\\[\\].<>/?~！@#￥……&*（）——|{}【】‘；：”“'。，、？，、～＄％＠＆＃＊?；︰…‥﹐˙?‘’“”〝〞‵′〃↑↓←→↖↗ΓΔΘ∧Ξэюㄅㄉㄓㄚㄞㄢㄦㄆㄊㄍㄐㄔㄗㄧㄛㄟㄣㄇㄋㄎㄑㄕㄘㄨㄜㄠㄤㄈㄏㄒㄖㄙㄩㄝㄡㄥāáǎà、ōóǒò、êēéěè、īíǐì、ūúǔù、ǖǘǚǜüぁぃぅぇぉかきくけこんさしすせそたちつってとゐなにぬねのはひふへほゑまみむめもゃゅょゎをァィゥヴェォカヵキクケヶコサシスセソタチツッテトヰンナニヌネノハヒフヘホヱマミムメモャュョヮヲˉˇ¨‘’々～‖∶”’‘|〃〔〕《》「」『』〖〗【【】()〔〕{｝ⅠⅡⅢⅣⅤⅥⅦⅧⅨⅩⅪⅫ①②③④⑤⑥⑦⑧⑨⑩≈≡≠=≤≥<>≮≯∷±+-×÷∫∮∝∞∧∨∑∏∪∩∈∵∴⊥‖∠⌒⊙≌∽√°′〃$￡￥‰%℃¤￠┌┍┎┏┐┑┒┓—┄┈├┝┞┟┠┡┢┣|┆┊┬┭┮┯┰┱┲┳┼┽┾┿╀╂╁╃§№☆★○●◎◇◆□■△▲※→←↑↓〓#&@\^_▁▂▃▄▅▆▇█▉▊▋▌▍▎▏▓▔▕◢◣◤◥☉♀♂⊙●○①⊕◎Θ⊙¤㊣▂ ▃ ▄ ▅ ▆ ▇ █ █ ■ ▓ 回 □ 〓≡ ╝╚╔ ╗╬ ═ ╓ ╩ ┠ ┨┯ ┷┏ ┓┗ ]");
			var pattern = new RegExp("[`~!@#$^&*()=|{}':;',\\[\\].<>/?~！@#￥……&*（）——|{}【】‘；：”“'。，、？]")
			var rs = ""; 
			for (var i = 0; i < s.length; i++) { 
			rs = rs+s.substr(i, 1).replace(pattern, ' '); 
			} 
			return rs; 
		},
		curDateTime:function(){
		     var d = new Date(); 
		     var year = d.getFullYear()
		     var month = d.getMonth()+1; 
		     var date = d.getDate(); 
		     var day = d.getDay(); 
		     var hours = d.getHours(); 
		     var minutes = d.getMinutes(); 
		     var seconds = d.getSeconds(); 
		     var ms = d.getMilliseconds(); 
		     var curDateTime= year;
		     if(month>9)
		     curDateTime = curDateTime +"-"+month;
		     else
		     curDateTime = curDateTime +"-0"+month;
		     if(date>9)
		     curDateTime = curDateTime +"-"+date;
		     else
		     curDateTime = curDateTime +"-0"+date;
		     if(hours>9)
		     curDateTime = curDateTime +" "+hours;
		     else
		     curDateTime = curDateTime +" 0"+hours;
		     if(minutes>9)
		     curDateTime = curDateTime +":"+minutes;
		     else
		     curDateTime = curDateTime +":0"+minutes;
		     if(seconds>9)
		     curDateTime = curDateTime +":"+seconds;
		     else
		     curDateTime = curDateTime +":0"+seconds;
		 return curDateTime; 
		 },
		/*
		  * 获取几天前的时间
		  */
		 getBeforDate:function(day){
			  var date=(iFunc.curDateTime()).split(" ");
		       var datearr=date[0].split("-");
		       var timearr=date[1].split(":");
		       var newDate = new Date();
		       newDate.setFullYear(datearr[0], datearr[1], datearr[2]);
		       newDate.setHours(timearr[0], timearr[1], timearr[2], 0);
		       var befminuts = newDate.getTime() - 1000 * 60 * 60 * 24 * parseInt(day);
		       var beforeDat = new Date;
		           beforeDat.setTime(befminuts);
		       var befMonth = beforeDat.getMonth();
		       var mon = befMonth >= 10 ? befMonth : '0' + befMonth;
		       var befDate = beforeDat.getDate();
		       var da = befDate >= 10 ? befDate : '0' + befDate;
		       var newDate = beforeDat.getFullYear() + '-' + mon + '-' + da ;
		       if(beforeDat.getHours()>9)
		    	   newDate = newDate +" "+beforeDat.getHours();
			   else
				   newDate = newDate +" 0"+beforeDat.getHours();
			   if(beforeDat.getMinutes()>9)
				   newDate = newDate +":"+beforeDat.getMinutes();
			   else
				   newDate = newDate +":0"+beforeDat.getMinutes();
			   if(beforeDat.getSeconds()>9)
				   newDate = newDate +":"+beforeDat.getSeconds();
			   else
				   newDate = newDate +":0"+beforeDat.getSeconds();
			   
			   return newDate;
		 },
		 /**
		 * checkbox全选按钮初始化,默认全选按钮为第一个复选框
		 * @param container 装载复选框的jquery对象
		 * @param name 复选框的name属性值
		 */
		checkbox_all_btn:function(container,name,property,type){
			//判断是否为特定属性进行筛选
			if(null==property || $.trim(property)==""){
				//初始化全选按钮
				$(container).find("input[name='"+name+"']:eq(0)").unbind("click").click(function(){
					var isCheck = $(container).find("input[name='"+name+"']:eq(0)").is(':checked');
					if(isCheck){
						var checkboxs = $(container).find("input[name='"+name+"']:gt(0)");//获取除第一个的所有checkbox元素
						if(checkboxs.length>0){
							for(var i=0 ; i<checkboxs.length ; i++){
								$(checkboxs[i]).prop("checked","checked");						
							}
						}
					}else{
						var checkboxs = $(container).find("input[name='"+name+"']:gt(0)");//获取除第一个的所有checkbox元素
						if(checkboxs.length>0){
							for(var i=0 ; i<checkboxs.length ; i++){
								$(checkboxs[i]).prop("checked","");						
							}
						}				
					}
				});
				//初始化非全选按钮，效果是：如果其他按钮全选则全选按钮为勾选状态，如果有任意非全选按钮未选中，全选按钮为非选中状态
				$(container).find("input[name='"+name+"']:gt(0)").unbind("click").click(function(){
					var isCheck = true;
					var checkboxs = $(container).find("input[name='"+name+"']:gt(0)");//获取除第一个的所有checkbox元素
					if(checkboxs.length>0){
						for(var i=0 ; i<checkboxs.length ; i++){
							if(!$(checkboxs[i]).is(':checked')){
								isCheck = false;
							}
						}
					}
					if(isCheck){
						$(container).find("input[name='"+name+"']:eq(0)").prop("checked","checked");
					}else{
						$(container).find("input[name='"+name+"']:eq(0)").prop("checked","");
					}
				});
				//初始化，按钮全部为选中状态
				//$(container).find("input[name='"+name+"']").prop("checked","checked");
				//判断初始化时是否全选
			   var isallchecked = true;
			   $(container).find("input[name='"+name+"']:gt(0)").each(function(){
				   if(!$(this).is(":checked")){
					   isallchecked = false;
				   }
			   });
			   if(isallchecked){
				   $(container).find("input[name='"+name+"']:eq(0)").prop("checked","checked"); 
			   }else{
				   $(container).find("input[name='"+name+"']:eq(0)").prop("checked","");
			   }
			}else{
				//初始化全选按钮
				$(container).find("input["+property+"='"+name+"']:eq(0)").unbind("click").click(function(){
					var isCheck = $(container).find("input["+property+"='"+name+"']:eq(0)").is(':checked');
					if(isCheck){
						var checkboxs = $(container).find("input["+property+"='"+name+"']:gt(0)");//获取除第一个的所有checkbox元素
						if(checkboxs.length>0){
							for(var i=0 ; i<checkboxs.length ; i++){
								$(checkboxs[i]).prop("checked","checked");						
							}
						}
					}else{
						var checkboxs = $(container).find("input["+property+"='"+name+"']:gt(0)");//获取除第一个的所有checkbox元素
						if(checkboxs.length>0){
							for(var i=0 ; i<checkboxs.length ; i++){
								$(checkboxs[i]).prop("checked","");						
							}
						}				
					};
				});
				//初始化非全选按钮，效果是：如果其他按钮全选则全选按钮为勾选状态，如果有任意非全选按钮未选中，全选按钮为非选中状态
				// $(container).find("input["+property+"='"+name+"']:gt(0)").unbind("click").click(function(){
				$(container).off('click',"input["+property+"='"+name+"']:gt(0)").on('click',"input["+property+"='"+name+"']:gt(0)",function(){
					var isCheck = true;
					var checkboxs = $(container).find("input["+property+"='"+name+"']:gt(0)");//获取除第一个的所有checkbox元素
					if(checkboxs.length>0){
						for(var i=0 ; i<checkboxs.length ; i++){
							if(!$(checkboxs[i]).is(':checked')){
								isCheck = false;
							}
						}
					}
					if(isCheck){
						$(container).find("input["+property+"='"+name+"']:eq(0)").prop("checked","checked");
					}else{
						$(container).find("input["+property+"='"+name+"']:eq(0)").prop("checked","");
					}
				});
				//初始化，按钮全部为选中状态
				if(type){
					$(container).find("input["+property+"='"+name+"']").prop("checked","checked");
					//判断初始化时是否全选
					var isallchecked = true;
					$(container).find("input["+property+"='"+name+"']:gt(0)").each(function(){
						if(!$(this).is(":checked")){
							isallchecked = false;
						}
					});
					if(isallchecked){
						$(container).find("input["+property+"='"+name+"']:eq(0)").prop("checked","checked"); 
					}else{
						$(container).find("input["+property+"='"+name+"']:eq(0)").prop("checked","");
					}
				}
			}
			
		}
};

