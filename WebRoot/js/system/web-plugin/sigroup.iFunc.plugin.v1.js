var iFunc = {
	reg : /^\s*$/,		//用于判断是否是一个或者多个空格
	split_1 : "#@#",
	split_2 : "~@~",
	//字符串为空判断
	isEmpty : function(str){
		if(str != null && str != undefined && this.reg.test(str) == false){
			return false;
		}
		return true;
	},
	//邮箱监测
	CheckEmail : function (email){
		var reyx= /^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+(.[a-zA-Z0-9_-])+/;
		return(reyx.test(email));
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
	  
	  //iPage按钮
	  exportBtn : '<button class="ipage-fenye-on exoprt_btn">导出所选</button>',
	  exportAllBtn : '<button class="ipage-fenye-on exoprt_all_btn">导出全部</button>',
	  infoCompareBtn : '<button class="ipage-fenye-on info_compare_btn">信息对比</button>',
	  infoAnalysisBtn : '<button class="ipage-fenye-on info_analysis_btn">信息分析</button>',
	  
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
		},
	  IsURL:function (str_url){
		  /*
		  if (str_url.match(/(((^https?:(?:\/\/)?)(?:[-;:&=\+\$,\w]+@)?[A-Za-z0-9.-]+|(?:www.|[-;:&=\+\$,\w]+@)[A-Za-z0-9.-]+)((?:\/[\+~%\/.\w-_]*)?\??(?:[-\+=&;%@.\w_]*)#?(?:[\w]*))?)$/g)){
		  return (true);
		  }else{
		  return (false);
		  }*/
		  var regEx = "((http|ftp|https)://)(([a-zA-Z0-9\._-]+\.[a-zA-Z]{2,6})|([0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}))(:[0-9]{1,4})*(/[a-zA-Z0-9\&%_\./-~-]*)?";
		       var re=new RegExp(regEx); 
		        if (re.test(str_url)){
		            return (true); 
		        }else{ 
		            return (false); 
		        }
		  },
		//导航按钮初始化
		initCommenttaskNavBtn:function($bottom) {
				var num = 5;
				$menus = $bottom.find("ul").find(".menu");
		        var first = -1;
		        for(var i=0 ; i<$menus.length; i++){
		            if($($menus[i]).attr("class").indexOf("active")>=0){
		                first = i;
		                break;
		            }
		        }
		        var last = -1;
		        for(var i=$menus.length-1 ; i>=0; i--){
		            if($($menus[i]).attr("class").indexOf("active")>=0){
		                last = i;
		                break;
		            }
		        }
		        if(first<0 && last<0){
		            $bottom.find("ul").find(".menu:lt("+num+")").addClass("active");
		        }
				
		        $bottom.find("ul").find(".menu").hide(500);
		        $bottom.find("ul").find(".active").show(500);

		        //right button
		        $bottom.find("ul").find(".menu-right").unbind("click").click(function(){
		        
		        $menus = $bottom.find("ul").find(".menu");
		        var first = -1;
		        for(var i=0 ; i<$menus.length; i++){
		            if($($menus[i]).attr("class").indexOf("active")>=0){
		                first = i;
		                break;
		            }
		        }
		        if(first>-1 && (first+=num)<$menus.length){
//		            $bottom.find("ul").find(".active").hide(500);
//		            setTimeout($bottom.find("ul").find(".active").removeClass("active"),500);
		            $bottom.find("ul").find(".active").removeClass("active");
		            for(var i=0 ; i<num ; i++){
		                $bottom.find("ul").find(".menu:eq("+first+")").addClass("active");
		                first+=1;
		            }
		            
			        $bottom.find("ul").find(".menu").hide(500);
			        $bottom.find("ul").find(".active").show(500);
		        }
		    });


		    $bottom.find("ul").find(".menu-left").unbind("click").click(function(){
		    
		        $menus = $bottom.find("ul").find(".menu");
		        
		        var last = -1;
		        for(var i=0 ; i<$menus.length; i++){
		            if($($menus[i]).attr("class").indexOf("active")>=0){
		                last = i;
		                break;
		            }
		        }
		        if(last>-1 && (last-=num)>=0){
//		            $bottom.find("ul").find(".active").hide(500);
//		            setTimeout($bottom.find("ul").find(".active").removeClass("active"),500);
		            $bottom.find("ul").find(".active").removeClass("active");
		            $bottom.find("ul").find(".active").removeClass("active");
		            for(var i=0 ; i<num ; i++){
		                $bottom.find("ul").find(".menu:eq("+last+")").addClass("active");
		                last+=1;
		            }
		            
		        $bottom.find("ul").find(".menu").hide(500);
		        $bottom.find("ul").find(".active").show(500);
		        }
		    });
		    
			}

	  
};

